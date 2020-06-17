package tracker;

import java.util.ArrayList;

public class OrderTracker {
	
	public static final ArrayList<Transaction> DAILY_TRANSACTIONS = new ArrayList<Transaction>();
	public static final ArrayList<Transaction> WEEKLY_TRANSACTIONS = new ArrayList<Transaction>();
	
	public static double dailyFees;
	public static double weeklyFees;
	
	public static int getTransactionsToday() {
		return DAILY_TRANSACTIONS.size();
	}
	
	public static int getTransactionsThisWeek() {
		return WEEKLY_TRANSACTIONS.size();
	}
	
	public static void addTransaction(Transaction t) {
		DAILY_TRANSACTIONS.add(t);
		dailyFees += t.getFees();
	}
	
	public static void dailyReset() {
		WEEKLY_TRANSACTIONS.addAll(DAILY_TRANSACTIONS);
		DAILY_TRANSACTIONS.clear();
		weeklyFees += dailyFees;
		dailyFees = 0;
	}
	
	public static void weeklyReset() {
		WEEKLY_TRANSACTIONS.clear();
		weeklyFees = 0;
	}

}
