package java2hu.gameflow;

import java2hu.Game;
import java2hu.overwrite.J2hObject;


/**
 * A game flow scheme is basically a script, except in code.
 * This scheme will determine how your game works out once you start it.
 * 
 * Do note:
 * You can't run anything Libgdx related in here, because this runs in async (Thus doesn't contain the lwjgl instance)
 * So to work with this is that you need to use the delay functions in @Game.getGame() to run things inside the game.
 * 
 * To make the flow scheme wait for a next action to happen (Like a boss dying, music at a certain point), you can use the setWait method.
 * Do know that anything more specific should probably be put in a SpellCard instance or inside the code of the Boss, since this class is only really for spawning bossing and entities
 * You have to give a BooleanRunnable, so basically when that BooleanRunnable returns true it will wait, and if it's false it will continue.
 * However to make it wait you have to use doWait(), setWait(..) only sets the waiting criteria, doWait() actually makes the thread sleep.
 */
public abstract class GameFlowScheme extends Thread
{
	public GameFlowScheme(String name)
	{
		super(name);
	}
	
	public GameFlowScheme()
	{
		super("Game flow scheme");
	}
	
	private boolean stop = false;
	private WaitConditioner wait = null;
	
	@Override
	public void run()
	{
		System.out.println("Running scheme");
		
		try
		{
			runScheme();
		}
		catch (ThreadDeath e)
		{
			System.out.println(getName() + " terminated.");
			// Do NOTHING, our threads handle being terminated perfectly and stop the flow right away.
		}
		catch(Exception e)
		{
			System.out.println("Exception in " + getName());
			e.printStackTrace();
		}
		
		System.out.println("Done running scheme");
	}
	
	public abstract void runScheme();
	
	public GameFlowScheme getRestartInstance()
	{
		try
		{
			Object obj = this.getClass().newInstance();
			
			return (GameFlowScheme) obj;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Couldn't make a new instance of this gameflow scheme for restart, extend getRestartInstance() and fix it!");
		return null;
	}
	
	public boolean doWait()
	{
		if(wait == null)
			return false;
		
		while(wait.returnTrueToWait())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if(stop)
			{
				stopScheme();
				break;
			}
		}
		
		return wait.returnTrueToWait();
	}
	
	public void setWait(WaitConditioner runnable)
	{
		wait = runnable;
	}
	
	public void wait(WaitConditioner runnable)
	{
		setWait(runnable);
		doWait();
	}
	
	public void waitTicks(final int tick)
	{
		setWait(new WaitConditioner()
		{
			private long startTick = Game.getGame().getTick();
			
			@Override
			public boolean returnTrueToWait()
			{
				return Game.getGame().getTick() < startTick + tick;
			}
		});
		
		doWait();
	}
	
	
	/**
	 * Run a task on the main thread, make it return 
	 * @param completionRunnable
	 */
	public void runSync(final Runnable task)
	{
		Game.getGame().addTask(task, 0);
	}
	
	/**
	 * Run a task on the main thread, make it return 
	 * @param completionRunnable
	 */
	public void runAndWaitSync(final SyncTask task)
	{
		Game.getGame().addTask(task, 0);
		
		wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !task.isCompleted();
			}
		});
	}
	
	/**
	 * Run a task on the main thread, make it return 
	 * @param completionRunnable
	 */
	public <T> T runAndReturnSync(final ReturnSyncTask<T> task)
	{
		Game.getGame().addTask(task, 0);
		
		wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				final long l = Game.getGame().getTick() % 200;
				
				if(!task.isCompleted() && l == 0)
				{
					System.out.println("Something not working? A runAndReturnSync task is still waiting.");
				}
				
				return !task.isCompleted();
			}
		});
		
		return task.getResult();
	}
	
	public static abstract class SyncTask implements Runnable
	{
		private boolean completed = false;
		
		public boolean isCompleted()
		{
			return completed;
		}
		
		public void setCompleted(boolean completed)
		{
			this.completed = completed;
		}
		
		@Override
		public abstract void run();
	}
	
	public static abstract class ReturnSyncTask<T> extends SyncTask
	{
		private T result;
		
		public T getResult()
		{
			return result;
		}
		
		public void setResult(T result)
		{
			this.result = result;
		}
		
		@Override
		public boolean isCompleted()
		{
			return result != null;
		}
		
		/**
		 * Doesn't do anything in this context, this task is completed once the result is set.
		 */
		@Override
		@Deprecated
		public void setCompleted(boolean completed)
		{
			
		}
	}
	
	public static abstract class WaitConditioner extends J2hObject
	{
		public abstract boolean returnTrueToWait();
	}
	
	public void stopScheme()
	{
		try
		{
			stop();
		}
		catch(ThreadDeath e)
		{
			System.out.println("ThreadDeath");
		}
	}
}
