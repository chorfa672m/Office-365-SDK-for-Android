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
import com.msopentech.odatajclient.engine.data.metadata.edm.AbstractAnnotations;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = AnnotationsDeserializer.class)
public class Annotations extends AbstractAnnotations {

    private static final long serialVersionUID = 3877353656301805410L;

    private final List<TypeAnnotation> typeAnnotations = new ArrayList<TypeAnnotation>();

    private final List<ValueAnnotation> valueAnnotations = new ArrayList<ValueAnnotation>();

    public List<TypeAnnotation> getTypeAnnotations() {
        return typeAnnotations;
    }

    public List<ValueAnnotation> getValueAnnotations() {
        return valueAnnotations;
    }

}
