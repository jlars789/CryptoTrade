package threads;

import currency.CurrencyHandler;

public class CurrencyRefresh implements Runnable {

	public CurrencyRefresh() {
		CurrencyHandler.updateValues();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
