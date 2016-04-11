import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FermatAndDixon {

	/*Function to calculate Factors using Fermat Factorisation Algorithm*/
	
	public static void FermatFactor(BigIntNumber N)
    {
        BigIntNumber a = N.ceilSqrt();
        BigIntNumber b2 = N.substractFrom(a.product(a));
                
        while (!isSquare(b2))
        {
            a=a.plus(new BigIntNumber(1));
            b2 = N.substractFrom(a.product(a));
        }
        
        BigIntNumber r1 = b2.floorSqrt().substractFrom(a);
        BigIntNumber r2 = N.divideBy(r1);
        display(r1, r2);
    }
    
	/** function to display roots **/
    
	public static void display(BigIntNumber r1, BigIntNumber r2)
    {
        System.out.println("Roots = "+ r1.toDecimalString() +" , "+ r2.toDecimalString());    
    }
    
	/** function to check if N is a perfect square or not **/
    
    public static boolean isSquare(BigIntNumber N)
    {
        BigIntNumber sqr = N.floorSqrt();
        BigIntNumber sqr1=sqr.plus(new BigIntNumber(1));
        
        if ((sqr.product(sqr).compareTo(N)==0) || (sqr1.product(sqr1).compareTo(N)==0))
            return true;
        return false;
    }
    
    /*Function to calculate Factors using Dixon Factorisation Algorithm*/
	
    private static int[] primes=new int[100005];
    
    public static void dixonFactor(BigIntNumber N)
    {
    	int found=0;
    	int lenPrimeBase=4;
    	
    	while(found==0)
    	{
    		int[] factorBase=new int[lenPrimeBase];
    		
    		for(int no=0;no<lenPrimeBase;no++)
    			factorBase[no]=primes[no];
        	
        	ArrayList<BigIntNumber> smoothNumbers=new ArrayList<BigIntNumber>();
        	ArrayList<BigIntNumber> smoothSquares=new ArrayList<BigIntNumber>();
        	
        	int count=0;
        	
        	ArrayList< int[] > expVec=new ArrayList< int[] >();
        	
        	BigIntNumber inNum=N.ceilSqrt();
        	
        	while(count<lenPrimeBase+1)
        	{
        		
        		int[] V=isSmooth(inNum,factorBase,N);
        		
        		if(V.length!=0)
        		{
        			expVec.add(V);
        			count++;
        			smoothSquares.add(inNum.product(inNum).mod(N));
        			smoothNumbers.add(inNum);
        		}
        		
        		inNum=inNum.plus(new BigIntNumber(1));
        	}
        	
        	int i;
        	
        	for(i=0;i<(1<<lenPrimeBase+1);i++)
        	{
        		int[] sum=new int[lenPrimeBase];
        		for(int nz=0;nz<lenPrimeBase;nz++)
        			sum[nz]=0;
        		
        		int countSetBit=0;
        		for(int j=0;j<lenPrimeBase+1;j++)
        		{
        			if((i & 1<<j)!=0)
        			{
        				countSetBit++;
        				for(int l=0;l<lenPrimeBase;l++)
        				 sum[l]=sum[l]+expVec.get(j)[l];
        			}
        		}
        		
        		int flag=1;
        		for(int z=0;z<lenPrimeBase;z++)
    			{
    				if(sum[z]%2!=0)
    					flag=0;
    			}
        		
        		if(countSetBit>1&&flag==1)
        		{
        			BigIntNumber A=new BigIntNumber(1),B=new BigIntNumber(1);
                	
                	for(int j=0;j<lenPrimeBase+1;j++)
                	{
                		if((i & 1<<j)!=0)
            			{
                			A=A.product(smoothNumbers.get(j));
                			B=B.product(smoothSquares.get(j));
            			}	
                	}
                	
                	A=A.mod(N);
                	B=B.ceilSqrt();
                	B=B.mod(N);               	
                	
                	if(A.compareTo(B)==1)
                	{
                		BigIntNumber r1=N.gcd(B.substractFrom(A));
                		BigIntNumber r2=N.gcd(B.plus(A));
                		
                		if(r1.product(r2).compareTo(N)==1)
                			r1=r1.divideBy(r1.gcd(r2));
                		
                		if(r1.compareTo(new BigIntNumber(1))!=0&&r1.compareTo(N)!=0)
                		{
                			found=1;
                			display(r1,r2);
                		}
                	}
                	else
                	{
                		BigIntNumber r1=N.gcd(A.substractFrom(B));
                		BigIntNumber r2=N.gcd(B.plus(A));
                		
                		if(r1.product(r2).compareTo(N)==1)
                			r1=r1.divideBy(r1.gcd(r2));
                		
                		if(r1.compareTo(new BigIntNumber(1))!=0&&r1.compareTo(N)!=0)
                		{
                			found=1;
                			display(r1,r2);
                		}
                			
                	}
                	
                	if(found==1)
                		break;
        		}
        			
        	}
        	
        	lenPrimeBase++;
    	}
    		
    }
	
    private static int[] isSmooth(BigIntNumber number,int [] factorBase,BigIntNumber N)
    {
    	int factorBaseLen=factorBase.length;
    	int[] resultArray=new int[factorBaseLen];
    	
    	for(int i=0;i<factorBaseLen;i++)
    		resultArray[i]=0;
    	
    	number=number.product(number);
    	number=number.mod(N);
    	
    	for(int i=0;i<factorBaseLen;i++)
    	{
    		BigIntNumber factorBaseNumber=new BigIntNumber (factorBase[i]);
    		
    		while(number.mod(factorBaseNumber).compareTo(new BigIntNumber(0))==0)
    		{
    			resultArray[i]++;
    			number=number.divideBy(factorBaseNumber);
    		}
    	}
    	
    	int [] emptyArray={};
    	
    	if(number.compareTo(new BigIntNumber(1))==0)
    		return resultArray;
    	else
    		return emptyArray;
    	
    }
    
    public static void generatePrimes()
    {
    	int[] num=new int[100001];
    	
    	for(int i=0;i<100001;i++)
    		num[i]=0;
    	
    	int k=0;
    	for(int i=2;i<100001;i++)
    	{
    		if(num[i]==0)
    		{
    			primes[k++]=i;
    			for(int j=2;i*j<100001;j++)
        		{
        			num[i*j]=1;
        		}
    		}
    		
    	}
    	
    }
    
    public static void main(String[] params) {
		 
    		generatePrimes();
    		
	    	
    		
    		while(true)
    		{
    			System.out.println("Enter 1 for Fermat Factorisation Method");
    			System.out.println("Enter 2 for Dixon Factorisation Method");
    			System.out.println("Enter 0 to Exit");
    			
    			
    			Scanner in = new Scanner(System.in);
    			int flag=in.nextInt();
    			
    			if(flag==0)
    				break;
    			
        		System.out.print("Enter a Number\n");
        		
        		BufferedReader buf=new BufferedReader(new InputStreamReader(System.in));
        		String inputStr = null;
				try {
					inputStr = buf.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		BigIntNumber d=BigIntNumber.valueOf(inputStr);
        		
    			
    			
    			if(flag==1)
    			{
    				Long startTime=System.nanoTime();
    		    	FermatFactor(d);
    		    	Long endTime=System.nanoTime();
    		    	
    		    	double difference1 = (endTime - startTime)/1e6;
    		    	System.out.println("Time for Fermat factorisation:"+difference1+"ms\n");
    		    	
    			}
    			else if(flag==2)
    			{
    				Long startTime=System.nanoTime();
    		    	dixonFactor(d);
    		    	Long endTime=System.nanoTime();
    		    	double difference2 = (endTime - startTime)/1e6;
    		    	System.out.println("Time for Dixon factorisation:"+difference2+"ms\n");
    			}
    			
    		}
    				 		    	
	 }
}
