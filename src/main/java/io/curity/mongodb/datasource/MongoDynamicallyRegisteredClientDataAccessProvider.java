/*
 *  Copyright 2019 Curity AB
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
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.scim.v2.extensions.DynamicallyRegisteredClientAttributes;
import se.curity.identityserver.sdk.datasource.DynamicallyRegisteredClientDataAccessProvider;

import static com.mongodb.client.model.Filters.eq;
import static se.curity.identityserver.sdk.attribute.scim.v2.extensions.DynamicallyRegisteredClientAttributes.CLIENT_ID;
import static se.curity.identityserver.sdk.attribute.scim.v2.extensions.DynamicallyRegisteredClientAttributes.RESOURCE_TYPE;

public final class MongoDynamicallyRegisteredClientDataAccessProvider implements DynamicallyRegisteredClientDataAccessProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(MongoDynamicallyRegisteredClientDataAccessProvider.class);
    private final MongoDatabase _database;

    public MongoDynamicallyRegisteredClientDataAccessProvider(ConnectionPool connectionPool)
    {
        _database = connectionPool.getDatabase();
    }

    @Override
    public DynamicallyRegisteredClientAttributes getByClientId(String clientId)
    {
        _logger.debug("Getting dynamic client with id: {}", clientId);
        @Nullable Map<String, Object> dcrClient = _database.getCollection(RESOURCE_TYPE)
                .find(eq(CLIENT_ID, clientId))
                .first();

        @Nullable Map<String, Object> strippedClient = MongoUtils.stripMongoFields(dcrClient);
        return (strippedClient == null)
                ? null
                : DynamicallyRegisteredClientAttributes.of(Attributes.fromMap(strippedClient));
    }

    @Override
    public void create(DynamicallyRegisteredClientAttributes dynamicallyRegisteredClientAttributes)
    {
        _logger.debug("Received request to CREATE dynamic client with id : {}",
                dynamicallyRegisteredClientAttributes.getClientId());
        Document document = new Document(dynamicallyRegisteredClientAttributes.toMap());
        _database.getCollection(RESOURCE_TYPE).insertOne(document);
    }

    @Override
    public void update(DynamicallyRegisteredClientAttributes dynamicallyRegisteredClientAttributes)
    {
        _logger.debug("Received request to UPDATE dynamic client for client : {}",
                dynamicallyRegisteredClientAttributes.getClientId());
        _database.getCollection(RESOURCE_TYPE).updateOne(eq(CLIENT_ID, dynamicallyRegisteredClientAttributes.getClientId()),
                new Document("$set", new Document(dynamicallyRegisteredClientAttributes.toMap())));
    }

    @Override
    public void delete(String clientId)
    {
        _logger.debug("Received request to DELETE dynamic client : {}", clientId);
        _database.getCollection(RESOURCE_TYPE).deleteOne(eq(CLIENT_ID, clientId));
    }
}
