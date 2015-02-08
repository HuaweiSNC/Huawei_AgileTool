package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class GetMyData extends OpsInterface {
	 public String url = "/dgntl/lsp/startLspPing";
	 public String fullUrl = "/dgntl/lsp/lspPingResults/lspPingResult?testName=%s";
	 public String param="testName";
	 public String type="";
	 
	public GetMyData(IOpsRestCaller restcall){
		super(restcall);
		super.url = url;
		super.fullUrl = fullUrl;
		super.param = param;
	}

	public RetRpc create(String[] content){
		return super.create(content);
	}
	public RetRpc modify(String[] content){
		return super.modify(content);
	}
	public RetRpc delete(String[] content){
		return super.create(content);
	}

	public RetRpc get(String[] content,String url){
		super.url = url;
		return super.get(content);
	}
	
	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		new HttpRequest();
		MyTunnelPolicy e  = new MyTunnelPolicy("2");
		//e.url = "/mpls/mplsTe/rsvpTeTunnels/rsvpTeTunnel";
		//e.getall();
		String aa = "<tnlPolicyName>PolicyTest1</tnlPolicyName><description></description><tnlPolicyType>tnlBinding</tnlPolicyType><tpNexthops><tpNexthop operation=\"merge\"><nexthopIPaddr>4.4.4.4</nexthopIPaddr><downSwitch>false</downSwitch><ignoreDestCheck>false</ignoreDestCheck><tpTunnels><tpTunnel operation=\"merge\"><tunnelName>Tunnel1</tunnelName></tpTunnel></tpTunnels></tpNexthop></tpNexthops>";
		String deleteaa = "PolicyTest1";
		
		e.body = deleteaa;
		System.out.println(e.delete(null)(new String[]{"cc"});
	}*/

}
