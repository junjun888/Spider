package org.spider.zwzl.exception;

/**
 * 爬虫挂掉了抛出的异常
 * <p>
 * Title: SpiderClashException
 * </p>
 * <p>
 * Description:
 * </p>
 *
 * @author 黄文俊
 * @date 2017年8月27日下午8:52:39
 * @version 1.0
 */
public class SpiderCrashException extends RuntimeException {

	private static final long serialVersionUID = -1904067580400543253L;
	// 20170504-20170507
	private String dateInterval;

	public SpiderCrashException(String dateInterval) {
		super();
		this.dateInterval = dateInterval;
	}

	/**
	 * 返回结束日期
	 * <p>Title: getQuery</p>
	 * <p>Description: </p>
	 * @return
	 */
	public String getEndDate() {
		String[] dates = dateInterval.split("-");
		return dates[1];
	}

	public void setEndDate(String dateInterval) {
		this.dateInterval = dateInterval;
	}
}
