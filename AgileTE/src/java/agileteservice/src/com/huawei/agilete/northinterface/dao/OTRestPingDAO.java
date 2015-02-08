package com.huawei.agilete.northinterface.dao;

import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.servlet.MyRestPing;
import com.huawei.agilete.data.MyData;
import com.huawei.agilete.data.MyData.RestType;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.agilete.northinterface.bean.OTIfm;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public class OTRestPingDAO  implements IOTDao{

    private static OTRestPingDAO single = null;
    private OpsRestCaller client = null;
    private OTRestPingDAO(){

    }
    public synchronized  static OTRestPingDAO getInstance() {
        if (single == null) {  
            single = new OTRestPingDAO();
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
        result.setContent("No page!");
        result.setStatusCode(404);
        return result;
    }

    public RetRpc edit(String content){
        RetRpc result = new RetRpc();
        result.setContent("No page!");
        result.setStatusCode(404);
        return result;
    }

    public RetRpc del(String apiPath){
        RetRpc result = new RetRpc();
        result.setContent("No page!");
        result.setStatusCode(404);
        return result;
    }

    public RetRpc get(String apiPath){
        RetRpc opsresult = new RetRpc();
        MyRestPing myRestPing  = new MyRestPing(client);
        opsresult = myRestPing.get(null);
        return opsresult;
    }
    
    public void pingDevicesTest(){
        DBAction db = new DBAction();
        List<String[]> list = db.getAll("devices","devices");
        for(int i=0;i<list.size();i++){
            String id = list.get(i)[0];
            if(id.matches("^[0-9]*$")){
                PDevices pDevices = new PDevices();
                pDevices.deviceId = id;
                Thread t = new Thread(pDevices);
                t.start();
            }
        }
    }
    
    class PDevices implements Runnable{
        public String deviceId = "";
        @Override
        public void run() {
            RetRpc r = new RetRpc();
            for(int i = 0;i<3;i++){
                r = pingDevice(deviceId);
                MyData.AllDevices.put(deviceId, r.getStatusCode());
                if(200 == r.getStatusCode()){
                     RetRpc ifms= OTIfmDAO.getInstance().control("", deviceId, "Vlanif", MyData.RestType.GET, "");
                     if(200 ==ifms.getStatusCode()){
                         OTDevice device = new OTDevice();
                         device.setId(deviceId);
                         device.setIfm(ifms.getContent());
                         List<OTIfm> list = OTIfmDAO.getInstance().getList(deviceId, "Vlanif");
                         device.setIfmList(list);
                         MyData.DevicesData.put(deviceId, device);
                         break;
                     }else{
                         MyData.AllDevices.put(deviceId, r.getStatusCode());
                     }
                }else{
                    try {
                        Thread.sleep(1000*(i+1));
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }
    
    
    
    public RetRpc pingDevice(String deviceId){
        RetRpc opsresult = new RetRpc();
        opsresult = control("", deviceId, "", MyData.RestType.GET, "");
        MyData.AllDevices.put(deviceId, opsresult.getStatusCode());
        return opsresult;    
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
