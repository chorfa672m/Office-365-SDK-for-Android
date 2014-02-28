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
package com.msopentech.odatajclient.engine.data.atom;

import com.msopentech.odatajclient.engine.data.AbstractPayloadObject;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import com.msopentech.org.apache.http.entity.ContentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class AtomSerializer {

    private AtomSerializer() {
        // Empty private constructor for static utility classes
    }

    public static <T extends AbstractPayloadObject> Element serialize(final T obj) throws ParserConfigurationException {
        if (obj instanceof AtomEntry) {
            return entry((AtomEntry) obj);
        } else if (obj instanceof AtomFeed) {
            return feed((AtomFeed) obj);
        } else {
            throw new IllegalArgumentException("Unsupported Atom object for standalone serialization: " + obj);
        }
    }

    private static void setLinks(final Element entry, final List<AtomLink> links) throws ParserConfigurationException {
        for (AtomLink link : links) {
            final Element linkElem = entry.getOwnerDocument().createElement(ODataConstants.ATOM_ELEM_LINK);

            linkElem.setAttribute(ODataConstants.ATTR_REL, link.getRel());
            linkElem.setAttribute(ODataConstants.ATTR_TITLE, link.getTitle());
            linkElem.setAttribute(ODataConstants.ATTR_HREF, link.getHref());

            if (StringUtils.isNotBlank(link.getType())) {
                linkElem.setAttribute(ODataConstants.ATTR_TYPE, link.getType());
            }

            if (link.getInlineEntry() != null || link.getInlineFeed() != null) {
                final Element inline = entry.getOwnerDocument().createElement(ODataConstants.ATOM_ELEM_INLINE);
                linkElem.appendChild(inline);

                if (link.getInlineEntry() != null) {
                    inline.appendChild(entry.getOwnerDocument().importNode(
                            entry((AtomEntry) link.getInlineEntry()), true));
                }
                if (link.getInlineFeed() != null) {
                    inline.appendChild(entry.getOwnerDocument().importNode(
                            feed((AtomFeed) link.getInlineFeed()), true));
                }
            }

            entry.appendChild(linkElem);
        }
    }

    private static Element entry(final AtomEntry entry) throws ParserConfigurationException {
        final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element entryElem = doc.createElement(ODataConstants.ATOM_ELEM_ENTRY);
        entryElem.setAttribute(XMLConstants.XMLNS_ATTRIBUTE, ODataConstants.NS_ATOM);
        entryElem.setAttribute(ODataConstants.XMLNS_METADATA, ODataConstants.NS_METADATA);
        entryElem.setAttribute(ODataConstants.XMLNS_DATASERVICES, ODataConstants.NS_DATASERVICES);
        entryElem.setAttribute(ODataConstants.XMLNS_GML, ODataConstants.NS_GML);
        entryElem.setAttribute(ODataConstants.XMLNS_GEORSS, ODataConstants.NS_GEORSS);
        if (entry.getBaseURI() != null) {
            entryElem.setAttribute(ODataConstants.ATTR_XMLBASE, entry.getBaseURI().toASCIIString());
        }
        doc.appendChild(entryElem);

        final Element category = doc.createElement(ODataConstants.ATOM_ELEM_CATEGORY);
        category.setAttribute(ODataConstants.ATOM_ATTR_TERM, entry.getType());
        category.setAttribute(ODataConstants.ATOM_ATTR_SCHEME, ODataConstants.ATOM_CATEGORY_SCHEME);
        entryElem.appendChild(category);

        if (StringUtils.isNotBlank(entry.getTitle())) {
            final Element title = doc.createElement(ODataConstants.ATOM_ELEM_TITLE);
            title.appendChild(doc.createTextNode(entry.getTitle()));
            entryElem.appendChild(title);
        }

        if (StringUtils.isNotBlank(entry.getSummary())) {
            final Element summary = doc.createElement(ODataConstants.ATOM_ELEM_SUMMARY);
            summary.appendChild(doc.createTextNode(entry.getSummary()));
            entryElem.appendChild(summary);
        }

        setLinks(entryElem, entry.getAssociationLinks());
        setLinks(entryElem, entry.getNavigationLinks());
        setLinks(entryElem, entry.getMediaEditLinks());

        final Element content = doc.createElement(ODataConstants.ATOM_ELEM_CONTENT);
        if (entry.isMediaEntry()) {
            if (StringUtils.isNotBlank(entry.getMediaContentType())) {
                content.setAttribute(ODataConstants.ATTR_TYPE, entry.getMediaContentType());
            }
            if (StringUtils.isNotBlank(entry.getMediaContentSource())) {
                content.setAttribute(ODataConstants.ATOM_ATTR_SRC, entry.getMediaContentSource());
            }
            if (content.getAttributes().getLength() > 0) {
                entryElem.appendChild(content);
            }

            if (entry.getMediaEntryProperties() != null) {
                entryElem.appendChild(doc.importNode(entry.getMediaEntryProperties(), true));
            }
        } else {
            content.setAttribute(ODataConstants.ATTR_TYPE, ContentType.APPLICATION_XML.getMimeType());
            if (entry.getContent() != null) {
                content.appendChild(doc.importNode(entry.getContent(), true));
            }
            entryElem.appendChild(content);
        }

        return entryElem;
    }

    private static Element feed(final AtomFeed feed) throws ParserConfigurationException {
        final DocumentBuilder builder = ODataConstants.DOC_BUILDER_FACTORY.newDocumentBuilder();
        final Document doc = builder.newDocument();

        final Element feedElem = doc.createElement(ODataConstants.ATOM_ELEM_FEED);
        feedElem.setAttribute(XMLConstants.XMLNS_ATTRIBUTE, ODataConstants.NS_ATOM);
        feedElem.setAttribute(ODataConstants.XMLNS_METADATA, ODataConstants.NS_METADATA);
        feedElem.setAttribute(ODataConstants.XMLNS_DATASERVICES, ODataConstants.NS_DATASERVICES);
        feedElem.setAttribute(ODataConstants.XMLNS_GML, ODataConstants.NS_GML);
        feedElem.setAttribute(ODataConstants.XMLNS_GEORSS, ODataConstants.NS_GEORSS);
        if (feed.getBaseURI() != null) {
            feedElem.setAttribute(ODataConstants.ATTR_XMLBASE, feed.getBaseURI().toASCIIString());
        }
        doc.appendChild(feedElem);

        if (StringUtils.isNotBlank(feed.getTitle())) {
            final Element title = doc.createElement(ODataConstants.ATOM_ELEM_TITLE);
            title.appendChild(doc.createTextNode(feed.getTitle()));
            feedElem.appendChild(title);
        }

        if (StringUtils.isNotBlank(feed.getSummary())) {
            final Element summary = doc.createElement(ODataConstants.ATOM_ELEM_SUMMARY);
            summary.appendChild(doc.createTextNode(feed.getSummary()));
            feedElem.appendChild(summary);
        }

        for (AtomEntry entry : feed.getEntries()) {
            feedElem.appendChild(doc.importNode(entry(entry), true));
        }

        return feedElem;
    }
}
