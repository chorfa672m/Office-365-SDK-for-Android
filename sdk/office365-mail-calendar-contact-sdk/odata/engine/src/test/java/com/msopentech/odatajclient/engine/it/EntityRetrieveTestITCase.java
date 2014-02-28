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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataGenericRetrieveRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataInlineEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInlineEntitySet;
import com.msopentech.odatajclient.engine.data.ODataObjectWrapper;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTest {

    protected String getServiceRoot() {
        return testDefaultServiceRootURL;
    }

    private void withInlineEntry(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).expand("Info");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();

        assertNotNull(entity);
        assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entity.getName());
        assertEquals(getServiceRoot() + "/Customer(-10)", entity.getEditLink().toASCIIString());

        assertEquals(5, entity.getNavigationLinks().size());
        assertTrue(entity.getAssociationLinks().isEmpty());

        boolean found = false;

        for (ODataLink link : entity.getNavigationLinks()) {
            if (link instanceof ODataInlineEntity) {
                final ODataEntity inline = ((ODataInlineEntity) link).getEntity();
                assertNotNull(inline);

                debugEntry(client.getBinder().getEntry(
                        inline, client.getResourceFactory().entryClassForFormat(format)), "Just read");

                final List<ODataProperty> properties = inline.getProperties();
                assertEquals(2, properties.size());

                assertTrue(properties.get(0).getName().equals("CustomerInfoId")
                        || properties.get(1).getName().equals("CustomerInfoId"));
                assertTrue(properties.get(0).getValue().toString().equals("11")
                        || properties.get(1).getValue().toString().equals("11"));

                found = true;
            }
        }

        assertTrue(found);
    }

    @Test
    public void withInlineEntryFromAtom() {
        withInlineEntry(ODataPubFormat.ATOM);
    }

    @Test
    public void withInlineEntryFromJSON() {
        // this needs to be full, otherwise there is no mean to recognize links
        withInlineEntry(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void withInlineFeed(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).expand("Orders");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();
        assertNotNull(entity);

        boolean found = false;

        for (ODataLink link : entity.getNavigationLinks()) {
            if (link instanceof ODataInlineEntitySet) {
                final ODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
                assertNotNull(inline);

                debugFeed(client.getBinder().getFeed(inline, client.getResourceFactory().feedClassForFormat(format)),
                        "Just read");

                found = true;
            }
        }

        assertTrue(found);
    }

    @Test
    public void withInlineFeedFromAtom() {
        withInlineFeed(ODataPubFormat.ATOM);
    }

    @Test
    public void withInlineFeedFromJSON() {
        // this needs to be full, otherwise there is no mean to recognize links
        withInlineFeed(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void genericRequest(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Car").appendKeySegment(16);

        final ODataGenericRetrieveRequest req =
                client.getRetrieveRequestFactory().getGenericRetrieveRequest(uriBuilder.build());
        req.setFormat(format.toString());

        final ODataRetrieveResponse<ODataObjectWrapper> res = req.execute();

        final ODataObjectWrapper wrapper = res.getBody();

        final ODataEntitySet entitySet = wrapper.getODataEntitySet();
        assertNull(entitySet);

        final ODataEntity entity = wrapper.getODataEntity();
        assertNotNull(entity);
    }

    @Test
    public void genericRequestAsAtom() {
        genericRequest(ODataPubFormat.ATOM);
    }

    @Test
    public void genericRequestAsJSON() {
        // this needs to be full, otherwise actions will not be provided
        genericRequest(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void multiKey(final ODataPubFormat format) {
        final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
        multiKey.put("FromUsername", "1");
        multiKey.put("MessageId", -10);

        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Message").appendKeySegment(multiKey);

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();
        assertNotNull(entity);
        assertEquals("1", entity.getProperty("FromUsername").getPrimitiveValue().<String>toCastValue());
    }

    @Test
    public void multiKeyAsAtom() {
        multiKey(ODataPubFormat.ATOM);
    }

    @Test
    public void multiKeyAsJSON() {
        multiKey(ODataPubFormat.JSON_FULL_METADATA);
    }

    @Test
    public void checkForETagAsATOM() {
        checkForETag(ODataPubFormat.ATOM);
    }

    @Test
    public void checkForETagAsJSON() {
        checkForETag(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void checkForETag(final ODataPubFormat format) {
        final URIBuilder uriBuilder =
                client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Product").appendKeySegment(-10);

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());

        final String etag = res.getEtag();
        assertTrue(StringUtils.isNotBlank(etag));

        final ODataEntity product = res.getBody();
        assertEquals(etag, product.getETag());
    }

    @Test(expected = IllegalArgumentException.class)
    public void issue99() {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Car");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(ODataPubFormat.JSON);

        // this statement should cause an IllegalArgumentException bearing JsonParseException
        // since we are attempting to parse an EntitySet as if it was an Entity
        req.execute().getBody();
    }
}
