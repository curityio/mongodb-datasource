package com.curity.mongodb.datasource.descriptor;

import com.curity.mongodb.datasource.ConnectionPool;
import com.curity.mongodb.datasource.MongoCredentialDataAccessProvider;
import com.curity.mongodb.datasource.MongoUserAccountDataAccessProvider;
import com.curity.mongodb.datasource.config.MongoDataAccessProviderConfiguration;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.datasource.AttributeDataAccessProvider;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;
import se.curity.identityserver.sdk.datasource.UserAccountDataAccessProvider;
import se.curity.identityserver.sdk.plugin.ManagedObject;
import se.curity.identityserver.sdk.plugin.descriptor.DataAccessProviderPluginDescriptor;

import java.util.Optional;

public final class MongoDataAccessPluginDescriptor implements DataAccessProviderPluginDescriptor<MongoDataAccessProviderConfiguration>
{
    @Override
    public String getPluginImplementationType()
    {
        return "mongodb";
    }

    @Override
    public Class<MongoDataAccessProviderConfiguration> getConfigurationType()
    {
        return MongoDataAccessProviderConfiguration.class;
    }

    @Nullable
    @Override
    public Class<? extends CredentialDataAccessProvider> getCredentialDataAccessProvider()
    {
        return MongoCredentialDataAccessProvider.class;
    }


    @Nullable
    @Override
    public Class<? extends UserAccountDataAccessProvider> getUserAccountDataAccessProvider()
    {
        return MongoUserAccountDataAccessProvider.class;
    }


    @Nullable
    @Override
    public Class<? extends AttributeDataAccessProvider> getAttributeDataAccessProvider()
    {
        return AttributeDataAccessProvider.class;
    }


    public Optional<? extends ManagedObject<MongoDataAccessProviderConfiguration>> createManagedObject(
            MongoDataAccessProviderConfiguration configuration)
    {
        return Optional.of(new ConnectionPool(configuration));
    }
}
