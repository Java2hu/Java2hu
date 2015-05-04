package java2hu;

public enum Border
{
	TOP, BOT, LEFT, RIGHT;
	
	public static Border getBorder(IPosition pos)
	{
		return getBorder(pos.getX(), pos.getY());
	}
	
	public static Border getBorder(float x, float y)
	{
		J2hGame game = Game.getGame();
		Border border = null;
		
		if(x < game.getMinX())
			border = Border.LEFT;
		
		if(x > game.getMaxX())
			border = Border.RIGHT;
		
		if(y > game.getMaxY())
			border = Border.TOP;
		
		if(y < game.getMinY())
			border = Border.BOT;
		
		return border;
	}
}
