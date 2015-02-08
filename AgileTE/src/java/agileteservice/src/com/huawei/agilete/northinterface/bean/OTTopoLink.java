package com.huawei.agilete.northinterface.bean;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class OTTopoLink {
    
/*    <topoLinks>
    <topoLink>
        <name></name>
        <headNodeConnector>
          <Connectorid>1</Connectorid>
          <toponode>
            <nodeID>1</nodeID> 
            <nodeType>1.1.1.1</nodeType>
          </toponode>
        </headNodeConnector>
        <tailNodeConnector>
          <Connectorid>3</Connectorid>
          <toponode>
            <nodeID>2</nodeID> 
            <nodeType>2.2.2.2</nodeType>
          </toponode>
        </tailNodeConnector>
        <cost>1</cost>
        <bandwidth>10</bandwidth>
    </topoLink> 
</topoLinks>*/

    private String name = "";
    private String headNodeConnectorid = "";
    private String headNodenodeID = "";
    private String headNodenodeType = "";
    private String tailNodeConnectorid = "";
    private String tailNodenodeID = "";
    private String tailNodenodeType = "";
    
    private String check = "";
    
    public OTTopoLink(){
        
    }
    public OTTopoLink(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("topoLinks".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
                        setName(domain.elementText("name"));
                    }
                }
            }else{
                if(null != el.elementText("name") && !"".equals(el.elementText("name"))){
                    setName(el.elementText("name"));
                }
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getHeadNodeConnectorid() {
        return headNodeConnectorid;
    }
    public void setHeadNodeConnectorid(String headNodeConnectorid) {
        this.headNodeConnectorid = headNodeConnectorid;
    }
    public String getHeadNodenodeID() {
        return headNodenodeID;
    }
    public void setHeadNodenodeID(String headNodenodeID) {
        this.headNodenodeID = headNodenodeID;
    }
    public String getHeadNodenodeType() {
        return headNodenodeType;
    }
    public void setHeadNodenodeType(String headNodenodeType) {
        this.headNodenodeType = headNodenodeType;
    }
    public String getTailNodeConnectorid() {
        return tailNodeConnectorid;
    }
    public void setTailNodeConnectorid(String tailNodeConnectorid) {
        this.tailNodeConnectorid = tailNodeConnectorid;
    }
    public String getTailNodenodeID() {
        return tailNodenodeID;
    }
    public void setTailNodenodeID(String tailNodenodeID) {
        this.tailNodenodeID = tailNodenodeID;
    }
    public String getTailNodenodeType() {
        return tailNodenodeType;
    }
    public void setTailNodenodeType(String tailNodenodeType) {
        this.tailNodenodeType = tailNodenodeType;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }

    
    

}
