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
		
 
		//���� ĳ�⻧��serverflow��
		String flowGroupName ="video";
		String flowGroupid ="1";
		String tenantId ="QQ";
		boolean bret = createServerFlowGroup(tenantId, flowGroupid, flowGroupName);
		
		if(!bret) return false;
		
		//���� ������ 
		String flowName ="MP4";
		String flowid ="1";
		bret = createFlow(flowGroupid, flowName, flowid);
		
		//�����������ʶ��������������
		String srcIpAddress = "10.139.111.2";
		String srcIpAddressMask = "255.255.255.0";
		String destIpAddress = "10.139.111.3";
		String destIpAddressMask = "255.255.255.0";
		int seqNum = 1;
		 bret = createFlowFilter(flowGroupid, flowid, seqNum, srcIpAddress,srcIpAddressMask, destIpAddress,destIpAddressMask);
		
		 //������Ӧ�ò��ԣ���ָ���⻧��VLAN ID ��ʲô��
		 String policyName ="name";
		 String policyId = "1";
		 String vlanid = "100";
		 bret = createFlowPolicy(flowGroupid, flowid,policyId, policyName);
		 bret = createFlowpolicysVlan(flowGroupid, policyId, vlanid);
		  
		  
		 //�󶨲���·����Ч�Ľӿڻ��б�, ���ñ���ӿ� �밲ȫ�豸���ӵĽӿ�
		 List<String> lstNetworkIfName = new ArrayList<String>();
		 lstNetworkIfName.add("tenantnetwork1");
		 String inressinterface = "interface100";
		 String egressinterface = "interface200";
		 bret = createRedirect(flowGroupid, inressinterface, egressinterface);
		  
		 return true;
		 
		
	}
	
	/****
	 * ����������Ϣ����
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
	 * ���� ��
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
	 * �������õķ���
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
	 * �������õ����ӿ�
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
	 * ���ض���
	 * @param flowGroup  
	 * @param ingressInterface ����ӿ�
	 * @param egressInterface �밲ȫ�豸���ӵĽӿ�
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
	 * �������ض����vlan
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
























