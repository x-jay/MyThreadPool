package com.study.schdule;

import java.net.ServerSocket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class test {
	public static void main(String[] args) {
		final CustomScheduledThreadPoolExecutor executor = new CustomScheduledThreadPoolExecutor(4);
		Runnable run = new Runnable()
		{
			public void run()
			{
				System.out.println("shit");
//				System.out.println(Thread.currentThread().getName());
			}
		};
		
		Runnable run1 = new Runnable()
		{
			public void run()
			{
				System.out.println("run1");
//				System.out.println(executor.getWorkerCount());
//				System.out.println(Thread.currentThread().getName());
			}
		};
		
		Runnable run2 = new Runnable()
		{
			public void run()
			{
				System.out.println("run2");
			}
		};
		
		
		Runnable shutdown = new Runnable()
		{
			public void run()
			{
				executor.shutdownNow();
			}
		};
//		System.out.println(Thread.currentThread().getName());
		
		executor.schedule(run);
		//执行运行
		
//		executor.schedule(run2,8000);
		//带延迟运行
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2014, 2,9,21,19,50);
//		executor.startForDate(run1, calendar.getTime());
		//指定日期定时运行
//		executor.scheduleAtFixedRate(run1, 4000, 1000,5, TimeUnit.MILLISECONDS);
		//延迟运行.并且指定重复次数.
//		ThreadPoolExecutor exec;
//		executor.scheduleAtFixedRate(run, 4000, 1000, TimeUnit.MILLISECONDS);
		//延迟运行.永久重复
//		executor.scheduleAtFixedRate(run1, 3000, 3000, TimeUnit.MILLISECONDS);
		
		
		//调用用于关闭线程池的方法.如果不调用该方法则会一直执行.
		executor.scheduleAtFixedRate(shutdown, 10000, 2000, TimeUnit.MILLISECONDS);
	}
}
