package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyMplsExplicitPathHop extends OpsInterface {
    public String url = "/mpls/mplsTe/explicitPaths/explicitPath";
     public String fullUrl = "/mpls/mplsTe/explicitPaths/explicitPath?explicitPathName=%s/explicitPathHops/explicitPathHop";
     public String param="mplsTunnelHopIndex";
     
    public MyMplsExplicitPathHop(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }
    /**
     * @param args
     */
/*    public static void main(String[] args) {
        MyMplsExplicitPathHop e  = new MyMplsExplicitPathHop("1");
        e.body = "2";
        //System.out.println(e.delete(new String[]{"cc"}));
    }*/

}
