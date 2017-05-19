package com.anz.securities.data.xmlloader.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.anz.securities.common.exception.UnsuccessfulDataLoading;

public class XMLLoaderUtil {

	public static Document getXMLDocument(final String resource) throws UnsuccessfulDataLoading {
		try {
			if ( null == resource ) {
				throw new UnsuccessfulDataLoading("NULL Resource");
			}
			
			InputStream input = XMLLoaderUtil.class.getClassLoader().getResourceAsStream(resource);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			if (null == input ) {
				throw new UnsuccessfulDataLoading("NULL Input stream while loading currencies");
			}
			Document doc = dBuilder.parse(input);

			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException exParse) {
			throw new UnsuccessfulDataLoading("XMLParse exception" + exParse.getMessage());
		} catch (IOException exIO) {
			throw new UnsuccessfulDataLoading("IO exception" + exIO.getMessage());
		} catch (Exception ex) {
			throw new UnsuccessfulDataLoading("Generic Exception  Loading Currencies" + ex.getMessage());
		}
	}
}
