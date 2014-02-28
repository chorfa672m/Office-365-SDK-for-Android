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
package com.msopentech.odatajclient.engine.communication.request;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.ODataServerErrorException;
import com.msopentech.odatajclient.engine.communication.header.ODataHeaderValues;
import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.batch.BatchRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.invoke.InvokeRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.streamed.StreamedRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataError;
import com.msopentech.odatajclient.engine.data.json.error.JSONODataError;
import com.msopentech.odatajclient.engine.data.xml.XMLODataError;
import com.msopentech.odatajclient.engine.format.ODataMediaFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.msopentech.org.apache.http.Header;
import com.msopentech.org.apache.http.HttpEntity;
import com.msopentech.org.apache.http.HttpResponse;
import com.msopentech.org.apache.http.client.HttpClient;
import com.msopentech.org.apache.http.client.methods.HttpUriRequest;
import com.msopentech.org.apache.http.impl.client.DecompressingHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of an OData request.
 * Get instance by using factories.
 *
 * @param <T> Accepted content-type formats by the request in object.
 *
 * @see CUDRequestFactory
 * @see BatchRequestFactory
 * @see InvokeRequestFactory
 * @see StreamedRequestFactory
 */
public class ODataRequestImpl<T extends Enum<T>> implements ODataRequest {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ODataRequest.class);

    protected final ODataClient odataClient;

    protected final Class<T> formatRef;

    /**
     * OData request method.
     */
    protected final HttpMethod method;

    /**
     * OData request header.
     */
    protected final ODataHeaders odataHeaders;

    /**
     * Target URI.
     */
    protected final URI uri;

    /**
     * HTTP client.
     */
    protected final HttpClient httpClient;

    /**
     * HTTP request.
     */
    protected final HttpUriRequest request;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param formatRef reference class for the format being used
     * @param method HTTP request method. If configured X-HTTP-METHOD header will be used.
     * @param uri OData request URI.
     */
    protected ODataRequestImpl(final ODataClient odataClient,
            final Class<T> formatRef, final HttpMethod method, final URI uri) {

        this.odataClient = odataClient;

        this.formatRef = formatRef;
        this.method = method;

        // initialize default headers
        this.odataHeaders = odataClient.getVersionHeaders();

        // target uri
        this.uri = uri;

        HttpClient _httpClient = odataClient.getConfiguration().getHttpClientFactory().
                createHttpClient(this.method, this.uri);
        if (odataClient.getConfiguration().isGzipCompression()) {
            _httpClient = new DecompressingHttpClient(_httpClient);
        }
        this.httpClient = _httpClient;

        this.request = odataClient.getConfiguration().getHttpUriRequestFactory().
                createHttpUriRequest(this.method, this.uri);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T getDefaultFormat() {
        return (T) (formatRef.equals(ODataPubFormat.class)
                ? odataClient.getConfiguration().getDefaultPubFormat()
                : (formatRef.equals(ODataValueFormat.class)
                ? odataClient.getConfiguration().getDefaultValueFormat()
                : (formatRef.equals(ODataMediaFormat.class)
                ? odataClient.getConfiguration().getDefaultMediaFormat()
                : odataClient.getConfiguration().getDefaultFormat())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaderNames() {
        return odataHeaders.getHeaderNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeader(final String name) {
        return odataHeaders.getHeader(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setAccept(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.accept, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setIfMatch(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.ifMatch, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setIfNoneMatch(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.ifNoneMatch, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setPrefer(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.prefer, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setXHTTPMethod(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.xHttpMethod, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setContentType(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.contentType, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest setSlug(final String value) {
        odataHeaders.setHeader(ODataHeaders.HeaderName.slug, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataRequest addCustomHeader(final String name, final String value) {
        odataHeaders.setHeader(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAccept() {
        final String acceptHead = odataHeaders.getHeader(ODataHeaders.HeaderName.accept);
        return StringUtils.isBlank(acceptHead) ? getDefaultFormat().toString() : acceptHead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIfMatch() {
        return odataHeaders.getHeader(ODataHeaders.HeaderName.ifMatch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIfNoneMatch() {
        return odataHeaders.getHeader(ODataHeaders.HeaderName.ifNoneMatch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefer() {
        return odataHeaders.getHeader(ODataHeaders.HeaderName.prefer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        final String contentTypeHead = odataHeaders.getHeader(ODataHeaders.HeaderName.contentType);
        return StringUtils.isBlank(contentTypeHead) ? getDefaultFormat().toString() : contentTypeHead;
    }

    /**
     * ${@inheritDoc }
     */
    @Override
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Gets request headers.
     *
     * @return request headers.
     */
    public ODataHeaders getHeader() {
        return odataHeaders;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public byte[] toByteArray() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append(getMethod().toString()).append(" ").
                    append(uri.toString()).append(" ").append("HTTP/1.1");

            baos.write(requestBuilder.toString().getBytes());

            baos.write(ODataStreamer.CRLF);

            // Set Content-Type and Accept headers with default values, if not yet set
            if (StringUtils.isBlank(odataHeaders.getHeader(ODataHeaders.HeaderName.contentType))) {
                setContentType(getContentType());
            }
            if (StringUtils.isBlank(odataHeaders.getHeader(ODataHeaders.HeaderName.accept))) {
                setAccept(getAccept());
            }

            for (String name : getHeaderNames()) {
                final String value = getHeader(name);

                if (StringUtils.isNotBlank(value)) {
                    baos.write((name + ": " + value).getBytes());
                    baos.write(ODataStreamer.CRLF);
                }
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream rawExecute() {
        try {
            final HttpEntity httpEntity = doExecute().getEntity();
            return httpEntity == null ? null : httpEntity.getContent();
        } catch (IOException e) {
            throw new HttpClientException(e);
        } catch (RuntimeException e) {
            this.request.abort();
            throw new HttpClientException(e);
        }
    }

    /**
     * Builds the request and execute it.
     *
     * @return HttpReponse object.
     */
    protected HttpResponse doExecute() {
        // Set Content-Type and Accept headers with default values, if not yet set
        if (StringUtils.isBlank(odataHeaders.getHeader(ODataHeaders.HeaderName.contentType))) {
            setContentType(getContentType());
        }
        if (StringUtils.isBlank(odataHeaders.getHeader(ODataHeaders.HeaderName.accept))) {
            setAccept(getAccept());
        }

        // Add header for KeyAsSegment management
        if (odataClient.getConfiguration().isKeyAsSegment()) {
            addCustomHeader(
                    ODataHeaders.HeaderName.dataServiceUrlConventions.toString(), ODataHeaderValues.keyAsSegment);
        }

        // Add all available headers
        for (String key : getHeaderNames()) {
            this.request.addHeader(key, odataHeaders.getHeader(key));
        }

        if (LOG.isDebugEnabled()) {
            for (Header header : this.request.getAllHeaders()) {
                LOG.debug("HTTP header being sent: " + header);
            }
        }

        final HttpResponse response;
        try {
            response = this.httpClient.execute(this.request);
        } catch (IOException e) {
            throw new HttpClientException(e);
        } catch (RuntimeException e) {
            this.request.abort();
            throw new HttpClientException(e);
        }

        if (response.getStatusLine().getStatusCode() >= 500) {
            String errorMessage = "";
            try {
                errorMessage = IOUtils.toString(response.getEntity().getContent());
            } catch (Exception e) {}
            
            throw new ODataServerErrorException(response.getStatusLine() + "\n" + errorMessage);
        } else if (response.getStatusLine().getStatusCode() >= 400) {
            try {
                final HttpEntity httpEntity = response.getEntity();
                if (httpEntity == null) {
                    throw new ODataClientErrorException(response.getStatusLine());
                } else {
                    final boolean isXML = getAccept().indexOf("json") == -1;
                    ODataError error;

                    try {
                        error = odataClient.getReader().readError(httpEntity.getContent(), isXML);
                    } catch (IllegalArgumentException e) {
                        LOG.warn("Error deserializing error response", e);
                        error = getGenericError(
                                response.getStatusLine().getStatusCode(),
                                response.getStatusLine().getReasonPhrase(),
                                isXML);
                    }

                    throw new ODataClientErrorException(response.getStatusLine(), error);
                }
            } catch (IOException e) {
                throw new HttpClientException(
                        "Received '" + response.getStatusLine() + "' but could not extract error body", e);
            }
        }

        return response;
    }

    /**
     * Gets an empty response that can be initialized by a stream.
     * <p>
     * This method has to be used to build response items about a batch request.
     *
     * @param <V> ODataResppnse type.
     * @return empty OData response instance.
     */
    @SuppressWarnings("unchecked")
    public <V extends ODataResponse> V getResponseTemplate() {

        for (Class<?> clazz : this.getClass().getDeclaredClasses()) {
            if (ODataResponse.class.isAssignableFrom(clazz)) {
                try {
                    final Constructor<?> constructor = clazz.getDeclaredConstructor(this.getClass());
                    constructor.setAccessible(true);
                    return (V) constructor.newInstance(this);
                } catch (Exception e) {
                    LOG.error("Error retrieving response class template instance", e);
                }
            }
        }

        throw new IllegalStateException("No response class template has been found");
    }

    private ODataError getGenericError(final int code, final String errorMsg, final boolean isXML) {
        final ODataError error;
        if (isXML) {
            error = new XMLODataError();
            final XMLODataError.Message msg = new XMLODataError.Message(
                    Collections.singletonMap("", (Object) errorMsg));

            ((XMLODataError) error).setMessage(msg);
            ((XMLODataError) error).setCode(String.valueOf(code));
        } else {
            error = new JSONODataError();
            final JSONODataError.Message msg = new JSONODataError.Message();
            msg.setValue(errorMsg);

            ((JSONODataError) error).setMessage(msg);
            ((JSONODataError) error).setCode(String.valueOf(code));
        }

        return error;
    }
}
