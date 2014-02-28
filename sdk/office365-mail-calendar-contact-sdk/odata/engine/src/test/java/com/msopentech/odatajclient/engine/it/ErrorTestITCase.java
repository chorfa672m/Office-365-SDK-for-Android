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

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.communication.request.AbstractODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.invoke.ODataInvokeRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataInvokeResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import com.msopentech.org.apache.http.HttpResponse;
import com.msopentech.org.apache.http.client.HttpClient;
import org.junit.Test;

/**
 * This is the unit test class to check basic entity operations.
 */
public class ErrorTestITCase extends AbstractTest {

    private class ErrorGeneratingRequest
            extends AbstractODataBasicRequestImpl<ODataEntityCreateResponse, ODataPubFormat> {

        public ErrorGeneratingRequest(final HttpMethod method, final URI uri) {
            super(client, ODataPubFormat.class, method, uri);
        }

        @Override
        protected InputStream getPayload() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public ODataEntityCreateResponse execute() {
            final HttpResponse res = doExecute();
            return new ODataEntityCreateResponseImpl(httpClient, res);
        }

        private class ODataEntityCreateResponseImpl extends ODataResponseImpl implements ODataEntityCreateResponse {

            public ODataEntityCreateResponseImpl(final HttpClient client, final HttpResponse res) {
                super(client, res);
            }

            @Override
            public ODataEntity getBody() {
                return ODataObjectFactory.newEntity("Invalid");
            }
        }
    }

    private void stacktraceError(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
        uriBuilder.appendEntitySetSegment("Customer");

        final ErrorGeneratingRequest errorReq = new ErrorGeneratingRequest(HttpMethod.POST, uriBuilder.build());
        errorReq.setFormat(format);

        ODataClientErrorException ocee = null;
        try {
            errorReq.execute();
        } catch (ODataClientErrorException e) {
            LOG.error("ODataClientErrorException found", e);
            ocee = e;
        }
        assertNotNull(ocee);
        assertEquals(400, ocee.getStatusLine().getStatusCode());
        assertNotNull(ocee.getCause());
        assertNotNull(ocee.getODataError());
    }

    @Test
    public void xmlStacktraceError() {
        stacktraceError(ODataPubFormat.ATOM);
    }

    @Test
    public void jsonStacktraceError() {
        stacktraceError(ODataPubFormat.JSON);
    }

    private void notfoundError(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
        uriBuilder.appendEntitySetSegment("Customer(154)");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        ODataClientErrorException ocee = null;
        try {
            req.execute();
        } catch (ODataClientErrorException e) {
            LOG.error("ODataClientErrorException found", e);
            ocee = e;
        }
        assertNotNull(ocee);
        assertEquals(404, ocee.getStatusLine().getStatusCode());
        assertNull(ocee.getCause());
        assertNotNull(ocee.getODataError());
    }

    @Test
    public void xmlNotfoundError() {
        notfoundError(ODataPubFormat.ATOM);
    }

    @Test
    public void jsonNotfoundError() {
        notfoundError(ODataPubFormat.JSON);
    }

    private void instreamError(final ODataPubFormat format) {
        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport("InStreamErrorGetCustomer");

        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        final ODataInvokeRequest<ODataEntitySet> req =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
        req.setFormat(format);

        final ODataInvokeResponse<ODataEntitySet> res = req.execute();
        res.getBody();
    }

    @Test(expected = IllegalArgumentException.class)
    public void atomInstreamError() {
        instreamError(ODataPubFormat.ATOM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jsonInstreamError() {
        instreamError(ODataPubFormat.JSON);
    }
}
