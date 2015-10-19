package java2hu.pathing;

import java.util.ArrayList;
import java.util.function.Consumer;
import java2hu.IPosition;
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
	public PathingHelper(StageObject owner)
	{
		this.owner = owner;
	}
	
	private StageObject owner;
	
	/**
	 * @return The StageObject this pathing helper is attached to.
	 */
	public StageObject getOwner()
	{
		return owner;
	}
	
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
	
	public void tickDelta(float delta)
	{
		if(getCurrentPath() != null)
			getCurrentPath().tickDelta(delta);
	}
	
	/**
	 * Sets the objects path to this one, the previous path will be deleted.
	 */
	public void path(Path currentPath)
	{
		this.currentPath = currentPath;
	}
	
	/**
	 * Creates a path moving to a specific position using {@link SinglePositionPath}.
	 * @param destination The position to move to.
	 * @param duration How long the pathing will take.
	 * @return The SinglePositionPath this helper is set to path to.
	 */
	public SinglePositionPath path(IPosition destination, Duration duration)
	{
		SinglePositionPath path = new SinglePositionPath(getOwner(), destination, duration);
		
		path(path);
		
		return path;
	}
	
	/**
	 * Uses {@link SimpleTouhouBossPath} to path to a random location on the top of the screen close to the player.
	 * @param duration How long the pathing will take.
	 * @return The SimpleTouhouBossPath this helper is set to path to.
	 */
	public SimpleTouhouBossPath pathAbovePlayer(Duration duration)
	{
		SimpleTouhouBossPath path = new SimpleTouhouBossPath(getOwner());
		
		path(path);
		
		return path;
	}
	
	public static class Path
	{
		private ArrayList<IPosition> path = new ArrayList<IPosition>();
		private ArrayList<Consumer<Path>> onDone = new ArrayList<Consumer<Path>>();
		
		/**
		 * Returns a list of the positions in this path.
		 */
		public ArrayList<IPosition> getPositions()
		{
			return path;
		}

		public void addPosition(IPosition pos)
		{
			getPositions().add(pos);
			recalculate();
		}
		
		public void removePosition(IPosition pos)
		{
			getPositions().remove(pos);
			recalculate();
		}
		
		protected StageObject object;
		
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
			
//			speed = speed;
		}
		
		private int index = 0;
		
		private boolean done = false;
		
		public boolean isDone()
		{
			return done;
		}
		
		private void done()
		{
			onDone.stream().forEach((c) -> c.accept(this));
		}
		
		/**
		 * Registers a consumer to be called on the finishing of this path.
		 */
		public void onDone(Consumer<Path> consumer)
		{
			onDone.add(consumer);
		}
		
		private Double lastAngle = null;
		
		public Double getLastAngle()
		{
			return lastAngle;
		}
		
		public void tickDelta(float delta)
		{
			if(done)
				return;
			
			if(index >= getPositions().size())
			{
				done = true;
				done();
				return;
			}
			
			IPosition to = getPositions().get(index);
			
			double angle = MathUtil.getAngle(to, object);
			double rad = Math.toRadians(angle);
			
			if(Double.isNaN(rad))
			{
				index++;
				tick();
				return;
			}
			
			lastAngle = angle;
			
			double distanceBefore = MathUtil.getDistance(object, to);
			
			final double x = Math.cos(rad) * speed * delta;
			
			object.setX((float) (object.getX() + x));
			
			final double y = Math.sin(rad) * speed * delta;
			
			object.setY((float) (object.getY() + y));
			
			double distance = MathUtil.getDistance(object, to);
			
			if(distanceBefore <= distance) // Check if it passed it's destination in this movement.
			{
				index++;
			}
		}
		
		public void tick()
		{
			
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
			if(getPositions().isEmpty() || index >= getPositions().size())
				return 0d;
			
			IPosition last = getPositions().get(index);
			
			double distance = MathUtil.getDistance(object, last);
			
			for(int i = index + 1; i < getPositions().size(); i++)
			{
				final IPosition next = getPositions().get(i);
				
				distance += MathUtil.getDistance(last, next);
				last = next;
			}
			
			return distance;
		}
	}
}
