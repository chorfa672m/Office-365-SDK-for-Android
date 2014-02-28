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

import java.io.Serializable;
import java.net.URI;
import org.w3c.dom.Element;

public interface ODataBinder extends Serializable {

    /**
     * Gets a <tt>FeedResource</tt> from the given OData entity set.
     *
     * @param <T> feed resource type.
     * @param feed OData entity set.
     * @param reference reference class.
     * @return <tt>FeedResource</tt> object.
     */
    <T extends FeedResource> T getFeed(ODataEntitySet feed, Class<T> reference);

    /**
     * Gets an <tt>EntryResource</tt> from the given OData entity.
     *
     * @param <T> entry resource type.
     * @param entity OData entity.
     * @param reference reference class.
     * @return <tt>EntryResource</tt> object.
     */
    <T extends EntryResource> T getEntry(ODataEntity entity, Class<T> reference);

    /**
     * Gets an <tt>EntryResource</tt> from the given OData entity.
     *
     * @param <T> entry resource type.
     * @param entity OData entity.
     * @param reference reference class.
     * @param setType whether to explicitly output type information.
     * @return <tt>EntryResource</tt> object.
     */
    <T extends EntryResource> T getEntry(ODataEntity entity, Class<T> reference, boolean setType);

    /**
     * Gets the given OData property as DOM element.
     *
     * @param prop OData property.
     * @return <tt>Element</tt> object.
     */
    Element toDOMElement(ODataProperty prop);

    ODataLinkCollection getLinkCollection(LinkCollectionResource linkCollection);

    /**
     * Gets <tt>ODataServiceDocument</tt> from the given service document resource.
     *
     * @param resource service document resource.
     * @return <tt>ODataServiceDocument</tt> object.
     */
    ODataServiceDocument getODataServiceDocument(ServiceDocumentResource resource);

    /**
     * Gets <tt>ODataEntitySet</tt> from the given feed resource.
     *
     * @param resource feed resource.
     * @return <tt>ODataEntitySet</tt> object.
     */
    ODataEntitySet getODataEntitySet(FeedResource resource);

    /**
     * Gets <tt>ODataEntitySet</tt> from the given feed resource.
     *
     * @param resource feed resource.
     * @param defaultBaseURI default base URI.
     * @return <tt>ODataEntitySet</tt> object.
     */
    ODataEntitySet getODataEntitySet(FeedResource resource, URI defaultBaseURI);

    /**
     * Gets <tt>ODataEntity</tt> from the given entry resource.
     *
     * @param resource entry resource.
     * @return <tt>ODataEntity</tt> object.
     */
    ODataEntity getODataEntity(EntryResource resource);

    /**
     * Gets <tt>ODataEntity</tt> from the given entry resource.
     *
     * @param resource entry resource.
     * @param defaultBaseURI default base URI.
     * @return <tt>ODataEntity</tt> object.
     */
    ODataEntity getODataEntity(EntryResource resource, URI defaultBaseURI);

    /**
     * Gets a <tt>LinkResource</tt> from the given OData link.
     *
     * @param <T> link resource type.
     * @param link OData link.
     * @param reference reference class.
     * @return <tt>LinkResource</tt> object.
     */
    <T extends LinkResource> T getLinkResource(ODataLink link, Class<T> reference);

    /**
     * Gets an <tt>ODataProperty</tt> from the given DOM element.
     *
     * @param property content.
     * @return <tt>ODataProperty</tt> object.
     */
    ODataProperty getProperty(Element property);
}
