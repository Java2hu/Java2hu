package java2hu.object.bullet;

import java2hu.object.bullet.LaserDrawer.LaserAnimation;

import com.badlogic.gdx.graphics.Color;

public interface ILaserType
{
	public LaserAnimation getAnimation();
	
	public float getThickness();
	public float getHitboxThickness();
	public Color getColor();
}
