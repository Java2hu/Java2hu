package java2hu;

import java.util.Random;

/**
 * Main class to generate random numbers, and even normal numbers.
 * All these methods make use of a seed, which can be saved to replay the stage back with player's keystrokes. (For this everything has to use this class though, it can't take other random generators into account)
 */
public class RNG
{
	private static Random random;
	private static long seed;
	
	static
	{
		generate();
	}
	
	/**
	 * Generates a random seed and sets it.
	 * @return
	 */
	public static long generate()
	{
		long seed = (long) (Math.random() * Long.MAX_VALUE);
		setSeed(seed);
		return seed;
	}
	
	public static void setSeed(long seed)
	{
		RNG.random = new Random(seed);
		RNG.seed = seed;
	}
	
	public static long getSeed()
	{
		return seed;
	}
	
	public static Random get()
	{
		return random;
	}
	
	public static double random()
	{
		return get().nextDouble();
	}
	
	/**
	 * Returns a random inclusive -1 to exclusive 1
	 */
	public static double randomMirror()
	{
		return (random() * 2d) - 1d;
	}
	
	/**
	 * Returns a multiplier from this tick.
	 * Where (current tick % ticks) == 0 returns 0
	 * And (current tick % ticks) == tick - 1 returns 1
	 */
	public static double multiplier(long ticks, long currentTick)
	{
		return (currentTick % (double)ticks) / ticks;
	}
	
	/**
	 * Returns a boolean depending on the tick.
	 * Where (current tick % ticks) < ticks / 2 returns false, else true.
	 * Same as (multiplierMirror(ticks) < 0 ? false : true)
	 */
	public static boolean booleanMultiplier(long ticks, long currentTick)
	{
		return multiplierMirror(ticks, currentTick) < 0 ? false : true;
	}
	
	/**
	 * Returns a multiplier from this tick.
	 * Where (current tick % ticks) == 0 returns -1
	 * And (current tick % ticks) == tick - 1 returns 1
	 */
	public static double multiplierMirror(long ticks, long currentTick)
	{
		return ((currentTick % (2d * ticks)) / ticks) - 1;
	}
	
	/**
	 * Returns an array of booleans containing true and false, useful to iterate over with 2 possible combinations.
	 */
	public final static boolean[] BOOLS = new boolean[] { true, false };
}
