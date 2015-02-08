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

public class OTFlow {




	private String name = "";
	private String identifyIndex = "";
	private String desIp = "";
	private String policy = "";
	private String isDouleConfig = "";
	private String desDeviceName = "";
	private String interfaceName = "";;

	private String check = "";

	public OTFlow(){

	}


	public OTFlow(String content){
		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			if("flows".equals(el.getName())){
				for(Iterator<Element> i=el.elementIterator();i.hasNext();){
					Element domain = i.next();
					if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
						setName(domain.elementText("name"));
					}
					if(null != domain.elementText("identifyIndex") && !"".equals(domain.elementText("identifyIndex"))){
						setIdentifyIndex(domain.elementText("identifyIndex"));
					}
					if(null != domain.elementText("desIp") && !"".equals(domain.elementText("desIp"))){
						setDesIp(domain.elementText("desIp"));
					}
					if(null != domain.elementText("policy") && !"".equals(domain.elementText("policy"))){
						setPolicy(domain.elementText("policy"));
					}
					if(null != domain.elementText("isDouleConfig") && !"".equals(domain.elementText("isDouleConfig"))){
						setIsDouleConfig(domain.elementText("isDouleConfig"));
					}
					if(null != domain.elementText("desDeviceName") && !"".equals(domain.elementText("desDeviceName"))){
						setDesDeviceName(domain.elementText("desDeviceName"));
					}
					if(null != domain.elementText("interfaceName") && !"".equals(domain.elementText("interfaceName"))){
						setInterfaceName(domain.elementText("interfaceName"));
					}
				}
			}else{
				return;
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
		}
	}

	public List<OTFlow> parseOpsToUi(String content){
		List<OTFlow> list = new ArrayList<OTFlow>();

		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			Element ele =el.element("l2vpnvpws").element("vpwsInstances");
			for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
				OTFlow oTFlow = new OTFlow();
				Element domain = i.next();
				if(null != domain.elementText("instanceName") && !"".equals(domain.elementText("instanceName"))){
					oTFlow.setName(domain.elementText("instanceName"));
				}

				if(null != domain.element("l2vpnAcs")){
					for(Iterator<Element> j=domain.element("l2vpnAcs").elementIterator();j.hasNext();){
						Element l2vpnAc = j.next();
						oTFlow.setInterfaceName(l2vpnAc.elementText("interfaceName"));
					}
				}
				if(null != domain.element("vpwsPws")){
					for(Iterator<Element> j=domain.element("vpwsPws").elementIterator();j.hasNext();){
						Element vpwsPw = j.next();
						oTFlow.setIdentifyIndex(vpwsPw.elementText("pwId"));
						oTFlow.setDesIp(vpwsPw.elementText("peerIp"));
						oTFlow.setPolicy(vpwsPw.elementText("tnlPolicyName"));
					}
				}
				list.add(oTFlow);
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param interfaceIs 是显示instanceName，1显示，0不
	 * @return
	 */
	public String getOpsMessage(String interfaceIs){
		String result = "";
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("t_flow", this);
		mapContext.put("interfaceIs", interfaceIs);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("TempleFlow.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String getUiMessage(List<OTFlow> list){
		String result = "";
		if(list.size() == 0){
			return result;
		}
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("T_Flow", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("UiTempleFlow.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentifyIndex() {
		return identifyIndex;
	}
	public void setIdentifyIndex(String identifyIndex) {
		this.identifyIndex = identifyIndex;
	}
	public String getDesIp() {
		return desIp;
	}
	public void setDesIp(String desIp) {
		this.desIp = desIp;
	}
	public String getPolicy() {
		return policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getIsDouleConfig() {
		return isDouleConfig;
	}
	public void setIsDouleConfig(String isDouleConfig) {
		this.isDouleConfig = isDouleConfig;
	}
	public String getDesDeviceName() {
		return desDeviceName;
	}
	public void setDesDeviceName(String desDeviceName) {
		this.desDeviceName = desDeviceName;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getCheck() {
		return check;
	}
	public void setCheck(String check) {
		this.check = check;
	}




}
