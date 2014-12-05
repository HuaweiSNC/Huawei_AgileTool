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
	 * 获取租户列表
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
	 * 根据租户ID获取Topo信息
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
	
	//获取指定租户的router列表
	
	//根据租户ID+ROUTER获取interface列表
	
	//根据租户ID获取external列表
	
	//根据租户ID+External名称获取vm信息
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
