package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.servlet.MyEthernet;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTVlanMapping;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public final class OTVlanMappingDAO   implements IOTDao{
    
    private static OTVlanMappingDAO single = null;
    private OpsRestCaller client = null;
    private OTVlanMappingDAO(){
        
    }
    public synchronized  static OTVlanMappingDAO getInstance() {
         if (single == null) {  
             single = new OTVlanMappingDAO();
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
        OTVlanMapping oTVlanMapping = new OTVlanMapping(content);
        if("".equals(oTVlanMapping.getCheck())){
            MyEthernet myEthernet = new MyEthernet(client);
            int[] transVlans = new int[oTVlanMapping.getInternalVlansNew().split(",").length];
            for(int i=0;i<oTVlanMapping.getInternalVlansNew().split(",").length;i++){
                transVlans[i] = Integer.valueOf(oTVlanMapping.getInternalVlansNew().split(",")[i]);
            }
            result = myEthernet.create(oTVlanMapping.getInterfaceName(),oTVlanMapping.getMappingVid(),transVlans);
        }
        return result;
    }
    
    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        //解析新报文
        OTVlanMapping oTVlanMappingNew = new OTVlanMapping(content);
        if(null != oTVlanMappingNew){
            //获取旧的
            MyEthernet myEthernet = new MyEthernet(client);
            String[] names = null;
            if(!"".equals(oTVlanMappingNew.getInterfaceName())){
                names = new String[]{oTVlanMappingNew.getInterfaceName().replace("/", "%2f")};
            }
            RetRpc opsresult = myEthernet.get(names);
            if(opsresult.getStatusCode()!=200){
                return opsresult;
            }
            OTVlanMapping oTVlanMapping = new OTVlanMapping();
            List<OTVlanMapping> otvlans = oTVlanMapping.parseOpsToUi(opsresult.getContent());
            OTVlanMapping oTVlanMappingOld = null;
            if(null != otvlans && otvlans.size() == 1){
                oTVlanMappingOld = otvlans.get(0);
                
                int[] transVlansNew = new int[oTVlanMappingNew.getInternalVlansNew().split(",").length];
                for(int i=0;i<oTVlanMappingNew.getInternalVlansNew().split(",").length;i++){
                    transVlansNew[i] = Integer.valueOf(oTVlanMappingNew.getInternalVlansNew().split(",")[i]);
                }
                int[] transVlansOld = new int[oTVlanMappingOld.getInternalVlansNew().split(",").length];
                for(int i=0;i<oTVlanMappingOld.getInternalVlansNew().split(",").length;i++){
                    transVlansOld[i] = Integer.valueOf(oTVlanMappingOld.getInternalVlansNew().split(",")[i]);
                }
                
                result = myEthernet.modify(oTVlanMappingNew.getInterfaceName(), oTVlanMappingNew.getMappingVid(), transVlansNew, transVlansOld);
                
                
            }
            
        }
        return result;
    }
    
    public RetRpc del(String apiVlan){
        RetRpc result = new RetRpc();
        
        String flag = get(apiVlan).getContent();
        if(null != flag && !"".equals(flag)){
            OTVlanMapping oTVlanMapping = new OTVlanMapping(flag);
            MyEthernet myEthernet = new MyEthernet(client);
            
            int[] vlans = new int[oTVlanMapping.getInternalVlansNew().split(",").length];
            for(int i=0;i<oTVlanMapping.getInternalVlansNew().split(",").length;i++){
                vlans[i] = Integer.parseInt(oTVlanMapping.getInternalVlansNew().split(",")[i]);
            }
            
            result = myEthernet.delete(oTVlanMapping.getInterfaceName(), oTVlanMapping.getMappingVid(), vlans);
        }
        return result;
    }
    
    public RetRpc get(String apiVlan){
        MyEthernet myEthernet = new MyEthernet(client);
        String[] names = null;
        if(!"".equals(apiVlan)){
            names = new String[]{apiVlan};
        }
        RetRpc opsresult = myEthernet.get(names);
        if(opsresult.getStatusCode()==200){
            OTVlanMapping oTVlanMapping = new OTVlanMapping();
            List<OTVlanMapping> otvlans = oTVlanMapping.parseOpsToUi(opsresult.getContent());
            opsresult.setContent(oTVlanMapping.getUiMessage(otvlans));
        }
        return opsresult;
    }
    
    public static void main(String[] args) {
//        String content = "<Vlans><Vlan><name>Vlan4</name><interfaceName>Vlan4</interfaceName><identifyIndex>502</identifyIndex><ingressIp>4.4.4.4</ingressIp><egressIp>2.2.2.2</egressIp><hotStandbyTime>15</hotStandbyTime><isDouleConfig>false</isDouleConfig><desDeviceName>123</desDeviceName><VlanPaths><VlanPath><pathType>hot_standby</pathType>backup<pathName>path</pathName><lspState>down</lspState></VlanPath></VlanPaths><paths><path><name>path</name><nextHops><nextHop><id>1</id><nextIp>10.1.1.1</nextIp></nextHop></nextHops></path></paths></Vlan></Vlans>";
        //System.out.println(OTVlanMappingDAO.getInstance().get("Ethernet0%2f5%2f3"));
    }
    
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }
    

}
