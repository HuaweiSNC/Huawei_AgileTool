package com.huawei.agilete.northinterface.bean;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class OTMainStandby {
    
    private String tunnelName = "";
    private String status = "";
    private String check = "";
    
    public OTMainStandby(){
        
    }
    
    public OTMainStandby(String content){
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new StringReader(content));//读取String的xml文本并转换
            Element mainStandbyElement = doc.getRootElement();
            String tunnelName = mainStandbyElement.elementText("tunnelName");
            if(!(tunnelName == null || tunnelName.equals(""))){
                setTunnelName(tunnelName);
            }
            String status = mainStandbyElement.elementText("status");
            if(!(status == null || status.equals(""))){
                setStatus(status);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    
    public List<OTMainStandby> parseOpsToUi(String content){
        List<OTMainStandby> list = new ArrayList<OTMainStandby>();
        return list;
    }
    

    public String getOpsMessage(){
        String result = "";
        if(!(getTunnelName()==null||getTunnelName().equals("")||getStatus()==null||getStatus().equals(""))){
            result = "<tunnelName>"+getTunnelName()+"</tunnelName>\n";
            if("primary".equalsIgnoreCase(getStatus())){
                result = result + "<switchMode>hsb2primary</switchMode>";//切换到主运行
            }else if("hot_standby".equalsIgnoreCase(getStatus())){
                result = result + "<switchMode>primary2hsb</switchMode>";//切换到备运行
            }else{
                
            }
        }
        return result;
    }



    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
