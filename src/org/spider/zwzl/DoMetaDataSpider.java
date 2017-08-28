package org.spider.zwzl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.spider.util.DateUtil;
import org.spider.util.MongoDB;
import org.spider.zwzl.exception.NotFoundDetailPageException;
import org.spider.zwzl.exception.SpiderCrashException;
import org.spider.zwzl.helper.LoginHelper;
import org.spider.zwzl.helper.PageHelper;
import org.spider.zwzl.helper.QuickHelper;

public class DoMetaDataSpider {

	private static MongoDB mg;

	/**
	 * 元数据 爬虫主程序
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		HandleExceptionAndReboot(Constants.DEFAULT_END_DATE);
	}

	/**
	 * 爬虫挂了会自动重启
	 * @param endDate
	 */
	private static void HandleExceptionAndReboot(String endDate) {
		try {
			doSpider(endDate);
		} catch (SpiderCrashException e) {
			try {
				Thread.sleep(Constants.REBOOT_WAIT_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("爬虫挂掉了：正再以指定查询条件重新启动。。。：" + e.getEndDate());
			HandleExceptionAndReboot(e.getEndDate());
		}
	}

	private static void doSpider(String endDate) {
		System.out.println("爬虫程序已启动。。。。。。。");

		// mg = MongoUtil.getMogoDbInstance();
		System.out.println("数据库连接成功");
		// 挂了重启查询数据库 并且重启爬虫 重启爬虫的时候的记录日志信息 并且打印

		// cookie 失效后还得重新生成cookie
		String loginCookie = LoginHelper.doLogin();
		System.out.println("模拟登陆成功");
		startSpider(endDate, loginCookie);
	}

	/**
	 * 爬虫程序启动
	 */
	private static void startSpider(String endDate, String loginCookie) throws SpiderCrashException {
		// 动态生成查询条件 然后不断的去爬

		List<String> dates = DateUtil.autoGenerateDateArray(DateUtil.getDateFormat(Constants.META_DATA_END_DATE),
				Constants.META_DATA_DATE_INTERVAL, DateUtil.getDateFormat(Constants.META_DATA_START_DATE),
				new ArrayList<String>());

		// 爬取指定日期范围的所有记录
		for (String date : dates) {
			try {
				String zhuanliListHtml = QuickHelper.getQuickListPage(loginCookie, date);
				System.out.println("开始爬取：" + date + "的数据");
				try {
					spiderListPage(zhuanliListHtml, loginCookie, date);
				} catch (Exception e) {
					throw new SpiderCrashException(date);
				}
				System.out.println("结束爬取：" + date + "的数据");
			} catch (Exception e) {
				// 爬取列表页失败
				// 处理异常
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * 爬取列表页
	 *
	 * @param listHtml
	 * @param loginCookie
	 */
	private static void spiderListPage(String listHtml, String loginCookie, String queryBy) {
		Document listPage = Jsoup.parse(listHtml);
		Element mianListContent = listPage.getElementById("notStat2");

		Elements allHref = mianListContent.select("a[href]");
		Element nextHref = null;

		List<String> details = new ArrayList<String>();

		// 找到了下页 链接 和当前页的所有详情
		for (Element element : allHref) {
			String childNodeValue = element.childNode(0).toString().trim();

			if (childNodeValue.equals("[尾页]") || childNodeValue.equals("[下一页]") || childNodeValue.equals("[上一页]")
					|| childNodeValue.equals("[首页]")) {
				if (childNodeValue.equals("[下一页]")) {
					nextHref = element;
				}
			} else {
				String detail = element.attr("href").substring(element.attr("href").lastIndexOf("(") + 2,
						element.attr("href").lastIndexOf(")") - 1);
				details.add(detail);
			}
		}

		spiderDetailPage(loginCookie, details, queryBy);

		// 获取 recordtotal sWriteDBQuery 20170807-20170808
		if (nextHref != null) {
			String nextHrefValue = nextHref.attr("onclick").toString();
			String currentPage = nextHrefValue.substring(nextHrefValue.indexOf("currPage=") + 9,
					nextHrefValue.lastIndexOf("\";"));
			System.out.println("开始爬取第：" + currentPage + "页");

			String recordtotal = listPage.getElementById("recordtotal").val();

			// 继续爬下页
			String nextPage = PageHelper.queryNextPage(loginCookie, currentPage, queryBy, recordtotal);
			spiderListPage(nextPage, loginCookie, queryBy);
			System.out.println("第：" + currentPage + "页爬取结束");
		} else {
			// 列表爬取完成 返回 重新生成查询条件
			System.out.println("当前查询条件无结果, 或者当前列表已经爬取结束.");
			return;
		}
	}

	/**
	 * 爬取所有详情页
	 */
	private static void spiderDetailPage(String loginCookie, List<String> details, String queryBy) {
		for (String detail : details) {
			try {
				// 爬取到一个详情页面 睡眠一会
				try {
					Thread.sleep(Constants.DETAIL_PAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				spiderOneDetailPage(loginCookie, detail, Constants.DEFAULT_FAIL_COUNT);
			} catch (Exception e) {
				// 找不到详情页 跳到下一页
				try {
					Thread.sleep(Constants.DETAIL_PAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("当前详情页无法获取,跳过:");
				continue;
			}
		}
	}

	/**
	 * 爬取一张详情页
	 *
	 * @param loginCookie
	 * @param detail
	 * @param faileTimes
	 * @throws Exception
	 */
	private static void spiderOneDetailPage(String loginCookie, String detail, int faileTimes) throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("爬取元数据：");
		Document detailDoc = Jsoup.parse(QuickHelper.getDetailPage(detail, loginCookie));
		System.out.println("  获取详情页");
		Element detailContent = detailDoc.getElementById("detailCont");

		if (detailContent != null) {
			System.out.println("  详情页不为空");
			Elements tds = detailContent.getElementsByTag("td");
			Map<String, String> params = new LinkedHashMap<String, String>();

			params.put("申请号", tds.get(1).childNode(0).toString());
			for (Element element : tds) {
				if (element.attr("id") != null && element.attr("id") != "") {
					String paramName = element.previousElementSibling().childNode(0).toString();
					paramName = paramName.replace("：", "").trim();
					params.put(paramName, element.childNode(0).toString());
				}
			}

			/*
			 * MongoCollection<org.bson.Document> coll = mg.getCollection();
			 * org.bson.Document obj = new org.bson.Document();
			 */
			// TODO 保存 params 中的元数据
			System.out.println(params);
			/*
			 * obj.put(params.get("sqh"), params);
			 *
			 * try { coll.insertOne(obj); System.out.println("元数据保存成功"); } catch
			 * (Exception e) { e.printStackTrace(); }
			 */
			System.out.println("爬取结束, 耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
		} else {
			System.out.println("detailContent:为空 , 正在重试:当前第 " + faileTimes + " 次");
			if (faileTimes >= Constants.MAX_FAIL_COUNT) {
				throw new NotFoundDetailPageException();
			} else {
				faileTimes += 1;
				spiderOneDetailPage(loginCookie, detail, faileTimes);
			}
		}
	}
}
