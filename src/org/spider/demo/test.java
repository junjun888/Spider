package org.spider.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.spider.util.DateUtil;

public class test {

	public static void main(String[] args) {
		// 动态生产日期参数 20170801-20170807
		Date descDateFormat = DateUtil.getDescDateFormat("20170809");
		System.out.println(descDateFormat);
		// 开始日期
//		Date startDate = DateUtil.getNowDate();
//		Date endDate = DateUtil.getDateFormat("2017-10-30");
//		// 间隔
//		int interval = 7;
//
//		List<String> dateIntervalArray = autoGenerateDateArray(endDate, interval, startDate, new ArrayList<String>());
//
//		for (String string : dateIntervalArray) {
//			System.out.println(string);
//		}
	}

	/**
	 * 根据开始时间和结束时间, 以一定的间隔 倒序的生成时间
	 *
	 * 20171023-20171030..
	 * 20171015-20171022..
	 *
	 * @param endDate Date endDate = DateUtil.getDateFormat("2017-10-30");
	 * @param interval
	 * @param startDate 同上
	 */
	private static List<String> autoGenerateDateArray(Date endDate, int interval, Date startDate, List<String> list) {
		Date beforeDate = DateUtil.getDescDayBefore(endDate, interval);

		if (DateUtil.fristDateIsMoreBig(beforeDate, startDate)) {
			list.add(DateUtil.getDescFormat(beforeDate) + "-" + DateUtil.getDescFormat(endDate));
			endDate = DateUtil.getDescDayBefore(beforeDate, 1);
			autoGenerateDateArray(endDate, interval, startDate, list);
		} else {
			list.add(DateUtil.getDescFormat(startDate) + "-" + DateUtil.getDescFormat(endDate));
			return list;
		}

		return list;
	}

	// 输入开始日期 20170807 和结束日期 20170807 和时间间隔 如何更加结束日期往前爬
}
