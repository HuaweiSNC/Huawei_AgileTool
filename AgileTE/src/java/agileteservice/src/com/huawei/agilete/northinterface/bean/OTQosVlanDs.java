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

public class OTQosVlanDs {

    private String name = "";
    private String vlanId = "";
    private String level = "";
    private String check = "";
    
    private String[] qosLevel = new String[]{"af1","af2", "af3", "af4", "ef"};
    

    public OTQosVlanDs(){

    }


    public OTQosVlanDs(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("qosVlanDs".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
                        setName(domain.elementText("name"));
                    }
                    if(null != domain.elementText("vlanId") && !"".equals(domain.elementText("vlanId"))){
                        setVlanId(domain.elementText("vlanId"));
                    }
                }
            }else{
                return;
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
    }

    public List<OTQosVlanDs> parseOpsToUi(String content){
        List<OTQosVlanDs> list = new ArrayList<OTQosVlanDs>();
///////////////////////////////////////////////////////////////////////////////////////
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele =el.element("qosVlanQoss").element("qosVlanDss");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTQosVlanDs oTQosVlanDs = new OTQosVlanDs();
                Element domain = i.next();
                if(null != domain.elementText("vlanid") && !"".equals(domain.elementText("vlanid"))){
                    oTQosVlanDs.setVlanId(domain.elementText("vlanid"));
                }
                if(null != domain.elementText("dsName") && !"".equals(domain.elementText("dsName"))){
                    oTQosVlanDs.setName(domain.elementText("dsName"));
                }
                list.add(oTQosVlanDs);
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
        mapContext.put("t_oTQosVlanDs", this);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleQosVlanDs.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    public String getUiMessage(List<OTQosVlanDs> list){
        String result = "";
        if(list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_oTQosVlanDs", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleQosVlanDs.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        for(int i=0;i<qosLevel.length;i++){
            if(name.equals(qosLevel[i])){
                setLevel(String.format("%d", i));
                break;
            }
        }
        
    }
    public String getVlanId() {
        return vlanId;
    }
    public void setVlanId(String vlanId) {
        this.vlanId = vlanId;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }


}
