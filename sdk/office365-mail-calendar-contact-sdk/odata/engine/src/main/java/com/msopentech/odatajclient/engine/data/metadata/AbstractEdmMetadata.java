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
package com.msopentech.odatajclient.engine.data.metadata;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractComplexType;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractDataServices;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractSchema;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEdmx;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEntityType;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractFunctionImport;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Entry point for access information about EDM metadata.
 */
public abstract class AbstractEdmMetadata<
        EDMX extends AbstractEdmx<DS, S, EC, E, C, FI>, DS extends AbstractDataServices<
        S, EC, E, C, FI>, S extends AbstractSchema<EC, E, C, FI>, EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        implements Serializable {

    private static final long serialVersionUID = -1214173426671503187L;

    protected final EDMX edmx;

    protected final Map<String, S> schemaByNsOrAlias;

    /**
     * Constructor.
     *
     * @param client OData client
     * @param inputStream source stream.
     */
    @SuppressWarnings("unchecked")
    public AbstractEdmMetadata(final ODataClient client, final InputStream inputStream) {
        edmx = (EDMX) client.getDeserializer().toMetadata(inputStream);

        this.schemaByNsOrAlias = new HashMap<String, S>();
        for (S schema : edmx.getDataServices().getSchemas()) {
            this.schemaByNsOrAlias.put(schema.getNamespace(), schema);
            if (StringUtils.isNotBlank(schema.getAlias())) {
                this.schemaByNsOrAlias.put(schema.getAlias(), schema);
            }
        }
    }

    /**
     * Checks whether the given key is a valid namespace or alias in the EdM metadata document.
     *
     * @param key namespace or alias
     * @return true if key is valid namespace or alias
     */
    public boolean isNsOrAlias(final String key) {
        return this.schemaByNsOrAlias.keySet().contains(key);
    }

    /**
     * Returns the Schema at the specified position in the EdM metadata document.
     *
     * @param index index of the Schema to return
     * @return the Schema at the specified position in the EdM metadata document
     */
    public S getSchema(final int index) {
        return this.edmx.getDataServices().getSchemas().get(index);
    }

    /**
     * Returns the Schema with the specified key (namespace or alias) in the EdM metadata document.
     *
     * @param key namespace or alias
     * @return the Schema with the specified key in the EdM metadata document
     */
    public S getSchema(final String key) {
        return this.schemaByNsOrAlias.get(key);
    }

    /**
     * Returns all Schema objects defined in the EdM metadata document.
     *
     * @return all Schema objects defined in the EdM metadata document
     */
    public List<S> getSchemas() {
        return this.edmx.getDataServices().getSchemas();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
