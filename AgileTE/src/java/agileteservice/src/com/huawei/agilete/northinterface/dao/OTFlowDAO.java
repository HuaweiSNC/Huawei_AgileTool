package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyL2vpnVpwsInstance;
import com.huawei.agilete.base.servlet.MyMplsLdpInstance;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTFlow;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTFlowDAO  implements IOTDao{

    private static OTFlowDAO single = null;
    private OpsRestCaller client = null;
    private OTFlowDAO(){
        
    }
    public synchronized  static OTFlowDAO getInstance() {
         if (single == null) {  
             single = new OTFlowDAO();
         }  
        return single;
    }
    
    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        this.client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result = del(apiPath);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else{
            
        }
        return result;
    }
    
    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        OTFlow oTFlow = new OTFlow(content);
        MyMplsLdpInstance myMplsLdpInstance = new MyMplsLdpInstance(client);
        result = myMplsLdpInstance.modify(new String[]{oTFlow.getDesIp()});
        //if(result.getStatusCode()==200){//??  原来直接为True TODO
            String bodyPath = oTFlow.getOpsMessage("1");
            MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
            myL2vpnVpwsInstance.body=bodyPath;
            result = myL2vpnVpwsInstance.create(null);
            if(200 != result.getStatusCode()){
                myL2vpnVpwsInstance.body="";
                RetRpc flag = myL2vpnVpwsInstance.delete((new String[]{oTFlow.getName(),"vpwsLdp"}));
            }
        //}
        return result;
    }
    
    public RetRpc edit(String content){
        //先删除 再添加
//        RetRpc result = new RetRpc();
//        OTFlow oTFlow = new OTFlow(content);
//        result = del(oTFlow.getName());
//        if(result.getStatusCode()==200){
//            result = add(content);
//        }
        
        //直接修改
        RetRpc result = new RetRpc();
        OTFlow oTFlow = new OTFlow(content);
        String bodyPath = oTFlow.getOpsMessage("0");
        MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
        myL2vpnVpwsInstance.body=bodyPath;
        result = myL2vpnVpwsInstance.modify(new String[]{oTFlow.getName()});
        return result;
    }
    
    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        String content = get(apiPath).getContent();
        
        if(null != content && !"".equals(content)){
            OTFlow oTFlow = new OTFlow(content);
            if(null != oTFlow){
                if(null != oTFlow.getDesIp() && !"".equals(oTFlow.getDesIp())){
                    MyMplsLdpInstance myMplsLdpInstance = new MyMplsLdpInstance(client);
                    result = myMplsLdpInstance.delete(new String[]{oTFlow.getDesIp()});
                }else{
                    result.setStatusCode(200);//原来直接为True TODO
                    result.setContent("true");
                }
                if(result.getStatusCode()==200){
                    MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
                    result = myL2vpnVpwsInstance.deleteEnable((new String[]{oTFlow.getName()}));//去使能
                    myL2vpnVpwsInstance.body = null;
                    if(result.getStatusCode()==200){
                    	result = myL2vpnVpwsInstance.delete((new String[]{oTFlow.getName(),"vpwsLdp"}));
                    }
                }
            }
        }
        return result;
    }
    
    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        List<OTFlow> list = getList(apiPath);
        if(null != list){
            OTFlow oTFlow = new OTFlow();
            String flag = oTFlow.getUiMessage(list);
            opsresult.setContent(flag);
        }else{
            opsresult.setStatusCode(500);
            opsresult.setContent("Error!");
        }
        return opsresult;
    }
    
    public List<OTFlow> getList(String apiPath){
        
        
        
//        Domain it = getDomain(domainId);
//        OTDevice device =  it.getdevice(devieid);
//        OpsServer server = it .getOpsServer();
//        client = new OpsRestCaller(server, pe1);
//        return client;
        
        List<OTFlow> list = null;
        MyL2vpnVpwsInstance myL2vpnVpwsInstance  =new MyL2vpnVpwsInstance(client);
        String[] name = null;
        if(!"".equals(apiPath)){
            name = new String[]{apiPath,"vpwsLdp"};
        }
        
        RetRpc opsresult = myL2vpnVpwsInstance.get(name);
        if(opsresult.getStatusCode()==200){
            OTFlow oTFlow = new OTFlow();
            list = oTFlow.parseOpsToUi(opsresult.getContent());
        }
        return list;
    }
    
    
    
    
    public static void main(String[] args) {
        String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        ////System.out.println(OTFlowDAO.getInstance().del("1", "5", "test4"));
        ////System.out.println(OTFlowDAO.getInstance().edit("1", "3", "<flows><flow><name>flow4</name><identifyIndex>400</identifyIndex><desIp>10.137.130.68</desIp><policy>policy2</policy><isDouleConfig>false</isDouleConfig><interfaceName>Ethernet0/1/4</interfaceName></flow></flows>"));
        ////System.out.println(OTFlowDAO.getInstance().edit("1", "5", "<flows><flow><name>test4</name><identifyIndex>45</identifyIndex><desIp>10.137.130.132</desIp><policy>WE</policy><isDouleConfig>false</isDouleConfig><interfaceName>Ethernet0/1/2</interfaceName></flow></flows>"));
        ////System.out.println(OTFlowDAO.getInstance().get("1", "4", ""));
        //System.out.println(OTFlowDAO.getInstance().control("1", "4", "",MyData.RestType.POST,content));
    }
    
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    

}
