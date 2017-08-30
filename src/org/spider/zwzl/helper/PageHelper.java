package org.spider.zwzl.helper;

import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.spider.exception.ForbiddenException;
import org.spider.util.HttpClientUtils;

public class PageHelper {

	/**
	 * 查询列表页
	 */
	private final static String PAGE_BASE_URL = "http://www.shanghaiip.cn/Search/searchservlet";

	/**
	 * 检索的主页
	 * @param cookie
	 * @return
	 * @throws ForbiddenException
	 */
	public static String queryNextPage(String cookie, String currentPage, String queryBy,String recordtotal, Proxy proxy) throws ForbiddenException {
		String result = "";
		try {
			Map<String, String> params = generateParams(currentPage, queryBy, recordtotal);
			result = HttpClientUtils.simpleGetInvokeWithCookie(PAGE_BASE_URL, cookie, params, proxy);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 生成分页参数
	 */
	public static Map<String, String> generateParams(String currentPage, String queryBy,String recordtotal) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put("currPage", currentPage);
		params.put("db", "us,fr,ep,gb,wo,jp,ch,ru,de,ot,cn");
		params.put("fieldlist", "");
		params.put("querydate", "");
		params.put("thesaurus", "0");
		params.put("dictionary", "0");
		params.put("fuzzyipc", "null");
		params.put("singledb", "null");
		params.put("recordtotal", recordtotal);
		params.put("selectedlist", "");
		params.put("sortField", "Default");
		params.put("conceptdic", "null");
		params.put("VarNum", "1");
		params.put("postSearchInput", "");
		params.put("advancedvalue1", "");
		params.put("pageSize", "20");
		params.put("showField", "PA");
		params.put("showField", "TI");
		params.put("showField", "PN");
		params.put("showField", "AN");
		params.put("showField", "AU");
		params.put("advancedfield1", "PN");
		params.put("postSearchLogic", "AND");
		params.put("pageSizeTop", "20");
		params.put("sortFieldTop", "Default");
		params.put("pageSizeBtm", "20");
		params.put("sortFieldBtm", "Default");
		params.put("isShowQuery", "null");
		params.put("sWriteDBQuery", "PD=[" + queryBy + "]");
		params.put("query", "PD=[" + queryBy + "]");
		params.put("hightLightQuery", "");

		return params;
	}
}
