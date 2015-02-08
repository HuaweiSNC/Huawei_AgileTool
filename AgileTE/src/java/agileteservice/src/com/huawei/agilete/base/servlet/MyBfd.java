package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;

public class MyBfd extends OpsInterface {
     public String url = "/bfd/bfdCfgSessions/bfdCfgSession";
     public String fullUrl = "/bfd/bfdCfgSessions/bfdCfgSession?sessName=%s";
     public String param="sessName";
     
    public MyBfd(IOpsRestCaller restcall){
        
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    
    /**
     * @param args
     */
    /*public static void main(String[] args) {
        MyBfd e  = new MyBfd("1");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        String aa = "<sessName>backupSpring1</sessName><createType>SESS_STATIC</createType><localDiscr>1302</localDiscr><remoteDiscr>1402</remoteDiscr><linkType>TE_TUNNEL</linkType><tunnelName>Tunnel1</tunnelName><detectMulti>3</detectMulti><minTxInt/><minRxInt/>";
        String deleteaa = "backupSpring1";
        
        e.body = aa;
        //System.out.println(e.delete(new String[]{"cc"}));
    }*/

}
