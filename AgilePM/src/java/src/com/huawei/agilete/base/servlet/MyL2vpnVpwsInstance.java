package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyL2vpnVpwsInstance extends OpsInterface {
	 public String url = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance";
	 //原来fullurl原来有instanceType条件，因为加上无法查询到数据，所以去除public String fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s&instanceType=%s";
	 public String fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s";
	 public String param="instanceName=%s&instanceType=%s";
	 
	public MyL2vpnVpwsInstance(IOpsRestCaller restcall){
		super(restcall);
		super.url = url;
		super.fullUrl = fullUrl;
		super.param = param;
	}

	public RetRpc delete(String[] content){
//		RetRpc result = new RetRpc();
		super.fullUrl = "/l2vpn/l2vpnvpws/vpwsInstances/vpwsInstance?instanceName=%s&instanceType=%s";
		return super.delete(content);
	}
	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		new HttpRequest();
		MyL2vpnVpwsInstance e  = new MyL2vpnVpwsInstance("2");
		//e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
		//e.getall();
		String aa = "<instanceName>spring3</instanceName>                <instanceType>vpwsLdp</instanceType>                <encapsulateType>vlan</encapsulateType>                <l2vpnAcs>                    <l2vpnAc operation=\"merge\">                        <interfaceName>ether0/1/9</interfaceName>                        <tagged>true</tagged>                        <accessPort>false</accessPort>                    </l2vpnAc>                </l2vpnAcs>                <vpwsPws>                    <vpwsPw operation=\"merge\">                        <pwRole>primary</pwRole>                        <pwId>100</pwId>                        <peerIp>1.1.1.1</peerIp>                        <tnlPolicyName>tp1</tnlPolicyName>                    </vpwsPw>                </vpwsPws>";
		String deleteaa = "<explicitPathName>main113</explicitPathName>";
		
		e.body = aa;
		System.out.println(e.create(null)(new String[]{"spring3","vpwsLdp"}));
	}*/

}
