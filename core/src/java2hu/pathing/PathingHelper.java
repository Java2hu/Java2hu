package java2hu.pathing;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Position;
import java2hu.events.pathing.PathingFinishEvent;
import java2hu.object.StageObject;
import java2hu.util.Duration;
import java2hu.util.Duration.Unit;
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
	
	public void tick()
	{
		if(getCurrentPath() != null)
			getCurrentPath().tick();
	}
	
	/**
	 * Sets the objects path to this one, the previous path will be deleted.
	 */
	public void path(Path currentPath)
	{
		this.currentPath = currentPath;
	}
	
	public static class Path
	{
		private ArrayList<Position> path = new ArrayList<Position>();
		
		/**
		 * Returns a read only list of the positions in this path.
		 */
		public ArrayList<Position> getPositions()
		{
			return new ArrayList<Position>(path);
		}
		
		public void addPosition(Position pos)
		{
			path.add(pos);
			recalculate();
		}
		
		public void removePosition(Position pos)
		{
			path.remove(pos);
			recalculate();
		}
		
		private StageObject object;
		
		/**
		 * Creates a path with the specified speed.
		 */
		public Path(StageObject object, float speedPerSecond)
		{
			this(object);
			this.speedPerSecond = speedPerSecond;	
			
			recalculate();
		}
		
		/**
		 * Creates a path over the specified time.
		 */
		public Path(StageObject object, Duration time)
		{
			this(object);
			this.time = time;
			
			recalculate();
		}
		
		private Path(StageObject object)
		{
			this.object = object;
		}
		
		private Float speedPerSecond;
		private Duration time;
		
		private double speed;
		
		/**
		 * Recalculates how long this path will take, and at what speed the boss will moved.
		 * If speed per second is set, it will move at that speed, so the path might take longer or shorter depending on the distance.
		 * If fixed time is set, the object will move at a fixed speed guaranteeing that the object will reach it's destination in that timeframe.
		 */
		public void recalculate()
		{
			if(speedPerSecond != null)
			{
				speed = speedPerSecond;
			}
			else if(time != null)
			{
				speed = getDistance() / time.getValue(Unit.SECOND);
			}
			
			speed = speed / J2hGame.currentTPS;
		}
		
		private int index = 0;
		
		private boolean done = false;
		
		public boolean isDone()
		{
			return done;
		}
		
		private void onDone()
		{
			PathingFinishEvent event = new PathingFinishEvent(object, this);
			Game.getGame().callEvent(event);
		}
		
		public void tick()
		{
			if(done)
				return;
			
			if(index >= path.size())
			{
				done = true;
				onDone();
				return;
			}
			
			Position to = path.get(index);
			
			double angle = MathUtil.getAngle(to, object);
			double rad = Math.toRadians(angle);
			
			if(Double.isNaN(rad))
			{
				index++;
				tick();
				return;
			}
			
			final double x = Math.cos(rad) * speed;
			
			object.setX((float) (object.getX() + x));
			
			final double y = Math.sin(rad) * speed;
			
			object.setY((float) (object.getY() + y));
			
			double distance = MathUtil.getDistance(object, to);
			
			if(distance <= speed)
			{
				index++;
			}
		}
		
		/**
		 * Returns how long this path will still be followed until it's at the end
		 */
		public Duration getTimeLeft()
		{
			double distance = getDistance();
			double ticks = distance / speed;
			
			return Duration.ticks(ticks);
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
		
		/**
		 * Returns the distance over the entire path.
		 * @return
		 */
		public double getDistance()
		{
			if(path.isEmpty() || index >= path.size())
				return 0d;
			
			Position last = path.get(index);
			
			double distance = MathUtil.getDistance(object, last);
			
			for(int i = index + 1; i < path.size(); i++)
			{
				final Position next = path.get(i);
				
				distance += MathUtil.getDistance(last, next);
				last = next;
			}
			
			return distance;
		}
	}
}
