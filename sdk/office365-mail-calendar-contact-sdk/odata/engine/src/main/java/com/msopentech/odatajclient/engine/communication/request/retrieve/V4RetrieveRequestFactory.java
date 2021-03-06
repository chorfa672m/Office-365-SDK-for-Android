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
package com.msopentech.odatajclient.engine.communication.request.retrieve;

import com.msopentech.odatajclient.engine.client.ODataV4Client;
import org.apache.commons.lang3.StringUtils;

public class V4RetrieveRequestFactory extends AbstractRetrieveRequestFactory {

    private static final long serialVersionUID = 546577958047902917L;

    public V4RetrieveRequestFactory(final ODataV4Client client) {
        super(client);
    }

    @Override
    public ODataV4MetadataRequest getMetadataRequest(final String serviceRoot) {
        return new ODataV4MetadataRequest(client, client.getURIBuilder(serviceRoot).appendMetadataSegment().build());
    }

    @Override
    public ODataServiceDocumentRequest getServiceDocumentRequest(final String serviceRoot) {
        return new ODataServiceDocumentRequest(client,
                StringUtils.isNotBlank(serviceRoot) && serviceRoot.endsWith("/")
                ? client.getURIBuilder(serviceRoot).build()
                : client.getURIBuilder(serviceRoot + "/").build());
    }
}
