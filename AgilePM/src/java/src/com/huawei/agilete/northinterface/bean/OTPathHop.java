package com.huawei.agilete.northinterface.bean;

public class OTPathHop {
	
	/*<id>1</id>
	<nextIp>10.1.1.1</nextIp>*/
	
	private String id = "";
	private String nextIp = "";
	
	public OTPathHop(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNextIp() {
		return nextIp;
	}
	public void setNextIp(String nextIp) {
		this.nextIp = nextIp;
	}

}
