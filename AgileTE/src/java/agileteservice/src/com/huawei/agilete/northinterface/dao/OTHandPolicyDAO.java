package com.huawei.agilete.northinterface.dao;

import com.huawei.agilete.base.servlet.MyHandPolicy;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTHandPolicy;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTHandPolicyDAO  implements IOTDao{

    private static OTHandPolicyDAO single = null;
    private OpsRestCaller client = null;
    private OTHandPolicyDAO(){

    }
    public synchronized  static OTHandPolicyDAO getInstance() {
        if (single == null) {  
            single = new OTHandPolicyDAO();
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
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result  = edit(content);
        }else{

        }

        return result;
    }
    
    
    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        result.setStatusCode(404);
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        OTHandPolicy oTPolicy = new OTHandPolicy(content);
        String bodyPath = oTPolicy.getOpsMessage();
        MyHandPolicy myTunnelPolicy  =new MyHandPolicy(client);
        myTunnelPolicy.body=bodyPath;
        String[] con = {oTPolicy.getTnlPolicyName(),oTPolicy.getNexthopIPaddr()};
        result = myTunnelPolicy.modify(con);
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
        //OTHandPolicyDAO.getInstance().get("1", "5", "");
        /*//System.out.println(OTHandPolicyDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    


}
