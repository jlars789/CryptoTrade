package currency;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Portfolio {
	
	private static final DecimalFormat df = new DecimalFormat("###.00");
	
	public Currency[] currencyList;
	private int currentIndex;
	private double USD;

	public Portfolio(double initial) {
		currencyList = CurrencyHandler.currencyArray;
		//currencyList[CurrencyHandler.getIndexByCode("USDT")].setAmountOwned(initial);
		USD = initial;
	}
	
	public void buyCurrency(String code, double amount) {
		double rate = currencyList[CurrencyHandler.getIndexByCode(code)].
				getSeries("h1").getBar(currentIndex).getClosePrice().doubleValue();
		double converted = amount / rate;
		USD -= amount;
		System.out.print("Bought " + code + " for " + df.format(amount) + ", USD in account: $" + USD);
		System.out.println(" at " + atTime() + " at $" + df.format(rate) + " per " + code);
		currencyList[CurrencyHandler.getIndexByCode(code)].getBacktestCapabilities().buy(converted);
	}
	
	public boolean canBuy(String code, double amount) {
		return ((getAmount(code) == 0) && (USD >= amount));
	}
	
	public void sellAllCurrency(String code) {
		double amount = getFiatAmount(code);
		double rate = currencyList[CurrencyHandler.getIndexByCode(code)].
				getSeries("h1").getBar(currentIndex).getClosePrice().doubleValue();
		currencyList[CurrencyHandler.getIndexByCode(code)].getBacktestCapabilities().sellAll();
		USD += amount;
		System.out.print("Sold all " + code + " for " + df.format(amount) + ", USD in account: $" + USD);
		System.out.println(" at " + atTime() +", profit $" + (amount-303.030303) + " | at $" + df.format(rate));
	}
	
	public void updateCurrency(String currency, double price) {
		currencyList[CurrencyHandler.getIndexByCode(currency)].checkHigh(price);
	}
	
	public void setIndex(int index) {
		this.currentIndex = index;
	}
	
	private ZonedDateTime atTime() {
		return currencyList[CurrencyHandler.getIndexByCode("BTC")].getSeries("h1").getBar(currentIndex).getEndTime();
	}
	
	public double getAmount(String code) {
		return currencyList[CurrencyHandler.getIndexByCode(code)].getAmountOwned();
	}
	
	public double getFiatAmount(String code) {
		double rate = currencyList[CurrencyHandler.getIndexByCode(code)].getSeries("h1").getBar(currentIndex).getClosePrice().doubleValue();
		return getAmount(code) * rate;
	}
	
	public double getTotal() {
		double total = 0;
		for(int i = 0 ; i < currencyList.length; i++) {
			double owned = currencyList[i].getAmountOwned();
			if(!CurrencyHandler.isExcluded(i) && 
					!CurrencyHandler.getCurrencyByCode(CurrencyHandler.getCodeByIndex(i)).isStable()) {
			total += getFiatAmount(currencyList[i].getCode());
			}
		}
		return total + USD;
	}
	
	public void displayAll(int index) {
		System.out.println("Portfolio Summary: ");
		for(int i = 0; i < currencyList.length; i++) {
			double owned = currencyList[i].getAmountOwned();
			if(!CurrencyHandler.isExcluded(i) && 
					!CurrencyHandler.getCurrencyByCode(CurrencyHandler.getCodeByIndex(i)).isStable()) {
			double rate = currencyList[i].getSeries("h1").getBar(index).getClosePrice().doubleValue();
			System.out.println(currencyList[i].getCode() + ": $" + (owned * rate));
			}
		}
		System.out.println("USD: $" + USD);
		System.out.println("Total: $" + getTotal());
		System.out.println("Profits: $" + (getTotal() - 10000));
	}
	
}
