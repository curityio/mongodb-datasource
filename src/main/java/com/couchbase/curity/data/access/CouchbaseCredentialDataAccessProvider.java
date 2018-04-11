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
import com.couchbase.curity.data.access.config.CredentialAccessConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.AttributeName;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;
import se.curity.identityserver.sdk.http.HttpRequest;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.service.WebServiceClient;

import java.util.Map;
import java.util.Optional;

import static com.couchbase.curity.data.access.WebUtils.urlEncode;

public class CouchbaseCredentialDataAccessProvider implements CredentialDataAccessProvider
{
    private static final String SUBJECT_PLACEHOLDER = ":subject";
    private static final String PASSWORD_PLACEHOLDER = ":password";
    private static final Logger _logger = LoggerFactory.getLogger(CouchbaseCredentialDataAccessProvider.class);

    private final CredentialAccessConfiguration _configuration;
    private final Json _json;
    private final WebServiceClient _webServiceClient;

    @SuppressWarnings("unused") // used through DI
    public CouchbaseCredentialDataAccessProvider(CouchbaseDataAccessProviderConfiguration configuration)
    {
        _configuration = configuration.getCredentialAccessConfiguration();
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

        String requestPath = createRequestPath(subjectId, newPassword.get());
        Map<String, String> requestParameterMap = createRequestParameterMap(subjectId, newPassword.get());

    }

    @Override
    @Nullable
    public AuthenticationAttributes verifyPassword(String userName, String password)
    {
        String requestPath = createRequestPath(userName, password);
        Map<String, String> requestParameterMap;

        if (_configuration.backendVerifiesPassword())
        {
            requestParameterMap = createRequestParameterMap(userName, password);
        }
        else
        {
            // Don't send the password when the backend is not doing anything with it
            requestParameterMap = createRequestParameterMap(userName, null);
        }

        WebServiceClient webServiceClient = _webServiceClient.withPath(requestPath);

        HttpRequest request = getHttpRequestToVerifyPassword(requestParameterMap, webServiceClient);

        HttpResponse CouchbaseResponse = request.response();

        _logger.debug("Couchbase data-source responds with status: {}", CouchbaseResponse.statusCode());

        return getAuthenticationAttributesFrom(CouchbaseResponse, userName);
    }

    @Nullable
    AuthenticationAttributes getAuthenticationAttributesFrom(HttpResponse CouchbaseResponse, String userName)
    {
        @Nullable AuthenticationAttributes attributes = null;

        return attributes;
    }

    private HttpRequest getHttpRequestToVerifyPassword(Map<String, String> requestParameterMap,
                                                       WebServiceClient webServiceClient)
    {
        return null;
    }

    private Attributes readFromCouchbaseResponse(String responseBody)
    {
        return Attributes.fromMap(_json.fromJson(responseBody), AttributeName.Format.JSON);
    }

    @Override
    public boolean customQueryVerifiesPassword()
    {
        return _configuration.backendVerifiesPassword();
    }

    /**
     * Helper method that crafts the request path that the call is made to. Can consider
     * the username and password to substitute pars of the path if needed.
     */

    String createRequestPath(String subject, String password)
    {
        return _configuration.urlPath()
                .replaceAll(SUBJECT_PLACEHOLDER, urlEncode(subject))
                .replaceAll(PASSWORD_PLACEHOLDER, urlEncode(password));
    }

    private Map<String, String> createRequestParameterMap(String subjectId, @Nullable String password)
    {
        return null;
    }

}