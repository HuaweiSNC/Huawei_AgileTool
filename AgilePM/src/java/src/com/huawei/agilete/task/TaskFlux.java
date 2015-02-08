package com.huawei.agilete.task;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.huawei.agilete.northinterface.dao.OTFluxDAO;
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

public class TaskFlux extends WorkerThread {

	private int time;
	public String type;
	public TaskFlux(String name) {
		this.name = name;
		this.title = "";
		LOG.debug("Task " + this.name + " is being wrapped");
	}

	@Override
	public void build() {
		OTFluxDAO.getInstance().runTaskMain(name,type);
	}



}
