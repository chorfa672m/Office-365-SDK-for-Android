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
package com.msopentech.odatajclient.engine.communication.request.invoke;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInvokeResult;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.data.metadata.EdmV3Type;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.ComplexType;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.DataServices;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.Edmx;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.EntityType;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.data.metadata.edm.v3.Schema;

public class V3InvokeRequestFactory extends AbstractInvokeRequestFactory<
        EdmV3Metadata, Edmx, DataServices, Schema, EntityContainer, EntityType, ComplexType, FunctionImport, AbstractOperation> {

    private static final long serialVersionUID = -659256862901915496L;

    public V3InvokeRequestFactory(final ODataV3Client client) {
        super(client);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
            final URI uri, final EdmV3Metadata metadata, final AbstractOperation operation) {

        HttpMethod method = operation.isSideEffecting() ? HttpMethod.POST : HttpMethod.GET;

        ODataInvokeRequest<RES> result;
        if (StringUtils.isBlank(operation.getReturnType())) {
            result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataNoContent>(
                    client, ODataNoContent.class, method, uri);
        } else {
            final EdmV3Type returnType = new EdmV3Type(metadata, operation.getReturnType());

            if (returnType.isCollection() && returnType.isEntityType()) {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataEntitySet>(
                        client, ODataEntitySet.class, method, uri);
            } else if (!returnType.isCollection() && returnType.isEntityType()) {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataEntity>(
                        client, ODataEntity.class, method, uri);
            } else {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataProperty>(
                        client, ODataProperty.class, method, uri);
            }
        }

        return result;
    }
}
