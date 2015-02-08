package com.huawei.agilete.northinterface.bean;

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class OTHandPolicy {
    
    private String tnlPolicyName = "";
    private String nexthopIPaddr = "";
    private String tunnelName = "";
    
    public OTHandPolicy(){
        
    }
    
    public OTHandPolicy(String content){
        SAXReader reader = new SAXReader();
        try{
            Document doc = reader.read(new StringReader(content));//读取String的xml文本并转换
            Element handPolicyElement = doc.getRootElement();
//            handPolicyElement.get
            String tunnelName = handPolicyElement.elementText("tunnelName");
            if(tunnelName!=null&&(!tunnelName.equals(""))){
                setTunnelName(tunnelName);
            }
            String nexthopIPaddr = handPolicyElement.elementText("nexthopIPaddr");
            if(nexthopIPaddr!=null&&(!nexthopIPaddr.equals(""))){
                setNexthopIPaddr(nexthopIPaddr);
            }
            String tnlPolicyName = handPolicyElement.elementText("tnlPolicyName");
            if(tnlPolicyName!=null&&(!tnlPolicyName.equals(""))){
                setTnlPolicyName(tnlPolicyName);
            }
        }catch(DocumentException e){
            e.printStackTrace();
        }
        
    }

    public String getOpsMessage(){
        String result = "";
        if(!(tunnelName==null||tunnelName.equals("")||nexthopIPaddr==null||nexthopIPaddr.equals("")||tnlPolicyName==null||tnlPolicyName.equals(""))){
            result = result + "<tunnelName>"+getTunnelName()+"</tunnelName>";
        }
        return result;
    }


    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public String getNexthopIPaddr() {
        return nexthopIPaddr;
    }

    public void setNexthopIPaddr(String nexthopIPaddr) {
        this.nexthopIPaddr = nexthopIPaddr;
    }

    public String getTunnelName() {
        return tunnelName;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    

}
