package org.spider.zwzl;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.spider.util.DateUtil;
import org.spider.util.MongoDB;
import org.spider.util.MongoUtil;
import org.spider.util.ProxyUtil;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

public class DoMetaDataSpider {

	private static MongoDB mg;

	private static ArrayBlockingQueue<String> queryDate;

	private static MongoCollection<org.bson.Document> coll = MongoUtil.getMogoDbInstance().getCollection();

	static {
		coll.createIndex(eq("申请号", 1), new IndexOptions().unique(true));
	}

	/**
	 * 元数据 爬虫主程序
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		doSpider();
	}

	@SuppressWarnings("static-access")
	public static void doSpider() {
		System.out.println("元数据，爬虫程序已启动。。。。。。。");

		mg = MongoUtil.getMogoDbInstance();
		System.out.println("数据库连接成功");
		System.out.println("自动生成查询条件");
		System.out.println("加载代理IP");
		ProxyUtil.getProxy();
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("代理ip加载完成");

		List<String> dates = DateUtil.autoGenerateDateArray(DateUtil.getDateFormat(Constants.META_DATA_END_DATE),
				Constants.META_DATA_DATE_INTERVAL, DateUtil.getDateFormat(Constants.META_DATA_START_DATE),
				new ArrayList<String>());

		queryDate = new ArrayBlockingQueue<>(dates.size());
		// 爬取指定日期范围的所有记录
		for (String date : dates) {
			queryDate.add(date);
		}

		for (int i = 0; i < Constants.METADATE_MAX_THREAD; i++) {
			MeteDataSpider meteDataSpider = new MeteDataSpider(queryDate, coll);
			meteDataSpider.start();
		}
	}
}