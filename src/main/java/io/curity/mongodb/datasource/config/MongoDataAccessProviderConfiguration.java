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

package io.curity.mongodb.datasource.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultInteger;
import se.curity.identityserver.sdk.config.annotation.DefaultString;
import se.curity.identityserver.sdk.config.annotation.Description;

public interface MongoDataAccessProviderConfiguration extends Configuration
{

    @Description("Mongo db host, e.g: 127.0.0.1")
    @DefaultString("127.0.0.1")
    String getHost();

    @Description("Mongo db port")
    @DefaultInteger(27017)
    int getPort();

    @Description("Database to use")
    String getDatabase();

    @Description("Username used to connect to db")
    String getUsername();

    @Description("Password used to connect to db")
    String getPassword();
}
