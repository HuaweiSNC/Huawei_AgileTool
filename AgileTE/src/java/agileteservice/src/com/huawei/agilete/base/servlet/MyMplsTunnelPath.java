package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyMplsTunnelPath extends OpsInterface {
     public String url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel/tunnelPaths/tunnelPath";
     public String fullUrl = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel?tunnelName=%s/tunnelPaths/tunnelPath";
     public String param="pathType";
     
    public MyMplsTunnelPath(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    
    /**
     * @param args
     */
    /*public static void main(String[] args) {
        MyMplsTunnelPath e  = new MyMplsTunnelPath("1");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<explicitPathName>main113</explicitPathName>            <explicitPathHops>              <explicitPathHop>                <mplsTunnelHopIndex>2222</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>10.4.1.2</mplsTunnelHopIpAddr>              </explicitPathHop>              <explicitPathHop>                <mplsTunnelHopIndex>2</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>10.2.1.2</mplsTunnelHopIpAddr>              </explicitPathHop>              <explicitPathHop>                <mplsTunnelHopIndex>3</mplsTunnelHopIndex>                <mplsTunnelHopType>includeStrict</mplsTunnelHopType>                <mplsTunnelHopIntType>default</mplsTunnelHopIntType>                <mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType>                <mplsTunnelHopIpAddr>4.4.4.4</mplsTunnelHopIpAddr>              </explicitPathHop>            </explicitPathHops>";
        String deleteaa = "<explicitPathName>main113</explicitPathName>";
        
        e.body = "<mplsTunnelHopIndex>4</mplsTunnelHopIndex>";
        //System.out.println(e.delete(new String[]{"cc"}));
    }*/

}
