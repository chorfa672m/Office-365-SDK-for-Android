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
package com.msopentech.odatajclient.engine.utils;

import java.io.InputStream;
import java.io.Writer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOM Parser.
 */
public abstract class AbstractDOMParser {

    /**
     * Parses the given input into a DOM tree.
     *
     * @param input stream to be parsed and de-serialized.
     * @return DOM tree
     */
    public abstract Element parse(InputStream input);

    /**
     * Writes DOM object by the given writer.
     *
     * @param content DOM to be streamed.
     * @param writer writer.
     */
    public abstract void serialize(Node content, Writer writer);
}
