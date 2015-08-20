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
	public void setVelocityXTick(float velocityX)
	{
		float perSecond = velocityX * game.currentTPS;
		
		this.velocityX = perSecond;
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityY
	 */
	@Deprecated
	public void setVelocityYTick(float velocityY)
	{
		float perSecond = velocityY * game.currentTPS;
		
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
	
	public void setVelocityX(float velocityX)
	{
		this.velocityX = velocityX;
	}
	
	public void setVelocityY(float velocityY)
	{
		this.velocityY = velocityY;
	}
	
	public float getVelocityX()
	{
		return velocityX;
	}
	
	public float getVelocityY()
	{
		return velocityY;
	}
	
	/**
	 * Send this object in the direction derived from the degree supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionDegTick(float degree, float speed)
	{
		setDirectionRadsTick((float) Math.toRadians(degree), speed);
	}
	
	/**
	 * Send this object in the direction derived from the radians supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionRadsTick(float radians, float speed)
	{
		setVelocityXTick((float) (Math.cos(radians) * speed));
		setVelocityYTick((float) (Math.sin(radians) * speed));
	}
	
	/**
	 * Send this object in the direction derived from the degree supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionDeg(float degree, float speed)
	{
		setDirectionRads((float) Math.toRadians(degree), speed);
	}
	
	/**
	 * Send this object in the direction derived from the radians supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionRads(float radians, float speed)
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
