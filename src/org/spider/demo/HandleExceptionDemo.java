package org.spider.demo;

public class HandleExceptionDemo {
	
	public static void main(String[] args) {
		ReBoot();
	}
	
	private static void ReBoot() {
		try {
			doSomeThing();
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ReBoot();
		}
	}
	
	public static void doSomeThing() throws Exception {
		System.out.println(1);
		throw new Exception();
	}

}
