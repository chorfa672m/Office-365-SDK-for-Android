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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

/**
 * Abstract base for classes implementing <tt>EntryResource</tt>.
 */
public abstract class AbstractEntryResource<LINK extends LinkResource>
        extends AbstractPayloadObject implements EntryResource {

    private static final long serialVersionUID = 2127764552600969783L;

    private String eTag;

    private String type;

    private String id;

    private LINK readLink;

    private LINK editLink;

    private List<LINK> associationLinks;

    private List<LINK> navigationLinks;

    private List<LINK> mediaEditLinks;

    private List<ODataOperation> operations;

    private Element content;

    private Element mediaEntryProperties;

    private String mediaContentSource;

    private String mediaContentType;

    public AbstractEntryResource() {
        associationLinks = new ArrayList<LINK>();
        navigationLinks = new ArrayList<LINK>();
        mediaEditLinks = new ArrayList<LINK>();
        operations = new ArrayList<ODataOperation>();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getETag() {
        return eTag;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LinkResource getSelfLink() {
        return readLink;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setSelfLink(final LinkResource readLink) {
        this.readLink = (LINK) readLink;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LinkResource getEditLink() {
        return editLink;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setEditLink(final LinkResource editLink) {
        this.editLink = (LINK) editLink;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAssociationLink(final LinkResource link) {
        return associationLinks.add((LINK) link);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<LINK> getAssociationLinks() {
        return associationLinks;
    }

    @SuppressWarnings("unchecked")
    private void setLinks(final List<LINK> links, final List<LinkResource> linkResources) {
        links.clear();
        for (LinkResource link : linkResources) {
            links.add((LINK) link);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addNavigationLink(final LinkResource link) {
        return navigationLinks.add((LINK) link);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAssociationLinks(final List<LinkResource> associationLinks) {
        setLinks(this.associationLinks, associationLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<LINK> getNavigationLinks() {
        return navigationLinks;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addMediaEditLink(final LinkResource link) {
        return mediaEditLinks.add((LINK) link);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setNavigationLinks(final List<LinkResource> navigationLinks) {
        setLinks(this.navigationLinks, navigationLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<LINK> getMediaEditLinks() {
        return mediaEditLinks;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMediaEditLinks(final List<LinkResource> mediaEditLinks) {
        setLinks(this.mediaEditLinks, mediaEditLinks);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ODataOperation> getOperations() {
        return operations;
    }

    /**
     * Adds operation.
     *
     * @param operation operation.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean addOperation(final ODataOperation operation) {
        return this.operations.add(operation);
    }

    /**
     * Sets operations.
     *
     * @param operations operations.
     */
    public void setOperations(final List<ODataOperation> operations) {
        this.operations.clear();
        if (operations != null && !operations.isEmpty()) {
            this.operations.addAll(operations);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Element getContent() {
        return content;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setContent(final Element content) {
        this.content = content;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Element getMediaEntryProperties() {
        return mediaEntryProperties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMediaEntryProperties(final Element mediaEntryProperties) {
        this.mediaEntryProperties = mediaEntryProperties;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getMediaContentType() {
        return this.mediaContentType;
    }

    /**
     * Sets media content type.
     *
     * @param mediaContentType media content type.
     */
    @Override
    public void setMediaContentType(final String mediaContentType) {
        this.mediaContentType = mediaContentType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getMediaContentSource() {
        return this.mediaContentSource;
    }

    /**
     * Sets media content source.
     *
     * @param mediaContentSource media content source.
     */
    @Override
    public void setMediaContentSource(final String mediaContentSource) {
        this.mediaContentSource = mediaContentSource;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMediaEntry() {
        return getMediaEntryProperties() != null || StringUtils.isNotBlank(this.mediaContentSource);
    }
}
