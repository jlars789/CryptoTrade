package currency;

import java.io.InputStream;
import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import aws.S3Pull;
import aws.S3Upload;
import coinbase.APICallBuilder;
import coinbase.pro.APICallPro;
import coinbase.pro.Orders;

public class CurrencyHandler {
	
	private static Currency[] currencyArray;
	public static String[] trades;

	public static void initialize() {

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
        	getCurrencyByCode(temp).addTrade(trade, min);
        }
        updateValues();
	}
	
	public static void updateUserPreferences() {
		JSONObject userPref = new JSONObject(S3Pull.readObject("user_pref/user_preferences.json"));
		for(int i = 0; i < currencyArray.length; i++) {
			double targetMark = userPref.getJSONObject(currencyArray[i].getCode()).getDouble("initial");
			double threshold = userPref.getJSONObject(currencyArray[i].getCode()).getDouble("threshold");
			String tag = userPref.getJSONObject(currencyArray[i].getCode()).getString("type");
			currencyArray[i].setUserPreferences(targetMark, threshold, tag);
		}
	}
	
	public static void analyzeScalp() {
		for(int i = 0; i < currencyArray.length; i++) {
			if(currencyArray[i].shortTermTrade()) {
				Orders.marketSellFiat(currencyArray[i].getCode(), currencyArray[i].getScalpTrade());
			}
		}
	}
	
	public static Currency[] sortByScalpEff() {
		int n = currencyArray.length; 
		  
        // One by one move boundary of unsorted subarray 
        for (int i = 0; i < n-1; i++) 
        { 
            // Find the minimum element in unsorted array 
            int min_idx = i; 
            for (int j = i+1; j < n; j++) { 
            	double com0 = currencyArray[j].getExchangeRate() * currencyArray[j].getMinTradeValue();
            	double com1 = currencyArray[min_idx].getExchangeRate() * currencyArray[min_idx].getMinTradeValue();
                if (com0 < com1) 
                    min_idx = j; 
            }
  
            // Swap the found minimum element with the first 
            // element 
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
	
	public static void updateValues() {
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
			
			if(cannotFind) System.out.println("Cannot find " + currencyArray[i].getCode());
			
			double amount = 0;
			
			for(int j = 0; j < acctData.length(); j++) {
				double bal = acctData.getJSONObject(j).getDouble("balance");
				if(acctData.getJSONObject(j).getString("currency").equals(id)) {
					acctData.getJSONObject(j).getString("id");
					amount = bal;
					break;
				}
			}
			
			currencyArray[i].dataUpdate(rate.doubleValue(), amount, change);
		}
		S3Upload.uploadString(acctData.toString(1), "ids/pro-wallet-data.json");
        
	}

}
