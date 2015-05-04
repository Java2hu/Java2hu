package java2hu.object.bullet;

import java2hu.object.bullet.LaserDrawer.LaserAnimation;

public interface ILaserType
{
	public LaserAnimation getAnimation();
	
	public float getThickness();
	public float getHitboxThickness();
}
