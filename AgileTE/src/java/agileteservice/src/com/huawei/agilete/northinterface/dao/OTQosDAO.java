package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyQos;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTQos;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTQosDAO  implements IOTDao{

    private static OTQosDAO single = null;
    private OpsRestCaller client = null;
    private OTQosDAO(){

    }
    public synchronized  static OTQosDAO getInstance() {
        if (single == null) {  
            single = new OTQosDAO();
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
        OTQos oTQos = new OTQos();
        int level = Integer.parseInt(content);
        //for(int i=0;i<5;i++){
        String bodyPath = oTQos.getOpsMessage(level); // i
        MyQos myQos  =new MyQos(client);
        myQos.body=bodyPath;
        result = myQos.create(null);
        //}
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

    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        OTQos qos = new OTQos();
        String qosLevel[] = qos.qosLevel;
        for(int i=0;i<qosLevel.length;i++){
            List<OTQos> list = getList(qosLevel[i]);
            if(null == list || list.isEmpty()){
                add(String.valueOf(i));
            }
        }
        opsresult.setContent("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><qoss><qos><level>1</level><value>af1</value></qos><qos><level>2</level><value>af2</value></qos><qos><level>3</level><value>af3</value></qos><qos><level>4</level><value>af4</value></qos><qos><level>5</level><value>ef</value></qos></qoss>");
        return opsresult;
    }

    public List<OTQos> getList(String apiPath){
        List<OTQos> list = null;
        MyQos myQos  =new MyQos(client);
        String[] name = null;
        if(!"".equals(apiPath)){
            name = new String[]{apiPath};
        }
        RetRpc opsresult = myQos.get(name);
        if(opsresult.getStatusCode()==200){
            OTQos oTQos = new OTQos();
            list = oTQos.parseOpsToUi(opsresult.getContent());
        }
        return list;
    }



    public static void main(String[] args) {
        //String content = "<tunnels><tunnel><name>Tunnel4</name><interfaceName>Tunnel4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><tunnelPaths><tunnelPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></tunnelPath></tunnelPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></tunnel></tunnels>";
        //OTPolicyDAO.getInstance().get("1", "5", "");
        /*//System.out.println(OTPolicyDAO.getInstance().edit("1", "5", "<policys><policy><name>PolicyTunnel10</name><tpNexthops><tpNexthop><nexthopIPaddr>1.1.1.1</nexthopIPaddr><tpTunnels><tpTunnel><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops></policy></policys>"));*/
    }
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }



}
