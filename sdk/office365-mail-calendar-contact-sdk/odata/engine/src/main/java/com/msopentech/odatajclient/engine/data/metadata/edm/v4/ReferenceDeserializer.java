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
package com.msopentech.odatajclient.engine.data.metadata.edm.v4;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEdmDeserializer;
import java.io.IOException;
import java.net.URI;

public class ReferenceDeserializer extends AbstractEdmDeserializer<Reference> {

    @Override
    protected Reference doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final Reference reference = new Reference();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Uri".equals(jp.getCurrentName())) {
                    reference.setUri(URI.create(jp.nextTextValue()));
                } else if ("Include".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    reference.getIncludes().add(jp.getCodec().readValue(jp, Include.class));
                } else if ("IncludeAnnotations".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    reference.getIncludeAnnotations().add(jp.getCodec().readValue(jp, IncludeAnnotations.class));
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    reference.getAnnotations().add(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return reference;
    }

}
