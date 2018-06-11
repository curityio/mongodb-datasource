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
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.extensions.LinkedAccount;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static se.curity.identityserver.sdk.attribute.AccountAttributes.RESOURCE_TYPE;

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
    public ResourceAttributes<?> getByUserName(String userName, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("userName", userName, false, attributesEnumeration);
    }

    @Override
    public ResourceAttributes<?> getByEmail(String email, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("emails", email, true, attributesEnumeration);
    }

    @Override
    public ResourceAttributes<?> getByPhone(String phone, ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        return _mongoUtils.getAccountAttributes("phoneNumbers", phone, true, attributesEnumeration);
    }

    @Override
    public AccountAttributes create(AccountAttributes accountAttributes)
    {
        Document document = new Document(accountAttributes.toMap());
        _database.getCollection(RESOURCE_TYPE).insertOne(document);

        return _mongoUtils.getAccountAttributes("userName", accountAttributes.getUserName(),
                false, null)
                .removeAttribute("password");
    }

    @Override
    public ResourceAttributes<?> update(AccountAttributes accountAttributes,
                                        ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        _database.getCollection(RESOURCE_TYPE).updateOne(eq("userName", accountAttributes.getUserName()),
                new Document("$set", new Document(accountAttributes.toMap())));
        return _mongoUtils.getAccountAttributes("userName",
                accountAttributes.getUserName(), false, attributesEnumeration);

    }

    @Override
    public ResourceAttributes<?> update(String accountId, Map<String, Object> map,
                                        ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        _database.getCollection(RESOURCE_TYPE).updateOne(eq("_id", new ObjectId(accountId)),
                new Document("$set", new Document(map)));

        return _mongoUtils.getAccountAttributes(accountId, attributesEnumeration);

    }

    @Override
    public ResourceAttributes<?> patch(String accountId, AttributeUpdate attributeUpdate,
                                       ResourceQuery.AttributesEnumeration attributesEnumeration)
    {
        Map<String, Object> dataMap = attributeUpdate.getAttributeReplacements().toMap();
        dataMap.putAll(attributeUpdate.getAttributeAdditions().toMap());
        _database.getCollection(RESOURCE_TYPE).updateOne(eq("_id", new ObjectId(accountId)),
                new Document("$set", new Document(dataMap)));

        return _mongoUtils.getAccountAttributes(accountId, attributesEnumeration);
    }

    // TODO implement linked accounts

    @Override
    public void link(String linkingAccountManager, String localAccountId, String foreignDomainName, String foreignUserName)
    {
        throw new NotImplementedException();
    }

    @Override
    public Collection<LinkedAccount> listLinks(String linkingAccountManager, String localAccountId)
    {
        throw new NotImplementedException();
    }

    @Override
    public AccountAttributes resolveLink(String linkingAccountManager, String foreignDomainName, String foreignAccountId)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean deleteLink(String linkingAccountManager, String localAccountId, String foreignDomainName, String foreignAccountId)
    {
        throw new NotImplementedException();
    }

    @Override
    public void delete(String accountId)
    {
        _database.getCollection(RESOURCE_TYPE).deleteOne(eq("_id", new ObjectId(accountId)));
    }

    @Override
    public ResourceQueryResult getAll(long startIndex, long count)
    {
        List<AccountAttributes> accountAttributes = _database.getCollection(RESOURCE_TYPE).find()
                .skip((int) startIndex)
                .limit((int) count)
                .into(new ArrayList<>()).stream()
                .map(item -> _mongoUtils.getAccountAttributes(item)).collect(Collectors.toList());
        return new ResourceQueryResult(accountAttributes, accountAttributes.size(), startIndex, count);
    }
}