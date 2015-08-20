package java2hu.plugin.bullets;

import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

public class AcceleratingBullet extends GetterPlugin<Bullet>
{
	/**
	 * Multiplies the X and Y velocity every tick by a specific multiplier.
	 * @param getter
	 */
	public AcceleratingBullet(Getter<Bullet> getter, float multiplier)
	{
		super(getter);
		
		setMultiplier(multiplier);
	}
	
	/**
	 * Multiplies the X and Y velocity every tick by a specific multiplier.
	 * @param getter
	 */
	public AcceleratingBullet(Getter<Bullet> getter, float multiplierX, float multiplierY)
	{
		super(getter);
		
		setMultiplier(multiplierX, multiplierY);
	}
	
	private float multiplierX;
	private float multiplierY;
	
	public float getMultiplierX()
	{
		return multiplierX;
	}
	
	public float getMultiplierY()
	{
		return multiplierY;
	}
	
	/**
	 * Sets both multiplierX and multiplierY to the same value.
	 * @param multiplier
	 */
	public void setMultiplier(float multiplier)
	{
		setMultiplier(multiplier, multiplier);
	}
	
	/**
	 * Sets both multiplierX and multiplierY
	 * @param multiplier
	 */
	public void setMultiplier(float multiplierX, float multiplierY)
	{
		this.multiplierX = multiplierX;
		this.multiplierY = multiplierY;
	}
	
	public void setMultiplierX(float multiplierX)
	{
		this.multiplierX = multiplierX;
	}
	
	public void setMultiplierY(float multiplierY)
	{
		this.multiplierY = multiplierY;
	}
	
	@Override
	public void update(StageObject object, long tick)
	{
		final Bullet b = (Bullet)object;
		
		b.setVelocityX(b.getVelocityX() * getMultiplierX());
		b.setVelocityY(b.getVelocityY() * getMultiplierY());
	}
}
