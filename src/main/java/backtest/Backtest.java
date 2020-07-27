package backtest;

import java.util.Scanner;

import org.ta4j.core.AnalysisCriterion;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.Strategy;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.Order.OrderType;
import org.ta4j.core.analysis.criteria.TotalProfitCriterion;

import currency.Currency;
import currency.CurrencyHandler;
import currency.Portfolio;

public class Backtest {
	
	public static Portfolio pf;
	
	public static double profitTotal = 0;
	public static int entries;
	public static int tradesMade;

	public static void runBacktestSuite(Currency[] currencyArray, String interval, int duration) {
		for(int i = 0; i < currencyArray.length; i++) {
			if(!currencyArray[i].isStable()) {
				AnalysisCriterion criterion = new TotalProfitCriterion();
				String code = currencyArray[i].getCode();
				TradingRecord r = runSingleBacktest(interval, duration, code);
				double fees = 0;
				double closingPrice = currencyArray[i].getSeries(interval).
						getBar(currencyArray[i].getSeries(interval).getEndIndex()).getClosePrice().doubleValue();
				for(int j = 0 ; j < r.getTrades().size(); j++) {
					Trade t = r.getTrades().get(j);
					System.out.print(t);
					System.out.print(" profit " + (t.getProfit().doubleValue()));
					System.out.println(" fees " + t.getExit().getValue().doubleValue() * 0.001 + "");
					fees += ((t.getExit().getValue().doubleValue() * 0.001)/t.getExit().getValue().doubleValue());
					if(t.isOpened()) {
						System.out.println("Held Asset: ");
						System.out.println(t.getEntry() + ", current: " + closingPrice);
					}
				}
				double profit = criterion.calculate(CurrencyHandler.getCurrencyByCode(code).getSeries(interval), r).doubleValue() - fees;
				profitTotal += (profit-1);
				entries++;
				System.out.println(code + " profit: " + profit);
				System.out.println("");
			}
		}
		double profitPercent = ((profitTotal)*100);
		//System.out.println("Average profit: " + ((profitTotal/entries)-1) + "%");
		System.out.println("Total profit: " + profitPercent + "%");
		System.out.println("Ending amount: $" + ((profitTotal+1)));
	}
	
	private static TradingRecord runSingleBacktest(String interval, int duration, String currency) {
		BarSeriesManager seriesManager = new BarSeriesManager(CurrencyHandler.getCurrencyByCode(currency).getSeries(interval));
		Strategy strategy = CurrencyHandler.buildStrategy(CurrencyHandler.getCurrencyByCode(currency).getSeries(interval), currency);
		TradingRecord results = seriesManager.run(strategy, OrderType.BUY);
		return results;
	}
	
	public static void runSimultaneousBacktest(double initialCapital) {
		pf = new Portfolio(initialCapital);
		double amount = initialCapital/(CurrencyHandler.getCurrencySize()-1);
		Strategy[] strategy = new Strategy[CurrencyHandler.getCurrencySize()];
		for(int i = 0; i < CurrencyHandler.getCurrencySize(); i++) {
			if(!CurrencyHandler.isExcluded(i) && !CurrencyHandler.getCurrencyByCode(CurrencyHandler.getCodeByIndex(i)).isStable()) {
				//System.out.println(CurrencyHandler.getCodeByIndex(i));
				strategy[i] = CurrencyHandler.buildStrategy(CurrencyHandler.getCurrencyByCode(CurrencyHandler.getCodeByIndex(i)).
					getSeries("h1"), CurrencyHandler.getCodeByIndex(i));
			}
			
		}
			
		//Strategy strategy = 
		for(int i = 0; i < CurrencyHandler.getCurrencyByCode("BTC").getSeries("h1").getEndIndex()+1; i++) {
			pf.setIndex(i);
			//System.out.println(pf.currencyList[CurrencyHandler.getIndexByCode("BTC")].getSeries("h1").getBar(i).getEndTime());
			for(int j = 0; j < strategy.length; j++) {
				if(!CurrencyHandler.isExcluded(j) && 
						!CurrencyHandler.getCurrencyByCode(CurrencyHandler.getCodeByIndex(j)).isStable()) {
					runOneBlock(CurrencyHandler.getCodeByIndex(j), strategy[j], i, amount);
				}
			}
		}
		pf.displayAll(CurrencyHandler.getCurrencyByCode("BTC").getSeries("h1").getEndIndex());
	}
	
	private static void runOneBlock(String currency, Strategy strategy, int index, double amount) {
		double high = CurrencyHandler.getCurrencyByCode(currency).getSeries("h1").getBar(index).getHighPrice().doubleValue();
		pf.updateCurrency(currency, high);
		if(strategy.getEntryRule().isSatisfied(index) && pf.canBuy(currency, amount)) {
			pf.buyCurrency(currency, amount);
		}
		else if(strategy.shouldExit(index) && pf.getAmount(currency) > 0) {
			pf.sellAllCurrency(currency);
		}
	}
	
	
	
	
}
