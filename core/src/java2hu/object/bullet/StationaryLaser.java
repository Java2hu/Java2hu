package java2hu.object.bullet;

import java2hu.IPosition;
import java2hu.Position;
import java2hu.touhou.bullet.ThStationaryLaserType;
import java2hu.util.Duration;
import java2hu.util.MathUtil;


public class StationaryLaser extends LaserDrawer
{
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	
	public StationaryLaser(ThStationaryLaserType type)
	{
		this(type, type.getThickness(), type.getHitboxThickness());
	}
	
	public StationaryLaser(ThStationaryLaserType type, float thickness, float hitboxThickness)
	{
		this(type.getAnimation(), thickness, hitboxThickness);
	}
	
	public StationaryLaser(LaserAnimation ani, float thickness, float hitboxThickness)
	{
		this(ani, thickness, hitboxThickness, 0, 0, 0, 0);
	}
	
	public StationaryLaser(ThStationaryLaserType type, float thickness, float hitboxThickness, float startX, float startY, float endX, float endY)
	{
		this(type.getAnimation(), thickness, hitboxThickness, startX, startY, endX, endY);
	}
	
	public StationaryLaser(LaserAnimation ani, float thickness, float hitboxThickness, float startX, float startY, float endX, float endY)
	{
		super(ani, thickness, hitboxThickness);
		
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	@Override
	public void update(float second)
	{
		super.update(second);
		
		if (spawnAnimation)
		{
			current = current.add(Duration.seconds(second));

			double mul = getSpawnAnimationModifier();
			
			System.out.println(mul);
			
			if (mul >= 1f)
				spawnAnimation = false;
			
			super.setThickness(getActualThickness() * mul);
			super.setHitboxThickness(getActualHitboxThickness() * mul);
		}
	}
	
	private boolean spawnAnimation = false;
	private double actualThickness, actualHitboxThickness;
	
	private Duration hitboxActive;
	private Duration finished;
	private Duration current;
	
	@Override
	public void checkCollision()
	{
		if (!isHitboxActive())
			return;
		
		super.checkCollision();
	}
	
	private double getSpawnAnimationModifier()
	{
		double raw = current.getValue() / finished.getValue();
		
		return Math.min(1, Math.max(0, raw));
	}
	
	private boolean isHitboxActive()
	{
		if (!spawnAnimation)
			return true;
		
		return hitboxActive.getValue() <= current.getValue();
	}
	
	@Override
	public void setThickness(double thickness)
	{
		if (spawnAnimation)
		{
			actualThickness = thickness;
			super.setThickness(thickness * getSpawnAnimationModifier());
			return;
		}
		
		super.setThickness(thickness);
	}
	
	@Override
	public void setHitboxThickness(double hitboxThickness)
	{
		if (spawnAnimation)
		{
			actualHitboxThickness = hitboxThickness;
			super.setHitboxThickness(actualHitboxThickness * getSpawnAnimationModifier());
			return;
		}
		
		super.setHitboxThickness(hitboxThickness);
	}
	
	/**
	 * While the laser is going through a spawn animation, {@link #getThickness()} will return the scaled thickness, but this will always return the actual thickness.
	 * @return The actual thickness of this stationary laser.
	 */
	public double getActualThickness()
	{
		if (spawnAnimation)
			return actualThickness;
		
		return getThickness();
	}
	
	@Override
	public double getThickness()
	{
		return super.getThickness();
	}
	
	/**
	 * While the laser is going through a spawn animation, {@link #getHitboxThickness()} will return the scaled hitbox thickness, but this will always return the actual hitbox thickness.
	 * @return The actual hitbox thickness of this stationary laser.
	 */
	public double getActualHitboxThickness()
	{
		if (spawnAnimation)
			return actualHitboxThickness;
		
		return getHitboxThickness();
	}
	
	@Override
	public double getHitboxThickness()
	{
		return super.getHitboxThickness();
	}
	
	/**
	 * Plays the stationary laser spawn animation, growing from 0 to full size.
	 * @param enableHitbox From what time the hitbox is enabled, and collision will be checked. </br>(Note that if the animation is over before this time has passed, it will be enabled regardless)
	 * @param perTick How long the animation lasts.
	 */
	public void spawnAnimation(Duration enableHitbox, Duration duration)
	{
		actualThickness = getThickness();
		actualHitboxThickness = getHitboxThickness();
		
		spawnAnimation = true;
		
		hitboxActive = enableHitbox;
		finished = duration;
		current = Duration.zero();
	}
	
	@Override
	public boolean doDelete()
	{
		return false;
	}
	
	@Override
	public void deletePoint(Position pos)
	{
		
	}
	
	private void updatePoints()
	{
		getPoints().clear();
		
		addPoint(getStartX(), getStartY());
		addPoint(getEndX(), getEndY());
	}
	
	public void setStartAndEnd(IPosition start, IPosition end)
	{
		setStartAndEnd(start.getX(), start.getY(), end.getX(), end.getY());
	}
	
	public void setStartAndEnd(float startX, float startY, float endX, float endY)
	{
		setStart(startX, startY);
		setEnd(endX, endY);
	}
	
	public void setStartX(float startX)
	{
		this.startX = startX;
		updatePoints();
	}
	
	public float getStartX()
	{
		return startX;
	}
	
	public void setStartY(float startY)
	{
		this.startY = startY;
		updatePoints();
	}
	
	public float getStartY()
	{
		return startY;
	}
	
	/**
	 * @return A read only position of the start coordinates of this stationary laser.
	 */
	public Position getStart()
	{
		return new Position(getStartX(), getStartY());
	}
	
	public void setStart(IPosition start)
	{
		setStart(start.getX(), start.getY());
	}
	
	public void setStart(float x, float y)
	{
		setStartX(x);
		setStartY(y);
	}
	
	public void setEndX(float endX)
	{
		this.endX = endX;
		
		updatePoints();
	}
	
	public float getEndX()
	{
		return endX;
	}
	
	public void setEndY(float endY)
	{
		this.endY = endY;
		
		updatePoints();
	}
	
	public float getEndY()
	{
		return endY;
	}
	
	/**
	 * @return A read only position of the end coordinates of this stationary laser.
	 */
	public Position getEnd()
	{
		return new Position(getEndX(), getEndY());
	}
	
	public void setEnd(IPosition end)
	{
		setEnd(end.getX(), end.getY());
	}
	
	public void setEnd(float x, float y)
	{
		setEndX(x);
		setEndY(y);
	}
	
	/**
	 * Extends the laser at the end coordinates.
	 * @param size How much pixels the laser is extended in the end direction.
	 */
	public void extendAtEnd(float size)
	{
		double distance = MathUtil.getDistance(getStart(), getEnd());
		double angle = MathUtil.getAngle(getEnd(), getStart());
		double rad = Math.toRadians(angle);
		
		double newDistance = distance + size;
		
		setEnd(new Position(getStartX() + (Math.cos(rad) * newDistance), getStartY() + (Math.sin(rad) * newDistance)));
	}
	
	/**
	 * Extends the laser at the start coordinates.
	 * @param size How much pixels the laser is extended in the start direction.
	 */
	public void extendAtStart(float size)
	{
		double distance = MathUtil.getDistance(getStart(), getEnd());
		double angle = MathUtil.getAngle(getStart(), getEnd());
		double rad = Math.toRadians(angle);
		
		double newDistance = distance + size;
		
		setStart(new Position(getEndX() + (Math.cos(rad) * newDistance), getEndY() + (Math.sin(rad) * newDistance)));
	}
	
	public void setDirectionDeg(float x, float y, float degree, float size)
	{
		setDirectionRads(x, y, (float) Math.toRadians(degree), size);
	}
	
	public void setDirectionRads(float x, float y, float rad, float size)
	{
		setStart(x, y);
		setEnd((float) (x + (Math.cos(rad) * size)), (float) (y + (Math.sin(rad) * size)));
	}
}
