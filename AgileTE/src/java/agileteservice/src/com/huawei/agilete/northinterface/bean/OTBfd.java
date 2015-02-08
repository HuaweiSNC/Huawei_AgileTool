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

public class OTBfd {
    
    private String name = "";
    private String discLocal = "";
    private String discRemote = "";
    private String tunnelName = "";
    private String minTxInterval = "";
    private String minRxInterval = "";
    private String check = "";
    private String wtrTimerInt = "";
    private String teBackup = "";
    private String shutdown = "";
    
    private List<OTBfd> bfds = new ArrayList<OTBfd>();
    
    public OTBfd(){
        
    }
    
    public OTBfd(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("bfds".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    OTBfd bfd = new OTBfd();
                    Element domain = i.next();
                    if(null != domain.elementText("name") && !"".equals(domain.elementText("name"))){
                        bfd.setName(domain.elementText("name"));
                    }
                    if(null != domain.elementText("discLocal") && !"".equals(domain.elementText("discLocal"))){
                        bfd.setDiscLocal(domain.elementText("discLocal"));
                    }
                    if(null != domain.elementText("discRemote") && !"".equals(domain.elementText("discRemote"))){
                        bfd.setDiscRemote(domain.elementText("discRemote"));
                    }
                    if(null != domain.elementText("tunnelName") && !"".equals(domain.elementText("tunnelName"))){
                        bfd.setTunnelName(domain.elementText("tunnelName"));
                    }
                    if(null != domain.elementText("minTxInterval") && !"".equals(domain.elementText("minTxInterval"))){
                        bfd.setMinTxInterval(domain.elementText("minTxInterval"));
                    }
                    if(null != domain.elementText("minRxInterval") && !"".equals(domain.elementText("minRxInterval"))){
                        bfd.setMinRxInterval(domain.elementText("minRxInterval"));
                    }
                    if(null != domain.elementText("teBackup") && !"".equals(domain.elementText("teBackup"))){
                        bfd.setTeBackup(domain.elementText("teBackup"));
                    }
                    if(null != domain.elementText("adminDown") && !"".equals(domain.elementText("adminDown"))){
                        bfd.setShutdown(domain.elementText("adminDown"));
                    }
                    bfds.add(bfd);
                }
            }else{
                return;
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    public List<OTBfd> parseOpsToUi(String content){
        List<OTBfd> list = new ArrayList<OTBfd>();

        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele =el.element("bfdCfgSessions");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTBfd oTBfd = new OTBfd();
                Element domain = i.next();
                if(null != domain.elementText("sessName") && !"".equals(domain.elementText("sessName"))){
                    oTBfd.setName(domain.elementText("sessName"));
                }
                if(null != domain.elementText("localDiscr") && !"".equals(domain.elementText("localDiscr"))){
                    oTBfd.setDiscLocal(domain.elementText("localDiscr"));
                }
                if(null != domain.elementText("remoteDiscr") && !"".equals(domain.elementText("remoteDiscr"))){
                    oTBfd.setDiscRemote(domain.elementText("remoteDiscr"));
                }
                if(null != domain.elementText("tunnelName") && !"".equals(domain.elementText("tunnelName"))){
                    oTBfd.setTunnelName(domain.elementText("tunnelName"));
                }
                if(null != domain.elementText("minTxInt") && !"".equals(domain.elementText("minTxInt"))){
                    oTBfd.setMinTxInterval(domain.elementText("minTxInt"));
                }
                if(null != domain.elementText("minRxInt") && !"".equals(domain.elementText("minRxInt"))){
                    oTBfd.setMinRxInterval(domain.elementText("minRxInt"));
                }
                if(null != domain.elementText("wtrTimerInt") && !"".equals(domain.elementText("wtrTimerInt"))){
                    oTBfd.setWtrTimerInt(domain.elementText("wtrTimerInt"));
                }
                if(null != domain.elementText("teBackup") && !"".equals(domain.elementText("teBackup"))){
                    oTBfd.setTeBackup(domain.elementText("teBackup"));
                }
                if(null != domain.elementText("adminDown") && !"".equals(domain.elementText("adminDown"))){
                    oTBfd.setShutdown(domain.elementText("adminDown"));
                }
                list.add(oTBfd);
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
        return list;
    }
    

    public String getOpsMessage(){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = null;
        mapContext = new LinkedHashMap<String, Object>();
        OTBfd otBfd = this;
        mapContext.put("t_bfd", otBfd);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleBfd.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    public String getUiMessage(List<OTBfd> list){
        String result = "";
        if(list.size() == 0){
            return "";
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = null;
        mapContext = new LinkedHashMap<String, Object>();
        OTBfd otBfd = this;
        mapContext.put("T_Bfd", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleBfd.tpl", mapContext, templetPath);
        result = write.toString();
        return result.toString();
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDiscLocal() {
        return discLocal;
    }
    public void setDiscLocal(String discLocal) {
        this.discLocal = discLocal;
    }
    public String getDiscRemote() {
        return discRemote;
    }
    public void setDiscRemote(String discRemote) {
        this.discRemote = discRemote;
    }
    public String getTunnelName() {
        return tunnelName;
    }
    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }
    public String getMinTxInterval() {
        return minTxInterval;
    }
    public void setMinTxInterval(String minTxInterval) {
        this.minTxInterval = minTxInterval;
    }
    public String getMinRxInterval() {
        return minRxInterval;
    }
    public void setMinRxInterval(String minRxInterval) {
        this.minRxInterval = minRxInterval;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public String getWtrTimerInt() {
        if(null != wtrTimerInt && !"".equals(wtrTimerInt)){
            int flag = Integer.parseInt(wtrTimerInt)*3;
            return String.format("%d", flag);
        }
        return wtrTimerInt;
    }
    public void setWtrTimerInt(String wtrTimerInt) {
        this.wtrTimerInt = wtrTimerInt;
    }
    public String getTeBackup() {
        return teBackup;
    }
    public void setTeBackup(String teBackup) {
        this.teBackup = teBackup;
    }
    public String getShutdown() {
        return shutdown;
    }
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    public List<OTBfd> getBfds() {
        return bfds;
    }

    public void setBfds(List<OTBfd> bfds) {
        this.bfds = bfds;
    }

    
    
    
    

}
