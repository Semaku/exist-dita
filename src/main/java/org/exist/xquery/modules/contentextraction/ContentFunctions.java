/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.xquery.modules.contentextraction;

import org.exist.dom.QName;
import org.exist.memtree.DocumentBuilderReceiver;
import org.exist.xquery.*;
import org.exist.xquery.value.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Dulip Withanage <dulip.withanage@gmail.com>
 * @version 1.0
 */
public class ContentFunctions extends BasicFunction {

    public final static FunctionSignature getMetadata = new FunctionSignature(
        new QName("get-metadata", ContentExtractionModule.NAMESPACE_URI, ContentExtractionModule.PREFIX),
        "extracts the metadata",
        new SequenceType[]{
            new FunctionParameterSequenceType("binary", Type.BASE64_BINARY, Cardinality.ONE, "The binary data to extract from")
        },
        new FunctionReturnSequenceType(Type.DOCUMENT, Cardinality.ONE, "Extracted metadata")
    );

    public final static FunctionSignature getMetadataAndContent = new FunctionSignature(
        new QName("get-metadata-and-content", ContentExtractionModule.NAMESPACE_URI, ContentExtractionModule.PREFIX),
        "extracts the metadata and contents",
        new SequenceType[]{
            new FunctionParameterSequenceType("binary", Type.BASE64_BINARY, Cardinality.ONE, "The binary data to extract from")
        },
        new FunctionReturnSequenceType(Type.DOCUMENT, Cardinality.ONE, "Extracted content and metadata")
    );

    public ContentFunctions(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        // is argument the empty sequence?
        if (args[0].isEmpty()) {
            return Sequence.EMPTY_SEQUENCE;
        }

        DocumentBuilderReceiver builder = new DocumentBuilderReceiver();
        ContentExtraction ce = new ContentExtraction();

        try {
            if (isCalledAs("get-metadata")) {
                ce.extractMetadata((BinaryValue) args[0].itemAt(0), (ContentHandler) builder);
            } else {
                ce.extractContentAndMetadata((BinaryValue) args[0].itemAt(0), (ContentHandler) builder);
            }

            return (NodeValue) builder.getDocument();
        } catch (IOException | SAXException | ContentExtractionException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new XPathException(this, ex.getMessage(), ex);
        }
    }
}