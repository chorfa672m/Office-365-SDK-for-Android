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

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.AbstractODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchableRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataInvokeResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataInvokeResult;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.msopentech.org.apache.http.HttpResponse;
import com.msopentech.org.apache.http.client.HttpClient;
import com.msopentech.org.apache.http.client.methods.HttpPost;
import com.msopentech.org.apache.http.client.methods.HttpRequestBase;
import com.msopentech.org.apache.http.client.utils.URIBuilder;

/**
 * This class implements an OData invoke operation request.
 */
public class ODataInvokeRequest<T extends ODataInvokeResult>
        extends AbstractODataBasicRequestImpl<ODataInvokeResponse<T>, ODataPubFormat>
        implements ODataBatchableRequest {

    private final Class<T> reference;

    /**
     * Function parameters.
     */
    private Map<String, ODataValue> parameters;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param reference reference class for invoke result
     * @param method HTTP method of the request.
     * @param uri URI that identifies the operation.
     */
    ODataInvokeRequest(
            final ODataClient odataClient,
            final Class<T> reference,
            final HttpMethod method,
            final URI uri) {

        super(odataClient, ODataPubFormat.class, method, uri);

        this.reference = reference;
        this.parameters = new LinkedHashMap<String, ODataValue>();
    }

    /**
     * Sets operation parameters.
     *
     * @param parameters operation parameters.
     */
    public void setParameters(final Map<String, ODataValue> parameters) {
        this.parameters.clear();
        if (parameters != null && !parameters.isEmpty()) {
            this.parameters.putAll(parameters);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFormat(final ODataPubFormat format) {
        final String _format = (reference.isAssignableFrom(ODataProperty.class) && format == ODataPubFormat.ATOM)
                ? ODataFormat.XML.toString()
                : format.toString();
        setAccept(_format);
        setContentType(_format);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected InputStream getPayload() {
        if (!this.parameters.isEmpty() && this.method == HttpMethod.POST) {
            // Additional, non-binding parameters MUST be sent as JSON
            final ODataEntity tmp = ODataObjectFactory.newEntity("");
            for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
                ODataProperty property = null;

                if (param.getValue().isPrimitive()) {
                    property = ODataObjectFactory.newPrimitiveProperty(param.getKey(), param.getValue().asPrimitive());
                } else if (param.getValue().isComplex()) {
                    property = ODataObjectFactory.newComplexProperty(param.getKey(), param.getValue().asComplex());
                } else if (param.getValue().isCollection()) {
                    property = ODataObjectFactory.newCollectionProperty(param.getKey(), param.getValue().asCollection());
                }

                if (property != null) {
                    tmp.addProperty(property);
                }
            }

            return odataClient.getWriter().writeEntity(tmp, ODataPubFormat.JSON, false);
        }

        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataInvokeResponse<T> execute() {
        final InputStream input = getPayload();

        if (!this.parameters.isEmpty()) {
            if (this.method == HttpMethod.GET) {
                final URIBuilder uriBuilder = new URIBuilder(this.uri);
                for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
                    if (!param.getValue().isPrimitive()) {
                        throw new IllegalArgumentException("Only primitive values can be passed via GET");
                    }

                    uriBuilder.addParameter(param.getKey(), param.getValue().toString());
                }
                try {
                    ((HttpRequestBase) this.request).setURI(uriBuilder.build());
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("While adding GET parameters", e);
                }
            } else if (this.method == HttpMethod.POST) {
                ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

                setContentType(ODataPubFormat.JSON.toString());
            }
        }

        try {
            return new ODataInvokeResponseImpl(httpClient, doExecute());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * Response class about an ODataInvokeRequest.
     */
    protected class ODataInvokeResponseImpl extends ODataResponseImpl implements ODataInvokeResponse<T> {

        private T invokeResult = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataInvokeResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataInvokeResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        @SuppressWarnings("unchecked")
        public T getBody() {
            if (invokeResult == null) {
                if (reference.isAssignableFrom(ODataNoContent.class)) {
                    invokeResult = (T) new ODataNoContent();
                }

                try {
                    if (reference.isAssignableFrom(ODataEntitySet.class)) {
                        invokeResult = (T) odataClient.getReader().readEntitySet(res.getEntity().getContent(),
                                ODataPubFormat.fromString(getContentType()));
                    }
                    if (reference.isAssignableFrom(ODataEntity.class)) {
                        invokeResult = (T) odataClient.getReader().readEntity(res.getEntity().getContent(),
                                ODataPubFormat.fromString(getContentType()));
                    }
                    if (reference.isAssignableFrom(ODataProperty.class)) {
                        invokeResult = (T) odataClient.getReader().readProperty(res.getEntity().getContent(),
                                ODataFormat.fromString(getContentType()));
                    }
                } catch (IOException e) {
                    throw new HttpClientException(e);
                } finally {
                    this.close();
                }
            }
            return invokeResult;
        }
    }
}
