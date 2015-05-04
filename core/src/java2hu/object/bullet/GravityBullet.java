package java2hu.object.bullet;

import com.badlogic.gdx.graphics.g2d.Animation;



/**
 * Bullet that constantly gets de-accelerated in the y axis (So it will go faster downwards) until it hit's it's terminal velocity
 * If you insert negative values, it will be repelled though.
 */
public class GravityBullet extends Bullet
{
	private float decrease;
	private float terminalVelocity;
	
	public GravityBullet(Animation scheme, float x, float y, float decrease, float terminalVelocity)
	{
		super(scheme, x, y);
		
		setDecrease(decrease);
		setTerminalVelocity(terminalVelocity);
	}

	public GravityBullet(IBulletType type, float x, float y, float decrease, float terminalVelocity)
	{
		this(type.getAnimation(), x, y, decrease, terminalVelocity);
	}

	public float getTerminalVelocity()
	{
		return terminalVelocity;
	}

	public void setTerminalVelocity(float terminalVelocity)
	{
		this.terminalVelocity = terminalVelocity;
	}

	public float getDecrease()
	{
		return decrease;
	}

	public void setDecrease(float decrease)
	{
		this.decrease = decrease;
	}

	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		if(getVelocityYTick() > getTerminalVelocity())
			return;
		
		setVelocityYTick(getVelocityYTick() + getDecrease() * 60);
	}
}
