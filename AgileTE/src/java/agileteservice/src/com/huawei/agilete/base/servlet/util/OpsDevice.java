package com.huawei.agilete.base.servlet.util;

import java.util.HashMap;
import java.util.Map;

import com.huawei.networkos.ops.IOpsDevice;

public class OpsDevice implements IOpsDevice {
    private String name;
    private String deviceName;
    private String server;
    private int port;
    private String userName;
    private String passwd;
    private String id;
    private String version;
    private String productType;
    
    public OpsDevice(){}
    
    public OpsDevice(String name, String deviceName, String server, int port, String userName, String passwd,
            String id, String version, String productType) {
        this.name = name;
        this.deviceName = deviceName;
        this.server = server;
        this.port = port;
        this.userName = userName;
        this.passwd = passwd;
        this.id = id;
        this.version = version;
        this.productType = productType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
     
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }
    public static Map<String, String> strToMap(String str)
    {
        Map<String, String> map = new HashMap<String, String>();
        String str1 = "{\"data-type\":\"STRING\",\"min\":\"0\",\"max\":\"255\",\"example\":\"string\",\"access\":\"readCreate\"}";
        String[] sss = str1.replace("{", "").replace("}", "").split(",");
        for(String s:sss)
        {
            String [] ss = s.split(":");
            map.put(ss[0], ss[1]);
        }
        return map;
    }
    public static void main(String[] args) {
        //System.out.println(OpsDevice.strToMap("123"));
    }
    
    
}
