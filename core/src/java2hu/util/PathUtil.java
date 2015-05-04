package java2hu.util;

import java.util.ArrayList;

import java2hu.Game;
import java2hu.IPosition;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;

public class PathUtil extends J2hObject
{
	public static PathTask moveCircle(final StageObject obj, IPosition center, float radius, float startDegree, float endDegree, float timePerDegree)
	{
		return moveCircle(obj, center.getX(), center.getY(), radius, startDegree, endDegree, timePerDegree);
	}
	
	public static PathTask moveCircle(StageObject obj, float centerX, float centerY, float radius, float startDegree, float endDegree, float timePerDegree)
	{
		ArrayList<Object> path = new ArrayList<Object>();
		
		path.add((float)(centerX + Math.cos(Math.toRadians(startDegree)) * radius));
		path.add((float)(centerY + Math.sin(Math.toRadians(startDegree)) * radius));
		
		float increment = 1f/timePerDegree;
		int waitTime = (int) (timePerDegree * increment);
		
		for(float i = startDegree + increment; i <= endDegree; i += increment)
		{
			path.add(waitTime); // Time taken.
			
			float x = (float)(centerX + Math.cos(Math.toRadians(i)) * radius);
			float y = (float)(centerY + Math.sin(Math.toRadians(i)) * radius);
			
			path.add(x);
			path.add(y);
		}
		
		return path(obj, path);
	}
	
	/**
	 * Move a stageobject over the presented path.
	 * @param positionsXYTime - ArrayList where every 1/3 is the x coords in a float, every 2/3 is the y coord in a float and 3/3 is the time of ticks to move to the next point in an integer
	 * If the last set has a time, it will be ignored.
	 */
	public static PathTask path(final StageObject obj, final ArrayList<Object> positionsXYTime)
	{
		int time = 0;
		
		final PathTask task = new PathTask();
		
		{
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					task.getTasks().addAll(moveTo(obj, (float)positionsXYTime.get(0), (float)positionsXYTime.get(1), 1).getTasks());
				}
			};

			if(!Game.getGame().isPaused())
				Game.getGame().addTaskGame(run, 1);
			else
				Game.getGame().addTaskPause(run, 1);
		}
		
		for(int i = 0; i < positionsXYTime.size() - 5; i += 3)
		{
			final float x = (float) positionsXYTime.get(i);
			final float y = (float) positionsXYTime.get(i + 1);
			final int waitTime = (int) positionsXYTime.get(i + 2);
			final float nextX = (float) positionsXYTime.get(i + 3);
			final float nextY = (float) positionsXYTime.get(i + 4);
			
			Runnable run = new Runnable()
			{
				@Override
				public void run()
				{
					task.getTasks().addAll(moveTo(obj, nextX, nextY, waitTime).getTasks());
				}
			};
			
			if(!Game.getGame().isPaused())
				Game.getGame().addTaskGame(run, time);
			else
				Game.getGame().addTaskPause(run, time);
			
			task.getTasks().add(run);
			
			time += waitTime;
		}
		
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{
				task.done();
			}
		};
		
		if(!Game.getGame().isPaused())
			Game.getGame().addTaskGame(run, time);
		else
			Game.getGame().addTaskPause(run, time);
		
		return task;
	}
	
	public static PathTask moveTo(final StageObject obj, IPosition pos, int ticks)
	{
		return moveTo(obj, pos.getX(), pos.getY(), ticks);
	}
	
	public static PathTask moveTo(final StageObject obj, float x, float y, int ticks)
	{
		float destinationX = x - obj.getX();
		float destinationY = y - obj.getY();
		
		float sectionX = destinationX / ticks;
		float sectionY = destinationY / ticks;
		
		final PathTask task = new PathTask();
		
		for(int i = 1; i <= ticks; i += 1)
		{
			final float tickX = obj.getX() + sectionX * i;
			final float tickY = obj.getY() + sectionY * i;
			
			Runnable run =  new Runnable()
			{
				@Override
				public void run()
				{
					obj.setX(tickX);
					obj.setY(tickY);
				}
			};
			
			task.getTasks().add(run);
			
			if(!Game.getGame().isPaused())
				Game.getGame().addTaskGame(run, i);
			else
				Game.getGame().addTaskPause(run, i);
		}
		
		Scheduler.delay(Game.getGame(), new Runnable()
		{
			@Override
			public void run()
			{
				task.done();
			}
		}, ticks);
		
		return task;
	}
	
	public static class PathTask
	{
		ArrayList<Runnable> tasks;
		Runnable onDone = null;
		boolean done = false;
		
		public PathTask()
		{
			this.tasks = new ArrayList<Runnable>();
		}
		
		public PathTask(ArrayList<Runnable> tasks)
		{
			this.tasks = tasks;
		}
		
		public void done()
		{
			if(onDone != null)
				onDone.run();
			
			done = true;
		}
		
		public ArrayList<Runnable> getTasks()
		{
			return tasks;
		}
		
		public Runnable getOnDone()
		{
			return onDone;
		}
		
		public void setOnDone(Runnable onDone)
		{
			this.onDone = onDone;
		}

		public boolean isDone()
		{
			return done;
		}
	}
}
