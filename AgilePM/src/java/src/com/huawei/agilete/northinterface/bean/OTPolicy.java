package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.data.MyData;
import com.huawei.networkos.templet.VMTempletManager;

public class OTPolicy {
	
	private String name = "";
	private String ip = "";
	private String tunnelName = "";
	private String check = "";
	
	public OTPolicy(){
		
	}
	
	public OTPolicy(String content){
		String flag = content;
		if(flag.contains("<name>")){
			String name = flag.substring(flag.indexOf("<name>")+"<name>".length(), flag.indexOf("</name>"));
			if(null !=name && !"".equals(name)){
				setName(name);
			}
		}
		if(flag.contains("<nexthopIPaddr>")){
			String ip = flag.substring(flag.indexOf("<nexthopIPaddr>")+"<nexthopIPaddr>".length(), flag.indexOf("</nexthopIPaddr>"));
			if(null !=ip && !"".equals(ip)){
				setIp(ip);
			}
		}
		if(flag.contains("<tunnelName>")){
			String tunnelName = flag.substring(flag.indexOf("<tunnelName>")+"<tunnelName>".length(), flag.indexOf("</tunnelName>"));
			if(null !=tunnelName && !"".equals(tunnelName)){
				setTunnelName(tunnelName);
			}
		}
	}
	
	public List<OTPolicy> parseOpsToUi(String content){
		List<OTPolicy> list = new ArrayList<OTPolicy>();

		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			Element ele =el.element("tunnelPolicys");
			for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
				OTPolicy oTBfd = new OTPolicy();
				Element domain = i.next();
				if(null != domain.elementText("tnlPolicyName") && !"".equals(domain.elementText("tnlPolicyName"))){
					oTBfd.setName(domain.elementText("tnlPolicyName"));
				}
				
				Element tpNexthops = domain.element("tpNexthops");
				if(null != tpNexthops){
					for(Iterator<Element> j=tpNexthops.elementIterator();j.hasNext();){
						Element tpNexthop = j.next();
						if(null != tpNexthop.elementText("nexthopIPaddr") && !"".equals(tpNexthop.elementText("nexthopIPaddr"))){
							oTBfd.setIp(tpNexthop.elementText("nexthopIPaddr"));
						}
						Element tpTunnels = tpNexthop.element("tpTunnels");
						if(null != tpTunnels){
							for(Iterator<Element> k=tpTunnels.elementIterator();k.hasNext();){
								Element tpTunnel = k.next();
								if(null != tpTunnel.elementText("tunnelName") && !"".equals(tpTunnel.elementText("tunnelName"))){
									oTBfd.setTunnelName(tpTunnel.elementText("tunnelName"));
								}
							}
						}
					}
				}

				list.add(oTBfd);
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
		}
		return list;
	}
	

	public String getOpsMessage(){
		String result = "";
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("t_policy", this);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("TemplePolicy.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String getUiMessage(List<OTPolicy> list){
		String result = "";
		if(list.size() == 0){
			return result;
		}
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("T_Policy", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("UiTemplePolicy.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
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
