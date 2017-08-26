package org.spider.zwzl;

public class Constants {

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
	public final static String FILE_DOWNLOAD_END_DATE = "2017-05-07";

	/**
	 * 文件下载 的时间间隔
	 */
	public final static int FILE_DOWNLOAD_DATE_INTERVAL = 7;

	/**
	 * 元数据爬取的开始日期
	 */
	public final static String META_DATA_START_DATE = "2017-01-01";

	/**
	 * 元数据爬取的结束日期
	 */
	public final static String META_DATA_END_DATE = "2017-05-07";

	/**
	 * 元数据爬取 的时间间隔
	 */
	public final static int META_DATA_DATE_INTERVAL = 7;


	// common
	/**
	 * 最大 请求失败次数
	 */
	public final static int MAX_FAIL_COUNT = 3;

	/**
	 * 默认 请求失败次数
	 */
	public final static int DEFAULT_FAIL_COUNT = 0;

	/**
	 * 本地IP
	 */
	public final static String LOCAL_IP = "192.168.2.30";

	/**
	 * mogodb 数据库
	 */
	public final static String DATABASE = "zhuanli";

	/**
	 * mogodb 表
	 */
	public final static String TABLE = "metadata";

}
