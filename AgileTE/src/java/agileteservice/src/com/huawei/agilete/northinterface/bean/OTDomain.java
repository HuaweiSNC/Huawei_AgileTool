package com.huawei.agilete.northinterface.bean;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class OTDomain {
    
    private String id = "";
    private String name = "";
    private String type = "";
    private String describe = "";
    private String check = "";

    public OTDomain(){
        
    }
    
    public OTDomain(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("domains".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("id") && !"".equals(domain.elementText("id"))){
                        setId(domain.elementText("id"));
                    }
                    if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
                        setName(domain.elementText("name"));
                    }
                    if(null != domain.elementText("type") && !"".equals(domain.elementText("type"))){
                        setType(domain.elementText("type"));
                    }
                    if(content.contains("<describe>")){
                        setDescribe(content.substring(content.indexOf("<describe>")+"<describe>".length(), content.indexOf("</describe>")));
                    }
//                    if(null != domain.elementText("describe") && !"".equals(domain.elementText("describe"))){
//                        setDescribe(domain.elementText("describe"));
//                    }
                }
            }else{
                if(null != el.elementText("id") && !"".equals(el.elementText("id"))){
                    setId(el.elementText("id"));
                }
                if(null != el.elementText("name") && !"".equals(el.elementText("name"))){
                    setName(el.elementText("name"));
                }
                if(null != el.elementText("type") && !"".equals(el.elementText("type"))){
                    setType(el.elementText("type"));
                }
                if(content.contains("<describe>")){
                    setDescribe(content.substring(content.indexOf("<describe>")+"<describe>".length(), content.indexOf("</describe>")));
                }
//                if(null != el.elementText("describe") && !"".equals(el.elementText("describe"))){
//                    setDescribe(el.elementText("describe"));
//                }
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    
    public String getOTDomain(String devices,String links){
        StringBuffer xml = new StringBuffer();
        xml.append("<domain>");
        xml.append("<id>").append(getId()).append("</id>");
        xml.append("<name>").append(getName()).append("</name>");
        xml.append("<type>").append(getType()).append("</type>");
        xml.append("<describe>").append(getDescribe()).append("</describe>");
        xml.append("<topo>");
        if(null != devices && !"".equals(devices)){
            xml.append(devices);
        }
        if(null != links && !"".equals(links)){
            xml.append(links);
        }
        xml.append("</topo>");
        xml.append("</domain>");
        return xml.toString();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    } 
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCheck() {
        return check;
    }
    private void setCheck(String check) {
        this.check = check;
    }
    public String getDescribe() {
        return describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    

}
