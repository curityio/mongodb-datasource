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
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;
import se.curity.identityserver.sdk.errors.ExternalServiceException;
import se.curity.identityserver.sdk.http.HttpRequest;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.http.HttpStatus;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.WebServiceClient;

import java.util.Collections;
import java.util.Map;

import static com.couchbase.curity.data.access.Constants.USER_BUCKET_PATH;
import static se.curity.identityserver.sdk.http.HttpRequest.createFormUrlEncodedBodyProcessor;

public class CouchbaseUserAccountDataAccessProvider implements UserAccountDataAccessProvider
{
    private final CouchbaseDataAccessProviderConfiguration _configuration;
    private final Json _json;
    private final WebServiceClient _webServiceClient;

    public CouchbaseUserAccountDataAccessProvider(CouchbaseDataAccessProviderConfiguration configuration)
    {
        this._configuration = configuration;
        _json = configuration.json();
        _webServiceClient = configuration.webServiceClient();
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
        HttpRequest request = _webServiceClient
                .withPath(USER_BUCKET_PATH + "/docs/" + accountAttributes.getUserName())
                .request()
                .contentType("application/x-www-form-urlencoded")
                .body(createFormUrlEncodedBodyProcessor(
                        Collections.singletonMap("value", _json.toJson(accountAttributes))))
                .method("POST");

        HttpResponse couchbaseResponse = request.response();
        if (couchbaseResponse.statusCode() != HttpStatus.OK.getCode())
        {
            throw new ExternalServiceException(couchbaseResponse.body(HttpResponse.asString()));
        }

        request = _webServiceClient
                .withPath(USER_BUCKET_PATH + "/docs/" + accountAttributes.getUserName())
                .request()
                .method("GET");
        couchbaseResponse = request.response();
        if (couchbaseResponse.statusCode() != HttpStatus.OK.getCode())
        {
            throw new ExternalServiceException(couchbaseResponse.body(HttpResponse.asString()));
        }

        Map<String, Object> dataMap = _json.fromJson(couchbaseResponse.body(HttpResponse.asString()));
        AccountAttributes newAccountAttributes = AccountAttributes.fromMap((Map) dataMap.get("json"));

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