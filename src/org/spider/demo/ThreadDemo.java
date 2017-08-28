package org.spider.demo;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadDemo extends Thread{

	private ArrayBlockingQueue<String> queryDate;

	public ThreadDemo(ArrayBlockingQueue<String> queryDate) {
		this.queryDate = queryDate;
	}

	@Override
	public void run() {
		while (true) {
			String query = queryDate.poll();

			if (query != null) {
				handleExceptionReboot(query, 0);
			} else {
				System.out.println("线程" + Thread.currentThread().getName() + "执行结束");
				break;
			}
		}
	}

	private static void handleExceptionReboot(String query, int failTimes) {
		try {
			doSomeThing(query);
		} catch (Exception e) {
			failTimes = failTimes + 1;

			if (failTimes > 2) {
				System.out.println("失败次数过多,放弃任务:" + query);
			} else {
				System.out.println("线程:" + Thread.currentThread().getName() + "执行任务出错了, 正在重新执行!");
				handleExceptionReboot(query, failTimes);
			}
		}
	}

	/**
	 * 真正活的函数
	 * @param query
	 * @throws Exception
	 */
	private static void doSomeThing(String query) throws Exception {
		if (new Random().nextBoolean()) {
			// 有一半的概率出错
			throw new Exception();
		} else {
			System.out.println("线程:" + Thread.currentThread().getName() + "执行任务:" + query);
		}
	}
}
