package analytics;

import analytics.util.Matrix;

public class LSSVM 
{
	private double[][] TrainX; //Save training input data
	private double[] TrainY; //Save the training category label
	private double[][] TestX; //Save test input data
	private double[] TestY; //Save the test category label
	private double[] w; //LSSVM weight
	private double b; //LSSVM offset
	private double gama; //gama parameter of LSSVM
	private double c=0.0000001; //Prevent irreversible matrix parameters
	private int n; //Dimension of data
	
	public void train(double[][] X,double[] Y,double gama)
	{
		this.TrainX=X; //Assignment of training set input data
		this.TrainY=Y; //Assignment of training set labels
		this.gama=gama; //The setting of gamga parameter in LSSVM

		this.n=X[0].length; //Get the dimension of the data
		
		
		process();
		
	}
	
	public void process() //Process of processing LSSVM
	{
		
		int N=TrainX.length; //Get the number of training set samples
		
		double[][] Z=new double[n][N]; //Create a two-dimensional array corresponding to the Z matrix
		
		for(int i=0;i<n;i++)
		{
			
			for(int j=0;j<N;j++)
			{
				
				Z[i][j]=TrainY[j]*TrainX[j][i];		//Calculation of the i-th row and i-column of the Z matrix
				
			}
			
		}
		
		Matrix Z_matrix=new Matrix(Z); //Create a new Z matrix instance
		Matrix Z_T_matrix=Z_matrix.transpose(); //Transpose matrix of matrix Z
		Matrix ZZ=Z_T_matrix.multiply(Z_matrix); //Matrix Z'*Z
		double[][] zz_array=ZZ.toArray(); //Matrix Z'*Z is converted to a two-dimensional array
		double[][] LSSVM_A=new double[N+1][N+1]; //Create A matrix of LSSVM to be solved
		
		LSSVM_A[0][0]=0+c; //The [0,0] element is 0
		for(int i=0;i<N;i++) //Assign the first row element and the first column element
		{
			LSSVM_A[0][i+1]=-TrainY[i];
			LSSVM_A[i+1][0]=TrainY[i];
		}
		
		for(int i=0;i<N;i++) //The other elements are assigned from the second row and second column
		{
			
			for(int j=0;j<N;j++)
			{
				
				if(i==j)
					LSSVM_A[i+1][j+1]=zz_array[i][j]+1/gama+c;
				else
					LSSVM_A[i+1][j+1]=zz_array[i][j];	
				
			}
		}
		
			
		double[][] LSSVM_b=new double[N+1][1]; //Create the b matrix of the LSSVM to be solved
		
		LSSVM_b[0][0]=0; //The [0,0] element is 0

		for(int i=0;i<N;i++) //Assign values ​​to the first column of elements
		{
			LSSVM_b[i+1][0]=1;
		}
		
		
		Matrix LSSVM_A_matrix=new Matrix(LSSVM_A); //Convert two-dimensional array LSSVM_A to matrix object
		
		Matrix LSSVM_b_matrix=new Matrix(LSSVM_b); //Convert two-dimensional array LSSVM_b to matrix object
		

		Matrix x_matrix=LSSVM_A_matrix.getInv().multiply(LSSVM_b_matrix); //Solve for Ax=b
				
		double[][] xx=x_matrix.toArray(); //Convert matrix x_matrix to two-dimensional array
	
		
		b=xx[0][0]; //The offset of LSSVM is xx[0][0]
		
		double[][] alpha=new double[N][1]; //Save Lagrangian coefficient
		
		for(int i=0;i<N;i++) //LSSVM's Langerian coefficient is the second element of xx, starting with all elements
			alpha[i][0]=xx[i+1][0];
		
		Matrix alpha_matrix=new Matrix(alpha); //Convert 2D array alpha to matrix object

		Matrix w_matrix=Z_matrix.multiply(alpha_matrix); //Because the weight w=Z*alpha
		
		double[][] w_array=w_matrix.toArray(); //Convert matrix w_matrix to two-dimensional array
		
		w=new double[n]; //Convert two-dimensional array to one-bit array
		
		for(int i=0;i<n;i++)
			w[i]=w_array[i][0];
			
		System.out.print("w:"); //Output w and b
		
		for(int i=0;i<n;i++)
			System.out.print(w[i]+" ");
		
		System.out.println();
		System.out.println("b:"+b);

	}
	
	
	public double[] predict(double[][] X) //Use LSSVM for prediction
	{
		int N=X.length; //Get the number of test samples
		
		double[] predict_y=new double[N]; //Save prediction results
		
		for(int i=0;i<N;i++)
		{
			double y=0;
			
			for(int j=0;j<n;j++)
			{
				
				y=y+w[j]*X[i][j];
				
			}
			
			y=y+b; //Calculate f(x)=wTx+b
			
			if(y<0) //Less than 0 is negative sample
			{
				predict_y[i]=-1;
			}	
			else //Positive samples greater than 0
			{
				predict_y[i]=1;
			}
				
		
			
		}
		
		return predict_y;
		
	}
	
}
