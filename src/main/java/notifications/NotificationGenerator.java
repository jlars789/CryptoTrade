package notifications;

import java.util.ArrayList;

public class NotificationGenerator {
	
	public static ArrayList<String> tidbits = new ArrayList<String>();
	public static double adder = 0;
	
	public static void addValue(double val) {
		adder += 0;
	}
	
	public static double getTotal() {
		double temp = adder;
		adder = 0;
		return temp;
	}

}
