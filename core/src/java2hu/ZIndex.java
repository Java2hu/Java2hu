package java2hu;

/**
 * Z-Index List, all internal code works based on this.
 */
public class ZIndex
{
	/**
	 * Below this is considered background.
	 */
	public static final int BACKGROUND = -1000;
	
	/**
	 * Above this is considered GUI, run last in the loop.
	 */
	public static final int GUI = 100000;
}
