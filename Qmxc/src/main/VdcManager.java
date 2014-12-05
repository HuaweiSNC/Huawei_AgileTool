package main;

import java.util.ArrayList;
import java.util.List;

import util.OpsRestCaller;
import util.OpsServiceManager;
import util.RetRpc;
import vdc.VdcTenant;

public class VdcManager {

	
	private OpsRestCaller restCaller =OpsServiceManager.getInstance().getDefaultOpsRestCall();
	
	/***
	 * ��ȡ�⻧�б�
	 * @return
	 * @throws Exception
	 */
	public List<VdcTenant.OneBody> getTenants() throws Exception{
		
		List list = new ArrayList<VdcTenant.OneBody>();
		
		VdcTenant tenantapi = new VdcTenant(restCaller);
		RetRpc rpc = tenantapi.getall();
		tenantapi.parseRestful(rpc);
		List<VdcTenant.OneBody> lstTenant = tenantapi.getMultiBody();
		return lstTenant;
	}
	
	/***
	 * �����⻧ID��ȡTopo��Ϣ
	 * @param tenantName
	 * @return
	 * @throws Exception
	 */
	public List<VdcTenant.OneBody> getTopologyByTenantName(String tenantName) throws Exception{
		
		List list = new ArrayList<VdcTenant.OneBody>();
		
		VdcTenant tenantapi = new VdcTenant(restCaller);
		RetRpc rpc = tenantapi.getall();
		tenantapi.parseRestful(rpc);
		List<VdcTenant.OneBody> lstTenant = tenantapi.getMultiBody();
		return lstTenant;
	}
	
	//��ȡָ���⻧��router�б�
	
	//�����⻧ID+ROUTER��ȡinterface�б�
	
	//�����⻧ID��ȡexternal�б�
	
	//�����⻧ID+External���ƻ�ȡvm��Ϣ
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
