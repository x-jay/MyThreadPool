package com.study.pool;

public class test {
	public static void main(String[] args) {
		Runnable run1 = new Runnable()
		{
			public void run()
			{
				System.out.println("run1");
			}
		};
		
		Runnable run2 = new Runnable()
		{
			public void run()
			{
				System.out.println("run2");
			}
		};
		
		Runnable run3 = new Runnable()
		{
			public void run()
			{
				System.out.println("run3");
			}
		};
		
		
		ThreadPool pool = new ThreadPool(3);
		
		try {
			pool.execute(run1);
			pool.execute(run2);
			pool.execute(run3);
			pool.execute(run3);
//			
//			
			pool.stopRequestIdleWorkers();
			Thread.sleep(2000);
			pool.stopRequestIdleWorkers();
			
			Thread.sleep(5000);
			pool.stopRequestAllWorkers();
//			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
}
