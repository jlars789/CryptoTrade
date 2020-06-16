package coinbase;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import notifications.ErrorLogger;
import okhttp3.Request;
import okhttp3.Response;

public class APICallBuilderPro {
	
	public static JSONArray getAccountData() {
		String requestPath = APIUtility.requests.getJSONObject("wallet_data_pro").getString("requestPath");
		//String method = APIUtility.requests.getJSONObject("wallet_data_pro").getString("method");
		
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
	
	public static void convertCurrency(String fromCode, String toCode, double amount) {
		
	}
	
	protected static void getCredentials() {
		
	}

}
