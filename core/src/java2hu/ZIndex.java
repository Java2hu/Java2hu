package java2hu;

/**
 * Z-Index List, all internal code works based on this.
 */
public class ZIndex
{
	/**
	 * Below this is considered background layer 1.
	 * Used for 3d environment backgrounds and the likes.
	 */
	public static final int BACKGROUND_LAYER_1 = -1000;
	
	/**
	 * Below this until {@value #BACKGROUND_LAYER_1} is considered background layer 2.
	 * Used for 2d boss specific or special evented backgrounds.
	 */
	public static final int BACKGROUND_LAYER_2 = -500;
	
	/**
	 * Z-Index where our Boss Aura draws all the backgrounda and applies a shader.
	 */
	public static final int BACKGROUND_AURA = 0;
	
	/**
	 * Above this is considered bullets.
	 */
	public static final int BULLETS = 1000;
	
	/**
	 * Above this is considered GUI, run last in the loop.
	 */
	public static final int GUI = 100000;
}
