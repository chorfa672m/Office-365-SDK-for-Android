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

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.ComplexType;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.DataServices;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.Edmx;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.EntityType;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.Schema;
import java.net.URI;

public class ODataV3MetadataRequest extends ODataMetadataRequest<
        EdmV3Metadata, Edmx, DataServices, Schema, EntityContainer, EntityType, ComplexType, FunctionImport> {

    public ODataV3MetadataRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, uri);
    }

}
