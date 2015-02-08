package com.huawei.agilete.task;

import com.huawei.networkos.ops.util.threadpool.WorkerThread;

/**
 * A thin wrapper around a Project object all it is designed to do is run the
 * Project in it's own thread
 * 
 * @author Jared Richardson
 */

public class TaskWrapper extends WorkerThread {

    private int time;
    public TaskWrapper(String name) {

        this.name = name;
        this.title = "NQA CE12800_A TUNNEL1";
        LOG.debug("Task " + this.name + " is being wrapped");
    }
 

    @Override
    public void build() {
        
        try {
            Thread.sleep(5000);
            time++;
            LOG.debug("name :" + name + ", title : " + title + ", time:" + time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
        
    }



}
