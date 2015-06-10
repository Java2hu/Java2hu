package java2hu.movement;

import java.util.ArrayList;
import java2hu.Position;
import java2hu.object.StageObject;
import java2hu.util.Duration;
import java2hu.util.MathUtil;

/**
 * Simple pathing class for stage objects.
 * Makes the specified object move over the specified route at either a specific speed, or in a specific time. 
 */
public class PathingHelper
{
	private Path currentPath;
	
	public Path getCurrentPath()
	{
		return currentPath;
	}
	
	public void setCurrentPath(Path currentPath)
	{
		this.currentPath = currentPath;
	}
	
	public static class Path extends ArrayList<Position>
	{
		private StageObject object;
		
		/**
		 * Creates a path with the specified speed.
		 */
		public Path(StageObject object, float speedPerSecond)
		{
			this(object);
			this.speedPerSecond = speedPerSecond;	
		}
		
		/**
		 * Creates a path over the specified time.
		 */
		public Path(StageObject object, Duration time)
		{
			this(object);
			this.time = time;
		}
		
		private Path(StageObject object)
		{
			this.object = object;
			recalculate();
		}
		
		private Float speedPerSecond;
		private Duration time;
		
		private double speed;
		
		public void recalculate()
		{
			if(speedPerSecond != null)
			{
				speed = speedPerSecond;
			}
			else if(time != null)
			{
				speed = getDistance() / time.toTicks();
			}
		}
		
		private int index = 0;
		
		public void tick()
		{
			Position to = get(index);
			
			double angle = MathUtil.getAngle(object, to);
			double rad = Math.toRadians(angle);
			
			object.setX((float) (object.getX() + (Math.cos(rad) * speed)));
			object.setY((float) (object.getY() + (Math.sin(rad) * speed)));
			
			double distance = MathUtil.getDistance(object, to);
			
			if(distance <= (speed * 1.25f))
			{
				index++;
			}
		}
		
		/**
		 * Sets the path to a speed per second type.
		 * Forces a recalculate of the time taken.
		 */
		public void setSpeedPerSecond(Float speedPerSecond)
		{
			this.speedPerSecond = speedPerSecond;
			this.time = null;
			recalculate();
		}
		
		/**
		 * Sets the path to a fixed duration type.
		 * Forces a recalculate of the time taken.
		 */
		public void setTime(Duration time)
		{
			this.time = time;
			this.speedPerSecond = null;
			recalculate();
		}
		
		public double getDistance()
		{
			if(isEmpty())
				return 0d;
			
			Position last = this.get(0);
			
			double distance = MathUtil.getDistance(object, last);
			
			for(int i = 0; i < size(); i++)
			{
				final Position next = get(i);
				
				distance += MathUtil.getDistance(last, next);
				last = next;
			}
			
			return distance;
		}
	}
}
