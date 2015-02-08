package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyL2vpnVpwsPw extends OpsInterface {
     public String url = "/l2vpn/l2vpnvpws/vpwsSwitchInstances/vpwsSwitchInstance/vpwsPws/vpwsPw";
     public String fullUrl = "/l2vpn/l2vpnvpws/vpwsSwitchInstances/vpwsSwitchInstance?instanceName=%s&instanceType=%s/vpwsPws/vpwsPw";
     public String param="explicitPathName";
     
    public MyL2vpnVpwsPw(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    
    /**
     * @param args
     */
    /*public static void main(String[] args) {
        MyL2vpnVpwsPw e  = new MyL2vpnVpwsPw("1");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<explicitPathName>main113</explicitPathName>            <explicitPathHops>              <explicitPathHop>                <mplsTunnelHopIndex>2222</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>10.4.1.2</mplsTunnelHopIpAddr>              </explicitPathHop>              <explicitPathHop>                <mplsTunnelHopIndex>2</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>10.2.1.2</mplsTunnelHopIpAddr>              </explicitPathHop>              <explicitPathHop>                <mplsTunnelHopIndex>3</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>4.4.4.4</mplsTunnelHopIpAddr>              </explicitPathHop>            </explicitPathHops>";
        String deleteaa = "<explicitPathName>main113</explicitPathName>";
        
        e.body = "<mplsTunnelHopIndex>4</mplsTunnelHopIndex>";
        //System.out.println(e.delete(new String[]{"cc"}));
    }*/

}
