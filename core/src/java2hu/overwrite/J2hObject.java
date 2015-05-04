package java2hu.overwrite;

import java2hu.J2hGame;
import java2hu.util.UtilList;

/**
 * Overwrite of @Object that contains references to the game and utils for ease sake.
 */
public class J2hObject
{
	public static J2hGame game;
	public static UtilList list = new UtilList();
	
	public J2hGame game()
	{
		return game;
	}
	
	public J2hGame getGame()
	{
		return game;
	}
	
	public UtilList utils()
	{
		return list;
	}
	
	public UtilList utilList()
	{
		return list;
	}
	
	public UtilList getUtilList()
	{
		return list;
	}
}
