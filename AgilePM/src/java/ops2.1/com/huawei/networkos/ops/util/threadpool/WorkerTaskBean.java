package com.huawei.networkos.ops.util.threadpool;

import com.huawei.agilete.northinterface.bean.OTTask;

public class WorkerTaskBean {

	private String name;
	private String title;
	private long interval;
	private Boolean isBreak;
	private Boolean running;
	// 请求实体 
	protected String body ="";
	// 请求的URL
	protected String requestUrl = "";
	private OTTask task;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	public Boolean getIsBreak() {
		return isBreak;
	}
	public void setIsBreak(Boolean isBreak) {
		this.isBreak = isBreak;
	}
	public Boolean getRunning() {
		return running;
	}
	public void setRunning(Boolean running) {
		this.running = running;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public OTTask getTask() {
		return task;
	}
	public void setTask(OTTask task) {
		this.task = task;
	}
	 
}
