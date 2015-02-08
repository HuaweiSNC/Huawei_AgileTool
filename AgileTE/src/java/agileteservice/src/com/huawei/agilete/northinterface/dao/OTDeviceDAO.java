package com.huawei.agilete.northinterface.dao;

import java.util.ArrayList;
import java.util.List;

import com.huawei.agilete.base.action.DBAction;
import com.huawei.agilete.base.common.MyIO;
import com.huawei.agilete.base.servlet.MyDevice;
import com.huawei.agilete.northinterface.action.AgileSysMAction;
import com.huawei.agilete.northinterface.action.AgileSysMBean;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public final class OTDeviceDAO {
    
    

    private static OTDeviceDAO single = null;
    private OpsRestCaller client = null;
    private OTDeviceDAO(){

    }
    public synchronized  static OTDeviceDAO getInstance() {
        if (single == null) {  
            single = new OTDeviceDAO();
        }  
        return single;
    }
    

    public RetRpc add(String domainId,String content){
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), "");
        
        RetRpc result = new RetRpc();
        content = MyIO.characterFormat(content);
        OTDevice oTDevice = new OTDevice(content);
        if(!"".equals(oTDevice.getCheck())){
            result.setStatusCode(500);
            return result;
        }
        String deviceId = "";
        //发送ops
        if(200 == result.getStatusCode()){
//            MyDevice myDevice = new MyDevice(client);
//            myDevice.body = oTDevice.getOTDevice(null, "json");
//            result = myDevice.create(oTDevice.getDeviceName());
//            if(200 == result.getStatusCode()){
//                deviceId = result.getContent();
//            }
            /*
             * 修改访问SYSM  SNC创建设备
             */
            AgileSysMAction agileSysMAction = new AgileSysMAction();
//            if("CE12800".equalsIgnoreCase(oTDevice.getType())){
            result = agileSysMAction.post(AgileSysMBean.SYSM_DEVICE_SNC_URL,content);
//            }
            /*
             * 
             */
            if(result.getStatusCode()==200||result.getStatusCode()==202){
                /*
                 * 修改访问SYSM  ops创建设备
                 */
                result = agileSysMAction.post(AgileSysMBean.SYSM_DEVICE_URL,content);
                if(200 == result.getStatusCode()){
                    deviceId = result.getContent();
                }else{
                	if("CE12800".equalsIgnoreCase(oTDevice.getType())){
                		agileSysMAction.delete(AgileSysMBean.SYSM_DEVICE_SNC_URL+"/"+"__"+oTDevice.getDeviceName());
                	}
                }
            }
        }

        if(null !=deviceId && !"".equals(deviceId) && deviceId.matches("^[0-9]*$")){
        	
        	MyDevice myDevice_ = new MyDevice(new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(domainId), deviceId));
            OTDevice oTDevice_ = new OTDevice();
            List <OTDevice> oTDeviceList = oTDevice_.getOTDeviceList(myDevice_.get(null).getContent());
            if(oTDeviceList.size()>0){
            	oTDevice.setSubdeviceid(oTDeviceList.get(0).getSubdeviceid());
            	oTDevice.setSubdevicePort(oTDeviceList.get(0).getSubdevicePort());
            }
            
            DBAction db = new DBAction();
            oTDevice.setId(deviceId);
            Boolean flag = db.insert("devices","devices", deviceId, oTDevice.getOTDevice(null, "xml"));
            
            
            if(flag && null !=domainId && !"".equals(domainId)){
                String domain = db.get("domains","domains",domainId);
                if(null !=domain && !"".equals(domain)){
                    StringBuffer newDevice = new StringBuffer();
                    newDevice.append("<toponodes>");
                    newDevice.append("<toponode><nodeID>");
                    newDevice.append(deviceId);
                    newDevice.append("</nodeID><show>true</show></toponode>");
                    domain = domain.replaceFirst("<toponodes>", newDevice.toString());
                    Boolean flagDb = db.insert("domains","domains", domainId, domain);
                    if(flagDb){
                        result.setStatusCode(200);
                        result.setContent(deviceId);
                    }else{
                        del(deviceId);
                        result.setStatusCode(500);
                        result.setContent("ops done! But write db error!");
                    }
                }else{
                    result.setStatusCode(500);
                    result.setContent("ops done! But read db error!");
                }
            }
        }else{
            result.setStatusCode(500);
            result.setContent("ops error!");
        }
        return result;
    }

    public RetRpc del(String deviceId){
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(""), deviceId);
        RetRpc result = new RetRpc();
        if(null != deviceId && !"".equals(deviceId)){
            AgileSysMAction agileSysMAction = new AgileSysMAction();
            /*
             * 修改访问SYSM  SNC删除设备
             */
            result = agileSysMAction.delete(AgileSysMBean.SYSM_DEVICE_SNC_URL+"/"+deviceId);
            /*
             * 
             */
            
            
//            MyDevice myDevice = new MyDevice(client);
//            result = myDevice.delete(null);
            /*
             * 修改访问SYSM
             */
            if(result.getStatusCode()==200||result.getStatusCode()==202){
                result = agileSysMAction.delete(AgileSysMBean.SYSM_DEVICE_URL+"/"+deviceId);
                
                
                if(true){
                    DBAction db = new DBAction();
                    Boolean flag = db.delete("devices", "devices", deviceId);
                    if(!flag){
                        result.setStatusCode(500);
                        result.setContent("db error!");
                    }else{
                        result.setContent("true");
                        //删除包含该设备的管理域
                        List<String[]> list = db.getAll("domains","domains");
                        for(int i=0;i<list.size();i++){
                            String domain = list.get(i)[1];
                            domain = MyIO.characterFormat(domain);
                            domain = domain.replace("<toponode><nodeID>"+deviceId+"</nodeID><show>true</show></toponode>", "");
                            db.insert("domains","domains", list.get(i)[0], domain);
                        }
                    }
                }
            }
        }
        return result;
    }


    public RetRpc edit(String content){
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(""), "");
        
        RetRpc result = new RetRpc();
        content = MyIO.characterFormat(content);
        OTDevice oTDevice = new OTDevice(content);
        if(!"".equals(oTDevice.getCheck())){
            result.setStatusCode(500);
            result.setContent("error!");
            return result;
        }
        AgileSysMAction agileSysMAction = new AgileSysMAction();
        if("CE12800".equalsIgnoreCase(oTDevice.getType())){
    	   result = agileSysMAction.put(AgileSysMBean.SYSM_DEVICE_SNC_URL,content);
        }
        //发送ops//////////////////////////////////////////////////////////////////////////////////
        if(200 == result.getStatusCode()){
//            MyDevice myDevice = new MyDevice(client);
//            myDevice.body = oTDevice.getOTDevice(null, "json");
//            result = myDevice.modify(null);
            /*
             * 修改访问SYSM
             */
            
            result = agileSysMAction.put(AgileSysMBean.SYSM_DEVICE_URL,content);
        }

        if(200 == result.getStatusCode()){
	        result.setContent("true");
	        DBAction db = new DBAction();
	        Boolean flag = db.insert("devices","devices", oTDevice.getId(), oTDevice.getOTDevice(null, "xml"));
	        if(!flag){
	            result.setStatusCode(500);
	            result.setContent("db error!");
	        }
        }

        return result;
    }
    public RetRpc get(String deviceId,Boolean orDomain){
        client = new OpsRestCaller(OTDomainDAO.getInstance().getOpsServer(""), deviceId);
        RetRpc result = new RetRpc();

        if(orDomain){
            DBAction db = new DBAction();
            List<String[]> list = db.getAll("devices","devices");
            StringBuffer xml = new StringBuffer();
            xml.append("<devices>");
            for(int i=0;i<list.size();i++){
                String device = list.get(i)[1];
                if(device.contains("<passwd>")){
                    device = device.replace(device.substring(device.indexOf("<passwd>")+"<passwd>".length(), device.indexOf("</passwd>")), "");
                }
                device = device.replace("<devices>", "");
                device = device.replace("</devices>", "");
                xml.append(device);
            }
            xml.append("</devices>");
            result.setContent(xml.toString());
        }else{
            if("".equals(deviceId)){
                MyDevice myDevice = new MyDevice(client);
                OTDevice oTDevice = new OTDevice();
                result.setContent(oTDevice.getOTDevice(myDevice.get(null).getContent()));
            }else{
                DBAction db = new DBAction();
                String flag = "";
                flag = db.get("devices","devices", deviceId);
                StringBuffer xml = new StringBuffer();
                if(null != flag && !"".equals(flag)){
                    if(flag.contains("<passwd>")){
                        flag = flag.replace(flag.substring(flag.indexOf("<passwd>")+"<passwd>".length(), flag.indexOf("</passwd>")), "");
                    }
                    int stateCode = 0;
                    MyDevice myDevice = new MyDevice(client);
                    RetRpc rdevice = myDevice.get(null);
                    OTDevice oTDevice = new OTDevice();
                    List<OTDevice> oTDeviceList = oTDevice.getOTDeviceList(rdevice.getContent());
                    if(null != oTDeviceList && oTDeviceList.size() == 1){
                        OTDevice d = oTDeviceList.get(0);
                        stateCode = d.getState();
                    }
//                    if(null != MyData.AllDevices.get(deviceId)){
//                        stateCode = MyData.AllDevices.get(deviceId);
//                    }else{
//                        OTRestPingDAO oTRestPingDAO =OTRestPingDAO.getInstance();
//                        oTRestPingDAO.setClient(client);
//                        RetRpc r =oTRestPingDAO.get("");
//                        stateCode = r.getStatusCode();
//                    }
                    String ifms = "<ifms></ifms>";
//                    if(200 == stateCode){
//                        if(null != MyData.DevicesData.get(deviceId)){
//                            OTDevice ifm = MyData.DevicesData.get(deviceId);
//                            ifms = ifm.getIfm();
//                        }else{
//                            RetRpc ifmRetRpc= OTIfmDAO.getInstance().control("", deviceId, "Vlanif", MyData.RestType.GET, "");
//                            if(200 ==ifmRetRpc.getStatusCode()){
//                                OTDevice device = new OTDevice();
//                                device.setId(deviceId);
//                                device.setIfm(ifmRetRpc.getContent());
//                                ifms = ifmRetRpc.getContent();
//                                MyData.DevicesData.put(deviceId, device);
//                            }else{
//                                MyData.AllDevices.put(deviceId, ifmRetRpc.getStatusCode());
//                            }
//                        }
//                    } else {
//                        //System.out.println(ifms);
//                    }
                      
                    StringBuffer state = new StringBuffer();
                    ////System.out.println(ifms);
                    state.append("</passwd>").append("<state>").append(stateCode).append("</state>").append(ifms);
                    flag = flag.replace("</passwd>", state);
                    xml.append("<devices>");
                    xml.append(flag);
                    
                    xml.append("</devices>");
                }
                result.setContent(xml.toString());
            }
        }

        return result;
    }



    public List<OTDevice> getByDomain(String domainId){
        List<OTDevice> list = new ArrayList<OTDevice>();
        DBAction db = new DBAction();
        String flag = db.get("domains","domains",domainId);
        if(null != flag && !"".equals(flag)){
            if(flag.contains("<toponodes>")){
                String domainDevices = flag.substring(flag.indexOf("<toponodes>")+"<toponodes>".length(), flag.indexOf("</toponodes>"));
                int i = 0;
                while(-1 != domainDevices.indexOf("<nodeID>",i)){
                    String deviceId = domainDevices.substring(domainDevices.indexOf("<nodeID>",i)+"<nodeID>".length(), domainDevices.indexOf("</nodeID>",i));
                    i = domainDevices.indexOf("</nodeID>",i)+"</nodeID>".length();
                    String device = db.get("devices","devices", deviceId);
                    list.add(new OTDevice(device));
                }
            }
        }
        return list;
    }
    
    public List<OTDevice> getAll(){
        List<OTDevice> list = new ArrayList<OTDevice>();
        DBAction db = new DBAction();
        List<String[]> flag = db.getAll("devices","devices");
        if(null != flag && !"".equals(flag)){
            for(int i =0 ;i<flag.size();i++){
                list.add(new OTDevice(flag.get(i)[1]));
            }
        }
        return list;
    }



    public static void main(String[] args) {
        //OTDeviceDAO.getInstance().add("1", "<devices><device><deviceName>Devicename_17</deviceName> <ipAddress>10.137.130.123</ipAddress><userName>root@123</userName><passwd>Admin@123</passwd><version>V100R003</version><type>CE5800</type><id>1</id></device></devices>");

        ////System.out.println(OTDeviceDAO.getInstance().del("10"));
        ////System.out.println(OTDeviceDAO.getInstance().get("",true));
        //List list = OTDeviceDAO.getInstance().getByDomain("1");
//        //System.out.println();
//        DBAction db = new DBAction();
//        Boolean flag = db.delete("devices", "devices", "${deviceId}");
        
    }


}
