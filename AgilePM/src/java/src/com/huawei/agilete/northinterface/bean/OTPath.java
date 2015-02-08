package com.huawei.agilete.northinterface.bean;

import java.util.ArrayList;
import java.util.List;

public class OTPath {
	
	private String name = "";
	private String type = "";
	private List<OTPathHop> pathHops = new ArrayList<OTPathHop>();
	
	public OTPath(){
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<OTPathHop> getPathHops() {
		return pathHops;
	}
	public void setPathHops(List<OTPathHop> pathHops) {
		this.pathHops = pathHops;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
