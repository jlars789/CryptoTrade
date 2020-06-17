package threads;

import currency.CurrencyHandler;

public class CurrencyRefresh implements Runnable {

	public CurrencyRefresh() {
		
	}

	@Override
	public void run() {
		CurrencyHandler.updateValues();
		CurrencyHandler.updateUserPreferences();
		
		CurrencyHandler.analyzeScalp();
	}

}
