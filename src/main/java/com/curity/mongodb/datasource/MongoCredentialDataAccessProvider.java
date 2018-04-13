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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.attribute.SubjectAttributes;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;

import java.util.Optional;

public class MongoCredentialDataAccessProvider implements CredentialDataAccessProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(MongoCredentialDataAccessProvider.class);

    private final MongoDatabase _database;

    private final MongoUtils _mongoUtils;

    @SuppressWarnings("unused") // used through DI
    public MongoCredentialDataAccessProvider(ConnectionPool connectionPool)
    {
        _database = connectionPool.getDatabase();
        _mongoUtils = new MongoUtils(_database);
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
        Attributes accountAttributes = _mongoUtils.getAccountAttributes("userName", userName, false);
        if (accountAttributes == null)
        {
            accountAttributes = Attributes.of(Attribute.of("password", ""));
        }
        return AuthenticationAttributes.of(SubjectAttributes.of(userName, accountAttributes),
                ContextAttributes.empty());
    }

    @Override
    public boolean customQueryVerifiesPassword()
    {
        return false;
    }

}