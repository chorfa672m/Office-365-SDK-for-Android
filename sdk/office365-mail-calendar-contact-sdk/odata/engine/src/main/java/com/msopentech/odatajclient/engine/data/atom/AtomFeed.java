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
package com.msopentech.odatajclient.engine.data.atom;

import com.msopentech.odatajclient.engine.data.AbstractPayloadObject;
import com.msopentech.odatajclient.engine.data.EntryResource;
import com.msopentech.odatajclient.engine.data.FeedResource;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * List of entries, represented via Atom.
 *
 * @see AtomEntry
 */
public class AtomFeed extends AbstractPayloadObject implements AtomObject, FeedResource {

    private static final long serialVersionUID = 5466590540021319153L;

    private URI baseURI;

    private String id;

    private String title;

    private String summary;

    private Date updated;

    private Integer count;

    private final List<AtomEntry> entries;

    private URI next;

    /**
     * Constructor.
     */
    public AtomFeed() {
        super();
        entries = new ArrayList<AtomEntry>();
    }

    @Override
    public URI getBaseURI() {
        return baseURI;
    }

    @Override
    public void setBaseURI(final String baseURI) {
        this.baseURI = URI.create(baseURI);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void setSummary(final String summary) {
        this.summary = summary;
    }

    @Override
    public Date getUpdated() {
        return new Date(updated.getTime());
    }

    @Override
    public void setUpdated(final Date updated) {
        this.updated = new Date(updated.getTime());
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getCount() {
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<AtomEntry> getEntries() {
        return entries;
    }

    /**
     * Add entry.
     *
     * @param entry entry.
     * @return 'TRUE' in case of success; 'FALSE' otherwise.
     */
    public boolean addEntry(final AtomEntry entry) {
        return this.entries.add(entry);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setEntries(final List<EntryResource> entries) {
        this.entries.clear();
        for (EntryResource entry : entries) {
            this.entries.add((AtomEntry) entry);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setNext(final URI next) {
        this.next = next;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URI getNext() {
        return next;
    }
}
