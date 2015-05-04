package java2hu.touhou.bullet;

import com.badlogic.gdx.graphics.Color;

/**
 * Possible bullet colors for Touhou bullets
 */
public enum ThBulletColor
{
	BLACK(Color.BLACK), 
	RED_DARK(new Color(0.4f, 0f, 0f, 1f)), 
	RED(Color.RED),
	PURPLE(new Color(0.5f, 0f, 0.5f, 1f)), 
	PINK(Color.PINK), 
	BLUE_DARK(new Color(0,0,0.5f, 1f)), 
	BLUE(Color.BLUE), 
	CYAN(Color.CYAN), 
	CYAN_LIGHT(Color.CYAN), 
	GREEN(Color.GREEN), 
	GREEN_LIGHT(Color.GREEN), 
	GREEN_LIGHTER(Color.GREEN), 
	YELLOW(Color.YELLOW), 
	YELLOW_LIGHT(Color.YELLOW), 
	ORANGE(Color.ORANGE), 
	WHITE(Color.WHITE);

	Color color;
	
	private ThBulletColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
}
