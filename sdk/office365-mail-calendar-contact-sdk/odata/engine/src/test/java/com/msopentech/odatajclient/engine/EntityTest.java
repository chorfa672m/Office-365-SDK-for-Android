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

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataOperation;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.data.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.io.InputStream;
import java.util.Collections;
import org.junit.Test;

public class EntityTest extends AbstractTest {

    private ODataV3Client getClient() {
        return v3Client;
    }

    private void readAndWrite(final ODataPubFormat format) {
        final InputStream input = getClass().getResourceAsStream("Customer_-10." + getSuffix(format));
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(input, getClient().getResourceFactory().entryClassForFormat(format)));
        assertNotNull(entity);

        assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entity.getName());
        assertTrue(entity.getEditLink().toASCIIString().endsWith("/Customer(-10)"));
        assertEquals(5, entity.getNavigationLinks().size());
        assertEquals(2, entity.getEditMediaLinks().size());

        boolean check = false;

        for (ODataLink link : entity.getNavigationLinks()) {
            if ("Wife".equals(link.getName()) && (link.getLink().toASCIIString().endsWith("/Customer(-10)/Wife"))) {
                check = true;
            }
        }

        assertTrue(check);

        final ODataEntity written = getClient().getBinder().getODataEntity(
                getClient().getBinder().getEntry(entity, getClient().getResourceFactory().entryClassForFormat(format)));
        assertEquals(entity, written);
    }

    @Test
    public void fromAtom() {
        readAndWrite(ODataPubFormat.ATOM);
    }

    @Test
    public void fromJSON() {
        readAndWrite(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void readGeospatial(final ODataPubFormat format) {
        final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-8." + getSuffix(format));
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(input, getClient().getResourceFactory().entryClassForFormat(format)));
        assertNotNull(entity);

        boolean found = false;
        for (ODataProperty property : entity.getProperties()) {
            if ("GeogMultiLine".equals(property.getName())) {
                found = true;
                assertTrue(property.hasPrimitiveValue());
                assertEquals(EdmSimpleType.GeographyMultiLineString.toString(),
                        property.getPrimitiveValue().getTypeName());
            }
        }
        assertTrue(found);

        final ODataEntity written = getClient().getBinder().getODataEntity(
                getClient().getBinder().getEntry(entity, getClient().getResourceFactory().entryClassForFormat(format)));
        assertEquals(entity, written);
    }

    @Test
    public void withGeospatialFromAtom() {
        readGeospatial(ODataPubFormat.ATOM);
    }

    @Test
    public void withGeospatialFromJSON() {
        // this needs to be full, otherwise there is no mean to recognize geospatial types
        readGeospatial(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void withActions(final ODataPubFormat format) {
        final InputStream input = getClass().getResourceAsStream("ComputerDetail_-10." + getSuffix(format));
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(input, getClient().getResourceFactory().entryClassForFormat(format)));
        assertNotNull(entity);

        assertEquals(1, entity.getOperations().size());
        assertEquals("ResetComputerDetailsSpecifications", entity.getOperations().get(0).getTitle());

        final ODataEntity written = getClient().getBinder().getODataEntity(
                getClient().getBinder().getEntry(entity, getClient().getResourceFactory().entryClassForFormat(format)));
        entity.setOperations(Collections.<ODataOperation>emptyList());
        assertEquals(entity, written);
    }

    @Test
    public void withActionsFromAtom() {
        withActions(ODataPubFormat.ATOM);
    }

    @Test
    public void withActionsFromJSON() {
        // this needs to be full, otherwise actions will not be provided
        withActions(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void mediaEntity(final ODataPubFormat format) {
        final InputStream input = getClass().getResourceAsStream("Car_16." + getSuffix(format));
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(input, getClient().getResourceFactory().entryClassForFormat(format)));
        assertNotNull(entity);
        assertTrue(entity.isMediaEntity());
        assertNotNull(entity.getMediaContentSource());
        assertNotNull(entity.getMediaContentType());

        final ODataEntity written = getClient().getBinder().getODataEntity(
                getClient().getBinder().getEntry(entity, getClient().getResourceFactory().entryClassForFormat(format)));
        assertEquals(entity, written);
    }

    @Test
    public void mediaEntityFromAtom() {
        mediaEntity(ODataPubFormat.ATOM);
    }

    @Test
    public void mediaEntityFromJSON() {
        mediaEntity(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void issue128(final ODataPubFormat format) {
        final InputStream input = getClass().getResourceAsStream("AllGeoTypesSet_-5." + getSuffix(format));
        final ODataEntity entity = getClient().getBinder().getODataEntity(
                getClient().getDeserializer().toEntry(input, getClient().getResourceFactory().entryClassForFormat(format)));
        assertNotNull(entity);

        final ODataProperty geogCollection = entity.getProperty("GeogCollection");
        assertEquals(EdmSimpleType.GeographyCollection.toString(), geogCollection.getPrimitiveValue().getTypeName());

        int count = 0;
        for (Geospatial g : geogCollection.getPrimitiveValue().<GeospatialCollection>toCastValue()) {
            assertNotNull(g);
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void issue128FromAtom() {
        issue128(ODataPubFormat.ATOM);
    }

    @Test
    public void issue128FromJSON() {
        issue128(ODataPubFormat.JSON_FULL_METADATA);
    }
}
