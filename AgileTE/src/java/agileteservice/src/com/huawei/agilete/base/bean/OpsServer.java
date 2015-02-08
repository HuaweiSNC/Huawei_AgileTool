package com.huawei.agilete.base.bean;

import com.huawei.networkos.ops.IOpsServer;

 
public class OpsServer implements IOpsServer{
    
    public final static String PROTOCOL_HTTP = "http";
    public final static String PROTOCOL_HTTPS = "https";
    
    private String serverIp;
    private int port;
    private String userName;
    private String passwd;
    private String protocol = PROTOCOL_HTTP;
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getProtocol()
    {
        return protocol;
    }
    
    public String getUrl()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(protocol).append("://").append(serverIp).append(":").append(port);
        return buf.toString();
    } 
    
    public String getServerIp() {
        return serverIp;
    }
 
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
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
      
}


