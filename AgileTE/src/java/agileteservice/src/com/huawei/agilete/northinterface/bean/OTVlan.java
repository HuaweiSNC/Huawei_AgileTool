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

import com.huawei.agilete.northinterface.dao.OTQosVlanDsDAO;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.templet.VMTempletManager;

public class OTVlan {
    
    
    private String name = "";
    private String desc = "";
    private String index = "";
    private String ifName = "";
    private String level = "";
    private String check = "";
    
    public OTVlan(){
        
    }
    
    public OTVlan(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("vlans".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
                        setName(domain.elementText("name"));
                    }
                    if(null != domain.elementText("desc") && !"".equals(domain.elementText("desc"))){
                        setDesc(domain.elementText("desc"));
                    }
                    if(null != domain.elementText("index") && !"".equals(domain.elementText("index"))){
                        setIndex(domain.elementText("index"));
                    }
                    if(null != domain.elementText("ifName")){ ///////////////需要改 if(null != domain.elementText("ifName")){
                        setIfName("Vlanif"+domain.elementText("index"));
                    }
                    if(null != domain.elementText("level") && !"".equals(domain.elementText("level"))){
                        setLevel(domain.elementText("level"));
                    }
                    
                }
            }else{
                return;
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    
    public String getOTVlanOpsMessage(){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("t_vlan", this);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleVlan.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }
    
    public List<OTVlan> parseOpsToUi(String content){
        List<OTVlan> list = new ArrayList<OTVlan>();
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele = el.element("vlans");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTVlan oTVlan = new OTVlan();
                Element domain = i.next();
                String vlandId = domain.elementText("vlanId");
                if(null != vlandId && !"".equals(vlandId)){
                    oTVlan.setIndex(vlandId);
                }
                if(null != domain.elementText("vlanName") && !"".equals(domain.elementText("vlanName"))){
                    oTVlan.setName(domain.elementText("vlanName"));
                }
                if(null != domain.elementText("vlanDesc") && !"".equals(domain.elementText("vlanDesc"))){
                    oTVlan.setDesc(domain.elementText("vlanDesc"));
                }
                if(null != domain.element("vlanif")){
                    if(null != domain.element("vlanif").elementText("ifName") && !"".equals(domain.element("vlanif").elementText("ifName"))){
                        oTVlan.setIfName(domain.element("vlanif").elementText("ifName"));
                    }
                }
                list.add(oTVlan);
            }
        } catch (DocumentException e) {
        }
        return list;
    }
    public List<OTVlan> parseOpsToUiGetQos(String content,OpsRestCaller client){
        List<OTVlan> list = new ArrayList<OTVlan>();
        OTQosVlanDsDAO oTQosVlanDsDAO = OTQosVlanDsDAO.getInstance();
        oTQosVlanDsDAO.setClient(client);
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele = el.element("vlans");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTVlan oTVlan = new OTVlan();
                Element domain = i.next();
                String vlandId = domain.elementText("vlanId");
                if(null != vlandId && !"".equals(vlandId)){
                    oTVlan.setIndex(vlandId);
                }
                if(null != domain.elementText("vlanName") && !"".equals(domain.elementText("vlanName"))){
                    oTVlan.setName(domain.elementText("vlanName"));
                }
                if(null != domain.elementText("vlanDesc") && !"".equals(domain.elementText("vlanDesc"))){
                    oTVlan.setDesc(domain.elementText("vlanDesc"));
                }
                if(null != domain.element("vlanif")){
                    if(null != domain.element("vlanif").elementText("ifName") && !"".equals(domain.element("vlanif").elementText("ifName"))){
                        oTVlan.setIfName(domain.element("vlanif").elementText("ifName"));
                    }
                }
                
//                List<OTQosVlanDs> oTQosVlanDss = oTQosVlanDsDAO.getList(vlandId);
//                if(oTQosVlanDss!=null&&oTQosVlanDss.size()==1){
//                    oTVlan.setLevel(oTQosVlanDss.get(0).getName());
//                }else{
                    /*
                     * 测试数据  
                     */
//                    Double sum = Math.random();
//                    Integer ii = (int)(sum*100);
//                    if(ii<20){
//                        oTVlan.setLevel("af1");
//                    }else if(ii<40){
//                        oTVlan.setLevel("af2");
//                    }else if(ii<60){
//                        oTVlan.setLevel("af3");
//                    }else{
//                        oTVlan.setLevel("");
//                    }
                    
//                }
                
                list.add(oTVlan);
            }
        } catch (DocumentException e) {
        }
        if(list.size()==1){//当查询的为一个时，进行单独查询，防止出现浪费内存
            List<OTQosVlanDs> oTQosVlanDss = oTQosVlanDsDAO.getList(list.get(0).getIndex());
            if(oTQosVlanDss!=null&&oTQosVlanDss.size()==1){
                list.get(0).setLevel(oTQosVlanDss.get(0).getName());
            }
        }else{//查询多个的时候，进行匹配等级信息
            List<OTQosVlanDs> oTQosVlanDss = oTQosVlanDsDAO.getList("");
            if(oTQosVlanDss==null || list==null){
                return list;
            }
            for(OTVlan otvlan:list){
                for(OTQosVlanDs otqos:oTQosVlanDss){
                    if(otvlan.getIndex().equals(otqos.getVlanId())){
                        otvlan.setLevel(otqos.getName());
                        oTQosVlanDss.remove(otqos);
                        break;
                    }
                }
            }
        }
        return list;
    }
    
    public String getUiMessage(List<OTVlan> list){
        String result = "";
        if(null == list || list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_Vlan", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleVlan.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }
    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }
    public String getIfName() {
        return ifName;
    }
    public void setIfName(String ifName) {
        this.ifName = ifName;
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
