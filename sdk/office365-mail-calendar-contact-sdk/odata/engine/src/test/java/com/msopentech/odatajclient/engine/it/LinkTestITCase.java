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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataLinkCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataLinkUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataLinkCollectionRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataLinkOperationResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataLinkCollection;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * This is the unit test class to check basic link operations.
 */
public class LinkTestITCase extends AbstractTest {

    protected String getServiceRoot() {
        return testDefaultServiceRootURL;
    }

    private ODataLinkCollection doRetrieveLinkURIs(final ODataFormat format, final String linkname) throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10);

        final ODataLinkCollectionRequest req =
                client.getRetrieveRequestFactory().getLinkCollectionRequest(uriBuilder.build(), linkname);
        req.setFormat(format);

        final ODataRetrieveResponse<ODataLinkCollection> res = req.execute();
        assertEquals(200, res.getStatusCode());

        return res.getBody();
    }

    private void retrieveLinkURIs(final ODataFormat format) throws IOException {
        final List<URI> links = doRetrieveLinkURIs(format, "Logins").getLinks();
        assertEquals(2, links.size());
        assertTrue(links.contains(URI.create(getServiceRoot() + "/Login('1')")));
        assertTrue(links.contains(URI.create(getServiceRoot() + "/Login('4')")));
    }

    @Test
    public void retrieveXMLLinkURIsWithNext() throws IOException {
        final ODataLinkCollection uris = doRetrieveLinkURIs(ODataFormat.XML, "Orders");
        assertEquals(2, uris.getLinks().size());
        assertNotNull(uris.getNext());
    }

    @Test
    public void retrieveXMLLinkURIs() throws IOException {
        retrieveLinkURIs(ODataFormat.XML);
    }

    @Test
    public void retrieveJSONLinkURIs() throws IOException {
        retrieveLinkURIs(ODataFormat.JSON);
    }

    private void createLink(final ODataFormat format) throws IOException {
        // 1. read current Logins $links (for later usage)
        final List<URI> before = doRetrieveLinkURIs(format, "Logins").getLinks();
        assertEquals(2, before.size());

        // 2. create new link
        final ODataLink newLink = ODataObjectFactory.
                newAssociationLink(null, URI.create(getServiceRoot() + "/Login('3')"));

        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).appendLinksSegment("Logins");

        final ODataLinkCreateRequest req = client.getCUDRequestFactory().getLinkCreateRequest(uriBuilder.build(), newLink);
        req.setFormat(format);

        final ODataLinkOperationResponse res = req.execute();
        assertEquals(204, res.getStatusCode());

        final List<URI> after = doRetrieveLinkURIs(format, "Logins").getLinks();
        assertEquals(before.size() + 1, after.size());

        // 3. reset Logins $links as before this test was run
        after.removeAll(before);
        assertEquals(Collections.singletonList(newLink.getLink()), after);

        assertEquals(204, client.getCUDRequestFactory().getDeleteRequest(
                client.getURIBuilder(getServiceRoot()).appendEntityTypeSegment("Customer").
                appendKeySegment(-10).appendLinksSegment("Logins('3')").build()).execute().getStatusCode());
    }

    @Test
    public void createXMLLink() throws IOException {
        createLink(ODataFormat.XML);
    }

    @Test
    public void createJSONLink() throws IOException {
        createLink(ODataFormat.JSON);
    }

    private void updateLink(final ODataFormat format, final UpdateType updateType) throws IOException {
        // 1. read what is the link before the test runs
        final URI before = doRetrieveLinkURIs(format, "Info").getLinks().get(0);

        // 2. update the link
        ODataLink newLink =
                ODataObjectFactory.newAssociationLink(null, URI.create(getServiceRoot() + "/CustomerInfo(12)"));

        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntityTypeSegment("Customer").appendKeySegment(-10).appendLinksSegment("Info");

        ODataLinkUpdateRequest req =
                client.getCUDRequestFactory().getLinkUpdateRequest(uriBuilder.build(), updateType, newLink);
        req.setFormat(format);

        ODataLinkOperationResponse res = req.execute();
        assertEquals(204, res.getStatusCode());

        URI after = doRetrieveLinkURIs(format, "Info").getLinks().get(0);
        assertNotEquals(before, after);
        assertEquals(newLink.getLink(), after);

        // 3. reset back the link value
        newLink = ODataObjectFactory.newAssociationLink(null, before);
        req = client.getCUDRequestFactory().getLinkUpdateRequest(uriBuilder.build(), updateType, newLink);
        req.setFormat(format);

        res = req.execute();
        assertEquals(204, res.getStatusCode());

        after = doRetrieveLinkURIs(format, "Info").getLinks().get(0);
        assertEquals(before, after);
    }

    @Test
    public void updateXMLLink() throws IOException {
        updateLink(ODataFormat.XML, UpdateType.MERGE);
        updateLink(ODataFormat.XML, UpdateType.PATCH);
        updateLink(ODataFormat.XML, UpdateType.REPLACE);
    }

    @Test
    public void updateJSONLink() throws IOException {
        updateLink(ODataFormat.JSON, UpdateType.MERGE);
        updateLink(ODataFormat.JSON, UpdateType.PATCH);
        updateLink(ODataFormat.JSON, UpdateType.REPLACE);
    }
}
