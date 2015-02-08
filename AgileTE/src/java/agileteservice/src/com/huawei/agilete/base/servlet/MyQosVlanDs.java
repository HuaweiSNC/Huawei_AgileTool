package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyQosVlanDs extends OpsInterface {
     public String url = "/qos/qosVlanQoss/qosVlanDss/qosVlanDs";
     public String fullUrl = "/qos/qosVlanQoss/qosVlanDss/qosVlanDs?vlanid=%s";
     public String param="vlanid";
     
    public MyQosVlanDs(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
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
