package org.spider.util;

import org.spider.zwzl.Constants;

public final class MongoUtil {

	private static MongoDB instance;

	static {
		try {
			instance = new MongoDB(Constants.LOCAL_IP, Constants.DATABASE, Constants.TABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MongoDB getMogoDbInstance(){
		return instance;
	}
}
