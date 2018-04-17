/*
 *  Copyright 2018 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
