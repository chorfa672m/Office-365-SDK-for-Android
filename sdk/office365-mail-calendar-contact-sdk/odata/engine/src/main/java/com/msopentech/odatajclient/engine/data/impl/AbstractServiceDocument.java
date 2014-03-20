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
package com.msopentech.odatajclient.engine.data.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.data.ServiceDocumentElement;
import com.msopentech.odatajclient.engine.data.ServiceDocumentResource;

public abstract class AbstractServiceDocument implements ServiceDocumentResource {

    private String title;

    @JsonProperty("value")
    private final List<ServiceDocumentElement> entitySets = new ArrayList<ServiceDocumentElement>();

    @Override
    public String getMetadataContext() {
        return null;
    }

    @Override
    public String getMetadataETag() {
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    protected ServiceDocumentElement getByName(final List<ServiceDocumentElement> elements, final String name) {
        ServiceDocumentElement result = null;
        for (ServiceDocumentElement element : elements) {
            if (name.equals(element.getName())) {
                result = element;
            }
        }
        return result;
    }

    protected ServiceDocumentElement getByTitle(final List<ServiceDocumentElement> elements, final String title) {
        ServiceDocumentElement result = null;
        for (ServiceDocumentElement element : elements) {
            if (title.equals(element.getTitle())) {
                result = element;
            }
        }
        return result;
    }

    @Override
    public List<ServiceDocumentElement> getEntitySets() {
        return entitySets;
    }

    @Override
    public ServiceDocumentElement getEntitySetByName(final String name) {
        return getByName(getEntitySets(), name);
    }

    @Override
    public ServiceDocumentElement getEntitySetByTitle(final String title) {
        return getByTitle(getEntitySets(), title);
    }

    @Override
    public List<ServiceDocumentElement> getFunctionImports() {
        return Collections.<ServiceDocumentElement>emptyList();
    }

    @Override
    public ServiceDocumentElement getFunctionImportByName(final String name) {
        return getByName(getFunctionImports(), name);
    }

    @Override
    public ServiceDocumentElement getFunctionImportByTitle(final String title) {
        return getByTitle(getFunctionImports(), title);
    }

    @Override
    public List<ServiceDocumentElement> getSingletons() {
        return Collections.<ServiceDocumentElement>emptyList();
    }

    @Override
    public ServiceDocumentElement getSingletonByName(final String name) {
        return getByName(getSingletons(), name);
    }

    @Override
    public ServiceDocumentElement getSingletonByTitle(final String title) {
        return getByTitle(getSingletons(), title);
    }

    @Override
    public List<ServiceDocumentElement> getRelatedServiceDocuments() {
        return Collections.<ServiceDocumentElement>emptyList();
    }

    @Override
    public ServiceDocumentElement getRelatedServiceDocumentByTitle(final String title) {
        return getByTitle(getRelatedServiceDocuments(), title);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
