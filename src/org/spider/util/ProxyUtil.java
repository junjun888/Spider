package org.spider.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.spider.Constants;

/**
 * 读取ip配置文档的工具类
 *
 * @author admin
 *
 */
public final class ProxyUtil {
	private static List<Proxy> proxys = new ArrayList<Proxy>();

	private static ArrayBlockingQueue<Proxy> proxyQueue;

	static {
		readIPFile();
		proxyQueue = new ArrayBlockingQueue<>(proxys.size());

		for (Proxy proxy : proxys) {
			proxyQueue.add(proxy);
		}
		System.out.println("已将代理放入代理队列, 代理总数:" + proxys.size());
	}

	private static void readIPFile() {
		try {
			System.out.println("开始读取配置文档, 并且将代理ip放入代理队列");
			BufferedReader pp = new BufferedReader(new FileReader(Constants.IP_FILE_BASE_PATH));
			String line = null;

			while ((line = pp.readLine()) != null) {
				String[] split = line.trim().split(":");
				InetSocketAddress ip = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
				Proxy proxyp = new Proxy(Proxy.Type.HTTP, ip);
				proxys.add(proxyp);
			}

			pp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取代理, 如果代理队列为空 则返回 null
	 *
	 * @return
	 */
	public static Proxy getProxy() {
		Proxy proxy = proxyQueue.poll();

		if (proxy == null) {
			synchronized (proxy) {
				proxy = proxyQueue.poll();
				// 这里必须有双重判断
				if (proxy == null) {
					readIPFile();
				}
			}
		}

		return proxy;
	}
}