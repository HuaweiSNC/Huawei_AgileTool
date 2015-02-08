package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyNqa;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.agilete.northinterface.bean.OTNqa;
import com.huawei.agilete.northinterface.bean.OTPolicy;
import com.huawei.agilete.northinterface.bean.OTTunnel;
import com.huawei.agilete.task.TaskNqa;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTNqaDAO  implements IOTDao{

    private static OTNqaDAO single = null;
    private OpsRestCaller client = null;
    public String deviceId = "";
    private OTNqaDAO(){

    }
    public synchronized  static OTNqaDAO getInstance() {
        if (single == null) {  
            single = new OTNqaDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.deviceId = deviceId;
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }/*else if(restType.equals(MyData.RestType.DELETE)){
            result  = del(apiPath);
        }else if(restType.equals(MyData.RestType.POST)){
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
        RetRpc result = new RetRpc(403);
        return result;
    }

    /***
     * @param apiPath: deviceId-tunnelName
     */
    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        String type = "";
        String name  = "";
        
        if(2 == apiPath.split("_").length){
            type = apiPath.split("_")[0];
            name = apiPath.split("_")[1];
        }
        
        DBAction db = new DBAction();
        List<String[]> rttList = db.getAll("nqa", "NQA"+"_"+type+"_"+deviceId+"_"+name);
        List<OTNqa> list = new ArrayList<OTNqa>();
        if(null != rttList){
            int i = rttList.size()>=20?rttList.size()-20:0;
            for(;i<rttList.size();i++){
                String[] flag = rttList.get(i)[1].split("_");
                //<data><schedule>$!{x}</schedule><value1>$!{y1}</value1><value2>$!{y2}</value2></data>
                OTNqa nqa = new OTNqa();
//                nqa.setTime(flag[0]);
//                nqa.setRttAverage(flag[1]);
                nqa.setTime(String.valueOf(System.currentTimeMillis()));
                nqa.setRttAverage(String.valueOf(Math.random()*100));
                nqa.setLoss(flag[2]);
                nqa.setType(type);
                //list.add(nqa);
            }
            if(!rttList.isEmpty()){
                String[] flag = rttList.get(rttList.size()-1)[1].split("_");
                //<data><schedule>$!{x}</schedule><value1>$!{y1}</value1><value2>$!{y2}</value2></data>
                OTNqa nqa = new OTNqa();
                nqa.setTime(flag[0]);
                nqa.setRttAverage(flag[1]);
                nqa.setLoss(flag[2]);
                nqa.setType(type);
                list.add(nqa);
            }
            if(!list.isEmpty()){
                OTNqa oTNqa = new OTNqa();
                opsresult.setContent(oTNqa.getUiMessage(list));
            }else{
                opsresult.setContent("");
            }
            
            
        }


        return opsresult;
    }



    public RetRpc runTaskMain(String domainId,String type){

        RetRpc opsresult = new RetRpc();
        List<OTDevice> deviceList = OTDeviceDAO.getInstance().getByDomain(domainId);
        if(null == deviceList || deviceList.size() == 0){
            return opsresult;
        }
        for(int index=0;index<deviceList.size();index++){
            OTDevice oTDevice = deviceList.get(index);
            OpsRestCaller client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), oTDevice.getId());
            //根据当前设备所有tunnel
            OTTunnelDAO.getInstance().setClient(client);
            List<OTTunnel> tunnelList = OTTunnelDAO.getInstance().getOps("",false);
            for(int i=0;i<tunnelList.size();i++){
                OTTunnel tunnel = tunnelList.get(i);
                TaskNqa tunnelTask = new TaskNqa(oTDevice.getId());
                long t = 0;
                try{
                    t = Long.parseLong("0");
                }catch(Exception e){
                }
                tunnelTask.client = client;
                tunnelTask.setRunOne(true);
                tunnelTask.setInterval(t);
                tunnelTask.setTitle("NQA"+"_"+type+"_"+client.deviceId+"_"+tunnel.getName()); //NQA CE12800_A TUNNEL1
                OTNqa tunnelNqa = new OTNqa();
                tunnelNqa.setName(tunnelTask.getTitle());
                tunnelNqa.setTunnelName(tunnel.getName());
                tunnelNqa.setType(type);
                tunnelTask.body = OTNqaDAO.getInstance().getOpsBody(tunnelNqa);
                if("tunnel".equals(type)){
                    MyData.TaskChildControl.addTask(tunnelTask);
                }else if("flow".equals(type)){
                    //获取当前设备所有pocily
                    OTPolicyDAO.getInstance().setClient(client);
                    List<OTPolicy> policyList = OTPolicyDAO.getInstance().getList("");
                    
                    //获取当前设备所有flow
                    OTFlowDAO.getInstance().setClient(client);
                    List<OTFlow> flowList = OTFlowDAO.getInstance().getList("");
                    
                    for(int j=0 ;j<policyList.size();j++){
                        OTPolicy oTPolicy = policyList.get(j);
                        if(tunnel.getName().equals(oTPolicy.getTunnelName())){
                            for(int k=0;k<flowList.size();k++){
                                OTFlow oTFlow = flowList.get(k);
                                if(oTPolicy.getName().equals(oTFlow.getPolicy())){
                                    TaskNqa flowTask = new TaskNqa(oTDevice.getId());
                                    long t1 = 0;
                                    try{
                                        t1 = Long.parseLong("0");
                                    }catch(Exception e){
                                    }
                                    flowTask.client = client;
                                    flowTask.setInterval(t1);
                                    flowTask.setRunOne(true);
                                    flowTask.setTitle("NQA"+"_"+type+"_"+client.deviceId+"_"+oTFlow.getName()); //NQA CE12800_A TUNNEL1
                                    OTNqa flowNqa = new OTNqa();
                                    flowNqa.setType(type);
                                    flowNqa.setName(flowTask.getTitle());
                                    flowNqa.setTunnelId(oTFlow.getIdentifyIndex());
                                    flowNqa.setPeerIp(oTFlow.getDesIp());
                                    flowTask.body = OTNqaDAO.getInstance().getOpsBody(flowNqa);
                                    MyData.TaskChildControl.addTask(flowTask);
                                }
                            }
                        }
                    }
                    
                    
                    
                    
                }
            }

        }



        return opsresult;
    }


    /**
     * 先GET。 再删除，再PING ,这里的ping为下一次做准备。
     * @param apiPath: deviceId-tunnelName 任务名
     */
    public RetRpc runTask(OpsRestCaller client,TaskNqa taskWrapper,Boolean saveDb){
        RetRpc opsresult = new RetRpc();
        String result = "";
        MyNqa myNqa  =new MyNqa(client);
        OTNqa nqa = null;
        String apiPath = taskWrapper.getTitle();
        myNqa.type = apiPath.split("_")[1];
        List<OTNqa> list = getList(apiPath,client);
        if(null != list && !list.isEmpty()){
            nqa = list.get(0);
//            String content = "<data><schedule>$!{x}</schedule><value1>$!{y1}</value1><value2>$!{y2}</value2></data>";
//            content = content.replace("$!{x}", String.format("%d", System.currentTimeMillis()));
//            content = content.replace("$!{y1}", nqa.getRttAverage());
//            content = content.replace("$!{y2}", nqa.getLoss());
            StringBuffer content = new StringBuffer();
            if("".equals(nqa.getLoss())){
                nqa.setLoss("NA");
            }
            content.append(System.currentTimeMillis()).append("_").append(nqa.getRttAverage()).append("_").append(nqa.getLoss());
            result = content.toString();
            DBAction db = new DBAction();
            Boolean flag = false;
            if(saveDb){
                flag = db.insert("nqa", apiPath, String.valueOf(System.currentTimeMillis()), content.toString());
            }
            if(saveDb && flag){
                List<String[]> allList = db.getAll("nqa", apiPath);
                int maxlengh = 100;
                if(allList.size() >= maxlengh){
                    for(int i=0;i<allList.size()-maxlengh;i++){
                        String[] flagData = allList.get(i);
                        flag = db.delete("nqa", apiPath, flagData[0]);
                    }
                }
            }
        }
        if(null == list){
            nqa = new OTNqa();
        }
        //del ping
        opsresult = myNqa.delete(new String[]{apiPath});
        //add ping
        if(200 == opsresult.getStatusCode()){
            myNqa.body = taskWrapper.body;
            opsresult = myNqa.create(null);
        }
        if(200 == opsresult.getStatusCode()){
            opsresult.setContent(result);
        }
        return opsresult;
    }

    public List<OTNqa> getList(String apiPath,OpsRestCaller client){
        List<OTNqa> list = null;
        if(null == client){
            client = this.client;
        }
        MyNqa myNqa  =new MyNqa(client);
        myNqa.type = apiPath.split("_")[1];
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



    public static void main(String[] args) {
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
