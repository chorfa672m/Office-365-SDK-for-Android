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
package com.msopentech.odatajclient.engine.data;

import com.msopentech.odatajclient.engine.data.metadata.AbstractEdmMetadata;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataObjectWrapper {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ODataObjectWrapper.class);

    private final ODataReader reader;

    private final byte[] obj;

    private final String format;

    /**
     * Constructor.
     *
     * @param is source input stream.
     * @param format source format (<tt>ODataPubFormat</tt>, <tt>ODataFormat</tt>, <tt>ODataValueFormat</tt>,
     * <tt>ODataServiceDocumentFormat</tt>).
     */
    public ODataObjectWrapper(final ODataReader reader, final InputStream is, final String format) {
        this.reader = reader;
        try {
            this.obj = IOUtils.toByteArray(is);
            this.format = format;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses stream as <tt>ODataEntitySetIterator</tt>.
     *
     * I
     *
     * @return <tt>ODataEntitySetIterator</tt> if success; null otherwise.
     */
    public ODataEntitySetIterator getODataEntitySetIterator() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataEntitySetIterator.class);
    }

    /**
     * Parses stream as <tt>ODataEntitySet</tt>.
     *
     * @return <tt>ODataEntitySet</tt> if success; null otherwise.
     */
    public ODataEntitySet getODataEntitySet() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataEntitySet.class);
    }

    /**
     * Parses stream as <tt>ODataEntity</tt>.
     *
     * @return <tt>ODataEntity</tt> if success; null otherwise.
     */
    public ODataEntity getODataEntity() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataEntity.class);
    }

    /**
     * Parses stream as <tt>ODataProperty</tt>.
     *
     * @return <tt>ODataProperty</tt> if success; null otherwise.
     */
    public ODataProperty getODataProperty() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataProperty.class);
    }

    /**
     * Parses stream as <tt>ODataLinkCollection</tt>.
     *
     * @return <tt>ODataLinkCollection</tt> if success; null otherwise.
     */
    public ODataLinkCollection getODataLinkCollection() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataLinkCollection.class);
    }

    /**
     * Parses stream as <tt>ODataValue</tt>.
     *
     * @return <tt>ODataValue</tt> if success; null otherwise.
     */
    public ODataValue getODataValue() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataValue.class);
    }

    /**
     * Parses stream as <tt>EdmMetadata</tt>.
     *
     * @return <tt>EdmMetadata</tt> if success; null otherwise.
     */
    public AbstractEdmMetadata getEdmMetadata() {
        return reader.read(new ByteArrayInputStream(obj), null, AbstractEdmMetadata.class);
    }

    /**
     * Parses stream as <tt>ODataServiceDocument</tt>.
     *
     * @return <tt>ODataServiceDocument</tt> if success; null otherwise.
     */
    public ODataServiceDocument getODataServiceDocument() {
        return reader.read(new ByteArrayInputStream(obj), format, ODataServiceDocument.class);
    }

    /**
     * Parses stream as <tt>ODataError</tt>.
     *
     * @return <tt>ODataError</tt> if success; null otherwise.
     */
    public ODataError getODataError() {
        return reader.read(new ByteArrayInputStream(obj), null, ODataError.class);
    }
}
