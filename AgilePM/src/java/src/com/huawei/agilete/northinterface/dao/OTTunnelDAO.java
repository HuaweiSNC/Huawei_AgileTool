package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.base.servlet.MyMplsExplicitPath;
import com.huawei.agilete.base.servlet.MyMplsRsvpTeTunnel;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTPath;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.agilete.northinterface.bean.OTTunnelPath;


public class OTTunnelDAO   implements IOTDao{

	private static OTTunnelDAO single = null;
	private  OpsRestCaller client = null;
	private String domainId;
	public OTTunnelDAO(){

	}
	public synchronized  static OTTunnelDAO getInstance() {
		if (single == null) {  
			single = new OTTunnelDAO();
		}  
		return single;
	}



	public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
		RetRpc result = new RetRpc();
		client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
		this.domainId = domainId;
		if(restType.equals(MyData.RestType.GET)){
			if(2 == apiPath.split("__").length && "state".equals(apiPath.split("__")[1])){
				result = getTunnelState(apiPath.split("__")[0]);
			}else{
				result = get(apiPath);
			}

		}else if(restType.equals(MyData.RestType.DELETE)){
			result = del(apiPath);
		}else if(restType.equals(MyData.RestType.POST)){
			result = add(content);
		}else if(restType.equals(MyData.RestType.PUT)){
			result = edit(content);
		}else if(restType.equals(MyData.RestType.GETOTHER)){
			result = getPaths(apiPath);
		}else{

		}
		return result;
	}

	public RetRpc add(String content){
		RetRpc result = new RetRpc();
		//		MyRollback myRollback = new MyRollback(client);
		//		RetRpc result = myRollback.getPoints(null);
		//		String rollBackXML = result.getContent();
		//		result = new RetRpc();
		//		if(result.getStatusCode()!=200){
		//			result.setContent("Back is error!");
		//			return result;
		//		}
		OTTunnel oTTunnel_ = new OTTunnel(content);
		for(OTTunnel oTTunnel:oTTunnel_.getTunnels()){
			if(result.getStatusCode() == 200){
				OpsRestCaller client = null;
				if(!"".equals(oTTunnel.getDeviceId())){
					client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTTunnel.getDeviceId());
				}else{
					client =this.client;
				}
				String[] bodyPath = oTTunnel.getOTPathOpsMessage();
				MyMplsExplicitPath myMplsExplicitPath = new MyMplsExplicitPath(client);
				if(result.getStatusCode() == 200){
					for(int i=0;i<bodyPath.length;i++){
						//if(result.getStatusCode()==200){
						myMplsExplicitPath.body=bodyPath[i];
						result = myMplsExplicitPath.create(null);
						if(result.getStatusCode() != 200){
							break;
						}
						//}
					}
				}
				if(result.getStatusCode() == 200){
					String bodyTunnel = oTTunnel.getOTTunnelOpsMessage();
					MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(client);
					myMplsRsvpTeTunnel.body =bodyTunnel;
					result = myMplsRsvpTeTunnel.create(null);
					if(result.getStatusCode() != 200){
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							//				e.printStackTrace();
						}
						result = myMplsRsvpTeTunnel.create(null);
					}
				}

				if(result.getStatusCode() == 200){
					MyIfmInterface myIfmInterface = new MyIfmInterface(client);
					myIfmInterface.body = "<ifmAm4><unNumIfName>LoopBack0</unNumIfName></ifmAm4>";
					myIfmInterface.modify(new String[]{oTTunnel.getName()});
				}
				//			if(result.getStatusCode()!=200){
				//				String commitId = myRollback.getXMLToLastCommitId(rollBackXML);
				//				String[] commitIds = {commitId};
				//				myRollback.rollbackByCommitId(commitIds);
				//				return result;
				//			}
			}
		}

		if(result.getStatusCode() != 200){
			for(OTTunnel oTTunnel:oTTunnel_.getTunnels()){
				OpsRestCaller clientDel = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTTunnel.getDeviceId());
				List<OTTunnelPath> oTTunnelPaths = oTTunnel.getTunnelPaths();
				for(OTTunnelPath oTTunnelPath:oTTunnelPaths){
					MyMplsExplicitPath myMplsExplicitPath = new MyMplsExplicitPath(clientDel);
					myMplsExplicitPath.body=null;
					myMplsExplicitPath.delete(new String[]{oTTunnelPath.getPathName()});
				}
				MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(clientDel);
				myMplsRsvpTeTunnel.delete(new String[]{oTTunnel.getName()});
			}
		}

		return result;
	}

	public RetRpc edit(String content){
		RetRpc result = new RetRpc();
		OTTunnel oTTunnel_ = new OTTunnel(content);
		//		String[] bodyPath = oTTunnel.getOTPathOpsMessage();
		//		MyMplsExplicitPath myMplsExplicitPath = new MyMplsExplicitPath(deviceId);
		//		List<OTPath> path = oTTunnel.getPaths();
		//		for(int i=0;i<path.size();i++){
		//			if(result.getStatusCode()==200){
		//				result = myMplsExplicitPath.delete(new String[]{path.get(i).getName()});
		//			}
		//		}
		//		for(int i=0;i<bodyPath.length;i++){
		//			if(result.getStatusCode()==200){
		//				myMplsExplicitPath.body=bodyPath[i];
		//				result = myMplsExplicitPath.create(null);
		//			}
		//		}
		//		if(result.getStatusCode()==200){
		//			String bodyTunnel = oTTunnel.getOTTunnelOpsMessage();
		//			MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(deviceId);
		//			myMplsRsvpTeTunnel.body =bodyTunnel;
		//			result = myMplsRsvpTeTunnel.modify(null);
		//		}
		for(OTTunnel oTTunnel:oTTunnel_.getTunnels()){
			OpsRestCaller client = null;
			if(!"".equals(oTTunnel.getDeviceId())){
				client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTTunnel.getDeviceId());
			}else{
				client =this.client;
			}
			MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(client);
			StringBuffer body = new StringBuffer();
			body.append("<tunnelName>").append(oTTunnel.getName()).append("</tunnelName>");
			body.append("<hotStandbyWtr>").append(oTTunnel.getHotStandbyTime()).append("</hotStandbyWtr>");
			myMplsRsvpTeTunnel.body =body.toString();
			result = myMplsRsvpTeTunnel.modify(null);
			if(result.getStatusCode()!=200){
				return result;
			}
		}
		return result;
	}
	public RetRpc del(String apiPath){
		RetRpc result = new RetRpc();
		List<OTTunnel> list = getOps(apiPath,false);
		OTTunnel oTTunnel = null;
		for(int i=0;i<list.size();i++){
			OTTunnel flag = list.get(i);
			if(apiPath.equals(flag.getName())){
				oTTunnel = flag;
			}
		}
		if(null != oTTunnel){
			//删tunnel
			if(result.getStatusCode()==200 && null != oTTunnel.getName() && !oTTunnel.getName().isEmpty() ){
				MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(client);
				result = myMplsRsvpTeTunnel.delete(new String[]{oTTunnel.getName()});
			}
			//删path
			if(result.getStatusCode()==200){
				List<OTTunnelPath> oTPathList= oTTunnel.getTunnelPaths();
				MyMplsExplicitPath myMplsExplicitPath = null;
				if(null != oTPathList && oTPathList.size() !=0){
					//				 result = true;
					myMplsExplicitPath = new MyMplsExplicitPath(client);
					for(int i=0;i<oTPathList.size();i++){
						String pathName = oTPathList.get(i).getPathName();
						//						if(result){
						if(null != pathName && !pathName.isEmpty()){
							result = myMplsExplicitPath.delete(new String[]{pathName});
						}
						//						}
					}
				}
			}
		}else{
			result.setStatusCode(500);
			result.setContent("Error!Get Tunnel error!");
		}
		return result;
	}

	public List<OTTunnel> getOps(String apiPath,Boolean allNot){
		MyMplsRsvpTeTunnel myMplsRsvpTeTunnel = new MyMplsRsvpTeTunnel(client);
		String[] name = null;
		if(!"".equals(apiPath)){
			name = new String[]{apiPath};
		}

		RetRpc opsresult = myMplsRsvpTeTunnel.get(name);
		if(opsresult.getStatusCode() != 200){
			return null;
		}
		OTTunnel oTTunnel = new OTTunnel();
		List<OTTunnel> list = oTTunnel.parseOpsToUi(opsresult.getContent(),client,allNot);
		List<OTTunnel> one = new ArrayList<OTTunnel>();
		if(!"".equals(apiPath)){
			for(int i =0;i<list.size();i++){
				OTTunnel tunnel = list.get(i);
				if(apiPath.equals(tunnel.getName())){
					one.add(tunnel);
				}
			}
			return one;
		}
		return list;
	}

	public RetRpc get(String apiPath){
		RetRpc result = new RetRpc();
		String resultString = "";
		Boolean allNot = false;
		if(null != apiPath && !apiPath.isEmpty()){
			allNot = true;
		}

		List<OTTunnel> list = getOps(apiPath,allNot);
		//		if(null != list){
		OTTunnel oTTunnel = new OTTunnel();
		resultString = oTTunnel.getOTTunnelUiMessage(list);
		result.setContent(resultString);
		result.setStatusCode(200);
		//		}else{
		//			if(!apiPath.isEmpty()){
		//				result.setContent("can not find "+apiPath);
		//				result.setStatusCode(500);
		//			}
		//			if(!apiPath.isEmpty()){
		//				result.setContent("can not find "+apiPath);
		//				result.setStatusCode(500);
		//			}
		//		}
		return result;
	}

	public RetRpc getTunnelState(String apiPath){
		RetRpc result = new RetRpc();
		String resultString = "";
		Boolean allNot = false;
		if(null != apiPath && !apiPath.isEmpty()){
			allNot = true;
		}

		List<OTTunnel> list = getOps(apiPath,allNot);
		if(null != list && list.size() == 1){
			OTTunnel oTTunnel = list.get(0);
			resultString = "<tunnels><tunnel><flow>"+oTTunnel.getWhereFlow()+"</flow></tunnel></tunnels>";
			result.setContent(resultString);
			result.setStatusCode(200);
		}else{
			result.setContent("can not find "+apiPath);
			result.setStatusCode(500);
		}
		return result;
	}

	public RetRpc getPaths(String apiPath){
		RetRpc result = new RetRpc();
		List<OTTunnel> list = getOps(apiPath,true);
		if(null != list && list.size() == 1){
			OTTunnel oTTunnel = list.get(0);
			List<OTPath> pathsList = new ArrayList<OTPath>();
			for(int i =0;i<oTTunnel.getPaths().size();i++){
				OTPath path = oTTunnel.getPaths().get(i);
				for(int j =0;j<oTTunnel.getTunnelPaths().size();j++){
					OTTunnelPath oTTunnelPath =oTTunnel.getTunnelPaths().get(j);
					if(path.getName().equals(oTTunnelPath.getPathName())){
						path.setType(oTTunnelPath.getPathType());
						break;
					}
				}
				pathsList.add(path);
			}
			String flag = oTTunnel.getOTTunnelPathUiMessage(pathsList);
			result.setContent(flag);
			result.setStatusCode(200);
		}else{
			result.setStatusCode(403);
		}
		return result;
	}



	public static void main(String[] args) {
		String content = "<tunnels><tunnel><name>Tunnel14</name><interfaceName>Tunnel14</interfaceName><identifyIndex>301</identifyIndex><ingressIp>3.3.3.3</ingressIp><egressIp>1.1.1.1</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>true</isDouleConfig><desDeviceName>rdrt1</desDeviceName><state></state><tunnelPaths><tunnelPath><pathType>main</pathType><pathName>maink</pathName><lspState>up</lspState></tunnelPath><tunnelPath><pathType>backup</pathType><pathName>backk</pathName><lspState>up</lspState></tunnelPath></tunnelPaths><paths><path><name>maink</name><nextHops><nextHop><id>1</id><nextIp>10.3.1.2</nextIp></nextHop><nextHop><id>2</id><nextIp>10.4.1.1</nextIp></nextHop><nextHop><id>3</id><nextIp>1.1.1.1</nextIp></nextHop></nextHops></path><path><name>backk</name><nextHops><nextHop><id>1</id><nextIp>10.5.1.1</nextIp></nextHop><nextHop><id>2</id><nextIp>10.2.1.2</nextIp></nextHop><nextHop><id>3</id><nextIp>1.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
		//		System.out.println(OTTunnelDAO.getInstance().add("1", "31", content));

		//String content = "<tunnels><tunnel><name>Tunnel678</name><interfaceName>Tunnel678</interfaceName><identifyIndex>705</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig></isDouleConfig><desDeviceName></desDeviceName><state></state><tunnelPaths><tunnelPath><pathType>primary</pathType><pathName>qq</pathName></tunnelPath><tunnelPath><pathType>hot_standby</pathType><pathName>aa</pathName></tunnelPath></tunnelPaths></tunnel></tunnels>";
		//System.out.println(OTTunnelDAO.getInstance().getPaths("1", "4", "Tunnel110"));
		//		OTTunnel oTTunnel = new OTTunnel(content);
		//		String bodyPath = oTTunnel.getOTTunnelOpsMessage();
		System.out.println(OTTunnelDAO.getInstance().control("2", "1", "Tunnel0", MyData.RestType.GETOTHER, ""));
	}

	public OpsRestCaller getClient() {
		return client;
	}
	public void setClient(OpsRestCaller client) {
		this.client = client;
	}



}
