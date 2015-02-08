package com.huawei.agilete.base.servlet;

import com.huawei.networkos.ops.client.IOpsRestCaller;
import com.huawei.networkos.ops.response.RetRpc;


public class OpsInterface {

	public String url;
	public String fullUrl;
	public String body;
	public String param;
	public IOpsRestCaller restcall = null;
	
	public IOpsRestCaller getRestcall() {
		return restcall;
	}

	public void setRestcall(IOpsRestCaller restcall) {
		this.restcall = restcall;
	}
 
	public OpsInterface(IOpsRestCaller restcall)
	{
		this.restcall = restcall;
	}
	
	private RetRpc createNotRestCaller()
	{
		RetRpc rpc = new RetRpc();
		rpc.setContent("restcall can not be null");
		rpc.setStatusCode(500);
		rpc.setStatus("error");
		rpc.setMsg("failed to action restcaller. ");
		return rpc;
	}
	
	public RetRpc create(String[] content){
		String u;
		u = change(content);
		if (null != restcall)
		{
			return restcall.post(u, body);
		}
		return createNotRestCaller();
	}
	
	public RetRpc modify(String[] content){
		String u;
		u = change(content);
		if (null != restcall)
		{
			return restcall.put(u, body);
		}
		return createNotRestCaller();
	}

	public RetRpc get(String[] content){
		String u;
		u = change(content);
		if(null != body && !"".equals(body)){
			u +="?"+param+"="+body;
		}
		if (null != restcall)
		{
			return restcall.get(u);
		}
		return createNotRestCaller();
	}
	
	public RetRpc delete(String[] content){
		String u;
		u = change(content);
		if(null != body && !"".equals(body)){
			u +="?"+param+"="+body;
		}
		
		if (null != restcall)
		{
			return restcall.delete(u);
		}
		return createNotRestCaller();
	}

	public RetRpc getall(){
		
		if (null != restcall)
		{
			return restcall.get(url);
		}
		return createNotRestCaller();
	}
	
	private String change(String[] content){
		String u="";
		String furl = fullUrl;
		if(null !=content && content.length != 0){
			for(String s :content){
				furl = furl.replaceFirst("%s", s);
	           }
			u = furl.replace("%s", "");
		}else{
			u= url;
		}
		return u;
	}

 

}
