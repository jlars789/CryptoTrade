package analytics.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Matrix 
{

    public int M;
	
	public int N;
	
	public double[][] data;
	/**
	 * No parameter construction, this is a formal method, in practice there can be no 0*0 empty matrix, pay attention to avoid NullPointerException
	 */
	public Matrix() {
		this.M = 0;
		this.N = 0;
		this.data = new double[M][N];
	}
	
	/**
	 * Including dimension structure
	 * @param M line
	 * @param N columns
	 */
	public Matrix(int M,int N) {
		this.M = M;
		this.N = N;
		this.data = new double[M][N];
        for(int i=0;i<M;i++) {
    	   for(int j=0;j<N;j++) {
    		   data[i][j]=0;
    	   }
        }
	}
	
	/**
	* Construct matrix with two-dimensional floating-point array
	* @param data floating point array
	*/
	public Matrix(float[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = data[i][j];
            }
        }
    }
	
	/**
	* Construct matrix using two-dimensional double-precision floating-point array
	* @param data floating point array
	*/
	public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = (double) data[i][j];
            }
        }
    }
	

	/**
	* Construct matrix with two-dimensional array
	* @param data integer array
	*/
	public Matrix(int[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = data[i][j];
            }
        }
    }
	
	/**
	* Initialize the matrix to the same number
	* @param M number of lines
	* @param N number of columns
	* @param allvalue specific floating point value
	*/
	public Matrix(int M,int N,double allvalue)
    {
		this.M = M;
	    this.N = N;
	    double[][]tmp = new double[M][N];
        for(int i=0;i<M;i++) {
    	   for(int j=0;j<N;j++) {
    		   tmp[i][j]=allvalue;
    	   }
        }
       data=tmp;
    }
	
	/**
	* Initialize the matrix to the same number
	* @param M number of lines
	* @param N number of columns
	* @param allvalue specific integer value
	*/
	public Matrix(int M,int N,int allvalue)
    {
		this.M = M;
	    this.N = N;
	    double[][]tmp = new double[M][N];
        for(int i=0;i<M;i++) {
    	   for(int j=0;j<N;j++) {
    		   tmp[i][j]=allvalue;
    	   }
        }
       data=tmp;
    }
	
	/**
	* Return the number of matrix rows
	* @return lines
	*/
	public int getRowNumber() {
		return this.M;
	}

	/**
	* Returns the number of matrix columns
	* @return number of columns
	*/
	public int getColoumNumber() {
		return this.N;
	}

	/**
	* Returns the element value of a specific index
	* @param Row row index
	* @param Coloum column index
	* @return element
	*/
	public double getElement(int Row, int Coloum) {
		return this.data[Row][Coloum];
	}

	/**
	* Generate random matrix
	* @param Row row index
	* @param Coloum column index
	* @return value
	*/
	public static Matrix random(int Row, int Coloum) {
		Matrix A = new Matrix(Row, Coloum);
        for(int i = 0; i < Row; i++) {
        	for(int j = 0; j < Coloum; j++) {
        		A.data[i][j] = (double) Math.random();
        	}
        }
        return A;
	}
	
	/**
	* Generate a random matrix with an upper limit, that is, random integers between [0,bound) will appear
	* @param Row row index
	* @param Coloum column index
	* @param bound
	* @return random matrix
	*/
	public static Matrix randomWithUpperLimit(int Row,int Coloum,int bound) {
		Matrix A = new Matrix(Row, Coloum);
		Random random = new Random();
        for(int i = 0; i < Row; i++) {
        	for(int j = 0; j < Coloum; j++) {
        		A.data[i][j] = (double) random.nextInt(bound);
        	}
        }
        return A;
	}
	
	
	
	/**
	* Generate a floating-point random matrix within a specific range, (lowerBound, upperBound)
	* @param Row number of rows
	* @param Coloum number of columns
	* @param lowerBound lower limit
	* @param upperBound upper limit
	* @return random matrix
	*/
	public static Matrix randomdoubleWithRange(int Row,int Coloum,double lowerBound,float upperBound) {
		Random random = new Random();
		Matrix A = new Matrix(Row, Coloum);
		for(int i=0;i<Row;i++) {
			DoubleStream doubleStream = random.doubles(Coloum, lowerBound, upperBound);
			double[] array = doubleStream.toArray();
			for(int j=0;j<Coloum;j++) {
				A.data[i][j] = (float) array[j];
			}
		}
		return A;
	}
	
	/**
	* Generate an integer random matrix within a specific range, [lowerBound, upperBound)
	* @param Row number of rows
	* @param Coloum number of columns
	* @param lowerBound lower limit
	* @param upperBound upper limit
	* @return random matrix
	*/
	public static Matrix randomIntWithRange(int Row,int Coloum,int lowerBound,int upperBound) {
		Random random = new Random();
		Matrix A = new Matrix(Row, Coloum);
		for(int i=0;i<Row;i++) {
			IntStream intStream = random.ints(Coloum, lowerBound, upperBound);
			int[] array = intStream.toArray();
			for(int j=0;j<Coloum;j++) {
				A.data[i][j] = array[j];
			}
		}
		return A;
	}
	
	/**
	* Generate a random Gaussian matrix with an average value of 0 and a standard deviation of 1
	* @param Row row index
	* @param Coloum column index
	* @return random Gaussian matrix
	*/
	public static Matrix randomGaussian(int Row, int Coloum) {
		Matrix A = new Matrix(Row, Coloum);
		Random random = new Random();
        for(int i = 0; i < Row; i++) {
        	for(int j = 0; j < Coloum; j++) {
        		A.data[i][j] = (float) random.nextGaussian();
        	}
        }
        return A;
	}

	/**
	* Set the specified index element to a specific value
	* @param Row row index
	* @param Coloum column index
	* @param e value
	*/
	public void setToSpecifiedValue(int Row, int Coloum, float e) {
		this.data[Row][Coloum] = e;
	}

	/**
	* Set all elements of the specified line to specific values
	* @param Row row index
	* @param e value
	* Matrix after @return setting
	*/
	public Matrix setRowToSpecifiedValue(int Row, float e) {
		 for (int j = 0; j < N; j++) {
	            this.data[Row][j] = e;
		 }
	     return this;
	}

	/**
	* Set all elements of the specified column to specific values
	* @param Coloum column index
	* @param e value
	* Matrix after @return setting
	*/
	public Matrix setColoumToSpecifiedValue(int Coloum, float e) {
		for (int i = 0; i < M; i++) {
			this.data[i][Coloum] = e;
		}
		return this;
	}

	/**
	* Flip by line
	* @return matrix after row-by-row inversion
	*/
	public Matrix flipDimensionByRow() {
		double[][] result = new double[M][N];
    	for(int i = 0; i < M/2; i++) {
			result[M-1-i] = this.data[i];
    		result[i] = this.data[M-1-i];
    	}
    	if(M%2 != 0) {
			result[M/2] = this.data[M/2];
		}
    	return new Matrix(result);
	}

	/**
	* Flip by column
	* @return matrix after the column is reversed
	*/
	public Matrix flipDimensionByColoum() {
		double[][] result = new double[M][N];
		for(int i = 0; i < M;i++) {
			for(int j = 0; j < N/2; j++) {
				result[i][N-1-j] = this.data[i][j];
				result[i][j] = this.data[i][N-1-j];
			}
		}
		if(N % 2 != 0) {
			for(int i=0;i<M;i++) {
				result[i][N/2] = this.data[i][N/2];
			}
		}
    	return new Matrix(result);
	}

	/**
	 * Matrix transpose
	 * @return matrix after transpose
	*/
	public Matrix transpose() {
		Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A.data[j][i] = this.data[i][j];
            }
        }
	    return A;
	}

	/**
	* Ask for prescription
	* @return matrix
	*/
	public Matrix sqrt() {
    	for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = (float) Math.sqrt(this.data[i][j]);
            }
    	}
        return this;
	}
	
	/**
	* Find tangent
	* @return matrix
	*/
	public Matrix tan() {
		for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = (float) Math.tan(this.data[i][j]);
            }
		}
        return this;
	}

	/**
	* Find the sine
	* @return matrix
	*/
	public Matrix sin() {
		for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = (float) Math.sin(this.data[i][j]);
            }
		}
        return this;
	}

	/**
	* Find the cosine
	* @return matrix
	*/
	public Matrix cos() {
		for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	this.data[i][j] = (float) Math.cos(this.data[i][j]);
            }
		}
        return this;
	}
	
	/**
	* Returns the e index of the matrix
	* @return matrix
	*/
	public Matrix exp() 
	{
		
		for (int i = 0; i < M; i++) 
		{
			
            for (int j = 0; j < N; j++) 
            {
            	this.data[i][j] = (float) Math.exp(this.data[i][j]);
            }
		}
		
        return this;
	}
	/**
	* Returns the log natural logarithm of the matrix
	* @return matrix
	*/
	public Matrix log() 
	{
		
		for (int i = 0; i < M; i++) 
		{
            for (int j = 0; j < N; j++) 
            {
            	this.data[i][j] = (float) Math.log(this.data[i][j]);
            }
		}
		
        return this;
	}

	/**
	* Add two matrices
	* @param B specifies the matrix
	* @return Addition of original matrix and specified matrix
	*/
	public Matrix plus(Matrix B) 
	{
		Matrix A = this;
		
        if (B.M != A.M || B.N != A.N) 
        {
        	throw new RuntimeException("The two matrix dimensions must be consistent");
        }
        
        Matrix C = new Matrix(M, N);
        
        for (int i = 0; i < M; i++) 
        {
            for (int j = 0; j < N; j++) 
            {
                C.data[i][j] = A.data[i][j] + B.data[i][j];
            }
        }
        return C;
	}

	/**
	* Subtract two matrices
	* @param B specifies the matrix
	* @return Subtraction of the original matrix and the specified matrix
	*/
	public Matrix minus(Matrix B) 
	{
		
		Matrix A = this;
		
        if (B.M != A.M || B.N != A.N) 
        {
        	throw new RuntimeException("The two matrix dimensions must be consistent");
        }
        
        Matrix C = new Matrix(M, N);
        
        for (int i = 0; i < M; i++) 
        {
            for (int j = 0; j < N; j++) 
            {
                C.data[i][j] = A.data[i][j] - B.data[i][j];
            }
        }
        return C;
	}
	

	/**
	* Multiply two matrices
	* @param B specifies the matrix
	* @return Multiplication of the original matrix and the specified matrix
	*/
	public Matrix multiply(Matrix B) 
	{
		Matrix A = this;
		
		if (A.N != B.M) 
		{
        	throw new RuntimeException("The number of columns in the first matrix must be equal to the number of rows in the second matrix");
		}
//		Matrix C = new Matrix(A.M, B.N);
		
		
		
		
		BigDecimal[][] CC=new BigDecimal[A.M][B.N];
        for (int i = 0; i < CC.length; i++) 
        {
            for (int j = 0; j < CC[0].length; j++) 
            {
            	CC[i][j]=new BigDecimal("0.0");
                for (int k = 0; k < A.N; k++) 
                {
                	
//                    C.data[i][j] = C.data[i][j] + A.data[i][k] * B.data[k][j];
                	  BigDecimal a=new BigDecimal(Double.toString(A.data[i][k]));
                	  BigDecimal b=new BigDecimal(Double.toString(B.data[k][j]));
                	  CC[i][j]=CC[i][j].add(a.multiply(b));
                }
                
            }
        }
        
        
        Matrix C = new Matrix(A.M, B.N);
        for(int i=0;i<C.M;i++)
        {
        	for(int j=0;j<C.N;j++)
        	{
        		C.data[i][j]=CC[i][j].doubleValue();
        	}
        }
        return C;
	}
	
	/**
	* Divide two matrices
	* @param B specifies the matrix
	* @return Division of the original matrix and the specified matrix
	*/
	public Matrix divide(Matrix B) {
		Matrix C = new Matrix(M, N);
    	for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
            	if (B.data[i][j]==0) {
                	throw new RuntimeException("Dividend cannot be 0");
        		}
                C.data[i][j] = data[i][j] / B.data[i][j];
            }
    	}
        return C;
	}

	/**
	* Two matrix dot product
	* @param B specifies the matrix
	* @return dot product of the original matrix and the specified matrix
	*/
	public Matrix dot(Matrix B) {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < C.M; i++) {
            for (int j = 0; j < C.N; j++) {
                 C.data[i][j] = data[i][j] * B.data[i][j];
            }
        }
        return C;
	}

	/**
	* Matrix dot product
	* @return Dot product of the original matrix and itself
	*/
	public Matrix dotBySelf() {
        return this.dot(this);
	}

	/**
	* Matrix plus a number
	* @param e number
	* @return result matrix
	*/
	public Matrix plusNumber(float e) {
		Matrix C = new Matrix(M, N,e);
        return this.plus(C);
	}

	/**
	* Matrix minus a number
	* @param e number
	* @return result matrix
	*/
	public Matrix minusNumber(float e) {
		Matrix C = new Matrix(M, N,e);
        return this.minus(C);
	}

	/**
	* Matrix multiplied by a number
	* @param e number
	* @return result matrix
	*/
	public Matrix multiplyNumber(float e) {
		Matrix C = new Matrix(M, N,e);
        return this.multiply(C);
	}
	
	/**
	* Matrix divided by a number
	* @param e number
	* @return result matrix
	*/
	public Matrix divideNumber(float e) {
		Matrix C = new Matrix(M, N,e);
        return this.divide(C);
	}

	/**
	* Determine whether two matrices are equal
	* @param t matrix to be compared
	* @return Boolean matrix
	*/
	public boolean equalTo(Matrix t) {
		 for (int i = 0; i < M; i++) {
	            for (int j = 0; j < N; j++) {
	                if (data[i][j] != t.data[i][j]) {
	                	return false;
	                }
	            }
		 }
         return true;
	}

	/**
	* Print to console
	*/
	public void showToConsole() {
		for (double[] a : data) {
            for (double b : a) {
                System.out.printf("%9.4f ", b);
            }
            System.out.println();
        }
	}

	/**
	* Find the rank of the matrix (3*3 or 2*2)
	* @return rank
	*/
	public double det() {
		if ((data.length == 3 && data[0].length == 3)||(data.length == 2&& data[0].length == 2)) { 
    		if(data.length == 3 && data[0].length == 3) {
    			double r = 0;
            	r = data[0][0]*data[1][1]*data[2][2]+data[1][0]*data[2][1]*data[0][2]+data[0][1]*data[1][2]*data[2][0];
            	r = r-data[0][2]*data[1][1]*data[2][0]-data[1][0]*data[2][2]*data[0][1]-data[0][0]*data[2][1]*data[1][2];
            	return r;
        	} else {
        		double r = 0;
            	r = data[0][0]*data[1][1]-data[0][1]*data[1][0];
             	return r;
        	}
		} else {
    		throw new RuntimeException("矩阵的维度必须为3*3的或者为2*2的");
		}
	}

	/**
	* Mesh the specific intervals (all parameters are int)
	* @param start1 1 start
	* @param distance1 1 pitch
	* @param end1 1 end
	* @param start2 2 start
	* @param distance2 2 pitch
	* @param end2 2 end
	* @return two matrices with the same principle as MATLAB
	*/
	public Matrix[] meshgrid(int start1, int distance1, int end1,
			int start2, int distance2, int end2) {
		int Coloum = (end1-start1)/distance1+1;
		int Row = (end2-start2)/distance2+1;
		Matrix[] result = new Matrix[2];
		result[0] = new Matrix(Row,Coloum);
		result[1] = new Matrix(Row,Coloum);
		for(int i = 0; i < Row; i++) {
			for(int j = 0; j < Coloum;j++) {
				result[0].setToSpecifiedValue(i, j,start1+j*distance1);
				result[1].setToSpecifiedValue(i, j,start2+i*distance2);
			}
		}
		return result;
	}
	
	/**
	* Mesh the specific intervals (all parameters are float)
	* @param start1 1 start
	* @param distance1 1 pitch
	* @param end1 1 end
	* @param start2 2 start
	* @param distance2 2 pitch
	* @param end2 2 end
	* @return two matrices with the same principle as MATLAB
	*/
	public Matrix[] meshgrid(float start1, float distance1, float end1,
			float start2, float distance2, float end2) {
		int Coloum = (int) ((end1-start1)/distance1+1);
		int Row = (int) ((end2-start2)/distance2+1);
		Matrix[] result = new Matrix[2];
		result[0] = new Matrix(Row,Coloum);
		result[1] = new Matrix(Row,Coloum);
		for(int i = 0; i < Row; i++) {
			for(int j = 0; j < Coloum;j++) {
				result[0].setToSpecifiedValue(i, j, start1+j*distance1);
				result[1].setToSpecifiedValue(i, j, start2+i*distance2);
			}
		}
		return result;
	}

	/**
	* Take the absolute value of each number of the matrix
	* @return matrix
	*/
	public Matrix abs() {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = Math.abs(data[i][j]) ;
            }
        }
        return C;
	}

	/**
	* Round up each number of the matrix
	* @return matrix
	*/
	public Matrix ceil() {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = (float) Math.ceil(data[i][j]) ;
            }
        }
        return C;
	}

	/**
	* Round each number of the matrix
	* @return matrix
	*/
	public Matrix round() {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = Math.round(data[i][j]) ;
            }
        }
        return C;
	}

	/**
	* Round down
	* @return matrix
	*/
	public Matrix floor() {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = (float) Math.floor(data[i][j]) ;
            }
        }
        return C;
	}

	/**
	* Into an array
	* @return two-dimensional array
	*/
	public double[][] toArray() {
    	return this.data;
	}
	
	/**
	* Return the sigmoid() value of each element of the matrix, sigmoid(x)=1/(1+exp(-x))
	* @return sigmoid matrix
	*/
	public Matrix sigmoid() {
		Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = (float) (1/(1+Math.exp(-data[i][j])));
            }
        }
        return C;
	}
	
	/**
	* Randomly shuffle the element positions of the matrix
	* @return random matrix
	*/
	public Matrix shuffle() {
		Matrix C = new Matrix(M,N);
		Random random = new Random();
		boolean[][] flag = new boolean[M][N];
		int i=0;
		int j=0;
		while(true) {
			int row = random.nextInt(M);
			int col = random.nextInt(N);
			if(flag[row][col]==false) {
				C.data[i][j] = this.data[row][col];
				j++;
				flag[row][col]=true;
				if(j==N) {
					j=0;
					i++;
				}
				if(i==N&&j==0) {
					break;
				}
			}
		}
		return C;
	}
	
	/**
	* Find the maximum value of the matrix
	* @return maximum
	*/
	public double max() {
		double max = data[0][0];
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				if(max<data[i][j]) {
					max = data[i][j];
				}
			}
		}
		return max;
	}

	/**
	* Find the minimum value of the matrix
	* @return minimum
	*/
	public double min() {
		double min = data[0][0];
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				if(min>data[i][j]) {
					min = data[i][j];
				}
			}
		}
		return min;
	}
	
	/**
	* Returns the maximum value of each row of the matrix
	* @return maximum value array
	*/
	public double[] maxOfEachRow() {
		double[] result = new double[M];
		double[][] tmp = data;
		for(int i=0;i<M;i++) {
			Arrays.parallelSort(tmp[i]);
			result[i] = tmp[i][N-1];
		}
		return result;
	}
	
	/**
	* Returns the maximum value of each column of the matrix
	* @return maximum value array
	*/
	public double[] maxOfEachColoum() {
		double[] result = new double[N];
		double[] col = new double[M];
		for(int i=0;i<N;i++) {
			for(int j=0;j<M;j++) {
				col[j] = data[j][i];
			}
			Arrays.parallelSort(col);
			result[i] = col[M-1];
		}
		return result;
	}
	
	/**
	* Returns the minimum value of each row of the matrix
	* @return minimum array
	*/
	public double[] minOfEachRow() {
		double[] result = new double[M];
		double[][] tmp = data;
		for(int i=0;i<M;i++) {
			Arrays.parallelSort(tmp[i]);
			result[i] = tmp[i][0];
		}
		return result;
	}
	
	/**
	* Returns the minimum value of each column of the matrix
	* @return minimum array
	*/
	public double[] minOfEachColoum() {
		double[] result = new double[N];
		double[] col = new double[M];
		for(int i=0;i<N;i++) {
			for(int j=0;j<M;j++) {
				col[j] = data[j][i];
			}
			Arrays.parallelSort(col);
			result[i] = col[0];
		}
		return result;
	}
	
	/**
	* Returns the sum of row vectors
	* @return and value
	*/
	public double sum() {
		if(M!=1) {
			throw new RuntimeException("The dimensions of the matrix must be 3*3 or 2*2");
		}
		double sum=0;
		for(int i=0;i<N;i++) {
			sum+=data[0][i];
		}
		return sum;
	}
	
	/**
	* Return the sum of each row
	* @return and array
	*/
	public double[] sumOfEachRow() {
		if(M==1) {
			throw new RuntimeException("The number of matrix rows must be greater than 1");
		}
		double[] sum=new double[M];
		for(int i=0;i<M;i++) {
			double s=0;
			for(int j=0;j<N;j++) {
				s+=data[i][j];
			}
			sum[i]=s;
		}
		return sum;
	}
	
	/**
	* Return the sum of each column
	* @return and array
	*/
	public double[] sumOfEachColoum() {
		if(N==1) {
			throw new RuntimeException("The number of matrix columns must be greater than 1");
		}
		double[] sum=new double[N];
		for(int i=0;i<N;i++) {
			double s=0;
			for(int j=0;j<M;j++) {
				s+=data[j][i];
			}
			sum[i]=s;
		}
		return sum;
	}
	
	/**
	* Get specific rows
	* @param index row index
	* @return row matrix
	*/
	public Matrix getRow(int index) {
		Matrix C = new Matrix(1,N);
		for(int i=0;i<N;i++) {
			C.data[0][i] = data[index][i];
		}
		return C;
	}
	
	public void print()
	{
		for(int i=0;i<M;i++)
		{
			for(int j=0;j<N;j++)
			{
				System.out.print(data[i][j]+" ");
			}
			System.out.println();
		}
		
	}
	/**
	* Get specific column
	* @param index column index
	* @return column matrix
	*/
	public Matrix getColoum(int index) {
		Matrix C = new Matrix(M,1);
		for(int i=0;i<M;i++) {
			C.data[i][0] = data[i][index];
		}
		return C;
	}
	
	/**
	* Find the inverse of a matrix
	* @param index column index
	* @return matrix inverse
	*/
	public Matrix getInv()
	{
		
		int rw = M, rk = N;   
		Matrix imat = new Matrix(rw, rk);   
		Matrix jmat = new Matrix(rw, rk);   

		for (int i = 0; i < rw; i++)    
			for (int j = 0; j < rw; j++)     
				jmat.data[i][j] = data[i][j];   

		for (int i = 0; i < rw; i++)    
			for (int j = 0; j < rw; j++)     
				imat.data[i][j] = 0;  

		 for (int i = 0; i < rw; i++)
			 imat.data[i][i] = 1;       
		 
		 for (int i = 0; i < rw; i++) 
		 {    
			 for (int j = 0; j < rw; j++) 
			 {    
				 if (i != j) 
				 {      
					 double t = jmat.data[j][i] / jmat.data[i][i];   
					 for (int k = 0; k < rw; k++) 
					 {       
						 jmat.data[j][k] -= jmat.data[i][k] * t;       
						 imat.data[j][k] -= imat.data[i][k] * t;      
					 }     
				 }   
			 }   
		 }    
		 
		 for (int i = 0; i < rw; i++)    
			 if (jmat.data[i][i] != 1) 
			 {     
				 double t = jmat.data[i][i];     
				 for (int j = 0; j < rw; j++) 
				 {      
					 jmat.data[i][j] = jmat.data[i][j] / t;      
					 imat.data[i][j] = imat.data[i][j] / t;     
				 }    
			 }   
		 
		 return imat; 
		
		
	}
}
