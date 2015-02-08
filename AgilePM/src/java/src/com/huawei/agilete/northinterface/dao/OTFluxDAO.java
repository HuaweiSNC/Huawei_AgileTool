package com.huawei.agilete.northinterface.dao;

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


public class OTFluxDAO  implements IOTDao{

	private static OTFluxDAO single = null;
	private OpsRestCaller client = null;
	public String deviceId = "";
	private OTFluxDAO(){

	}
	public synchronized  static OTFluxDAO getInstance() {
		if (single == null) {  
			single = new OTFluxDAO();
		}  
		return single;
	}

	public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
		RetRpc result = new RetRpc();
		this.deviceId = deviceId;
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

	/***
	 * @param apiPath: deviceId-tunnelName
	 */
	public RetRpc get(String apiPath){
		RetRpc opsresult = new RetRpc();
		String type = "";
		String name  = "";
		
		if(2 == apiPath.split("_").length){
			type = apiPath.split("_")[0];
			name = apiPath.split("_")[1];
		}
		
		DBAction db = new DBAction();
		List<String[]> rttList = db.getAll("flux", "FLUX"+"_"+type+"_"+deviceId+"_"+name);
		List<OTNqa> list = new ArrayList<OTNqa>();
		if(null != rttList){
			int i = rttList.size()>=20?rttList.size()-20:0;
			for(;i<rttList.size();i++){
				String[] flag = rttList.get(i);
				//<data><schedule>$!{x}</schedule><value1>$!{y1}</value1><value2>$!{y2}</value2></data>
				OTNqa nqa = new OTNqa();
				nqa.setTime(flag[0]);
				nqa.setRttAverage("0"/*flag[1]*/);
				list.add(nqa);
			}
			if(!list.isEmpty()){
				OTNqa nqa = new OTNqa();
				opsresult.setContent(nqa.getUiMessage(list));
			}else{
				opsresult.setContent("");
			}
			
			
		}


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
		/*System.out.println(OTNqaDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
	}
	public OpsRestCaller getClient() {
		return client;
	}
	public void setClient(OpsRestCaller client) {
		this.client = client;
	}



}
