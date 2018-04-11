package com.couchbase.curity.data.access.descriptor;

import com.couchbase.curity.data.access.CouchbaseCredentialDataAccessProvider;
import com.couchbase.curity.data.access.config.CouchbaseDataAccessProviderConfiguration;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.datasource.AttributeDataAccessProvider;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;
import se.curity.identityserver.sdk.plugin.descriptor.DataAccessProviderPluginDescriptor;

public final class CouchbaseDataAccessPluginDescriptor implements DataAccessProviderPluginDescriptor
{
    @Override
    public String getPluginImplementationType()
    {
        return "couchbase";
    }

    @Override
    public Class<? extends Configuration> getConfigurationType()
    {
        return CouchbaseDataAccessProviderConfiguration.class;
    }

    @Nullable
    @Override
    public Class<? extends CredentialDataAccessProvider> getCredentialDataAccessProvider()
    {
        return CouchbaseCredentialDataAccessProvider.class;
    }

    @Nullable
    @Override
    public Class<? extends AttributeDataAccessProvider> getAttributeDataAccessProvider()
    {
        return AttributeDataAccessProvider.class;
    }
}
