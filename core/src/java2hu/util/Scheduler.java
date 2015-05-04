package java2hu.util;


import java.util.WeakHashMap;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.overwrite.J2hObject;

/**
 * Simple scheduler.
 */
public class Scheduler extends J2hObject
{
	public static WeakHashMap<Object, WeakHashMap<Object, Long>> trackers = new WeakHashMap<Object, WeakHashMap<Object, Long>>();
	
	public static void delay(J2hGame stage, final Runnable task, int ticks)
	{
		stage.addTaskGame(task, ticks);
	}
	
	/**
	 * Tracking will allow you to store a few objects
	 * and use that as a time indicator as to when they expire. ie. you want to
	 * add a cooldown to something, you can track(obj, obj, 1000) and you can
	 * use isTracked to check if the 1000ms is over.
	 * This uses a weak reference, so don't worry about anything staying behind that clogs up memory.
	 */
	public static void trackMillis(Object key, Object value, Long millis)
	{
		if (!trackers.containsKey(key))
		{
			trackers.put(key, new WeakHashMap<Object, Long>());
		}
		
		WeakHashMap<Object, Long> map = trackers.get(key);
		
		float ticks = millis * (60f/1000f);
		
		map.put(value, (long) (Game.getGame().getTick() + (ticks)));
	}
	
	public static void track(Object key, Object value, Long ticks)
	{
		if (!trackers.containsKey(key))
		{
			trackers.put(key, new WeakHashMap<Object, Long>());
		}
		
		WeakHashMap<Object, Long> map = trackers.get(key);
		
		map.put(value, (long) Game.getGame().getTick() + ticks);
	}
	
	public static boolean isTracked(Object key, Object value)
	{
		return getTrackedTime(key, value) > 0;
	}
	
	public static long getTrackedTime(Object key, Object value)
	{
		if (!trackers.containsKey(key))
		{
			return 0;
		}
		
		WeakHashMap<Object, Long> map = trackers.get(key);
		
		if (!map.containsKey(value))
		{
			return 0;
		}
		
		long result = map.get(value) - Game.getGame().getTick();
		
		return result < 0 ? 0 : result;
	}
	
	public static class Task extends J2hObject
	{
		// If we're going to expand this.
	}
}
