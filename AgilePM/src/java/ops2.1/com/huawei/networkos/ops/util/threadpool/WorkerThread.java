/*********************************************************************
Copyright, 2008-2010, Huawei Tech. Co., Ltd.
All Rights Reserved
----------------------------------------------------------------------
Project Code  : VIBE V3.0
 *********************************************************************/
/**
 * @file  
 * @author xilequan 00106601
 * @brief
 * @detail
 * @date 2009-01-08
 * @version 1.0
 * @see 
 * @note
 */

package com.huawei.networkos.ops.util.threadpool;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huawei.agilete.northinterface.bean.OTTask;

public abstract class WorkerThread implements Runnable {

	protected static final Logger LOG = Logger.getLogger(WorkerThread.class);
	// 间隔运行时间秒, 如果小于等于0，代表只运行一次
	protected long interval = 0L;
	// 默认没有名称
	public static final String BLANK_NAME = "blank";
	// 任务名称
	protected String name = BLANK_NAME;
	//任务标题
	protected String title = BLANK_NAME;
	// 请求实体 
	public String body ="";  // protected->public
	// 请求的URL
	protected String requestUrl = "";
	// 计数器
	private long countWait = 0L;
	// 是否构建完成
	private boolean doneBuilding;
	// 当前是否是break状态
	private boolean isBreak = false;
	// 下一次是否是break状态
	private boolean nextBreak = false;
	// 是否只是运行一次
	private boolean isRunOne = false;
	// 判断是否在运行状态
	private boolean running = false;
	// 是否运行过
	private Object doneBuildingMutex = new Object();
	// 运行
	public abstract void build();
	// oTTask 
	public OTTask oTTask;  
	
	// 获取结果数据内容
	public Object getResult() {
		if (doneBuilding()) {
			return "finished";
		} else {
			return null;
		}
	} 
	 
	public boolean isRunOne() {
		return isRunOne;
	}

	public void setRunOne(boolean isRunOne) {
		this.isRunOne = isRunOne;
	}

	// 获取任务的名称
	public String getName()
	{
		return name;
	}
	 
	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// 获取任务运行标题, 如果标题一样，认为两个任务是一样的
	public String getTitle() {
		return title;
	}
 
	// 运行任务
	public void run() {
		
		try {
			running = true;
			System.out.println("task running : " + getName() + ", title : " +  getTitle());
			build();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			setDoneBuilding(true);
			running = false;
			System.out.println("task ending : " + getName() + ", title : " +  getTitle());
		}
	}
	
	private void setDoneBuilding(boolean done) {
		synchronized (doneBuildingMutex) {
			doneBuilding = done;
		}
	}

	public boolean doneBuilding() {
		return doneBuilding;
	}
	
	// 是否在运行状态
	public boolean isRun()
	{
		return running;
	}
	
	public long getTimeWaitfor() {
		return countWait;
	}

	public void setTimeWaitfor(long timeWaitfor) {
		this.countWait = timeWaitfor;
	}
 
	public void setInterval(long interval) {
		this.interval = interval;
	}

	public long getInterval() {
		return interval;
	}
	
	public boolean isBreak() {
		return isBreak;
	}
	
	

	/***
	 * 是否相同
	 * @param task
	 * @return
	 */
	public boolean isEqual(WorkerThread task)
	{
		if (StringUtils.equals(getName(), task.getName())
				&& StringUtils.equals(getTitle(), task.getTitle()))
		{
			return true;
		}
		
		return false;
	}
	
	/***
	 * 是否相同
	 * @param task
	 * @return
	 */
	public boolean isEqual(String taskName, String taskTitle)
	{
		if (StringUtils.equals(getName(), taskName)
				&& StringUtils.equals(getTitle(), taskTitle))
		{
			return true;
		}
		
		return false;
	}

	/***
	 * 重新设置计数器
	 * 
	 * @param preInterval
	 *            间隔时间
	 */
	public void resetTime(long preInterval) {
		
		// 当前设置 的时间间隔少于定时器时间，就为定时器时间 
		if (interval > 0 && interval <= preInterval) {
			countWait = 1;
			nextBreak = false;
			setDoneBuilding(false) ;
		// 当设置时间小于等于0，就只运行一次
		} else if (interval <= 0)
		{
			countWait = 1;
			nextBreak = true;
	    // 否则按计数器时间进行运行
		} else {
			countWait = interval / preInterval;
			nextBreak = false;
			setDoneBuilding(false) ;
		}
	}
	
	public void start()
	{
		isBreak = false;
	}
	
	public void stop()
	{
		isBreak = true;
	}

	/***
	 * 
	 * 获取是否运行
	 * @return 如果返回true表示运行，否则不运行
	 */
	public boolean getNextBuild() {
		
		if (nextBreak)
		{
			isBreak = true;
			return false;
		}
		
		if (countWait > 0) {
			countWait--;
		}

		if (0 == countWait) {
			countWait = -1;
			return true;
		}
		return false;
	}

	/***
	 * 返回bean
	 * @return
	 */
	public WorkerTaskBean getBean()
	{
		WorkerTaskBean bean = new WorkerTaskBean();
		bean.setBody(body);
		bean.setInterval(interval);
		bean.setIsBreak(isBreak);
		bean.setName(name);
		bean.setRequestUrl(requestUrl);
		bean.setRunning(running);
		bean.setTitle(title);
		bean.setTask(oTTask);
		return bean;
	}
}
