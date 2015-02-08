package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyNqa extends OpsInterface {
     public String url = "/dgntl/lsp/startLspPing";
     public String fullUrl = "/dgntl/lsp/lspPingResults/lspPingResult?testName=%s";
     public String param="testName";
     public String type="";
     
    public MyNqa(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    public RetRpc create(String[] content){
        if("tunnel".equals(type)){
            super.url = "/_action/dgntl/lsp/startLspPing";
        }else if("flow".equals(type)){
            super.url = "/_action/dgntl/pwe3/startPwe3Ping";
        }else{
            return new RetRpc(403);
        }
        
        
        return super.create(content);
    }
    
    public RetRpc modify(String[] content){
        
        
        return super.modify(content);
    }

    public RetRpc delete(String[] content){
        if(null != content){
            if("tunnel".equals(type)){
                super.fullUrl = "/_action/dgntl/lsp/deleteLspPing?testName=%s";
            }else if("flow".equals(type)){
                super.fullUrl = "/_action/dgntl/pwe3/deletePwe3Ping?testName=%s";
            }else{
                return new RetRpc(403);
            }
            
        }else{
            return new RetRpc(403);
        }
        return super.create(content);
    }

    public RetRpc get(String[] content){
        if(null != content){
            if("tunnel".equals(type)){
                super.fullUrl = "/dgntl/lsp/lspPingResults/lspPingResult?testName=%s";
            }else if("flow".equals(type)){
                super.fullUrl = "/dgntl/pwe3/pwe3PingResults/pwe3PingResult?testName=%s";
            }else{
                return new RetRpc(403);
            }
        }else{
            return new RetRpc(403);
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
