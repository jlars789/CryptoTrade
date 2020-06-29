package threads;


import java.time.Duration;
import java.time.LocalDateTime;

import currency.CurrencyHandler;

public class CurrencyRefresh implements Runnable {

	public CurrencyRefresh() {
		
	}

	@Override
	public void run() {
		LocalDateTime start = LocalDateTime.now();
		try {
			CurrencyHandler.checkUserPurchases();
			Thread.sleep(15);
			CurrencyHandler.updateValues(false);
			Thread.sleep(15);
			CurrencyHandler.updateUserPreferences();
			Thread.sleep(15);
			CurrencyHandler.scalpStrategy();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		LocalDateTime finish = LocalDateTime.now();
		System.out.println("Currency refresh completed in " + (float)(Duration.between(start, finish).toMillis())/1000 + "s");
		
	}

}
