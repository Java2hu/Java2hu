package java2hu.pathing;

import java.util.ArrayList;
import java2hu.IPosition;
import java2hu.object.StageObject;
import java2hu.pathing.PathingHelper.Path;
import java2hu.util.MathUtil;

import com.google.common.collect.Lists;

/**
 * A continuous path based on a velocity.
 */
public class VelocityPath extends Path
{
	public VelocityPath(StageObject object)
	{
		this(object, 0f, 0f);
	}
	
	public VelocityPath(StageObject object, float velocityX, float velocityY)
	{
		super(object, 0);
		
		this.velocityX = velocityX;
		this.velocityY = velocityY;
	}
	
	private float velocityX;
	private float velocityY;
	
	private ArrayList<IPosition> pos = Lists.newArrayList();
	
	@Override
	public ArrayList<IPosition> getPositions()
	{
		return pos;
	}
	
	@Override
	public void tickDelta(float delta)
	{
		super.tickDelta(delta);
		
		object.setX(object.getX() - (velocityX * delta));
		object.setY(object.getY() - (velocityY * delta));
	}
	
	/**
	 * Returns the distance of the velocity vector instead.
	 * @return
	 */
	@Override
	public double getDistance()
	{
		return MathUtil.getDistance(0, 0, getVelocityX(), getVelocityY());
	}
	
	@Override
	public void tick()
	{

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
