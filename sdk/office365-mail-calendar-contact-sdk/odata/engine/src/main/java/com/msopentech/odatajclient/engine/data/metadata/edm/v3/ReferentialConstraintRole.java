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
package com.msopentech.odatajclient.engine.data.metadata.edm.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEdm;
import com.msopentech.odatajclient.engine.data.metadata.edm.PropertyRef;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ReferentialConstraintRoleDeserializer.class)
public class ReferentialConstraintRole extends AbstractEdm {

    private static final long serialVersionUID = -3712887115248634164L;

    private String role;

    private List<PropertyRef> propertyRefs = new ArrayList<PropertyRef>();

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public List<PropertyRef> getPropertyRefs() {
        return propertyRefs;
    }

    public void setPropertyRefs(final List<PropertyRef> propertyRefs) {
        this.propertyRefs = propertyRefs;
    }
}
