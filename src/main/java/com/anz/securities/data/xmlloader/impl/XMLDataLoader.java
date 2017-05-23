package com.anz.securities.data.xmlloader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anz.securities.common.Constants;
import com.anz.securities.common.exception.UnsuccessfulDataLoading;
import com.anz.securities.data.xmlloader.util.XMLLoaderUtil;
import com.anz.securities.dataloader.spi.DataLoader;
import com.anz.securities.dataloader.spi.LoaderConfig;
import com.anz.securities.entities.api.ConversionRate;
import com.anz.securities.entities.api.ConversionRule;
import com.anz.securities.entities.api.Currency;
import com.anz.securities.entities.dto.CurrencyConverter;
import com.anz.securities.entities.impl.ConversionRateImpl;
import com.anz.securities.entities.impl.ConversionRuleImpl;
import com.anz.securities.entities.impl.CurrencyImpl;

/**
 * Provides an implementation to load the data
 * 
 * @author Anand Katti
 *
 */
public class XMLDataLoader implements DataLoader {
	private static Logger logger = LoggerFactory.getLogger(XMLDataLoader.class);

	/**
	 * Loads both the rules data and the rates data
	 * 
	 * @see com.anz.securities.dataloader.spi.DataLoader.loadData
	 */
	public CurrencyConverter loadData(final LoaderConfig config) throws UnsuccessfulDataLoading {
		CurrencyConverter converter = new CurrencyConverter();
		Map<String, Currency> currencyRuleMap = loadConversionRules(
				(String) config.getConfigValue(Constants.CONVERSION_RULE_RESOURCE_KEY));
		List<ConversionRate> listRates = loadConversionRates(
				(String) config.getConfigValue(Constants.CONVERSION_RATE_RESOURCE_KEY));
		converter.setMapCurrecy(currencyRuleMap);
		converter.setListRates(listRates);
		return converter;
	}

	/**
	 * Loads the conversion rate data
	 * 
	 * @param resource
	 * @return rateList
	 * @throws UnsuccessfulDataLoading
	 */
	private List<ConversionRate> loadConversionRates(final String resource) throws UnsuccessfulDataLoading {
		try {
			logger.debug("Loading data from:", resource);
			List<ConversionRate> rateList = new ArrayList<>();
			Document doc = XMLLoaderUtil.getXMLDocument(resource);

			NodeList nList = doc.getElementsByTagName(Constants.CONVERTION_RATE);
			String srcCur;
			String destCur;
			String rate;

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				srcCur = eElement.getElementsByTagName(Constants.SOURCE_CURRENCY).item(0).getTextContent();
				destCur = eElement.getElementsByTagName(Constants.DESTINATION_CURRENCY).item(0).getTextContent();
				rate = eElement.getElementsByTagName(Constants.RATE).item(0).getTextContent();

				ConversionRate convRate = new ConversionRateImpl(srcCur, destCur, Double.parseDouble(rate));

				rateList.add(convRate);
			}
			logger.debug("Loading data completed with size:", rateList.size());
			return rateList;
		} catch (Exception ex) {
			throw new UnsuccessfulDataLoading(ex.getMessage(), ex);
		}

	}

	/**
	 * Loads the rules data
	 * 
	 * @param resource
	 * @return ruleMap
	 * @throws UnsuccessfulDataLoading
	 */
	private Map<String, Currency> loadConversionRules(final String resource) throws UnsuccessfulDataLoading {
		try {
			logger.debug("Loading data from:", resource);
			Map<String, Currency> currencyRuleMap = new HashMap<>();

			Document doc = XMLLoaderUtil.getXMLDocument(resource);
			NodeList nList = doc.getElementsByTagName(Constants.SOURCE_CURRENCY);
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				String currencyId = eElement.getAttribute(Constants.ID);
				int decimal = Integer.parseInt(eElement.getAttribute(Constants.SUPPORTED_DECIMAL));

				NodeList childNodes = eElement.getElementsByTagName(Constants.DESTINATION_CURRENCY);
				List<ConversionRule> listConversionRules = new ArrayList<>();
				for (int temp1 = 0; temp1 < childNodes.getLength(); temp1++) {
					Node nNode1 = childNodes.item(temp1);
					Element eElement1 = (Element) nNode1;
					String destCurrencyId = eElement1.getAttribute(Constants.ID);
					String linkedTo = eElement1.getAttribute(Constants.LINKED_TO);
					ConversionRule converRule = new ConversionRuleImpl(destCurrencyId, linkedTo);
					listConversionRules.add(converRule);
				}
				Currency tempCurrency = new CurrencyImpl(currencyId, decimal, listConversionRules);
				currencyRuleMap.put(currencyId, tempCurrency);
			}
			logger.debug("Loading data completed with size:", currencyRuleMap.size());
			return currencyRuleMap;

		} catch (Exception ex) {
			throw new UnsuccessfulDataLoading("Generic Exception Loading Currencies:", ex);
		}
	}
}
