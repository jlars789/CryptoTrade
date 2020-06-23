package currency;

public class CurrencyData {
	
	//Fiat value
	private double initialInvestment;
	
	private double[] extrema = new double[2];
	private double average;
	private double toUSD;

	public CurrencyData(double initialOwned) {
		this.initialInvestment = initialOwned;
	}
	
	public void setInitialInvest(double initial, double change) {
		this.initialInvestment = initial + change;
	}
	
	public void setExtrema(double high, double low) {
		this.extrema[0] = high;
		this.extrema[1] = low;
	}
	
	public void setAverage(double open, double close) {
		double sum = open+close;
		this.average = sum/2;
	}
	
	public double getInitial() {
		return this.initialInvestment;
	}
	
	/*
	public double getInitialFiat() {
		return this.initialInvestment * this.initialExchangeRate;
	}
	*/
	
	public void addOrder(double trade) {
		this.toUSD += trade;
	}
	
	public void soldAll(double fin) {
		double loss = fin - this.initialInvestment;
		addOrder(loss);
	}
	
	public double getConverted() {
		return this.toUSD;
	}
	
	public void reset() {
		this.toUSD = 0;
	}
	
	public double getEccentricity() {
		double numerator = extrema[0] - extrema[1];
		return numerator/average;
	}

}
