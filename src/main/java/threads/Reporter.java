package threads;

import org.joda.time.LocalDateTime;

import currency.CurrencyHandler;
import notifications.NotificationGenerator;
import notifications.email.MessageSender;

public class Reporter implements Runnable {

	public Reporter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		String dat = CurrencyHandler.getDailyMessages();
		double total = NotificationGenerator.getTotal();
		
		LocalDateTime now = LocalDateTime.now();
		
		String subject = "Crypto Bot Daily Report for " + now.getMonthOfYear() + "/" + now.getDayOfMonth() + "/" + now.getYear() + "/";
		
		String message = "Daily revenue for today was $" + total +"\n";
		message += dat;
		
		MessageSender.deliverMessage(subject, message);
		
	}

}
