package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.northinterface.dao.OTIfmDAO;
import com.huawei.networkos.templet.VMTempletManager;

public class OTLink {
    

    private String name = "";
    private String headNodeId = "";
    private String headNodeIp = "";
    private String headNodeConnectorid = "";
    private String headNodeConnectorip = "";
    private String tailNodeId = "";
    private String tailNodeIp = "";
    private String tailNodeConnectorid = "";
    private String tailNodeConnectorip = "";
    
    private String check = "";
    
    public OTLink(){
    } 

    public OTLink(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele = el.element("ospfv2comm").element("ospfSites");
            if("ospfSite".equals(ele.getName())){
                for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                    Element ospfSite = i.next();
                    for(Iterator<Element> j=ospfSite.elementIterator();j.hasNext();){
                        Element ospfMplsTeStatistic = j.next();
                        Element linkTlvDatas = ospfMplsTeStatistic.element("linkTlvDatas");
                        for(Iterator<Element> k=linkTlvDatas.elementIterator();k.hasNext();){
                            Element linkTlvData = k.next();
                            //System.out.println(linkTlvData.elementText("linkId"));
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }

    public List<OTLink> getOtlinkList(List<OTDevice> deviceList,OTDevice oTDevice,List<OTLink> list,String content,HashMap<String, OTDevice> devicesData){
        List<OTLink> result = null;
        if(null != list){
            result = list;
        }else{
            result = new ArrayList<OTLink>();
        }
        
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele = el.element("ospfv2comm").element("ospfSites");
            if("ospfSites".equals(ele.getName())){
                for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                    Element ospfSite = i.next();
                    Element ospfMplsTeStatistics = ospfSite.element("ospfMplsTeStatistics");
                    if(null==ospfMplsTeStatistics){
                    	continue;
                    }
                    for(Iterator<Element> j=ospfMplsTeStatistics.elementIterator();j.hasNext();){
                        Element ospfMplsTeStatistic = j.next();
                        Element linkTlvDatas = ospfMplsTeStatistic.element("linkTlvDatas");
                        if(null==linkTlvDatas){
                        	continue;
                        }
                        for(Iterator<Element> k=linkTlvDatas.elementIterator();k.hasNext();){
                            Element linkTlvData = k.next();
                            OTLink oTLink = new OTLink();
                            if(null != linkTlvData.elementText("remoteIp")){
                                if("0.0.0.0".equals(linkTlvData.elementText("remoteIp"))){
                                    if(null != linkTlvData.elementText("linkId") && !"".equals(linkTlvData.elementText("linkId"))){
                                        oTLink.setTailNodeConnectorip(linkTlvData.elementText("linkId"));
                                    }
                                }else{
                                    oTLink.setTailNodeConnectorip(linkTlvData.elementText("remoteIp"));
                                }
                            }
//                            if(null != linkTlvData.elementText("remoteIp") && !"".equals(linkTlvData.elementText("remoteIp"))){
//                                oTLink.setTailNodeConnectorip(linkTlvData.elementText("remoteIp"));
//                            }
                            Element localIps = linkTlvData.element("localIps");
                            if(null != localIps){
                                for(Iterator<Element> z=localIps.elementIterator();z.hasNext();){
                                    Element localIp = z.next();
                                    if(null != localIp.elementText("localIp") && !"".equals(localIp.elementText("localIp"))){
                                        oTLink.setHeadNodeConnectorip(localIp.elementText("localIp"));
                                    }
                                }
                            }
//                            oTLink.setHeadNodeId(oTDevice.getId());
//                            oTLink.setHeadNodeIp(oTDevice.getDeviceTopoIp());
                            if("".equals(oTLink.getHeadNodeConnectorip()) || "0.0.0.0".equals(oTLink.getHeadNodeConnectorip()) || "".equals(oTLink.getTailNodeConnectorip()) || "0.0.0.0".equals(oTLink.getTailNodeConnectorip())){
                                continue;
                            }
                            if(oTLink.getTailNodeConnectorip().equals(oTLink.getHeadNodeConnectorip())){
                                continue;
                            }
//                            for(int ii=0;ii<list.size();ii++){
//                                    if (oTLink.getHeadNodeConnectorip().equals(list.get(ii).getHeadNodeConnectorip()) 
//                                            && oTLink.getTailNodeConnectorip().equals(list.get(ii).getTailNodeConnectorip())) {
//                                        continue;
//                                    }
//                            } 
                            
                            Object[] o1 =OTIfmDAO.getInstance().getByIp(deviceList, oTLink.getHeadNodeConnectorip(), "Vlanif", devicesData);
                            if(null != o1){
                                OTDevice device = (OTDevice)o1[0];
                                oTLink.setHeadNodeId(device.getId());
                                oTLink.setHeadNodeIp(device.getDeviceTopoIp());
                                OTIfm ifm = (OTIfm)o1[1];
                                oTLink.setHeadNodeConnectorid(ifm.getName());
                            }
                            Object[] o2 =OTIfmDAO.getInstance().getByIp(deviceList, oTLink.getTailNodeConnectorip(), "Vlanif", devicesData);
                            if(null != o2){
                                OTDevice device = (OTDevice)o2[0];
                                oTLink.setTailNodeId(device.getId());
                                oTLink.setTailNodeIp(device.getDeviceTopoIp());
                                OTIfm ifm = (OTIfm)o2[1];
                                oTLink.setTailNodeConnectorid(ifm.getName());
                            }
                            if(null != o1 && null != o2){
                                if(!oTLink.getHeadNodeIp().equals(oTLink.getTailNodeIp())){
                                    result.add(oTLink);
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
        return result;
    }
    
    public String getUiMessage(List<OTLink> list){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_Link", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleLink.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }
    
    

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHeadNodeId() {
        return headNodeId;
    }
    public void setHeadNodeId(String headNodeId) {
        this.headNodeId = headNodeId;
    }
    public String getHeadNodeIp() {
        return headNodeIp;
    }
    public void setHeadNodeIp(String headNodeIp) {
        this.headNodeIp = headNodeIp;
    }
    public String getHeadNodeConnectorid() {
        return headNodeConnectorid;
    }
    public void setHeadNodeConnectorid(String headNodeConnectorid) {
        this.headNodeConnectorid = headNodeConnectorid;
    }
    public String getHeadNodeConnectorip() {
        return headNodeConnectorip;
    }
    public void setHeadNodeConnectorip(String headNodeConnectorip) {
        this.headNodeConnectorip = headNodeConnectorip;
    }
    public String getTailNodeId() {
        return tailNodeId;
    }
    public void setTailNodeId(String tailNodeId) {
        this.tailNodeId = tailNodeId;
    }
    public String getTailNodeIp() {
        return tailNodeIp;
    }
    public void setTailNodeIp(String tailNodeIp) {
        this.tailNodeIp = tailNodeIp;
    }
    public String getTailNodeConnectorid() {
        return tailNodeConnectorid;
    }
    public void setTailNodeConnectorid(String tailNodeConnectorid) {
        this.tailNodeConnectorid = tailNodeConnectorid;
    }
    public String getTailNodeConnectorip() {
        return tailNodeConnectorip;
    }
    public void setTailNodeConnectorip(String tailNodeConnectorip) {
        this.tailNodeConnectorip = tailNodeConnectorip;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    
    


    
    

}
