package com.anz.securities.data.xmlloader.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.anz.securities.common.exception.UnsuccessfulDataLoading;

/**
 * Utility class for XML Loader
 * 
 * @author Anand Katti
 *
 */
public class XMLLoaderUtil {

	private XMLLoaderUtil() {
	}

	/**
	 * Returns the DOM Document object for the reosurce
	 * 
	 * @param resource
	 * @return document
	 * @throws UnsuccessfulDataLoading
	 */
	public static Document getXMLDocument(final String resource) throws UnsuccessfulDataLoading {
		try {
			if (null == resource) {
				throw new UnsuccessfulDataLoading("NULL Resource");
			}

			InputStream input = XMLLoaderUtil.class.getClassLoader().getResourceAsStream(resource);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			if (null == input) {
				throw new UnsuccessfulDataLoading("NULL Input stream while loading currencies");
			}
			Document doc = dBuilder.parse(input);

			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException exParse) {
			throw new UnsuccessfulDataLoading("XMLParse exception", exParse);
		} catch (IOException exIO) {
			throw new UnsuccessfulDataLoading("IO exception", exIO);
		} catch (Exception ex) {
			throw new UnsuccessfulDataLoading(ex.getMessage(), ex);
		}
	}
}
