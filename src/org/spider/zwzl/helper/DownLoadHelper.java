package org.spider.zwzl.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.spider.util.HttpClientUtils;
import org.spider.zwzl.Constants;

public class DownLoadHelper {

	/**
	 * AN 基础 url
	 */
	private final static String AN_BASE_URL = "http://www.shanghaiip.cn/Search/incfun/pdftype.jsp?sANLaw=";

	/**
	 * PN 基础 url
	 */
	private final static String PN_BASE_URL = "http://www.shanghaiip.cn/Search/incfun/pdftype.jsp?sPNLaw=";

	/**
	 * download 基础 url
	 */
	private final static String DOWNLOAD_BASE_URL = "http://www.shanghaiip.cn/Search/";

	/**
	 * 获取下载链接
	 *
	 * @param cookie
	 * @return
	 */
	public static String doAnGetDownloadHref(String cookie, String anValue) {
		try {
			String result = HttpClientUtils.simpleGetInvokeWithCookie(AN_BASE_URL + anValue, cookie);

			return getDownloadLink(result);
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
	 * 获取下载链接
	 *
	 * @param cookie
	 * @return
	 */
	public static String doPnGetDownloadHref(String cookie, String pnValue) {
		try {
			String result = HttpClientUtils.simpleGetInvokeWithCookie(PN_BASE_URL + pnValue, cookie);

			return getDownloadLink(result);
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
	 * 获取下载链接
	 *
	 * @param cookie
	 * @return
	 */
	public static void doDownLoad(String cookie, String downloadLink, String fileName, String fileDir) {
		System.out.println("开始下载：" + fileName);
		long start = System.currentTimeMillis();
		try {
			HttpEntity entity = HttpClientUtils.GetInvokeWithCookie(downloadLink, cookie, HttpClientUtils.DETAULT_FAIL_COUNT, HttpClientUtils.defaultProxy);
			if (entity != null) {
				InputStream in = entity.getContent();
				int fileSize = in.available();

				if (fileSize != 0 && fileSize <= 177) {
					// 文件不存在
					System.out.println("该pdf不存在：" + fileName);
					return;
				}

				OutputStream out = null;
				String descFilePath = Constants.File_ROOT_URL + fileDir;
				File descDir = new File(descFilePath);

				if (!descDir.exists()) {
					descDir.mkdir();
				}

				System.out.println(descFilePath + "\\" + fileName);
				File file = new File(descFilePath + "\\" + fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				out = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int readLength = 0;
				while ((readLength = in.read(buffer)) > 0) {
					byte[] bytes = new byte[readLength];
					System.arraycopy(buffer, 0, bytes, 0, readLength);
					out.write(bytes);
				}

				out.flush();
				out.close();
				in.close();
			} else {
				return;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			System.out.println("结束下载：" + fileName + "用时：" + (System.currentTimeMillis() - start) + "毫秒");
		}
	}

	/**
	 * 对返回的结果进行处理 返回下载链接
	 */
	private static String getDownloadLink(String html) {
		Document downloadHtml = Jsoup.parse(html);
		Elements allHref = downloadHtml.select("a[href]");
		String href = allHref.get(0).attr("href").substring(3);

		return DOWNLOAD_BASE_URL + href;
	}

}
