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

import com.huawei.networkos.ops.util.threadpool.WorkerTaskBean;
import com.huawei.networkos.templet.VMTempletManager;

public class OTTask {
    
    private String name = "";
    private String time = "";
    private String state = "";
    private String type = "";
    private String body = "";
    private String check = "";
    
    public OTTask(){
        
    }
    
    public OTTask(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("tasks".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    Element sc = domain.element("sc");
                    if(null != sc.elementText("name") && !"".equals(sc.elementText("name"))){
                        setName(sc.elementText("name"));
                    }
                    if(null != sc.elementText("time") && !"".equals(sc.elementText("time"))){
                        setTime(sc.elementText("time"));
                    }
                    if(null != sc.elementText("state") && !"".equals(sc.elementText("state"))){
                        setState(sc.elementText("state"));
                    }
                    if(null != sc.elementText("type") && !"".equals(sc.elementText("type"))){
                        setType(sc.elementText("type"));
                    }
                    Element request = domain.element("request");
                    if(null != request){
                        setBody(request.asXML());
                    }
                }
            }else{
                return;
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
    }
    
    public OTTask(WorkerTaskBean workerTaskBean){
        setName(workerTaskBean.getName());
//        setTime(workerTaskBean.get)
//        setState(workerTaskBean.getRunning())
//        setType(workerTaskBean.get)
//        setBody(workerTaskBean.get)
        
    }

    
    public List<OTTask> parseOpsToUi(String content){
        List<OTTask> list = new ArrayList<OTTask>();

        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            Element ele =el.element("tunnelPolicys");
            for(Iterator<Element> i=ele.elementIterator();i.hasNext();){
                OTTask oTBfd = new OTTask();
                Element domain = i.next();
                if(null != domain.elementText("tnlPolicyName") && !"".equals(domain.elementText("tnlPolicyName"))){
                    oTBfd.setName(domain.elementText("tnlPolicyName"));
                }
                
                Element tpNexthops = domain.element("tpNexthops");
                if(null != tpNexthops){
                    for(Iterator<Element> j=tpNexthops.elementIterator();j.hasNext();){
                        Element tpNexthop = j.next();
                        if(null != tpNexthop.elementText("nexthopIPaddr") && !"".equals(tpNexthop.elementText("nexthopIPaddr"))){
//                            oTBfd.setIp(tpNexthop.elementText("nexthopIPaddr"));
                        }
                        Element tpTunnels = tpNexthop.element("tpTunnels");
                        if(null != tpTunnels){
                            for(Iterator<Element> k=tpTunnels.elementIterator();k.hasNext();){
                                Element tpTunnel = k.next();
                                if(null != tpTunnel.elementText("tunnelName") && !"".equals(tpTunnel.elementText("tunnelName"))){
//                                    oTBfd.setTunnelName(tpTunnel.elementText("tunnelName"));
                                }
                            }
                        }
                    }
                }

                list.add(oTBfd);
            }
        }catch (DocumentException e) {
            //e.printStackTrace();
        }
        return list;
    }
    

    public String getUiMessage(List<OTTask> list){
        String result = "";
        if(list.size() == 0){
            return result;
        }
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_task", list);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleTask.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }

}
