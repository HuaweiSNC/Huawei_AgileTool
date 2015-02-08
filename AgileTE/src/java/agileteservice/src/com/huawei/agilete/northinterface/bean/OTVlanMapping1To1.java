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

public class OTVlanMapping1To1 {
    
    private String interfaceName = "";
    private List<MappingPort> mappingPortList = new ArrayList<MappingPort>();
    private String isDouleConfig = "";
    private String desDeviceName = "";
    private String check = "";
    
    public  OTVlanMapping1To1(){
        
    }
    
    public  OTVlanMapping1To1(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("vlanmappings".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("interfaceName") && !"".equals(domain.elementText("interfaceName"))){
                        setInterfaceName(domain.elementText("interfaceName"));
                    }
                    
                    ///////////////////////////////////////////////////////
                    if(null != domain.element("mappingPorts")){
                        for(Iterator<Element> j=domain.element("mappingPorts").elementIterator();j.hasNext();){
                            Element mappingPort =  j.next();
                            MappingPort mapping = new MappingPort();
                            if(null != mappingPort.elementText("mappingVid") && !"".equals(mappingPort.elementText("mappingVid"))){
                                mapping.setMappingVid(Integer.parseInt(mappingPort.elementText("mappingVid")));
                            }
                            if(null != mappingPort.elementText("mappingVidOld") && !"".equals(mappingPort.elementText("mappingVidOld"))){
                                mapping.setMappingVidOld(Integer.parseInt(mappingPort.elementText("mappingVidOld")));
                            }
                            if(null != mappingPort.element("internalVlansOld")){
                                if(null != mappingPort.element("internalVlansOld").elementText("vlansIndex") && !"".equals(mappingPort.element("internalVlansOld").elementText("vlansIndex"))){
                                    String flag =mappingPort.element("internalVlansOld").elementText("vlansIndex");
                                    mapping.setInternalVlansOld(flag);
//                            for(int j=0;j<flag.split(",").length;j++){
//                                internalVlansOld[j] = Integer.parseInt(flag.split(",")[j]);
//                            }
                                }
                            }
                            if(null != mappingPort.element("internalVlansNew")){
                                if(null != mappingPort.element("internalVlansNew").elementText("vlansIndex") && !"".equals(mappingPort.element("internalVlansNew").elementText("vlansIndex"))){
                                    String flag =mappingPort.element("internalVlansNew").elementText("vlansIndex");
                                    mapping.setInternalVlansNew(flag);
//                            for(int j=0;j<flag.split(",").length;j++){
//                                internalVlansNew[j] = Integer.parseInt(flag.split(",")[j]);
//                            }
                                }
                            }
                            this.mappingPortList.add(mapping);
                        }
                    }
                    
                }
            }else{
                return;
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    /**转换成提交ops格式
     * @return
     */
    public String getOTPathOpsMessage(){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("t_vlanMapping1To1", this);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleVlanMapping1To1.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }
    public List<OTVlanMapping1To1> parseOpsToUi(String content,Boolean allEthernet){
        List<OTVlanMapping1To1> list = new ArrayList<OTVlanMapping1To1>();
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement().element("ethernetIfs");
            for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                OTVlanMapping1To1 oTVlanMapping = new OTVlanMapping1To1();
                Element domain = i.next();
                if(null != domain.elementText("ifName") && !"".equals(domain.elementText("ifName"))){
                    oTVlanMapping.setInterfaceName(domain.elementText("ifName"));
                }
                if(null != domain.element("l2Attribute") && null != domain.element("l2Attribute").element("l2MapPortExts")){
//                    for(Iterator<Element> j=domain.element("l2Attribute").element("l2MapPortExts").elementIterator();j.hasNext();){
//                        Element l2MappingPort = j.next();
//                        String transVlans = l2MappingPort.elementText("transVlans");
//                        oTVlanMapping.setInternalVlansNew(getTransVlans(transVlans));
//                        if(null != l2MappingPort.elementText("mappingVid") && !"".equals(l2MappingPort.elementText("mappingVid"))){
//                            oTVlanMapping.setMappingVid(Integer.valueOf( l2MappingPort.elementText("mappingVid")));
//                        }
//                    }
                    Element e =  domain.element("l2Attribute").element("l2MapPortExts");
                    for(Iterator<Element> j=e.elementIterator();j.hasNext();){
                        Element l2MapPortExt = j.next();
                        MappingPort mapping = new MappingPort();
                        String mapVid = l2MapPortExt.elementText("mapVid");
                        if(mapVid!=null && mapVid!=""){
                            mapping.setMappingVid(Integer.parseInt(mapVid));
                        }
                        String mappingVid = l2MapPortExt.elementText("mappingVid");
                        mapping.setInternalVlansNew(mappingVid);
                        oTVlanMapping.getMappingPortList().add(mapping);
                    }
                    
                    list.add(oTVlanMapping);
                }
                if(allEthernet){
                    list.add(oTVlanMapping);
                }
                
            }
        } catch (DocumentException e) {
        }
        return list;
    }
    
    
    
    public String getUiMessage(List<OTVlanMapping1To1> list){
        String result = "";
        if(list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_VlanMapping", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleVlanMapping.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }
    
    private String getTransVlans(String vlans){
        String result = "";
        StringBuffer buf = new StringBuffer();
        for(int i=0;i<vlans.length();i++){
            String flag = Integer.toBinaryString(Integer.valueOf(String.valueOf(vlans.charAt(i)),16));
            while (flag.length() < 4) {
                flag = "0" + flag;
            }
            for(int j=0;j<4;j++){
                if('1' == flag.charAt(j)){
                    buf.append(i*4+j);
                    buf.append(",");
                }
            }
        }
        result = buf.substring(0, buf.length()-1);
        return result;
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
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
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public List<MappingPort> getMappingPortList() {
        return mappingPortList;
    }
    public void setMappingPortList(List<MappingPort> mappingPortList) {
        this.mappingPortList = mappingPortList;
    }

    public static void main(String[] args) {
        OTVlanMapping1To1 o = new OTVlanMapping1To1();
        o.getTransVlans("");
    }
    
}
