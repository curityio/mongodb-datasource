/*
 *  Copyright 2015 Curity AB
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


import com.curity.mongodb.datasource.config.MongoDataAccessProviderConfiguration;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;
import se.curity.identityserver.sdk.service.Json;

import java.util.Map;

import static com.curity.mongodb.datasource.Constants.USERS_COLLECTION;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoUserAccountDataAccessProvider implements UserAccountDataAccessProvider
{
    private final MongoDataAccessProviderConfiguration _configuration;
    private final Json _json;
    private final MongoClient _mongoClient;
    private final MongoDatabase _database;

    public MongoUserAccountDataAccessProvider(MongoDataAccessProviderConfiguration configuration)
    {
        this._configuration = configuration;
        _json = configuration.json();

        ServerAddress serverAddress = new ServerAddress(configuration.getHost(), configuration.getPort());
        MongoCredential mongoCredential = MongoCredential.createCredential(configuration.getUsername(), configuration.getDatabase(), configuration.getPassword().toCharArray());

        CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
        this._mongoClient = new MongoClient(serverAddress, mongoCredential, options);
        this._database = this._mongoClient.getDatabase(configuration.getDatabase());
    }

    @Override
    public ResourceAttributes<?> getByUserName(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public ResourceAttributes<?> getByEmail(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public ResourceAttributes<?> getByPhone(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public AccountAttributes create(AccountAttributes accountAttributes)
    {
        Document document = new Document(accountAttributes.toMap());
        this._database.getCollection(USERS_COLLECTION).insertOne(document);
        accountAttributes = accountAttributes.removeAttribute("password");
        return accountAttributes;
    }

    @Override
    public ResourceAttributes<?> update(AccountAttributes accountAttributes, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public ResourceAttributes<?> update(String s, Map<String, Object> map, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public ResourceAttributes<?> patch(String s, AttributeUpdate attributeUpdate, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return null;
    }

    @Override
    public void link(String s, String s1, String s2)
    {

    }

    @Override
    public void delete(String s)
    {

    }

    @Override
    public ResourceQueryResult getAll(long l, long l1)
    {
        return null;
    }
}