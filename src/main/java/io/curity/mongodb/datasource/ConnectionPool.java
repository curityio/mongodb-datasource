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

package io.curity.mongodb.datasource;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.curity.mongodb.datasource.config.MongoDataAccessProviderConfiguration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import se.curity.identityserver.sdk.plugin.ManagedObject;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ConnectionPool extends ManagedObject<MongoDataAccessProviderConfiguration>
{
    private final MongoClient _mongoClient;
    private final MongoDataAccessProviderConfiguration _configuration;


    public ConnectionPool(MongoDataAccessProviderConfiguration configuration)
    {
        super(configuration);
        _configuration = configuration;

        String uri = String.format("mongodb://%s:%s", _configuration.getHost(), _configuration.getPort());
        ConnectionString connectionString = new ConnectionString(uri);

        MongoCredential mongoCredential = MongoCredential.createCredential(configuration.getUsername(),
                configuration.getDatabase(), configuration.getPassword().toCharArray());

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // TODO: it must be possible to configure the connection with an SSL server truststore.
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .credential(mongoCredential)
                .applyConnectionString(connectionString)
                .build();

        _mongoClient = MongoClients.create(settings);
    }

    public MongoDatabase getDatabase()
    {
        return _mongoClient.getDatabase(_configuration.getDatabase());
    }

    @Override
    public void close()
    {
        _mongoClient.close();
    }
}
