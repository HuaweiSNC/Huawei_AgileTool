package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyTunnelPolicy;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTPolicy;


public class OTPolicyDAO  implements IOTDao{

	private static OTPolicyDAO single = null;
	private OpsRestCaller client = null;
	private OTPolicyDAO(){

	}
	public synchronized  static OTPolicyDAO getInstance() {
		if (single == null) {  
			single = new OTPolicyDAO();
		}  
		return single;
	}

	 public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
			RetRpc result = new RetRpc();
			client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
		if(restType.equals(MyData.RestType.GET)){
			result = get(apiPath);
		}else if(restType.equals(MyData.RestType.DELETE)){
			result  = del(apiPath);
		}else if(restType.equals(MyData.RestType.POST)){
			result = add(content);
		}else if(restType.equals(MyData.RestType.PUT)){
			result  = edit(content);
		}else{

		}

		return result;
	}
	
	
	public RetRpc add(String content){
		RetRpc result = new RetRpc();
		OTPolicy oTPolicy = new OTPolicy(content);
		String bodyPath = oTPolicy.getOpsMessage();
		MyTunnelPolicy myTunnelPolicy  =new MyTunnelPolicy(client);
		myTunnelPolicy.body=bodyPath;
		result = myTunnelPolicy.create(null);
		return result;
	}

	public RetRpc edit(String content){
		RetRpc result = new RetRpc(403);
//		OTPolicy oTPolicy = new OTPolicy(content);
//		result = del(oTPolicy.getName());
//		if(result.getStatusCode()==200){
//			result = add(content);
//		}
		return result;
	}

	public RetRpc del(String apiPath){
		RetRpc result = new RetRpc();
		if(null != apiPath && !"".equals(apiPath)){
			MyTunnelPolicy myTunnelPolicy  =new MyTunnelPolicy(client);
			myTunnelPolicy.body = apiPath;
			result = myTunnelPolicy.delete(null);
		}else{
			result.setStatusCode(500);
			result.setContent("Error!ApiPath is null!");
		}
		return result;
	}

	public RetRpc get(String apiPath){
		RetRpc opsresult = new RetRpc();
		List<OTPolicy> list = getList(apiPath);
		if(null != list){
			OTPolicy oTPolicy = new OTPolicy();
			String flag = oTPolicy.getUiMessage(list);
			opsresult.setContent(flag);
		}else{
			opsresult.setStatusCode(500);
			opsresult.setContent("Error!");
		}
		return opsresult;
	}
	
	public List<OTPolicy> getList(String apiPath){
		List<OTPolicy> list = null;
		MyTunnelPolicy myTunnelPolicy  =new MyTunnelPolicy(client);
		String[] name = null;
		if(!"".equals(apiPath)){
			name = new String[]{apiPath};
		}
		RetRpc opsresult = myTunnelPolicy.get(name);
		if(opsresult.getStatusCode()==200){
			OTPolicy oTPolicy = new OTPolicy();
			list = oTPolicy.parseOpsToUi(opsresult.getContent());
		}
		return list;
	}
	
	

	public static void main(String[] args) {
		//String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
		//OTPolicyDAO.getInstance().get("1", "5", "");
		/*System.out.println(OTPolicyDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
	}
	public OpsRestCaller getClient() {
		return client;
	}
	public void setClient(OpsRestCaller client) {
		this.client = client;
	}
	


}
