package coinbase;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;
import org.json.JSONTokener;

import currency.CurrencyHandler;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class APIUtility {
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public static final String CB_BASE_URL = "https://api.coinbase.com";
	public static final String CB_PRO_URL = "https://api.pro.coinbase.com";	
	public static final String COINCAP_URL = "api.coincap.io";
	
	public static final String CB_ACCESS_KEY = "CB-ACCESS-KEY";
	public static final String CB_ACCESS_SIGN = "CB-ACCESS-SIGN";
	public static final String CB_ACCESS_TIMESTAMP = "CB-ACCESS-TIMESTAMP";
	public static final String CB_VERSION = "CB-VERSION";
	public static final String CB_ACCESS_PASSPHRASE = "CB-ACCESS-PASSPHRASE";
	
	public static JSONObject requests;
	public static String accessKey;
	public static String secretKey;
	
	public static String proAccessKey;
	public static String proSecretKey;
	public static String proPassphrase;
	
	public static void initialize() {
		getCredentials();
		getAPICalls();
	}
	
	public static Request buildGetRequest(String requestPath, boolean withAuth) {
		String timestamp = getEpochTime();
		String signature = getHMACHeader(secretKey, timestamp, "GET", requestPath, "");
		String url = CB_BASE_URL + requestPath;
		
		System.out.println("Attempting to GET data from " + url);
		
		Request request = null;
		if(withAuth) {
			request = new Request.Builder()
			.addHeader(CB_ACCESS_KEY, accessKey)
			.addHeader(CB_ACCESS_SIGN, signature)
			.addHeader(CB_ACCESS_TIMESTAMP, timestamp)
			.addHeader(CB_VERSION, getDate())
			.addHeader("Accept", "application/json")
			.addHeader("Content-Type", "application/json")
			.url(url)
			.build();
		} else {
			request = new Request.Builder()
					.url(url)
					.build();
		}
		return request;
	}
	
	public static Request buildPostRequest(JSONObject body, String requestPath) {
		
		if(body==null) body = new JSONObject();
		System.out.println(body.toString());
		System.out.println(requestPath);
		String bodyString = body.toString();
		String timestamp = getEpochTime();
		String signature = getHMACHeader(secretKey, timestamp, "POST", requestPath, bodyString);
		
		String url = CB_BASE_URL + requestPath;
		RequestBody rb = RequestBody.create(JSON, body.toString());
		
		System.out.println("Attempting to POST data to " + url);
		
		Request request = new Request.Builder()
				.addHeader(CB_ACCESS_KEY, accessKey)
				.addHeader(CB_ACCESS_SIGN, signature)
				.addHeader(CB_ACCESS_TIMESTAMP, timestamp)
				.addHeader(CB_VERSION, getDate())
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.url(url)
				.post(rb)
				.build();
		return request;
	}
	
	public static Request buildProGetRequest(String requestPath, boolean withAuth) {
		String timestamp = getEpochTime();
		String signature = getHMACHeaderPro(proSecretKey, timestamp, "GET", requestPath, "");
		String url = CB_PRO_URL + requestPath;
		
		System.out.println("Attempting to GET data from " + url);
		
		Request request = null;
		if(withAuth) {
			request = new Request.Builder()
			.addHeader(CB_ACCESS_KEY, proAccessKey)
			.addHeader(CB_ACCESS_SIGN, signature)
			.addHeader(CB_ACCESS_TIMESTAMP, timestamp)
			.addHeader(CB_VERSION, getDate())
			.addHeader(CB_ACCESS_PASSPHRASE, proPassphrase)
			.addHeader("Accept", "application/json")
			.addHeader("Content-Type", "application/json")
			.url(url)
			.build();
		} else {
			request = new Request.Builder()
					.url(url)
					.build();
		}
		return request;
	}
	
	public static Request buildProPostRequest(JSONObject body, String requestPath) {
		if(body==null) body = new JSONObject();
		String bodyString = body.toString();
		String timestamp = getEpochTime();
		String signature = getHMACHeaderPro(proSecretKey, timestamp, "POST", requestPath, bodyString);
		
		String url = CB_PRO_URL + requestPath;
		RequestBody rb = RequestBody.create(JSON, body.toString());
		
		System.out.println("Attempting to POST data to " + url);
		
		Request request = new Request.Builder()
				.addHeader(CB_ACCESS_KEY, proAccessKey)
				.addHeader(CB_ACCESS_SIGN, signature)
				.addHeader(CB_ACCESS_TIMESTAMP, timestamp)
				.addHeader(CB_VERSION, getDate())
				.addHeader(CB_ACCESS_PASSPHRASE, proPassphrase)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type", "application/json")
				.url(url)
				.post(rb)
				.build();
		return request;
	}
	
	public static Request buildCoinCapRequest(String requestPath) {
		String url = "http://" +COINCAP_URL + requestPath + "?limit=250";
		System.out.println("Attempting to GET data from " + url);
		Request request = new Request.Builder()
				.addHeader("Accept-Encoding", "deflate")
				.url(url)
				.build();
		return request;
	}
	
	public static String getHMACHeader(String secretKey, String timestamp, String method, String requestPath, String body) {
    	String prehash = timestamp + method.toUpperCase() + requestPath;
    	
    	if(method.equals("POST") || method.equals("PUT")) {
    		prehash += body;
    	}

    	 SecretKeySpec keyspec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
    	 Mac sha256 = null;
		try {
			sha256 = (Mac) Mac.getInstance("HmacSHA256");
			sha256.init(keyspec);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
		
		String hash = Hex.encodeHexString((sha256.doFinal(prehash.getBytes())));
    	 
    	return hash;
    }
    
    public static String getHMACHeaderPro(String secretKey, String timestamp, String method, String requestPath, String body) {
    	String prehash = timestamp + method.toUpperCase() + requestPath;
    	
    	if(method.equals("POST") || method.equals("PUT")) {
    		prehash += body;
    	}

    	 byte[] secretDecoded = Base64.getDecoder().decode(secretKey);
    	 SecretKey keyspec = new SecretKeySpec(secretDecoded, "HmacSHA256");
    	 Mac sha256 = null;
    	 
    	 try {
    		 sha256 = (Mac) Mac.getInstance("HmacSHA256");
			sha256.init(keyspec);
			
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 return Base64.getEncoder().encodeToString(sha256.doFinal(prehash.getBytes()));
    }
    
    public static String getISOTime() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		return nowAsISO;
	}
	
	public static String getEpochTime() {
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
		proAccessKey = System.getenv("CBPRO_ACCESS_KEY");
		proSecretKey = System.getenv("CBPRO_SECRET_KEY");
		proPassphrase = System.getenv("CBPRO_PASSPHRASE");
	}
	
	public static String getDate() {
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
