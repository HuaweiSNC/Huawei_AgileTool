package com.huawei.agilete.base.bean;

 
public class OpsServer {
    
    private String serverIp;
    private int port;
    private String userName;
    private String passwd;
    private String protocol = "http://";
     
    
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
    	buf.append("http://").append(serverIp).append(":").append(port);
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


