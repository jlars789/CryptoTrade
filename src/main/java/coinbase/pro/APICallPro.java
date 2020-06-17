package coinbase.pro;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import coinbase.APICommunicator;
import coinbase.APIUtility;
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
	
	public static JSONObject convertCurrency(String fromCode, String toCode, double amount) {
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

}
