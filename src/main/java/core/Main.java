package core;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import api.APIUtility;
import aws.S3Upload;
import backtest.Backtest;
import coinbase.pro.APICallPro;
import currency.CurrencyHandler;
import threads.CurrencyRefresh;
import threads.Reporter;

public class Main {
	//https://github.com/jlars789/CryptoTrade
	public static void main(String[] args) {
		
		LocalDateTime init = LocalDateTime.now();
		int size = 120;
		startUp();
		if(args[0].equals("backtest")) {
			if(args.length > 1) {
				size = Integer.parseInt(args[2]);
				CurrencyHandler.initialize(size);
				//CurrencyHandler.runBacktestSuite(args[1], Integer.parseInt(args[2]));
				Backtest.runSimultaneousBacktest(10000);
			} else {
				size = 8000;
				CurrencyHandler.initialize(size);
				//CurrencyHandler.runBacktestSuite("h1", size);
				Backtest.runSimultaneousBacktest(10000);
			}
		} else {
		CurrencyHandler.initialize(size);
		LocalDateTime startUp = LocalDateTime.now();
		System.out.println("Setup time to complete: " + ((float)(Duration.between(init, startUp).toMillis())/1000) + "s");
		
		//testFunction();
		LocalDateTime test = LocalDateTime.now();
		System.out.println("Test time to complete: " + ((float)(Duration.between(startUp, test).toMillis())/1000) + "s");
		System.out.println("Memory used: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000 + "MB");
		scheduledServiceCreator();
		}

	}
	
	private static void testFunction() {
		Connection dbConn = getRemoteConnection();
		if(dbConn == null) return;
		//String query = "SELECT * from Hourly WHERE time > " + shaveToYesterday();
		String query  = "SELECT * from Hourly";
		PreparedStatement pst;
		try {
			pst = dbConn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = pst.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			rs.afterLast();
			rs.previous();
			for(int i = 0; i < 10; i++) {
				int offset = 0;
				for(int j = 0; j < CurrencyHandler.getCurrencySize()-1; j++) {
					System.out.print(rsmd.getColumnLabel(j+2) + " " + rs.getDouble(j+2));
					if(CurrencyHandler.getCodeByIndex(j).equals("COMP")) offset++;
					System.out.println(", " + CurrencyHandler.getCodeByIndex(j+offset));
				}
				System.out.println("");
				rs.previous();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void startUp() {
		APIUtility.initialize();
		//CurrencyHandler.initialize();
	}
	
	public static final void scheduledServiceCreator() {
		long duration[] = getTimeTo();
		
		//15 second based operations
		//System.out.println(duration[0]);
		CurrencyRefresh p = new CurrencyRefresh();
		ScheduledExecutorService quMinuteBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> quMinuteScheduler = quMinuteBasedOperator.scheduleAtFixedRate(p, 0, 15000, TimeUnit.MILLISECONDS);
		//System.out.println(quMinuteScheduler.getDelay(TimeUnit.MILLISECONDS));
		
		/*
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
		*/
		//Daily scheduler
		ScheduledExecutorService dayBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> dayScheduler = dayBasedOperator.scheduleAtFixedRate(new Reporter(), duration[5], 86400000, TimeUnit.MILLISECONDS);
		/*
		//Weekly scheduler
		ScheduledExecutorService weekBasedOperator = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> weekScheduler = weekBasedOperator.scheduleAtFixedRate(null, duration[6], 604800000, TimeUnit.MILLISECONDS);
		*/
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
	
	public static Connection getRemoteConnection() {
		try {
	      Class.forName("com.mysql.cj.jdbc.Driver");
	      String dbName = "crypto";
	      String userName = System.getenv("RDS_USER");
	      String password = System.getenv("RDS_PASSWORD");
	      String hostname = System.getenv("RDS_HOSTNAME");
	      String port = "3306";
	      String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
	      System.out.println("Attempting to connect to MySQL Database...");
	      Connection con = DriverManager.getConnection(jdbcUrl);
	      return con;
	    } catch (ClassNotFoundException e) { 
	    	e.printStackTrace();
	    }
	    catch (SQLException e) { 
	    	e.printStackTrace();
	    }
	    return null;
	}
	    
}
