package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyNqa;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.agilete.northinterface.bean.OTNqa;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public class OTNqaNowDAO  implements IOTDao{

    private static OTNqaNowDAO single = null;
    private OpsRestCaller client = null;
    private OTNqaNowDAO(){

    }
    public synchronized  static OTNqaNowDAO getInstance() {
        if (single == null) {  
            single = new OTNqaNowDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result  = del(apiPath);
        }/*else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result  = edit(content);
        }*/else{

        }

        return result;
    }


    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc(403);
        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        
        String type = "";
        String name  = "";
        OTNqa nqa = new OTNqa();
        if(2 == apiPath.split("_").length){
            type = apiPath.split("_")[0];
            name = apiPath.split("_")[1];
        }else if(apiPath.split("_").length>2){
            int place = apiPath.indexOf("_");
            type = apiPath.substring(0, place);
            name = apiPath.substring(place+1);
        }else{
            result.setStatusCode(500);
            result.setContent("");
        }
        nqa.setName(type+"_"+client.deviceId+"_"+name);
        
        MyNqa myNqa = new MyNqa(client);
        myNqa.type = type;
        //del ping
        result = myNqa.delete(new String[]{nqa.getName()});
        
        return result;
    }

    /***
     * @param apiPath: type_name
     */
    public RetRpc get(String apiPath){

        RetRpc opsresult = new RetRpc();
        String type = "";
        String name  = "";
        String body = "";
        OTNqa nqa = new OTNqa();
        if(2 == apiPath.split("_").length){
            type = apiPath.split("_")[0];
            name = apiPath.split("_")[1];
        }else if(apiPath.split("_").length>2){
            int place = apiPath.indexOf("_");
            type = apiPath.substring(0, place);
            name = apiPath.substring(place+1);
        }else{
            opsresult.setStatusCode(500);
            opsresult.setContent("");
        }
        nqa.setName(type+"_"+client.deviceId+"_"+name);
        if("tunnel".equals(type)){
            nqa.setTunnelName(name);
            nqa.setType(type);
        }else if("flow".equals(type)){
            OTFlowDAO.getInstance().setClient(client);
            List<OTFlow> flowList = OTFlowDAO.getInstance().getList(name);
            if(null != flowList && flowList.size() == 1){
                OTFlow oTFlow = flowList.get(0);
                nqa.setType(type);
                nqa.setTunnelId(oTFlow.getIdentifyIndex());
                nqa.setPeerIp(oTFlow.getDesIp());
            }else{
                opsresult.setStatusCode(500);
                opsresult.setContent("can not find flow :"+name);
                return opsresult;
            }
        }

        MyNqa myNqa  =new MyNqa(client);
        myNqa.type = type;
        List<OTNqa> list = getList(nqa.getName(),client);
        RetRpc result = new RetRpc();
        if(200 == opsresult.getStatusCode()){
            if(null != list && !list.isEmpty()){
                for(OTNqa oTNqa:list){
//                    if("1".equals(MyData.getSimulantData())){
//                        oTNqa.setRttAverage(String.valueOf((int)(Math.random()*100)));
//                        if("flow".equals(type)){
//                            oTNqa.setLoss(String.valueOf((int)(Math.random()*100)));
//                        }
//                    }
                    oTNqa.setTime(String.valueOf(System.currentTimeMillis()));
                    result.setContent(oTNqa.getUiMessage(list));
                }
            }
        }
        if("".equals(result.getContent()) || "false".equals(result.getContent())){
            result.setContent("NA");
        }
        
        if(null !=nqa.getType() || !"".equals(nqa.getType())){
            body = OTNqaDAO.getInstance().getOpsBody(nqa);
            NqaTask nqaTask = new NqaTask();
            nqaTask.name = nqa.getName();
            nqaTask.body = body;
            nqaTask.type = type;
            nqaTask.client = client;
            Thread t = new Thread(nqaTask);
            t.start();
        }
        return result;
    }

    class NqaTask implements Runnable{
        public String name = "";
        public String body = "";
        public String type = "";
        public OpsRestCaller client;

        @Override
        public void run() {
            RetRpc opsresult = new RetRpc();
            if(null != client){
                MyNqa myNqa = new MyNqa(client);
                myNqa.type = type;
                //del ping
                opsresult = myNqa.delete(new String[]{name});
                //add ping
                if(200 == opsresult.getStatusCode()){
                    myNqa.body = body;
                    opsresult = myNqa.create(null);
                }
            }
        }

    }


    public List<OTNqa> getList(String apiPath,OpsRestCaller client){
        List<OTNqa> list = null;
        if(null == client){
            client = this.client;
        }
        MyNqa myNqa  =new MyNqa(client);
        myNqa.type = apiPath.split("_")[0];
        String[] name = null;
        if(!"".equals(apiPath)){
            name = new String[]{apiPath};
        }
        RetRpc opsresult = myNqa.get(name);
        if(opsresult.getStatusCode()==200){
            OTNqa OTNqa = new OTNqa();
            list = OTNqa.parseOpsToUi(myNqa.type,opsresult.getContent());
        }
        return list;
    }

    public String getOpsBody(OTNqa nqa){
        OTNqa oTNqa = nqa;
        String body = oTNqa.getOpsMessage();
        return body;
    }
    
    public RetRpc clearNqa(){
        RetRpc result = new RetRpc();
        DBAction db = new DBAction();
        List<String[]> list = db.getAll("devices","devices");
        for(int i=0;i<list.size();i++){
            String deviceId = list.get(i)[0];
            OpsRestCaller  client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(""), deviceId);
            this.client = client;
            OTTunnelDAO.getInstance().setClient(client);
            List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps("",false);
            if(null != tunnelList){
                for(OTTunnel oTTunnel:tunnelList){
                    String name = "tunnel_"+oTTunnel.getName();
                    del(name);
                }
            }
            List<OTFlow> flowList = OTFlowDAO.getInstance().getList("");
            if(null != flowList){
                for(OTFlow oTFlow:flowList){
                    String name = "flow_"+oTFlow.getName();
                    del(name);
                }
            }
        }
        return result;
    }



    public static void main(String[] args) {
        OTNqaNowDAO.getInstance().clearNqa();
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        //OTNqaDAO.getInstance().get("1", "5", "");
        /*//System.out.println(OTNqaDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }



}
