package tracker;

import org.joda.time.DateTime;
import org.json.JSONObject;

import currency.CurrencyHandler;

public class Transaction {
	
	private String type;
	private String tradeID;
	private String ID;
	private String side;
	
	private double amount;
	private double fees;
	
	private String date;
	private String time;

	public Transaction(JSONObject response) {
		DateTime now = DateTime.now();
		String month = now.monthOfYear().getAsShortText();
		String day = now.dayOfMonth().getAsShortText();
		String year = "" + now.getYear();
		this.date = month + " " + day + ", " + year;
		
		this.time = now.toLocalTime().toString();
		
		this.type = response.getString("type");
		this.tradeID = response.getString("product_id");
		String from = tradeID.split("-")[0];
		this.ID = response.getString("id");
		this.side = response.getString("side");
		this.amount = response.getDouble("size") * CurrencyHandler.getCurrencyByCode(from).getExchangeRate();
		this.fees = response.getDouble("fill_fees");
	}
	
	public double getFees() {
		return this.fees;
	}
	
	public String toString() {
		String verb;
		if(side.equals("sell")) verb = "sold";
		else verb = "bought";
		
		String msg = "CryptoTrading Bot " + verb + " $" + this.amount + " in a " + this.type + " transaction on \n" +
						this.date + " at " + this.time + ". \nID: " + this.ID;
		return msg;
	}

}
