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

import com.huawei.networkos.templet.VMTempletManager;

public class OTQos {
    
    private String name = "";
    private String serviceClass = "";
    private String baValue = "";
    
    private String check = "";
    //af1,af2, af3, af4, ef
    public String[] qosLevel = new String[]{"af1","af2", "af3", "af4", "ef"};
    
    public OTQos(){
        
    }
    
    public OTQos(int level){
        if(level>=0 && level<=5){
            baValue = String.format("%d", level);
            serviceClass = qosLevel[level];
            name = qosLevel[level];
        }
        
    }
    
    public List<OTQos> parseOpsToUi(String content){
        List<OTQos> list = new ArrayList<OTQos>();

        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele =el.element("qosDss");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTQos oTQos = new OTQos();
                Element domain = i.next();
                if(null != domain.elementText("dsName") && !"".equals(domain.elementText("dsName"))){
                    oTQos.setName(domain.elementText("dsName"));
                }
                Element tpNexthops = domain.element("qosBas");
                if(null != tpNexthops){
                    for(Iterator<Element> j=tpNexthops.elementIterator();j.hasNext();){
                        Element tpNexthop = j.next();
                        if(null != tpNexthop.elementText("baValue") && !"".equals(tpNexthop.elementText("baValue"))){
                            oTQos.setBaValue(tpNexthop.elementText("baValue"));
                        }
                        if(null != tpNexthop.elementText("serviceClass") && !"".equals(tpNexthop.elementText("serviceClass"))){
                            oTQos.setServiceClass(tpNexthop.elementText("serviceClass"));
                        }
                    }
                }

                list.add(oTQos);
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
        return list;
    }
    

    public String getOpsMessage(int level){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        OTQos qos = new OTQos(level);
        mapContext.put("t_qos", qos);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleQos.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    public String getUiMessage(List<OTQos> list){
        String result = "";
        if(list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_qos", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleQos.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBaValue() {
        return baValue;
    }
    public void setBaValue(String baValue) {
        this.baValue = baValue;
    }
    public String getServiceClass() {
        return serviceClass;
    }
    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }

}
