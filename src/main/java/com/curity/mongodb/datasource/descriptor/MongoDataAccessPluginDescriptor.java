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

package com.curity.mongodb.datasource.descriptor;

import com.curity.mongodb.datasource.ConnectionPool;
import com.curity.mongodb.datasource.MongoBucketDataAccessProvider;
import com.curity.mongodb.datasource.MongoCredentialDataAccessProvider;
import com.curity.mongodb.datasource.MongoDeviceDataAccessProvider;
import com.curity.mongodb.datasource.MongoUserAccountDataAccessProvider;
import com.curity.mongodb.datasource.config.MongoDataAccessProviderConfiguration;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.datasource.BucketDataAccessProvider;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;
import se.curity.identityserver.sdk.datasource.DeviceDataAccessProvider;
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
    public Class<? extends DeviceDataAccessProvider> getDeviceDataAccessProvider()
    {
        return MongoDeviceDataAccessProvider.class;
    }

    @Nullable
    @Override
    public Class<? extends BucketDataAccessProvider> getBucketDataAccessProvider()
    {
        return MongoBucketDataAccessProvider.class;
    }

    public Optional<? extends ManagedObject<MongoDataAccessProviderConfiguration>> createManagedObject(
            MongoDataAccessProviderConfiguration configuration)
    {
        return Optional.of(new ConnectionPool(configuration));
    }
}
