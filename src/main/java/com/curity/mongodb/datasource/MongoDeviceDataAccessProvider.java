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
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.extensions.DeviceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.datasource.DeviceDataAccessProvider;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;
import static se.curity.identityserver.sdk.attribute.scim.v2.extensions.DeviceAttributes.RESOURCE_TYPE;

public class MongoDeviceDataAccessProvider implements DeviceDataAccessProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(MongoDeviceDataAccessProvider.class);

    private final MongoDatabase _database;

    private final MongoUtils _mongoUtils;

    @SuppressWarnings("unused") // used through DI
    public MongoDeviceDataAccessProvider(ConnectionPool connectionPool)
    {
        _database = connectionPool.getDatabase();
        _mongoUtils = new MongoUtils(_database);
    }

    @Override
    public DeviceAttributes getBy(String deviceId, String accountId)
    {
        Map<String, Object> dataMap = _database.getCollection(RESOURCE_TYPE)
                .find(and(eq("accountId", accountId), eq("deviceId", deviceId))).first();
        return getDeviceAttributes(dataMap);
    }

    @Override
    public ResourceAttributes<?> getBy(String deviceId, String accountId,
                                       ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        Map<String, Object> dataMap = _database.getCollection(RESOURCE_TYPE)
                .find(and(eq("accountId", accountId), eq("deviceId", deviceId)))
                .projection(include(new ArrayList<>(attributesEnumeration.getAttributes()))).first();
        return getDeviceAttributes(dataMap);
    }

    @Override
    public DeviceAttributes getById(String deviceId)
    {
        Map<String, Object> dataMap = _database.getCollection(RESOURCE_TYPE)
                .find(eq("deviceId", deviceId)).first();
        return getDeviceAttributes(dataMap);
    }

    @Override
    public ResourceAttributes<?> getById(String deviceId, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        Map<String, Object> dataMap = _database.getCollection(RESOURCE_TYPE)
                .find(eq("deviceId", deviceId))
                .projection(include(new ArrayList<>(attributesEnumeration.getAttributes()))).first();
        return getDeviceAttributes(dataMap);
    }

    @Override
    public List<DeviceAttributes> getByAccountId(String accountId)
    {
        return _database.getCollection(RESOURCE_TYPE)
                .find(eq("accountId", accountId)).into(new ArrayList<>())
                .stream().map(item -> getDeviceAttributes(item)).collect(Collectors.toList());
    }

    @Override
    public List<? extends ResourceAttributes<?>> getByAccountId(String accountId, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _database.getCollection(RESOURCE_TYPE)
                .find(eq("accountId", accountId))
                .projection(include(new ArrayList<>(attributesEnumeration.getAttributes())))
                .into(new ArrayList<>())
                .stream().map(item -> getDeviceAttributes(item)).collect(Collectors.toList());
    }

    @Override
    public void create(DeviceAttributes deviceAttributes)
    {
        Document document = new Document(deviceAttributes.toMap());
        _database.getCollection(RESOURCE_TYPE).insertOne(document);
    }

    @Override
    public void update(DeviceAttributes deviceAttributes)
    {
        _database.getCollection(RESOURCE_TYPE).updateOne(eq("deviceId", deviceAttributes.getDeviceId()),
                new Document("$set", new Document(deviceAttributes.toMap())));
    }

    @Override
    public void delete(String id)
    {
        _database.getCollection(RESOURCE_TYPE).deleteOne(eq("_id", new ObjectId(id)));
    }

    @Override
    public void delete(String deviceId, String accountId)
    {
        _database.getCollection(RESOURCE_TYPE).deleteOne(
                and(
                        eq("deviceId", deviceId),
                        eq("accountId", accountId)
                ));
    }

    @Override
    public ResourceQueryResult getAll(long startIndex, long count)
    {
        List<DeviceAttributes> deviceAttributes = _database.getCollection(RESOURCE_TYPE).find()
                .skip((int) startIndex)
                .limit((int) count)
                .into(new ArrayList<>()).stream()
                .map(item -> getDeviceAttributes(item)).collect(Collectors.toList());
        return new ResourceQueryResult(deviceAttributes, deviceAttributes.size(), startIndex, count);
    }

    public DeviceAttributes getDeviceAttributes(Map<String, Object> dataMap)
    {
        if (dataMap != null)
        {
            dataMap.put("id", dataMap.get("_id").toString());
            dataMap.remove("_id");
            return DeviceAttributes.of(Attributes.fromMap(dataMap));
        }
        return null;
    }
}