package com.huawei.agilete.northinterface.bean;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.huawei.networkos.templet.VMTempletManager;

public class OTIfm {




    private String name = "";
    private String phyType = "";
    private String ipAddress = "";
    private String subnetMask = "";

    private String check = "";

    public OTIfm(){

    }


    public List<OTIfm> parseOpsToUi(List<String[]> content){
        List<OTIfm> list = new ArrayList<OTIfm>();
        for(int i=0;i<content.size();i++){
            String[] flag= content.get(i);
            OTIfm oTIfm = new OTIfm();
            if(null != flag[1]){
                oTIfm.setName(flag[1]);
            }
            if(null != flag[0]){
                oTIfm.setPhyType(flag[0]);
            }
            if(null != flag[2]){
                oTIfm.setIpAddress(flag[2]);
            }
            if(null != flag[3]){
                oTIfm.setSubnetMask(flag[3]);
            }
            list.add(oTIfm);
        }
        return list;
    }

    /**
     * @param interfaceIs 是显示instanceName，1显示，0不
     * @return
     */
    public String getUiMessage(String deviceId,List<OTIfm> list){
        String result = "";
        VMTempletManager templet = VMTempletManager.getInstance();
        Map<String, Object> mapContext = new LinkedHashMap<String, Object>();
        mapContext.put("T_Ifm", list);
        mapContext.put("t_deviceId", deviceId);
        String templetPath = templet.getResource("/templet/agilete")    ;
        StringWriter write = templet.process("UiTempleIfm.tpl", mapContext, templetPath);
        result = write.toString();
        return result;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhyType() {
        return phyType;
    }
    public void setPhyType(String phyType) {
        this.phyType = phyType;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getSubnetMask() {
        return subnetMask;
    }
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }




}
