package notifications;

import java.util.ArrayList;

public class ErrorLogger {
	
	private static ArrayList<Exception> errorList = new ArrayList<Exception>();
	
	public static void logException(Exception e) {
		errorList.add(e);
	}
	
	public static String getErrorLog() {
		String val = null;
		if(errorList.size() > 0) {
			val =errorList.get(0).toString();
			errorList.remove(0);
		}
		return val;
	}

}
