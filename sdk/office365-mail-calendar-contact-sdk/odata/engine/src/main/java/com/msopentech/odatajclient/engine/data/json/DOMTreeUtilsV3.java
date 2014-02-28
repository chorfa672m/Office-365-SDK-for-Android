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
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.XMLUtils;

final class DOMTreeUtilsV3 {
    private DOMTreeUtilsV3(){}

    /**
     * Serializes DOM content as JSON.
     *
     * @param client OData client.
     * @param jgen JSON generator.
     * @param content content.
     * @param propType whether to output type information in the way needed for property values or not.
     * @throws IOException in case of write error.
     */
    public static void writeSubtree(
            final ODataClient client, final JsonGenerator jgen, final Node content, final boolean propType)
            throws IOException {

        for (Node child : XMLUtils.getChildNodes(content, Node.ELEMENT_NODE)) {
            final String childName = XMLUtils.getSimpleName(child);

            final Node typeAttr = child.getAttributes().getNamedItem(ODataConstants.ATTR_M_TYPE);
            if (typeAttr != null && EdmSimpleType.isGeospatial(typeAttr.getTextContent())) {
                jgen.writeStringField(propType ? ODataConstants.JSON_TYPE : childName + "@" + ODataConstants.JSON_TYPE,
                        typeAttr.getTextContent());

                jgen.writeObjectFieldStart(childName);
                GeospatialJSONHandler.serialize(jgen, (Element) child, typeAttr.getTextContent());
                jgen.writeEndObject();
            } else if (XMLUtils.hasOnlyTextChildNodes(child)) {
                if (child.hasChildNodes()) {
                    final String out;
                    if (typeAttr == null) {
                        out = child.getChildNodes().item(0).getNodeValue();
                    } else {
                        final EdmSimpleType type = EdmSimpleType.fromValue(typeAttr.getTextContent());
                        final ODataPrimitiveValue value = client.getPrimitiveValueBuilder().setType(type).
                                setText(child.getChildNodes().item(0).getNodeValue()).build();
                        out = value.toString();

                        jgen.writeStringField(childName + "@" + ODataConstants.JSON_TYPE, type.toString());
                    }
                    jgen.writeStringField(childName, out);
                } else {
                    if (child.getAttributes().getNamedItem(ODataConstants.ATTR_NULL) == null) {
                        if (typeAttr != null && EdmSimpleType.String.toString().equals(typeAttr.getTextContent())) {
                            jgen.writeStringField(childName + "@" + ODataConstants.JSON_TYPE, typeAttr.getTextContent());
                            jgen.writeStringField(childName, StringUtils.EMPTY);
                        } else {
                            jgen.writeArrayFieldStart(childName);
                            jgen.writeEndArray();
                        }
                    } else {
                        jgen.writeNullField(childName);
                    }
                }
            } else {
                if (XMLUtils.hasElementsChildNode(child)) {
                    jgen.writeArrayFieldStart(childName);

                    for (Node nephew : XMLUtils.getChildNodes(child, Node.ELEMENT_NODE)) {
                        if (XMLUtils.hasOnlyTextChildNodes(nephew)) {
                            jgen.writeString(nephew.getChildNodes().item(0).getNodeValue());
                        } else {
                            jgen.writeStartObject();
                            DOMTreeUtils.writeSubtree(client, jgen, nephew);
                            jgen.writeEndObject();
                        }
                    }

                    jgen.writeEndArray();
                } else {
                    jgen.writeObjectFieldStart(childName);
                    if (typeAttr != null) {
                        jgen.writeStringField(ODataConstants.JSON_TYPE, typeAttr.getTextContent());
                    }

                    DOMTreeUtils.writeSubtree(client, jgen, child);

                    jgen.writeEndObject();
                }
            }
        }
    }

    /**
     * Recursively builds DOM content out of JSON subtree rooted at given node.
     *
     * @param document root of the DOM document being built
     * @param parent parent of the nodes being generated during this step
     * @param node JSON node to be used as source for DOM elements
     */
    public static void buildSubtree(final Element parent, final JsonNode node) {
        final Iterator<String> fieldNameItor = node.fieldNames();
        final Iterator<JsonNode> nodeItor = node.elements();
        while (nodeItor.hasNext()) {
            final JsonNode child = nodeItor.next();
            final String name = fieldNameItor.hasNext() ? fieldNameItor.next() : "";

            // no name? array item
            if (name.isEmpty()) {
                final Element element = parent.getOwnerDocument().createElementNS(ODataConstants.NS_DATASERVICES,
                        ODataConstants.PREFIX_DATASERVICES + ODataConstants.ELEM_ELEMENT);
                parent.appendChild(element);

                if (child.isValueNode()) {
                    if (child.isNull()) {
                        element.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_NULL,
                                Boolean.toString(true));
                    } else {
                        element.appendChild(parent.getOwnerDocument().createTextNode(child.asText()));
                    }
                }

                if (child.isContainerNode()) {
                    buildSubtree(element, child);
                }
            } else if (!name.contains("@") && !ODataConstants.JSON_TYPE.equals(name)) {
                final Element property = parent.getOwnerDocument().createElementNS(
                        ODataConstants.NS_DATASERVICES, ODataConstants.PREFIX_DATASERVICES + name);
                parent.appendChild(property);

                boolean typeSet = false;
                if (node.hasNonNull(name + "@" + ODataConstants.JSON_TYPE)) {
                    property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                            node.get(name + "@" + ODataConstants.JSON_TYPE).textValue());
                    typeSet = true;
                }

                if (child.isNull()) {
                    property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_NULL,
                            Boolean.toString(true));
                } else if (child.isValueNode()) {
                    if (!typeSet) {
                        if (child.isInt()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.Int32.toString());
                        }
                        if (child.isLong()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.Int64.toString());
                        }
                        if (child.isBigDecimal()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.Decimal.toString());
                        }
                        if (child.isDouble()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.Double.toString());
                        }
                        if (child.isBoolean()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.Boolean.toString());
                        }
                        if (child.isTextual()) {
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    EdmSimpleType.String.toString());
                        }
                    }

                    property.appendChild(parent.getOwnerDocument().createTextNode(child.asText()));
                } else if (child.isContainerNode()) {
                    if (!typeSet && child.hasNonNull(ODataConstants.JSON_TYPE)) {
                        property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                child.get(ODataConstants.JSON_TYPE).textValue());
                    }

                    final String type = property.getAttribute(ODataConstants.ATTR_M_TYPE);
                    if (StringUtils.isNotBlank(type) && EdmSimpleType.isGeospatial(type)) {
                        if (EdmSimpleType.Geography.toString().equals(type)
                                || EdmSimpleType.Geometry.toString().equals(type)) {

                            final String geoType = child.get(ODataConstants.ATTR_TYPE).textValue();
                            property.setAttributeNS(ODataConstants.NS_METADATA, ODataConstants.ATTR_M_TYPE,
                                    geoType.startsWith("Geo")
                                    ? EdmSimpleType.namespace() + "." + geoType
                                    : type + geoType);
                        }

                        if (child.has(ODataConstants.JSON_COORDINATES) || child.has(ODataConstants.JSON_GEOMETRIES)) {
                            GeospatialJSONHandler.deserialize(
                                    child, property, property.getAttribute(ODataConstants.ATTR_M_TYPE));
                        }
                    } else {
                        buildSubtree(property, child);
                    }
                }
            }
        }
    }
}
