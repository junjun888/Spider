package org.spider.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.spider.Constants;

/**
 * 读取ip配置文档的工具类
 * @author admin
 *
 */
public final class ReadIpConfigUtil {
	private static List<Proxy> proxy = new ArrayList<Proxy>();

	static {
		try {
			BufferedReader pp = new BufferedReader(new FileReader(Constants.IP_FILE_BASE_PATH));
			String line = null;

			while ((line = pp.readLine()) != null) {
				String[] split = line.trim().split(":");
				InetSocketAddress ip = new InetSocketAddress(split[0],
				Integer.parseInt(split[1]));
				Proxy proxyp = new Proxy(Proxy.Type.HTTP, ip);
				proxy.add(proxyp);
			}

				pp.close();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}

	public static List<Proxy> getProxyList() {
		return proxy;
	}
}