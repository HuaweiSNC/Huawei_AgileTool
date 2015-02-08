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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Used to encapsulate the concept of a Thread Pool
 * <P>
 * The queue accepts tasks that implement the WorkerThread interface.
 * Each task may be named, but do not have to be.  You may then waitOn
 * for ever task to complete or just the named tasks you care about...
 * or not wait at all.
 *
 * @author Jared Richardson
 * @version $Id: ThreadQueue.java 2800 2007-01-28 08:52:59Z jfredrick $
 */

public class ThreadQueue extends Thread {
    private static final Logger LOG = Logger.getLogger(ThreadQueue.class);

    // A ThreadGroup that logs uncaught exception using Log4J
    private final ThreadGroup loggingGroup = new Log4jThreadGroup("Logging group", LOG);

    /**
     * The list of WorkerThreads that are waiting to run (currently idle)
     */
    private final List idleTasks = Collections.synchronizedList(new LinkedList());
    /**
     * The list of WorkerThreads that are running now (currently busy)
     */
    private final List busyTasks = Collections.synchronizedList(new LinkedList());

    /**
     * the resultList from each WorkerThread's run
     */
    private final Map resultList = Collections.synchronizedMap(new HashMap());

    /**
     * Retains a handle to all the running Threads
     * to handle all sorts of interesting situations
     */

    private final Map runningThreads = Collections.synchronizedMap(new HashMap());

    /**
     * The number of java.lang.Threads to be launched by the pool at one time
     */
    private final int threadCount = ThreadQueueProperties.getMaxThreadCount();

    /**
     * The amount to time to sleep between loops
     */
    private static final int SLEEP_TIME = 100;

    /**
     * A handle to the ThreadQueue singleton
     */
    private static ThreadQueue threadQueue;

    /**
     * tells the main process when to exit
     */
    private static boolean terminate = false;

    /*
       fetch tasks to be executed from the idle list,
       put them on the busy list, and
       execute them
    */
    public void run() {
        while (true) {
            if (ThreadQueue.terminate) {
                LOG.info("terminating ThreadQueue.run()");
                return;
            }

            final boolean nothingWaiting = idleTasks.size() == 0;
            final boolean maxedOut = busyTasks.size() >= threadCount;

            if (nothingWaiting || maxedOut) {
                sleep(SLEEP_TIME);
            } else {
                handleWaitingTask();
            }

            cleanCompletedTasks();
        }
    }

    private void handleWaitingTask() {
        LOG.debug("handling waiting task");
        synchronized (busyTasks) {
            WorkerThread worker = (WorkerThread) idleTasks.remove(0);
            Thread thisThread = new Thread(loggingGroup, worker);
            busyTasks.add(worker);
            runningThreads.put(worker, thisThread);
            thisThread.start();
        }
    }

    private void cleanCompletedTasks() {
        synchronized (busyTasks) {
            Iterator tasks = busyTasks.iterator();
            while (tasks.hasNext()) {
                WorkerThread task = (WorkerThread) tasks.next();
                Object result = task.getResult();
                final boolean taskDone = result != null;
                if (taskDone) {
                    LOG.debug("Found a finished task, name =" + task.getName() +", result = "  +  task.getResult());
                    resultList.put(task.getName(), result);
                    tasks.remove();
                    runningThreads.remove(task);
                }
            }
        }
    }

    /**
     * An internal wrapper around the creation of the
     * Thread Pool singleton
     */

    private static ThreadQueue getThreadQueue() {
        if (threadQueue == null) {
            threadQueue = new ThreadQueue();
            threadQueue.start();
        }
        return threadQueue;
    }

    /**
     * Adds a task to the idleList to be executed
     */
    public static void addTask(WorkerThread task) {
        LOG.debug("Preparing to add worker task " + task.getName());

/*        if (isActive(task.getName())) {
            throw new RuntimeException("Duplicate task name!");
        }*/
        synchronized (getThreadQueue().busyTasks) {
            getThreadQueue().idleTasks.add(task);
        }
    }
    
	public static boolean isExist(String taskName) {

		if (null == taskName) {
			System.out.println("task name is null :" + taskName);
			return true;
		}
		  
		WorkerThread task = getIdleTask(taskName);
        if (task != null) {
            return true;
        }
        task = getBusyTask(taskName);
        if (task != null) {
            return true;
        }
        
		return false;
	} 

    
    /****
     * 判断两个任务是否相同
     * @param taskOne
     * @param taskTwo
     * @return
     */
	public static boolean isExist(WorkerThread taskOne) {

		if (null == taskOne) {
			System.out.println("null title:" + taskOne);
			return true;
		}
		
		String taskName = taskOne.getName();
		String title = taskOne.getTitle();
		
		WorkerThread task = getIdleTask(taskName, title);
        if (task != null) {
        	System.out.println("ok1 title:" + taskName + ", title:"+ title);
            return true;
        }
        task = getBusyTask(taskName, title);
        if (task != null) {
        	System.out.println("ok2 title:" + taskName + ", title:"+ title);
            return true;
        }
        
        //System.out.println("nok title:" + taskName + ", title:"+ title);
		return false;
	} 

    /**
     * This may not *always* work -- a task may slip by us between queue checks.
     * That's OK.  We'd rather have transient results than block the busy queue
     *    until we're done just to get a position report on a task.
     */
    public static String findPosition(String taskName) {
        WorkerThread task = getIdleTask(taskName);
        if (task != null) {
            return getTaskPosition(task, getThreadQueue().idleTasks, "IDLE");
        }
        task = getBusyTask(taskName);
        if (task != null) {
            return getTaskPosition(task, getThreadQueue().busyTasks, "BUSY");
        }
        Object result = getResult(taskName);
        if (result != null) {
            return "[ COMPLETE ]";
        }
        return "[ not found in queues ]";
    }

 
    private static String getTaskPosition(WorkerThread task, List queue, String queueName) {
        int position;
        int length;
        synchronized (getThreadQueue().busyTasks) {
            position = queue.indexOf(task);
            length = queue.size();
        }
        return formatPosition(position, length, queueName);
    }
    
    private static String formatPosition(int position, int length, String queueName) {
        if (position < 0) {
            return "[ NONE ]";
        }
        // position is 0-based, make it 1-based for human reporting
        return queueName + "[ " + (position + 1) + " / " + length + " ]";
    }

    /**
     * Checks to see if a specific task is either running or waiting in our system
     *
     * @return TRUE if task is waiting or running, FALSE if it is finished
     */
    public static boolean isActive(String taskName) {
        synchronized (getThreadQueue().busyTasks) {
            // it's either busy or idle
            return !((getBusyTask(taskName) == null)
                    && (getIdleTask(taskName) == null));
        }
    }

    /**
     * fetch a result from a completed WorkerThread
     * a null result means it's not done yet
     */

    private static Object getResult(String workerName) {
        return getThreadQueue().resultList.get(workerName);
    }

    /**
     * retrieves an active task from the busy list
     *
     * @return the active task (if present) or null if it cannot be found
     */
    private static WorkerThread getBusyTask(String taskName) {
        synchronized (getThreadQueue().busyTasks) {
            return getTask(taskName, getThreadQueue().busyTasks.iterator());
        }
    }

    /**
     * retrieves an idle task from the idle list
     *
     * @return the idle task (if present) or null if it cannot be found
     */
    private static WorkerThread getIdleTask(String taskName) {
        synchronized (getThreadQueue().idleTasks) {
            return getTask(taskName, getThreadQueue().idleTasks.iterator());
        }
    }

    
    /**
     * retrieves an active task from the busy list
     *
     * @return the active task (if present) or null if it cannot be found
     */
    private static WorkerThread getBusyTask(String taskName, String title) {
        synchronized (getThreadQueue().busyTasks) {
            return getTask(taskName, title, getThreadQueue().busyTasks.iterator());
        }
    }
    
    /**
     * retrieves an idle task from the idle list
     *
     * @return the idle task (if present) or null if it cannot be found
     */
    private static WorkerThread getIdleTask(String taskName, String title) {
        synchronized (getThreadQueue().idleTasks) {
            return getTask(taskName, title, getThreadQueue().idleTasks.iterator());
        }
    }
     
    /**
     * retrieves a task from the list
     *
     * @return the task (if present) or null if it cannot be found
     */
    private static WorkerThread getTask(String taskName, String title, Iterator myIt) {
        while (myIt.hasNext()) {
            WorkerThread thisWorker = (WorkerThread) myIt.next();
            String tempString = thisWorker.getName();
            String titleString = thisWorker.getTitle();
            if (tempString.equalsIgnoreCase(taskName) 
            		&& titleString.equalsIgnoreCase(title)) {
                return thisWorker;
            }
        }
        return null;
    }
    
    /**
     * retrieves a task from the list
     *
     * @return the task (if present) or null if it cannot be found
     */
    private static WorkerThread getTask(String taskName, Iterator myIt) {
        while (myIt.hasNext()) {
            WorkerThread thisWorker = (WorkerThread) myIt.next();
            String tempString = thisWorker.getName();
            if (tempString.equalsIgnoreCase(taskName)) {
                return thisWorker;
            }
        }
        return null;
    }

    /**
     * @return the names of the tasks in the busy list; may be empty
     */
    public static List getBusyTaskNames() {
        List names;
        synchronized (getThreadQueue().busyTasks) {
            names = getTaskNames(getThreadQueue().busyTasks.iterator());
        }
        return names;
    }

    /**
     * @return the names of the tasks in the idle list; may be empty
     */
    public static List getIdleTaskNames() {
        List names;
        synchronized (getThreadQueue().busyTasks) {
            names = getTaskNames(getThreadQueue().idleTasks.iterator());
        }
        return names;
    }

    /**
     * @return the names of the tasks in the list; may be empty
     */
    private static List getTaskNames(Iterator taskIter) {
        List names = new LinkedList();
        while (taskIter.hasNext()) {
            WorkerThread thisWorker = (WorkerThread) taskIter.next();
            names.add(thisWorker.getName());
        }
        return names;
    }

    /**
     * Utility call for sleeps
     */
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }

    static void stopQueue() {
        threadQueue.interrupt();
        threadQueue = null;
    }

}
