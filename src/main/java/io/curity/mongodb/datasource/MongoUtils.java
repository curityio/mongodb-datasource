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

package io.curity.mongodb.datasource;

import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

public class MongoUtils
{
    private final MongoDatabase _database;

    public MongoUtils(MongoDatabase database)
    {
        _database = database;
    }

    public AccountAttributes getAccountAttributes(Map<String, Object> dataMap)
    {
        if (dataMap != null)
        {
            return AccountAttributes.fromMap(stripMongoFields(dataMap));
        }
        return null;
    }

    @Nullable
    public static Map<String, Object> stripMongoFields(@Nullable Map<String, Object> dataMap)
    {
        if (dataMap != null)
        {
            HashMap<String,Object> newMap = new HashMap<>(dataMap);
            newMap.put("id", dataMap.get("_id").toString());
            newMap.remove("_id");
            return newMap;
        }
        return null;
    }

    private Bson buildFilter(String key, String value, boolean hasPrimaryAttribute)
    {
        return hasPrimaryAttribute ? and(eq(key + ".value", value), eq(key + ".primary", true))
                : eq(key, value);
    }

    public AccountAttributes getAccountAttributes(String key, String value, boolean hasPrimaryAttribute,
                                                  ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        Map<String, Object> dataMap = attributesEnumeration == null ?
                _database.getCollection(AccountAttributes.RESOURCE_TYPE)
                        .find(buildFilter(key, value, hasPrimaryAttribute)).first() :
                _database.getCollection(AccountAttributes.RESOURCE_TYPE)
                        .find(buildFilter(key, value, hasPrimaryAttribute))
                        .projection(include(new ArrayList<>(attributesEnumeration.getAttributes()))).first();

        return getAccountAttributes(dataMap);
    }

    public AccountAttributes getAccountAttributes(String accountId,
                                                  ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        Map<String, Object> dataMap = attributesEnumeration == null ?
                _database.getCollection(AccountAttributes.RESOURCE_TYPE)
                        .find(eq("_id", new ObjectId(accountId))).first() :
                _database.getCollection(AccountAttributes.RESOURCE_TYPE)
                        .find(eq("_id", new ObjectId(accountId)))
                        .projection(include(new ArrayList<>(attributesEnumeration.getAttributes()))).first();

        return getAccountAttributes(dataMap);
    }
}
