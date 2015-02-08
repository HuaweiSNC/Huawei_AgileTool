package com.huawei.agilete.northinterface.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.base.servlet.MyL2vpnVpwsInstance;
import com.huawei.agilete.base.servlet.MyMplsExplicitPath;
import com.huawei.agilete.base.servlet.MyMplsRsvpTeTunnel;
import com.huawei.agilete.base.servlet.MyTunnelPolicy;
import com.huawei.agilete.base.servlet.util.RestUtil;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.action.Activator;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTDomain;
import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.agilete.northinterface.bean.OTPath;
import com.huawei.agilete.northinterface.bean.OTPolicy;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.agilete.northinterface.bean.OTTunnelPath;


public class OTTfDAO implements IOTDao{

	private static OTTfDAO single = null;
	private OpsRestCaller client = null;
	private String domainId;

	private HashMap<String,RetRpc> deviceTTreeMap;

	public OTTfDAO(){

	}
	public synchronized  static OTTfDAO getInstance() {
		if (single == null) {  
			single = new OTTfDAO();
		}  
		return single;
	}

	public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
		RetRpc result = new RetRpc();
		this.domainId = domainId;
		if(restType.equals(MyData.RestType.GET)){
			if(2 == apiPath.split("_").length){
				deviceId = apiPath.split("_")[0];
				client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				result = edit(apiPath.split("_")[1]);
			}else{
				long time1= System.currentTimeMillis();
				if("refurbish".equals(apiPath)){
					result = get(domainId,1);
				}else{
					result = get(domainId,2);
				}
				long time2= System.currentTimeMillis()-time1;
				System.out.println("time= "+time2);
			}

		}else if(restType.equals(MyData.RestType.DELETE)){
			result = del(apiPath);
		}else if(restType.equals(MyData.RestType.POST)){
			result = add(content);
		}else if(restType.equals(MyData.RestType.PUT)){

		}else{

		}
		return result;
	}

	public RetRpc edit(String apiPath) {
		RetRpc result = new RetRpc();

		if(null == apiPath || "".equals(apiPath)){
			result.setStatusCode(500);
			result.setContent("tunnelName can not be null");
			return result;
		}
		if(null == MyData.getTunnelTree().get(domainId)){
			result = get(domainId,2);
		}else{
			OTTunnelDAO.getInstance().setClient(client);
			List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps(apiPath,false);
			if(null == tunnelList){
				result.setStatusCode(500);
				result.setContent("can not find tunnel:"+apiPath);
				return result;
			}
			OTTunnel oTTunnel = tunnelList.get(0);
			String tf = MyData.getTunnelTree().get(domainId);
			try {
				Document doc = DocumentHelper.parseText(tf);
				Element el = doc.getRootElement();
				for(Iterator<Element> i=el.elementIterator();i.hasNext();){
					Element domain = i.next();
					String tunnelName = domain.attributeValue("name").split("-")[0];
					if(apiPath.equals(tunnelName) && client.deviceId.equals(domain.attributeValue("deviceId"))){
						Attribute priState = domain.attribute("priState");
						priState.setText(oTTunnel.getStatePri());
						Attribute state = domain.attribute("state");
						state.setText(oTTunnel.getState());
						Attribute backState = domain.attribute("backState");
						backState.setText(oTTunnel.getStateBack());
						Attribute flow = domain.attribute("flow");
						flow.setText(oTTunnel.getWhereFlow());
						break;
					}
				}
				result.setContent(doc.asXML());
				MyData.getTunnelTree().put(domainId, result.getContent());
			}catch (DocumentException e) {
				//e.printStackTrace();
			}
		}
		return result;
	}
	public RetRpc del(String apiPath) {
		return null;
	}
	public RetRpc add(String content) {
		return null;
	}

	public RetRpc get(String domainId){
		RetRpc result = new RetRpc();
		result = get(domainId, 2);
		return result;
	}

	/**
	 * 
	 * @param domainId
	 * @param timer 0:定时刷新 1:强制刷新 2:使用缓存数据
	 * @return
	 */
	public RetRpc get(String domainId,int timer){
		RetRpc result = new RetRpc();
		deviceTTreeMap = new HashMap<String,RetRpc>();
		if(timer == 2 && (null == MyData.InitState.get(domainId) || !MyData.InitState.get(domainId))){
			result.setStatusCode(501);
			result.setContent("the system is initializing");
			return result;
		}

		if(domainId.equals("4") ){
			System.out.println();
		}
		if(timer == 2  && null != MyData.getTunnelTree().get(domainId)){
			result.setContent(MyData.getTunnelTree().get(domainId));
			return result;
		}

		StringBuffer xml = new StringBuffer();
		xml.append("<domains id='").append(domainId).append("'>");
		List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
		if(null == deviceList || deviceList.size() == 0){
			MyData.InitState .put(domainId, true);
			return result;
		}

		//DBAction db = new DBAction();
		for(int index=0;index<deviceList.size();index++){
			OTDevice oTDevice = deviceList.get(index);
			DeviceTTreeTask deviceTTreeTask = new DeviceTTreeTask();
			deviceTTreeTask.oTDevice = oTDevice;
			deviceTTreeTask.timer = timer;
			Thread t = new Thread(deviceTTreeTask);
			t.start();
		}
		Boolean flag = true;
		while(flag){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			if(deviceTTreeMap.size() == deviceList.size()){
				for(int i=0;i<deviceList.size();i++){
					RetRpc deviceTTree = deviceTTreeMap.get(deviceList.get(i).getId());
					if(deviceTTree != null && 200 == deviceTTree.getStatusCode()){
						xml.append(deviceTTree.getContent());
					}
				}
				break;
			}
		}

		xml.append("</domains>");
		result.setContent(xml.toString());
		MyData.getTunnelTree().put(domainId, result.getContent());
		DeviceTTreeDataTask deviceTTreeDataTask = new DeviceTTreeDataTask();
		deviceTTreeDataTask.domainId = domainId;
		Thread t = new Thread(deviceTTreeDataTask);
		t.start();
		return result;
	}

	class DeviceTTreeTask implements Runnable{
		public OTDevice oTDevice;
		private RetRpc result;
		public int timer = -1;

		@Override
		public void run() {
			result = new RetRpc();
			result.setContent("");
			OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTDevice.getId());
			//ping devices
			OTRestPingDAO.getInstance().setClient(client);
			RetRpc pingDevice =OTRestPingDAO.getInstance().get("");

			if(null == pingDevice || 200 != pingDevice.getStatusCode()){
				MyData.AllDevices.put(oTDevice.getId(), pingDevice.getStatusCode());
				result = pingDevice;
				deviceTTreeMap.put(oTDevice.getId(), result);
				//推送连不通消息
				if(null != Activator.autobahnServer){
					StringBuffer deviceDone = new StringBuffer();
					deviceDone.append("<massage type='devices'>");
					deviceDone.append("<devices>").append("<device>").append("<id>").append(oTDevice.getId()).append("</id>").append("<state>").append(result.getStatusCode()).append("</state>").append("</device>").append("</devices>");
					deviceDone.append("</massage>");
					Activator.autobahnServer.sendToAll(deviceDone.toString());
				}
				return;
			}else{
				MyData.AllDevices.put(oTDevice.getId(), pingDevice.getStatusCode());
			}

			//根据当前设备所有tunnel
			OTTunnelDAO.getInstance().setClient(client);
			List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps("",false);
			if(null == tunnelList){
				result.setContent("");
				deviceTTreeMap.put(oTDevice.getId(), result);
				return;
			}
			//获取当前设备所有pocily
			OTPolicyDAO.getInstance().setClient(client);
			List<OTPolicy> policyList = OTPolicyDAO.getInstance().getList("");

			//获取当前设备所有flow
			OTFlowDAO.getInstance().setClient(client);
			List<OTFlow> flowList = OTFlowDAO.getInstance().getList("");

			StringBuffer xml = new StringBuffer();
			//配对
			for(int i=0;i<tunnelList.size();i++){
				OTTunnel tunnel = tunnelList.get(i);
				xml.append("<tunnel ");
				xml.append("name= '").append(tunnel.getName()).append("-").append(oTDevice.getDeviceName()).append("' ");
				xml.append("ingressIp= '").append(tunnel.getIngressIp()).append("' ");
				xml.append("egressIp= '").append(tunnel.getEgressIp()).append("' ");
				xml.append("state= '").append(tunnel.getState()).append("' ");
				xml.append("priState= '").append(tunnel.getStatePri()).append("' ");
				xml.append("backState= '").append(tunnel.getStateBack()).append("' ");
				String rttTunnel = "NA";
				String flux = "NA";
				Boolean rttFlag = false;
				if(rttFlag && timer == 0 && MyData.InitState.get(domainId)){
					//from in time
					RetRpc retRpcTunnelrtt =  OTNqaNowDAO.getInstance().control(domainId, oTDevice.getId(), "tunnel_"+tunnel.getName(), MyData.RestType.GET, "");
					if(200 == retRpcTunnelrtt.getStatusCode()){
						String flag = retRpcTunnelrtt.getContent();
						try{
							rttTunnel = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
						}catch(Exception e){
							rttTunnel = "NA";
						}
					}

					RetRpc retRpcTunnelflux =  OTFluxNowDAO.getInstance().control(domainId, oTDevice.getId(), "tunnel_"+tunnel.getName(), MyData.RestType.GET, "");
					if(200 == retRpcTunnelflux.getStatusCode()){
						String flag = retRpcTunnelflux.getContent();
						try{
							flux = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
						}catch(Exception e){
							flux = "NA";
						}
					}
				}
				//from db
				//				List<String[]> rttTunnelList = db.getAll("nqa", "NQA"+"_"+"tunnel"+"_"+oTDevice.getId()+"_"+tunnel.getName());
				//				if(null != rttTunnelList && rttTunnelList.size() != 0){
				//					String flag = rttTunnelList.get(rttTunnelList.size()-1)[1];
				//					rttTunnel = flag.split("_")[1];
				//				}
				//				List<String[]> fluxList = db.getAll("flux", "FLUX"+"_"+"tunnel"+"_"+oTDevice.getId()+"_"+tunnel.getName());
				//				if(null != fluxList && fluxList.size() != 0){
				//					String flag =  fluxList.get(fluxList.size()-1)[1];
				//					flux = "0"/* flag.split("_")[1]*/;
				//				}
				xml.append("rtt= '").append(rttTunnel).append("' ");
				xml.append("flux= '").append(flux).append("' ");
				xml.append("deviceId= '").append(oTDevice.getId()).append("' ");
				xml.append("flow= '").append(tunnel.getWhereFlow()).append("' "); 
				if(null != tunnel.getTunnelPaths()){
					for(int j=0 ;j<tunnel.getTunnelPaths().size();j++){
						OTTunnelPath oTTunnelPath = tunnel.getTunnelPaths().get(j);
						if("hot_standby".equalsIgnoreCase(oTTunnelPath.getPathType())){
							xml.append("priPath= '").append(oTTunnelPath.getPathName()).append("' ");
						}else if("Primary".equalsIgnoreCase(oTTunnelPath.getPathType())){
							xml.append("backPath= '").append(oTTunnelPath.getPathName()).append("' ");
						}else{
							continue;
						}
						/*
						 if("hot_standby".equalsIgnoreCase(oTTunnelPath.getPathType())){
							for(int k=0;k<tunnel.getPaths().size();k++){
								OTPath oTPath = tunnel.getPaths().get(k);
								if(oTTunnelPath.getPathName().endsWith(oTPath.getName())){
									String pathByte = tunnel.getOTTunnelPathsUiMessage(oTPath);
									StringBuffer sbuf = new StringBuffer();
									for (int ii = 0; ii < pathByte.getBytes().length; ii++) {
										sbuf.append(pathByte.getBytes()[ii]);		
									}
									xml.append("backPath= '").append(sbuf).append("' ");
									break;
								}
							}
						}else if("Primary".equalsIgnoreCase(oTTunnelPath.getPathType())){
							for(int k=0;k<tunnel.getPaths().size();k++){
								OTPath oTPath = tunnel.getPaths().get(k);
								if(oTTunnelPath.getPathName().endsWith(oTPath.getName())){
									String pathByte = tunnel.getOTTunnelPathsUiMessage(oTPath);
									StringBuffer sbuf = new StringBuffer();
									for (int ii = 0; ii < pathByte.getBytes().length; ii++) {
										sbuf.append(pathByte.getBytes()[ii]);		
										}
									xml.append("Primary= '").append(sbuf).append("' ");
									break;
								}
							}
						}else{
							continue;
						}
						 */

					}
				}
				xml.append(">");
				if(null != policyList && null != flowList){
					for(int j=0 ;j<policyList.size();j++){
						OTPolicy oTPolicy = policyList.get(j);
						if(tunnel.getName().equals(oTPolicy.getTunnelName())){
							for(int k=0;k<flowList.size();k++){
								OTFlow oTFlow = flowList.get(k);
								if(oTPolicy.getName().equals(oTFlow.getPolicy())){
									xml.append("<flow ");
									xml.append("name= '").append(oTFlow.getName()).append("' ");
									xml.append("ingressIp= '").append(tunnel.getIngressIp()).append("' ");
									xml.append("egressIp= '").append(oTFlow.getDesIp()).append("' ");
									xml.append("deviceId= '").append(oTDevice.getId()).append("' ");
									String rttFlow = "NA";
									String lossFlow = "NA";
									Boolean rttFlowFlag = false;
									if(rttFlowFlag && timer == 0 && MyData.InitState.get(domainId)){
										RetRpc retRpcFlow =  OTNqaNowDAO.getInstance().control(domainId, oTDevice.getId(), "flow_"+oTFlow.getName(), MyData.RestType.GET, "");
										if(200 == retRpcFlow.getStatusCode()){
											String flag = retRpcFlow.getContent();
											try{
												rttFlow = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
												lossFlow = flag.substring(flag.indexOf("<value2>")+"<value2>".length(), flag.indexOf("</value2>"));
											}catch(Exception e){
												flux = "NA";
												lossFlow = "NA";
											}
										}
									}

									//									List<String[]> rttFlowList = db.getAll("nqa", "NQA"+"_"+"flow"+"_"+oTDevice.getId()+"_"+oTFlow.getName());
									//									if(null != rttFlowList && rttFlowList.size() != 0){
									//										String flag = rttFlowList.get(rttFlowList.size()-1)[1];
									//										rttFlow = flag.split("_")[1];
									//										lossFlow = flag.split("_")[2];
									//									}
									xml.append("rtt= '").append(rttFlow).append("' ");
									xml.append("loss= '").append(lossFlow).append("' ");
									xml.append("/>");
								}
							}
						}
					}
				}

				//				<tunnel name='' ingressIp='' egressIp='' state='' priPath='' backPath=''> 
				//				<flow name='' ingressIp='' egressIp=''/> 
				//		    </tunnel>
				xml.append("</tunnel>");
			}
			result.setContent(xml.toString());
			deviceTTreeMap.put(oTDevice.getId(), result);
		}
		public RetRpc getResult() {
			return result;
		}
	}


	class DeviceTTreeDataTask implements Runnable{

		public String domainId = "";
		@Override
		public void run() {
			if("".equals(domainId)){
				return;
			}
			String content = MyData.getTunnelTree().get(domainId);
			if(null == content || "".equals(content)){
				return;
			}

			HashMap<String, HashMap<String, String>> deviceTunnelFlux = new HashMap<String, HashMap<String, String>>();
			List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
			if(null == deviceList || deviceList.size() == 0){
				return;
			}
			//读取stat.xml
			List<String[]> statList = MyData.getStat();

			for(int index=0;index<deviceList.size();index++){
				OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceList.get(index).getId());
				MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
				HashMap<String, String> flux = myIfmInterface.getFlux(null);
				deviceTunnelFlux.put(deviceList.get(index).getId(), flux);
			}

			try {
				Document doc = DocumentHelper.parseText(content);
				Element el = doc.getRootElement();
				String domainId = el.attributeValue("id");
				OpsRestCaller client = null;
				for(Iterator<Element> i=el.elementIterator();i.hasNext();){
					Element domain = i.next();
					String deviceId = domain.attributeValue("deviceId");

					String tunnelName = domain.attributeValue("name").split("-")[0];  //注意devicename 可能包含"-"
					String rttTunnel = "NA";
					String flux = "NA";
					//不取nqa
					//					RetRpc retRpcTunnelrtt =  OTNqaNowDAO.getInstance().control(domainId, deviceId, "tunnel_"+tunnelName, MyData.RestType.GET, "");
					//					if(200 == retRpcTunnelrtt.getStatusCode()){
					//						String flag = retRpcTunnelrtt.getContent();
					//						try{
					//							rttTunnel = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
					//						}catch(Exception e){
					//							rttTunnel = "NA";
					//						}
					//					}
					for(int statCount=0;statCount<statList.size();statCount++){
						String[] stat = statList.get(statCount);
						if(null != stat && stat[0].equals(domainId) && stat[1].equals(deviceId) && stat[2].equals(tunnelName)){
							tunnelName = stat[3];
						}
					}
					
					String tunnelFlux = deviceTunnelFlux.get(deviceId).get(tunnelName);
					if(null != tunnelFlux && !"".equals(tunnelFlux)){
						RetRpc retRpcTunnelflux =  OTFluxNowDAO.getInstance().get("", tunnelFlux);
						if(200 == retRpcTunnelflux.getStatusCode()){
							String flag = retRpcTunnelflux.getContent();
							try{
								flux = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
							}catch(Exception e){
								flux = "NA";
							}
						}
					}

					if(!"NA".equals(rttTunnel)){
						Attribute attribute = domain.attribute("rtt");
						attribute.setText(rttTunnel);
					}
					if(!"NA".equals(flux)){
						Attribute attribute = domain.attribute("flux");
						attribute.setText(flux);
					}
					//不取nqa
					//					for(Iterator<Element> j=domain.elementIterator();j.hasNext();){
					//						Element flow = j.next();
					//						if(null != flow && "flow".equals(flow.getName())){
					//							String rttFlow = "NA";
					//							String lossFlow = "NA";
					//							String flowName = flow.attributeValue("name");
					//							
					//							RetRpc retRpcFlow =  OTNqaNowDAO.getInstance().control(domainId, deviceId, "flow_"+flowName, MyData.RestType.GET, "");
					//							if(200 == retRpcFlow.getStatusCode()){
					//								String flag = retRpcFlow.getContent();
					//								try{
					//									rttFlow = flag.substring(flag.indexOf("<value1>")+"<value1>".length(), flag.indexOf("</value1>"));
					//									lossFlow = flag.substring(flag.indexOf("<value2>")+"<value2>".length(), flag.indexOf("</value2>"));
					//								}catch(Exception e){
					//									lossFlow = "NA";
					//								}
					//							}
					//							if(!"NA".equals(rttFlow)){
					//								Attribute attribute = flow.attribute("rtt");
					//								attribute.setText(rttTunnel);
					//							}
					//							if(!"NA".equals(lossFlow)){
					//								Attribute attribute = flow.attribute("loss");
					//								attribute.setText(lossFlow);
					//							}
					//						}
					//					}
				}
				String result = doc.asXML();
				MyData.getTunnelTree().put(domainId, result);
				StringBuffer xml = new StringBuffer();
				xml.append("<massage type='tfs'>").append(result).append("</massage>");
				if(null != Activator.autobahnServer){
					Activator.autobahnServer.sendToAll(xml.toString());
				}
			}catch (DocumentException e) {
				//e.printStackTrace();
			}

		}



	}


	public List<String[]> getStat(){
		List<String[]> statList = new ArrayList<String[]>();
		ReadConfig readConfig = new ReadConfig();
		String statPath = readConfig.getPath().append("conf\\stat.xml").toString();
		File file = new File(statPath);
		if(file.isFile() && file.exists()){
			List<OTDevice> allDevices = OTDeviceDAO.getInstance().getAll();
			try {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(file);
				
				Element el = doc.getRootElement();
				if("domains".equals(el.getName())){
					for(Iterator<Element> i=el.elementIterator();i.hasNext();){
						Element domain = i.next();
						for(Iterator<Element> j=domain.elementIterator();j.hasNext();){
							Element device = j.next();
							for(Iterator<Element> k=device.elementIterator();k.hasNext();){
								Element tunnel = k.next();
								String [] stat = new String[5];
								OTDomain oTDomain = OTDomainDAO.getInstance().getByName(domain.attributeValue("name"));
								if(null != oTDomain && !"".equals(oTDomain.getId())){
									stat[0] = oTDomain.getId();
								}
								for(int z=0;z<allDevices.size();z++){
									OTDevice oTDevice = allDevices.get(z);
									if(oTDevice.getDeviceName().equals(device.attributeValue("name"))){
										stat[1]=oTDevice.getId();
										break;
									}
								}
								stat[2]=tunnel.attributeValue("name");
								stat[3]=tunnel.attributeValue("toInterface");
								stat[4]=tunnel.attributeValue("ifoperspeed");
								//stat[3] = stat[3].replace("/", "%2f");
								statList.add(stat);
							}
						}
					}
				}
			}catch (DocumentException e) {
			}
		}
		return statList;
	}



	public static void main(String[] args) {
		//String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
		//OTPolicyDAO.getInstance().get("1", "5", "");
		//System.out.println(OTTfDAO.getInstance().control("2","50","refurbish",MyData.RestType.GET,"").getContent());
		System.out.println(OTTfDAO.getInstance().getStat());
		
	}



}
