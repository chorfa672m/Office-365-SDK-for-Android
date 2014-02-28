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

import com.msopentech.odatajclient.engine.utils.ODataConstants;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.net.URI;

/**
 * OData link.
 */
public class ODataLink extends ODataItem {

    private static final long serialVersionUID = 7274966414277952124L;

    /**
     * Link type.
     */
    protected final ODataLinkType type;

    /**
     * Link rel.
     */
    protected final String rel;

    /**
     * Constructor.
     *
     * @param uri URI.
     * @param type type.
     * @param title title.
     */
    ODataLink(final URI uri, final ODataLinkType type, final String title) {
        super(title);
        this.link = uri;

        this.type = type;

        switch (this.type) {
            case ASSOCIATION:
                this.rel = ODataConstants.ASSOCIATION_LINK_REL + title;
                break;

            case ENTITY_NAVIGATION:
            case ENTITY_SET_NAVIGATION:
                this.rel = ODataConstants.NAVIGATION_LINK_REL + title;
                break;

            case MEDIA_EDIT:
            default:
                this.rel = ODataConstants.MEDIA_EDIT_LINK_REL + title;
                break;
        }
    }

    /**
     * Constructor.
     *
     * @param baseURI base URI.
     * @param href href.
     * @param type type.
     * @param title title.
     */
    ODataLink(final URI baseURI, final String href, final ODataLinkType type, final String title) {
        this(URIUtils.getURI(baseURI, href), type, title);
    }

    /**
     * Gets link type.
     *
     * @return link type;
     */
    public ODataLinkType getType() {
        return type;
    }

    /**
     * Gets link rel.
     *
     * @return link rel
     */
    public String getRel() {
        return rel;
    }
}
