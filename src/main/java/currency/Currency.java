package currency;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DoubleNum;

import api.CoinCapAPI;
import backtest.BacktestUtil;
import notifications.NotificationGenerator;
import rule.Trade;

public class Currency {
	
	private String name;
	private String code;
	private double exchangeRate;
	private double amountOwned;
	
	private BarSeries minuteSeries;
	private BarSeries hourSeries;
	private BarSeries daySeries;
	
	private boolean skipSales;
	
	private DecimalFormat df;
	private BacktestUtil bu;
	
	private Trade openTrade;
	
	private CurrencyData data;
	
	private double minTradeValue;
	
	public Currency(String name, String code) {
		this.name = name;
		this.code = code;
		this.bu = new BacktestUtil(this);
	}
	
	public void dataInit(JSONArray min, JSONArray hour, JSONArray day, int size) {
		
		if(min!= null) minuteSeries = buildAndAddBarsFromList(min, this.code, size, "m1");
		if(hour!= null) hourSeries = buildAndAddBarsFromList(hour, this.code, size, "h1");
		if(day!= null) daySeries = buildAndAddBarsFromList(day, this.code, size, "d1");
	}
	
	public void updateBar(String interval) {
		int duration = 1000;
		if(interval.equals("m1")) {
			 duration *= 60;
		 } else if(interval.equals("h1")) {
			 duration *= (60 * 60);
		 } else {
			 duration *= (24 * 60 * 60);
		 }
		
		long now = System.currentTimeMillis();
		int minus = (int)(duration *1.5);
		JSONObject lastEntry = new JSONObject();
		if(!this.isStable()) {
			JSONArray dat = CoinCapAPI.getIntervalRate(this.getCCName(), interval, now-(minus));
			lastEntry = dat.getJSONObject(dat.length()-1);
		}
		addJSONObjectToBar(lastEntry, interval);
	}
	
	public void dataUpdate(double exchangeRate, double amountOwned) {
		this.exchangeRate = exchangeRate;
		this.amountOwned = amountOwned;
	}
	
	/*
	public void setUserPreferences(double targetMark, double threshold, String tag) {
		if(tag.equalsIgnoreCase(Type.IGNORED.toString())) this.tag=Type.IGNORED;
		else if(tag.equalsIgnoreCase(Type.WATCHED.toString())) this.tag=Type.WATCHED;
		else if(tag.equalsIgnoreCase(Type.SHORT_TERM.toString())) this.tag=Type.SHORT_TERM;
		else if(tag.equalsIgnoreCase(Type.LONG_TERM.toString())) this.tag=Type.LONG_TERM;
		else if(this.tag == null) this.tag=Type.IGNORED;
	}
	*/
	
	/*
	public double validScalpValue(double value) {
		if((exchangeRate*minTradeValue) > value){
			value = exchangeRate*minTradeValue;
		}
		return value;
	}
	
	public boolean canTrade(String trade) {
		String tradeID = this.code + "-" + trade;
		return availableTrades.contains(tradeID);
	}
	
	public boolean shortTermTrade() {
		if((!(this.code.equals("USD") || this.code.equals("USDC")))) {
			boolean t = ((this.tag.equals(Type.SHORT_TERM)) && (this.getFiatValue() > this.getScalpTradeValue()));
			//System.out.println("Fiat: " + this.getFiatValue() + ", scalp: " + this.getScalpTradeValue());
			return t;
		} else return false;
	}
	
	public boolean stopOrderSell() {
		if((!(this.code.equals("USD") || this.code.equals("USDC")))) {
			return ((this.tag.equals(Type.SHORT_TERM)) && (this.getFiatValue() < ((this.data.getInitial() * (1-CurrencyHandler.AUTO_SELL)))));
		} else return false;
	}
	
	public void addTrade(String tradeID, double minValue, String minIncrement) {
		if(!availableTrades.contains(tradeID)) availableTrades.add(tradeID);
		this.minTradeValue = minValue;
		minIncrement = minIncrement.replace(".", "");
		minIncrement = minIncrement.split("1")[0];
		int l = minIncrement.length();
		String reg = "#####0";
		if(l > 0) reg += ".";
		for(int i = 0; i < l; i++) {
			reg+="#";
		}
		
		this.df = new DecimalFormat(reg);
		this.df.setRoundingMode(RoundingMode.DOWN);
	}
	*/
	
	private void addJSONObjectToBar(JSONObject j, String interval) {
		Duration d;
		 if(interval.equals("m1")) {
			 d = Duration.ofMinutes(1);
		 } else if(interval.equals("h1")) {
			 d = Duration.ofHours(1);
		 } else {
			 d = Duration.ofDays(1);
		 }
		Instant time = Instant.ofEpochMilli(j.getLong("period"));
   	 	ZonedDateTime zdt = ZonedDateTime.ofInstant(time, ZoneOffset.UTC);
   	 	zdt = zdt.plus(d);
   	 	String open = j.getString("open");
   	 	String high = j.getString("high");
   	 	String low = j.getString("low");
   	 	String close = j.getString("close");
   	 	String volume = j.getString("volume");
   	 	Bar nextBar = new BaseBar(d, zdt, open, high, low, close, volume);
   	 	
   	 	if(interval.equals("m1")) {
   	 		minuteSeries.addBar(nextBar, false);
   	 	} else if(interval.equals("h1")) {
   	 		hourSeries.addBar(nextBar, false);
   	 	} else {
   	 		daySeries.addBar(nextBar, false);
   	 	}
	}
	
	 private static BarSeries buildAndAddBarsFromList(JSONArray arr, String name, int size, String interval) {
	        // Store Bars in a list and add them later. The bars must have the same Num type
		 
		 if(size > arr.length()) size = arr.length();
		 if(size > 0) {
		 System.out.println("building bars for " + name + "-" + interval + " with size " + size);
		 Duration d;
		 if(interval.equals("m1")) {
			 d = Duration.ofMinutes(1);
		 } else if(interval.equals("h1")) {
			 d = Duration.ofHours(1);
		 } else {
			 d = Duration.ofDays(1);
		 }
		 ZonedDateTime endTime = ZonedDateTime.now();
	     ArrayList<Bar> bars = new ArrayList<Bar>();
	     for(int i = 0; i < size; i++) {
	    	 JSONObject current = arr.getJSONObject(i);
	    	 Instant time = Instant.ofEpochMilli(current.getLong("period"));
	    	 ZonedDateTime zdt = ZonedDateTime.ofInstant(time, ZoneOffset.UTC);
	    	 zdt = zdt.plus(d);
	    	 String open = current.getString("open");
	    	 String high = current.getString("high");
	    	 String low = current.getString("low");
	    	 String close = current.getString("close");
	    	 String volume = current.getString("volume");
	    	 //Num
	    	 Bar nextBar = new BaseBar(d, zdt, open, high, low, close, volume);
	    	 bars.add(nextBar);
	     }

	     return new BaseBarSeriesBuilder().withName(name+"_"+interval).withNumTypeOf(DoubleNum::valueOf).withMaxBarCount(size)
	                .withBars(bars).build();
		 } else return null;
	    }
	
	public double formatToStandard(double val) {
		return Double.parseDouble(df.format(val));
	}
	
	public String getCCName() {
		return this.name.toLowerCase().replace(" ", "-");
	}
	
	public boolean isStable() {
		return (code.equals("USD") || code.equals("USDT") || code.equals("USDC") || code.equals("BUSD") || code.equals("DAI"));
	}
	
	public BarSeries getSeries(String interval) {
		BarSeries bs = null;
		if(interval.equals("m1")) {
			bs = minuteSeries;
		}
		else if(interval.equals("h1")) {
			bs = hourSeries;
		}
		else if(interval.equals("d1")) {
			bs = daySeries;
		}
		return bs;
	}
	
	public void setTrade(double price) {
		this.openTrade = new Trade(price);
	}
	
	public void checkHigh(double price) {
		if(openTrade != null) {
			openTrade.checkForHigh(price);
		}
	}
	
	public void endTrade() {
		this.openTrade = null;
	}
	
	public Trade getOpenTrade() {
		return this.openTrade;
	}
	/*
	public void setCurrencyData(double initialOwned) {
		this.data = new CurrencyData(initialOwned);
	}
	
	public CurrencyData getData() {
		return this.data;
	}
	
	public double getInitial() {
		return this.data.getInitial();
	}
	
	public void updateInitial(double change) {
		System.out.println(this.code + " " + change);
		data.setInitialInvest(this.getFiatValue(), change);
		this.skipSales = true;
	}
	
	public void setSale(boolean sale) {
		this.skipSales = sale;
	}
	*/
	public boolean addOnReport() {
		return this.data.getConverted() > 0;
	}
	
	public String messageToAdd() {
		String dat = this.code + " added $" + this.data.getConverted() + " USD";
		NotificationGenerator.addValue(this.data.getConverted());
		this.data.reset();
		return dat;
	}
	
	public double getExchangeRate() {
		return minuteSeries.getBar(minuteSeries.getEndIndex()).getClosePrice().doubleValue();
	}
	
	public double getAmountOwned() {
		return this.amountOwned;
	}
	
	/*
	public double getScalpTrade() {
		System.out.println(code + ": " + this.amountOwned + " - " + convertFromFiat(this.data.getInitial()));
		return this.amountOwned - convertFromFiat(this.data.getInitial());
	}
	
	public double getScalpTradeQuote() {
		return this.getFiatValue() - this.data.getInitial();
	}
	
	/*
	public double getThreshold() {
		return this.threshold[0];
	}
	*/
	
	public double getFiatValue() {
		return (this.exchangeRate * this.amountOwned);
	}
	
	public double getScalpTradeValue() {
		return ((this.data.getInitial())  + (this.minTradeValue * this.exchangeRate));
	}
	
	public double convertFromFiat(double fiatValue) {
		 return Double.parseDouble(df.format(fiatValue/this.exchangeRate));
	}
	
	/**
	 * @return The minimum trade value in the native currency
	 */
	/*
	public double getMinTradeValue() {
		return Double.parseDouble(df.format(minTradeValue));
	}
	*/
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setAmountOwned(double am) {
		this.amountOwned = am;
	}
	
	public void setExchangeRate(double rate) {
		this.exchangeRate = rate;
	}
	
	public BacktestUtil getBacktestCapabilities() {
		return this.bu;
	}
	
	public Currency copy() {
		return new Currency(this.code, this.name)
				.copyAmountUtil(this.amountOwned, this.exchangeRate)
				.copySeries(this.minuteSeries, this.hourSeries, this.daySeries);
	}
	
	private Currency copyAmountUtil(double amountOwned, double exchangeRate) {
		this.setAmountOwned(amountOwned);
		this.setExchangeRate(exchangeRate);
		return this;
	}
	
	private Currency copySeries(BarSeries min, BarSeries hour, BarSeries day) {
		this.minuteSeries = min;
		this.hourSeries = hour;
		this.daySeries = day;
		return this;
	}

}
