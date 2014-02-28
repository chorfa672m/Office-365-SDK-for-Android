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
package com.msopentech.odatajclient.engine.data.metadata.edm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = DataServicesDeserializer.class)
public abstract class AbstractDataServices<S extends AbstractSchema<EC, E, C, FI>, EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        extends AbstractEdm {

    private static final long serialVersionUID = -9126377222393876166L;

    private String dataServiceVersion;

    private String maxDataServiceVersion;

    public String getDataServiceVersion() {
        return dataServiceVersion;
    }

    public void setDataServiceVersion(final String dataServiceVersion) {
        this.dataServiceVersion = dataServiceVersion;
    }

    public String getMaxDataServiceVersion() {
        return maxDataServiceVersion;
    }

    public void setMaxDataServiceVersion(final String maxDataServiceVersion) {
        this.maxDataServiceVersion = maxDataServiceVersion;
    }

    public abstract List<S> getSchemas();
}
