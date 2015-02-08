package com.huawei.agilete.northinterface.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.bean.OpsServer;
import com.huawei.agilete.base.common.MyIO;
import com.huawei.agilete.base.common.ReadConfig;
import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.northinterface.bean.OTDomain;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class OTDomainDAO {
    private static OTDomainDAO single = null;
    private MyData myData;
    private OTDomainDAO(){
        myData = new MyData();
    }
    public synchronized  static OTDomainDAO getInstance() {
        if (single == null) {  
            single = new OTDomainDAO();
        }  
        return single;
    }

    class Runner implements Runnable { 
        private OTDomain oTDomain;
        private Element headNodeConnector;
        private Element tailNodeConnector;
        private int i;
        private Map flags;
        public Runner(OTDomain oTDomain,Element headNodeConnector,Element tailNodeConnector,int i,Map flags){
            this.oTDomain = oTDomain;
            this.headNodeConnector = headNodeConnector;
            this.tailNodeConnector = tailNodeConnector;
            this.i = i;
            this.flags = flags;
        }
        public void run() {        
            String connectorid = headNodeConnector.elementText("Connectorid");
            //接口ip
            String Connectorip = headNodeConnector.elementText("Connectorip");
            String connectorState = "down";
            //开始设备的节点
            Element toponode = headNodeConnector.element("toponode");
            //设备id
            String nodeID = toponode.elementText("nodeID");
            
            MyIfmInterface myIfmInterface = new MyIfmInterface( new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(oTDomain.getId()), nodeID));
            
            List<String[]> ifmlist = myIfmInterface.getAll("Vlanif",connectorid);
            for(String[] ifm:ifmlist){
                if(ifm[1].equals(connectorid)){
                    connectorState=ifm[4];
                }
            }
            
            
            String old = "<headNodeConnector><Connectorid>"+connectorid+"</Connectorid><Connectorip>"+Connectorip+"</Connectorip><toponode><nodeID>"+nodeID+"</nodeID>";
            String replace = "<headNodeConnector><Connectorid>"+connectorid+"</Connectorid><ConnectorState>"+connectorState+"</ConnectorState><Connectorip>"+Connectorip+"</Connectorip><toponode><nodeID>"+nodeID+"</nodeID>";
            myData.links=myData.links.replace(old, replace);
            
            
            String tailconnectorid = tailNodeConnector.elementText("Connectorid");
            //终点接口ip
            String tailConnectorip = tailNodeConnector.elementText("Connectorip");
            String tailconnectorState = "down";
            //终点设备的节点
            Element tailtoponode = tailNodeConnector.element("toponode");
            //终点设备id
            String tailnodeID = tailtoponode.elementText("nodeID");
            MyIfmInterface tailmyIfmInterface = new MyIfmInterface( new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(oTDomain.getId()), tailnodeID));
            
            List<String[]> tailifmlist = tailmyIfmInterface.getAll("Vlanif",tailconnectorid);
            for(String[] ifm:tailifmlist){
                if(ifm[1].equals(tailconnectorid)){
                    tailconnectorState=ifm[4];
                }
            }
            
            String tailold = "<tailNodeConnector><Connectorid>"+tailconnectorid+"</Connectorid><Connectorip>"+tailConnectorip+"</Connectorip><toponode><nodeID>"+tailnodeID+"</nodeID>";
            String tailreplace = "<tailNodeConnector><Connectorid>"+tailconnectorid+"</Connectorid><ConnectorState>"+tailconnectorState+"</ConnectorState><Connectorip>"+tailConnectorip+"</Connectorip><toponode><nodeID>"+tailnodeID+"</nodeID>";
            myData.links=myData.links.replace(tailold, tailreplace);
            flags.put(i, true);
        }
    }
    
    
    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        content = MyIO.characterFormat(content);
        OTDomain oTDomain = new OTDomain(content);
        if(null != oTDomain.getId() && !"".equals(oTDomain.getId())){
            content = content.replace(content.substring(content.indexOf("<id>"), content.indexOf("</id>")+"</id>".length()), "");
        }
        DBAction db = new DBAction();
        List<String[]> list = db.getAll("domains","domains");
        int x=1;
        for(String[] ch:list){
            int y = Integer.parseInt(ch[0]);
            x = x>=y?x:y;
        }
        
        String maxId = String.format("%d", x+1);
        StringBuffer id = new StringBuffer();
        id.append("<id>");
        id.append(maxId);
        id.append("</id><name>");
        content = content.replace("<name>", id);
        Boolean flag = db.insert("domains","domains", maxId, content);
        
        //            if(flag){
        //                //add devices
        //                int count = 0;
        //                while(-1 != content.indexOf("<device>",count)){
        //                    StringBuffer devices = new StringBuffer();
        //                    devices.append("<devices>");
        //                    devices.append(content.substring(content.indexOf("<device>",count), content.indexOf("</device>",count)+"</device>".length()));
        //                    devices.append("</devices>");
        //                    count =content.indexOf("</device>",count)+"</device>".length();
        //                    OTDeviceDAO.getInstance().add(maxId, devices.toString());
        //                }
        //            }
        if(flag){
            OTWtrDAO.getInstance().control(maxId, "", "", MyData.RestType.POST, "<wtrs><wtr><domainId>"+maxId+"</domainId><value>5</value></wtr></wtrs>");
            result.setStatusCode(200);
            result.setContent(maxId);
        }
        return result;
    }

    public RetRpc del(String domainId){
        RetRpc result = new RetRpc();

        if(null != domainId && !"".equals(domainId)){
            DBAction db = new DBAction();
            Boolean flag = db.delete("domains","domains", domainId);
            if(flag){
                result.setStatusCode(200);
                List<String[]> list = db.getAll("links",domainId);
                if(null != list){
                    for(int i=0;i<list.size();i++){
                        OTLinkDAO.getInstance().control(domainId, "", list.get(i)[0], MyData.RestType.DELETE, "");
                    }
                }
                OTTopoLocationDAO.getInstance().del(domainId);
                OTWtrDAO.getInstance().control(domainId, "", "", MyData.RestType.DELETE, "");
                result.setContent("true");
            }else{
                result.setStatusCode(500);
                result.setContent("db error!");
            }
        }else{
            result.setStatusCode(403);
            result.setContent("ID is null!");
        }
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        content = MyIO.characterFormat(content);
        OTDomain oTDomain = new OTDomain(content);
        if("".equals(oTDomain.getCheck())){
            DBAction db = new DBAction();
            Boolean flag = db.insert("domains","domains", oTDomain.getId(), content);
            if(flag){
                result.setStatusCode(200);
                result.setContent("true");
            }else{
                result.setStatusCode(500);
                result.setContent("db error!");
            }
        }else{
            result.setStatusCode(403);
            result.setContent("Input is null!");
        }
        return result;
    }
    public List<String[]> getAllList(){
        DBAction db = new DBAction();
        List<String[]> list = db.getAll("domains","domains");
        return list;
    }
    
    
    /**
     * 获取接口状态信息，添加到links中，，，xc
     * @param links
     * @param oTDomain
     * @return
     */
    
    
    
    private String getIfmState(OTDomain oTDomain){
        Map flags = new LinkedMap();
        Long l1 = new Date().getTime();
        if(!"".equals(myData.links)&&myData.links!=null){
            try {
                Document doc = DocumentHelper.parseText(myData.links);
                Element ele = doc.getRootElement();
                int i = 0;
                for(Iterator<Element> itera=ele.elementIterator();itera.hasNext();i++){
                    flags.put(i, false);
                    Element topoLink =itera.next();
                    //开始的节点
                    Element headNodeConnector = topoLink.element("headNodeConnector");
                    Element tailNodeConnector = topoLink.element("tailNodeConnector");
                    synchronized (myData.links) {
                        Thread t1 = new Thread(new Runner(oTDomain,headNodeConnector,tailNodeConnector,i,flags));
                        t1.start();
                    }
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        
        
        while(true){
                boolean flag = false;
                Collection c = flags.values();
                Iterator it = c.iterator();
                int i = 0;
                for (i=0; it.hasNext();) {
                    flag = (Boolean) it.next();
                    if(flag){
                        i++;
                    }
                }
                if(i==c.size()){
                    break;
                }
                
        }
        Long l2 = new Date().getTime();
        //System.out.println("获取接口状态的时间是："+(l2-l1));
        
        return myData.links;
    }
    
    
    public RetRpc getAll(){
        RetRpc result = new RetRpc();
        List<String[]> list = getAllList();
        StringBuffer xml = new StringBuffer();
        xml.append("<domains>");
        for(int i=0;i<list.size();i++){
            String domain = list.get(i)[1];
            domain = domain.replace("<domains>", "");
            domain = domain.replace("</domains>", "");
            domain = domain.replace("</domain>", "");
            domain = domain.replace("</describe>", "</describe><topo>");
            
            xml.append(domain);
            OTDomain oTDomain = new OTDomain(list.get(i)[1]);
            if("".equals(oTDomain.getCheck())){
                
                myData.links = OTLinkDAO.getInstance().control(oTDomain.getId(), "", "", MyData.RestType.GET, "").getContent();
                //获取接口状态信息  xc
                getIfmState(oTDomain);
                xml.append(myData.links);
            }
            xml.append("</topo>");
            xml.append("</domain>");
        }
        xml.append("</domains>");
        result.setStatusCode(200);
        result.setContent(xml.toString());
        return result;
    }
    
    
    public RetRpc get(String id){
        RetRpc result = new RetRpc();
        if(StringUtils.isBlank(id)){
             result.setStatusCode(500);
             result.setContent("id can not null");
            return  result;
        }
            DBAction db = new DBAction();
            String flag = db.get("domains","domains",id);
            if(null != flag && !"".equals(flag)){
                OTDomain oTDomain = new OTDomain(flag);
                flag = flag.replace("</describe>", "</describe><topo>");
                if("".equals(oTDomain.getCheck())){
                    myData.links = OTLinkDAO.getInstance().control(oTDomain.getId(), "", "", MyData.RestType.GET, "").getContent();
                    //获取接口状态信息  xc
                    getIfmState(oTDomain);
                    
                    flag = flag.replace("</domain>", myData.links+"</topo></domain>");
                }
                
                result.setStatusCode(200);
                result.setContent(flag);
            }
        
        return result;
    }
    
    public OTDomain getByName(String name){
        RetRpc result = new RetRpc();
        List<String[]> list = getAllList();
        for(int i=0;i<list.size();i++){
            OTDomain oTDomain = new OTDomain(list.get(i)[1]);
            if(name.equals(oTDomain.getName())){
                return oTDomain;
            }
        }
        return null;
    }
    
    public OpsServer getOpsServer(String domainId){
        //default
        OpsServer opsServer = new OpsServer();
        ReadConfig r  =new ReadConfig();
        opsServer.setServerIp(r.get().getProperty("OpsServerIp").split(",")[0]);
        String port = r.get().getProperty("OpsServerPort").split(",")[0];
        try{
            opsServer.setPort(Integer.parseInt(port));
        }catch(Exception e){
        }
        opsServer.setProtocol(r.get().getProperty("OpsProtocol").split(",")[0]);
        opsServer.setUserName(r.get().getProperty("OpsUserName").split(",")[0]);
        opsServer.setPasswd(r.get().getProperty("OpsPw").split(",")[0]);
        return opsServer;
    }
    
    public static void main(String[] args) {
        //OTDomainDAO.getInstance().add("<domains><domain><name>管理域A</name><type>Agile TE View</type><devices><device> <deviceName>1</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices><device><deviceName>2</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices><device><deviceName>3</deviceName><ipAddress>10.111.69.127</ipAddress><userName>root</userName><passwd>toot@123</passwd><version>V8R8</version><type>CX600</type><id>1</id></device></devices></domain></domains>");
        //System.out.println(OTDomainDAO.getInstance().del("1"));
        ////System.out.println(OTDomainDAO.getInstance().get(""));
    }

}






