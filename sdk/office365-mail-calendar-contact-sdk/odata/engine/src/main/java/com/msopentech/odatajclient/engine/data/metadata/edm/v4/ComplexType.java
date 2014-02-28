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

import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractComplexType;
import java.util.ArrayList;
import java.util.List;

public class ComplexType extends AbstractComplexType implements AnnotatedEdm {

    private static final long serialVersionUID = -1251230308269425962L;

    private boolean abstractEntityType = false;

    private String baseType;

    private boolean openType = false;

    private final List<Property> properties = new ArrayList<Property>();

    private final List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();

    private Annotation annotation;

    public boolean isAbstractEntityType() {
        return abstractEntityType;
    }

    public void setAbstractEntityType(final boolean abstractEntityType) {
        this.abstractEntityType = abstractEntityType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(final String baseType) {
        this.baseType = baseType;
    }

    public boolean isOpenType() {
        return openType;
    }

    public void setOpenType(final boolean openType) {
        this.openType = openType;
    }

    @Override
    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public Property getProperty(final String name) {
        Property result = null;
        for (Property property : getProperties()) {
            if (name.equals(property.getName())) {
                result = property;
            }
        }
        return result;
    }

    public List<NavigationProperty> getNavigationProperties() {
        return navigationProperties;
    }

    public NavigationProperty getNavigationProperty(final String name) {
        NavigationProperty result = null;
        for (NavigationProperty property : getNavigationProperties()) {
            if (name.equals(property.getName())) {
                result = property;
            }
        }
        return result;
    }

    @Override
    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }

}
