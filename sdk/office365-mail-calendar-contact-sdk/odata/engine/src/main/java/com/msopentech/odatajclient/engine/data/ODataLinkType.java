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
package com.msopentech.odatajclient.engine.data;

import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import org.apache.commons.lang3.StringUtils;
import com.msopentech.org.apache.http.entity.ContentType;

/**
 * OData link types.
 */
public enum ODataLinkType {

    /**
     * Entity navigation link.
     */
    ENTITY_NAVIGATION(ODataPubFormat.ATOM + ";type=entry"),
    /**
     * Entity set navigation link.
     */
    ENTITY_SET_NAVIGATION(ODataPubFormat.ATOM + ";type=feed"),
    /**
     * Association link.
     */
    ASSOCIATION(ContentType.APPLICATION_XML.getMimeType()),
    /**
     * Media-edit link.
     */
    MEDIA_EDIT("*/*");

    private String type;

    private ODataLinkType(final String type) {
        this.type = type;
    }

    private ODataLinkType setType(final String type) {
        this.type = type;
        return this;
    }

    /**
     * Gets
     * <code>ODataLinkType</code> instance from the given rel and type.
     *
     * @param rel rel.
     * @param type type.
     * @return <code>ODataLinkType</code> object.
     */
    public static ODataLinkType fromString(final String rel, final String type) {
        if (StringUtils.isNotBlank(rel) && rel.startsWith(ODataConstants.MEDIA_EDIT_LINK_REL)) {
            return MEDIA_EDIT.setType(StringUtils.isBlank(type) ? "*/*" : type);
        }

        if (ODataLinkType.ENTITY_NAVIGATION.type.equals(type)) {
            return ENTITY_NAVIGATION;
        }

        if (ODataLinkType.ENTITY_SET_NAVIGATION.type.equals(type)) {
            return ENTITY_SET_NAVIGATION;
        }

        if (ODataLinkType.ASSOCIATION.type.equals(type)) {
            return ASSOCIATION;
        }

        throw new IllegalArgumentException("Invalid link type: " + type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return type;
    }
}
