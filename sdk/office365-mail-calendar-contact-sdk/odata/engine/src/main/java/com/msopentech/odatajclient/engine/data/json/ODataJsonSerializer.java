/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.data.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.msopentech.odatajclient.engine.client.ODataClient;

import java.io.IOException;

abstract class ODataJsonSerializer<T> extends JsonSerializer<T> {

    protected ODataClient client;

    protected abstract void doSerializeV3(T value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException;
    
    protected abstract void doSerializeV4(T value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException;

    @Override
    public void serialize(final T value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {

        client = (ODataClient) provider.getAttribute(ODataClient.class);
        switch (client.getWorkingVersion()) {
            case V3:
                doSerializeV3(value, jgen, provider);
                break;
            case V4:
                doSerializeV4(value, jgen, provider);
                break;
            default:
                throw new UnsupportedOperationException("Unknown OData version");
        }
    }

}
