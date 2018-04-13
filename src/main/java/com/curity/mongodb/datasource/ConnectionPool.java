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
