package com.huawei.networkos.ops.client;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.OpsRestClient;
import org.apache.wink.client.Resource;

import com.huawei.agilete.base.bean.OpsServer;
import com.huawei.agilete.base.common.ServiceLog;
import com.huawei.agilete.base.servlet.util.OpsDevice;
import com.huawei.agilete.northinterface.bean.OTDevice;
import com.huawei.networkos.ops.response.RetRpc;


/**
 * HUAWEI restful api类
 * 
 * @author hwx168991
 * 
 */
public class OpsRestCaller implements IOpsRestCaller {

	private static final Log log = LogFactory.getLog(OpsRestCaller.class);
	private String protocol = "";
	public String serverInfo = "";
	public String serverIp = "";
	public String serverPort = "80";
	public String deviceId = "";
	
	public OpsRestCaller(OpsServer opsServer,String deviceId) {
		
		this.protocol = opsServer.getProtocol();
		this.serverIp = opsServer.getServerIp();
		this.deviceId = deviceId;
		this.serverPort = String.valueOf( opsServer.getPort());
	}
	
	public OpsRestCaller(OpsServer opsServer, OTDevice device) {
		
		this.protocol = opsServer.getProtocol();
		this.serverIp = opsServer.getServerIp();
		this.deviceId = device.getId();
		this.serverPort = String.valueOf(opsServer.getPort());
	}
	
	public OpsRestCaller(OpsServer opsServer, OpsDevice device) {
		
		
		this.protocol = opsServer.getProtocol();
		this.serverIp = opsServer.getServerIp();
		this.deviceId = device.getId();
		this.serverPort = String.valueOf(opsServer.getPort()); 
		 
	}
	 
	public String getServerInfo() {
		return serverInfo;
	}
 
	public void setServerInfo(String businessUrl) {
		this.serverInfo = businessUrl;
	}

	/**
	 * get,post,put,delete方法 返回url，data
	 * 
	 */
	public RetRpc get(String url) {
		return restcall(url, null, ACTION_GET);
	}

	public RetRpc post(String url, String data) {
		return restcall(url, data, ACTION_POST);
	}

	public RetRpc put(String url, String data) {
		return restcall(url, data, ACTION_PUT);
	}

	public RetRpc delete(String url) {
		return restcall(url, null, ACTION_DELETE);
	}

	public void buildServer(StringBuilder out)
	{
		 out.append(this.protocol);
		 out.append(this.serverIp).append(":").append(serverPort);
	}
	
	public void buildBusiness(StringBuilder out)
	{
		if (StringUtils.isNotBlank(serverInfo))
		{
			out.append(serverInfo);
		} else {
			if (StringUtils.isNotBlank(deviceId))
			{
				out.append("/devices/").append(this.deviceId);
			}else{
				out.append("/devices");
			}
		}
		
		
	}
	
	public void buildPath(StringBuilder out)
	{
		
		
	}
	
	public RetRpc restcall(String path, String data, String action) {
		long time1 = 0,time2 = 0;
		time1 = System.currentTimeMillis();

		StringBuilder out = new StringBuilder();
	         
	    buildServer(out);
	    buildBusiness(out);
	    out.append(path);
	    String url = out.toString();
	        

		RetRpc r = connect(url, action, data);
		time2 = System.currentTimeMillis() - time1;
		
//		StringBuffer logs = new StringBuffer();
//		logs.append("OPS_").append(action);
//		logs.append("{").append("\n");
//		logs.append("expend time=").append(time2).append("ms\n");
//		logs.append("url:").append(url).append("\n");
//		logs.append("message:").append(data).append("\n");
//		logs.append("state:").append(r.getStatusCode()).append("\n");
//		if(200 != r.getStatusCode()){
//			logs.append("error:").append(r.getContent()).append("\n");
//		}
//		logs.append("}");
//		log.error(logs);
		ServiceLog serviceLog = new ServiceLog();
		serviceLog.outOPSLog(url, r, time2, action, data);

		return r;
		
	}

	/**
	 * 连接http
	 * 
	 * @param url
	 * @param method
	 * @param data
	 * @return
	 */

	public RetRpc connect(String url, String method, String data) {

		RetRpc ret = new RetRpc();
		String address = url;
 
		URI uri = URI.create(address);
		
		// 返回新的address地址
		// create the rest client instance
		OpsRestClient client = new OpsRestClient();
		// create the resource instance to interact with
		Resource resource = null;
		 
		resource = client.resource(uri);
		 
		// issue the request
		ClientResponse response = null;

		try {
			if (ACTION_POST.equals(method)) {
				response = resource.accept("text/plain").post(data);
				
			} else if (ACTION_PUT.equals(method)) {
				response = resource.accept("text/plain").put(data);

			} else if (ACTION_DELETE.equals(method)) {
				response = resource.accept("text/plain").delete();

			} else {
				response = resource.accept("text/plain").get();
			}
		} catch (Exception ex) {
			
			//ex.printStackTrace();
			ret.setStatusCode(500);
			ret.setMsg(ex.getMessage());
			ret.setStatus("connected failed .");
			System.out.println(ret.getStatus());
			
			return ret;
		}  
		
		int code = response.getStatusCode();
		String msg = response.getMessage();
		ret.setStatusCode(code);
		ret.setStatus(response.getStatusType().getReasonPhrase());
		ret.setMsg(msg);

		// deserialize response
		ret.setContent(response.getEntity(String.class));

		return ret;
	}

}
