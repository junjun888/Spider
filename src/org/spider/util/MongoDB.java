package org.spider.util;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
    private MongoClient mc;
    private MongoDatabase db;
    private MongoCollection<Document> col;

    public MongoDB(String host, String dbname, String tbname) throws Exception {
        if (isNull(host)) throw new NullPointerException("host is null");
        if (isNull(dbname)) throw new NullPointerException("dbname is null");
        if (isNull(tbname)) throw new NullPointerException("tbname is null");
        this.mc = new MongoClient(host);
        //this.mc = new MongoClient(host,27018);
        this.db = this.mc.getDatabase(dbname);
        this.col = this.db.getCollection(tbname);
    }

    public boolean isNull(String str) {
        if (str == null || "".equals(str.replaceAll("\\s", ""))) return true;
        return false;
    }

    public MongoClient getMongo() {
        return this.mc;
    }

    public MongoDatabase getDB() {
        return this.db;
    }

    public MongoCollection<Document> getCollection() {
        return this.col;
    }

    public MongoDatabase getDB(String dbname) {
        if (this.mc == null) return null;
        return this.mc.getDatabase(dbname);
    }

    public MongoCollection<Document> getCollection(String name) {
        if (this.db == null) return null;
        return this.db.getCollection(name);
    }

    public void close() {
        if (this.mc != null) this.mc.close();
    }
}
