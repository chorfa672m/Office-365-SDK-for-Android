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
package com.msopentech.odatajclient.engine.data.metadata.edm.v4;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.communication.request.invoke.AbstractOperation;

public class ActionImport extends AbstractAnnotatedEdm implements AbstractOperation {

    private static final long serialVersionUID = -866422101558426421L;

    @JsonProperty(value = "Name", required = true)
    private String name;

    @JsonProperty(value = "Action", required = true)
    private String action;

    @JsonProperty(value = "EntitySet")
    private String entitySet;
    
    private boolean isSideEffecting = true;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(final String entitySet) {
        this.entitySet = entitySet;
    }

    @Override
    public String getReturnType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSideEffecting() {
        return isSideEffecting;
    }
    
    public void setIsSideEffecting(boolean isSideEffecting) {
        this.isSideEffecting = isSideEffecting;
    }

    @Override
    public String getURI() {
        throw new UnsupportedOperationException();
    }

}
