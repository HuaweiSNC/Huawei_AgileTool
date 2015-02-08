package com.huawei.agilete.task;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huawei.agilete.northinterface.dao.OTNqaDAO;
import com.huawei.networkos.ops.builders.BuildController;
import com.huawei.networkos.ops.client.OpsRestCaller;
import com.huawei.networkos.ops.util.threadpool.WorkerThread;

/**
 * A thin wrapper around a Project object all it is designed to do is run the
 * Project in it's own thread
 * 
 * @author Jared Richardson
 */

public class TaskNqa extends WorkerThread {

	private int time;
	public OpsRestCaller client;
	public String domianId;
	public int type; //1:main 2:tunnel 3:flow 4:flux
	public TaskNqa(String name) {

		this.name = name;
		this.title = "NQA CE12800_A TUNNEL1";
		LOG.debug("Task " + this.name + " is being wrapped");
	}
 

	@Override
	public void build() {
			OTNqaDAO.getInstance().runTask(client,this,true);
	}



}
