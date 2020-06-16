package analytics.util;

public class Equation {
	
	private double coeff[];
	
	public Equation(double[] coeff) {
		this.coeff = coeff;
	}
	
	public double evaluate(double x) {
		return (coeff[0] * (x * x)) + (coeff[1] * x) + coeff[2];
	}
	
	public double evalDer(double x) {
		return (2 * coeff[0] * x) + coeff[1];
	}
	
	public String getEquation() {
		return coeff[0] + "x^2 + " + coeff[1] + "x + " + coeff[2];
	}

}
