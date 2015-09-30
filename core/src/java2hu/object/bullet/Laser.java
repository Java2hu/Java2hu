package java2hu.object.bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java2hu.Position;
import java2hu.util.MathUtil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Position of the laser is the tip of the laser, the specified length of the laser will follow in trail of the tip.
 * 
 * Allows a few performance related modifiers, namely:
 * 
 * 
 * Distance on screen - How long the complete laser can be before it starts deleting the last points (-1 to disable)
 * 
 * Units per point - How much distance needs to be passed at least since the last point to allow another points to spawn. (-1 to disable)
 * 
 * Update skip - Skip updating the points every specified tick, ie. skip = 2, tick 1, tick 2(skip), tick 3, tick 4(skip). (-1 to disable)
 * 
 * Max points - The max amount of points the laser can consist of, if you use this and disable length on screen, the laser
 * will shrink if points become more concentrated, and extend if the points get more spread out. (-1 to disable)
 * 
 * If both max points and distance on screen are enabled, it'll create a sort of "hybrid", that will have a max distance, but can become smaller.
 * 
 * NOTE: If you disable both distance on screen and max points, the laser will become a sort of cancer cell, never dies, constantly creating new points but never deleting.
 * You HAVE to manually delete it if it comes to that.
 */
public class Laser extends LaserDrawer
{
	int skip = -1;
	int maxPoints = -1;
	float lengthOnScreen = -1;
	LinkedList<Position> lastPositions;
	ArrayList<Integer> deletedPoints = new ArrayList<Integer>();
	boolean doUpdate = true;
	
	public Laser(final ILaserType type, float x, float y, float lengthOnScreen)
	{
		this(type.getAnimation(), type.getThickness(), type.getHitboxThickness(), x, y, lengthOnScreen);
		
		setDeletionColor(type.getColor());
		
		this.type = new IBulletType()
		{
			@Override
			public Color getEffectColor()
			{
				return type.getColor();
			}
			
			@Override
			public Animation getAnimation()
			{
				return null;
			}
		};
	}
	
	public Laser(LaserAnimation animation, float thickness, float hitboxThickness, float x, float y, float lengthOnScreen)
	{
		super(animation, thickness, hitboxThickness);
		
		this.x = x;
		this.y = y;
		this.lengthOnScreen = lengthOnScreen;

		lastPositions = new LinkedList<Position>();
		
		doMakeNewMesh(false); // We do that for you.
		this.setZIndex(1000);
	}
	
	@Override
	public void checkCollision()
	{

	}
	
	@Override
	public void deletePoint(Position pos)
	{
		Iterator<Position> it = lastPositions.descendingIterator();
		
		int position = 0;
		
		while(it.hasNext())
		{
			if(pos == it.next())
			{
				deletedPoints.add(position);
			}
			
			position++;
		}
	}
	
	@Override
	public void update(long tick)
	{
		super.update(tick);
		
		if(skip > 0 && tick % skip == 0)
			return;
		
		{
			Position last = lastPositions.isEmpty() ? null : lastPositions.getLast();
			
			float distance = (float) (last != null ? MathUtil.getDistance(this, last) : getUnitsPerPoint() + 1);
			
			{
				if(getUnitsPerPoint() <= 0 || distance >= getUnitsPerPoint())
				{
					lastPositions.add(new Position(getX(), getY()));
				}

				getPoints().clear();
				
				Iterator<Position> it = lastPositions.descendingIterator();
				
				int position = 0;
				
				while(it.hasNext())
				{
					if(deletedPoints.contains(position))
					{
						getPoints().add(new Position(Float.NaN, Float.NaN));
					}
					else
					{
						getPoints().add(it.next());
					}
					
					position++;
				}

				makeNewMesh();
			}
		}

		{
			Iterator<Position> it = lastPositions.descendingIterator();
			
			float totalDistance = 0;
			int amount = 0;
			
			Position last = lastPositions.peek();

			while(it.hasNext())
			{
				Position pos = it.next();
				
				if(getMaxPoints() >= 0 && amount > getMaxPoints())
				{
					it.remove();
					continue;
				}
				
				if(getMaximumLengthOnScreen() >= 0 && totalDistance > getMaximumLengthOnScreen())
				{
					it.remove();
					continue;
				}
				
				totalDistance += last != null ? MathUtil.getDistance(pos, last) : 0;
				
				last = pos;
				amount++;
			}
		}
		
		super.checkCollision();
	}
	
	public void clearPath()
	{
		lastPositions.clear();
	}
	
	/**
	 * How long (in normal distance units) the laser will be, if this is disabled, it will fall back to max points.
	 * Use -1 to disable.
	 * Default: none, set in constructor.
	 * @return
	 */
	public float getMaximumLengthOnScreen()
	{
		return lengthOnScreen;
	}
	
	public void setLengthOnScreen(float lengthOnScreen)
	{
		this.lengthOnScreen = lengthOnScreen;
	}
	
	private float unitsPerPoint = 10;
	
	/** Set how many units covered since the last point, will result in a new points being spawned.
	 * Default: 10
	 */
	public void setUnitsPerPoint(float units)
	{
		unitsPerPoint = units;
	}
	
	public float getUnitsPerPoint()
	{
		return unitsPerPoint;
	}
	
	/**
	 * Skip updating the positions every specified tick. (ie. skip = 2, tick 1, tick 2 (skip), tick 3, tick 4 (skip), etc...)
	 * Use -1 for no update skip.
	 * Default: -1 (disabled)
	 * @param skip
	 */
	public void setUpdateSkip(int skip)
	{
		this.skip = skip;
	}
	
	public int getUpdateSkip()
	{
		return skip;
	}
	
	/**
	 * Set how many points your laser can have max, if the length on screen is disabled, it will fall back to this.
	 * Use -1 for no limit
	 * Default: -1 (No limit)
	 * @param maxPoints
	 */
	public void setMaxPoints(int maxPoints)
	{
		this.maxPoints = maxPoints;
	}
	
	public int getMaxPoints()
	{
		return maxPoints;
	}
	
	public void doUpdate(boolean doUpdate)
	{
		this.doUpdate = doUpdate;
	}
	
	public boolean doUpdate()
	{
		return doUpdate;
	}
	
	@Override
	public void setDirectionDegTick(double degree, double speed)
	{
		super.setDirectionDegTick(degree, speed);
	}
	
	@Override
	public void setDirectionRadsTick(double radians, double speed)
	{
		super.setDirectionRadsTick(radians, speed);
	}
}
