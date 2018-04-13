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


import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;

import java.util.Map;

import static com.curity.mongodb.datasource.Constants.USERS_COLLECTION;

public class MongoUserAccountDataAccessProvider implements UserAccountDataAccessProvider
{
    private final MongoDatabase _database;
    private final MongoUtils _mongoUtils;

    public MongoUserAccountDataAccessProvider(ConnectionPool connectionPool)
    {
        _database = connectionPool.getDatabase();
        _mongoUtils = new MongoUtils(_database);
    }

    @Override
    public ResourceAttributes<?> getByUserName(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("userName", s, false);
    }

    @Override
    public ResourceAttributes<?> getByEmail(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("emails", s, true);
    }

    @Override
    public ResourceAttributes<?> getByPhone(String s, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("phoneNumbers", s, true);
    }

    @Override
    public AccountAttributes create(AccountAttributes accountAttributes)
    {
        Document document = new Document(accountAttributes.toMap());
        _database.getCollection(USERS_COLLECTION).insertOne(document);

        AccountAttributes newAccountAttributes = _mongoUtils.getAccountAttributes("userName", accountAttributes.getUserName(), false);
        newAccountAttributes = newAccountAttributes.removeAttribute("password");
        return newAccountAttributes;
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