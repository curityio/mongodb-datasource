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

import com.curity.mongodb.datasource.config.MongoDataAccessProviderConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
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
        ServerAddress serverAddress = new ServerAddress(configuration.getHost(), configuration.getPort());
        MongoCredential mongoCredential = MongoCredential.createCredential(configuration.getUsername(),
                configuration.getDatabase(), configuration.getPassword().toCharArray());

        CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // TODO: it must be possible to configure the connection with an SSL server truststore.
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
        _mongoClient = new MongoClient(serverAddress, mongoCredential, options);
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
