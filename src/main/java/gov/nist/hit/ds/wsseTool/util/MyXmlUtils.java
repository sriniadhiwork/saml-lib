package gov.nist.hit.ds.wsseTool.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Various helper methods for handling DOM documents.
 * 
 * 
 * @author gerardin
 * 
 */

public class MyXmlUtils {

	private static Transformer transformer;
	private static DocumentBuilderFactory dbf;

	static {

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
	}

	static {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	}

	/**
	 * Allow a jar-proof access to the resource. Replace getDocument(String
	 * filepath)
	 */
	public static Document getDocumentWithResourcePath(String resourcePath)
			throws SAXException, IOException, ParserConfigurationException {
		InputStream is = null;
		is = MyXmlUtils.class.getClassLoader()
				.getResourceAsStream(resourcePath);
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(is));
		is.close();
		return doc;
	}

	public static Document getDocumentAsResource(String resourcePath)
			throws SAXException, IOException, ParserConfigurationException {
		URL url = MyXmlUtils.class.getClassLoader().getResource(resourcePath);
		InputStream is = url.openStream();
		Document doc = dbf.newDocumentBuilder().parse(new InputSource(is));
		is.close();
		return doc;
	}

	/**
	 * @deprecated This method can only be used for tests within this project
	 */
	public static Document getDocument(String filepath) throws SAXException,
			IOException, ParserConfigurationException {
		Document doc = dbf.newDocumentBuilder().parse(
				new InputSource(new FileReader(filepath)));
		return doc;
	}
	
	/**
	 * Return the string representation of the @xml node.
	 */
	public static String DomToString(Node xml){
		/*
		 * TODO We are inefficient.
		 * - First, because of the XMLSlurper API, we convert the DOM representation to a stringWriter.
		 * Hard to improve without overloading XmlSlurper.parse().
		 * - Second we dump the stringwriter to a string before putting it in a reader.
		 * That's inefficient and other techniques are available.
		 * (See : http://ostermiller.org/convert_java_writer_reader.html)
		 */
		StringWriter s = new StringWriter();
		MyXmlUtils.DomToStream(xml, s);
		return s.toString();
	}

	public static void DomToStream(Node xml, OutputStream out)
			throws RuntimeException {
		try {
			transformer.transform(new DOMSource(xml), new StreamResult(
					new OutputStreamWriter(out, "UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void DomToStream(Node xml, Writer out)
			throws RuntimeException {
		try {
			transformer.transform(new DOMSource(xml), new StreamResult(out));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void DomToFile(Node xml, String filename)
			throws RuntimeException {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(xml), new StreamResult(
					new FileOutputStream(filename)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void printFileAsIs(String filename) throws IOException {
		File f = new File(filename);
		FileReader reader = new FileReader(f);

		Integer c;
		while ((c = reader.read()) != -1) {
			System.out.printf("%c", c);
		}

		reader.close();
	}

}
