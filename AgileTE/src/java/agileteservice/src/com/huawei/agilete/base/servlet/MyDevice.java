package com.huawei.agilete.base.servlet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyDevice extends OpsInterface {
    public String url = "";
    public String fullUrl = "%s";
    public String param="";

    public MyDevice(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }


    public RetRpc create(String[] content){
        return null;
    }
    public RetRpc create(String deviceName){
        RetRpc flag = super.create(null);
        if(202 == flag.getStatusCode() || 200 == flag.getStatusCode()){
            RetRpc all = super.getall();
            String deviceId = getDeviceId(deviceName, all.getContent());
            flag.setContent(deviceId);
            flag.setStatusCode(200);
        }
        return flag;
    }

    private String getDeviceId(String deviceName,String contentJson) {
        JSONObject jb = JSONObject.fromObject(contentJson);
        JSONArray ja = jb.getJSONArray("devices");
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jb1 = JSONObject.fromObject(ja.getJSONObject(i).getString("device"));
            if(deviceName.equals(jb1.getString("devicename"))){
                return jb1.getString("id");
            }
        }
        return "";
    }




    /**
     * @param args
     */
/*    public static void main(String[] args) {
        new HttpRequest();
        MyDevice e  = new MyDevice("");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "{\"device\":{\"devicename\": \"rt50\",\"version\": \"V100R003\",\"connectstatus\": \"['normal', 'normal']\",\"productType\": \"CE5800\",\"passwd\": \"\",\"ip\": \"10.137.130.139\",\"username\": \"root@123\"}}";

        e.body = aa;
        //System.out.println(e.create("rt50"));
        String a ="{\"devices\": [{\"device\": {\"status\": \"normal\", \"devicename\": \"rt5\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.139\", \"username\": \"root@123\", \"id\": 11}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"rt4\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.123\", \"username\": \"root@123\", \"id\": 10}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"rt6\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.139\", \"username\": \"root@123\", \"id\": 12}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"testcy\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"189.32.95.10\", \"username\": \"netconf\", \"id\": 4}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"rt1\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.68\", \"username\": \"root@123\", \"id\": 7}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"testdevice2\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.222\", \"username\": \"root@1234\", \"id\": 6}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"rt3\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.131\", \"username\": \"root@123\", \"id\": 9}}, {\"device\": {\"status\": \"normal\", \"devicename\": \"rt2\", \"version\": \"V100R003\", \"connectstatus\": \"['normal', 'normal']\", \"productType\": \"CE5800\", \"passwd\": \"\", \"ip\": \"10.137.130.113\", \"username\": \"root@123\", \"id\": 8}}]}";
        //System.out.println(e.getDeviceId("rt5",a));

    }*/

}
