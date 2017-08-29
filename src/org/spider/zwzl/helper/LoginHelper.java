package org.spider.zwzl.helper;

import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.spider.exception.ForbiddenException;
import org.spider.util.HttpClientUtils;

public class LoginHelper {

	private static final String LOGIN_URL = "http://www.shanghaiip.cn/Search/login.do?keyword&ztsjk=liuhaipeng&active=zljs&schTag=0&creatorid=guest&utype=3&userid=3&username=guest&dblist=us%3Bfr%3Bep%3Bgb%3Bwo%3Bjp%3Bch%3Bru%3Bde%3Bot%3Bcn&hiskeepday=0&hiskeepcount=0&opid=sch_bilang%2Csch_autokeyword%2Csch_lawstate%2Csch_report%2Csch_dblist%2Csch_conceptsch%2Csch_ipcstat%2Csch_fullTextdown%2Csch_tiludown%2Csch_statistic&pid=1&count=1468088&zwzmc&zwzdz&searchscope&isshowsubdb=1&style=1&sxzljs=-1";

	public static String doLogin (Proxy proxy) throws ForbiddenException {
		try {
			String loginResult = HttpClientUtils.getLoginCookie(LOGIN_URL, proxy);

			return loginResult.substring(0, loginResult.lastIndexOf(";"));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ForbiddenException e) {
			throw e;
		}

		return "";
	}
}