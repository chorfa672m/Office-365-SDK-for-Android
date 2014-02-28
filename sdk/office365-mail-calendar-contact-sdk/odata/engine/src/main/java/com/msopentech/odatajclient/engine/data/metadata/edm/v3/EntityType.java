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

import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractEntityType;
import java.util.ArrayList;
import java.util.List;

public class EntityType extends AbstractEntityType {

    private static final long serialVersionUID = 8727765036150269547L;

    private final List<Property> properties = new ArrayList<Property>();

    private final List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();

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

    @Override
    public List<NavigationProperty> getNavigationProperties() {
        return navigationProperties;
    }

    @Override
    public NavigationProperty getNavigationProperty(final String name) {
        NavigationProperty result = null;
        for (NavigationProperty property : getNavigationProperties()) {
            if (name.equals(property.getName())) {
                result = property;
            }
        }
        return result;
    }

}
