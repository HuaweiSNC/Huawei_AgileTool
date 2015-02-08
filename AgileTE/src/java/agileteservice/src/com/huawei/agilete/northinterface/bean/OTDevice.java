package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.huawei.networkos.ops.IOpsDevice;
import com.huawei.networkos.templet.VMTempletManager;

public class OTDevice implements IOpsDevice {


    private String deviceName = "";
    private String ipAddress = "";
    private String deviceTopoIp = "";
    private String userName = "";
    private String passwd = "";
    private String version = "";
    private String type = "";
    private String id = "";
    private int state = 0;
    private List<OTIfm> ifmList = new ArrayList<OTIfm>();    
    private String ifm = "";
    private int port;

    private int subdevicePort;
    private String subdeviceid;
    
    private String check = "";

    public OTDevice(){

    } 

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public OTDevice(String content){
        try {
            Document doc = DocumentHelper.parseText(content);
            Element el = doc.getRootElement();
            if("devices".equals(el.getName())){
                for(Iterator<Element> i=el.elementIterator();i.hasNext();){
                    Element domain = i.next();
                    if(null != domain.elementText("id") && !"".equals(domain.elementText("id"))){
                        setId(domain.elementText("id"));
                    }
                    if(null != domain.elementText("deviceName") && !"".equals(domain.elementText("deviceName"))){
                        setDeviceName(domain.elementText("deviceName"));
                    }
                    if(null != domain.elementText("ipAddress") && !"".equals(domain.elementText("ipAddress"))){
                        setIpAddress(domain.elementText("ipAddress"));
                    }
                    if(null != domain.elementText("userName") && !"".equals(domain.elementText("userName"))){
                        setUserName(domain.elementText("userName"));
                    }
                    if(null != domain.elementText("passwd") && !"".equals(domain.elementText("passwd"))){
                        setPasswd(domain.elementText("passwd"));
                    }
                    if(null != domain.elementText("version") && !"".equals(domain.elementText("version"))){
                        setVersion(domain.elementText("version"));
                    }
                    if(null != domain.elementText("type") && !"".equals(domain.elementText("type"))){
                        setType(domain.elementText("type"));
                    }
                    if(null != domain.elementText("deviceTopoIp") && !"".equals(domain.elementText("deviceTopoIp"))){
                        setDeviceTopoIp(domain.elementText("deviceTopoIp"));
                    }
                    if(null != domain.elementText("subdeviceid") && !"".equals(domain.elementText("subdeviceid"))){
                    	setSubdeviceid(domain.elementText("subdeviceid"));
                    }
                }
            }else if("device".equals(el.getName())){
                if(null != el.elementText("id") && !"".equals(el.elementText("id"))){
                    setId(el.elementText("id"));
                }
                if(null != el.elementText("deviceName") && !"".equals(el.elementText("deviceName"))){
                    setDeviceName(el.elementText("deviceName"));
                }
                if(null != el.elementText("ipAddress") && !"".equals(el.elementText("ipAddress"))){
                    setIpAddress(el.elementText("ipAddress"));
                }
                if(null != el.elementText("userName") && !"".equals(el.elementText("userName"))){
                    setUserName(el.elementText("userName"));
                }
                if(null != el.elementText("passwd") && !"".equals(el.elementText("passwd"))){
                    setPasswd(el.elementText("passwd"));
                }
                if(null != el.elementText("version") && !"".equals(el.elementText("version"))){
                    setVersion(el.elementText("version"));
                }
                if(null != el.elementText("type") && !"".equals(el.elementText("type"))){
                    setType(el.elementText("type"));
                }
                if(null != el.elementText("deviceTopoIp") && !"".equals(el.elementText("deviceTopoIp"))){
                    setDeviceTopoIp(el.elementText("deviceTopoIp"));
                }
                if(null != el.elementText("subdeviceid") && !"".equals(el.elementText("subdeviceid"))){
                	setSubdeviceid(el.elementText("subdeviceid"));
                }
            }else{

                return;
            }
        } catch (DocumentException e) {
            //e.printStackTrace();
        }
    }

    public String getOTDevice(List <OTDevice> oTDeviceList,String type){
        String result = "";
        Boolean flag = false;
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = null;
        mapContext = new LinkedHashMap<String, Object>();
        if(null == oTDeviceList){
            oTDeviceList = new ArrayList<OTDevice>();
            oTDeviceList.add(this);
            flag = true;
        }
        mapContext.put("T_Device", oTDeviceList);
        mapContext.put("t_type", type);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("TempleDeviceX.tpl", mapContext, templetPath);
        result = write.toString();
        if(flag){
            result = result.replace("<devices>", "").replace("</devices>", "");
        }
        return result;
    }

    public List <OTDevice> getOTDeviceList(String devicesJson){
        List <OTDevice> oTDeviceList = new ArrayList<OTDevice>();
        if(null == devicesJson || "".equals(devicesJson) || "null".equals(devicesJson)){
            return oTDeviceList;
        }
        
        JSONObject jb = JSONObject.fromObject(devicesJson);
        try{
            JSONArray ja = jb.getJSONArray("devices");
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jb1 = JSONObject.fromObject(ja.getJSONObject(i).getString("device"));
                OTDevice oTDevice = new OTDevice();
                oTDevice.setId(jb1.getString("id"));
                oTDevice.setDeviceName(jb1.getString("devicename"));
//                oTDevice.setDeviceTopoIp("");
                oTDevice.setUserName(jb1.getString("username"));
                oTDevice.setPasswd("");
                oTDevice.setVersion(jb1.getString("version"));
                oTDevice.setType(jb1.getString("productType"));
                if(jb1.containsKey("subdevices")){
	                JSONArray subdevices = jb1.getJSONArray("subdevices");
	                for (int x = 0; x < subdevices.size(); x++) {
	                	JSONObject subdevice = subdevices.getJSONObject(x);
	                    oTDevice.setDeviceTopoIp(subdevice.getString("ip"));
	                    oTDevice.setSubdevicePort(Integer.parseInt((subdevice.getString("port"))));
	                    oTDevice.setSubdeviceid((subdevice.getString("id")));
	                    break;
	                }
                }
                if("normal".equals(jb1.getString("status"))){
                    oTDevice.setState(200);
                }else{
                    oTDevice.setState(500);
                }
                oTDeviceList.add(oTDevice);
            }
        }catch(Exception e){
                JSONObject jb1 = JSONObject.fromObject(jb.getString("device"));
                OTDevice oTDevice = new OTDevice();
                oTDevice.setId(jb1.getString("id"));
                oTDevice.setDeviceName(jb1.getString("devicename"));
                oTDevice.setDeviceTopoIp("");
                oTDevice.setUserName(jb1.getString("username"));
                oTDevice.setPasswd("");
                oTDevice.setVersion(jb1.getString("version"));
                oTDevice.setType(jb1.getString("productType"));
                if(jb1.containsKey("subdevices")){
	                JSONArray subdevices = jb1.getJSONArray("subdevices");
	                for (int x = 0; x < subdevices.size(); x++) {
	                	JSONObject subdevice = subdevices.getJSONObject(x);
	                    oTDevice.setDeviceTopoIp(subdevice.getString("ip"));
	                    oTDevice.setSubdevicePort(Integer.parseInt((subdevice.getString("port"))));
	                    oTDevice.setSubdeviceid((subdevice.getString("id")));
	                    break;
	                }
                }
                if("normal".equals(jb1.getString("status"))){
                    oTDevice.setState(200);
                }else{
                    oTDevice.setState(500);
                }
                oTDeviceList.add(oTDevice);
        }
        return oTDeviceList;
    }
    public String getOTDevice(String devicesJson){
        List <OTDevice> oTDeviceList = getOTDeviceList(devicesJson);
        String result = getOTDevice(oTDeviceList, "xml");
        return result;
    }

    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPasswd() {
        return passwd;
    }
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public String getDeviceTopoIp() {
        return deviceTopoIp;
    }
    public void setDeviceTopoIp(String deviceTopoIp) {
        this.deviceTopoIp = deviceTopoIp;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public String getIfm() {
        return ifm;
    }
    public void setIfm(String ifm) {
        this.ifm = ifm;
    }
    public List<OTIfm> getIfmList() {
        return ifmList;
    }
    public void setIfmList(List<OTIfm> ifmList) {
        this.ifmList = ifmList;
    }

	public int getSubdevicePort() {
		return subdevicePort;
	}

	public void setSubdevicePort(int subdevicePort) {
		this.subdevicePort = subdevicePort;
	}

	public String getSubdeviceid() {
		return subdeviceid;
	}

	public void setSubdeviceid(String subdeviceid) {
		this.subdeviceid = subdeviceid;
	}








}
