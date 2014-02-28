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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.msopentech.odatajclient.engine.client.ODataClient;

abstract class ODataJsonDeserializer<T> extends JsonDeserializer<T> {

    protected ODataClient client;

    protected abstract T doDeserializeV3(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException;
    
    protected abstract T doDeserializeV4(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException;

    @Override
    public T deserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        client = (ODataClient) ctxt.findInjectableValue(ODataClient.class.getName(), null, null);
        switch (client.getWorkingVersion()) {
            case V3:
                return doDeserializeV3(jp, ctxt);
            case V4:
                return doDeserializeV4(jp, ctxt);
            default:
                throw new UnsupportedOperationException("Unknown OData Version");
        }
        
    }

}
