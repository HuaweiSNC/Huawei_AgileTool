package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.base.servlet.MyMainStandby;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTMainStandby;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTMainStandbyDAO  implements IOTDao{

    private static OTMainStandbyDAO single = null;
    private OpsRestCaller client = null;
    private OTMainStandbyDAO(){

    }
    public synchronized  static OTMainStandbyDAO getInstance() {
        if (single == null) {  
            single = new OTMainStandbyDAO();
        }  
        return single;
    }

     public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        result.setStatusCode(404);
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else{

        }

        return result;
    }
    
    
    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        result.setStatusCode(404);
        return result;
    }

    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        OTMainStandby oTMainStandby = new OTMainStandby(content);
        String body = oTMainStandby.getOpsMessage();
        if(!(body==null || body.equals(""))){
            MyMainStandby myMainStandby = new MyMainStandby(client);
            myMainStandby.body = body;
            result = myMainStandby.create(null);
        }else{
            result.setStatusCode(500);
            result.setContent("false");
        }
        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        result.setStatusCode(404);
        return result;
    }

    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        opsresult.setStatusCode(404);
        return opsresult;
    }
    
    

    public static void main(String[] args) {
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        //OTMainStandbyDAO.getInstance().get("1", "5", "");
        /*//System.out.println(OTMainStandbyDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    


}
