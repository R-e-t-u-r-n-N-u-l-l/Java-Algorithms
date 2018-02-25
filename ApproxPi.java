package algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ApproxPi {
	private static final BigDecimal ONE  = new BigDecimal("1");
    private static final BigDecimal TWO  = new BigDecimal("2");
    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal ACC  = new BigDecimal("1E-10000");
    
    
    //Gauss-Legendre algorithm
    //Very fast and pretty accurate, but consumes a lot of memory
    //Last few digits are not computed correctly
    //Doesn't go above 4679 decimals somehow in Eclipse
    public static BigDecimal piGauss(int decimals) {
    	BigDecimal a = ONE;
    	BigDecimal b = ONE.divide(sqrt(TWO, decimals), decimals, RoundingMode.HALF_UP);
    	BigDecimal t = ONE.divide(FOUR);
    	BigDecimal p = ONE;
    	BigDecimal oldA;
    	
    	while(a.subtract(b).abs().compareTo(ACC) > 0) {
    		oldA = a;
    		
    		a = oldA.add(b).divide(TWO, decimals, RoundingMode.HALF_UP);
    		b = sqrt(oldA.multiply(b), decimals);
    		t = t.subtract(p.multiply(oldA.subtract(a).multiply(oldA.subtract(a))));
    		p = p.multiply(TWO);
    	}
    	
    	return a.add(b).multiply(a.add(b)).divide(FOUR.multiply(t), decimals, RoundingMode.HALF_UP);
    }
    
    
    //Leibniz Series to approximating pi
    //This method is not very accurate 
    public static BigDecimal piLeibniz(int decimals) {
    	BigDecimal pi = ONE;
    	
    	 for(int i = 3; i < decimals; i += 2) {
    		 if((i - 1) % 4 == 0)
    			 pi = pi.add(ONE.divide(new BigDecimal(i), decimals, RoundingMode.HALF_UP));
    		 else
    			 pi = pi.subtract(ONE.divide(new BigDecimal(i), decimals, RoundingMode.HALF_UP));
    	 }
    	
    	return pi.multiply(FOUR);
    }
    
    
    //Viète's formula for approximating pi
    //This method is not very fast
    public static BigDecimal piViete(int decimals) {
        BigDecimal old = sqrt(TWO, decimals);
        BigDecimal pi  = old;
        
        for(int i = 0; i < decimals * 2; i++) {
        	old = sqrt(TWO.add(old), decimals);
        	pi = pi.multiply(old.divide(TWO));
        }
        
        return TWO.divide(pi, decimals, RoundingMode.HALF_UP).multiply(TWO);
    }
    
    
    //Square root for Java's BegDecimal
    public static BigDecimal sqrt(BigDecimal value, int scale) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(value.doubleValue()));
        
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = value.divide(x0, scale, RoundingMode.HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, scale, RoundingMode.HALF_UP);
        }
        
        return x1;
    }
   
    
    public static void main(String[] args) {
        System.out.println(piGauss(4679));
    }
}