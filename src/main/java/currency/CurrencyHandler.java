package currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import aws.S3Upload;
import coinbase.APICallBuilder;
import coinbase.APICallBuilderPro;

public class CurrencyHandler {
	
	private static Currency[] currencyArray;

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
		JSONArray acctData = APICallBuilderPro.getAccountData();
		
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
				}
			}
			
			if(cannotFind) System.out.println("Cannot find " + currencyArray[i].getCode());
			
			
			BigDecimal one = new BigDecimal("1.0");
			rate = one.divide(rate, 3, RoundingMode.HALF_UP);
			double amount = 0;
			
			String caller = "";
			
			for(int j = 0; j < acctData.length(); j++) {
				double bal = acctData.getJSONObject(j).getDouble("balance");
				if(acctData.getJSONObject(j).getString("currency").equals(id)) {
					caller = acctData.getJSONObject(j).getString("id");
					amount = bal;
					break;
				}
			}
			
			currencyArray[i].dataUpdate(rate.doubleValue(), amount, change);
		}
		S3Upload.uploadString(acctData.toString(1), "ids/pro-wallet-data.json");
        
	}

}
