package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyVlan;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTVlan;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTVlanDAO   implements IOTDao{

    private static OTVlanDAO single = null;
    private OpsRestCaller client = null;
    private OTVlanDAO(){

    }
    public synchronized  static OTVlanDAO getInstance() {
        if (single == null) {  
            single = new OTVlanDAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            result = del(apiPath);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else{

        }

        return result;
    }

    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        OTVlan oTVlan = new OTVlan(content);
        if("".equals(oTVlan.getCheck())){
            String bodyVlan = oTVlan.getOTVlanOpsMessage();
            MyVlan myVlan = new MyVlan(client);
            myVlan.body =bodyVlan;
//            if("true".equals(oTVlan.getIfName())){
//                result = myVlan.createIf(null);
//            }else{
                result = myVlan.create(null);
//            }
        }
        if(result.getStatusCode()==200){
            result.setContent("true");
        }
        return result;
    }

    public RetRpc edit(String content) {
        RetRpc result = new RetRpc();
        OTVlan oTVlan = new OTVlan(content);
        if("".equals(oTVlan.getCheck())){
            String bodyVlan = oTVlan.getOTVlanOpsMessage();
            MyVlan myVlan = new MyVlan(client);
            myVlan.body =bodyVlan;
//            if("true".equals(oTVlan.getIfName())){
//                result = myVlan.createIf(null);
//            }else{
                result = myVlan.modify(null);
//            }
        }
        if(result.getStatusCode()==200){
            result.setContent("true");
        }
        return result;
    }

    public RetRpc del(String apiPath) {
        RetRpc result = new RetRpc();
        MyVlan myVlan = new MyVlan(client);
        List<OTVlan> list = getList(apiPath);
        if(null != list && list.size() == 1){
            OTVlan oTVlan = list.get(0);
            if(!"".equals(oTVlan.getIfName())){
                result = myVlan.delete(new String[]{oTVlan.getIndex(),oTVlan.getIfName()});
            }else{
                result = myVlan.delete(new String[]{apiPath});
            }
        }
        if(result.getStatusCode()==200){
            result.setContent("true");
        }
        return result;
    }

    public RetRpc get(String apiPath){
        RetRpc result = new RetRpc();
        OTVlan oTVlan = new OTVlan();
        List<OTVlan> otvlans = getList(apiPath);
        String flag = oTVlan.getUiMessage(otvlans);
        result.setContent(flag);
        return result;
    }

    public List<OTVlan> getList(String apiPath){
        List<OTVlan> otvlans = null;
        MyVlan myVlan = new MyVlan(client);
        String[] names = null;
        if(!"".equals(apiPath)){
            names = new String[]{apiPath};
        }
        RetRpc opsresult = myVlan.get(names);
        if(opsresult.getStatusCode()==200){
            OTVlan oTVlan = new OTVlan();
            otvlans = oTVlan.parseOpsToUiGetQos(opsresult.getContent(),client);
        }
        return otvlans;
    }

    /*public static void main(String[] args) {
        String content = "<Vlans><Vlan><name>Vlan4</name><interfaceName>Vlan4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><VlanPaths><VlanPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></VlanPath></VlanPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></Vlan></Vlans>";
        //OTVlanDAO.getInstance().control("1", "5", "");
    }*/

    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }


}
