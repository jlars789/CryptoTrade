package coinbase;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import currency.CurrencyHandler;
import notifications.ErrorLogger;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class APICallBuilder {
	
	private static final String CB_ACCESS_KEY = "CB-ACCESS-KEY";
	private static final String CB_ACCESS_SIGN = "CB-ACCESS-SIGN";
	private static final String CB_ACCESS_TIMESTAMP = "CB-ACCESS-TIMESTAMP";
	private static final String CB_VERSION = "CB-VERSION";
	
	public static JSONObject requests;
	public static String accessKey;
	public static String secretKey;
	
	public static void initialize() {
		getAPICalls();
		getCredentials();
	}
	
	public static JSONObject getMassExchangeRate() {
		String url = requests.getJSONObject("bulk_rates").getString("requestPath");
		Request request = new Request.Builder()
				.url(url)
				.build();
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
	
	public static double getExchangeRate(String currencyCode) {
		String url = requests.getJSONObject("exchange_rate").getString("requestPath");
		url.replaceAll("${var}", currencyCode);
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response res;
		double rate = 0;
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
		String url = requests.getJSONObject("wallet_data").getString("requestPath");
		String requestPath = "/v2/accounts";
		String method = "GET";
		String timestamp = getEpochTime();
		String body = "";
		String header = HeaderGenerator.getHMACHeader(secretKey, timestamp, method, requestPath, body);
		
		
		Request request = new Request.Builder()
				.addHeader(CB_ACCESS_KEY, accessKey)
				.addHeader(CB_ACCESS_SIGN, header)
				.addHeader(CB_ACCESS_TIMESTAMP, timestamp)
				.addHeader(CB_VERSION, getDate())
				.addHeader("Accept", "application/json")
				.url(url)
				.build();
		
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
	
	public static String getAccountID(String currencyCode) {
		return "";
	}
	
	public static void convertProCurrency(String fromCode, String toCode, double amount) {
		
	}
	
	public static void convertCurrency(String fromCode, String toCode, double amount) {
		
	}
	
	private static String getISOTime() {
		String url = requests.getJSONObject("time").getString("requestPath");
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response res;
		String time="";
		
		try {
			res = APICommunicator.sendRequest(request);
			JSONObject par = new JSONObject(res);
			JSONObject ch = par.getJSONObject("data");
			time = ch.getString("iso");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLogger.logException(e);
		}
		return time;
	}
	
	private static String getEpochTime() {
		BigDecimal dec = new BigDecimal(Instant.now().toEpochMilli()/1000);
		String now = dec.toPlainString();
		return now;
	}
	
	private static void getAPICalls() {
		String resourceName = "/CBAPIFixture.json";
        InputStream is = CurrencyHandler.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName);
        }

        JSONTokener tokener = new JSONTokener(is);
        requests = new JSONObject(tokener);
	}
	
	private static void getCredentials() {
		accessKey = System.getenv("CB_ACCESS_KEY");
		secretKey = System.getenv("CB_SECRET_KEY");
	}
	
	private static String getDate() {
		LocalDateTime now = LocalDateTime.now();
		String year = Integer.toString(now.getYear());
		String addM = "";
		if(now.getMonthOfYear() < 10) addM = "0";
		String month = addM+now.getMonthOfYear();
		
		String addD = "";
		if(now.getDayOfMonth() < 10) addD = "0";
		String day = addD+now.getDayOfMonth();
		
		return year+"-"+month+"-"+day;
		
	}

}
