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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;
import se.curity.identityserver.sdk.errors.ExternalServiceException;
import se.curity.identityserver.sdk.http.HttpRequest;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.http.HttpStatus;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.WebServiceClient;

import java.util.Map;
import java.util.Optional;

import static com.couchbase.curity.data.access.Constants.USER_BUCKET_PATH;
import static java.util.Collections.singletonMap;

public class CouchbaseCredentialDataAccessProvider implements CredentialDataAccessProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(CouchbaseCredentialDataAccessProvider.class);

    private final Json _json;
    private final WebServiceClient _webServiceClient;

    @SuppressWarnings("unused") // used through DI
    public CouchbaseCredentialDataAccessProvider(CouchbaseDataAccessProviderConfiguration configuration)
    {
        _json = configuration.json();
        _webServiceClient = configuration.webServiceClient();
    }

    @Override
    public void updatePassword(AccountAttributes account)
    {
        String subjectId = account.getUserName();
        Optional<String> newPassword = Optional.ofNullable(account.getPassword());

        if (!newPassword.isPresent())
        {
            _logger.warn("Cannot update account password, missing password value");
            return;
        }
    }

    @Override
    @Nullable
    public AuthenticationAttributes verifyPassword(String userName, String password)
    {
        HttpRequest request = _webServiceClient
                .withPath(USER_BUCKET_PATH + "/docs/" + userName)
                .request()
                .method("GET");
        HttpResponse couchbaseResponse = request.response();
        if (couchbaseResponse.statusCode() != HttpStatus.OK.getCode())
        {
            throw new ExternalServiceException(couchbaseResponse.body(HttpResponse.asString()));
        }

        Map<String, Object> dataMap = _json.fromJson(couchbaseResponse.body(HttpResponse.asString()));
        AccountAttributes accountAttributes = AccountAttributes.fromMap((Map) dataMap.get("json"));
        AccountAttributes currentAccountAttributes = AccountAttributes.fromMap(singletonMap("password", password));
        if (accountAttributes.getPassword() != currentAccountAttributes.getPassword())
        {
            throw new ExternalServiceException("Bad credentials");
        }

        return AuthenticationAttributes.of(userName, ContextAttributes.empty());
    }

    @Override
    public boolean customQueryVerifiesPassword()
    {
        return false;
    }

}