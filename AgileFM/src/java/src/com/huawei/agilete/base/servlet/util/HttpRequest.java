package com.huawei.agilete.base.servlet.util;

import java.net.URI;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.OpsRestClient;
import org.apache.wink.client.Resource;


import com.huawei.networkos.ops.response.RetRpc;

public class HttpRequest {
	public static final String ACTION_GET = "GET";
	public static final String ACTION_POST = "POST";
	public static final String ACTION_PUT = "PUT";
	public static final String ACTION_DELETE = "DELETE";
	
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
			
			ex.printStackTrace();
			ret.setStatusCode(500);
			ret.setMsg(ex.getMessage());
			ret.setStatus("connected failed .");
			
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
