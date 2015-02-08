package com.huawei.agilete.northinterface.bean;


public class OTRestPing {
    
    private String name = "";
    private String ip = "";
    private String tunnelName = "";
    private String check = "";
    
    public OTRestPing(){
        
    }
    
    


    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getTunnelName() {
        return tunnelName;
    }
    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }

}
