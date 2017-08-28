package org.spider.demo;

import java.util.concurrent.ArrayBlockingQueue;

public class TestThread {
	private static ArrayBlockingQueue<String> queryDate;

	public static void main(String[] args) {

		queryDate = new ArrayBlockingQueue<String>(100);

		for (int i = 0; i < 100 ; i++) {
			queryDate.add("任务" + i + "");
		}

		ThreadDemo trDemo1 = new ThreadDemo(queryDate);
		ThreadDemo trDemo2 = new ThreadDemo(queryDate);
		ThreadDemo trDemo3 = new ThreadDemo(queryDate);

		trDemo1.start();
		trDemo2.start();
		trDemo3.start();
	}
}
