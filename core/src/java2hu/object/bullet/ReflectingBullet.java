package java2hu.object.bullet;

import java2hu.Border;
import java2hu.Game;
import java2hu.J2hGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;



/**
 * Bullet that reflects when touching the sides of the game
 * Extend the method onReflect(int time) to do special stuff
 */
public class ReflectingBullet extends Bullet
{
	private int reflectTimes = 0;
	private int maxReflectTimes = 0;
	
	/**
	 * @param maxReflectTimes - How many times to reflect, insert -1 for infinite.
	 */
	public ReflectingBullet(Animation scheme, float x, float y, int maxReflectTimes)
	{
		this(scheme, null, x, y, maxReflectTimes);
	}
	
	/**
	 * @param maxReflectTimes - How many times to reflect, insert -1 for infinite.
	 */
	public ReflectingBullet(Animation scheme, Color color, float x, float y, int maxReflectTimes)
	{
		super(scheme, color, x, y);
		
		setMaxReflectTimes(maxReflectTimes);
	}
	
	public ReflectingBullet(IBulletType type, float x, float y, int maxReflectTimes)
	{
		super(type, x, y);
		
		setMaxReflectTimes(maxReflectTimes);
	}

	public int getReflectTimes()
	{
		return reflectTimes;
	}

	public void setReflectTimes(int reflectTimes)
	{
		this.reflectTimes = reflectTimes;
	}

	public int getMaxReflectTimes()
	{
		return maxReflectTimes;
	}

	public void setMaxReflectTimes(int maxReflectTimes)
	{
		this.maxReflectTimes = maxReflectTimes;
	}
	
	private Border lastBorder;
	
	@Override
	public void onUpdateDelta(float delta)
	{
		super.onUpdateDelta(delta);
		
		J2hGame game = Game.getGame();
		
		if(getReflectTimes() < getMaxReflectTimes())
		{
			Border border = Border.getBorder(this);
			
			if(!game.inBoundary(getX(), getY()))
			{
				if(lastBorder != null && lastBorder == border)
					return;
				
				setReflectTimes(getReflectTimes() + 1);
				
				boolean x = getX() < game.getMinX() || getX() > game.getMaxX();
			
				if(doReflect(border, getReflectTimes()))
				{
					if(x)
						setVelocityX(-getVelocityX());
					else
						setVelocityY(-getVelocityY());
					
					onReflect(border, getReflectTimes());
					
					lastBorder = border;
				}
			}
			else
			{
				lastBorder = null;
			}
		}
	}
	
	public boolean doReflect(Border border, int reflectAmount)
	{
		return true;
	}
	
	public void onReflect(Border border, int reflectAmount)
	{
		
	}
}
