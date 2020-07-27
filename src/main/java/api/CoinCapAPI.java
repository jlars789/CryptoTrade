package api;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.Response;

public class CoinCapAPI {

	
	public static JSONArray getRatesFromDate(String name, String interval, ZonedDateTime start, ZonedDateTime end) {
		long startUnix = start.toEpochSecond() * 1000;
		final long finEnd = end.toEpochSecond() * 1000;
		long endUnix = finEnd;
		int loops = (int) Math.ceil(Duration.between(start, end).getSeconds()/(86400 * 30));
		long adder = ((86400L * 30) * 1000L);
		JSONArray builderArray = new JSONArray();
		
		if(loops <= 1) {
			builderArray = getIntervalRate(name, interval, startUnix, endUnix);
		} else {
			endUnix = startUnix + adder;
			for(int i = 0; i < loops+1; i++) {
				System.out.println(LocalDateTime.ofEpochSecond(endUnix/1000, 0, ZoneOffset.UTC));
				JSONArray latestPull = getIntervalRate(name, interval, startUnix, endUnix);
				for(int j = 0; j < latestPull.length(); j++) {
					JSONObject jo = latestPull.getJSONObject(j);
					builderArray.put(jo);
				}
				
				startUnix = endUnix + 2000;
				if(i < loops-1) {
					endUnix = startUnix+adder;
				} else {
					endUnix = finEnd;
				}
			}
		}
		/*
		if(Duration.between(start, end).getSeconds() > (86400 * 30)) {
			 endUnix = startUnix + adder;
			 System.out.println(startUnix + ", " + endUnix + " ," + adder);
		}
		
		JSONArray ja = getIntervalRate(name, interval, startUnix, endUnix);
		startUnix = endUnix;
		endUnix = startUnix + adder;
		if(Duration.between(start, end).getSeconds() > (86400 * 30)) {
			JSONArray fin = new JSONArray();
			JSONArray temp = getIntervalRate(name, interval, startUnix, endUnix);
			for(int i = 0 ; i < ja.length() + temp.length(); i++) {
				JSONObject j = null;
				if(i < ja.length()) {
					j = ja.getJSONObject(i);
				} else {
					j = temp.getJSONObject(i - ja.length());
				}
				System.out.println(j.toString());
				fin.put(j);
			}
			return fin;
		}
		return ja;
		*/
		return builderArray;
		
	} 
	
	public static JSONArray getRatesFromDate(String name, String interval, ZonedDateTime start) {
		return getRatesFromDate(name, interval, start, ZonedDateTime.now());
	}
	
	public static JSONArray getIntervalRate(String name, String interval, long start, long end) {
		long nowUnix = System.currentTimeMillis();
		Request request = buildCoinCapRequest(name, interval, start, end);
		
		Response res=null;
		JSONArray arr = null;
		try {
			res = APICommunicator.sendRequest(request);
			JSONObject par = new JSONObject(res.body().string());
			arr = par.getJSONArray("data");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	public static JSONArray getIntervalRate(String name, String interval, long start) {
		long nowUnix = System.currentTimeMillis();
		return getIntervalRate(name, interval, start, nowUnix);
	}
	
	public static JSONArray getHistoricalRate(String name, String interval) {
		Request request = buildCoinCapRequest(name, interval, 0, 0);
		
		Response res=null;
		JSONArray arr = null;
		try {
			res = APICommunicator.sendRequest(request);
			JSONObject par = new JSONObject(res.body().string());
			arr = par.getJSONArray("data");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	public static Request buildCoinCapRequest(String name, String interval, long startTime, long endTime) {
		//"https://api.coincap.io/v2/candles?exchange=binance&interval=m1&baseId=ethereum&quoteId=tether";
		String url ="";
		if(startTime == endTime) {
			url = "https://api.coincap.io/v2/candles?exchange=binance&interval="+ 
				interval + "&baseId=" + name + "&quoteId=tether"; 
		} else {
			url = "https://api.coincap.io/v2/candles?exchange=binance&interval="+ 
					interval + "&baseId=" + name + "&quoteId=tether&start="+ startTime + "&end=" + endTime; 
		}
		System.out.println("Attempting to GET data from " + url);
		Request request = new Request.Builder()
				.addHeader("Accept-Encoding", "deflate")
				.url(url)
				.build();
		return request;
	}
	
}
