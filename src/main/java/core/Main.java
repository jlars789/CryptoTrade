package core;


import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import aws.S3Upload;
//import aws.S3Upload;
import coinbase.APICallBuilder;
import coinbase.APIUtility;
import coinbase.pro.Orders;
import currency.Currency;
import currency.CurrencyHandler;
import threads.CurrencyRefresh;

public class Main {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		LocalDateTime init = LocalDateTime.now();
		
		startUp();
		LocalDateTime startUp = LocalDateTime.now();
		System.out.println("Setup time to complete: " + ((float)(Duration.between(init, startUp).toMillis())/1000) + "s");
		
		testFunction();
		LocalDateTime test = LocalDateTime.now();
		System.out.println("Test time to complete: " + ((float)(Duration.between(startUp, test).toMillis())/1000) + "s");
		
		
		
		
		long duration[] = getTimeTo();
		//15 second based operations
		ScheduledExecutorService quMinuteBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> quMinuteScheduler = quMinuteBasedOperator.scheduleAtFixedRate(new CurrencyRefresh(), duration[0], 15000, TimeUnit.MILLISECONDS);
		System.out.println("Passed");
		
		//minute based operations
		ScheduledExecutorService minuteBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> minuteScheduler = minuteBasedOperator.scheduleAtFixedRate(null, duration[1], 60000, TimeUnit.MILLISECONDS);
		
		//5 minute based operations
		ScheduledExecutorService fiveMinuteBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> fiveMinuteScheduler = fiveMinuteBasedOperator.scheduleAtFixedRate(null, duration[2], 300000, TimeUnit.MILLISECONDS);
		
		//15 minute based operations
		ScheduledExecutorService quHourBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> quHourScheduler = quHourBasedOperator.scheduleAtFixedRate(null, duration[3], 900000, TimeUnit.MILLISECONDS);
		
		//Hour based operations
		ScheduledExecutorService hourBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> hourScheduler = hourBasedOperator.scheduleAtFixedRate(null, duration[4], 3600000, TimeUnit.MILLISECONDS);
		
		//Daily scheduler
		ScheduledExecutorService dayBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> dayScheduler = dayBasedOperator.scheduleAtFixedRate(null, duration[5], 86400000, TimeUnit.MILLISECONDS);
		
		//Daily scheduler
		ScheduledExecutorService weekBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> weekScheduler = weekBasedOperator.scheduleAtFixedRate(null, duration[6], 604800000, TimeUnit.MILLISECONDS);

	}
	
	private static void testFunction() {
		
	}
	
	private static void startUp() {
		APIUtility.initialize();
		CurrencyHandler.initialize();
		CurrencyHandler.updateValues();
	}
	
	public static long[] getTimeTo() {
		
		LocalDateTime start = LocalDateTime.now();
		
		LocalDateTime quMinuteEnd = start.truncatedTo(ChronoUnit.MINUTES);
		long secondAdd = 15 - (start.getSecond()%15);
		quMinuteEnd = quMinuteEnd.plusSeconds(secondAdd);
		
		LocalDateTime minuteEnd = start.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
		
		LocalDateTime fiveMinuteEnd = start.plusMinutes(5-(start.getMinute()%5)).truncatedTo(ChronoUnit.MINUTES);
		
		LocalDateTime quHourEnd = start.plusMinutes(15-(start.getMinute()%15)).truncatedTo(ChronoUnit.MINUTES);
		
		LocalDateTime hourEnd = start.plusHours(1).truncatedTo(ChronoUnit.HOURS);
		
		LocalDateTime dayEnd = start.plusDays(1).truncatedTo(ChronoUnit.DAYS);
		
		int getDay = start.getDayOfWeek().getValue();
		
		LocalDateTime weekEnd = start.plusDays(7-((getDay+1)%7)).truncatedTo(ChronoUnit.DAYS);
	    
	    long duration[] = new long[7]; 
	    duration[0] = Duration.between(start, quMinuteEnd).toMillis();
	    duration[1] = Duration.between(start, minuteEnd).toMillis();
	    duration[2] = Duration.between(start, fiveMinuteEnd).toMillis();
	    duration[3] = Duration.between(start, quHourEnd).toMillis();
	    duration[4] = Duration.between(start, hourEnd).toMillis();
	    duration[5] = Duration.between(start, dayEnd).toMillis();
	    duration[6] = Duration.between(start, weekEnd).toMillis();
	    
	    return duration;
	}
}
