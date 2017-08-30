package org.spider.zwzl;

/**
 * 专利文档爬虫的常量类
 * @author admin
 *
 */
public class Constants {

	// FILE
	/**
	 * 下载文件的后缀
	 */
	public static final String DOWNLOAD_FILE_SUFFIX = ".pdf";

	/**
	 * 下载文件的根目录（需要手动创建）
	 */
	public final static String File_ROOT_URL = "F:\\专利PDF\\";

	/**
	 * 文件下载的开始日期
	 */
	public final static String FILE_DOWNLOAD_START_DATE = "2017-01-01";

	/**
	 * 文件下载的结束日期
	 */
	public final static String FILE_DOWNLOAD_END_DATE = "2017-08-22";

	/**
	 * 文件下载 的时间间隔（天）
	 */
	public final static int FILE_DOWNLOAD_DATE_INTERVAL = 7;

	/**
	 * 文件下载 最大线程数
	 */
	public final static int FILE_DOWNLOAD_MAX_THREAD = 2;

	// METADATA

	/**
	 * 元数据爬取的开始日期
	 */
	public final static String META_DATA_START_DATE = "2016-08-22";

	/**
	 * 元数据爬取的结束日期
	 */
	public final static String META_DATA_END_DATE = "2017-08-22";

	/**
	 * 元数据爬取 的时间间隔（天）
	 */
	public final static int META_DATA_DATE_INTERVAL = 3;

	/**
	 * 元数据爬取 爬一个详情页后的睡眠时间
	 */
	public final static int DETAIL_PAGE_THREAD_WAIT_TIME = 5000;

	/**
	 * 元数据爬取 最大线程数
	 */
	public final static int METADATE_MAX_THREAD = 100;


	// common
	/**
	 * 爬虫挂了以后 重启等待时间
	 */
	public final static int REBOOT_WAIT_TIME = 10000;

	/**
	 * 默认 enddate
	 */
	public final static String DEFAULT_END_DATE = "";

	/**
	 * 最大 重启次数
	 */
	public final static int MAX_FAIL_COUNT = 2;

	/**
	 * 默认 请求失败次数
	 */
	public final static int DEFAULT_FAIL_COUNT = 0;

	/**
	 * 本地IP
	 */
	public final static String LOCAL_IP = "127.0.0.1";

	/**
	 * mogodb 数据库
	 */
	public final static String DATABASE = "zhuanli";

	/**
	 * mogodb 表
	 */
	public final static String TABLE = "metedata";

}
