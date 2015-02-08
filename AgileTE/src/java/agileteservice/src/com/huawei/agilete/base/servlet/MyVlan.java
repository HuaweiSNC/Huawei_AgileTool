package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyVlan extends OpsInterface {
    public String url = "/vlan/vlans/vlan";
    public String fullUrl = "/vlan/vlans/vlan?vlanId=%s";
    public String param="vlanId=%s";

    public MyVlan(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    public RetRpc create(String[] content){
        RetRpc result = null;
//        if(!super.body.contains("<vlanId>")){
//            String allVlan = getall().getContent();
//            int maxId = 1;
//            try {
//                Document doc = DocumentHelper.parseText(allVlan);
//                Element ele = doc.getRootElement();
//                Element vlans = ele.element("vlans");
//                maxId = vlans.elements().size() + 1;
//            } catch (DocumentException e) {
//                //e.printStackTrace();
//            }
//            super.body += "<vlanId>"+maxId+"</vlanId>";
//        }
        result = super.create(content);
        return result;
    }
    public RetRpc createIf(String[] content){
        RetRpc result = null;
//        if(!super.body.contains("<vlanId>")){
//            String allVlan = getall().getContent();
//            int maxId = 1;
//            try {
//                Document doc = DocumentHelper.parseText(allVlan);
//                Element ele = doc.getRootElement();
//                Element vlans = ele.element("vlans");
//                maxId = vlans.elements().size() + 1;
//            } catch (DocumentException e) {
//                //e.printStackTrace();
//            }
//            super.body += "<vlanId>"+maxId+"</vlanId>";
//        }
        super.body += "<vlanif>"+"<cfgBand>500</cfgBand> <dampTime>5</dampTime>"+"</vlanif>";
        result = super.create(content);
        return result;
    }
    
    /**
     * 
     * @param content vlanId,vlanName,Vlanif30
     * @return
     */
    public RetRpc delete(String[] content){
        RetRpc result = new RetRpc();
        //删除if
        MyIfmInterface myIfmInterface = new MyIfmInterface(this.restcall);
        if(2 == content.length){
            result = myIfmInterface.delete((new String[]{content[1]}));
        }
        if(200 == result.getStatusCode()){
            result = super.delete(content);
        }
        return result;
    }

    public RetRpc get(String[] content){
        super.url = "/vlan/vlans/vlan?vlanId,vlanName,vlanDesc&vlanif?ifName";
        return super.get(content);
    }
    
    
    /**
     * @param args
     */
    /*public static void main(String[] args) {
        MyVlan e  = new MyVlan("5");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<vlanName>vlanIf11</vlanName>";
        String deleteaa = "primary";

        e.body = aa;
        //System.out.println(e.createIf(null)(new String[]{"cc"});
    }*/

}
