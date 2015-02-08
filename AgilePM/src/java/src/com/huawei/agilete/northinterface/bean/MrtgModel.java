package com.huawei.agilete.northinterface.bean;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.accessibility.internal.resources.accessibility;


public class MrtgModel {
	
	private String id;
	private String Interval = "";
	private String Language = "";
	private String Title = "";
	private String PageTop = "";
	private String Options = "";
	private String MaxBytes = "";
	private String url = "";
	private String xpathObject = "";
	private String xpathTarget = "";
	private String software = "";
	private String XPath = "";

	public MrtgModel(){
		
	}
	/**
	 * 将xml转换成bean
	 * @param xml
	 */
	public MrtgModel(String xml){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			doc.normalize();
			Element et = doc.getDocumentElement();
			NodeList nodes = et.getChildNodes();
			for(int i = 0;i<nodes.getLength();i++) {
				String name = nodes.item(i).getNodeName();
				String data = nodes.item(i).getTextContent().trim();
				if(null!=name&&name.equals("id")){
					setId(data);
				}else if(null!=name&&name.equals("Interval")){
					setInterval(data);
				}else if(null!=name&&name.equals("Language")){
					setLanguage(data);
				}else if(null!=name&&name.equals("Title")){
					setTitle(data);
				}else if(null!=name&&name.equals("PageTop")){
					setPageTop(data);
				}else if(null!=name&&name.equals("Options")){
					setOptions(data);
				}else if(null!=name&&name.equals("MaxBytes")){
					setMaxBytes(data);
				}else if(null!=name&&name.equals("XPath")){
					setXPath(data);
				}else if(null!=name&&name.equals("url")){
					setUrl(data);
				}else if(null!=name&&name.equals("xpathObject")){
					setXpathObject(data);
				}else if(null!=name&&name.equals("xpathTarget")){
					setXpathTarget(data);
				}else if(null!=name&&name.equals("software")){
					setSoftware(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public String getMrtgModelXML(){
		StringBuffer reutrnxml = new StringBuffer("<listenerData>\n");
		reutrnxml.append("	<id>").append(getId()).append("</id>\n")
			.append("	<Interval>").append(getInterval()).append("</Interval>\n")
			.append("	<Language>").append(getLanguage()).append("</Language>\n")
			.append("	<Title><![CDATA[").append(getTitle()).append("]]></Title>\n")
			.append("	<PageTop><![CDATA[").append(getPageTop()).append("]]></PageTop>\n")
			.append("	<Options><![CDATA[").append(getOptions()).append("]]></Options>\n")
			.append("	<MaxBytes>").append(getMaxBytes()).append("</MaxBytes>\n")
			.append("	<url><![CDATA[").append(getUrl()).append("]]></url>\n")
			.append("	<xpathObject><![CDATA[").append(getXpathObject()).append("]]></xpathObject>\n")
			.append("	<xpathTarget><![CDATA[").append(getXpathTarget()).append("]]></xpathTarget>\n")
			.append("	<software><![CDATA[").append(getSoftware()).append("]]></software>\n")
			.append("</listenerData>\n");
		return reutrnxml.toString();
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInterval() {
		if(null==Interval||"null".equals(Interval)){
			this.Interval = "";
		}
		return Interval;
	}
	public void setInterval(String interval) {
		Interval = interval;
	}
	public String getLanguage() {
		if(null==Language||"null".equals(Language)){
			this.Language = "";
		}
		return Language;
	}
	public void setLanguage(String language) {
		Language = language;
	}
	public String getTitle() {
		if(null==Title||"null".equals(Title)){
			this.Title = "";
		}
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getPageTop() {
		if(null==PageTop||"null".equals(PageTop)){
			this.PageTop = "";
		}
		return PageTop;
	}
	public void setPageTop(String pageTop) {
		PageTop = pageTop;
	}
	public String getOptions() {
		if(null==Options||"null".equals(Options)){
			this.Options = "";
		}
		return Options;
	}
	public void setOptions(String options) {
		Options = options;
	}
	public String getMaxBytes() {
		if(null==MaxBytes||"null".equals(MaxBytes)){
			this.MaxBytes = "";
		}
		return MaxBytes;
	}
	public void setMaxBytes(String maxBytes) {
		MaxBytes = maxBytes;
	}
	public String getXPath() {
		if(null==XPath||"null".equals(XPath)){
			this.XPath = "";
		}
		return XPath;
	}
	public void setXPath(String xPath) {
		XPath = xPath;
	}
	public String getUrl() {
		if(null==url||"null".equals(url)){
			this.url = "";
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getXpathObject() {
		if(null==xpathObject||"null".equals(xpathObject)){
			this.xpathObject = "";
		}
		return xpathObject;
	}
	public void setXpathObject(String xpathObject) {
		this.xpathObject = xpathObject;
	}
	public String getXpathTarget() {
		if(null==xpathTarget||"null".equals(xpathTarget)){
			this.xpathTarget = "";
		}
		return xpathTarget;
	}
	public void setXpathTarget(String xpathTarget) {
		this.xpathTarget = xpathTarget;
	}
	public String getSoftware() {
		if(null==software||"null".equals(software)){
			this.software = "";
		}
		return software;
	}
	public void setSoftware(String software) {
		this.software = software;
	}
	
}
