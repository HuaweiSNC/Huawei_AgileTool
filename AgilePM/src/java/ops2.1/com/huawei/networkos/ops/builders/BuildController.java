package com.huawei.networkos.ops.builders;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.huawei.networkos.ops.util.threadpool.WorkerTaskBean;
import com.huawei.networkos.ops.util.threadpool.WorkerThread;

public class BuildController implements Runnable {

	private static final Logger LOG = Logger.getLogger(BuildController.class);
	private TaskManager taskQueue = new TaskManager();
	private final LinkedList<WorkerThread> scheduleQueue = new LinkedList<WorkerThread>();

	private long interval = 2L;
	private Thread scheduleThread;
	private boolean waiting;

	/***
	 * 取出所有的 运行信息
	 * @return
	 */
	public List<WorkerTaskBean> getTasks()
	{
		List<WorkerTaskBean> beans = new LinkedList<WorkerTaskBean> ();
		synchronized (scheduleQueue) {
			for (WorkerThread thread : scheduleQueue) {
				beans.add(thread.getBean());
			}
		}		
		return beans;
	}
	
	/***
	 * 增加task运行
	 * 
	 * @param task
	 */
	public void addTask(WorkerThread task) {

		if (null == task) {
			return;
		}

		// 判断是否需要增加当前的Task，如果已存在，就不再重复加入
		synchronized (scheduleQueue) {

			for (WorkerThread thread : scheduleQueue) {
				if (thread.isEqual(task)) {
					LOG.debug("warnning skip, task: name: "  + task.getName() + ", title: "+ task.getTitle());
					return;
				}
			}
			scheduleQueue.add(task);
			scheduleQueue.notify();
		}
	}

	/***
	 * 启动task运行
	 * 
	 * @param taskName
	 * @param taskTitle
	 * @return
	 */
	public boolean startScheduleTask(String taskName, String taskTitle) {

		// 判断是否需要增加当前的Task，如果已存在，就不再重复加入
		synchronized (scheduleQueue) {

			for (WorkerThread thread : scheduleQueue) {
				if (thread.isEqual(taskName, taskTitle)) {
					thread.start();
				}
			}
		}

		return false;
	}

	/***
	 * 暂停task运行
	 * 
	 * @param taskName
	 * @param taskTitle
	 * @return
	 */
	public boolean stopScheduleTask(String taskName, String taskTitle) {

		// 判断是否需要增加当前的Task，如果已存在，就不再重复加入
		synchronized (scheduleQueue) {

			for (WorkerThread thread : scheduleQueue) {
				if (thread.isEqual(taskName, taskTitle)) {
					thread.stop();
				}
			}
		}

		return false;
	}

	/***
	 * 移除任务
	 * 
	 * @param taskName
	 * @param taskTitle
	 * @return
	 */
	public boolean removeTask(String taskName, String taskTitle) {

		// 判断是否需要增加当前的Task，如果已存在，就不再重复加入
		synchronized (scheduleQueue) {

			for (WorkerThread thread : scheduleQueue) {
				if (thread.isEqual(taskName, taskTitle)) {
					scheduleQueue.remove(thread);
					return true;
				}
			}
		}

		return false;
	}

	public void resume() {
		taskQueue.start();
		start();
	}

	public void pause() {
		taskQueue.stop();
		stop();
	}

	public void halt() {
		pause();
		System.exit(0);
	}

	public String getTaskQueueStatus() {
		if (taskQueue.isAlive()) {
			if (taskQueue.isWaiting()) {
				return "waiting";
			} else {
				return "alive";
			}
		} else {
			return "dead";
		}
	}

	private void start() {

		// 运行时间状态联调
		scheduleThread = new Thread(this, "TasksBuildQueueThread");
		scheduleThread.setDaemon(false);
		scheduleThread.start();
		while (!scheduleThread.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				String message = "Tasks ScheduleQueue.start() interrupted";
				LOG.error(message, e);
				throw new RuntimeException(message);
			}
		}
	}

	private void stop() {
		LOG.info("Stopping ScheduleQueue");
		if (scheduleThread != null) {
			scheduleThread.interrupt();
		}
		synchronized (scheduleQueue) {
			scheduleQueue.notify();
		}
	}

	@Override
	public void run() {

		try {
			LOG.info("Tasks ScheduleQueue started");
			while (true) {
				synchronized (scheduleQueue) {
					if (scheduleQueue.isEmpty()) {
						waiting = true;
						scheduleQueue.wait();
					}
					waiting = false;
				}
				serviceQueue();
			}
		} catch (InterruptedException e) {
			String message = "Task ScheduleQueue.run() interrupted. Stopping?";
			LOG.debug(message, e);
		} catch (Throwable e) {
			LOG.error("Tasks ScheduleQueue.run()", e);
		} finally {
			waiting = false;
			LOG.info("Tasks ScheduleQueue thread is no longer alive");
		}

	}

	private void serviceQueue() {

		// 进行选择运行
		LinkedList<WorkerThread> scheduleDelete = new LinkedList<WorkerThread>();
		
		synchronized (scheduleQueue) {
			// 处理运行事宜
			for (WorkerThread schedule : scheduleQueue) {
				
				if (schedule.doneBuilding() && schedule.isRunOne())
				{
					scheduleDelete.add(schedule);
				}
				
				// 资源已被使用存在，当前不运行
				if (taskQueue.isExistResource(schedule))
				{
					LOG.info("Tasks ScheduleQueue thread now nexttime :" + schedule.getName());
					continue;
				}
				// 进行运行处理
				if (!schedule.isBreak() && schedule.getNextBuild()) {
					schedule.resetTime(interval);
					taskQueue.requestBuild(schedule);
				}
		
			}
			
			// 删除处理只运行一次的任务
			for (WorkerThread schedule : scheduleDelete) {
				scheduleQueue.remove(schedule);
				LOG.info("Tasks ScheduleQueue thread now remove :" + schedule.getName());
			}
		}

		try {
			Thread.sleep(interval * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
