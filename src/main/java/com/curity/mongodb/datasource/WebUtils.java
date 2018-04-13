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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

final class WebUtils
{
    private static final Logger _logger = LoggerFactory.getLogger(WebUtils.class);

    private WebUtils()
    {
    }

    static String urlEncode(String value)
    {
        try
        {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException("Cannot encode parameter, UTF-8 is not supported");
        }
    }

    static String urlEncodedFormData(Map<String, String> formParameters)
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;

        for (Map.Entry<String, String> entry : formParameters.entrySet())
        {
            if (!first)
            {
                stringBuilder.append("&");
            }
            else
            {
                first = false;
            }
            try
            {
                stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new IllegalStateException("Unable to encode form data using UTF-8");
            }
        }

        return stringBuilder.toString();
    }

    static boolean hasSuccessStatusCode(HttpResponse response)
    {
        return response.statusCode() >= 200
                && response.statusCode() < 300;
    }
}
