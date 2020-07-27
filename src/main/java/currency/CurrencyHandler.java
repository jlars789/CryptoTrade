package currency;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;

import api.CoinCapAPI;
import aws.S3Pull;
import rule.StopAfterHigh;

public class CurrencyHandler {
	
	public static final int IS_USER_BUY = 1;
	public static final int PERM_SELL = 10;
	public static final double AUTO_SELL = 0.025;
	public static final int COUNTER_MAX = 480;
	
	private static int counter = 0;
	
	public static ArrayList<String> orderIDs = new ArrayList<String>();
	
	protected static Currency[] currencyArray;
	public static String[] trades;
	
	private static ArrayList<String> excluded = new ArrayList<String>();
	
	public static LocalDateTime initTime;
	public static LocalDateTime orderCheckStamp;
	
	public static Exchange bitstamp;

	/**
	 * Instantiates all pertinent currency values not relating to user data </br>
	 * Pulls data from CurrencyFixture.json to instantiate the name, code, and asset ID </br>
	 * Pulls data from "products" endpoint on Coinbase Pro to instantiate 24hr change and exchange rate
	 * 
	 */
	
	public static void initialize(int size) {

		initTime = LocalDateTime.now(ZoneOffset.UTC);
		orderCheckStamp = LocalDateTime.now(ZoneOffset.UTC);
		
		String resourceName = "/CurrencyFixture.json";
        InputStream is = CurrencyHandler.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName);
        }

        JSONTokener tokener = new JSONTokener(is);
        JSONArray array = new JSONArray(tokener);
        
        currencyArray = new Currency[array.length()];
        
        for(int i = 0; i < array.length(); i++) {
        	String name = array.getJSONObject(i).getString("name");
        	String code = array.getJSONObject(i).getString("code");
        	currencyArray[i] = new Currency(name, code);
        }
        
   
        
        initCurrencies(size);
        
        Properties prop = new Properties();
        String fileName = "/properties/auth.properties";
        InputStream is1 = CurrencyHandler.class.getResourceAsStream(fileName);
        
        try {
            prop.load(is1);
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        
        ExchangeSpecification exSpec = new ExchangeSpecification(BinanceExchange.class.getName());
        exSpec.setApiKey(prop.getProperty("binance.apikey"));
        exSpec.setSecretKey(prop.getProperty("binance.secretkey"));
        bitstamp = ExchangeFactory.INSTANCE.createExchange(exSpec);
        
        
	}
	
	public static Strategy buildStrategy(BarSeries bs, String currency) {
		ClosePriceIndicator closePrice = new ClosePriceIndicator(bs);
		
		
		EMAIndicator shortEma = new EMAIndicator(closePrice, 9);
	    EMAIndicator longEma = new EMAIndicator(closePrice, 26);

	    StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(bs, 14);

	    MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
	    EMAIndicator emaMacd = new EMAIndicator(macd, 9);
	    
	    EMAIndicator avg14 = new EMAIndicator(closePrice, 10);
        StandardDeviationIndicator sd14 = new StandardDeviationIndicator(closePrice, 10);

        // Bollinger bands
        BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(avg14);
        BollingerBandsLowerIndicator lowBBand = new BollingerBandsLowerIndicator(middleBBand, sd14);
        BollingerBandsUpperIndicator upBBand = new BollingerBandsUpperIndicator(middleBBand, sd14);
        
        /*
        Rule entryRule = new UnderIndicatorRule(closePrice, lowBBand)
        		.and(new UnderIndicatorRule(new RSIIndicator(closePrice, 10), 30))
        		.and(new OverIndicatorRule(macd, emaMacd));
        //Rule entryRule1 = new 
	    Rule exitRule = new StopLossRule(closePrice, PrecisionNum.valueOf("5"));
	    
	    Rule indicExit = new UnderIndicatorRule(macd, emaMacd)
		.and(new OverIndicatorRule(new RSIIndicator(closePrice, 10), 60));
	    exitRule = exitRule.or(indicExit);
	    /*
	    Rule indicExit = new CrossedUpIndicatorRule(closePrice, upBBand)
	    		.and(new OverIndicatorRule(new RSIIndicator(closePrice, 10), 70));
	    		*/
	    
	    
	    		
	   Rule entryRule = new CrossedDownIndicatorRule(new RSIIndicator(closePrice, 25), 30);
	   //Rule exitRule = new CrossedUpIndicatorRule(new RSIIndicator(closePrice, 25), 70);
	   Rule exitRule = new StopAfterHigh(closePrice, PrecisionNum.valueOf("15"), currency)
			   .or(new CrossedUpIndicatorRule(new RSIIndicator(closePrice, 25), 70));
	   /*
	   Rule exitRule = new CrossedUpIndicatorRule(new RSIIndicator(closePrice, 25), 70)
			   .or(new StopLossRule(closePrice, PrecisionNum.valueOf("2")));
			   //.or(new TrailingStopLossRule(closePrice, PrecisionNum.valueOf("12.5"), 20));
	   //exitRule = exitRule.or(new TrailingStopLossRule(closePrice, PrecisionNum.valueOf("12.5"), 20));
	  // exitRule = exitRule.or(new StopLossRule(closePrice, PrecisionNum.valueOf("10")));
	    
	    
	    //new TrailingStopLossRule(closePrice, PrecisionNum.valueOf("20"))
		//.or
	    /*
		Rule entryRule = new CrossedUpIndicatorRule(macd, emaMacd)
				.and(new UnderIndicatorRule(new RSIIndicator(closePrice, 14), 40));
		Rule exitRule = new TrailingStopLossRule(closePrice, PrecisionNum.valueOf("10"))
				.or((new CrossedDownIndicatorRule(macd, emaMacd))
				.and(new OverIndicatorRule(new RSIIndicator(closePrice, 14), 70)));
		*/
	    /*
	    // Entry rule
	    Rule entryRule = new OverIndicatorRule(macd, emaMacd)
	    		.and(new UnderIndicatorRule(stochasticOscillK, Decimal.valueOf(20)));
	    
	    // Exit rule
	    Rule exitRule = new UnderIndicatorRule(macd, emaMacd)
	    		.and(new OverIndicatorRule(stochasticOscillK, Decimal.valueOf(80))); 
	    		*/
	    
	    return new BaseStrategy(entryRule, exitRule);
	}
	
	/**
	 * Pulls data from an AWS S3 Bucket to evaluate user preferences
	 */
	/*
	public static void updateUserPreferences() {
		if(counter % 4 == 2) {
			JSONObject userPref = new JSONObject(S3Pull.readObject("user_pref/user_preferences.json"));
			for(int i = 0; i < currencyArray.length; i++) {
				if(!currencyArray[i].getCode().equals("USD")) {
					String tag = userPref.getJSONObject(currencyArray[i].getCode()).getString("type");
					currencyArray[i].setUserPreferences(0, 0, tag);
				}
			}
		}
	}
	*/
	
	private static void initCurrencies(int size) {
		String s = "2019-07-13 00:00";
		String e = "2020-07-13 00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		ZonedDateTime start = LocalDateTime.parse(s, formatter).atZone(ZoneId.of("Z"));
		ZonedDateTime end = LocalDateTime.parse(e, formatter).atZone(ZoneId.of("Z"));
		
		for(int i = 0 ; i < currencyArray.length; i++) {
			if(!currencyArray[i].isStable()) {
				
				JSONArray min = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "m1");
				//JSONArray hour = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "h1");
				//JSONArray day = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "d1");
			
				//JSONArray min = CoinCapAPI.getRatesFromDate(currencyArray[i].getCCName(), "m1", start, end);
				JSONArray hour = CoinCapAPI.getRatesFromDate(currencyArray[i].getCCName(), "h1", start, end);
				JSONArray day = CoinCapAPI.getRatesFromDate(currencyArray[i].getCCName(), "d1", start, end);
				currencyArray[i].dataInit(min, hour, day, size);
			}
		}
		
		int btcSize = currencyArray[getIndexByCode("BTC")].getSeries("h1").getBarCount();
		for(int i = 0 ; i < currencyArray.length; i++) {
			if(!currencyArray[i].isStable() && !currencyArray[i].getCode().equals("REP")) {
				//System.out.println(currencyArray[i].getCode());
				if(currencyArray[i].getSeries("h1").getBarCount() < btcSize) {
					excluded.add(currencyArray[i].getCode());
				}
			} else {
				excluded.add(currencyArray[i].getCode());
			}
		}
	}
	
	public static boolean isExcluded(String currency) {
		boolean isExcluded = false;
		for(int i = 0; i < excluded.size(); i++) {
			if(excluded.get(i).equals(currency)) {
				isExcluded = true;
				break;
			}
		}
		return isExcluded;
	}
	
	public static boolean isExcluded(int index) {
		return isExcluded(getCodeByIndex(index));
	}
	
	public static Currency getCurrencyByCode(String code) {
		Currency c = null;
		for(int i= 0; i < currencyArray.length; i++) {
			if(currencyArray[i].getCode().equals(code)) {
				c = currencyArray[i];
				break;
			}
		}
		return c;
	}
	
	public static int getIndexByCode(String code) {
		int index = 0;
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].getCode().equals(code)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	public static void initValues(int size) {
		for(int i = 0; i < currencyArray.length; i++) {
			JSONArray min = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "m1");
			JSONArray hour = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "h1");
			JSONArray day = CoinCapAPI.getHistoricalRate(currencyArray[i].getCCName(), "d1");
			
			currencyArray[i].dataInit(min, hour, day, size);
		}
	}
	
	public static void addBars(String interval) {
		for(int i = 0; i < currencyArray.length; i++) {
			currencyArray[i].updateBar(interval);
		}
	}
	
	public static String getDailyMessages(){
		String str = "";
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].addOnReport()) {
				str += currencyArray[i].messageToAdd();
				str += "\n";
			}
		}
		return str;
	}
	
	public static int getCurrencySize() {
		return currencyArray.length;
	}
	
	public static String getCodeByIndex(int index) {
		return currencyArray[index].getCode();
	}
	
	private static void addID(String id) {
		if(orderIDs.size() > 30) {
			orderIDs.remove(0);
		}
		orderIDs.add(id);
	}

}
