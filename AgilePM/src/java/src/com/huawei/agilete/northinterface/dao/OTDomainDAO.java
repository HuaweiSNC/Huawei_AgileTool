package com.huawei.agilete.northinterface.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.bean.OpsServer;
import com.huawei.agilete.base.common.MyIO;
import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.data.MyData;
import com.huawei.networkos.ops.response.RetRpc;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTDomain;

public class OTDomainDAO {
	private static OTDomainDAO single = null;
	private OTDomainDAO(){

	}
	public synchronized  static OTDomainDAO getInstance() {
		if (single == null) {  
			single = new OTDomainDAO();
		}  
		return single;
	}

	public RetRpc add(String content){
		RetRpc result = new RetRpc(403);
		return result;
	}

	public RetRpc del(String domainId){
		RetRpc result = new RetRpc(403);
		return result;
	}

	public RetRpc edit(String content){
		RetRpc result = new RetRpc(403);
		return result;
	}
	public List<String[]> getAllList(){
		DBAction db = new DBAction();
		List<String[]> list = db.getAll("domains","domains");
		return list;
	}
	public RetRpc getAll(){
		RetRpc result = new RetRpc();
		List<String[]> list = getAllList();
		StringBuffer xml = new StringBuffer();
		xml.append("<domains>");
		for(int i=0;i<list.size();i++){
			String domain = list.get(i)[1];
			domain = domain.replace("<domains>", "");
			domain = domain.replace("</domains>", "");
			domain = domain.replace("</domain>", "");
			domain = domain.replace("</describe>", "</describe><topo>");
			
			xml.append(domain);
			OTDomain oTDomain = new OTDomain(list.get(i)[1]);
//			if("".equals(oTDomain.getCheck())){
//				String links = OTLinkDAO.getInstance().control(oTDomain.getId(), "", "", MyData.RestType.GET, "").getContent();
//				//String links = OTLinkDAO.getInstance().get(oTDomain.getId()).getContent();
//				xml.append(links);
//			}
			xml.append("</topo>");
			xml.append("</domain>");
		}
		xml.append("</domains>");
		result.setStatusCode(200);
		result.setContent(xml.toString());
		return result;
	}
	
	
	public RetRpc get(String id){
		RetRpc result = new RetRpc();
		if(StringUtils.isBlank(id)){
			 result.setStatusCode(500);
			 result.setContent("id can not null");
			return  result;
		}
			DBAction db = new DBAction();
			String flag = db.get("domains","domains",id);
			if(null != flag && !"".equals(flag)){
				OTDomain oTDomain = new OTDomain(flag);
				flag = flag.replace("</describe>", "</describe><topo>");
//				if("".equals(oTDomain.getCheck())){
//					String links = OTLinkDAO.getInstance().control(oTDomain.getId(), "", "", MyData.RestType.GET, "").getContent();
//					//String links = OTLinkDAO.getInstance().get(oTDomain.getId()).getContent();
//					flag = flag.replace("</domain>", links+"</topo></domain>");
//				}
				
				result.setStatusCode(200);
				result.setContent(flag);
			}
		
		return result;
	}
	
	public OTDomain getByName(String name){
		RetRpc result = new RetRpc();
		List<String[]> list = getAllList();
		for(int i=0;i<list.size();i++){
			OTDomain oTDomain = new OTDomain(list.get(i)[1]);
			if(name.equals(oTDomain.getName())){
				return oTDomain;
			}
		}
		return null;
	}
	
	
	public OpsServer getOpsServer(String domainId){
		//default
		OpsServer opsServer = new OpsServer();
		ReadConfig r  =new ReadConfig();
		opsServer.setServerIp(r.get().getProperty("OpsServerIp"));
		String port = r.get().getProperty("OpsServerPort");
		try{
			opsServer.setPort(Integer.parseInt(port));
		}catch(Exception e){
		}
		opsServer.setProtocol(r.get().getProperty("OpsProtocol"));
		opsServer.setUserName(r.get().getProperty("OpsUserName"));
		opsServer.setPasswd(r.get().getProperty("OpsPw"));
		return opsServer;
	}
	
	public static void main(String[] args) {
		//OTDomainDAO.getInstance().add("<domains><domain><name>管理域A</name><type>Agile TE View</type><devices><device> <deviceName>1</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices><device><deviceName>2</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices><device><deviceName>3</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices></domain></domains>");
		System.out.println(OTDomainDAO.getInstance().del("1"));
		//System.out.println(OTDomainDAO.getInstance().get(""));
	}

}
