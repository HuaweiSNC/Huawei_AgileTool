package com.huawei.agilete.base.bean;

import java.util.ArrayList;
import java.util.List;

public class Device {
    private String deviceName;
    private String server;
    private int port;
    private String userName;
    private String passwd;
    private int id;
    private String version;
    private String productType;
    private List<String[]> interfaces = new ArrayList<String[]>();
    
    private String[] loopBack = new String[3]; //[3]name ip  subnetMask
    private String[] gigabitEthernet = new String[3];
    
     
    public Device(){
        
    }

    public Device( String deviceName, String server, int port,
            String userName, String passwd, int id, String version,
            String productType) {
        super();
        this.deviceName = deviceName;
        this.server = server;
        this.port = port;
        this.userName = userName;
        this.passwd = passwd;
        this.id = id;
        this.version = version;
        this.productType = productType;
    }
  
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
 
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<String[]> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String[]> interfaces) {
        this.interfaces = interfaces;
    }
    
    public String[] getGigabitEthernet() {
        return gigabitEthernet;
    }

    public void setGigabitEthernet(String[] gigabitEthernet) {
        this.gigabitEthernet = gigabitEthernet;
    }

    public String[] getLoopBack() {
        return loopBack;
    }

    public void setLoopBack(String[] loopBack) {
        this.loopBack = loopBack;
    }
     
}
