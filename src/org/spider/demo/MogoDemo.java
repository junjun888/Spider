package org.spider.demo;

import static com.mongodb.client.model.Filters.eq;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.spider.util.MongoDB;
import org.spider.util.MongoUtil;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

public class MogoDemo {

	public static void main(String[] args) {
		try {
			MongoDB mg = MongoUtil.getMogoDbInstance();
			// 获取连接
			MongoCollection<org.bson.Document> coll  = mg.getCollection();
			coll.createIndex(eq("zhuanlihao", 1), new IndexOptions().unique(true));
			Document obj = new Document();
			Map<String, String> map = new HashMap<String, String>();
			map.put("CN121234123", "content");

			try {
				coll.insertOne(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
