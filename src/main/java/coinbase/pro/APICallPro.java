package coinbase.pro;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import coinbase.APICommunicator;
import coinbase.APIUtility;
import currency.Currency;
import currency.CurrencyHandler;
import notifications.ErrorLogger;
import okhttp3.Request;
import okhttp3.Response;

public class APICallPro {
	
	public static JSONArray getAccountData() {
		String requestPath = APIUtility.requests.getJSONObject("wallet_data_pro").getString("requestPath");
		Request request = APIUtility.buildProGetRequest(requestPath, true);
		
		Response res;
		JSONArray par = null;
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			par = new JSONArray(data);
			//System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return par;
	}
	
	public static void convertUSDtoUSDC(double amount) {
		convertStable("USD", "USDC", amount);
	}
	
	public static void convertUSDCtoUSD(double amount) {
		convertStable("USD", "USDC", amount);
	}
	
	public static void convertCurrency(String fromCode, String toCode, double amount) {
		Currency c0 = CurrencyHandler.getCurrencyByCode(fromCode);
		Currency c1 = CurrencyHandler.getCurrencyByCode(toCode);
		
		boolean shareTrades = false;
		if((c0.canTrade("USD") && c1.canTrade("USD")) || (c0.canTrade("USDC") && c1.canTrade("USDC"))) shareTrades = true;
		
		if(shareTrades) {
			Orders.marketSellFiat(fromCode, amount);
			Orders.marketBuyFiat(toCode, amount);
		} else {
			String from = getTrade(c0.getCode());
			String to = getTrade(c0.getCode());
			Orders.marketSellFiat(fromCode, amount);
			convertStable(from, to, amount);
			Orders.marketBuyFiat(toCode, amount);
		}
	}
	
	private static JSONObject convertStable(String fromCode, String toCode, double amount) {
		String requestPath = APIUtility.requests.getJSONObject("convert_pro").getString("requestPath");
		double conv = CurrencyHandler.getCurrencyByCode(fromCode).convertFromFiat(amount);
		
		JSONObject body = new JSONObject();
		body.put("from", fromCode);
		body.put("to", toCode);
		body.put("amount", conv);
		
		Request request = APIUtility.buildProPostRequest(body, requestPath);
		Response res;
		JSONObject obj = null;
		
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			obj = new JSONObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return obj;
	}
	
	public static JSONArray getTrades() {
		String requestPath = APIUtility.requests.getJSONObject("products").getString("requestPath");
		
		Request request = APIUtility.buildProGetRequest(requestPath, false);
		JSONArray arr = null;
		Response res;
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			arr = new JSONArray(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	private static String getTrade(String code) {
		Currency c = CurrencyHandler.getCurrencyByCode(code);
		String tr = "";
		if(c.canTrade("USD")) tr="USD";
		else if(c.canTrade("USDC")) tr="USDC";
		else tr="BTC";
		return tr;
	}

}
