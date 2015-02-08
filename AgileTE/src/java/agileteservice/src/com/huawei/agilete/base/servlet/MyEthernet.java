package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public class MyEthernet extends OpsInterface {
    public String url = "/ethernet/ethernetIfs/ethernetIf";
    public String fullUrl = "/ethernet/ethernetIfs/ethernetIf?ifName=%s";
    public String param="ifName=%s";

    public MyEthernet(IOpsRestCaller restcall){
        
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    /**
     * 
     * @param ifName Ethernet口
     * @param mappingVid 目的vlan
     * @param transVlans 
     * @return
     */
    public RetRpc create(String ifName,int mappingVid,int[] transVlans){
        RetRpc result = null;
        String content = operating(1, ifName, mappingVid, transVlans, null);
        super.body = content;
        result = super.modify(null);
        return result;
    }

    /**
     * 
     * @param ifName Ethernet口
     * @param mappingVid 目的vlan
     * @param transVlans 
     * @return
     */
    public RetRpc modify(String ifName,int mappingVid,int[] transVlans,int[] transVlans_old){
        RetRpc result = null;
        String content = operating(2, ifName, mappingVid, transVlans, transVlans_old);
        super.body = content;
        result = super.modify(null);
        return result;
    }

    public RetRpc delete(String ifName,int mappingVid,int[] transVlans){
        RetRpc result = null;
        String content = operating(3, ifName, mappingVid, transVlans, null);
        super.body = content;
        result = super.modify(null);
        if(200 == result.getStatusCode()){
            StringBuffer content1 = new StringBuffer();
            content1.append("<ifName>").append(ifName).append("</ifName>").append("<l2Enable>disable</l2Enable>");
            super.body = content1.toString();
            result = super.modify(null);
        }
        return result;
    }

    private String operating(int type,String ifName,int mappingVid,int[] transVlans,int[] transVlans_old){
        String transVlansForward = "";
        String[] transVlansForward0 = new String[4096];
        String transVlansBack = "";
        String[] transVlansBack0 = new String[4096];
        for(int i=0;i<4096;i++){
            transVlansBack0[i] = "0";
            transVlansForward0[i] = "0";
        }
        String sign = type == 3?"0":"1";
            for(int i=0;i<transVlans.length;i++){
                transVlansBack0[transVlans[i]] = sign;
                transVlansForward0[transVlans[i]] = "1";
            }
        

        if(null != transVlans_old){
            for(int i=0;i<transVlans_old.length;i++){
                transVlansForward0[transVlans_old[i]] = "1";
            }
        }
        for(int i=0;i<4096;){
            int value = 0;
            String flag = transVlansBack0[i+0]+transVlansBack0[i+1]+transVlansBack0[i+2]+transVlansBack0[i+3];
            value = Integer.parseInt(flag,2);
            transVlansForward += Integer.toHexString(value);

            flag = transVlansForward0[i+0]+transVlansForward0[i+1]+transVlansForward0[i+2]+transVlansForward0[i+3];
            value = Integer.parseInt(flag,2);
            transVlansBack += Integer.toHexString(value);

            i += 4;
        }
        StringBuffer content = new StringBuffer();
        content.append("<ifName>").append(ifName).append("</ifName>");
        if(type == 1){
            content.append("<l2Enable>enable</l2Enable>");
        }

        content.append("<l2Attribute>");
        content.append("<l2MappingPorts>");
        if(type == 1){
            //create
            content.append("<l2MappingPort operation=\"create\">");
        }else {
            content.append("<l2MappingPort operation=\"merge\">");
        }

        content.append("<mappingVid>").append(mappingVid).append("</mappingVid>");
        content.append("<transVlans>").append(transVlansForward).append(":").append(transVlansBack).append("</transVlans>");
        content.append("</l2MappingPort>");
        content.append("</l2MappingPorts>");
        content.append("</l2Attribute>");
        return content.toString();


    }

    /**
     * @param args
     */
    /*public static void main(String[] args) {
        new HttpRequest();
        MyEthernet e  = new MyEthernet("4");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<vlanName>vlan7</vlanName>";
        String deleteaa = "primary";
        e.operating(1," ifName", 10, new int[]{2,1,6}, new int[]{2,1,6});
        //e.body = aa;
        //System.out.println(e.create("Ethernet0/5/3",10,new int[]{2,1,6},new int[]{2,1,5}));
    }*/

}
