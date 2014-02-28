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
package com.msopentech.odatajclient.engine.it;

import static com.msopentech.odatajclient.engine.it.AbstractTest.TEST_PRODUCT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.header.ODataHeaderValues;
import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.net.URI;
import java.util.LinkedHashMap;
import org.junit.Test;

/**
 * This is the unit test class to check entity update operations.
 */
public class EntityUpdateTestITCase extends AbstractTest {

    protected String getServiceRoot() {
        return testDefaultServiceRootURL;
    }

    @Test
    public void mergeAsAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        final ODataEntity merge = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        merge.setEditLink(uri);
        updateEntityDescription(format, merge, UpdateType.MERGE, etag);
    }

    @Test
    public void mergeAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        final ODataEntity merge = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        merge.setEditLink(uri);
        updateEntityDescription(format, merge, UpdateType.MERGE, etag);
    }

    @Test
    public void patchAsAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        final ODataEntity patch = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        patch.setEditLink(uri);
        updateEntityDescription(format, patch, UpdateType.PATCH, etag);
    }

    @Test
    public void patchAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        final ODataEntity patch = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        patch.setEditLink(uri);
        updateEntityDescription(format, patch, UpdateType.PATCH, etag);
    }

    @Test
    public void replaceAsAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final ODataEntity changes = read(format, client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Car").appendKeySegment(14).build());
        updateEntityDescription(format, changes, UpdateType.REPLACE);
    }

    @Test
    public void replaceAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final ODataEntity changes = read(format, client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Car").appendKeySegment(14).build());
        updateEntityDescription(format, changes, UpdateType.REPLACE);
    }

    @Test
    public void patchLinkAsAtom() {
        patchLink(ODataPubFormat.ATOM);
    }

    @Test
    public void patchLinkAsJSON() {
        patchLink(ODataPubFormat.JSON_FULL_METADATA);
    }

    public void patchLink(final ODataPubFormat format) {
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).build();

        final ODataEntity patch =
                ODataObjectFactory.newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");
        patch.setEditLink(uri);

        // ---------------------------------------
        // Update to CustomerInfo(12)
        // ---------------------------------------
        URI customerInfoURI = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("CustomerInfo").appendKeySegment(12).build();

        patch.addLink(ODataObjectFactory.newEntityNavigationLink("Info", customerInfoURI));

        update(UpdateType.PATCH, patch, format, null);

        customerInfoURI = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).appendStructuralSegment("Info").build();

        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(customerInfoURI);
        req.setFormat(format);

        ODataEntity newInfo = req.execute().getBody();

        assertEquals(Integer.valueOf(12),
                newInfo.getProperty("CustomerInfoId").getPrimitiveValue().<Integer>toCastValue());
        // ---------------------------------------

        // ---------------------------------------
        // Restore to CustomerInfo(11)
        // ---------------------------------------
        patch.getNavigationLinks().clear();

        customerInfoURI = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("CustomerInfo").appendKeySegment(11).build();
        newInfo = read(format, customerInfoURI);

        patch.addLink(ODataObjectFactory.newEntityNavigationLink("Info", customerInfoURI));

        update(UpdateType.PATCH, patch, format, null);

        customerInfoURI = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).appendStructuralSegment("Info").build();

        req = client.getRetrieveRequestFactory().getEntityRequest(customerInfoURI);
        req.setFormat(format);

        newInfo = req.execute().getBody();

        assertEquals(Integer.valueOf(11),
                newInfo.getProperty("CustomerInfoId").getPrimitiveValue().<Integer>toCastValue());
        // ---------------------------------------
    }

    private ODataEntityUpdateRequest buildMultiKeyUpdateReq(final ODataPubFormat format) {
        final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
        multiKey.put("FromUsername", "1");
        multiKey.put("MessageId", -10);
        final ODataEntity message = read(format, client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Message").appendKeySegment(multiKey).build());
        message.getAssociationLinks().clear();
        message.getNavigationLinks().clear();

        final boolean before = message.getProperty("IsRead").getPrimitiveValue().<Boolean>toCastValue();
        message.getProperties().remove(message.getProperty("IsRead"));
        message.addProperty(ODataObjectFactory.newPrimitiveProperty("IsRead",
                client.getPrimitiveValueBuilder().setValue(!before).setType(EdmSimpleType.Boolean).build()));

        return client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.MERGE, message);
    }

    private void mergeMultiKey(final ODataPubFormat format) {
        final ODataEntityUpdateResponse res = buildMultiKeyUpdateReq(format).execute();
        assertEquals(204, res.getStatusCode());
    }

    @Test
    public void mergeMultiKeyAsAtom() {
        mergeMultiKey(ODataPubFormat.ATOM);
    }

    @Test
    public void mergeMultiKeyAsJSON() {
        mergeMultiKey(ODataPubFormat.JSON_FULL_METADATA);
    }

    @Test
    public void updateReturnContent() {
        final ODataEntityUpdateRequest req = buildMultiKeyUpdateReq(client.getConfiguration().getDefaultPubFormat());
        req.setPrefer(ODataHeaderValues.preferReturnContent);

        final ODataEntityUpdateResponse res = req.execute();
        assertEquals(200, res.getStatusCode());
        assertEquals(ODataHeaderValues.preferReturnContent,
                res.getHeader(ODataHeaders.HeaderName.preferenceApplied).iterator().next());
        assertNotNull(res.getBody());
    }

    @Test
    public void concurrentModification() {
        final URI uri = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        final ODataEntity product = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        product.setEditLink(uri);
        updateEntityStringProperty("BaseConcurrency",
                client.getConfiguration().getDefaultPubFormat(), product, UpdateType.MERGE, etag);

        try {
            updateEntityStringProperty("BaseConcurrency",
                    client.getConfiguration().getDefaultPubFormat(), product, UpdateType.MERGE, etag);
            fail();
        } catch (ODataClientErrorException e) {
            assertEquals(412, e.getStatusLine().getStatusCode());
        }
    }
}
