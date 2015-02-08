package com.huawei.agilete.northinterface.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.base.servlet.MyNqa;
import com.huawei.agilete.base.servlet.MyTunnelPolicy;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.networkos.ops.util.threadpool.WorkerTaskBean;
import com.huawei.plugins.rrdtool.manage.RrdTool;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.agilete.northinterface.bean.OTNqa;
import com.huawei.agilete.northinterface.bean.OTPolicy;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.agilete.task.TaskFluxTunnel;
import com.huawei.agilete.task.TaskNqa;
import com.huawei.agilete.task.TaskWrapper;


public class OTFluxNowDAO  implements IOTDao{

	private static OTFluxNowDAO single = null;
	private OpsRestCaller client = null;
	public String domainId;
	private OTFluxNowDAO(){

	}
	public synchronized  static OTFluxNowDAO getInstance() {
		if (single == null) {  
			single = new OTFluxNowDAO();
		}  
		return single;
	}

	public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
		RetRpc result = new RetRpc();
		this.domainId = domainId;
		client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
		if(restType.equals(MyData.RestType.GET)){
			result = get(apiPath);
		}/*else if(restType.equals(MyData.RestType.DELETE)){
			result  = del(apiPath);
		}else if(restType.equals(MyData.RestType.POST)){
			result = add(content);
		}else if(restType.equals(MyData.RestType.PUT)){
			result  = edit(content);
		}*/else{

		}

		return result;
	}


	public RetRpc add(String content){
		RetRpc result = new RetRpc();


		return result;
	}

	public RetRpc edit(String content){
		RetRpc result = new RetRpc(403);
		return result;
	}

	public RetRpc del(String apiPath){
		RetRpc result = new RetRpc(403);
		return result;
	}

	public RetRpc get(String apiPath){
		return get(apiPath, "");
	}

	/***
	 * @param apiPath: tunnel_tunnelName
	 */
	public RetRpc get(String apiPath,String value){
		RetRpc opsresult = new RetRpc();
		String type = "";
		String name  = "";
		String body = "";
		String statSpeed = "0";
		if(2 == apiPath.split("_").length){
			type = apiPath.split("_")[0];
			name = apiPath.split("_")[1];
		}
		//读取stat.xml
		List<String[]> statList = MyData.getStat();
		for(int statCount=0;statCount<statList.size();statCount++){
			String[] stat = statList.get(statCount);
			if(null != stat[0] && null != stat[1] && stat[0].equals(domainId) && stat[1].equals(client.deviceId) && stat[2].equals(name)){
				name = stat[3];
				statSpeed = stat[4];
				break;
			}
		}

		String content = "";
		String content2 = "";
		if("-1".equals(MyData.getSimulantData())){
			if(null == value || "".equals(value)){
				MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
				HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
				content = flux.get(name);
			}else{
				content = value;
			}

			if(null == value || "".equals(value)){
				MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
				HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{name});
				content2 = flux.get(name);
			}else{
				content2 = value;
			}
		}
		BigDecimal receibveByte ;
		BigDecimal sendByte;
		BigDecimal ifoperspeed;
		BigDecimal time;
		String twoValue = "NA";
		//currBandwith = (new BigDecimal(Bandwidth).subtract(new BigDecimal(lastBandwidth.get(lbId)))).divide((new BigDecimal(timeInterval)).divide(new BigDecimal(1000)),2).multiply(new BigDecimal(8));
		String receibveByte1 = "0", receibveByte2 = "0";
		String sendByte1 = "0", sendByte2 = "0";
		String ifoperspeed1 = "0", ifoperspeed2 = "0";
		String time1 = "0", time2 = "0";
		if(null != content && !"".equals(content) && null != content2 && !"".equals(content2)){
			try{
				receibveByte1 = content.split("_")[0];
				sendByte1 = content.split("_")[1];
				ifoperspeed1 = content.split("_")[2];
				time1 = content.split("_")[3];

				receibveByte2 = content2.split("_")[0];
				sendByte2 = content2.split("_")[1];
				ifoperspeed2 = content2.split("_")[2];
				time2 = content2.split("_")[3];

				receibveByte = new BigDecimal(receibveByte2).subtract(new BigDecimal(receibveByte1));
				sendByte = new BigDecimal(sendByte2).subtract(new BigDecimal(sendByte1));
				time = new BigDecimal(time2).subtract(new BigDecimal(time1)).divide(new BigDecimal(1000),2); 
				if(null == statSpeed || "0".equals(statSpeed) || "".equals(statSpeed)){
					if(!"0".equals(ifoperspeed1) && !"".equals(ifoperspeed1)){
						statSpeed = ifoperspeed1;
					}else if(!"0".equals(ifoperspeed2) && !"".equals(ifoperspeed2)){
						statSpeed = ifoperspeed2;
					}else{
						statSpeed = "1";
					}
				}
				if(receibveByte.equals(new BigDecimal("0")) && sendByte.equals(new BigDecimal("0"))){
					twoValue = "0";
				}else{
					twoValue = receibveByte.add(sendByte).divide(time,10,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(statSpeed),BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(8)).toString();
				}
			}catch(Exception e){
				twoValue = "NA";
				//e.printStackTrace();
			}
		}

		List<OTNqa> list = new ArrayList<OTNqa>();
		OTNqa nqa = new OTNqa();
		//		MyData.setSimulantData("10");
		if("-2".equals(MyData.getSimulantData())){
			nqa.setTime(String.valueOf(System.currentTimeMillis()));
			nqa.setRttAverage(String.valueOf((int)(Math.random()*100)));
		}else if("-1".equals(MyData.getSimulantData())){
			//long ave = Long.valueOf(receibveByte) +Long.valueOf(sendByte);
			nqa.setTime(String.valueOf(System.currentTimeMillis()));
			nqa.setRttAverage(twoValue);
		}else{
			nqa.setTime(String.valueOf(System.currentTimeMillis()));
			nqa.setRttAverage(MyData.getSimulantData());
		}
		String aa =RrdTool.getInstance().update_fetchRrd("ifmflux", nqa.getTime(), nqa.getRttAverage());
		//System.out.println("rrdtool="+aa);
		list.add(nqa);
		opsresult.setContent(nqa.getUiMessage(list));
		return opsresult;
	}



	public RetRpc runTaskMain(String domainId,String type){

		RetRpc opsresult = new RetRpc();
		List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
		if(null == deviceList || deviceList.size() == 0){
			return opsresult;
		}
		for(int index=0;index<deviceList.size();index++){
			OTDevice oTDevice = deviceList.get(index);
			OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTDevice.getId());
			//根据当前设备所有tunnel
			OTTunnelDAO.getInstance().setClient(client);
			List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps("",false);
			for(int i=0;i<tunnelList.size();i++){
				OTTunnel tunnel = tunnelList.get(i);
				TaskFluxTunnel tunnelTask = new TaskFluxTunnel(oTDevice.getId());
				long t = 0;
				try{
					t = Long.parseLong("0");
				}catch(Exception e){
				}
				tunnelTask.client = client;
				tunnelTask.setInterval(t);
				tunnelTask.setTitle("FLUX"+"_"+type+"_"+client.deviceId+"_"+tunnel.getName()); //NQA CE12800_A TUNNEL1
				OTNqa tunnelNqa = new OTNqa();
				tunnelNqa.setName(tunnelTask.getTitle());
				tunnelTask.body = tunnel.getName();
				if("tunnel".equals(type)){
					MyData.TaskChildControl.addTask(tunnelTask);
				}
			}
		}
		return opsresult;
	}


	public RetRpc runTask(OpsRestCaller client,TaskFluxTunnel taskWrapper){
		RetRpc opsresult = new RetRpc();
		MyIfmInterface myIfmInterface  =new MyIfmInterface(client);
		HashMap<String, String> flux = myIfmInterface.getFlux(new String[]{taskWrapper.body});
		String content= flux.get(taskWrapper.body);
		DBAction db = new DBAction();
		Boolean dbFlag = db.insert("flux", taskWrapper.getTitle(), String.valueOf(System.currentTimeMillis()), content);
		if(dbFlag){
			List<String[]> allList = db.getAll("flux", taskWrapper.getTitle());
			int maxlengh = 100;
			if(allList.size() >= maxlengh){
				for(int i=0;i<=allList.size()-maxlengh;i++){
					String[] flagData = allList.get(i);
					dbFlag = db.delete("flux", taskWrapper.getTitle(), flagData[0]);
				}
			}
		}

		return opsresult;
	}


	public String getOpsBody(String type,OTNqa nqa){
		OTNqa oTNqa = nqa;
		String body = oTNqa.getOpsMessage();
		return body;
	}



	public static void main(String[] args) {
		//String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
		//OTNqaDAO.getInstance().get("1", "5", "");
		System.out.println(OTFluxNowDAO.getInstance().control("2", "4", "tunnel_Tunnel0", MyData.RestType.GET, ""));
	}
	public OpsRestCaller getClient() {
		return client;
	}
	public void setClient(OpsRestCaller client) {
		this.client = client;
	}



}
