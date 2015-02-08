package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyVlanMapping1To1;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.MappingPort;
import com.huawei.agilete.northinterface.bean.OTVlanMapping1To1;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTVlanMapping1To1DAO implements IOTDao{

    private static OTVlanMapping1To1DAO single = null;
    private OpsRestCaller client = null;
    private OTVlanMapping1To1DAO(){

    }
    public synchronized  static OTVlanMapping1To1DAO getInstance() {
        if (single == null) {  
            single = new OTVlanMapping1To1DAO();
        }  
        return single;
    }

    public RetRpc control(String domainId, String deviceId, String apiPath, RestType restType,String content){
        RetRpc result = new RetRpc();
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId);
        if(null != apiPath && !"".equals(apiPath)){
            apiPath = apiPath.replace("/", "%2f");
        }

        if(restType.equals(MyData.RestType.GET)){
            result = get(apiPath);
        }else if(restType.equals(MyData.RestType.DELETE)){
            //result = del(apiPath);
            result.setStatusCode(403);
        }else if(restType.equals(MyData.RestType.POST)){
            if("delete".equals(apiPath)){
                result = del(content);
            }else{
                result = add(content);
            }
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else{

        }

        return result;
    }

    public RetRpc add(String content){
        RetRpc result = new RetRpc();
        OTVlanMapping1To1 oTVlanMapping1To1 = new OTVlanMapping1To1(content);
        //        OTVlanMapping1To1.setInterfaceName(OTVlanMapping1To1.getInterfaceName().replace("/", "%2f"));
        if("".equals(oTVlanMapping1To1.getCheck())){
            String createxml = oTVlanMapping1To1.getOTPathOpsMessage();
            createxml = createxml.replace("\t", "").replace("\n", "").replace("\r", "");
            MyVlanMapping1To1 myVlanMapping1To1 = new MyVlanMapping1To1(client);
            myVlanMapping1To1.body = createxml;
            result = myVlanMapping1To1.create(null);
            //            result = myEthernet.modify(content);
        }
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();

        MyVlanMapping1To1 myVlanMapping1To1 = new MyVlanMapping1To1(client);
        //解析新报文
        OTVlanMapping1To1 oTVlanMapping1To1 = new OTVlanMapping1To1(content);
        if("".equals(oTVlanMapping1To1.getCheck())){
            List<MappingPort> mappingPortList = oTVlanMapping1To1.getMappingPortList();
            if(null != mappingPortList && mappingPortList.size() == 1){
                MappingPort mappingPort = mappingPortList.get(0);
                //new 删除报文
                OTVlanMapping1To1 oTVlanMapping1To1Del = new OTVlanMapping1To1();
                oTVlanMapping1To1Del.setInterfaceName(oTVlanMapping1To1.getInterfaceName());
                MappingPort mappingPortDel = new MappingPort();
                mappingPortDel.setMappingVid(mappingPort.getMappingVidOld());
                mappingPortDel.setInternalVlansNew(mappingPort.getInternalVlansOld());
                oTVlanMapping1To1Del.getMappingPortList().add(mappingPortDel);
                String delxml = oTVlanMapping1To1Del.getOTPathOpsMessage();
                delxml = delxml.replace("\t", "").replace("\n", "").replace("\r", "");

                myVlanMapping1To1.body = delxml;
                result = myVlanMapping1To1.delete(null);

                //add
                if(result.getStatusCode()==200){
                    String createxml = oTVlanMapping1To1.getOTPathOpsMessage();
                    createxml = createxml.replace("\t", "").replace("\n", "").replace("\r", "");
                    myVlanMapping1To1.body = createxml;
                    result = myVlanMapping1To1.create(null);
                }
            }
        }
        return result;
    }

    public RetRpc del(String content){
        RetRpc result = new RetRpc();
        if(result.getStatusCode()==200){
            OTVlanMapping1To1 oTVlanMapping1To1 = new OTVlanMapping1To1(content);
            //        OTVlanMapping1To1.setInterfaceName(OTVlanMapping1To1.getInterfaceName().replace("/", "%2f"));
            if("".equals(oTVlanMapping1To1.getCheck())){
                String createxml = oTVlanMapping1To1.getOTPathOpsMessage();
                createxml = createxml.replace("\t", "").replace("\n", "").replace("\r", "");
                MyVlanMapping1To1 myVlanMapping1To1 = new MyVlanMapping1To1(client);
                myVlanMapping1To1.body = createxml;
                result = myVlanMapping1To1.delete(null);
                //            result = myEthernet.modify(content);
            }
        }
        return result;

    }

    public RetRpc get(String apiVlan){
        MyVlanMapping1To1 myEthernet = new MyVlanMapping1To1(client);
        String[] names = null;
        Boolean allEthernet = false;
        if(!"".equals(apiVlan)){
            names = new String[]{apiVlan};
            if("allEthernet".equals(apiVlan)){
                allEthernet = true;
            }
        }
        RetRpc opsresult = myEthernet.get(names);
        if(opsresult.getStatusCode()==200){
            OTVlanMapping1To1 OTVlanMapping1To1 = new OTVlanMapping1To1();
            List<OTVlanMapping1To1> otvlans = OTVlanMapping1To1.parseOpsToUi(opsresult.getContent(),allEthernet);
            opsresult.setContent(OTVlanMapping1To1.getUiMessage(otvlans));
            //System.out.println(opsresult.getContent());
        }
        return opsresult;
    }

    public static void main(String[] args) {
        //        String content = "<Vlans><Vlan><name>Vlan4</name><interfaceName>Vlan4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><VlanPaths><VlanPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></VlanPath></VlanPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></Vlan></Vlans>";
        //System.out.println(OTVlanMapping1To1DAO.getInstance().get("Ethernet0%2f5%2f3"));
    }

    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }


}
