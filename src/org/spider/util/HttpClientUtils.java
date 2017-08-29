package org.spider.util;

import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.spider.exception.ForbiddenException;

public final class HttpClientUtils {

	/**
	 * 连接超时时间
	 */
	public static final int CONNECTION_TIMEOUT_MS = 20000;

	/**
	 * Cookie
	 */
	public static final String COOKIE = "Cookie";

	/**
	 * 读取数据超时时间
	 */
	public static final int SO_TIMEOUT_MS = 15000;

	/**
	 * 连接请求最大失败次数
	 */
	public static final int MAX_FAIL_COUNT = 1;

	/**
	 * 请求默认失败次数
	 */
	public static final int DETAULT_FAIL_COUNT = 0;

	/**
	 * 连接失败等待时长
	 */
	public static final int CONNECT_FAIL_WAIT_TIME = 5000;

	public static final String CONTENT_TYPE_JSON_CHARSET = "application/json;charset=gbk";

	public static final String CONTENT_TYPE_XML_CHARSET = "application/xml;charset=gbk";

	/**
	 * httpclient读取内容时使用的字符集
	 */
	public static final String CONTENT_CHARSET = "GBK";

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static final Charset GBK = Charset.forName(CONTENT_CHARSET);

	/*static {
		System.out.println("扫描配置文件。。。");
		System.out.println("加载代理ip。。。");
		List<Proxy> proxyList = ProxyUtil.getProxyList();
		List<Proxy> usefulProxys = new ArrayList<Proxy>();

		System.out.println("代理ip可用性测试开始。。。");
		for (Proxy proxy : proxyList) {
			boolean isAvailable = false;
			try {
				isAvailable = HttpClientUtils.testConn(Constants.TEST_CONN_URL, proxy);
				if (isAvailable) {
					System.out.println("该代理可用" + proxy);
					usefulProxys.add(proxy);
				} else {
					System.out.println("该代理不可用" + proxy);
				}
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}

		proxyQueue = new ArrayBlockingQueue<>(usefulProxys.size());
		for (Proxy proxy : usefulProxys) {
			proxyQueue.add(proxy);
		}

		System.out.println("代理ip可用性测试结束。。。");
	}*/

	/**
	 * 测试链接，简单get调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static boolean testConn(String url, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException {
		HttpClient client = HttpClientUtils.buildHttpClient(true);

		HttpGet get = HttpClientUtils.buildHttpGet(url, null);
		// TODO 根据代理设置http请求
		get.setConfig(HttpClientUtils.buildRequestConfig(proxy));

		HttpResponse response = client.execute(get);
		int statusCode = response.getStatusLine().getStatusCode();

		if (HttpStatus.SC_OK == statusCode) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 简单get调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	/*public static String simpleGetInvoke(String url, Map<String, String> params)
			throws ClientProtocolException, IOException, URISyntaxException {
		return simpleGetInvoke(url, params,CONTENT_CHARSET, HttpClientUtils.DETAULT_FAIL_COUNT, HttpClientUtils.defaultProxy);
	}*/

	/**
	 * 简单get调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	/*public static String simpleGetInvoke(String url)
			throws ClientProtocolException, IOException, URISyntaxException {
		return simpleGetInvoke(url, null);
	}*/

	/**
	 * 简单get调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	/*public static String simpleGetInvoke(String url, Map<String, String> params,String charset, int faileTimes, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException {

		HttpClient client = buildHttpClient(true);

		HttpGet get = buildHttpGet(url, params);
		// 设置代理
		get.setConfig(HttpClientUtils.buildRequestConfig(proxy));

		HttpResponse response = null;

		try {
			response = client.execute(get);
			assertStatus(response);
		} catch (ForbiddenException forbiddenException) {
			simpleGetInvokeHandler(url, params, charset, faileTimes, proxy);
		}  catch (Exception e) {
			if (faileTimes >= HttpClientUtils.MAX_FAIL_COUNT) {
				System.out.println("请求发送失败， 第：" + faileTimes + "次, 更换ip重新请求");
				simpleGetInvokeHandler(url, params, charset, faileTimes, proxy);
			} else {
				try {
					Thread.sleep(HttpClientUtils.CONNECT_FAIL_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				faileTimes += 1;
				simpleGetInvoke(url, params, charset, faileTimes, HttpClientUtils.defaultProxy);
			}
		}

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			String returnStr = EntityUtils.toString(entity,charset);
			return returnStr;
		}
		return null;
	}
*/
	/**
	 * 简单get调用  携带 cookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String simpleGetInvokeWithCookie(String url,String cookie, Map<String, String> params,String charset, int faileTimes, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException, ForbiddenException {

		HttpClient client = buildHttpClient(true);

		HttpGet get = buildHttpGet(url, params);
		get.setHeader(COOKIE, cookie);
		get.setConfig(HttpClientUtils.buildRequestConfig(proxy));
		HttpResponse response = null;

		try {
			response = client.execute(get);
			assertStatus(response);

		} catch (ConnectTimeoutException connectTimeoutException) {
			throw new ForbiddenException();
		} catch (ForbiddenException forbiddenException) {
			throw new ForbiddenException();
		} catch (Exception e) {
			faileTimes = reConnect(faileTimes);
			return simpleGetInvokeWithCookie(url, cookie, params, charset, faileTimes, proxy);
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			String returnStr = EntityUtils.toString(entity,charset);
			return returnStr;
		} else {
			return "";
		}
	}

	/**
	 * 简单get调用  携带 cookie 返回 response entity
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static HttpEntity GetInvokeWithCookie(String url, String cookie, int faileTimes, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException {

		HttpClient client = buildHttpClient(true);

		HttpGet get = buildHttpGet(url, null);
		get.setHeader(COOKIE, cookie);
		get.setConfig(HttpClientUtils.buildRequestConfig(proxy));

		HttpResponse response = null;

		try {
			response = client.execute(get);
			assertStatus(response);
		} catch (ConnectTimeoutException connectTimeoutException) {
			throw new ForbiddenException();
		} catch (ForbiddenException forbiddenException) {
			throw new ForbiddenException();
		} catch (SocketTimeoutException e) {
			System.out.println(Thread.currentThread().getName() + ":socket连接超时,放弃下载");
		} catch (Exception e) {
			faileTimes = reConnect(faileTimes);
			return GetInvokeWithCookie(url, cookie, faileTimes, proxy);
		}

		HttpEntity entity = response.getEntity();

		return entity;
	}

	/**
	 * 简单get调用  携带 cookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String simpleGetInvokeWithCookie(String url,String cookie, Map<String, String> params, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException, ForbiddenException {
		return simpleGetInvokeWithCookie(url, cookie, params, "UTF-8", HttpClientUtils.DETAULT_FAIL_COUNT, proxy);
	}

	/**
	 * 简单get调用  携带 cookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ForbiddenException
	 */
	public static String simpleGetInvokeWithCookie(String url, String cookie, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException, ForbiddenException {
		return simpleGetInvokeWithCookie(url, cookie, null, "UTF-8", HttpClientUtils.DETAULT_FAIL_COUNT, proxy);
	}

	/**
	 * 获取loginCookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getLoginCookie(String url, Map<String, String> params,String charset, int faileTimes, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException, ForbiddenException {

		HttpClient client = buildHttpClient(true);

		HttpGet get = buildHttpGet(url, params);
		get.setConfig(HttpClientUtils.buildRequestConfig(proxy));

		HttpResponse response = null;
		String loginCookieValue;

		try {
			response = client.execute(get);
			assertStatus(response);

		} catch (ConnectTimeoutException connectTimeoutException) {
			throw new ForbiddenException();
		} catch (ForbiddenException forbiddenException) {
			throw new ForbiddenException();
		} catch (Exception e) {
			faileTimes = reConnect(faileTimes);
			return getLoginCookie(url, params, charset, faileTimes, proxy);
		}

		loginCookieValue = response.getLastHeader("Set-Cookie").getValue();

		return loginCookieValue;
	}

	/**
	 * 重新连接
	 * @param faileTimes
	 * @return
	 * @throws ForbiddenException
	 */
	private static int reConnect(int faileTimes) throws ForbiddenException {
		if (faileTimes >= HttpClientUtils.MAX_FAIL_COUNT) {
			System.out.println(Thread.currentThread().getName() + ":连接失败,切换代理");
			throw new ForbiddenException();
		} else {
			System.out.println(Thread.currentThread().getName() + ":连接失败, 正在重试, 第 " + faileTimes + " 次");
			try {
				Thread.sleep(HttpClientUtils.CONNECT_FAIL_WAIT_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			faileTimes += 1;
		}
		return faileTimes;
	}

/*	private static String getLoginCookieHandler(String url, Map<String, String> params, String charset, int faileTimes,
			Proxy proxy) throws ClientProtocolException, IOException, URISyntaxException {
		Proxy newProxy = getNewProxy(proxy);
		String loginCookie;

		if (newProxy != null) {
			System.out.println("新ip不为空, 用新ip发起请求");
			loginCookie = getLoginCookie(url, params, charset, faileTimes, newProxy);
		} else {
			System.out.println("新ip为空, 用旧ip发起请求");
			getLoginCookie(url, params, charset, faileTimes, proxy);
			loginCookie = getLoginCookie(url, params, charset, faileTimes, newProxy);
		}

		return loginCookie;
	}

	private static Proxy getNewProxy(Proxy proxy) {
		System.out.println("该IP被封了， 正在切换ip。。。");
		Proxy newProxy = proxyQueue.poll();
		System.out.println("获取新ip:" + newProxy);

		proxyQueue.add(proxy);
		System.out.println("将旧ip放入队列");
		return newProxy;
	}
*/
	/**
	 * 获取loginCookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String getLoginCookie(String url, Proxy proxy)
			throws ClientProtocolException, IOException, URISyntaxException, ForbiddenException {
		return getLoginCookie(url, null, "UTF-8", HttpClientUtils.DETAULT_FAIL_COUNT, proxy);
	}

	/**
	 * 简单post调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 *//*
	public static String simplePostInvoke(String url, Map<String, String> params)
			throws URISyntaxException, ClientProtocolException, IOException {
		return simplePostInvoke(url, params,CONTENT_CHARSET, HttpClientUtils.DETAULT_FAIL_COUNT);
	}
	*//**
	 * 简单post调用
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 *//*
	public static String simplePostInvoke(String url, Map<String, String> params,String charset, int faileTimes)
			throws URISyntaxException, ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);

		HttpPost postMethod = buildHttpPost(url, params);

		HttpResponse response = client.execute(postMethod);

		try {
			assertStatus(response);
		} catch (ForbiddenException forbiddenException) {
			System.out.println("该IP被封了， 正在切换ip。。。");
		} catch (Exception e) {
			if (faileTimes >= HttpClientUtils.MAX_FAIL_COUNT) {
				throw e;
			} else {
				try {
					Thread.sleep(HttpClientUtils.CONNECT_FAIL_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				faileTimes += 1;
				simplePostInvoke(url, params, charset, faileTimes);
			}
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			String returnStr = EntityUtils.toString(entity, charset);
			return returnStr;
		}

		return null;
	}

	*//**
	 * 简单post调用 使用cookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 *//*
	public static String simplePostInvokeWithCookie(String url,String cookie ,Map<String, String> params,String charset, int faileTimes)
			throws URISyntaxException, ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);

		HttpPost postMethod = buildHttpPost(url, params);
		postMethod.setHeader(COOKIE, cookie);

		HttpResponse response = client.execute(postMethod);

		try {
			assertStatus(response);
		} catch (ForbiddenException forbiddenException) {
			System.out.println("该IP被封了， 正在切换ip。。。");
		} catch (Exception e) {
			if (faileTimes >= HttpClientUtils.MAX_FAIL_COUNT) {
				throw e;
			} else {
				try {
					Thread.sleep(HttpClientUtils.CONNECT_FAIL_WAIT_TIME);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				faileTimes += 1;
				simplePostInvokeWithCookie(url, cookie, params, charset, faileTimes);
			}
		}

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			String returnStr = EntityUtils.toString(entity, charset);
			return returnStr;
		}

		return null;
	}

	*//**
	 * 简单post调用 使用cookie
	 *
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 *//*
	public static String simplePostInvokeWithCookie(String url,String cookie, Map<String, String> params)
			throws URISyntaxException, ClientProtocolException, IOException {

		return simplePostInvokeWithCookie(url, cookie, params, "utf-8", HttpClientUtils.DETAULT_FAIL_COUNT);
	}*/

	/**
	 * 创建HttpClient
	 *
	 * @param isMultiThread
	 * @return
	 */
	public static HttpClient buildHttpClient(boolean isMultiThread) {

		CloseableHttpClient client;

		if (isMultiThread)
			client = HttpClientBuilder
					.create()
					.setConnectionManager(
							new PoolingHttpClientConnectionManager()).build();
		else
			client = HttpClientBuilder.create().build();
		// 设置代理服务器地址和端口
		// client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
		return client;
	}
/*
	*//**
	 * 构建httpPost对象
	 *
	 * @param url
	 * @param headers
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 *//*
	public static HttpPost buildHttpPost(String url, Map<String, String> params)
			throws UnsupportedEncodingException, URISyntaxException {
		Assert.notNull(url, "构建HttpPost时,url不能为null");
		HttpPost post = new HttpPost(url);
		setCommonHttpMethod(post);
		HttpEntity he = null;
		if (params != null) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				formparams.add(new BasicNameValuePair(key, params.get(key)));
			}
			he = new UrlEncodedFormEntity(formparams, GBK);
			post.setEntity(he);
		}
		// 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
		// setContentLength(post, he);
		return post;

	}*/

	/**
	 * 构建httpGet对象
	 *
	 * @param url
	 * @param headers
	 * @return
	 * @throws URISyntaxException
	 */
	public static HttpGet buildHttpGet(String url, Map<String, String> params)
			throws URISyntaxException {
		Assert.notNull(url, "构建HttpGet时,url不能为null");
		HttpGet get = new HttpGet(buildGetUrl(url, params));
		return get;
	}

	/**
	 * build getUrl str
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	private static String buildGetUrl(String url, Map<String, String> params) {
		StringBuffer uriStr = new StringBuffer(url);
		if (params != null) {
			List<BasicNameValuePair> ps = new ArrayList<BasicNameValuePair> ();
			for (String key : params.keySet()) {
				ps.add(new BasicNameValuePair(key, params.get(key)));
			}
			uriStr.append("?");
			uriStr.append(URLEncodedUtils.format(ps, UTF_8));
		}
		return uriStr.toString().replace("=&", "&");
	}

	/**
	 * 设置HttpMethod通用配置
	 *
	 * @param httpMethod
	 */
	public static void setCommonHttpMethod(HttpRequestBase httpMethod) {
		httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);// setting
																		// contextCoding
//		httpMethod.setHeader(HTTP.CHARSET_PARAM, CONTENT_CHARSET);
		// httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON_CHARSET);
		// httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_XML_CHARSET);
	}

	/**
	 * 设置成消息体的长度 setting MessageBody length
	 *
	 * @param httpMethod
	 * @param he
	 */
	public static void setContentLength(HttpRequestBase httpMethod,
			HttpEntity he) {
		if (he == null) {
			return;
		}
		httpMethod.setHeader(HTTP.CONTENT_LEN, String.valueOf(he.getContentLength()));
	}

	/**
	 * 根据代理 构建公用RequestConfig
	 *
	 * @return
	 */
	public static RequestConfig buildRequestConfig(Proxy proxy) {
		String address = proxy.address().toString();
		String[] addressArr = address.replace("/", "").split(":");
		String ip = addressArr[0].trim();
		String host = addressArr[1].trim();

		if (proxy != null && !StringUtil.isBlank(ip) && !StringUtil.isBlank(host)) {
			// 设置请求和传输超时时间
			HttpHost httpHost = new HttpHost(ip, Integer.parseInt(host));
			RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost)
					.setSocketTimeout(SO_TIMEOUT_MS)
					.setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
			return requestConfig;
		} else {
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(SO_TIMEOUT_MS)
					.setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
			return requestConfig;
		}
	}

	/**
	 * 强验证必须是200状态否则报异常
	 * @param res
	 * @throws ForbiddenException
	 * @throws HttpException
	 */
 static	void assertStatus(HttpResponse res) throws IOException, ForbiddenException{
		Assert.notNull(res, "http响应对象为null");
		Assert.notNull(res.getStatusLine(), "http响应对象的状态为null");
		System.out.println(Thread.currentThread().getName() + "响应的状态码是:" + res.getStatusLine().getStatusCode());
		switch (res.getStatusLine().getStatusCode()) {
		case HttpStatus.SC_OK:
			break;
		case HttpStatus.SC_FORBIDDEN:
			throw new ForbiddenException();
//		case HttpStatus.SC_ACCEPTED:
//		case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
//		case HttpStatus.SC_NO_CONTENT:
//		case HttpStatus.SC_RESET_CONTENT:
//		case HttpStatus.SC_PARTIAL_CONTENT:
//		case HttpStatus.SC_MULTI_STATUS:
		default:
			throw new IOException("服务器响应状态异常，失败。" + " 异常状态码：" + res.getStatusLine().getStatusCode());
		}
	}
	private HttpClientUtils() {
	}
}
