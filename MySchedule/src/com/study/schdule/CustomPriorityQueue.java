package com.study.schdule;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//该队列是一个优先级队列.并且也是一个阻塞队列
public class CustomPriorityQueue extends PriorityQueue<CustomTask>{
	public ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private boolean isOk = false;
	
	@Override
	public boolean add(CustomTask e) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try
		{
			CustomTask ct = this.peek();
//			int val = this.size();
			boolean res = super.offer(e);
			//判断是否有线程进入了永久休眠
			//调用方法进行唤醒所有.以免线程进入永久休眠(等待)
			//(如果没有进入永久休眠也不会有影响.类似于notifyAll)
//			if(ct==null || e.compareTo(ct)<0)
			
			if(ct==null && isOk)
			{
				condition.signalAll();
			}
			return res;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public CustomTask take() throws InterruptedException
	{
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		//和lock不同的是当阻塞时可被interrupt的锁.
		
//		lock.lock();
		try
		{
			for(;;)
			{
				CustomTask ct = this.poll();
				//如果线程为空.则进入永久等待.
				if(ct==null)
				{
//					Thread.yield();
					isOk=true;
					
					condition.await();
				}
				else
				{
//					long delay = ct.getDelay(TimeUnit.NANOSECONDS);
//					if(delay>0)
//					{
////						long tl = condition.awaitNanos(ct.time);
//						condition.await(1000, TimeUnit.MILLISECONDS);
//					}
//					else
//					{
//						CustomTask task = this.peek();
//						return task;
//					}
					
					if(ct.time>0)
					{
						condition.await(ct.time, ct.getUnit());
					}
					CustomTask task = ct;
					
//					assert task!=null;
//					if(this.size()!=0)
//					{
//						System.out.println("sdfdsf");
//						condition.signalAll();
//					}
					return task;
				}
			}
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		finally
		{
			lock.unlock();
		}
		return null;
	}
	
	
	//获取头部元素.但是不会删除.
	@Override
	public synchronized CustomTask peek() {
		return super.peek();
	}
	
	//获取头部元素.会删除元素
	@Override
	public synchronized CustomTask poll() {
		return super.poll();
	}
	
	
	//添加元素
	@Override
	public boolean offer(CustomTask e) {
		return super.offer(e);
	}
}
