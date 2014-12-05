package main;

import java.util.ArrayList;
import java.util.List;

import serviceflow.ServiceflowsFlow;
import serviceflow.ServiceflowsFlowFilter;
import serviceflow.ServiceflowsFlowPolicy;
import serviceflow.ServiceflowsRedirect;
import serviceflow.ServiceflowsServiceflow;
import serviceflow.ServiceflowsTunnelEncap;
import util.OpsRestCaller;
import util.OpsServiceManager;

public class ServiceFlowManager {

	private OpsRestCaller restCaller =OpsServiceManager.getInstance().getDefaultOpsRestCall();
	
	
	public boolean CreateFlow () throws Exception {
		
 
		//创建 某租户的serverflow组
		String flowGroupName ="video";
		String flowGroupid ="1";
		String tenantId ="QQ";
		boolean bret = createServerFlowGroup(tenantId, flowGroupid, flowGroupName);
		
		if(!bret) return false;
		
		//创建 流类型 
		String flowName ="MP4";
		String flowid ="1";
		bret = createFlow(flowGroupid, flowName, flowid);
		
		//创建具体的流识别器，流过滤器
		String srcIpAddress = "10.139.111.2";
		String srcIpAddressMask = "255.255.255.0";
		String destIpAddress = "10.139.111.3";
		String destIpAddressMask = "255.255.255.0";
		int seqNum = 1;
		 bret = createFlowFilter(flowGroupid, flowid, seqNum, srcIpAddress,srcIpAddressMask, destIpAddress,destIpAddressMask);
		
		 //定义流应用策略，并指定租户的VLAN ID 是什么。
		 String policyName ="name";
		 String policyId = "1";
		 String vlanid = "100";
		 bret = createFlowPolicy(flowGroupid, flowid,policyId, policyName);
		 bret = createFlowpolicysVlan(flowGroupid, policyId, vlanid);
		  
		  
		 //绑定策略路由生效的接口或列表, 设置北向接口 与安全设备连接的接口
		 List<String> lstNetworkIfName = new ArrayList<String>();
		 lstNetworkIfName.add("tenantnetwork1");
		 String inressinterface = "interface100";
		 String egressinterface = "interface200";
		 bret = createRedirect(flowGroupid, inressinterface, egressinterface);
		  
		 return true;
		 
		
	}
	
	/****
	 * 创建流组信息内容
	 * @param flowGroupName
	 * @param flowGroupID
	 * @param flowGroup
	 * @return
	 * @throws Exception
	 */
	public boolean createServerFlowGroup(String flowGroup, String flowGroupID, String flowGroupName) throws Exception {
		
		
		List list = new ArrayList<ServiceflowsServiceflow.OneBody>();
		
		ServiceflowsServiceflow tenantapi = new ServiceflowsServiceflow(restCaller);
		ServiceflowsServiceflow.OneBody flowForTenant = tenantapi.new OneBody();
		flowForTenant.setId(flowGroupID);
		flowForTenant.setName(flowGroupName);
		flowForTenant.setTenantId(flowGroup);
		
		tenantapi.setBody(flowForTenant);
		
		return tenantapi.create();
	}
 
	/***
	 * 创建 流
	 * @param flowGroup
	 * @param flowName
	 * @param flowId
	 * @return
	 * @throws Exception
	 */
	public boolean createFlow(String flowGroup, String flowName, String flowId) throws Exception {
		
		
		List list = new ArrayList<ServiceflowsFlow.OneBody>();
		ServiceflowsFlow tenantapi = new ServiceflowsFlow(restCaller);
		tenantapi.setId(flowGroup);
		ServiceflowsFlow.OneBody flow = tenantapi.new OneBody();
		flow.setFlowId(flowId);
		flow.setFlowName(flowName);
		flow.setFlowType("IPV4");
		tenantapi.setBody(flow);
		return tenantapi.create();
	}
	
	
	/***
	 * 策略作用的方向
	 * @param flowGroup
	 * @param flowId
	 * @param destIpAddress
	 * @param seqNum
	 * @param destIpAddressMask
	 * @param srcIpAddress
	 * @param srcIpAddressMask
	 * @return
	 * @throws Exception
	 */
	public boolean createFlowFilter(String flowGroup, String flowId, int seqNum, String srcIpAddress, String srcIpAddressMask, String destIpAddress, String destIpAddressMask) throws Exception {
		
		
		List list = new ArrayList<ServiceflowsFlow.OneBody>();
		
		ServiceflowsFlowFilter tenantapi = new ServiceflowsFlowFilter(restCaller);
		ServiceflowsFlowFilter.OneBody flow = tenantapi.new OneBody();
		tenantapi.setId(flowGroup);
		tenantapi.setFlowId(flowId);
		flow.setDestIpAddress(destIpAddress);
		flow.setDestIpAddressMask(destIpAddressMask);
		flow.setSeqNum(String.valueOf(seqNum));
		flow.setSrcIpAddress(srcIpAddress);
		flow.setSrcIpAddressMask(srcIpAddressMask);
		
		tenantapi.setBody(flow);
		
		return tenantapi.create();
	}
	
	
	/***
	 * 策略作用的主接口
	 * @param flowGroup
	 * @param flowId
	 * @param policyName
	 * @param policyId
	 * @return
	 * @throws Exception
	 */
	public boolean createFlowPolicy(String flowGroup,  String flowId, String policyId, String policyName) throws Exception {
		
		List list = new ArrayList<ServiceflowsFlow.OneBody>();
		ServiceflowsFlowPolicy tenantapi = new ServiceflowsFlowPolicy(restCaller);
		tenantapi.setId(flowGroup);
		ServiceflowsFlowPolicy.OneBody flow = tenantapi.new OneBody();
		flow.setFlowId(flowId);
		flow.setPolicyName(policyName);
		flow.setPolicyId(policyId);
		tenantapi.setBody(flow);
		return tenantapi.create();
	}
	
	/***
	 * 流重定向
	 * @param flowGroup  
	 * @param ingressInterface 北向接口
	 * @param egressInterface 与安全设备连接的接口
	 * @return
	 * @throws Exception
	 */
	public boolean createRedirect(String flowGroup, String ingressInterface, String egressInterface) throws Exception {
		
		List list = new ArrayList<ServiceflowsRedirect.OneBody>();
		ServiceflowsRedirect tenantapi = new ServiceflowsRedirect(restCaller);
		tenantapi.setId(flowGroup);
		ServiceflowsRedirect.OneBody flow = tenantapi.new OneBody();
		flow.setEgressInterface(egressInterface);
		flow.setIngressInterface(ingressInterface);
		tenantapi.setBody(flow);
		return tenantapi.create();
	}
	
	/****
	 * 流策略重定向的vlan
	 * @param flowGroup
	 * @param policyId
	 * @param vlanid
	 * @return
	 * @throws Exception
	 */
	public boolean createFlowpolicysVlan(String flowGroup, String policyId, String vlanid) throws Exception {
		
		List list = new ArrayList<ServiceflowsTunnelEncap.OneBody>();
		ServiceflowsTunnelEncap tenantapi = new ServiceflowsTunnelEncap(restCaller);
		tenantapi.setId(flowGroup);
		tenantapi.setPolicyId(policyId);
		ServiceflowsTunnelEncap.OneBody flow = tenantapi.new OneBody();
		flow.setEncapType("vlan");
		flow.setRemarkValue(vlanid);
		 
		tenantapi.setBody(flow);
		return tenantapi.create();
	}
	
	
	
}
























