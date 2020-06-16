package coinbase;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import currency.CurrencyHandler;
import notifications.ErrorLogger;
import okhttp3.Request;
import okhttp3.Response;

public class APICallBuilder {
	
	/*
	public static JSONObject getMassExchangeRate() {
		String url = APIUtility.requests.getJSONObject("bulk_rates").getString("requestPath");
		Request request = APIUtility.buildGetRequest(url, false);
		Response res;
		
		JSONObject obj = null;
		
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			//System.out.println(data);
			obj = new JSONObject(data);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		
		return obj;
		
	}
	*/
	
	public static JSONArray getMassExchangeRate() {
		String url = APIUtility.requests.getJSONObject("exchange_rate_cc").getString("requestPath");
		Request request = APIUtility.buildCoinCapRequest(url);
		
		Response res=null;
		JSONArray arr = null;
		try {
			res = APICommunicator.sendRequest(request);
			JSONObject par = new JSONObject(res.body().string());
			arr = par.getJSONArray("data");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return arr;
	}
	
	public static double getExchangeRate(String currencyCode) {
		String url = APIUtility.requests.getJSONObject("exchange_rate").getString("requestPath");
		url = url.replaceAll("${var}", currencyCode);
		Request request = APIUtility.buildGetRequest(url, false);
		double rate = 0;
		Response res=null;
		try {
			res = APICommunicator.sendRequest(request);
			JSONObject par = new JSONObject(res);
			JSONObject ch = par.getJSONObject("data");
			rate = ch.getDouble("amount");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return rate;
	}
	
	public static JSONObject getAccountData() {
		String requestPath = APIUtility.requests.getJSONObject("wallet_data").getString("requestPath");
		
		Request request = APIUtility.buildGetRequest(requestPath, true);
		
		Response res;
		JSONObject par = null;
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			par = new JSONObject(data);
			//System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return par;
	}
	
	public static JSONObject convertCurrency(String fromCode, String toCode, double amount) {
		String fromID, toID;
		fromID = CurrencyHandler.findCurrencyID(fromCode);
		toID = CurrencyHandler.findCurrencyID(toCode);
		String requestPath = APIUtility.requests.getJSONObject("convert").getString("requestPath").replace("${var}", fromID);
		requestPath = requestPath.replace("${var}", fromID);
		
		
		JSONObject bodyFormatter = new JSONObject();
		bodyFormatter.put("type", "transfer");
		bodyFormatter.put("to", toID);
		bodyFormatter.put("amount", amount);
		bodyFormatter.put("currency", "USD");
		
		Request request = APIUtility.buildPostRequest(bodyFormatter, requestPath);
		Response res;
		JSONObject par = null;
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			System.out.println(res.code());
			//System.out.println(res.headers().)
			par = new JSONObject(data);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		
		return par;
	}
	
	public static JSONObject getTransactionHistory(String id) {
		String url = APIUtility.requests.getJSONObject("transactions").getString("requestPath");
		url = url.replace("${var}", id);
		
		Request request = APIUtility.buildGetRequest(url, true);
		
		Response res;
		JSONObject par = null;
		try {
			res = APICommunicator.sendRequest(request);
			String data = res.body().string();
			par = new JSONObject(data);
			//System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return par;
	}
	
	

}
