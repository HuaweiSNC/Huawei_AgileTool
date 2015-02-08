package com.huawei.networkos.ops;

import com.huawei.agilete.base.bean.OpsServer;
import com.huawei.agilete.base.servlet.util.OpsDevice;
import com.huawei.agilete.base.servlet.util.OpsServiceCfgXmlutil;
import com.huawei.networkos.ops.client.OpsRestCaller;

 
/***
 * ops服务管理
 * @author sWX203247
 *
 */
public class OpsServiceManager {
    
    private  static OpsServiceManager serviceManager;
    private OpsServiceCfgXmlutil OpsServiceConfigXmlutil = null  ;
    private OpsDevice  defaultconfig = null;
    public static String servicecfgxmlpath = "src\\com\\huawei\\vlloverte\\base\\servlet\\util\\OpsServiceConfig.xml";
    
    public synchronized static OpsServiceManager getInstance(){
        if(null == serviceManager){
            
            //String path = System.getProperty("user.dir")+File.separator+"util"+File.separator;
           // String servicecfgxmlpath =path+ "OpsServiceConfig.xml";
            return new OpsServiceManager(servicecfgxmlpath); 
        }
        return serviceManager;
    }
    
    private OpsServiceManager(String servicecfgxmlpath) {
        
        OpsServiceCfgXmlutil servicecfgxml = new OpsServiceCfgXmlutil(servicecfgxmlpath);
        this.OpsServiceConfigXmlutil = servicecfgxml;
        servicecfgxml.parseDevicesmap();
    }

    /**
     * 获得默认device
     * @return
     */
    public OpsDevice getDefaultOpsConfig(){
   
        defaultconfig = OpsServiceConfigXmlutil.getDeivceByName("default");
        return defaultconfig;
        }

    /**
     * 获得默认device的客户端
     * @return
     */
    public OpsRestCaller getDefaultOpsRestCall(){
    	
    	OpsDevice opsdevice = getDefaultOpsConfig();
    	OpsServer server = new OpsServer();
    	server.setServerIp(opsdevice.getServer());
    	server.setPasswd(opsdevice.getPasswd());
    	server.setPort(opsdevice.getPort());
    	server.setUserName(opsdevice.getUserName());
        return new OpsRestCaller(server, opsdevice);
   }
    
   
    
    
    /**
     * 根据device获得对于的客户端
     * @return
     *//*
    public OpsRestCaller getOpsRestCallByName(String name){
        return new OpsRestCaller(OpsServiceConfigXmlutil.getDeivceByName(name));
        }
     */

}
