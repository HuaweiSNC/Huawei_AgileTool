package com.huawei.networkos.ops.response;

public class RetRpc {

	private int statusCode = 200;
	private String status ="";
	private String content = "false";
	private String msg = "";
	
	public RetRpc(){
	}
	public RetRpc(int statusCode){
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		//content = MyIO.characterFormat(content);
		this.content = content;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[\"").append(this.statusCode).append("\"");
		buf.append(",\"").append(this.msg).append("\"");
		buf.append(",\"").append(this.content).append("\"]");
		
		return buf.toString();
	}
	
	
	
}
