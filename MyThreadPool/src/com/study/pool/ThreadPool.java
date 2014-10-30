package com.study.pool;


//模拟线程池
public class ThreadPool {
	private CustomSafeQueue idleWorkers;
	private ThreadPoolWorker[] workerList;
	
	public ThreadPool(int numberOfThreads)
	{
		numberOfThreads = Math.max(1, numberOfThreads);
		
		idleWorkers = new CustomSafeQueue(numberOfThreads);
		workerList = new ThreadPoolWorker[numberOfThreads];
		
		for(int i=0;i<workerList.length;i++)
		{
			workerList[i] = new ThreadPoolWorker(idleWorkers);
		}
	}
	
	public void execute(Runnable target) throws InterruptedException
	{
		ThreadPoolWorker worker = (ThreadPoolWorker)idleWorkers.remove();
		worker.process(target);
	}
	
	public void stopRequestIdleWorkers()
	{
		try
		{
			Object[] idle = idleWorkers.removeAll();
			for(int i=0;i<idle.length;i++)
			{
				((ThreadPoolWorker)idle[i]).stopRequest();
			}
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
	
	public void stopRequestAllWorkers()
	{
		stopRequestIdleWorkers();
		
		try
		{
			Thread.sleep(250);
		}
		catch(InterruptedException e)
		{
			
		}
		
		for(int i=0;i<workerList.length;i++)
		{
			if(workerList[i].isAlive())
			{
				workerList[i].stopRequest();
			}
		}
	}
		
}

class ThreadPoolWorker
{
	private static int nextWorkerID = 0;
	private int workerID;
	private CustomSafeQueue idleWorkers;
	//将当前处理任务的线程加入到该队列..通常会有这种情况.handoffBox队列将任务加入了.但是有些任务可能需要花更长时间才能执行完成.但后面的任务已经执行完了.
	//然而队列是数组形式.通过顺序来存放的.所以为了防止这种情况.每次要加入一个任务.就必须将该线程加入到idleWorkers队列.如果idleWorkers队列已经满了.则表示
	//还有任务没有执行完.所以add方法会调用wait进行等待.所以就解决了这种问题.
	
	private CustomSafeQueue handoffBox;
	private Thread internalThread;
	private volatile boolean noStopRequested;
	
	
	public ThreadPoolWorker(CustomSafeQueue idleWorkers)
	{
		this.idleWorkers = idleWorkers;
		
		workerID = getNextWorkerID();
		handoffBox = new CustomSafeQueue(1);
		
		
		noStopRequested = true;
		
		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					runWork();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		internalThread = new Thread(run);
		internalThread.start();
	}
	
	public static synchronized int getNextWorkerID()
	{
		int id = nextWorkerID;
		nextWorkerID++;
		return id;
	}
	
	public void process(Runnable target) throws InterruptedException
	{
		handoffBox.add(target);
	}
	
	private void runWork() throws InterruptedException
	{
		try
		{
			while(noStopRequested)
			{
				System.out.println("workerID=" + workerID + ", ready for work");
				
				idleWorkers.add(this);
				
				Runnable run = (Runnable)handoffBox.remove();
				//每当有任务来就会接收,否则就阻塞
				
				System.out.println("workerID=" + workerID + ",starting execution of new Runnable：" + run);
				runIt(run);
			}
		}
		catch(InterruptedException e)
		{
//			e.printStackTrace();
			//在自己有意调用线程的结束时.InterruptedException这个错误不应该被显示.因为这个错误只要你关闭线程都会产生.
			Thread.currentThread().interrupt();
		}
	}
	
	private void runIt(Runnable run)
	{
		try
		{
			run.run();
		}
		catch(Exception e)
		{
			System.out.println("Uncaught exception fell through from run()");
			e.printStackTrace();
		}
		finally
		{
			Thread.interrupted();
		}
	}
	
	public void stopRequest()
	{
		System.out.println("workerID=" + workerID + ",stopRequest() received.");
		noStopRequested = false;
		internalThread.interrupt();
	}
	
	public boolean isAlive()
	{
		return internalThread.isAlive();
	}
}
