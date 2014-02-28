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
package com.msopentech.odatajclient.proxy.api.annotations;

import com.msopentech.odatajclient.engine.data.metadata.EdmContentKind;
import com.msopentech.odatajclient.engine.data.metadata.edm.ConcurrencyMode;
import com.msopentech.odatajclient.engine.data.metadata.edm.StoreGeneratedPattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind POJO field to EDM property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Property {

    String name();

    String type();

    boolean nullable() default true;

    String defaultValue() default "";

    int maxLenght() default Integer.MAX_VALUE;

    boolean fixedLenght() default false;

    int precision() default 0;

    int scale() default 0;

    boolean unicode() default true;

    String collation() default "";

    String srid() default "";

    ConcurrencyMode concurrencyMode() default ConcurrencyMode.None;

    String mimeType() default "";

    /* -- Feed Customization annotations -- */
    String fcSourcePath() default "";

    String fcTargetPath() default "";

    EdmContentKind fcContentKind() default EdmContentKind.text;

    String fcNSPrefix() default "";

    String fcNSURI() default "";

    boolean fcKeepInContent() default false;

    StoreGeneratedPattern storeGeneratedPattern() default StoreGeneratedPattern.None;
}
