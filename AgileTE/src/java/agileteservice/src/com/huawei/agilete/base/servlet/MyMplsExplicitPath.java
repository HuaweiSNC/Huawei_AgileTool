package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyMplsExplicitPath extends OpsInterface {
     public String url = "/mpls/mplsTe/explicitPaths/explicitPath";
     public String fullUrl = "/mpls/mplsTe/explicitPaths/explicitPath?explicitPathName=%s";
     public String param="explicitPathName";
     
     
    public MyMplsExplicitPath(IOpsRestCaller restcall) {
        
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

//    用户自己建立反向path
//    public Boolean createbyFull(){
//        String bodyForward = super.body;
//        String backwards = "";
//        super.create(null);
//        return null;
//    }
    
    

    
    /**
     * @param args
     *//*
    public static void main(String[] args) {
        new HttpRequest();
        MyMplsExplicitPath e  = new MyMplsExplicitPath("5");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
            String aa = "<explicitPathName>main</explicitPathName><explicitPathHops><explicitPathHop><mplsTunnelHopIndex>1</mplsTunnelHopIndex><mplsTunnelHopType>includeStrict</mplsTunnelHopType><mplsTunnelHopIntType>default</mplsTunnelHopIntType><mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType><mplsTunnelHopIpAddr>10.4.1.2</mplsTunnelHopIpAddr></explicitPathHop><explicitPathHop><mplsTunnelHopIndex>2</mplsTunnelHopIndex><mplsTunnelHopType>includeStrict</mplsTunnelHopType><mplsTunnelHopIntType>default</mplsTunnelHopIntType><mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType><mplsTunnelHopIpAddr>10.2.1.2</mplsTunnelHopIpAddr></explicitPathHop><explicitPathHop><mplsTunnelHopIndex>3</mplsTunnelHopIndex><mplsTunnelHopType>includeStrict</mplsTunnelHopType><mplsTunnelHopIntType>default</mplsTunnelHopIntType><mplsTunnelHopAddrType>IPV4</mplsTunnelHopAddrType><mplsTunnelHopIpAddr>2.2.2.2</mplsTunnelHopIpAddr></explicitPathHop></explicitPathHops>";
            String deleteaa = "path";
            
            e.body = aa;
        //System.out.println(e.create(null)(new String[]{"cc"});
    }*/

}
