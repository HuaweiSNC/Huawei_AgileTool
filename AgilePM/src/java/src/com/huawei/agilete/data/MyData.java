package com.huawei.agilete.data;

import java.util.HashMap;
import java.util.List;

import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.dao.OTTfDAO;
import com.huawei.networkos.ops.builders.BuildController;

public class MyData {
	public static final String XmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	public static BuildController TaskMainControl;
	public static BuildController TaskChildControl;
	
	private static String CassandraPath;
	private static String CassandraServer;
	private static String CassandraPort;
	private static String CassandraKeyspace;
	
	private static String OpsUrl;
	
	private static String AlarmServerIp;
	private static String AlarmServerPort;
	
	//ops模板
	private static String TempleTunnel;
	private static String TempleTunnelPath;
	private static String TempleVlan;
	private static String TempleFlow;	
	private static String TempleBfd;	
	private static String TemplePolicy;	
	
	//UI模板
	private static String UiTempleTunnel;
	private static String UiTempleFlow;
	private static String UiTempleVlanMapping;
	private static String UiTempleBfd;
	private static String UiTemplePolicy;	
	private static String UiTempleVlan;	
	
	public static HashMap<String, String> DomainsData;
	public static HashMap<String, OTDevice> DevicesData = new HashMap<String, OTDevice>(); //include ifm
	public static String explicitPathData;
	public static String ersvpTeTunnelData;
	public static String vpwsInstanceData;
	public static String vlanData;
	
	public static int ServerSocketPort;
	
	public static String CurDomains; //当前管理域
	
	public static StringBuffer longging;
	
	public static HashMap<String, Boolean> InitState = new HashMap<String, Boolean>();
	
	public static String SimulantData;
	
	public static enum RestType{
    	GET,
    	POST,
    	PUT,
    	DELETE,
    	GETOTHER
    } 
	
	//缓存数据
	//tunnelTree
	public static HashMap<String,String> tunnelTree = new HashMap<String, String>();
	
	public static HashMap<String,Integer> AllDevices = new HashMap<String, Integer>();
	
	private static List<String[]> stat;
	
	
	public MyData(){
		
		ReadConfig r  =new ReadConfig();
		CassandraPath = r.get().getProperty("CassandraPath");
		CassandraServer = r.get().getProperty("CassandraServer");
		CassandraPort = r.get().getProperty("CassandraPort");
		CassandraKeyspace = r.get().getProperty("CassandraKeyspace");
		OpsUrl = r.get().getProperty("OpsUrl");
		
		AlarmServerIp = r.get().getProperty("AlarmServerIp");
		AlarmServerPort = r.get().getProperty("AlarmServerPort");
		
		SimulantData = r.get().getProperty("SimulantData");
		
		
	}

	
	
	public static String getCassandraPath() {
		if(null == CassandraPath){
			ReadConfig r  =new ReadConfig();
			CassandraPath = r.get().getProperty("CassandraPath");
		}
		return CassandraPath;
	}
	public static void setCassandraPath(String cassandraPath) {
		CassandraPath = cassandraPath;
	}
	public static String getCassandraServer() {
		if(null == CassandraServer){
			ReadConfig r  =new ReadConfig();
			CassandraServer = r.get().getProperty("CassandraServer");
		}
		return CassandraServer;
	}
	public static void setCassandraServer(String cassandraServer) {
		CassandraServer = cassandraServer;
	}
	public static String getCassandraPort() {
		if(null == CassandraPort){
			ReadConfig r  =new ReadConfig();
			CassandraPort = r.get().getProperty("CassandraPort");
		}
		return CassandraPort;
	}
	public static void setCassandraPort(String cassandraPort) {
		CassandraPort = cassandraPort;
	}
	public static String getCassandraKeyspace() {
		if(null == CassandraKeyspace){
			ReadConfig r  =new ReadConfig();
			CassandraKeyspace = r.get().getProperty("CassandraKeyspace");
		}
		return CassandraKeyspace;
	}
	public static void setCassandraKeyspace(String cassandraKeyspace) {
		CassandraKeyspace = cassandraKeyspace;
	}
	public static String getOpsUrl() {
		if(null == OpsUrl){
			ReadConfig r  =new ReadConfig();
			OpsUrl = r.get().getProperty("OpsUrl");
		}
		return OpsUrl;
	}
	public static void setOpsUrl(String opsUrl) {
		OpsUrl = opsUrl;
	}
	public static String getTempleTunnel() {
		if(null == TempleTunnel){
			//TempleTunnel = "<tunnelName>$tunnelName</tunnelName><mplsTunnelIndex>$mplsTunnelIndex</mplsTunnelIndex><interfaceName>$interfaceName</interfaceName><mplsTunnelIngressLSRId>$mplsTunnelIngressLSRId</mplsTunnelIngressLSRId><mplsTunnelEgressLSRId>$mplsTunnelEgressLSRId</mplsTunnelEgressLSRId> <tunnelState>$tunnelState</tunnelState><hotStandbyWtr>$hotStandbyWtr</hotStandbyWtr> <hotStandbyEnable>$hotStandbyEnable</hotStandbyEnable><mplsTunnelRecordRoute>RECORD_LABLE</mplsTunnelRecordRoute><foreach_tunnelPathsList><tunnelPaths><foreach_tunnelPath><tunnelPath><pathType>$pathType</pathType><includeAll>0x0</includeAll><includeAny>0x0</includeAny><excludeAny>0x0</excludeAny><hopLimit>32</hopLimit><lspId>0</lspId><modifyLspId>0</modifyLspId><explicitPathName>$explicitPathName</explicitPathName></tunnelPath></foreach_tunnelPath></tunnelPaths></foreach_tunnelPathsList>";
			ReadConfig r  =new ReadConfig();
			TempleTunnel = r.getTemple("TempleTunnel.tpl");
		}
		return TempleTunnel;
	}
	public static void setTempleTunnel(String templeTunnel) {
		TempleTunnel = templeTunnel;
	}
	public static String getTempleTunnelPath() {
		if(null == TempleTunnelPath){
			//TempleTunnelPath = "<explicitPathName>$explicitPathName</explicitPathName><foreach_explicitPathHopsList><explicitPathHops><foreach_explicitPathHop><explicitPathHop><mplsTunnelHopIndex>$mplsTunnelHopIndex</mplsTunnelHopIndex><mplsTunnelHopType>includeStrict</mplsTunnelHopType><mplsTunnelHopIntType>default</mplsTunnelHopIntType><mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType><mplsTunnelHopIpAddr>$mplsTunnelHopIpAddr</mplsTunnelHopIpAddr></explicitPathHop></foreach_explicitPathHop></explicitPathHops><foreach_explicitPathHopsList>";
			ReadConfig r  =new ReadConfig();
			TempleTunnelPath = r.getTemple("TempleExplicitPath.tpl");
		}
		return TempleTunnelPath;
	}
	public static void setTempleTunnelPath(String templeTunnelPath) {
		TempleTunnelPath = templeTunnelPath;
	}
	public static String getUiTempleTunnel() {
		if(null == UiTempleTunnel){
			ReadConfig r  =new ReadConfig();
			UiTempleTunnel = r.getTemple("UiTempleTunnel.tpl");
		}
		return UiTempleTunnel;
	}
	public static void setUiTempleTunnel(String uiTempleTunnel) {
		UiTempleTunnel = uiTempleTunnel;
	}
	public static String getTempleFlow() {
		if(null == TempleFlow){
			ReadConfig r  =new ReadConfig();
			TempleFlow = r.getTemple("TempleFlow.tpl");
		}
		return TempleFlow;
	}
	public static void setTempleFlow(String templeFlow) {
		TempleFlow = templeFlow;
	}
	public static String getUiTempleFlow() {
		if(null == UiTempleFlow){
			ReadConfig r  =new ReadConfig();
			UiTempleFlow = r.getTemple("UiTempleFlow.tpl");
		}
		return UiTempleFlow;
	}
	public static void setUiTempleFlow(String uiTempleFlow) {
		UiTempleFlow = uiTempleFlow;
	}
	public static void setTempleVlan(String templeVlan) {
		TempleVlan = templeVlan;
	}
	public static String getTempleVlan() {
		if(null == TempleVlan){
			ReadConfig r  =new ReadConfig();
			TempleVlan = r.getTemple("TempleVlan.tpl");
		}
		return TempleVlan;
	}
	public static String getUiTempleVlanMapping() {
		if(null == UiTempleVlanMapping){
			ReadConfig r  =new ReadConfig();
			UiTempleVlanMapping = r.getTemple("UiTempleVlanMapping.tpl");
		}
		return UiTempleVlanMapping;
	}
	public static void setUiTempleVlanMapping(String uiTempleVlanMapping) {
		UiTempleVlanMapping = uiTempleVlanMapping;
	}
	public static String getTempleBfd() {
		if(null == TempleBfd){
			ReadConfig r  =new ReadConfig();
			TempleBfd = r.getTemple("TempleBfd.tpl");
		}
		return TempleBfd;
	}
	public static void setTempleBfd(String templeBfd) {
		TempleBfd = templeBfd;
	}
	public static String getUiTempleBfd() {
		if(null == UiTempleBfd){
			ReadConfig r  =new ReadConfig();
			UiTempleBfd = r.getTemple("UiTempleBfd.tpl");
		}
		return UiTempleBfd;
	}
	public static void setUiTempleBfd(String uiTempleBfd) {
		UiTempleBfd = uiTempleBfd;
	}
	public static String getTemplePolicy() {
		if(null == TemplePolicy){
			ReadConfig r  =new ReadConfig();
			TemplePolicy = r.getTemple("TemplePolicy.tpl");
		}
		return TemplePolicy;
	}
	public static void setTemplePolicy(String templePolicy) {
		TemplePolicy = templePolicy;
	}
	public static String getUiTemplePolicy() {
		if(null == UiTemplePolicy){
			ReadConfig r  =new ReadConfig();
			UiTemplePolicy = r.getTemple("UiTemplePolicy.tpl");
		}
		return UiTemplePolicy;
	}
	public static void setUiTemplePolicy(String uiTemplePolicy) {
		UiTemplePolicy = uiTemplePolicy;
	}
	public static String getUiTempleVlan() {
		if(null == UiTempleVlan){
			ReadConfig r  =new ReadConfig();
			UiTempleVlan = r.getTemple("UiTempleVlan.tpl");
		}
		return UiTempleVlan;
	}
	public static void setUiTempleVlan(String uiTempleVlan) {
		UiTempleVlan = uiTempleVlan;
	}
	public static HashMap<String,String> getTunnelTree() {
		return tunnelTree;
	}
	public static void setTunnelTree(HashMap<String,String> tunnelTree) {
		MyData.tunnelTree = tunnelTree;
	}
	public static int getServerSocketPort() {
		if(0 == ServerSocketPort){
			ReadConfig r  =new ReadConfig();
			try{
				ServerSocketPort = Integer.parseInt(r.get().getProperty("ServerSocketPort"));
			}catch(Exception e){
				ServerSocketPort = 9003;
			}
		}
		return ServerSocketPort;
	}
	public static void setServerSocketPort(int serverSocketPort) {
		ServerSocketPort = serverSocketPort;
	}
	public static String getAlarmServerIp() {
		if(null == AlarmServerIp){
			ReadConfig r  =new ReadConfig();
			AlarmServerIp = r.get().getProperty("AlarmServerIp");
		}
		return AlarmServerIp;
	}
	public static void setAlarmServerIp(String alarmServerIp) {
		AlarmServerIp = alarmServerIp;
	}
	public static String getAlarmServerPort() {
		if(null == AlarmServerPort){
			ReadConfig r  =new ReadConfig();
			AlarmServerPort = r.get().getProperty("AlarmServerPort");
		}
		return AlarmServerPort;
	}
	public static void setAlarmServerPort(String alarmServerPort) {
		AlarmServerPort = alarmServerPort;
	}
	public static String getSimulantData() {
		if(null == SimulantData){
			ReadConfig r  =new ReadConfig();
			SimulantData = r.get().getProperty("SimulantData");
		}
		return SimulantData;
	}
	public static void setSimulantData(String simulantData) {
		SimulantData = simulantData;
	}
	public static List<String[]> getStat() {
		if(null == stat){
			MyData.setStat(OTTfDAO.getInstance().getStat());
		}
		return stat;
	}
	public static void setStat(List<String[]> stat) {
		MyData.stat = stat;
	}
	
	
	
}
