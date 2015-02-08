package com.huawei.agilete.task;

import com.huawei.agilete.northinterface.bean.OTTask;
import com.huawei.agilete.northinterface.dao.OTFluxDAO;
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
    private OTTask oTTask;
    
    public TaskFlux(String name) {
        this.name = name;
        this.title = "";
        LOG.debug("Task " + this.name + " is being wrapped");
    }

    @Override
    public void build() {
        OTFluxDAO.getInstance().runTaskMain(name,type);
    }

	public OTTask getoTTask() {
		return oTTask;
	}

	public void setoTTask(OTTask oTTask) {
		this.oTTask = oTTask;
	}

    
    


}
