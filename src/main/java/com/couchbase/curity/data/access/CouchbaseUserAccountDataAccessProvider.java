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

package com.couchbase.curity.data.access;


import com.couchbase.curity.data.access.config.CouchbaseDataAccessProviderConfiguration;
import com.couchbase.curity.data.access.config.UserAccountAccessConfiguration;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;

import java.util.Map;

public class CouchbaseUserAccountDataAccessProvider implements UserAccountDataAccessProvider
{
    private final UserAccountAccessConfiguration _configuration;

    public CouchbaseUserAccountDataAccessProvider(CouchbaseDataAccessProviderConfiguration configuration)
    {
        this._configuration = configuration.getUserAccountAccessConfiguration();
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
        return null;
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