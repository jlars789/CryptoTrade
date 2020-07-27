package currency;

public class CurrencyData {
	
	//Fiat value
	private double initialInvestment;
	
	private double toUSD;
	

	public CurrencyData(double initialOwned) {
		this.initialInvestment = initialOwned;
	}
	
	public void setInitialInvest(double initial, double change) {
		this.initialInvestment = initial + change;
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

}
