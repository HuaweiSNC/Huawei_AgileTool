package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyQosVlanDs;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTQosVlanDs;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTQosVlanDsDAO  implements IOTDao{

    private static OTQosVlanDsDAO single = null;
    private OpsRestCaller client = null;
    private OTQosVlanDsDAO(){

    }
    public synchronized  static OTQosVlanDsDAO getInstance() {
        if (single == null) {  
            single = new OTQosVlanDsDAO();
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
        OTQosVlanDs oTQosVlanDs = new OTQosVlanDs(content);
        String bodyPath = oTQosVlanDs.getOpsMessage();
        MyQosVlanDs myQosVlanDs  =new MyQosVlanDs(client);
        myQosVlanDs.body=bodyPath;
        result = myQosVlanDs.create(null);
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        OTQosVlanDs oTQosVlanDs = new OTQosVlanDs(content);
        String bodyPath = oTQosVlanDs.getOpsMessage();
        MyQosVlanDs myQosVlanDs  =new MyQosVlanDs(client);
        myQosVlanDs.body=bodyPath;
        result = myQosVlanDs.modify(null);
        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        if(null != apiPath && !"".equals(apiPath)){
            MyQosVlanDs myQosVlanDs  =new MyQosVlanDs(client);
            myQosVlanDs.body = apiPath;
            result = myQosVlanDs.delete(null);
        }else{
            result.setStatusCode(500);
            result.setContent("Error!ApiPath is null!");
        }
        return result;
    }

    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        List<OTQosVlanDs> list = getList(apiPath);
        if(null != list){
            OTQosVlanDs oTQosVlanDs = new OTQosVlanDs();
            String flag = oTQosVlanDs.getUiMessage(list);
            opsresult.setContent(flag);
        }else{
            opsresult.setStatusCode(500);
            opsresult.setContent("Error!");
        }
        return opsresult;
    }
    
    public List<OTQosVlanDs> getList(String apiPath){
        List<OTQosVlanDs> list = null;
        MyQosVlanDs myQosVlanDs  =new MyQosVlanDs(client);
        String[] name = null;
        if(!"".equals(apiPath)){
            name = new String[]{apiPath};
        }
        RetRpc opsresult = myQosVlanDs.get(name);
        if(opsresult.getStatusCode()==200){
            OTQosVlanDs oTQosVlanDs = new OTQosVlanDs();
            list = oTQosVlanDs.parseOpsToUi(opsresult.getContent());
        }
        return list;
    }
    
    

    public static void main(String[] args) {
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        //OTQosVlanDsDAO.getInstance().get("1", "5", "");
        /*//System.out.println(OTQosVlanDsDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    


}
