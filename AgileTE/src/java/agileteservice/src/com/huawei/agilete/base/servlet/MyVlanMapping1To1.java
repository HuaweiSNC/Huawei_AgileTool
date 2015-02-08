package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyVlanMapping1To1 extends OpsInterface {
     public String url = "/_action/ethernet/createL2MapExt";
     //public String fullUrl = "/ethernet/ethernetIfs/ethernetIf?ifName=%s/l2Attribute/l2MapPortExts";
     public String fullUrl = "/ethernet/ethernetIfs/ethernetIf?ifName=%s";
     public String param="tnlPolicyName";
     
    public MyVlanMapping1To1(IOpsRestCaller restcall){
        super(restcall);
        super.url = url;
        super.fullUrl = fullUrl;
        super.param = param;
    }

    /* (non-Javadoc)
     * 创建
     */
    public RetRpc create(String[] content){
        super.url = "/_action/ethernet/createL2MapExt";
        return super.create(content);
    }
    
    
    
    /* (non-Javadoc)
     * 编辑（用不到）
     */
    public RetRpc modify(String[] content){
        
        
        return super.modify(content);
    }
    /* (non-Javadoc)
     * 删除
     */
    public RetRpc delete(String[] content){
        
        super.url = "/_action/ethernet/deleteL2MapExt";
        return super.create(content);
    }

    public RetRpc get(String[] content){
        if(content==null){
            super.url = "/ethernet/ethernetIfs/ethernetIf/l2Attribute/l2MapPortExts";
        }else if("allEthernet".equals(content[0])){
            content = null;
            super.url = "/ethernet/ethernetIfs/ethernetIf?ifName";
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
