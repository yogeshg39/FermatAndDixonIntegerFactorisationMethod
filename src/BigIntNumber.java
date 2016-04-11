import java.util.Arrays;
import java.util.Formatter;

public class BigIntNumber implements Comparable<BigIntNumber>{

	public final static BigIntNumber ZERO = new BigIntNumber();
	public final static BigIntNumber ONE = new BigIntNumber(1);
	
    public final static int base = 1000000000;   //Base of the number system used
    private final static int baseDecimalDigit = 9; 
    
    private int[] digits;
    
    public BigIntNumber(int... digits)
    {
    	int countOfZero=0;
    	boolean zeroFlag=true;
    	
    	for(int digit:digits)
    	{
    		if(digit>=base||digit<0)
    			throw new IllegalArgumentException("Digit " + digit + " out of range!");
    		
    		if(zeroFlag)
    		{
    			if(digit!=0)
    				zeroFlag=false;
    			else
    				countOfZero++;	
    		}
    	}
    	this.digits = Arrays.copyOfRange(digits, countOfZero, digits.length);
    }
	
    public String toString() {
        return "BigIntNumber:" + Arrays.toString(digits);
    }
    
    public static BigIntNumber valueOf(String decimal)  {
    	int decLen=decimal.length();
    	int arrLen=(decLen-1)/baseDecimalDigit+1;
    	
    	int[] digits=new int[arrLen];
    	int firstDigit=decLen-(arrLen-1)*baseDecimalDigit;
    	
    	for(int i=0;i<arrLen;i++)
    	{
    		String stringToBeAdded=decimal.substring(Math.max(firstDigit+(i-1)*baseDecimalDigit,0),
    													(i)*baseDecimalDigit+firstDigit);
    		
    		int integerToBeAdded=Integer.parseInt(stringToBeAdded);
    		digits[i]=integerToBeAdded;
    	}
    	return new BigIntNumber(digits);
    }

    public BigIntNumber plus(BigIntNumber number){
    	
    	int[]result=new int[Math.max(this.digits.length, number.digits.length)+1];
    	
    	addDigitsToResult(result, result.length-1, this.digits);
        addDigitsToResult(result, result.length-1, number.digits);
    	
    	return new BigIntNumber(result);
    }

    private void addDigitsToResult(int[] result, int resultIndex,int... addArray)
	{
		int addArrayIndex = addArray.length - 1;
		while(addArrayIndex >= 0) {
			addDigit(result, resultIndex,addArray[addArrayIndex]);
			addArrayIndex--;
			resultIndex--;
		}
	}
    
    private void addDigit(int[] result, int resultIndex,int addArrayDigit)
	{
		int sum = result[resultIndex] + addArrayDigit;
		result[resultIndex] = sum % base;
		int carry = sum / base;
		if(carry > 0) {
			addDigit(result, resultIndex - 1, carry);
		}
	}
    
    public BigIntNumber substractFrom(BigIntNumber number)
    {
    	int[] result=new int[Math.max(this.digits.length, number.digits.length)];
    	
    	addDigitsToResult(result,result.length-1,number.digits);
    	substractDigitsFromResult(result,result.length-1,this.digits);
    	return new BigIntNumber(result);
    }
    
    private void substractDigitsFromResult(int[] result, int resultIndex,int... substractArray)
	{
		int substractArrayIndex = substractArray.length - 1;
		while(substractArrayIndex >= 0) {
			substractDigit(result, resultIndex,substractArray[substractArrayIndex]);
			substractArrayIndex--;
			resultIndex--;
		}
	}
    
    private void substractDigit(int[] result, int resultIndex,int substractEndDigit)
    {
		if(substractEndDigit>result[resultIndex])
		{
			result[resultIndex]=result[resultIndex]+base;
			result[resultIndex]=result[resultIndex]-substractEndDigit;
			addDigit(result, resultIndex - 1, -1);
		}
		else
		{
			result[resultIndex]=result[resultIndex]-substractEndDigit;
		}
		
	}

    
    public BigIntNumber product(BigIntNumber number)
    {
    	int[] result=new int [this.digits.length+number.digits.length];
    	
    	int resultIndex=result.length-1;
    	for(int i = 0; i < this.digits.length; i++) {
    		for(int j = 0; j < number.digits.length; j++) {
    		   multiplyDigit(result, resultIndex - (i + j),this.digits[this.digits.length-i-1],
                        number.digits[number.digits.length-j-1]);
    		}
    	}
    	return new BigIntNumber(result);
    }
    
    private void multiplyDigit(int[] result, int resultIndex,int firstFactor, int secondFactor) {
		long prod = (long)firstFactor * (long)secondFactor;
		int prodDigit = (int)(prod % base);
		int carry = (int)(prod / base);
		addDigitsToResult(result, resultIndex, carry, prodDigit);
	}
	
    public BigIntNumber divideBy(BigIntNumber divisor )
    {
    	if(this.compareTo(divisor)== -1)
    		return BigIntNumber.valueOf("0");
    
    	int[] result=new int[this.digits.length];
    	 	
    	divide(result,0,this.digits,divisor);
    	
    	return new BigIntNumber(result);
    }
    
    private BigIntNumber divide(int[] result,int resultIndex, int[] divident,BigIntNumber divisor)
    {
    	if(new BigIntNumber(divident).compareTo(divisor)== -1)
    		return new BigIntNumber(divident);
    	
    	int divisorLen=divisor.digits.length;
    	int dividentLen=divident.length;
        	
    	int[] newArray=new int[divisorLen];
    	
    	for(int i=0;i<=divisorLen-1;i++)
    		newArray[i]=divident[i];
    	    	
    	BigIntNumber remainder=new BigIntNumber(newArray);
    	
    	for(int i=divisorLen-1;i<dividentLen;i++)
    	{
    		BigIntNumber preNum=remainder;
    		
    		if(preNum.compareTo(divisor)== -1)
    			result[resultIndex++]=0;
    		else
    		{
    			BigIntNumber multiple=divisor;    			    			
    			int x = 0, L = 0, R = base;    	        
    			
    			while (L <= R) {
    	            
    				int mid = (L+R)>>1;
    				
    				if(preNum.compareTo(multiple.product(new BigIntNumber(mid)))==0)
    				{
    					x=mid+1;
    					break;
    				}
    			
    				if(preNum.compareTo(multiple.product(new BigIntNumber(mid)))<1) {
    	                x = mid;
    	                R = mid-1;
    	            }
    	            else
    	                L = mid+1;
    	        }
    				
    			result[resultIndex++]=x-1;
    			remainder=multiple.product(new BigIntNumber(x-1)).substractFrom(preNum);
    		}
    		
    		if(i!=dividentLen-1){
    			
    			int[] preNumArray=addElement(remainder.digits,divident[i+1]);
        		remainder=new BigIntNumber(preNumArray);
    		}
    	}
    	
    	return remainder;
    }
    
    static int[] addElement(int[] preNumArray,int addElement)
    {
    	preNumArray=Arrays.copyOf(preNumArray,preNumArray.length+1);
    	preNumArray[preNumArray.length-1]=addElement;
    	return preNumArray;
    }
    
    public BigIntNumber mod(BigIntNumber divisor)
    {
    	int[] result=new int[this.digits.length];
    	BigIntNumber remainder=divide(result,0,this.digits,divisor);
		return remainder;
    	
    }
    
    public BigIntNumber gcd(BigIntNumber that)
    {
    	BigIntNumber result=gcdFunc(this,that);
    	
    	return result;
    }
    
    private BigIntNumber gcdFunc(BigIntNumber num1,BigIntNumber num2)
    {
    	if(num2.compareTo(new BigIntNumber(0))==0)
    		return num1;
    	else
    		return gcdFunc(num2,num1.mod(num2));
    }
    
    public BigIntNumber floorSqrt()
    {
    	BigIntNumber number=this;
    	
        if (number.compareTo(new BigIntNumber(1))==0)
           return number;
     
        BigIntNumber start = new BigIntNumber(1);
        BigIntNumber end = number,ans=ONE;
              
        while (start.compareTo(end)!=1)
        {
           BigIntNumber mid=start.plus(end).divideBy(new BigIntNumber(2));

           if(mid.product(mid).compareTo(number)==0)
        	   return mid;
           
           if(mid.product(mid).compareTo(number)<0)
           {
        	   start=mid.plus(new BigIntNumber(1));
        	   ans=mid;
           }
           else
           {
        	   BigIntNumber one=new BigIntNumber(1);
        	   end=one.substractFrom(mid);
           }
        	   
        }
        
        return ans;
        
    }
    
    public BigIntNumber ceilSqrt()
    {
    	BigIntNumber number=this;
    	
        if (number.compareTo(new BigIntNumber(1))==0)
           return number;
     
        BigIntNumber start = new BigIntNumber(1);
        BigIntNumber end = number,ans=ONE;
              
        while (start.compareTo(end)!=1)
        {
           BigIntNumber mid=start.plus(end).divideBy(new BigIntNumber(2));

           if(mid.product(mid).compareTo(number)==0)
        	   return mid;
           
           if(mid.product(mid).compareTo(number)<0)
           {
        	   start=mid.plus(new BigIntNumber(1));
           }
           else
           {
        	   BigIntNumber one=new BigIntNumber(1);
        	   ans=mid;
        	   end=one.substractFrom(mid);
           }
        	   
        }
        
        return ans;
        
    }

    
    public String toDecimalString() {
		
        StringBuilder returnString =new StringBuilder(baseDecimalDigit * digits.length);
        Formatter f = new Formatter(returnString);
        f.format("%d", digits[0]);
        for(int i = 1 ; i < digits.length; i++) {
            f.format("%09d", digits[i]);
        }
        return returnString.toString();
    }
	
    public boolean equals(Object object) {
        return object instanceof BigIntNumber &&
            this.compareTo((BigIntNumber)object) == 0;
    }
    
    public int compareTo(BigIntNumber that) {
        if(this.digits.length < that.digits.length) {
            return -1;
        }
        if (that.digits.length < this.digits.length) {
            return 1;
        }
        // same length, compare the digits
        for(int i = 0; i < this.digits.length; i++) {
            if(this.digits[i] < that.digits[i]) {
                return -1;
            }
            if(that.digits[i] < this.digits[i]) {
                return 1;
            }
        }
        // same digits
        return 0;
    }
   
    public static void main(String[] params) {
    	
    	BigIntNumber d=BigIntNumber.valueOf("70454655665");
    	System.out.println("d="+d.toDecimalString());
    	
    	BigIntNumber d1=BigIntNumber.valueOf("56684465612");
    	System.out.println("d1="+d1.toDecimalString());
    	
    	BigIntNumber d3=d.plus(d1);
    	System.out.println("d+d1="+d3.toDecimalString());
    	
    	d3=d.product(d1);
    	System.out.println("d*d1="+d3.toDecimalString());
    	
    	d3=d1.substractFrom(d);
    	System.out.println("d-d1="+d3.toDecimalString());
    	
    	BigIntNumber d4=BigIntNumber.valueOf("70454655665");
    	BigIntNumber d5=BigIntNumber.valueOf("70454655664");
    	
    	if(d4.equals(d5))
    		System.out.println("Equal");
    	else
    		System.out.println("Not Equal");
    	
    	BigIntNumber d7=BigIntNumber.valueOf("3180686561354");
    	System.out.println("d7="+d7.toDecimalString());
    	
    	BigIntNumber d6=BigIntNumber.valueOf("565656511");
    	System.out.println("d6="+d6.toDecimalString());
    	
    	d3=d7.divideBy(d6);
    	System.out.println("d7/d6="+d3.toDecimalString());
    	
    	d3=d7.mod(d6);
    	System.out.println("d7%d6="+d3.toDecimalString());
    	
    	BigIntNumber d8=BigIntNumber.valueOf("3180686561354");
    	System.out.println("d8="+d8.toDecimalString());
    	
    	d3=d8.floorSqrt();
    	System.out.println("Floor Square Root of d8="+d3.toDecimalString());
    	
    	BigIntNumber d9=BigIntNumber.valueOf("31806832018010");
    	System.out.println("d8="+d9.toDecimalString());
    	
    	d3=d9.ceilSqrt();
    	System.out.println("Ceil Square Root of d9="+d3.toDecimalString());
    	
    	BigIntNumber d10=BigIntNumber.valueOf("199345454656124");
    	System.out.println("d10="+d10.toDecimalString());
    	
    	BigIntNumber d11=BigIntNumber.valueOf("56684465612");
    	System.out.println("d11="+d11.toDecimalString());
    	
    	d3=d10.gcd(d11);
    	
    	System.out.println("GCD of d10 and d11="+d3.toDecimalString());
    	
    	System.out.println("Compare d10 and d11="+d10.compareTo(d11));
    	
    	
    }

}
