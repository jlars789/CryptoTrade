package analytics;
import analytics.util.Equation;

public class Regression {
	
	private double[] p1, p2, p3;
	private double denominator;
	private Equation eq;
	private double coeff[];	
	public Regression() {
		this.p1 = new double[2];
		this.p2 = new double[2];
		this.p3 = new double[2];
		this.coeff = new double[3];
		this.generate();
	}
	
	public Regression(double[] p1, double[] p2, double[] p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.generate();
	}
	

	public void generate() {
		
		this.denominator = 1;
		
		boolean quadratic = false;
		boolean linear = true;
		boolean constant = false;
		
		if((p2[1] > p1[1] && p2[1] > p3[1])) {
			quadratic = true;
		}
		else if(p2[1] < p1[1] && p2[1] < p3[1]) {
			quadratic = true;
		}
		else if((p1[1] > p2[1] && p2[1] > p3[1]) || (p1[1] < p2[1] && p2[1] < p3[1])) {
			linear = true;
		}
		else if(p1[1] == p2[1] && p2[1] == p3[1]) {
			constant = true;
		}
		
		if(quadratic) {
			init(p1, p2, p3);
			coeff[0] = ACoeff(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
			coeff[1] = BCoeff(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
			coeff[2] = CCoeff(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
		}
		else if(linear) {
			double slope = (double)(p1[1] - p3[1])/(double)(p1[0] - p3[0]);
			coeff[0] = 0;
			coeff[1] = slope;
			coeff[2] = BVal(slope, p1[0], p1[1]);
		}
		else if(constant) {
			coeff[0] = 0;
			coeff[1] = 0;
			coeff[2] = BVal(0, p1[0], p1[1]);
		}
		
		this.eq = new Equation(coeff);
		
	}
	
	public Equation getEquation() {
		return eq;
	}
	
	private double BVal(double slope, double x, double y) {
		return y - (slope * x);
	}
	
	private double ACoeff(double x1, double y1, double x2, double y2, double x3, double y3) {
		double A = ((x3*(y2-y1)) + (x2*(y1-y3)) + (x1*(y3-y2)))/denominator;
		return A;
	}
	
	private double BCoeff(double x1, double y1, double x2, double y2, double x3, double y3) {
		double B = (((x1*x1)*(y2-y3))+((x3*x3)*(y1-y2))+((x2*x2)*(y3-y1)))/denominator;
		return B;
	}
	
	private double CCoeff(double x1, double y1, double x2, double y2, double x3, double y3) {
		double C = (((x2*x2)*((x3*y1)-(x1*y3)))+(x2*(((x1*x1)*y3)-((x3*x3)*y1))) + 
				((x1*x3)*(x3 - x1)*y2))/denominator;
		return C;
	}
	
	private void init(double[] p1, double[] p2, double[] p3) {
		denominator = (p1[0]-p2[0])*(p1[0]-p3[0])*(p2[0]-p3[0]);
	}

}
