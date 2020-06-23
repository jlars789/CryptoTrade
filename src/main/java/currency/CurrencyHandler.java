package currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import aws.S3Pull;
import aws.S3Upload;
import coinbase.APICallBuilder;
import coinbase.pro.APICallPro;
import coinbase.pro.Orders;
import notifications.email.MessageSender;

public class CurrencyHandler {
	
	public static final int IS_USER_BUY = 1;
	public static final int PERM_SELL = 10;
	public static final double AUTO_SELL = 0.05;
	public static final int COUNTER_MAX = 480;
	
	public static int counter = 0;
	
	public static ArrayList<String> orderIDs = new ArrayList<String>();
	
	private static Currency[] currencyArray;
	public static String[] trades;
	
	public static LocalDateTime initTime;

	/**
	 * Instantiates all pertinent currency values not relating to user data </br>
	 * Pulls data from CurrencyFixture.json to instantiate the name, code, and asset ID </br>
	 * Pulls data from "products" endpoint on Coinbase Pro to instantiate 24hr change and exchange rate
	 * 
	 */
	
	public static void initialize() {

		initTime = LocalDateTime.now(ZoneOffset.UTC);
		
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
        	String id = array.getJSONObject(i).getString("asset_id");
        	currencyArray[i] = new Currency(name, code, id);
        }
        
        JSONArray j = APICallPro.getTrades();
        
        for(int i = 0; i < j.length(); i++) {
        	String temp = j.getJSONObject(i).getString("base_currency");
        	String trade = j.getJSONObject(i).getString("id");
        	double min = j.getJSONObject(i).getDouble("base_min_size");
        	String minInc = j.getJSONObject(i).getString("base_increment");
        	getCurrencyByCode(temp).addTrade(trade, min, minInc);
        }
        updateValues(true);
	}
	
	/**
	 * Pulls data from an AWS S3 Bucket to evaluate user preferences
	 */
	
	public static void updateUserPreferences() {
		JSONObject userPref = new JSONObject(S3Pull.readObject("user_pref/user_preferences.json"));
		for(int i = 0; i < currencyArray.length; i++) {
			//double targetMark = userPref.getJSONObject(currencyArray[i].getCode()).getDouble("initial");
			//double threshold = userPref.getJSONObject(currencyArray[i].getCode()).getDouble("threshold");
			if(!currencyArray[i].getCode().equals("USD")) {
				String tag = userPref.getJSONObject(currencyArray[i].getCode()).getString("type");
				currencyArray[i].setUserPreferences(0, 0, tag);
			}
		}
	}
	
	/**
	 * Calls the {@code analyzeScalp()} and {@code stopOrderScalp} as part of the default scalp strategy
	 */
	
	public static void scalpStrategy() {
		analyzeScalp();
		stopOrderSell();
	}
	
	/**
	 * Sees if the currency is a margin above the original investment to the point where it can sell
	 */
	private static void analyzeScalp() {
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].getFiatValue() > 0) {
				System.out.println(currencyArray[i].getCode() + ": " + currencyArray[i].getFiatValue() + ", Initial " + currencyArray[i].getInitial());
			}
			if(currencyArray[i].shortTermTrade()) {
				System.out.println("Making scalp trade of " + currencyArray[i].getCode());
				//System.out.println(currencyArray[i].formatToStandard(currencyArray[i].getScalpTrade()));
				Orders.marketSellFiat(currencyArray[i].getCode(), currencyArray[i].getExchangeRate() * currencyArray[i].formatToStandard(currencyArray[i].getScalpTrade()));
				currencyArray[i].getData().addOrder(currencyArray[i].getScalpTrade());
			}
		}
	}
	
	/**
	 * Evaluates 
	 */
	
	private static void stopOrderSell() {
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].stopOrderSell()) {
				System.out.println("Attempting to sell all of " + currencyArray[i].getCode());
				double originalValue = currencyArray[i].getFiatValue();
				Orders.marketSellFiat(currencyArray[i].getCode(), currencyArray[i].formatToStandard(currencyArray[i].getFiatValue()-.1));
				LocalDateTime time = LocalDateTime.now();
				String message = "On " + time.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + time.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + 
						" " + time.getDayOfMonth() + ", all of your " + currencyArray[i].getName() + " was sold as it went below the desired threshold. It was worth $" + 
						originalValue + " at the time of sale";
				
				MessageSender.deliverMessage("Alert: All " + currencyArray[i].getCode() + " was sold!" , message);
				
				currencyArray[i].getData().soldAll(originalValue);
			}
		}
	}
	
	public static Currency[] sortByScalpEff() {
		int n = currencyArray.length; 
		  
        for (int i = 0; i < n-1; i++) 
        { 
            int min_idx = i; 
            for (int j = i+1; j < n; j++) { 
            	double com0 = currencyArray[j].getExchangeRate() * currencyArray[j].getMinTradeValue();
            	double com1 = currencyArray[min_idx].getExchangeRate() * currencyArray[min_idx].getMinTradeValue();
                if (com0 < com1) 
                    min_idx = j; 
            }
  
            Currency temp = currencyArray[min_idx]; 
            currencyArray[min_idx] = currencyArray[i]; 
            currencyArray[i] = temp; 
        } 
        Currency[] ret = currencyArray;
        return ret;
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
	
	public static String findCurrencyID(String code) {
		String ID = "Invalid code";
		
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].getCode().equals(code)) {
				ID = currencyArray[i].getID();
			}
		}
		return ID;
	}
	
	public static void updateValues(boolean init) {
		JSONArray rates = APICallBuilder.getMassExchangeRate();
		JSONArray acctData = APICallPro.getAccountData();
		
		
		for(int i = 0; i < currencyArray.length; i++) {
			String id = currencyArray[i].getCode();
			BigDecimal rate = null;
			double change = 0;
			boolean cannotFind = true;
			for(int j = 0; j < rates.length(); j++) {
				if(rates.getJSONObject(j).getString("symbol").equals(currencyArray[i].getCode())) {
					rate = new BigDecimal(rates.getJSONObject(j).getString("priceUsd"));
					change = Double.parseDouble(rates.getJSONObject(j).getString("changePercent24Hr"));
					cannotFind = false;
					break;
				}
			}
			
			if(cannotFind) {
				//System.out.println("Cannot find " + currencyArray[i].getCode());
				rate = new BigDecimal("1.0");
				change = 0.0;
			}
			
			double amount = 0;
			
			for(int j = 0; j < acctData.length(); j++) {
				double bal = acctData.getJSONObject(j).getDouble("available");
				if(acctData.getJSONObject(j).getString("currency").equals(id)) {
					acctData.getJSONObject(j).getString("id");
					amount = bal;
					break;
				}
			}
			
			
			
			
			if(init) {
				String data = S3Pull.readObject("user_pref/initial_invest.json");
				JSONObject obj = new JSONObject(data);
				double initial = obj.getJSONObject(currencyArray[i].getCode()).getDouble("initial");
				currencyArray[i].dataInit(rate.doubleValue(), amount, initial, change);
			}
			else {
				currencyArray[i].dataUpdate(rate.doubleValue(), amount, change);
				
			}
		}
		counter++;
		if(counter >= COUNTER_MAX) {
			S3Upload.uploadString(acctData.toString(1), "ids/pro-wallet-data.json");
			counter = 0;
		}
        
	}
	
	public static void checkUserPurchases() {
		JSONArray orders = APICallPro.getFullOrderHistory();
		
		boolean modification = false;
		boolean called = false;
		JSONObject obj = null;
		
		for(int i = 0; i < orders.length(); i++) {
			JSONObject t = orders.getJSONObject(i);
			boolean counted = false;
			
			for(int j = 0; j < orderIDs.size(); j++) {
				if(orderIDs.get(j).equals(t.getString("id"))) counted = true;
			}
			if(!counted) {
				addID(t.getString("id"));
				String time = orders.getJSONObject(i).getString("created_at");
				time = time.replace("Z", "");
				LocalDateTime now = LocalDateTime.parse(time);
				long dur = Duration.between(initTime, now).toMillis();
				if(dur > 0) {
					if(!called) {
						String data = S3Pull.readObject("user_pref/initial_invest.json");
						obj = new JSONObject(data);
						called = true;
					}
					if(t.getString("side").equals("buy") && t.getString("done_reason").equals("filled") && (Double.parseDouble(t.getString("executed_value")) >= IS_USER_BUY)){
						String productID = t.getString("product_id");
						if(productID.contains("USDC")) {
							productID = productID.replace("-USDC", "");
						} else {
							productID = productID.replace("-USD", "");
						}
						
						double value = t.getDouble("executed_value");
						getCurrencyByCode(productID).updateInitial(value);
						double ini = obj.getJSONObject(productID).getDouble("initial");
						obj.getJSONObject(productID).put("initial", ini+value);
						modification = true;
					}
					else if(t.getString("side").equals("sell") && t.getString("done_reason").equals("filled") && (Double.parseDouble(t.getString("executed_value")) >= PERM_SELL)){
						String productID = t.getString("product_id");
						if(productID.contains("USDC")) {
							productID = productID.replace("-USDC", "");
						} else {
							productID = productID.replace("-USD", "");
						}
						
						double value = t.getDouble("executed_value");
						getCurrencyByCode(productID).updateInitial(value);
						double ini = obj.getJSONObject(productID).getDouble("initial");
						obj.getJSONObject(productID).put("initial", ini+value);
						modification = true;
					}
				}
				
			}
		}
		if(modification) {
			S3Upload.uploadString(obj.toString(1), "user_pref/initial-invest.json");
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
	
	private static void addID(String id) {
		if(orderIDs.size() > 30) {
			orderIDs.remove(0);
		}
		orderIDs.add(id);
	}

}
