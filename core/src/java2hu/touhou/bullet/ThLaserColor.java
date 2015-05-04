package java2hu.touhou.bullet;

import com.badlogic.gdx.graphics.Color;

/**
 * Possible bullet colors for Touhou bullets
 */
public enum ThLaserColor
{
	BLACK(Color.BLACK), 
	BLUE_LIGHT(Color.BLUE), 
	BLUE(new Color(0,0,0.5f, 1f)), 
	CYAN_DARK(Color.CYAN), 
	CYAN(Color.CYAN), 
	GREEN(Color.GREEN), 
	GREEN_LIGHT(Color.GREEN), 
	GREEN_LIGHTER(Color.GREEN), 
	ORANGE(Color.ORANGE),
	PINK(Color.PINK), 
	PURPLE(new Color(0.5f, 0f, 0.5f, 1f)), 
	RED(new Color(0.4f, 0f, 0f, 1f)), 
	RED_LIGHT(Color.RED),
	WHITE(Color.WHITE),
	YELLOW_DARK(Color.YELLOW), 
	YELLOW(Color.YELLOW),
	LIGHTNING(Color.WHITE);
	 
	Color color;
	
	private ThLaserColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
}
