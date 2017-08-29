package org.spider.zwzl;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.jsoup.Jsoup;
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

/**
 * 元数据爬取者
 * @author admin
 *
 */
public class FileSpider extends Thread {

	private static Proxy proxy;

	private ArrayBlockingQueue<String> queryDate;

	public FileSpider(ArrayBlockingQueue<String> queryDate) {
		FileSpider.proxy = ProxyUtil.getProxy();
		this.queryDate = queryDate;
	}

	/**
	 * 获取当前线程的名称
	 * @return
	 */
	private static String getCurrentThreadName() {
		return "线程:" + Thread.currentThread().getName();
	}

	@Override
	public void run() {
		while (true) {
			String date = queryDate.poll();

			if (date != null) {
				// 要捕获查询无结果的异常 并且 continue
				handleExceptionReboot(date, Constants.DEFAULT_FAIL_COUNT);
			} else {
				System.out.println(getCurrentThreadName() + "执行结束");
				break;
			}
		}
	}

	private static void handleExceptionReboot(String date, int failTimes) {
		try {
			doSpider(date);
		} catch (Exception e) {
			failTimes = failTimes + 1;

			if (failTimes > Constants.MAX_FAIL_COUNT) {
				System.out.println(getCurrentThreadName() + "失败次数过多,放弃任务:" + date);
			} else {
				System.out.println(getCurrentThreadName() +  "执行任务出错了, 正在一指定查询条件" + date + "重新执行!!!");
				handleExceptionReboot(date, failTimes);
			}
		}
	}

	/**
	 * @param query
	 * @throws Exception
	 */
	private static void doSpider(String date) {
		try {
			String loginCookie = LoginHelper.doLogin(proxy);
			System.out.println(getCurrentThreadName() + "模拟登陆成功");

			String zhuanliListHtml = QuickHelper.getQuickListPage(loginCookie, date, proxy);//zhuanliListHtml 可能为 “”
			System.out.println(getCurrentThreadName() + "开始爬取：" + date + "的数据");
			spiderListPage(zhuanliListHtml, loginCookie, date, proxy);// 要抛出列表页无结果的异常
			System.out.println(getCurrentThreadName() + "结束爬取：" + date + "的数据");
		} catch (ForbiddenException forbiddenException) {
			// 要捕获代理不能用的异常--> 切换代理
			proxy = ProxyUtil.getProxy();
			System.out.println(getCurrentThreadName() + "获取 " + date + "的列表失败, 已经获取新代理：" + proxy + "正在以指定日期重新爬取：" + date);
			doSpider(date);
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
	private static void spiderListPage(String listHtml, String loginCookie, String queryBy, Proxy proxy){
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
		spiderDetailPage(loginCookie, details, queryBy, proxy);

		// 获取 recordtotal sWriteDBQuery 20170807-20170808
		if (nextHref != null) {
			spiderNextPage(loginCookie, queryBy, proxy, listPage, nextHref);
		} else {
			// 列表爬取完成 返回 重新生成查询条件
			System.out.println(getCurrentThreadName() + "当前查询条件无结果, 或者当前列表已经爬取结束.");
			return;
		}
	}

	/**
	 * 爬取下一页
	 * @param loginCookie
	 * @param queryBy
	 * @param proxy
	 * @param listPage
	 * @param nextHref
	 */
	private static void spiderNextPage(String loginCookie, String queryBy, Proxy proxy, Document listPage,
			Element nextHref) {
		String nextHrefValue = nextHref.attr("onclick").toString();
		String currentPage = nextHrefValue.substring(nextHrefValue.indexOf("currPage=") + 9,
				nextHrefValue.lastIndexOf("\";"));
		System.out.println(getCurrentThreadName() + "开始爬取第：" + currentPage + "页");

		String recordtotal = listPage.getElementById("recordtotal").val();

		// 继续爬下页
		String nextPage = null;

		try {
			nextPage = PageHelper.queryNextPage(loginCookie, currentPage, queryBy, recordtotal, proxy);
		} catch (ForbiddenException e) {
			System.out.println(getCurrentThreadName() + "爬下页的过程中代理失效, 切换代理继续爬取当前页");
			// 切换代理 继续尝试
			proxy = ProxyUtil.getProxy();
			spiderNextPage(loginCookie, queryBy, proxy, listPage, nextHref);
		}

		spiderListPage(nextPage, loginCookie, queryBy, proxy);
		System.out.println(getCurrentThreadName() + "第：" + currentPage + "页爬取结束");
	}

	/**
	 * 爬取所有详情页
	 * @throws Exception
	 */
	private static void spiderDetailPage(String loginCookie, List<String> details, String queryBy, Proxy proxy){
		for (String detail : details) {
			try {
				spiderOneDetailPage(loginCookie, queryBy, detail, Constants.DEFAULT_FAIL_COUNT, proxy);
			} catch (ForbiddenException forbiddenException) {
				System.out.println(getCurrentThreadName() + "爬取详页失败, 正在切换代理重新爬取");
				proxy = ProxyUtil.getProxy();
				spiderOneDetailPage(loginCookie, queryBy, detail, Constants.DEFAULT_FAIL_COUNT, proxy);
			} catch (Exception e) {
				System.out.println(getCurrentThreadName() + "当前详情页无法获取,跳过:");
				continue;
			}
		}
	}

	/**
	 * 爬取一张详情页
	 * <p>Title: spiderOneDetailPage</p>
	 * <p>Description: </p>
	 * @param loginCookie
	 * @param queryBy
	 * @param detail
	 * @throws Exception
	 */
	private static void spiderOneDetailPage(String loginCookie, String queryBy, String detail, int faileTimes, Proxy proxy){
		// TODO 待会要改的
		Document detailDoc = null;
		try {
			detailDoc = Jsoup.parse(QuickHelper.getDetailPage(detail, loginCookie, proxy));
		} catch (ForbiddenException e) {
			throw new ForbiddenException();
		} catch (Exception e) {
			// 找不到详情页， 调到下一页
			throw new NotFoundDetailPageException();
		}
		Element detailContent = detailDoc.getElementById("detailCont");

		if (detailContent != null) {

			Elements hrefs = detailContent.select("a[href]");
			Element downloadHref = null;

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
				onclickValue = onclickValue.substring(onclickValue.lastIndexOf(",") + 2, onclickValue.lastIndexOf(")") -1);
				String[] downloadInfo = onclickValue.split("_");
				String downloadSuffix = downloadInfo[0];
				String downloadLink = "";

				try {
					if (downloadSuffix.equals("AN")) {
						downloadLink = DownLoadHelper.doAnGetDownloadHref(loginCookie, downloadInfo[1], proxy);
					} else if (downloadSuffix.equals("PN")) {
						downloadLink = DownLoadHelper.doPnGetDownloadHref(loginCookie, downloadInfo[1], proxy);
					}

					System.out.println("下载链接为：" + downloadLink);
					// 把一次查询所有的下载链接放在安全队列里面 开启多个线程去下载  可能要防止 cookie 过期
					DownLoadHelper.doDownLoad(loginCookie, downloadLink, downloadInfo[1] + Constants.DOWNLOAD_FILE_SUFFIX, queryBy, proxy);
				} catch (ForbiddenException e) {
					System.out.println(getCurrentThreadName()+ "代理失效, 正在重新登录, 并且下载");
					proxy = ProxyUtil.getProxy();
					// TODO 重新获取cookie
					loginCookie = LoginHelper.doLogin(proxy);
					spiderOneDetailPage(loginCookie, queryBy, detail, faileTimes, proxy);
				}
			}
		} else {
			System.out.println("detailContent:为空 , 正在重试:当前第 " + faileTimes + " 次");
			if (faileTimes >= Constants.MAX_FAIL_COUNT) {
				throw new NotFoundDetailPageException();
			} else {
				faileTimes += 1;
				spiderOneDetailPage(loginCookie,queryBy ,detail, faileTimes, proxy);
			}
		}
	}

}