package org.spider.zwzl.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.spider.util.HttpClientUtils;

/**
 * 查询帮助者
 * @author admin
 *
 */
public class QuickHelper {

	/**
	 * 查询主页
	 */
	private final static String QUICK_URL = "http://www.shanghaiip.cn/Search/quick/quick.jsp";

	/**
	 * 查询列表页
	 */
	private final static String QUICK_PAGE_LIST_URL = "http://www.shanghaiip.cn/Search/quick.do";

	/**
	 * 查询列表页
	 */
	private final static String QUICK_DETAIL_BASE_URL = "http://www.shanghaiip.cn/Search/detail/detail.jsp?selectedlist=";

	/**
	 * 检索的主页
	 * @param cookie
	 * @return
	 */
	public static String getQuickMainPage(String cookie) {
		try {
			String result = HttpClientUtils.simpleGetInvokeWithCookie(QUICK_URL, cookie);

			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 详情页
	 * @param cookie
	 * @return
	 */
	public static String getDetailPage(String id, String cookie) {
		try {
			String result = HttpClientUtils.simpleGetInvokeWithCookie(QUICK_DETAIL_BASE_URL + id, cookie);

			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 查询
	 * @param cookie
	 * @param params
	 * @return
	 */
	public static String getQuickListPage(String cookie, String dateRange) {
		try {
			Map<String, String> params = getQueryListParams(dateRange);

			String result = HttpClientUtils.simpleGetInvokeWithCookie(QUICK_PAGE_LIST_URL, cookie, params);

			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static Map<String, String> getQueryListParams(String dateRange) {
		String[] dates = dateRange.split("-");
		Map<String, String> params = new HashMap<>();
		params.put("selCountry1", "CN");
		params.put("radTime", "on");
		params.put("strBegDate", dates[0]);
		params.put("strEndDate", dates[1]);
		params.put("selDataBase1", "");
		params.put("strAUPA", "");
		params.put("strTIAB", "");
		params.put("strFTEXT", "");
		params.put("strPNANPR", "");
		params.put("selCountry", "CN");
		params.put("selDataBase", "ALL");
		params.put("radYear", "0");
		params.put("radDate", "1");
		params.put("chkBilingual", "0");
		params.put("chkThesaurus", "0");
		params.put("chkIPC", "0");
		params.put("pageFlag", "quick");
		params.put("selDate", "PD");
		params.put("selYear", "");

		return params;
	}

}