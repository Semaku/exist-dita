package org.exist.xquery.modules.contentextraction;

import org.apache.commons.io.IOUtils;
import org.exist.memtree.DocumentBuilderReceiver;
import org.exist.xquery.XPathException;
import org.exist.xquery.value.Base64BinaryValueType;
import org.exist.xquery.value.BinaryValue;
import org.exist.xquery.value.BinaryValueFromBinaryString;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Ivan Lagunov
 */
public class ContentExtractionTest {

    @Test
    public void testMetadata() throws IOException, ContentExtractionException, SAXException, XPathException, TransformerException {
        BinaryValue value = loadBinaryValue();

        DocumentBuilderReceiver builder = new DocumentBuilderReceiver();
        ContentExtraction ce = new ContentExtraction();
        ce.extractMetadata(value, builder);
        Document document = builder.getDocument();

        metadataAssertions(document);
        printDocument(document, System.out);
    }

    @Test
    public void testContentAndMetadata() throws IOException, ContentExtractionException, SAXException, XPathException, TransformerException {
        BinaryValue value = loadBinaryValue();

        DocumentBuilderReceiver builder = new DocumentBuilderReceiver();
        ContentExtraction ce = new ContentExtraction();
        ce.extractContentAndMetadata(value, (ContentHandler) builder);
        Document document = builder.getDocument();

        metadataAssertions(document);
        int nPages = document.getElementsByTagName("body").item(0).getChildNodes().getLength();
        Assert.assertTrue(nPages > 0);
        printDocument(document, System.out);
    }

    private void metadataAssertions(Document document) {
        Assert.assertNotNull(document);
        Assert.assertEquals("html", document.getDocumentElement().getNodeName());
        final NamedNodeMap nPagesAttributes = document.getElementsByTagName("meta").item(7).getAttributes();
        Assert.assertEquals("xmpTPg:NPages", nPagesAttributes.getNamedItem("name").getNodeValue());
        Assert.assertEquals("3", nPagesAttributes.getNamedItem("content").getNodeValue());
    }

    private BinaryValue loadBinaryValue() throws IOException, XPathException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("pdf_base64.txt")) {
            assert is != null;
            String pdfBase64 = IOUtils.toString(is, StandardCharsets.UTF_8);
            return new BinaryValueFromBinaryString(new Base64BinaryValueType(), pdfBase64);
        }
    }

    private void printDocument(Document doc, OutputStream out) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, StandardCharsets.UTF_8)));
    }
}