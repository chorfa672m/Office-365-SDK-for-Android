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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequest;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequest.BatchStreamManager;
import com.msopentech.odatajclient.engine.communication.request.ODataStreamManager;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchResponseItem;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataChangeset;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataChangesetResponseItem;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataRetrieve;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataRetrieveResponseItem;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest.ODataEntityResponseImpl;
import org.junit.Test;
import com.msopentech.odatajclient.engine.communication.response.ODataBatchResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import com.msopentech.odatajclient.engine.utils.Wrapper;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.msopentech.org.apache.http.HttpResponse;

public class BatchTestITCase extends AbstractTest {

    private static String PREFIX = "!!PREFIX!!";

    private static String SUFFIX = "!!SUFFIX!!";

    private static int MAX = 10000;

    @Test
    public void stringStreaming() {
        final TestStreamManager streaming = new TestStreamManager();

        new StreamingThread(streaming).start();

        streaming.addObject((PREFIX + "\n").getBytes());

        for (int i = 0; i <= MAX; i++) {
            streaming.addObject((i + ") send info\n").getBytes());
        }

        streaming.addObject((SUFFIX).getBytes());
        streaming.finalizeBody();
    }

    @Test
    public void emptyBatchRequest() {
        // create your request
        final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testDefaultServiceRootURL);

        final BatchStreamManager payload = request.execute();
        final ODataBatchResponse response = payload.getResponse();

        assertEquals(202, response.getStatusCode());
        assertEquals("Accepted", response.getStatusMessage());

        final Iterator<ODataBatchResponseItem> iter = response.getBody();
        assertFalse(iter.hasNext());
    }

    @Test
    public void changesetWithError() {
        // create your request
        final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testDefaultServiceRootURL);

        final BatchStreamManager payload = request.execute();
        final ODataChangeset changeset = payload.addChangeset();

        URIBuilder targetURI;
        ODataEntityCreateRequest create;

        targetURI = client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Customer");
        for (int i = 1; i <= 2; i++) {
            // Create Customer into the changeset
            create = client.getCUDRequestFactory().getEntityCreateRequest(
                    targetURI.build(),
                    getSampleCustomerProfile(100 + i, "Sample customer", false));
            create.setFormat(ODataPubFormat.JSON);
            changeset.addRequest(create);
        }

        targetURI = client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("WrongEntitySet");
        create = client.getCUDRequestFactory().getEntityCreateRequest(
                targetURI.build(),
                getSampleCustomerProfile(105, "Sample customer", false));
        create.setFormat(ODataPubFormat.JSON);
        changeset.addRequest(create);

        targetURI = client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Customer");
        for (int i = 3; i <= 4; i++) {
            // Create Customer into the changeset
            create = client.getCUDRequestFactory().getEntityCreateRequest(
                    targetURI.build(),
                    getSampleCustomerProfile(100 + i, "Sample customer", false));
            create.setFormat(ODataPubFormat.ATOM);
            changeset.addRequest(create);
        }

        final ODataBatchResponse response = payload.getResponse();
        assertEquals(202, response.getStatusCode());
        assertEquals("Accepted", response.getStatusMessage());

        final Iterator<ODataBatchResponseItem> iter = response.getBody();
        final ODataChangesetResponseItem chgResponseItem = (ODataChangesetResponseItem) iter.next();

        final ODataResponse res = chgResponseItem.next();
        assertEquals(404, res.getStatusCode());
        assertEquals("Not Found", res.getStatusMessage());
        assertEquals(Integer.valueOf(3), Integer.valueOf(
                res.getHeader(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME).iterator().next()));
        assertFalse(chgResponseItem.hasNext());
    }

    @Test
    public void changesetWithReference() {
        // create your request
        final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testDefaultServiceRootURL);
        final BatchStreamManager streamManager = request.execute();

        final ODataChangeset changeset = streamManager.addChangeset();
        ODataEntity customer = getSampleCustomerProfile(20, "sample customer", false);

        URIBuilder uriBuilder = client.getURIBuilder(testAuthServiceRootURL).appendEntitySetSegment("Customer");

        // add create request
        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), customer);

        changeset.addRequest(createReq);

        // retrieve request reference
        int createRequestRef = changeset.getLastContentId();

        // add update request: link CustomerInfo(17) to the new customer
        final ODataEntity customerChanges = ODataObjectFactory.newEntity(customer.getName());
        customerChanges.addLink(ODataObjectFactory.newEntityNavigationLink(
                "Info",
                client.getURIBuilder(testAuthServiceRootURL).appendEntitySetSegment("CustomerInfo").
                appendKeySegment(17).build()));

        final ODataEntityUpdateRequest updateReq = client.getCUDRequestFactory().getEntityUpdateRequest(
                URI.create("$" + createRequestRef), UpdateType.PATCH, customerChanges);

        changeset.addRequest(updateReq);

        final ODataBatchResponse response = streamManager.getResponse();
        assertEquals(202, response.getStatusCode());
        assertEquals("Accepted", response.getStatusMessage());

        // verify response payload ...
        final Iterator<ODataBatchResponseItem> iter = response.getBody();

        final ODataBatchResponseItem item = iter.next();
        assertTrue(item instanceof ODataChangesetResponseItem);

        final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;

        ODataResponse res = chgitem.next();
        assertEquals(201, res.getStatusCode());
        assertTrue(res instanceof ODataEntityCreateResponse);

        customer = ((ODataEntityCreateResponse) res).getBody();

        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(
                URIUtils.getURI(testDefaultServiceRootURL, customer.getEditLink().toASCIIString() + "/Info"));

        assertEquals(Integer.valueOf(17),
                req.execute().getBody().getProperty("CustomerInfoId").getPrimitiveValue().<Integer>toCastValue());

        res = chgitem.next();
        assertEquals(204, res.getStatusCode());
        assertTrue(res instanceof ODataEntityUpdateResponse);

        // clean ...
        assertEquals(204, client.getCUDRequestFactory().getDeleteRequest(
                URIUtils.getURI(testDefaultServiceRootURL, customer.getEditLink().toASCIIString())).execute().
                getStatusCode());

        try {
            client.getRetrieveRequestFactory().getEntityRequest(
                    URIUtils.getURI(testDefaultServiceRootURL, customer.getEditLink().toASCIIString())).
                    execute().getBody();
            fail();
        } catch (Exception e) {
            // ignore
        }
    }

    @Test
    public void batchRequest() {
        // create your request
        final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testDefaultServiceRootURL);

        final BatchStreamManager streamManager = request.execute();

        // -------------------------------------------
        // Add retrieve item
        // -------------------------------------------
        ODataRetrieve retrieve = streamManager.addRetrieve();

        // prepare URI
        URIBuilder targetURI = client.getURIBuilder(testDefaultServiceRootURL);
        targetURI.appendEntityTypeSegment("Customer").appendKeySegment(-10).
                expand("Logins").select("CustomerId,Logins/Username");

        // create new request
        ODataEntityRequest query = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());
        query.setFormat(ODataPubFormat.ATOM);

        retrieve.setRequest(query);
        // -------------------------------------------

        // -------------------------------------------
        // Add changeset item
        // -------------------------------------------
        final ODataChangeset changeset = streamManager.addChangeset();

        // Update Product into the changeset
        targetURI = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10);
        final URI editLink = targetURI.build();

        final ODataEntity merge = ODataObjectFactory.newEntity(TEST_PRODUCT_TYPE);
        merge.setEditLink(editLink);

        merge.addProperty(ODataObjectFactory.newPrimitiveProperty(
                "Description", client.getPrimitiveValueBuilder().setText("new description from batch").build()));

        final ODataEntityUpdateRequest changes =
                client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.MERGE, merge);
        changes.setFormat(ODataPubFormat.JSON_FULL_METADATA);
        changes.setIfMatch(getETag(editLink));

        changeset.addRequest(changes);

        // Create Customer into the changeset
        targetURI = client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Customer");
        final ODataEntity original = getSampleCustomerProfile(1000, "Sample customer", false);
        final ODataEntityCreateRequest create =
                client.getCUDRequestFactory().getEntityCreateRequest(targetURI.build(), original);
        create.setFormat(ODataPubFormat.ATOM);
        changeset.addRequest(create);
        // -------------------------------------------

        // -------------------------------------------
        // Add retrieve item
        // -------------------------------------------
        retrieve = streamManager.addRetrieve();

        // prepare URI
        targetURI = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10);

        // create new request
        query = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());

        retrieve.setRequest(query);
        // -------------------------------------------

        final ODataBatchResponse response = streamManager.getResponse();
        assertEquals(202, response.getStatusCode());
        assertEquals("Accepted", response.getStatusMessage());

        final Iterator<ODataBatchResponseItem> iter = response.getBody();

        // retrive the first item (ODataRetrieve)
        ODataBatchResponseItem item = iter.next();
        assertTrue(item instanceof ODataRetrieveResponseItem);

        ODataRetrieveResponseItem retitem = (ODataRetrieveResponseItem) item;
        ODataResponse res = retitem.next();
        assertTrue(res instanceof ODataEntityResponseImpl);
        assertEquals(200, res.getStatusCode());
        assertEquals("OK", res.getStatusMessage());

        ODataEntityResponseImpl entres = (ODataEntityResponseImpl) res;
        ODataEntity entity = entres.getBody();
        assertEquals(new Integer(-10), entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue());

        // retrieve the second item (ODataChangeset)
        item = iter.next();
        assertTrue(item instanceof ODataChangesetResponseItem);

        final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;
        res = chgitem.next();
        assertTrue(res instanceof ODataEntityUpdateResponse);
        assertEquals(204, res.getStatusCode());
        assertEquals("No Content", res.getStatusMessage());

        res = chgitem.next();
        assertTrue(res instanceof ODataEntityCreateResponse);
        assertEquals(201, res.getStatusCode());
        assertEquals("Created", res.getStatusMessage());

        final ODataEntityCreateResponse createres = (ODataEntityCreateResponse) res;
        entity = createres.getBody();
        assertEquals(new Integer(1000), entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue());

        // retrive the third item (ODataRetrieve)
        item = iter.next();
        assertTrue(item instanceof ODataRetrieveResponseItem);

        retitem = (ODataRetrieveResponseItem) item;
        res = retitem.next();
        assertTrue(res instanceof ODataEntityResponseImpl);
        assertEquals(200, res.getStatusCode());
        assertEquals("OK", res.getStatusMessage());

        entres = (ODataEntityResponseImpl) res;
        entity = entres.getBody();
        assertEquals("new description from batch",
                entity.getProperty("Description").getPrimitiveValue().<String>toCastValue());

        assertFalse(iter.hasNext());
    }

    private static class TestStreamManager extends ODataStreamManager<ODataBatchResponse> {

        public TestStreamManager() {
            super(new Wrapper<Future<HttpResponse>>());
        }

        public ODataStreamManager<ODataBatchResponse> addObject(byte[] src) {
            stream(src);
            return this;
        }

        @Override
        protected ODataBatchResponse getResponse(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    /**
     * To be used for debug purposes.
     */
    private static class StreamingThread extends Thread {

        private final TestStreamManager streaming;

        public StreamingThread(final TestStreamManager streaming) {
            this.streaming = streaming;
        }

        @Override
        public void run() {
            try {
                final StringBuilder builder = new StringBuilder();

                byte[] buff = new byte[1024];

                int len;

                while ((len = streaming.getBody().read(buff)) >= 0) {
                    builder.append(new String(buff, 0, len));
                }

                assertTrue(builder.toString().startsWith(PREFIX));
                assertTrue(builder.toString().contains((MAX / 2) + ") send info"));
                assertTrue(builder.toString().contains((MAX / 3) + ") send info"));
                assertTrue(builder.toString().contains((MAX / 20) + ") send info"));
                assertTrue(builder.toString().contains((MAX / 30) + ") send info"));
                assertTrue(builder.toString().contains(MAX + ") send info"));
                assertTrue(builder.toString().endsWith(SUFFIX));

            } catch (IOException e) {
                fail();
            }
        }
    }

    private static class BatchStreamingThread extends Thread {

        private final BatchStreamManager streaming;

        public BatchStreamingThread(final BatchStreamManager streaming) {
            this.streaming = streaming;
        }

        @Override
        public void run() {
            try {
                final StringBuilder builder = new StringBuilder();

                byte[] buff = new byte[1024];

                int len;

                while ((len = streaming.getBody().read(buff)) >= 0) {
                    builder.append(new String(buff, 0, len));
                }

                LOG.debug("Batch request {}", builder.toString());

                assertTrue(builder.toString().contains("Content-Id:2"));
                assertTrue(builder.toString().contains("GET " + servicesODataServiceRootURL));
            } catch (IOException e) {
                fail();
            }
        }
    }
}
