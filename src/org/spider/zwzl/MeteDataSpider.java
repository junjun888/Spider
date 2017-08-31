package org.spider.zwzl;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.spider.exception.ForbiddenException;
import org.spider.util.ProxyUtil;
import org.spider.zwzl.exception.NotFoundDetailPageException;
import org.spider.zwzl.helper.DownLoadHelper;
import org.spider.zwzl.helper.LoginHelper;
import org.spider.zwzl.helper.PageHelper;
import org.spider.zwzl.helper.QuickHelper;

import com.mongodb.client.MongoCollection;

/**
 * 元数据爬取者
 *
 * @author admin
 *
 */
public class MeteDataSpider extends Thread {

	// TODO 要改成线程独有的变量
	// private static Proxy proxy;
	private static ThreadLocal<Proxy> proxys = new ThreadLocal<Proxy>();

	// TODO 要改成线程独有的变量
	// private static String loginCookie;
	private static ThreadLocal<String> loginCookies = new ThreadLocal<String>();

	private static MongoCollection<org.bson.Document> coll;

	private ArrayBlockingQueue<String> queryDate;

	@SuppressWarnings("static-access")
	public MeteDataSpider(ArrayBlockingQueue<String> queryDate, MongoCollection<org.bson.Document> coll) {
		proxys.set(ProxyUtil.getProxy());
		this.queryDate = queryDate;
		this.coll = coll;
	}

	/**
	 * 获取当前线程的名称
	 *
	 * @return
	 */
	private static String getCurrentThreadName() {
		return "线程:" + Thread.currentThread().getName() + " ";
	}

	@Override
	public void run() {
		while (true) {
			String date = queryDate.poll();

			if (date != null) {
				// 要捕获查询无结果的异常 并且 continue
				handleExceptionReboot(date);
			} else {
				System.out.println(getCurrentThreadName() + "执行结束");
				break;
			}
		}
	}

	private static void handleExceptionReboot(String date) {
		try {
			doSpider(date);
		} catch (Exception e) {
			System.out.println(getCurrentThreadName() + "执行任务出错了, 正切换代理,并且以指定查询条件" + date + "重新执行!");
			proxys.set(ProxyUtil.getProxy());
			doSpider(date);
		}
	}

	/**
	 * @param query
	 * @throws Exception
	 */
	private static void doSpider(String date) {
		try {
			loginCookies.set(LoginHelper.doLogin(proxys.get()));
			System.out.println(getCurrentThreadName() + "模拟登陆成功");
			String zhuanliListHtml = QuickHelper.getQuickListPage(loginCookies.get(), date, proxys.get());
			System.out.println(getCurrentThreadName() + "开始爬取：" + date + "的数据");
			if (StringUtil.isBlank(zhuanliListHtml)) {
				System.out.println(getCurrentThreadName() + "爬取列表首页失败， 正在切换代理重新爬取");
				throw new ForbiddenException();
			}
			spiderListPage(zhuanliListHtml, date);// 要抛出列表页无结果的异常
			System.out.println(getCurrentThreadName() + "结束爬取：" + date + "的数据");
		} catch (ForbiddenException forbiddenException) {
			// 要捕获代理不能用的异常--> 切换代理
			proxys.set(ProxyUtil.getProxy());
			System.out
					.println(getCurrentThreadName() + "获取 " + date + "的列表失败, 已经获取新代理：" + proxys.get() + "正在以指定日期重新爬取：" + date);
			doSpider(date);
		} catch (Exception e) {
			System.out.println("doSpider:");
			e.printStackTrace();
			throw e;
		}
		// 要捕获没有查询到列表页的异常
	}

	/**
	 * 爬取列表页
	 *
	 * @param listHtml
	 * @param loginCookie
	 * @throws ForbiddenException
	 */
	private static void spiderListPage(String listHtml, String queryBy) {
		try {
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

			// 爬取每一个详细页
			spiderDetailPage(details, queryBy);

			// 获取 recordtotal sWriteDBQuery 20170807-20170808
			if (nextHref != null) {
				spiderNextPage(queryBy, listPage, nextHref);
			} else {
				// 列表爬取完成 返回 重新生成查询条件
				System.out.println(getCurrentThreadName() + "当前查询条件无结果, 或者当前列表已经爬取结束.");
				return;
			}
		} catch (ForbiddenException e) {
			changePorxy();
			System.out.println(getCurrentThreadName() + "代理错误爬取列表页出错, 正在切换代理重新爬取");
			spiderListPage(listHtml, queryBy);
		} catch (NullPointerException e) {
			changePorxy();
			System.out.println(getCurrentThreadName() + "空指针,爬取列表页出错, 正在切换代理重新爬取");
			spiderListPage(listHtml, queryBy);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(getCurrentThreadName() + ": 抛出了一次");
			e.printStackTrace();
		}
	}

	/**
	 * 改变当前线程的代理, 同时更新改线程的cookie
	 * @return
	 */
	private static void changePorxy() {
		proxys.set(ProxyUtil.getProxy());
		loginCookies.set(LoginHelper.doLogin(proxys.get()));
	}

	/**
	 * 爬取下一页
	 *
	 * @param loginCookie
	 * @param queryBy
	 * @param proxy
	 * @param listPage
	 * @param nextHref
	 */
	private static void spiderNextPage(String queryBy, Document listPage,
			Element nextHref) {
		String nextHrefValue = nextHref.attr("onclick").toString();
		String currentPage = nextHrefValue.substring(nextHrefValue.indexOf("currPage=") + 9,
				nextHrefValue.lastIndexOf("\";"));
		System.out.println(getCurrentThreadName() + "开始爬取第：" + currentPage + "页");
		String recordtotal = listPage.getElementById("recordtotal").val();

		// 继续爬下页
		String nextPage = null;

		try {
			nextPage = PageHelper.queryNextPage(loginCookies.get(), currentPage, queryBy, recordtotal, proxys.get());
		} catch (ForbiddenException e) {
			System.out.println(getCurrentThreadName() + "爬下页的过程中代理失效, 切换代理继续爬取当前页");
			changePorxy();
			spiderNextPage(queryBy, listPage, nextHref);
		}

		spiderListPage(nextPage, queryBy);
		System.out.println(getCurrentThreadName() + "第：" + currentPage + "页爬取结束");
	}

	/**
	 * 爬取所有详情页
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	private static void spiderDetailPage(List<String> details, String queryBy) {
		for (String detail : details) {
			try {
				// 爬取到一个详情页面 睡眠一会
				try {
					Thread.currentThread().sleep(Constants.DETAIL_PAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				spiderOneDetailPage(detail, Constants.DEFAULT_FAIL_COUNT);
			} catch (ForbiddenException forbiddenException) {
				System.out.println(getCurrentThreadName() + "爬取详页失败, 正在切换代理重新爬取");
				changePorxy();
				spiderOneDetailPage(detail, Constants.DEFAULT_FAIL_COUNT);
			} catch (NotFoundDetailPageException e) {
				System.out.println(getCurrentThreadName() + "多次尝试无法爬取，爬取详页失败, 跳过。");
				continue;
			} catch (Exception e) {
				try {
					Thread.currentThread().sleep(Constants.DETAIL_PAGE_THREAD_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println(getCurrentThreadName() + "当前详情页无法获取,跳过:");
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
	 * @throws NotFoundDetailPageException
	 * @throws Exception
	 */
	private static void spiderOneDetailPage(String detail, int faileTimes)
			throws ForbiddenException, NotFoundDetailPageException {
		long startTime = System.currentTimeMillis();
		System.out.println(getCurrentThreadName() + "爬取元数据：");
		Document detailDoc = null;
		try {
			detailDoc = Jsoup.parse(QuickHelper.getDetailPage(detail, loginCookies.get(), proxys.get()));
		} catch (ForbiddenException e) {
			System.out.println(getCurrentThreadName() + "爬取详情页的时候代理不可用了, 正在切换代理重新爬取当前详情页.");
			changePorxy();
			spiderOneDetailPage(detail, faileTimes);
		} catch (Exception e) {
			// 找不到详情页， 调到下一页
			System.out.println(getCurrentThreadName() + "爬取详情页的时候出错了");
			e.printStackTrace();
		}

		if (detailDoc == null) {
			System.out.println(getCurrentThreadName() + "爬取详页" + detail + "失败, 正在切换代理重新爬取");
			changePorxy();
			spiderOneDetailPage(detail, faileTimes);
		} else {
			Element detailContent = detailDoc.getElementById("detailCont");

			if (detailContent != null) {
				System.out.println(getCurrentThreadName() + "  详情页不为空");
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

				// 获取下载链接
				Elements hrefs = detailContent.select("a[href]");
				Element downloadHref = null;
				String downloadLink = "";

				// 在详情页寻到到下载链接
				for (Element element : hrefs) {
					String childNodeValue = element.childNode(0).toString().trim();

					if (childNodeValue.equals("全文浏览")) {
						downloadHref = element;
						break;
					}
				}
				if (downloadHref != null) {
					String onclickValue = downloadHref.attr("onclick");
					onclickValue = onclickValue.substring(onclickValue.lastIndexOf(",") + 2,
							onclickValue.lastIndexOf(")") - 1);
					String[] downloadInfo = onclickValue.split("_");
					String downloadSuffix = downloadInfo[0];

					if (downloadSuffix.equals("AN")) {
						downloadLink = DownLoadHelper.doAnGetDownloadHref(loginCookies.get(), downloadInfo[1], proxys.get());
					} else if (downloadSuffix.equals("PN")) {
						downloadLink = DownLoadHelper.doPnGetDownloadHref(loginCookies.get(), downloadInfo[1], proxys.get());
					}
					params.put("下载链接", downloadLink);
				}

				org.bson.Document obj = new org.bson.Document();

				// TODO 保存 params 中的元数据
				System.out.println(getCurrentThreadName() + "爬取到元数据：" + params);
				// obj.put(params.get("申请号"), params);
				for (Entry<String, String> entry : params.entrySet()) {
					obj.append(entry.getKey(), entry.getValue());
				}
				try {
					coll.insertOne(obj);
					System.out.println(getCurrentThreadName() + params.get("申请号") + "元数据保存成功");
				} catch (Exception e) {
				}

				System.out.println(getCurrentThreadName() + "爬取结束, 耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
			} else {
				System.out.println(getCurrentThreadName() + "detailContent:为空 , 正在重试:当前第 " + faileTimes + " 次");
				if (faileTimes >= Constants.MAX_FAIL_COUNT) {
					throw new NotFoundDetailPageException();
				} else {
					faileTimes += 1;
					spiderOneDetailPage(detail, faileTimes);
				}
			}
		}
	}
}