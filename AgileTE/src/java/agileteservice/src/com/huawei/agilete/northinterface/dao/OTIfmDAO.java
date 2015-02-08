package com.huawei.agilete.northinterface.dao;

import java.util.HashMap;
import java.util.List;

import com.huawei.agilete.base.servlet.MyIfmInterface;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTIfm;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTIfmDAO  implements IOTDao{
    
    private static OTIfmDAO single = null;
    private OpsRestCaller client = null;
    
    private OTIfmDAO(){
        
    }
    public synchronized  static OTIfmDAO getInstance() {
         if (single == null) {  
             single = new OTIfmDAO();
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
        }else if(restType.equals(MyData.RestType.POST)){
            result = add(content);
        }else if(restType.equals(MyData.RestType.PUT)){
            result = edit(content);
        }else{
            
        }
        return result;
    }
    
    
    public RetRpc edit(String content) {
        return null;
    }
    public RetRpc del(String apiPath) {
        return null;
    }
    public RetRpc add(String content) {
        return null;
    }

    public RetRpc get(String type){
        RetRpc result = new RetRpc();
        String deviceId = client.deviceId;
        if(null != client.deviceId && !"".equals(deviceId)){
            MyIfmInterface myIfmInterface = new MyIfmInterface(client);
            List<String[]> list = myIfmInterface.getAll(type);
            StringBuffer xml = new StringBuffer();
            xml.append("<ifms id='").append(deviceId).append("'>");
            for(int i=0;i<list.size();i++){
                    xml.append("<ifm>");
                    xml.append("<name>").append(list.get(i)[1]).append("</name>");
                    xml.append("<phyType>").append(list.get(i)[0]).append("</phyType>");
                    xml.append("<ips><ip>");
                    xml.append("<ipAddress>").append(list.get(i)[2]).append("</ipAddress>");
                    xml.append("<subnetMask>").append(list.get(i)[3]).append("</subnetMask>");
                    xml.append("</ip></ips>");
                    xml.append("</ifm>");
            }
            xml.append("</ifms>");
            result.setContent(xml.toString());
        }else{
            result.setStatusCode(500);
            result.setContent("false");
        }
        return result;
    }
    
    public RetRpc get2(String type){
        RetRpc result = new RetRpc();
        String deviceId = client.deviceId;
        if(null != deviceId && !"".equals(deviceId)){
            List<OTIfm> ifmList = getList(deviceId,type);
            OTIfm oTIfm = new OTIfm();
            String xml = oTIfm.getUiMessage(deviceId, ifmList);
            result.setContent(xml.toString());
        }else{
            result.setStatusCode(500);
            result.setContent("false");
        }
        return result;
    }
    
    public List<OTIfm> getList(String deviceId, String type){
        List<OTIfm> ifmList = null;
        if(null != deviceId && !"".equals(deviceId)){
            OpsRestCaller client = null;
            if(null != deviceId && !"".equals(deviceId)){
                client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(""), deviceId);
            }else{
                client = this.client;
            }
            MyIfmInterface myIfmInterface = new MyIfmInterface(client);
            List<String[]> list = myIfmInterface.getAll(type);
            OTIfm oTIfm = new OTIfm();
            ifmList = oTIfm.parseOpsToUi(list);
        }
        return ifmList;
    }
    
    //public static HashMap<String, OTDevice> DevicesData = new HashMap<String, OTDevice>();
    
    public Object[] getByIp(List<OTDevice> deviceList,String ip, String type,HashMap<String, OTDevice> devicesData){
    	Object[] returnObject = null;
        for(int index=0;index<deviceList.size();index++){
            OTDevice oTDevice = deviceList.get(index);
            List<OTIfm> ifms = null ;
            
            if(null != devicesData.get(oTDevice.getId())){
                OTDevice ifm = devicesData.get(oTDevice.getId());
                ifms = ifm.getIfmList();
            }else{
                ifms= OTIfmDAO.getInstance().getList(oTDevice.getId(), type);
                if(null != ifms){
                    oTDevice.setIfmList(ifms);
                    devicesData.put(oTDevice.getId(),oTDevice);
                }
                
            }
            if(null != ifms){
                for(int i =0;i<ifms.size();i++){
                    OTIfm ifm = ifms.get(i);
                    if(ip.equals(ifm.getIpAddress())){
                        return new Object[]{oTDevice,ifm};
                    }
                }
            }
        }
        return returnObject;
    }
    
    
    
    public static void main(String[] args) {
        //System.out.println(OTIfmDAO.getInstance().control("4", "46", "Vlanif", MyData.RestType.GET, ""));
    }
    
    public OpsRestCaller getClient() {
        return client;
    }
    public void setClient(OpsRestCaller client) {
        this.client = client;
    }

}
