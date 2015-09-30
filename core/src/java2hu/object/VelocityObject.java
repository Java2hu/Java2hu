package java2hu.object;

/**
 * Object with a velocity.
 */
public abstract class VelocityObject extends FreeStageObject
{
	public VelocityObject(float x, float y)
	{
		super(x, y);
	}

	protected float velocityX;
	protected float velocityY;
	
	@Override
	public void onUpdateDelta(float delta)
	{
		this.setX(getX() - (velocityX * delta));
		this.setY(getY() - (velocityY * delta));
		
		super.onUpdateDelta(delta);
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityX
	 */
	@Deprecated
	public void setVelocityXTick(double velocityX)
	{
		float perSecond = (float) (velocityX * game.currentTPS);
		
		this.velocityX = perSecond;
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityY
	 */
	@Deprecated
	public void setVelocityYTick(double velocityY)
	{
		float perSecond = (float) (velocityY * game.currentTPS);
		
		this.velocityY = perSecond;
	}
	
	/**
	 * Legacy method to return the velocity x from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityXTick()
	{
		return velocityX / game.currentTPS;
	}
	
	/**
	 * Legacy method to return the velocity y from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityYTick()
	{
		return velocityY / game.currentTPS;
	}
	
	public void setVelocityX(double velocityX)
	{
		this.velocityX = (float) velocityX;
	}
	
	public void setVelocityY(double velocityY)
	{
		this.velocityY = (float) velocityY;
	}
	
	public double getVelocityX()
	{
		return velocityX;
	}
	
	public double getVelocityY()
	{
		return velocityY;
	}
	
	/**
	 * Send this object in the direction derived from the degree supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionDegTick(double degree, double speed)
	{
		setDirectionRadsTick(Math.toRadians(degree), speed);
	}
	
	/**
	 * Send this object in the direction derived from the radians supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionRadsTick(double radians, double speed)
	{
		setVelocityXTick((Math.cos(radians) * speed));
		setVelocityYTick((Math.sin(radians) * speed));
	}
	
	/**
	 * Send this object in the direction derived from the degree supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionDeg(double degree, double speed)
	{
		setDirectionRads(Math.toRadians(degree), speed);
	}
	
	/**
	 * Send this object in the direction derived from the radians supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionRads(double radians, double speed)
	{
		setVelocityX((float) (Math.cos(radians) * speed));
		setVelocityY((float) (Math.sin(radians) * speed));
	}
	
	/**
	 * Halts the movement of this object, making it stand still.
	 */
	public void haltMovement()
	{
		setVelocityX(0);
		setVelocityY(0);
	}
}
