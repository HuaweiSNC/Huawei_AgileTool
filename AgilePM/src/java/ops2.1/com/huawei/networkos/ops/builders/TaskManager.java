/*********************************************************************
Copyright, 2008-2010, Huawei Tech. Co., Ltd.
All Rights Reserved
----------------------------------------------------------------------
Project Code  : VIBE V3.0
*********************************************************************/
package com.huawei.networkos.ops.builders;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.huawei.networkos.ops.util.threadpool.ThreadQueue;
import com.huawei.networkos.ops.util.threadpool.WorkerThread;

public class TaskManager implements Runnable {
	
    private static final Logger LOG = Logger.getLogger(TaskManager.class);

    private final LinkedList<WorkerThread> queue = new LinkedList<WorkerThread>();
    private boolean waiting = false;
    private Thread buildQueueThread;

    private List listeners = new ArrayList();

    /**
     * @param project
     */
    public void requestBuild(WorkerThread task) {
    	
        LOG.debug("Tasks BuildQueue.requestBuild Thread = " + Thread.currentThread().getName());
        
        notifyListeners();
        synchronized (queue) {
        	if (!ThreadQueue.isExist(task))
        	{
        		for(WorkerThread thread : queue)
        		{
        			if (thread.isEqual(task))
        			{
        				LOG.debug("Exist running Queue, skip : task"  + task.getName() + " title: "+ task.getTitle());
        				return ;
        			}
        		}
                LOG.debug("Add running Queue : task"  + task.getName());
                queue.add(task);
                queue.notify();
        	} else {
        		 LOG.debug("Skip Exist task: "  + task.getName() + " title: "+ task.getTitle());
        	}
        }
        
        
        
    }
 
    public boolean isExistResource(WorkerThread task)
    {
        int position;
        int length;
        synchronized (queue) {
            position = queue.indexOf(task);
            length = queue.size();
        }
        if (position < 0) {
            return ThreadQueue.isExist(task.getName());
        }
        
        return true;
    }

    /**
     * @param project The project to find in the queues
     * @return String representing this project's position in the various queues, e.g. IDLE[ 5 / 24 ]
     */
    public String findPosition(WorkerThread task) {
        int position;
        int length;
        synchronized (queue) {
            position = queue.indexOf(task);
            length = queue.size();
        }
        if (position < 0) {
            return ThreadQueue.findPosition(task.getName());
        }
        // position is 0-based, make it 1-based for human reporting
        return "BUILD_REQUESTED[ " + (position + 1) + " / " + length + " ]";
    }

    void serviceQueue() {
        
        while (!queue.isEmpty()) {
            
        	WorkerThread nextTask;
            synchronized (queue) {
                if (queue.isEmpty()) {
                    break;
                }
                nextTask = (WorkerThread) queue.remove(0);
            }
            
            if (nextTask != null) {
                LOG.debug("now adding to the task thread queue: " + nextTask.getName());
                
                // let's not add the task more than once
                String name = nextTask.getName();
                String title = nextTask.getTitle();
                LOG.debug(" Now Active queue : " + nextTask.getName());
                ThreadQueue.addTask(nextTask);
                
             /*   if (ThreadQueue.isActive(name)) {
                  LOG.debug("Skip Active queue: " + name);
                  
                  // it's already there... don't re-add it.
                  // later, we'll need to add it to a queued up list
                  // so we don't 'forget' about the new build request
                } else {
                    
                  LOG.debug(" Is not Active queue : " + nextTask.getName());
                  ThreadQueue.addTask(nextTask);
                }*/
            }
            
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            LOG.debug("Tasks queue.isEmpty() == " + queue.isEmpty());
        }
       
    }

    public void run() {
        try {
            LOG.info("Tasks BuildQueue started");
            while (true) {
                synchronized (queue) {
                    if (queue.isEmpty()) {
                        waiting = true;
                        queue.wait();
                    }
                    waiting = false;
                }
                serviceQueue();
            }
        } catch (InterruptedException e) {
            String message = "Tasks BuildQueue.run() interrupted. Stopping?";
            LOG.debug(message, e);
        } catch (Throwable e) {
            LOG.error("Tasks BuildQueue.run()", e);
        } finally {
            waiting = false;
            LOG.info("Tasks BuildQueue thread is no longer alive");
        }
    }

    void start() {
        buildQueueThread = new Thread(this, "TasksBuildQueueThread");
        buildQueueThread.setDaemon(false);
        buildQueueThread.start();
        while (!buildQueueThread.isAlive()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                String message = "Tasks BuildQueue.start() interrupted";
                LOG.error(message, e);
                throw new RuntimeException(message);
            }
        }
    }

    void stop() {
        LOG.info("Stopping BuildQueue");
        if (buildQueueThread != null) {
            buildQueueThread.interrupt();
        }
        synchronized (queue) {
            queue.notify();
        }
    }

    public boolean isAlive() {
        return true;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        Iterator toNotify = listeners.iterator();
        while (toNotify.hasNext()) {
            try {
                ((Listener) toNotify.next()).buildRequested();
            } catch (Exception e) {
                LOG.error("exception notifying listener before project queued", e);
            }
        }

    }

    public static interface Listener extends EventListener {
        void buildRequested();
    }
}
