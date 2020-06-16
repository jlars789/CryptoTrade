package currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import coinbase.APICallBuilder;

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
        	currencyArray[i] = new Currency(name, code);
        }
	}
	
	public static void updateValues() {
		JSONObject rates = APICallBuilder.getMassExchangeRate();
		JSONObject acctData = APICallBuilder.getAccountData();
		JSONArray arr = acctData.getJSONArray("data");
		
		for(int i = 0; i < currencyArray.length; i++) {
			String id = currencyArray[i].getCode();
			
			BigDecimal rate = new BigDecimal(rates.getJSONObject("data").getJSONObject("rates").getString(id));
			BigDecimal one = new BigDecimal("1.0");
			rate = one.divide(rate, 3, RoundingMode.HALF_UP);
			double amount = 0;
			
			for(int j = 0; j < arr.length(); j++) {
				JSONObject bal = arr.getJSONObject(j).getJSONObject("balance");
				if(bal.getString("currency").equals(id)) {
					amount = bal.getDouble("amount");
					break;
				}
			}
			
			currencyArray[i].dataUpdate(rate.doubleValue(), amount);
		}
	}

}
