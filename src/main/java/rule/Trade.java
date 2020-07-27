package rule;

public class Trade {
	
	private double entryPrice;
	private double highPrice;
	private int index;

	public Trade(double entryPrice) {
		this.entryPrice = entryPrice;
		this.highPrice = entryPrice;
		//this.index = index;
	}
	
	public void checkForHigh(double barHigh) {
		if(barHigh > highPrice) highPrice = barHigh;
	}
	
	public double getHigh() {
		return this.highPrice;
	}
	
	public double getEntry() {
		return this.entryPrice;
	}
	
	public int getIndex() {
		return this.index;
	}
	
}
