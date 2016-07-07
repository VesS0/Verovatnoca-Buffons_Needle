package buffons.needle;


public class RandomGenerator
{
/***Linear congruential generator***/
	
private int N = (int)(System.currentTimeMillis() % r); 
private static final long p = 314159269;
private static final long k = 907633409;
private static final int r = Integer.MAX_VALUE;



public double getNumber() 
{
		N = (int)((p* N + k) % r);
		return N / (double)r; 
}
}