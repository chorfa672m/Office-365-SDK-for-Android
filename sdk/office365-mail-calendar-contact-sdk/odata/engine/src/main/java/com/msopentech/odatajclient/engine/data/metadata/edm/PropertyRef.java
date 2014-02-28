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

import com.fasterxml.jackson.annotation.JsonProperty;

public class PropertyRef extends AbstractEdm {

    private static final long serialVersionUID = 6738212067449628983L;

    @JsonProperty(value = "Name", required = true)
    private String name;

    @JsonProperty(value = "Alias")
    private String alias;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

}
