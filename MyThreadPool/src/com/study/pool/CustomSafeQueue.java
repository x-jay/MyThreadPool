package com.study.pool;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//一个固定长度的并且线程安全的队列
/*
 * 特定：当该队列已满时.如果你再调用add添加方法.他会调用wait进行等待.一直到调用了remove方法.
 * remove方法调用了notifyAll方法唤醒了add方法..同样元素为空时.他也会调用wait.一直到调用了add方法.
 * 
 */
public class CustomSafeQueue {
	private Object[] queue;
	private int capacity;
	private int size;
	private int head;
	private int tail;
	
	public CustomSafeQueue(int cap)
	{
		capacity = (cap > 0 ) ? cap : 1;
		queue = new Object[capacity];
		head = 0;
		size = 0;
		tail = 0;
	}
	
	public synchronized int getSize()
	{
		return size;
	}
	
	public synchronized boolean isFull()
	{
		return (size == capacity);
	}
	
	public synchronized void add(Object obj) throws InterruptedException
	{
		while(isFull())
		{
			this.wait();
		}
		
		queue[head] = obj;
		head = (head+1) % capacity;
		//这样赋值的作用是.如果值相同则等于零.不相同则还是head这个数;
		size++;
		
		this.notifyAll();
	}
	
	public boolean isEmpty()
	{
		return size<1; 
	}
	
	
	public synchronized Object[] removeAll() throws InterruptedException
	{
		
		Object[] list = new Object[size];
		
		
		for(int i=0;i<list.length;i++)
		{
			list[i] = remove();
		}
		return list;
	}
	
	public synchronized Object remove() throws InterruptedException
	{
		while(size==0)
		{
			this.wait();
		}
		
		Object obj = queue[tail];
		queue[tail] = null;
		tail = (tail + 1) % capacity;
		size--;
		
		this.notifyAll();
		
		return obj;
	}
	
	
	public synchronized void printState()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("SimpleObjectFIFO:\n");
		sb.append(" capacity=" + capacity + "\n");
		sb.append(" size=" + size+ "\n");
		
		
		if(isFull())
		{
			sb.append("- Full");
		}
		else if(size==0)
		{
			sb.append("- Empty");
		}
		sb.append("\n");
		
		sb.append("head=" + head + "\n");
		sb.append("tail=" + tail + "\n");
		
		for(int i=0;i<queue.length;i++)
		{
			sb.append("queue[" + i + "]=" + queue[i] + "\n");
		}
		
		System.out.println(sb);
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		final CustomSafeQueue queue = new CustomSafeQueue(5);
		final Random rand = new Random(50);
		Runnable run1 = new Runnable()
		{
			public void run()
			{
				try {
					while(!Thread.interrupted())
					{
						TimeUnit.MICROSECONDS.sleep(200);
						int val = rand.nextInt(100);
						System.out.println(Thread.currentThread().getName() + ",已加入最新的值：" + val);
						queue.add(val);
					}
					System.out.println("run1没有被interrupt时已经运行完了");
				} catch (InterruptedException e) {
					System.out.println("run1 Interrupted");
				}
			}
		};
		
		Runnable run2 = new Runnable()
		{
			public void run()
			{
				try
				{
					while(!Thread.interrupted())
					{
						TimeUnit.MICROSECONDS.sleep(200);
						System.out.println(Thread.currentThread().getName() + ",队列已将其移除：" + queue.remove());;
					}
					System.out.println("run2没有被interrupt时已经运行完了");
				}
				catch(InterruptedException e)
				{
					System.out.println("run2 Interrupted");
				}
			}
		};
		
		Thread addThread = new Thread(run1);
		Thread delThread = new Thread(run2);
		addThread.start();
		delThread.start();
		
		TimeUnit.MILLISECONDS.sleep(10);
		addThread.interrupt();
		delThread.interrupt();
		
		
//		ExecutorService exec = Executors.newCachedThreadPool();
//		exec.execute(run1);
//		exec.execute(run2);
//		TimeUnit.SECONDS.sleep(1);
//		exec.shutdownNow();
	}
}
