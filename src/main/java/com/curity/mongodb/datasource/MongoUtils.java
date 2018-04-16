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
