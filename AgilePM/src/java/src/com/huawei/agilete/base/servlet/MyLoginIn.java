package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;

public class MyLoginIn implements IOpsRestCaller{
	 public String url = "";
	 public String fullUrl = "http://10.111.69.127:8080/database/agilegre/checkresult?username=&infoname=checkpassword&data=";
	 public String param="";
	 public IOpsRestCaller restcall = null;
	public MyLoginIn(IOpsRestCaller restcall){
		this.restcall=restcall;
	}

	public RetRpc get(String[] concent){
		RetRpc retRpc = new RetRpc();
		retRpc = restcall.get("http://10.111.69.127:8080/database/agilegre/checkresult?username="+concent[0]+"&infoname=checkpassword&data="+concent[1]);
		return retRpc;
	}

	@Override
	public RetRpc get(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetRpc post(String url, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetRpc put(String url, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetRpc delete(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RetRpc restcall(String path, String data, String action) {
		// TODO Auto-generated method stub
		return null;
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
