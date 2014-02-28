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
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractFunctionImport;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = FunctionImportDeserializer.class)
public class FunctionImport extends AbstractFunctionImport {

    private static final long serialVersionUID = -6214472528425935461L;

    private String name;

    private String returnType;

    private String entitySet;

    private String entitySetPath;

    private boolean composable;

    private boolean sideEffecting = true;

    private boolean bindable;

    private boolean alwaysBindable;

    private String httpMethod;

    private final List<Parameter> parameters = new ArrayList<Parameter>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(final String returnType) {
        this.returnType = returnType;
    }

    @Override
    public String getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(final String entitySet) {
        this.entitySet = entitySet;
    }

    public String getEntitySetPath() {
        return entitySetPath;
    }

    public void setEntitySetPath(final String entitySetPath) {
        this.entitySetPath = entitySetPath;
    }

    public boolean isComposable() {
        return composable;
    }

    public void setComposable(final boolean composable) {
        this.composable = composable;
    }

    public boolean isSideEffecting() {
        return sideEffecting;
    }

    public void setSideEffecting(final boolean sideEffecting) {
        this.sideEffecting = sideEffecting;
    }

    public boolean isBindable() {
        return bindable;
    }

    public void setBindable(final boolean bindable) {
        this.bindable = bindable;
    }

    public boolean isAlwaysBindable() {
        return alwaysBindable;
    }

    public void setAlwaysBindable(final boolean alwaysBindable) {
        this.alwaysBindable = alwaysBindable;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(final String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String getURI() {
        throw new UnsupportedOperationException();
    }

}
