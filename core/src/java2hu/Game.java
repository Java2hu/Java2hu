package java2hu;

/**
 * Easy singleton reference to the game, to get access to spawn methods and stuff like that.
 * add a static import to this class to easily get the game with getGame()
 */
public class Game
{
	protected static J2hGame singleton;
	
	public static J2hGame getGame()
	{
		return singleton;
	}
}
