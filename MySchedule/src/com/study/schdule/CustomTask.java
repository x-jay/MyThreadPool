package com.study.schdule;

import java.util.concurrent.TimeUnit;

public class CustomTask implements Comparable<CustomTask>,Runnable{
	private long proied;
	//重复的时间间隔
	
	public long time;
	//延迟运行的时间
	
	private Runnable task;
	//运行任务
	
	private TimeUnit unit;
	//时间单位
	
	public int repeatCount;
	//重复次数
	
	public Runnable getTask() {
		return task;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public CustomTask(Runnable task)
	{
		this.time = 0;
		this.proied = 0;
		this.unit = TimeUnit.MILLISECONDS;
		this.task = task;
		this.repeatCount = 0;
	}
	
	public CustomTask(Runnable task,long initial)
	{
		this(task);
		this.time = initial;
		this.unit = TimeUnit.MILLISECONDS;
		this.proied = 0;
		this.repeatCount = 0;
	}

	public CustomTask(Runnable task,long initial,long proied,TimeUnit unit)
	{
		this(task,initial);
		this.proied = proied;
		this.unit = unit;
		this.repeatCount = 0;
	}
	
	public CustomTask(Runnable task,long initial,long proied,int repeatCount,TimeUnit unit)
	{
		this(task,initial,proied,unit);
		this.repeatCount = repeatCount;
	}
	
	
	@Override
	public int compareTo(CustomTask ct) {
		return proied>ct.proied?1:(proied<ct.proied?-1:0);
	}

	@Override
	public void run() {
		task.run();
		
		if(this.proied!=0 && this.repeatCount-->1)
		{
			time = proied;
			CustomScheduledThreadPoolExecutor.queue.offer(this);
		}
	}
}
