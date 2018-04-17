package com.curity.mongodb.datasource;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import se.curity.identityserver.sdk.datasource.BucketDataAccessProvider;

import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MongoBucketDataAccessProvider implements BucketDataAccessProvider
{

    private final MongoDatabase _database;
    private final static String RESOURCE_TYPE = "Bucket";

    @SuppressWarnings("unused") // used through DI
    public MongoBucketDataAccessProvider(ConnectionPool connectionPool)
    {
        _database = connectionPool.getDatabase();
    }

    @Override
    public Map<String, Object> getAttributes(String subject, String purpose)
    {
        return _database.getCollection(RESOURCE_TYPE)
                .find(and(eq("subject", subject), eq("purpose", purpose)))
                .first();
    }

    @Override
    public Map<String, Object> storeAttributes(String subject, String purpose, Map<String, Object> dataMap)
    {
        dataMap.put("subject", subject);
        dataMap.put("purpose", purpose);

        _database.getCollection(RESOURCE_TYPE)
                .updateOne(and(eq("subject", subject), eq("purpose", purpose)),
                        new Document("$set", new Document(dataMap)),
                        new UpdateOptions().upsert(true));

        return getAttributes(subject, purpose);
    }

    @Override
    public boolean clearBucket(String subject, String purpose)
    {
        DeleteResult result = _database.getCollection(RESOURCE_TYPE)
                .deleteOne(and(eq("subject", subject), eq("purpose", purpose)));
        return result.getDeletedCount() == 1L;
    }
}
