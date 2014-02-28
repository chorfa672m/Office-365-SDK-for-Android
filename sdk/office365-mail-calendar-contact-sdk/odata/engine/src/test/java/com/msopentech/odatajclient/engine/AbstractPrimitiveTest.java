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
package com.msopentech.odatajclient.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataGeospatialValue;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Geospatial.Dimension;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Polygon;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

public abstract class AbstractPrimitiveTest extends AbstractTest {

    protected abstract ODataFormat getFormat();

    protected abstract ODataClient getClient();

    protected ODataVersion getVersion() {
        return getClient().getWorkingVersion();
    }

    protected String getFilename(final String entity, final String propertyName) {
        return getVersion().name().toLowerCase()
                + File.separatorChar
                + entity.replace('(', '_').replace(")", "")
                + "_" + propertyName.replaceAll("/", "_") + "." + getSuffix(getFormat());
    }

    protected ODataPrimitiveValue writePrimitiveValue(final ODataPrimitiveValue value) {
        final ODataPrimitiveValue newValue;
        if (EdmSimpleType.isGeospatial(value.getTypeName())) {
            newValue = getClient().getGeospatialValueBuilder().
                    setType(EdmSimpleType.fromValue(value.getTypeName())).
                    setTree(((ODataGeospatialValue) value).toTree()).build();
        } else {
            newValue = getClient().getPrimitiveValueBuilder().
                    setType(EdmSimpleType.fromValue(value.getTypeName())).
                    setValue(value.toValue()).build();
        }

        final InputStream written = getClient().getWriter().writeProperty(
                ODataObjectFactory.newPrimitiveProperty(ODataConstants.ELEM_PROPERTY, newValue),
                getFormat());
        return readPrimitiveValue(written);
    }

    protected ODataPrimitiveValue readPrimitiveValue(final InputStream input) {
        final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
        assertNotNull(property);
        assertTrue(property.hasPrimitiveValue());
        assertNotNull(property.getPrimitiveValue());

        return property.getPrimitiveValue();
    }

    protected ODataPrimitiveValue readPrimitiveValue(final String entity, final String propertyName) {
        final ODataPrimitiveValue value =
                readPrimitiveValue(getClass().getResourceAsStream(getFilename(entity, propertyName)));

        if (EdmSimpleType.isGeospatial(value.getTypeName())) {
            assertEquals(value.toValue(), writePrimitiveValue(value).toValue());
        } else {
            assertEquals(value.toString(), writePrimitiveValue(value).toString());
        }

        return value;
    }

    protected void int32(final String entity, final String propertyName, final int check) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.Int32.toString(), opv.getTypeName());

        final Integer value = opv.<Integer>toCastValue();
        assertNotNull(value);
        assertTrue(check == value);
    }

    protected void string(final String entity, final String propertyName, final String check) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.String.toString(), opv.getTypeName());

        final String value = opv.<String>toCastValue();
        assertNotNull(value);
        assertEquals(check, value);

        assertEquals(opv, writePrimitiveValue(opv));
    }

    protected void decimal(final String entity, final String propertyName, final BigDecimal check) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.Decimal.toString(), opv.getTypeName());

        final BigDecimal value = opv.<BigDecimal>toCastValue();
        assertNotNull(value);
        assertTrue(check.equals(value));
    }

    protected void datetime(final String entity, final String propertyName, final String check) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.DateTime.toString(), opv.getTypeName());

        final ODataTimestamp value = opv.<ODataTimestamp>toCastValue();
        assertNotNull(value);
        assertEquals(check, opv.toString());
    }

    protected void guid(final String entity, final String propertyName, final String check) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.Guid.toString(), opv.getTypeName());

        final UUID value = opv.<UUID>toCastValue();
        assertNotNull(value);
        assertEquals(check, opv.toString());
    }

    protected void binary(final String entity, final String propertyName) {
        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(EdmSimpleType.Binary.toString(), opv.getTypeName());

        final byte[] value = opv.<byte[]>toCastValue();
        assertNotNull(value);
        assertTrue(value.length > 0);
        assertTrue(Base64.isBase64(opv.toString()));
    }

    private void checkPoint(final Point point, final Point check) {
        assertEquals(check.getX(), point.getX(), 0);
        assertEquals(check.getY(), point.getY(), 0);
        assertEquals(check.getZ(), point.getZ(), 0);
    }

    protected void point(
            final String entity,
            final String propertyName,
            final Point expectedValues,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final Point point = opv.<Point>toCastValue();
        assertNotNull(point);
        assertEquals(expectedDimension, point.getDimension());

        checkPoint(point, expectedValues);
    }

    private void checkLine(final LineString line, final List<Point> check) {
        final List<Point> points = new ArrayList<Point>();

        for (Point point : line) {
            points.add(point);
        }

        assertEquals(check.size(), points.size());

        for (int i = 0; i < points.size(); i++) {
            checkPoint(points.get(i), check.get(i));
        }
    }

    protected void lineString(
            final String entity,
            final String propertyName,
            final List<Point> check,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final LineString lineString = opv.<LineString>toCastValue();
        assertNotNull(lineString);
        assertEquals(expectedDimension, lineString.getDimension());

        checkLine(lineString, check);
    }

    protected void multiPoint(
            final String entity,
            final String propertyName,
            final List<Point> check,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final MultiPoint multiPoint = opv.<MultiPoint>toCastValue();
        assertNotNull(multiPoint);
        assertEquals(expectedDimension, multiPoint.getDimension());

        final List<Point> points = new ArrayList<Point>();

        for (Point point : multiPoint) {
            points.add(point);
        }

        assertEquals(check.size(), points.size());

        for (int i = 0; i < points.size(); i++) {
            checkPoint(points.get(i), check.get(i));
        }
    }

    protected void multiLine(
            final String entity,
            final String propertyName,
            final List<List<Point>> check,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final MultiLineString multiLine = opv.<MultiLineString>toCastValue();
        assertNotNull(multiLine);
        assertEquals(expectedDimension, multiLine.getDimension());

        final List<LineString> lines = new ArrayList<LineString>();

        int i = 0;
        for (LineString line : multiLine) {
            checkLine(line, check.get(i));
            i++;
        }
    }

    private void checkPoligon(
            final Polygon polygon,
            final List<Point> checkInterior,
            final List<Point> checkExterior) {

        final List<Point> points = new ArrayList<Point>();

        for (Point point : polygon.getInterior()) {
            points.add(point);
        }

        assertEquals(checkInterior.size(), points.size());

        for (int i = 0; i < points.size(); i++) {
            checkPoint(checkInterior.get(i), points.get(i));
        }

        points.clear();

        for (Point point : polygon.getExterior()) {
            points.add(point);
        }

        assertEquals(checkExterior.size(), points.size());

        for (int i = 0; i < points.size(); i++) {
            checkPoint(checkExterior.get(i), points.get(i));
        }

    }

    protected void polygon(
            final String entity,
            final String propertyName,
            final List<Point> checkInterior,
            final List<Point> checkExterior,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final Polygon polygon = opv.<Polygon>toCastValue();

        assertNotNull(polygon);
        assertEquals(expectedDimension, polygon.getDimension());

        checkPoligon(polygon, checkInterior, checkExterior);

    }

    protected void multiPolygon(
            final String entity,
            final String propertyName,
            final List<List<Point>> checkInterior,
            final List<List<Point>> checkExterior,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final MultiPolygon multiPolygon = opv.<MultiPolygon>toCastValue();
        assertNotNull(multiPolygon);
        assertEquals(expectedDimension, multiPolygon.getDimension());

        int i = 0;
        for (Polygon polygon : multiPolygon) {
            checkPoligon(
                    polygon,
                    checkInterior.isEmpty() ? Collections.<Point>emptyList() : checkInterior.get(i),
                    checkExterior.isEmpty() ? Collections.<Point>emptyList() : checkExterior.get(i));
            i++;
        }
    }

    protected void geomCollection(
            final String entity,
            final String propertyName,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final GeospatialCollection collection = opv.<GeospatialCollection>toCastValue();
        assertNotNull(collection);
        assertEquals(expectedDimension, collection.getDimension());

        final Iterator<Geospatial> itor = collection.iterator();
        int count = 0;
        while (itor.hasNext()) {
            count++;

            final Geospatial geospatial = itor.next();
            if (count == 1) {
                assertTrue(geospatial instanceof Point);
            }
            if (count == 2) {
                assertTrue(geospatial instanceof LineString);
            }
        }
        assertEquals(2, count);
    }

    protected void geogCollection(
            final String entity,
            final String propertyName,
            final EdmSimpleType expectedType,
            final Dimension expectedDimension) {

        final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
        assertEquals(expectedType.toString(), opv.getTypeName());

        final GeospatialCollection collection = opv.<GeospatialCollection>toCastValue();
        assertNotNull(collection);
        assertEquals(expectedDimension, collection.getDimension());

        final Iterator<Geospatial> itor = collection.iterator();
        int count = 0;
        while (itor.hasNext()) {
            count++;

            final Geospatial geospatial = itor.next();
            if (count == 1) {
                assertTrue(geospatial instanceof GeospatialCollection);
            }
            if (count == 2) {
                assertTrue(geospatial instanceof GeospatialCollection);
            }
        }
        assertEquals(2, count);
    }
}
