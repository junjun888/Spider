package org.spider.demo;


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
			// coll.createIndex(eq("zhuanlihao", 1), new IndexOptions().unique(true));
			
			Document obj = new Document();
			Map<String, String> map = new HashMap<String, String>();
			map.put("专利编号", "CN121234124");
			map.put("内容", "content1");
			
			obj.put("1", map);

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
