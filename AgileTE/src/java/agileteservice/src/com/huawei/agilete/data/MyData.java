package com.huawei.agilete.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.huawei.agilete.base.bean.OpsServer;
import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.base.servlet.util.RestUtil;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.dao.OTTfDAO;
import com.huawei.networkos.ops.builders.BuildController;

public class MyData {
    
    //域的画线信息   xc
    public static String links;
    
    public static final String XmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    
    public static BuildController TaskMainControl;
    public static BuildController TaskChildControl;
    
    private static String CassandraPath;
    private static String CassandraServer;
    private static String CassandraPort;
    private static String CassandraKeyspace;
    private static String CassandraUserName;
	private static String CassandraPassWd;
    
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
    
    public static int serverSocketPort;
    
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
    
    //客户端对应的验证码,用来存储每个客户端对应的验证码
    private static HashMap<String,String> clientVerifyCode;
    private static HashMap<String,String> clientLoginInfo;
    public synchronized static HashMap<String,String> getClientVerifyCode() {
        if (clientVerifyCode == null) {
            clientVerifyCode = new HashMap<String,String>();
        }
        return clientVerifyCode;
    }
    

    public synchronized static HashMap<String,String> getClientLoginInfo() {
        if (clientLoginInfo == null) {
            clientLoginInfo = new HashMap<String,String>();
        }
        return clientLoginInfo;
    }
    
    public MyData(){
        
        ReadConfig r  =new ReadConfig();
        CassandraPath = r.get().getProperty("CassandraPath");
        CassandraServer = r.get().getProperty("CassandraServer");
        CassandraPort = r.get().getProperty("CassandraPort");
        CassandraKeyspace = r.get().getProperty("CassandraKeyspace");
        CassandraUserName = r.get().getProperty("CassandraName");
		CassandraPassWd = r.get().getProperty("CassandraPw");
        OpsUrl = r.get().getProperty("OpsUrl");
        
        AlarmServerIp = r.get().getProperty("AlarmServerIp");
        AlarmServerPort = r.get().getProperty("AlarmServerPort");
        
        SimulantData = r.get().getProperty("SimulantData");
        
        
    }

    
    public static String getCassandraUserName() {
		if(null == CassandraUserName){
			ReadConfig r  =new ReadConfig();
			CassandraUserName = r.get().getProperty("CassandraName");
		}
		return CassandraUserName;
	}
	
	public static String getCassandraPassWd() {
		if(null == CassandraPassWd){
			ReadConfig r  =new ReadConfig();
			CassandraPassWd = r.get().getProperty("CassandraPw");
		}
		return CassandraPassWd;
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
        if(0 == serverSocketPort){
            ReadConfig r  =new ReadConfig();
            try{
                serverSocketPort = Integer.parseInt(r.get().getProperty("ServerSocketPort"));
            }catch(Exception e){
                serverSocketPort = 9003;
            }
        }
        return serverSocketPort;
    }
    public static void setServerSocketPort(int serverSocketPort) {
        serverSocketPort = serverSocketPort;
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
    

    public static String[] getIps() {
        String[] ips = null;
        ReadConfig r  =new ReadConfig();
        String statPath = r.getPath().append("conf\\ips.ini").toString();
        File file = new File(statPath);

        String content = "";
        try {
            content = RestUtil.readFile2(file);
        } catch (Exception e) {
            return null;
        }
        if(!"".equals(content)&&null!=content){
            ips = content.split("\n");
        }
        return ips;
    }


    public static List<OpsServer> getOpsServers() {
            List<OpsServer> OpsServers = new ArrayList<OpsServer>();
            ReadConfig r  =new ReadConfig();
            Properties p = r.get();
            if(null!=r&&null!=p){
            String[] ips = p.getProperty("OpsServerIp").split(",");
            String[] ports = p.getProperty("OpsServerPort").split(",");
            String[] protocols= p.getProperty("OpsProtocol").split(",");
            String[] userNames= p.getProperty("OpsUserName").split(",");
            String[] pws = p.getProperty("OpsPw").split(",");
            for(int i = 0;i<ips.length;i++){
                OpsServer opsServer = new OpsServer();
                if(null!=ips[i]&&!"".equals(ips[i])){
                    opsServer.setServerIp(ips[i]);    
                }
                if(null!=ports[i]&&!"".equals(ports[i])){
                    opsServer.setPort(Integer.parseInt(ports[i]));    
                }
                if(null!=protocols[i]&&!"".equals(protocols[i])){
                    opsServer.setProtocol(protocols[i]);    
                }
                if(null!=userNames[i]&&!"".equals(userNames[i])){
                    opsServer.setUserName(userNames[i]);    
                }
                if(null!=ips[i]&&!"".equals(ips[i])){
                    opsServer.setPasswd(pws[i]);    
                }
                OpsServers.add(opsServer);
            }
            
        }
        return OpsServers;
    }

    
}
