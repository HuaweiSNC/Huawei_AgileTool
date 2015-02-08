package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyMplsRsvpTeLsps extends OpsInterface {
     public String url = "/mpls/mplsTe/rsvpTeLsps/rsvpTeLsp";
    // public String url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel?tunnelName,mplsTunnelIngressLSRId,mplsTunnelEgressLSRId,mplsTunnelIndex,adminStatus,tunnelState,hotStandbyWtr,workingLspType&tunnelPaths/tunnelPath?pathType,explicitPathName";
     public String fullUrl = "/mpls/mplsTe/rsvpTeLsps/rsvpTeLsp?tunnelName=%s";
     public String param="tunnelName";
     
    public MyMplsRsvpTeLsps(IOpsRestCaller restcall){
        
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }
    
    
    public RetRpc get(String[] content){
        return super.get(content);
    }
    
    /**
     * @param args
     *//*
    public static void main(String[] args) {
        new HttpRequest();
        MyMplsRsvpTeTunnel e  = new MyMplsRsvpTeTunnel("5");
        //e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
        //e.getall();
        //String aa = "<tunnelName>Tunnel2</tunnelName><mplsTunnelIndex>1</mplsTunnelIndex><interfaceName>Tunnel1</interfaceName><mplsTunnelIngressLSRId>4.4.4.4</mplsTunnelIngressLSRId><mplsTunnelEgressLSRId>2.2.2.2</mplsTunnelEgressLSRId> <tunnelState>UP</tunnelState><hotStandbyWtr>100</hotStandbyWtr> <hotStandbyEnable>true</hotStandbyEnable><mplsTunnelRecordRoute>RECORD_LABLE</mplsTunnelRecordRoute><tunnelPaths><tunnelPath><pathType>primary</pathType><includeAll>0x0</includeAll><includeAny>0x0</includeAny><excludeAny>0x0</excludeAny><hopLimit>32</hopLimit><lspId>0</lspId><modifyLspId>0</modifyLspId><explicitPathName>primaryPath</explicitPathName></tunnelPath><tunnelPath><pathType>hot_standby</pathType><includeAll>0x0</includeAll><includeAny>0x0</includeAny><excludeAny>0x0</excludeAny><hopLimit>32</hopLimit><lspId>0</lspId><modifyLspId>0</modifyLspId><explicitPathName>backupPath</explicitPathName></tunnelPath></tunnelPaths>";
        
        //e.body = aa;
        e.body = " <tunnelName>Tunnel14</tunnelName>";
        ////System.out.println(e.create(null));
        e.body ="";
        //System.out.println(e.get(new String[]{"Tunnel8"}));
    }*/

}
