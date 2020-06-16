package coinbase;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APICommunicator {

	public static OkHttpClient client = new OkHttpClient();
	
	public static Response sendRequest(Request request) throws IOException {
		Response response = client.newCall(request).execute();
		if(!response.isSuccessful()) {
			throw new IOException("API Call not successful. Error code: " + response.code() + ": " + response.message() + " " + response.body().string());
		} else {
			return response;
		}
	}
}
