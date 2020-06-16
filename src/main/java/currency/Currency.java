package currency;


public class Currency {
	
	private String name;
	private String code;
	private double exchangeRate;
	private double amountOwned;
	
	enum Type {
		IGNORED, WATCHED, SHORT_TERM, LONG_TERM
	}
	
	private double targetMark;
	//0 is upper, 1 is lower
	private double[] threshold = new double[2];
	private Type tag;
	
	public Currency(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public void dataUpdate(double exchangeRate, double amountOwned) {
		this.exchangeRate = exchangeRate;
		this.amountOwned = amountOwned;
	}
	
	public void setUserPreferences(double targetMark, double[] threshold, String tag) {
		if(tag.equalsIgnoreCase(Type.IGNORED.toString())) this.tag=Type.IGNORED;
		else if(tag.equalsIgnoreCase(Type.WATCHED.toString())) this.tag=Type.WATCHED;
		else if(tag.equalsIgnoreCase(Type.SHORT_TERM.toString())) this.tag=Type.SHORT_TERM;
		else if(tag.equalsIgnoreCase(Type.LONG_TERM.toString())) this.tag=Type.LONG_TERM;
		else if(this.tag == null) this.tag=Type.IGNORED;
		
		this.targetMark = targetMark;
		this.threshold = threshold;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}

}
