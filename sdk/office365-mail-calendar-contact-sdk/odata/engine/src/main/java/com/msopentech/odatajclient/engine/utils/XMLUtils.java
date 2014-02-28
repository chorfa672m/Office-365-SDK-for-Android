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
package com.msopentech.odatajclient.engine.utils;

import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Geospatial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML utilities.
 */
public final class XMLUtils {

    public static final AbstractDOMParser PARSER;

    static {
        final Iterator<AbstractDOMParser> itor =
                ServiceLoader.load(AbstractDOMParser.class, Thread.currentThread().getContextClassLoader()).iterator();
        PARSER = itor.hasNext() ? itor.next() : new DefaultDOMParserImpl();
    }

    private XMLUtils() {
        // Empty private constructor for static utility classes       
    }

    /**
     * Gets XML node name.
     *
     * @param node node.
     * @return node name.
     */
    public static String getSimpleName(final Node node) {
        return node.getLocalName() == null
                ? node.getNodeName().substring(node.getNodeName().indexOf(':') + 1)
                : node.getLocalName();
    }

    /**
     * Gets the given node's children of the given type.
     *
     * @param node parent.
     * @param nodetype searched child type.
     * @return children.
     */
    public static List<Node> getChildNodes(final Node node, final short nodetype) {
        final List<Node> result = new ArrayList<Node>();

        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeType() == nodetype) {
                result.add(child);
            }
        }

        return result;
    }

    /**
     * Gets the given node's children with the given name.
     *
     * @param node parent.
     * @param nodetype searched child name.
     * @return children.
     */
    public static List<Element> getChildElements(final Element node, final String name) {
        final List<Element> result = new ArrayList<Element>();

        if (StringUtils.isNotBlank(name)) {
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if ((child instanceof Element) && name.equals(child.getNodeName())) {
                    result.add((Element) child);
                }
            }
        }

        return result;
    }

    /**
     * Checks if the given node has <tt>element</tt> children.
     *
     * @param node parent.
     * @return 'TRUE' if the given node has at least one <tt>element</tt> child; 'FALSE' otherwise.
     */
    public static boolean hasElementsChildNode(final Node node) {
        boolean found = false;

        for (Node child : getChildNodes(node, Node.ELEMENT_NODE)) {
            if (ODataConstants.ELEM_ELEMENT.equals(XMLUtils.getSimpleName(child))) {
                found = true;
            }
        }

        return found;
    }

    /**
     * Checks if the given node has only text children.
     *
     * @param node parent.
     * @return 'TRUE' if the given node has only text children; 'FALSE' otherwise.
     */
    public static boolean hasOnlyTextChildNodes(final Node node) {
        boolean result = true;
        final NodeList children = node.getChildNodes();
        for (int i = 0; result && i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeType() != Node.TEXT_NODE) {
                result = false;
            }
        }

        return result;
    }

    public static EdmSimpleType simpleTypeForNode(final Geospatial.Dimension dimension, final Node node) {
        EdmSimpleType type = null;

        if (ODataConstants.ELEM_POINT.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyPoint
                    : EdmSimpleType.GeometryPoint;
        } else if (ODataConstants.ELEM_MULTIPOINT.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyMultiPoint
                    : EdmSimpleType.GeometryMultiPoint;
        } else if (ODataConstants.ELEM_LINESTRING.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyLineString
                    : EdmSimpleType.GeometryLineString;
        } else if (ODataConstants.ELEM_MULTILINESTRING.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyMultiLineString
                    : EdmSimpleType.GeometryMultiLineString;
        } else if (ODataConstants.ELEM_POLYGON.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyPolygon
                    : EdmSimpleType.GeometryPolygon;
        } else if (ODataConstants.ELEM_MULTIPOLYGON.equals(node.getNodeName())) {
            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyMultiPolygon
                    : EdmSimpleType.GeometryMultiPolygon;
        } else if (ODataConstants.ELEM_GEOCOLLECTION.equals(node.getNodeName())
                || ODataConstants.ELEM_GEOMEMBERS.equals(node.getNodeName())) {

            type = dimension == Geospatial.Dimension.GEOGRAPHY
                    ? EdmSimpleType.GeographyCollection
                    : EdmSimpleType.GeometryCollection;
        }

        return type;
    }
}
