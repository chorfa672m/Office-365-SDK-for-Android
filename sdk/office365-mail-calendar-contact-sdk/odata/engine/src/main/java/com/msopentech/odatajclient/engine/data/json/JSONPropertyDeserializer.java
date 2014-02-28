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
import java.net.URI;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.XMLUtils;

/**
 * Parse JSON string into <tt>JSONProperty</tt>.
 *
 * @see JSONProperty
 */
public class JSONPropertyDeserializer extends ODataJsonDeserializer<JSONProperty> {

    private final static String v4AnnotationPrefix = "@";
    
    @Override
    protected JSONProperty doDeserializeV3(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

        final JSONProperty property = new JSONProperty();

        if (tree.hasNonNull(ODataConstants.JSON_METADATA)) {
            property.setMetadata(URI.create(tree.get(ODataConstants.JSON_METADATA).textValue()));
            tree.remove(ODataConstants.JSON_METADATA);
        }

        try {
            final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
            final Document document = builder.newDocument();

            Element content = document.createElement(ODataConstants.ELEM_PROPERTY);

            if (property.getMetadata() != null) {
                final String metadataURI = property.getMetadata().toASCIIString();
                final int dashIdx = metadataURI.lastIndexOf('#');
                if (dashIdx != -1) {
                    content.setAttribute(ODataConstants.ATTR_M_TYPE, metadataURI.substring(dashIdx + 1));
                }
            }

            JsonNode subtree = null;
            if (tree.has(ODataConstants.JSON_VALUE)) {
                if (tree.has(ODataConstants.JSON_TYPE)
                        && StringUtils.isBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {

                    content.setAttribute(ODataConstants.ATTR_M_TYPE, tree.get(ODataConstants.JSON_TYPE).asText());
                }

                final JsonNode value = tree.get(ODataConstants.JSON_VALUE);
                if (value.isValueNode()) {
                    content.appendChild(document.createTextNode(value.asText()));
                } else if (EdmSimpleType.isGeospatial(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                    subtree = tree.objectNode();
                    ((ObjectNode) subtree).put(ODataConstants.JSON_VALUE, tree.get(ODataConstants.JSON_VALUE));
                    if (StringUtils.isNotBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                        ((ObjectNode) subtree).put(
                                ODataConstants.JSON_VALUE + "@" + ODataConstants.JSON_TYPE,
                                content.getAttribute(ODataConstants.ATTR_M_TYPE));
                    }
                } else {
                    subtree = tree.get(ODataConstants.JSON_VALUE);
                }
            } else {
                subtree = tree;
            }

            if (subtree != null) {
                DOMTreeUtilsV3.buildSubtree(content, subtree);
            }

            final List<Node> children = XMLUtils.getChildNodes(content, Node.ELEMENT_NODE);
            if (children.size() == 1) {
                final Element value = (Element) children.iterator().next();
                if (ODataConstants.JSON_VALUE.equals(XMLUtils.getSimpleName(value))) {
                    if (StringUtils.isNotBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                        value.setAttribute(ODataConstants.ATTR_M_TYPE, content.getAttribute(ODataConstants.ATTR_M_TYPE));
                    }
                    content = value;
                }
            }

            property.setContent(content);
        } catch (ParserConfigurationException e) {
            throw new JsonParseException("Cannot build property", parser.getCurrentLocation(), e);
        }

        return property;
    }

    @Override
    protected JSONProperty doDeserializeV4(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);
        final JSONProperty property = new JSONProperty();
        
        try {
            final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
            final Document document = builder.newDocument();

            Element content = document.createElement(ODataConstants.ELEM_PROPERTY);

            JsonNode subtree = null;
            if (tree.has(ODataConstants.JSON_VALUE)) {
                if (tree.has(v4AnnotationPrefix + ODataConstants.JSON_TYPE)
                        && StringUtils.isBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {

                    content.setAttribute(ODataConstants.ATTR_M_TYPE, tree.get(v4AnnotationPrefix + ODataConstants.JSON_TYPE).asText());
                }

                final JsonNode value = tree.get(ODataConstants.JSON_VALUE);
                if (value.isValueNode()) {
                    content.appendChild(document.createTextNode(value.asText()));
                } else if (EdmSimpleType.isGeospatial(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                    subtree = tree.objectNode();
                    ((ObjectNode) subtree).put(ODataConstants.JSON_VALUE, tree.get(ODataConstants.JSON_VALUE));
                    if (StringUtils.isNotBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                        ((ObjectNode) subtree).put(
                                ODataConstants.JSON_VALUE + "@" + ODataConstants.JSON_TYPE,
                                content.getAttribute(ODataConstants.ATTR_M_TYPE));
                    }
                } else {
                    subtree = tree.get(ODataConstants.JSON_VALUE);
                }
            } else {
                subtree = tree;
            }

            if (subtree != null) {
                DOMTreeUtilsV4.buildSubtree(content, subtree);
            }

            final List<Node> children = XMLUtils.getChildNodes(content, Node.ELEMENT_NODE);
            if (children.size() == 1) {
                final Element value = (Element) children.iterator().next();
                if (ODataConstants.JSON_VALUE.equals(XMLUtils.getSimpleName(value))) {
                    if (StringUtils.isNotBlank(content.getAttribute(ODataConstants.ATTR_M_TYPE))) {
                        value.setAttribute(ODataConstants.ATTR_M_TYPE, content.getAttribute(ODataConstants.ATTR_M_TYPE));
                    }
                    content = value;
                }
            }

            property.setContent(content);
        } catch (ParserConfigurationException e) {
            throw new JsonParseException("Cannot build property", parser.getCurrentLocation(), e);
        }

        return property;
    }
}
