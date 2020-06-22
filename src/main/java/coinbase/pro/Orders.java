package coinbase.pro;

import java.io.IOException;

import org.json.JSONObject;

import coinbase.APICommunicator;
import coinbase.APIUtility;
import currency.Currency;
import currency.CurrencyHandler;
import okhttp3.Request;
import okhttp3.Response;
import tracker.OrderTracker;
import tracker.Transaction;

public class Orders {
	
	public static JSONObject marketSellFiat(String code, double amount) {
		String requestPath = APIUtility.requests.getJSONObject("trade").getString("requestPath");
		Currency c = CurrencyHandler.getCurrencyByCode(code);
		if(c.convertFromFiat(amount) < c.getMinTradeValue()) {
			amount = c.getMinTradeValue();
			System.out.println("Using minimum trade value");
		}
		else {
			amount = c.convertFromFiat(amount);
		}
		System.out.println("Attempting to sell " +code + " for " + amount + " " + code);
		String tradeID = getOptimalSell(code);
		if(tradeID.contains("USDC")) {
			return limitSellFiat(tradeID, amount);
		}
		JSONObject body = new JSONObject();
		body.put("type", "market");
		body.put("size", amount);
		body.put("side", "sell");
		body.put("product_id", tradeID);
		
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
		
		OrderTracker.addTransaction(new Transaction(obj));
		System.out.println("Sold " + code + " for $" + amount);
		return obj;
		
	}
	
	private static JSONObject limitSellFiat(String tradeID, double amount) {
		String requestPath = APIUtility.requests.getJSONObject("trade").getString("requestPath");
		String code = tradeID.split("-")[0];
		JSONObject body = new JSONObject();
		body.put("type", "limit");
		body.put("price", CurrencyHandler.getCurrencyByCode(code));
		body.put("size", amount);
		body.put("side", "sell");
		body.put("product_id", tradeID);
		
		
		
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
		
		OrderTracker.addTransaction(new Transaction(obj));
		System.out.println("Put Limit Order Up for " + code + " for $" + amount);
		return obj;
	}
	
	public static JSONObject marketBuyFiat(String code, double amount) {
		String requestPath = APIUtility.requests.getJSONObject("trade").getString("requestPath");
		Currency c = CurrencyHandler.getCurrencyByCode(code);
		if(c.convertFromFiat(amount) < c.getMinTradeValue()) amount = c.getMinTradeValue();
		else amount = c.convertFromFiat(amount);
		
		String tradeID = getOptimalSell(code);
		JSONObject body = new JSONObject();
		body.put("type", "market");
		body.put("size", amount);
		body.put("side", "buy");
		body.put("product_id", tradeID);
		
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
		
		OrderTracker.addTransaction(new Transaction(obj));
		System.out.println("Bought " + code + " for $" + amount);
		return obj;
	}
	
	private static String getOptimalSell(String code) {
		Currency c = CurrencyHandler.getCurrencyByCode(code);
		String tr = "";
		if(c.canTrade("USD")) tr="USD";
		else if(c.canTrade("USDC")) tr="USDC";
		else tr="BTC";
		return code + "-" + tr;
	}

}
