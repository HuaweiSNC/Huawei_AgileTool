package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyMplsLdpInstance extends OpsInterface {
     public String url = "/mpls/mplsLdp/ldpInstances/ldpInstance";
     public String fullUrl = "/mpls/mplsLdp/ldpInstances/ldpInstance";
     public String param="";
     
     //GET  /mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers
     //GET   /mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers/ldpRemotePeer?vrfName=_public_&remotePeerName=2.2.2.2
     //PUT   /mpls/mplsLdp/ldpInstances/ldpInstance
     //DELETE /mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers
     
    public MyMplsLdpInstance(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    public RetRpc create(String[] content){
        
        return modify(content);
    }
    
    public RetRpc modify(String[] content){
        if(null == super.body || "".equals(super.body) ){
            String messge = "<vrfName>_public_</vrfName><ldpRemotePeers><ldpRemotePeer operation=\"create\"><remotePeerName>$peerIp</remotePeerName><remoteIp>$peerIp</remoteIp><noMapping>false</noMapping></ldpRemotePeer></ldpRemotePeers>";
            messge = messge.replace("$peerIp", content[0]);
            super.body = messge;
        }
        return super.modify(null);
    }

    public RetRpc get(String[] content){
        if(null != content){
            StringBuffer str = new StringBuffer("/mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers/ldpRemotePeer?vrfName=_public_&remotePeerName=");
            str.append(content[0]);
            super.url = str.toString();
        }else{
            super.url = "/mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers";
        }
        return super.get(null);
    }
    
    public RetRpc delete(String[] content){
        if(null != content){
            StringBuffer str = new StringBuffer("/mpls/mplsLdp/ldpInstances/ldpInstance?vrfName=_public_/ldpRemotePeers/ldpRemotePeer?vrfName=_public_&remotePeerName=");
            str.append(content[0]);
            super.url = str.toString();
            return super.delete(null);
        }
        return null;
    }

    public RetRpc getall(){
        return null;
    }
    
    
    
    /**
     * @param args
     */
/*    public static void main(String[] args) {
        new HttpRequest();
        MyMplsLdpInstance e  = new MyMplsLdpInstance("4");
        //System.out.println(e.get(new String[]{"10.137.130.131"})(new String[]{"spring3","vpwsLdp"}));
    }*/

}
