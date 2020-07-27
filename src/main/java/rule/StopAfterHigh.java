package rule;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import currency.CurrencyHandler;

public class StopAfterHigh implements Rule {
	
	private ClosePriceIndicator closePriceIndicator;
	private Num lossPct;
	private String currency;
	
	public StopAfterHigh(ClosePriceIndicator closePriceIndicator, Num lossPct, String currency) {
		this.closePriceIndicator = closePriceIndicator;
		this.lossPct = lossPct;
		this.currency = currency;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = false;
		Trade openTrade = CurrencyHandler.getCurrencyByCode(currency).getOpenTrade();
		if(openTrade != null) {
			double highest = openTrade.getHigh();
			double threshold = highest - (highest * (lossPct.doubleValue()/100));
			//System.out.println("High " + highest);
			//System.out.println("Threshold " + threshold);
			if(closePriceIndicator.getBarSeries().getBar(index).getClosePrice().doubleValue() <= threshold) {
				satisfied = true;
			}
		}
		return satisfied;
	}

}
