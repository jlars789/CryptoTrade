package backtest;

import currency.Currency;

public class BacktestUtil {
	
	private Currency c;
	
	public BacktestUtil(Currency c) {
		this.c = c;
	}
	
	public double buy(double amount) {
		c.setAmountOwned(c.getAmountOwned() + amount);
		c.setTrade(amount);
		return amountWithFees(amount);
	}
	
	public void sell(double amount) {
		c.setAmountOwned(c.getAmountOwned() - amountWithFees(amount));
	}
	
	public double sellAll() {
		double prev = amountWithFees(c.getAmountOwned());
		c.setAmountOwned(0);
		c.endTrade();
		return prev;
	}
	
	private double amountWithFees(double amount) {
		return amount - (amount * 0.001);
	}

}
