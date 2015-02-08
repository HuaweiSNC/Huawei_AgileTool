package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyOspfv2 extends OpsInterface {
     public String url = "/ospfv2/ospfv2comm/ospfSites/ospfSite/ospfMplsTeStatistics/ospfMplsTeStatistic/linkTlvDatas/linkTlvData";
     public String fullUrl = "";
     public String param="";
     
    public MyOspfv2(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }
    
    public RetRpc get(String[] content){
        if(null == content){
            //super.url = "/ospfv2/ospfv2comm/ospfSites/ospfSite/ospfMplsTeStatistics/ospfMplsTeStatistic/linkTlvDatas/linkTlvData?linkId&localIps/localIp?localIp";
        }
        
        return super.get(content);
        
    }


    
    /**
     * @param args
     */
/*    public static void main(String[] args) {
        new HttpRequest();
        MyTunnelPolicy e  = new MyTunnelPolicy("2");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<tnlPolicyName>PolicyTest1</tnlPolicyName><description></description><tnlPolicyType>tnlBinding</tnlPolicyType><tpNexthops><tpNexthop operation=\"merge\"><nexthopIPaddr>4.4.4.4</nexthopIPaddr><downSwitch>false</downSwitch><ignoreDestCheck>false</ignoreDestCheck><tpTunnels><tpTunnel operation=\"merge\"><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops>";
        String deleteaa = "PolicyTest1";
        
        e.body = deleteaa;
        //System.out.println(e.delete(null)(new String[]{"cc"});
    }*/

}
