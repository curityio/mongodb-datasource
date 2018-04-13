package com.curity.mongodb.datasource;

import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import se.curity.identityserver.sdk.attribute.AccountAttributes;

import java.util.Map;

import static com.curity.mongodb.datasource.Constants.USERS_COLLECTION;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MongoUtils
{
    private final MongoDatabase _database;

    public MongoUtils(MongoDatabase database)
    {
        _database = database;
    }

    public AccountAttributes getAccountAttributes(String key, String value, boolean isPrimary)
    {
        Map<String, Object> dataMap;
        Bson filters;

        filters = isPrimary ? and(eq(key + ".value", value), eq(key + ".primary", true))
                : eq(key, value);

        dataMap = _database.getCollection(USERS_COLLECTION)
                .find(filters).first();

        if (dataMap != null)
        {
            dataMap.put("id", dataMap.get("_id").toString());
            dataMap.remove("_id");
            return AccountAttributes.fromMap(dataMap);
        }
        return null;
    }
}
