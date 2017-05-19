package com.anz.securities.data.xmlloader.impl;

import static org.mockito.Mockito.doThrow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.w3c.dom.Document;

import com.anz.securities.common.Constants;
import com.anz.securities.common.exception.UnsuccessfulDataLoading;
import com.anz.securities.data.xmlloader.util.XMLLoaderUtil;
import com.anz.securities.dataloader.spi.LoaderConfig;
import com.anz.securities.entities.api.Currency;
import com.anz.securities.entities.dto.CurrencyConverter;

public class XMLDataLoaderTest {

	@InjectMocks
	XMLDataLoader objectUnderTest;

	@Mock
	Logger logger;

	@Mock
	Document doc1, doc2;

	@Mock
	XMLLoaderUtil xmlUtil;

	private LoaderConfig config;

	@Before
	public void setUp() {
		objectUnderTest = new XMLDataLoader();
		config = new LoaderConfig();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testLoadData() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, Constants.CONVERSION_RATE_RESOURCE_VALUE);
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, Constants.CONVERSION_RULE_RESOURCE_VALUE);
		CurrencyConverter converter = objectUnderTest.loadData(config);
		Assert.assertNotNull(converter);
	}

	@Test
	public void testLoadDataFailureNullInputStream() {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, Constants.CONVERSION_RATE_RESOURCE_VALUE);
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, Constants.CONVERSION_RULE_RESOURCE_VALUE);
		UnsuccessfulDataLoading expectedException = new UnsuccessfulDataLoading("NULL Input stream while loading currencies");
		try {
			doThrow(expectedException).when(XMLLoaderUtil.getXMLDocument("fileName"));
			objectUnderTest.loadData(config);
		} catch (final Exception ex) {
			Assert.assertEquals(ex.getClass(), expectedException.getClass());
		}
	}

	@Test
	public void testLoadDataFailureIncorrectXML() {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, "ConversionRate_Incorrect.xml");
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, Constants.CONVERSION_RULE_RESOURCE_VALUE);
		UnsuccessfulDataLoading expectedException = new UnsuccessfulDataLoading("XMLParse exception");
		try {
			objectUnderTest.loadData(config);
		} catch (final Exception ex) {
			Assert.assertEquals(ex.getClass(), expectedException.getClass());
		}

	}

	@Test
	public void testLoadDataFailureConversionRateNoContent() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, "ConversionRate_NoContent.xml");
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, Constants.CONVERSION_RULE_RESOURCE_VALUE);
		CurrencyConverter converter = objectUnderTest.loadData(config);
		Assert.assertEquals(converter.getListRates().size(), 0);

	}

	@Test
	public void testLoadDataFailureConversionRuleNoContent() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, Constants.CONVERSION_RATE_RESOURCE_VALUE);
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, "ConversionRulesNoContent.xml");
		CurrencyConverter converter = objectUnderTest.loadData(config);
		Assert.assertEquals(converter.getMapCurrecy().isEmpty(), true);

	}

	@Test
	public void testLoadDataFailureConversionRuleWithNoDestinationCurrencies() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, Constants.CONVERSION_RATE_RESOURCE_VALUE);
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, "ConversionRulesWithNoDestinationCurrencies.xml");
		CurrencyConverter converter = objectUnderTest.loadData(config);
		Assert.assertEquals(converter.getMapCurrecy().isEmpty(), false);
		for (Currency cur : converter.getMapCurrecy().values()) {
			Assert.assertEquals(cur.getRules().isEmpty(), true);
		}
	}

	@Test
	public void testLoadDataFailureConversionRuleWithNoData() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, "ConversionRate_NoContent.xml");
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, "ConversionRulesNoContent.xml");

		CurrencyConverter converter = objectUnderTest.loadData(config);
		Assert.assertNotNull(converter);
		Assert.assertEquals(converter.getMapCurrecy().isEmpty(), true);
		Assert.assertEquals(converter.getListRates().isEmpty(), true);
	}

	@Test
	public void testLoadDataFailureConversionRuleWithIncorrectDecimal() throws Exception {
		config.setConfig(Constants.CONVERSION_RATE_RESOURCE_KEY, "ConversionRate_NoContent.xml");
		config.setConfig(Constants.CONVERSION_RULE_RESOURCE_KEY, "ConversionRulesIncorrectDecimal.xml");

		UnsuccessfulDataLoading expectedException = new UnsuccessfulDataLoading("XMLParse exception");
		try {
			objectUnderTest.loadData(config);
		} catch (final Exception ex) {
			Assert.assertEquals(ex.getClass(), expectedException.getClass());
		}
	}

}
