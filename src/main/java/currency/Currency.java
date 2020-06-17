package currency;

import java.util.ArrayList;

public class Currency {
	
	private String name;
	private String code;
	private double exchangeRate;
	private double amountOwned;
	private double percentChangeDay;
	
	private String ID;
	
	enum Type {
		IGNORED, WATCHED, SHORT_TERM, LONG_TERM
	}
	
	private double targetMark;
	private double[] threshold = new double[2];
	private Type tag;
	
	private double initialOwned;
	
	private ArrayList<String> availableTrades;
	private double minTradeValue;
	
	public Currency(String name, String code, String ID) {
		this.name = name;
		this.code = code;
		this.ID = ID;
		this.availableTrades = new ArrayList<String>();
		
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
		
		this.targetMark = targetMark;
		this.threshold[0] = validScalpValue(threshold);
		this.threshold[1] = validScalpValue(threshold);
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
		return ((this.tag.equals(Type.SHORT_TERM)) && (this.getFiatValue() > this.getScalpTradeValue()));
	}
	
	public void addTrade(String tradeID, double minValue) {
		if(!availableTrades.contains(tradeID)) availableTrades.add(tradeID);
		this.minTradeValue = minValue;
	}
	
	public void setUserData(double initialOwned) {
		this.initialOwned = initialOwned;
	}
	
	public double getExchangeRate() {
		return this.exchangeRate;
	}
	
	public double getAmountOwned() {
		return this.amountOwned;
	}
	
	public double getScalpTrade() {
		return this.getFiatValue() - this.targetMark;
	}
	
	public double getThreshold() {
		return this.threshold[0];
	}
	
	public double getFiatValue() {
		return (this.exchangeRate * this.amountOwned);
	}
	
	public double getScalpTradeValue() {
		return (this.targetMark + this.threshold[0]);
	}
	
	public double convertFromFiat(double fiatValue) {
		 return fiatValue/this.exchangeRate;
	}
	
	public double getMinTradeValue() {
		return minTradeValue;
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
