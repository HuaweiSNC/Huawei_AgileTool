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

public class OTNqa {

	private String name = "";
	private String type = "";
	private String tunnelName = "";
	private String tunnelId = "";
	private String peerIp = "";
	private String time = "";
	private String rttAverage = "";
	private String loss = "";
	private String abscissa = "";
	private String check = "";

	public OTNqa(){

	}

	public OTNqa(String content){
		try {
			Document doc = DocumentHelper.parseText(content);
			Element el = doc.getRootElement();
			if("request".equals(el.getName())){
				if(null != el.elementText("testname") && !"".equals(el.elementText("testname"))){
					setName(el.elementText("testname"));
				}
				if(null != el.elementText("tunnelName") && !"".equals(el.elementText("tunnelName"))){
					setTunnelName(el.elementText("tunnelName"));
				}
			}else{
				return;
			}
		}catch (DocumentException e) {
			//e.printStackTrace();
		}
	}

	public List<OTNqa> parseOpsToUi(String type,String content){
		List<OTNqa> list = new ArrayList<OTNqa>();
		if("tunnel".equals(type)){
			try {
				Document doc = DocumentHelper.parseText(content);
				Element el = doc.getRootElement();
				Element ele =el.element("lsp").element("lspPingResults");
				for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
					OTNqa oTNqa = new OTNqa();
					Element domain = i.next();
					if(null != domain.elementText("testName") && !"".equals(domain.elementText("testName"))){
						oTNqa.setName(domain.elementText("testName"));
					}
					if(null != domain.elementText("tunnelName") && !"".equals(domain.elementText("tunnelName"))){
						oTNqa.setTunnelName(domain.elementText("tunnelName"));
					}
					if(null != domain.elementText("rttAverage") && !"".equals(domain.elementText("rttAverage"))){
						oTNqa.setRttAverage(domain.elementText("rttAverage"));
					}
					list.add(oTNqa);
				}
			}catch (DocumentException e) {
				//e.printStackTrace();
			}
		}else if("flow".equals(type)){
			try {
				Document doc = DocumentHelper.parseText(content);
				Element el = doc.getRootElement();
				Element ele =el.element("pwe3").element("pwe3PingResults");
				for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
					OTNqa oTNqa = new OTNqa();
					Element domain = i.next();
					if(null != domain.elementText("testName") && !"".equals(domain.elementText("testName"))){
						oTNqa.setName(domain.elementText("testName"));
					}
					if(null != domain.elementText("lossRatio") && !"".equals(domain.elementText("lossRatio"))){
						oTNqa.setLoss(domain.elementText("lossRatio"));
					}
					if(null != domain.elementText("rttAverage") && !"".equals(domain.elementText("rttAverage"))){
						oTNqa.setRttAverage(domain.elementText("rttAverage"));
					}
					list.add(oTNqa);
				}
			}catch (DocumentException e) {
				//e.printStackTrace();
			}
		}else{

		}
		return list;
	}


	public String getOpsMessage(){
		String result = "";
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("t_nqa", this);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = templet.process("TempleNqa.tpl", mapContext, templetPath);
		result = write.toString();
		return result;
	}

	public String getUiMessage(List<OTNqa> list){
		String result = "";
		if(null == list || list.size() == 0){
			return result;
		}
		VMTempletManager templet = VMTempletManager.getInstance();
		Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
		mapContext.put("T_nqa", list);
		String templetPath = templet.getResource("/templet/agilete")	;
		StringWriter write = new StringWriter();
		try{
			write = templet.process("UiTempleNqa.tpl", mapContext, templetPath);
		}catch (Exception e) {
			e.printStackTrace();
		}
		result = write.toString();
		return result;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getRttAverage() {
		return rttAverage;
	}
	public void setRttAverage(String rttAverage) {
		this.rttAverage = rttAverage;
	}
	public String getAbscissa() {
		return abscissa;
	}
	public void setAbscissa(String abscissa) {
		this.abscissa = abscissa;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTunnelId() {
		return tunnelId;
	}
	public void setTunnelId(String tunnelId) {
		this.tunnelId = tunnelId;
	}
	public String getPeerIp() {
		return peerIp;
	}
	public void setPeerIp(String peerIp) {
		this.peerIp = peerIp;
	}
	public String getLoss() {
		return loss;
	}
	public void setLoss(String loss) {
		this.loss = loss;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
