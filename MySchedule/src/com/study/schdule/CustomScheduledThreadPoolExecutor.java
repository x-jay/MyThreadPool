package com.study.schdule;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class CustomScheduledThreadPoolExecutor {
	public static CustomPriorityQueue queue = new CustomPriorityQueue();
	//任务队列
	
	private Set<Worker> workers = new HashSet<Worker>();
	//线程队列
	
	private final int maxThreadCount;
	//最大线程数量
	
	public CustomScheduledThreadPoolExecutor(int maxThreadCount)
	{
		this.maxThreadCount = maxThreadCount;
	}

	//获取当前已创建的线程数量
	public  int getWorkerCount()
	{
		return workers.size();
	}
	
	//获取线程池最大数量
	public  int getMaxThreadCount()
	{
		return this.maxThreadCount;
	}
	
	//按指定的时间执行
	public void startForDate(Runnable task,Date date)
	{
		long target = date.getTime();
		long now = new Date().getTime();
		if(now>target)
		{
			throw new RuntimeException("目标时间小于当前时间");
		}
		long initial = target - now;
		schedule(task,initial);
	}
	
	//立即执行.没有延迟也没有定时
	public void schedule(Runnable task)
	{
		CustomTask ct = new CustomTask(task);
		//对Runnable进行包装.包装成自己一个CustomTask对象
		
		
		delayedExecute(ct,queue);
		//执行主方法
	}
	
	
	//延迟执行.但是只执行一次
	public void schedule(Runnable task,long initial)
	{
		CustomTask ct = new CustomTask(task,initial);
		delayedExecute(ct,queue);
	}
	
	
	//延迟执行并且重复执行
	public void scheduleAtFixedRate(Runnable task,long initial,long proied,TimeUnit unit)
	{
		CustomTask ct = new CustomTask(task,initial,proied,unit);
		delayedExecute(ct,queue);
	}
	
	public void scheduleAtFixedRate(Runnable task,long initial,long proied,int repeatCount,TimeUnit unit)
	{
		CustomTask ct = new CustomTask(task,initial,proied,repeatCount,unit);
		delayedExecute(ct,queue);
	}
	
	public void delayedExecute(CustomTask ct,CustomPriorityQueue queue)
	{
		final ReentrantLock lock = queue.lock;
		lock.lock();
		
		//如果当前线程数量小于线程池最大数量
		if(this.getWorkerCount()<maxThreadCount)
		{
			Thread t = newThread();	//创建一个新线程
			if(t!=null)
			{
				t.start();
			}
		}
		
		//将任务加入队列中.
		queue.add(ct);
		lock.unlock();
	}
	
	
	//创建新的线程
	public Thread newThread()
	{
		Worker w = new Worker(queue);
		Thread t = new Thread(w);
		if(t!=null)
		{
			w.thread = t;
			
			workers.add(w);
			//加入线程队列
		}
		return t;
	}
	
	
	//关闭线程池中的所有线程
	public void shutdownNow()
	{
		for(Iterator<Worker> it = workers.iterator();it.hasNext();)
		{
			it.next().thread.interrupt();
		}
	}
}	
