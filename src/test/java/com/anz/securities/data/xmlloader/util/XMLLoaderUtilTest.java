package com.anz.securities.data.xmlloader.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.anz.securities.common.Constants;
import com.anz.securities.common.exception.UnsuccessfulDataLoading;

public class XMLLoaderUtilTest {

	@Mock
	Logger logger;
	
	private Document document;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetXMLDocument() throws Exception {
		document = XMLLoaderUtil.getXMLDocument(Constants.CONVERSION_RATE_RESOURCE_VALUE);
		Assert.assertNotNull(document);
	}

	@Test
	public void testGetXMLDocumentWithNullResource() {
		UnsuccessfulDataLoading extectedException = new UnsuccessfulDataLoading("NULL Resource");
		try {
			document = XMLLoaderUtil.getXMLDocument(null);	
		} catch( Exception ex) {
			Assert.assertEquals(ex.getClass(), extectedException.getClass());
		}
	}

	@Test
	public void testGetXMLDocumentWithNullStream() {
		UnsuccessfulDataLoading extectedException = new UnsuccessfulDataLoading("NULL Input stream while loading currencies");
		try {
			document = XMLLoaderUtil.getXMLDocument("unknownfilename");	
		} catch( Exception ex) {
			Assert.assertEquals(ex.getClass(), extectedException.getClass());
		}
	}
	
	@Test
	public void testGetXMLDocumentWithIncorrectXML() {
		UnsuccessfulDataLoading extectedException = new UnsuccessfulDataLoading("NULL Input stream while loading currencies");
		try {
			document = XMLLoaderUtil.getXMLDocument("ConversionRate_Incorrect.xml");	
		} catch( Exception ex) {
			Assert.assertEquals(ex.getClass(), extectedException.getClass());
		}
	}

}
