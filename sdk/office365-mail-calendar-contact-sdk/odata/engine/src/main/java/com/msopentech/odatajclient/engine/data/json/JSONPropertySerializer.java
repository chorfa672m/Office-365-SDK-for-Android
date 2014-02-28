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
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.XMLUtils;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writes out JSON string from <tt>JSONProperty</tt>.
 *
 * @see JSONProperty
 */
public class JSONPropertySerializer extends ODataJsonSerializer<JSONProperty> {

    @Override
    public void doSerializeV3(final JSONProperty property, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        if (property.getMetadata() != null) {
            jgen.writeStringField(ODataConstants.JSON_METADATA, property.getMetadata().toASCIIString());
        }

        final Element content = property.getContent();
        if (XMLUtils.hasOnlyTextChildNodes(content)) {
            jgen.writeStringField(ODataConstants.JSON_VALUE, content.getTextContent());
        } else {
            try {
                final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
                final Document document = builder.newDocument();
                final Element wrapper = document.createElement(ODataConstants.ELEM_PROPERTY);

                if (XMLUtils.hasElementsChildNode(content)) {
                    wrapper.appendChild(document.renameNode(
                            document.importNode(content, true), null, ODataConstants.JSON_VALUE));

                    DOMTreeUtils.writeSubtree(client, jgen, wrapper);
                } else if (EdmSimpleType.isGeospatial(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                    wrapper.appendChild(document.renameNode(
                            document.importNode(content, true), null, ODataConstants.JSON_VALUE));

                    DOMTreeUtilsV3.writeSubtree(client, jgen, wrapper, true);
                } else {
                    DOMTreeUtils.writeSubtree(client, jgen, content);
                }
            } catch (Exception e) {
                throw new JsonParseException("Cannot serialize property", JsonLocation.NA, e);
            }
        }

        jgen.writeEndObject();
    }
    
    @Override
    public void doSerializeV4(final JSONProperty property, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();

        final Element content = property.getContent();
        if (XMLUtils.hasOnlyTextChildNodes(content)) {
            jgen.writeStringField(ODataConstants.JSON_VALUE, content.getTextContent());
        } else {
            try {
                final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
                final Document document = builder.newDocument();
                final Element wrapper = document.createElement(ODataConstants.ELEM_PROPERTY);

                if (XMLUtils.hasElementsChildNode(content)) {
                    wrapper.appendChild(document.renameNode(
                            document.importNode(content, true), null, ODataConstants.JSON_VALUE));

                    DOMTreeUtils.writeSubtree(client, jgen, wrapper);
                } else if (EdmSimpleType.isGeospatial(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                    wrapper.appendChild(document.renameNode(
                            document.importNode(content, true), null, ODataConstants.JSON_VALUE));

                    DOMTreeUtilsV4.writeSubtree(client, jgen, wrapper, true);
                } else {
                    DOMTreeUtils.writeSubtree(client, jgen, content);
                }
            } catch (Exception e) {
                throw new JsonParseException("Cannot serialize property", JsonLocation.NA, e);
            }
        }

        jgen.writeEndObject();
    }
}
