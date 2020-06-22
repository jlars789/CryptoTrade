package currency;

import java.text.DecimalFormat;
import java.util.ArrayList;

import notifications.NotificationGenerator;

public class Currency {
	
	private String name;
	private String code;
	private double exchangeRate;
	private double amountOwned;
	private double percentChangeDay;
	
	private String ID;
	private DecimalFormat df;
	
	enum Type {
		IGNORED, WATCHED, SHORT_TERM, LONG_TERM
	}
	
	//private double targetMark;
	//private double[] threshold = new double[2];
	private Type tag;
	
	private CurrencyData data;
	
	private ArrayList<String> availableTrades;
	private double minTradeValue;
	
	public Currency(String name, String code, String ID) {
		this.name = name;
		this.code = code;
		this.ID = ID;
		this.availableTrades = new ArrayList<String>();
		
	}
	
	public void dataInit(double exchangeRate, double initialOwned, double percentChangeDay) {
		dataUpdate(exchangeRate, initialOwned, percentChangeDay);
		setCurrencyData(initialOwned);
	}
	
	public void dataUpdate(double exchangeRate, double amountOwned, double percentChangeDay) {
		this.exchangeRate = exchangeRate;
		this.amountOwned = amountOwned;
		this.percentChangeDay = percentChangeDay;
	}
	
	public void setUserPreferences(double targetMark, double threshold, String tag) {
		if(tag.equalsIgnoreCase(Type.IGNORED.toString())) this.tag=Type.IGNORED;
		else if(tag.equalsIgnoreCase(Type.WATCHED.toString())) this.tag=Type.WATCHED;
		else if(tag.equalsIgnoreCase(Type.SHORT_TERM.toString())) this.tag=Type.SHORT_TERM;
		else if(tag.equalsIgnoreCase(Type.LONG_TERM.toString())) this.tag=Type.LONG_TERM;
		else if(this.tag == null) this.tag=Type.IGNORED;
		/*
		this.targetMark = targetMark;
		this.threshold[0] = validScalpValue(threshold);
		this.threshold[1] = validScalpValue(threshold);
		*/
	}
	
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
		if(!(this.code.equals("USD") || this.code.equals("USDC"))) {
			boolean t = ((this.tag.equals(Type.SHORT_TERM)) && (this.getFiatValue() > this.getScalpTradeValue()));
			return t;
		} else return false;
	}
	
	public boolean stopOrderSell() {
		if(!(this.code.equals("USD") || this.code.equals("USDC"))) {
			return ((this.tag.equals(Type.SHORT_TERM)) && (this.getFiatValue() < ((this.data.getInitial() * this.getExchangeRate()) * (1-CurrencyHandler.AUTO_SELL))));
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
	}
	
	public double formatToStandard(double val) {
		return Double.parseDouble(df.format(val));
	}
	
	public void setCurrencyData(double initialOwned) {
		this.data = new CurrencyData(initialOwned);
	}
	
	public CurrencyData getData() {
		return this.data;
	}
	
	public void updateInitial(double change) {
		data.setInitialInvest(this.amountOwned, change);
	}
	
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
		return this.exchangeRate;
	}
	
	public double getAmountOwned() {
		return this.amountOwned;
	}
	
	public double getScalpTrade() {
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
		return ((this.data.getInitial() * this.exchangeRate)  + (this.minTradeValue * this.exchangeRate));
	}
	
	public double convertFromFiat(double fiatValue) {
		 return Double.parseDouble(df.format(fiatValue/this.exchangeRate));
	}
	
	public double getMinTradeValue() {
		return Double.parseDouble(df.format(minTradeValue));
	}
	
	public double getChange() {
		return this.percentChangeDay;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public Type getTag() {
		return this.tag;
	}

}
