package notifications.sms;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import notifications.ErrorLogger;
import okhttp3.MediaType;

public class SendSMS {
	
	public static void sendMessage(String message) {
		
		String formattedMessage = replace(message);
		
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "ipnUrl=null&message="+formattedMessage+"&toNumber=15404229056");
		Request request = new Request.Builder()
				.url("https://quick-easy-sms.p.rapidapi.com/send")
				.post(body)
				.addHeader("x-rapidapi-host", "quick-easy-sms.p.rapidapi.com")
				.addHeader("x-rapidapi-key", "6052ac844fmshb83d8c25601bcc8p170b89jsnb7bf4e9186ff")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();
	
		try {
			Response response = client.newCall(request).execute();
		} catch(IOException e) {
			ErrorLogger.logException(e);
		}
	}
	
	private static String replace(String str) {
	    String[] words = str.split(" ");
	    StringBuilder sentence = new StringBuilder(words[0]);

	    for (int i = 1; i < words.length; ++i) {
	        sentence.append("%20");
	        sentence.append(words[i]);
	    }

	    return sentence.toString();
	}
	
}
