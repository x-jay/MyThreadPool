package com.study.schdule;

import java.util.PriorityQueue;

public class Worker implements Runnable{
//一个工作线程对象.
	public Thread thread;
	private CustomPriorityQueue queue;
	public Worker(CustomPriorityQueue queue)
	{
		this.queue = queue;
	}
	
	public void run()
	{
		Runnable task = null;
		while((task=getTask())!=null)
		{
			runTask(task);
			task = null;
		}
	}
	
	public void runTask(Runnable run)
	{
		run.run();
	}
	
	public Runnable getTask()
	{
//		if(!queue.isEmpty())
//		{
//			CustomTask task = queue.remove();
//			return task.getTask();
//		}
//		return null;
		
		CustomTask ct;
		try {
			ct = queue.take();
			return ct;
		} catch (InterruptedException e) {
			
		}	
		return null;
	}
	
	
}
