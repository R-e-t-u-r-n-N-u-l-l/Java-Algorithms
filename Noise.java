package terrain;

import java.util.Random;

public class Noise {

	private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
	private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
	
	private static short perm[] = new short[512];
	private static short permMod12[] = new short[512];
	
	 private static short p[] = new short[256];
	
	  static {
		long seed = Math.round(Math.random() * 1000000000000l);
		Random r = new Random(seed);
		System.out.println(seed);
	    for(int i = 0; i < 256; i++) {
	    	p[i] = (short) r.nextInt(256);
	    }
	    for(int i=0; i<512; i++) {
	    	perm[i] = p[i & 255];
	    	permMod12[i] = (short)(perm[i] % 12);
	    }
	  }
	  
	  private static Grad grad2[] = {
			  new Grad(1, 1),
			  new Grad(-1, 1),
			  new Grad(1, -1),
			  new Grad(-1, -1),
              new Grad(1, 0),
              new Grad(-1, 0),
              new Grad(1, 0),
              new Grad(-1, 0),
              new Grad(0, 1),
              new Grad(0, -1),
              new Grad(0, 1),
              new Grad(0, -1)
      };
	
	  private static double dot(Grad g, double x, double y) {
		    return g.x * x + g.y * y;
	  }
	  
	public static double noise(double xin, double yin) {
	    double n0, n1, n2; // Noise contributions from the three corners
	    // Skew the input space to determine which simplex cell we're in
	    double s = (xin + yin) * F2; // Hairy factor for 2D
	    int i = fastfloor(xin + s);
	    int j = fastfloor(yin + s);
	    double t = (i + j) * G2;
	    double X0 = i - t; // Unskew the cell origin back to (x,y) space
	    double Y0 = j - t;
	    double x0 = xin - X0; // The x,y distances from the cell origin
	    double y0 = yin - Y0;
	    // For the 2D case, the simplex shape is an equilateral triangle.
	    // Determine which simplex we are in.
	    int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
	    if(x0 > y0) {
	    	i1=1; j1=0; // lower triangle, XY order: (0,0)->(1,0)->(1,1)
	    } else {
	    	i1=0; j1=1; // upper triangle, YX order: (0,0)->(0,1)->(1,1)
	    }
	    // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
	    // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
	    // c = (3-sqrt(3))/6
	    double x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
	    double y1 = y0 - j1 + G2;
	    double x2 = x0 - 1.0 + 2.0 * G2; // Offsets for last corner in (x,y) unskewed coords
	    double y2 = y0 - 1.0 + 2.0 * G2;
	    // Work out the hashed gradient indices of the three simplex corners
	    int ii = i & 255;
	    int jj = j & 255;
	    int gi0 = permMod12[ii + perm[jj]];
	    int gi1 = permMod12[ii + i1 + perm[jj + j1]];
	    int gi2 = permMod12[ii + 1 + perm[jj + 1]];
	    // Calculate the contribution from the three corners
	    double t0 = 0.5 - x0 * x0 - y0 * y0;
	    if(t0 < 0) 
	    	n0 = 0.0;
	    else {
	      t0 *= t0;
	      n0 = t0 * t0 * dot(grad2[gi0], x0, y0);  // (x,y) of grad3 used for 2D gradient
	    }
	    double t1 = 0.5 - x1 * x1 - y1 * y1;
	    if(t1 < 0) 
	    	n1 = 0.0;
	    else {
	      t1 *= t1;
	      n1 = t1 * t1 * dot(grad2[gi1], x1, y1);
	    }
	    double t2 = 0.5 - x2 * x2 - y2 * y2;
	    if(t2 < 0) 
	    	n2 = 0.0;
	    else {
	      t2 *= t2;
	      n2 = t2 * t2 * dot(grad2[gi2], x2, y2);
	    }
	    // Add contributions from each corner to get the final noise value.
	    // The result is scaled to return values in the interval [-1,1].
	    return 70.0 * (n0 + n1 + n2);
	  }
	
	public static int fastfloor(double x) {
	    int xi = (int) x;
	    return x < xi ? xi - 1 : xi;
	  }
	
	private static class Grad {
	    double x, y;

	    Grad(double x, double y) {
	      this.x = x;
	      this.y = y;
	    }
	  }
	
	public static float[][] generateSimplexNoise(int width, int height) {
	      float[][] simplexnoise = new float[width][height];
	      float frequency = 10f / (float) width;
	      
	      for(int x = 0; x < width; x++) {
	         for(int y = 0; y < height; y++) {
	            simplexnoise[x][y] = (float) noise(x * frequency,y * frequency);
	            simplexnoise[x][y] = (simplexnoise[x][y] + 1) / 2;   //generate values between 0 and 1
	         }
	      }
	      return simplexnoise;
	}
}