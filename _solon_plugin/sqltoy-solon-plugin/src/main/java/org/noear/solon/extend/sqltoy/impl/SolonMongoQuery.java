package org.noear.solon.extend.sqltoy.impl;

import com.mongodb.client.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.sagacity.sqltoy.SqlToyContext;
import org.sagacity.sqltoy.integration.MongoQuery;

import java.util.ArrayList;
import java.util.List;

public class SolonMongoQuery implements MongoQuery {
    private MongoDatabase db;

    @Override
    public MongoCollection<Document> getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }

    @Override
    public <T> List<T> find(String query, Class<T> entityClass, String collectionName, Long skip, Integer limit) {
        MongoCollection<Document> collection = getCollection(collectionName);
        FindIterable<T> findIterable = collection.find(BsonDocument.parse(query), entityClass);

        if (skip != null) {
            findIterable.skip(skip.intValue());
        }
        if (limit != null) {
            findIterable.limit(limit);
        }

        MongoCursor<T> cur = findIterable.iterator();

        List<T> data = new ArrayList<>();

        while (cur.hasNext()) {
            data.add(cur.next());
        }
        cur.close();

        return data;
    }

    @Override
    public long count(String query, String collectionName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        Document sum = new Document();
        sum.put("$sum", 1);

        Document count = new Document();
        count.put("_id", null);
        count.put("count", sum);

        Document group = new Document();
        group.put("$group", count);

        List<Document> list = new ArrayList<Document>();
        list.add(group);

        AggregateIterable iterable = collection.aggregate(list);
        MongoCursor<Document> cursor = iterable.iterator();
        while (cursor.hasNext()) {
            Document docu = cursor.next();
            return (Long) docu.get("count");
        }
        return 0;
    }

    @Override
    public void initialize(SqlToyContext sqlToyContext) {
        db = sqlToyContext.getAppContext().getBean(MongoDatabase.class);
    }
}