package org.spider.zwzl;

public class Start {

	public static void main(String[] args) {
		System.out.println("开始专利爬取");
		// DoFileSpider.doSpider();
		DoMetaDataSpider.doSpider();
	}
}
