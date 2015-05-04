package java2hu.util;

import com.badlogic.gdx.audio.Sound;

public class SoundUtil
{
	/**
	 * Play a sound only once, and allow it only once every interval.
	 * Very useful for sounds made while enemies are on stage, since it won't play concurrent sounds.
	 * @param sound
	 * @param volume
	 * @param identifier
	 * @param interval
	 */
	public static void playSoundSingle(final Sound sound, float volume, String identifier, long interval)
	{
		Getter<Sound> getter = new Getter<Sound>()
		{
			@Override
			public Sound get()
			{
				return sound;
			};
		};
		
		playSoundSingle(getter, volume, identifier, interval);
	}
	
	/**
	 * Play a sound only once, and allow it only once every interval.
	 * Very useful for sounds made while enemies are on stage, since it won't play concurrent sounds.
	 * @param sound
	 * @param volume
	 * @param identifier
	 * @param interval
	 */
	public static void playSoundSingle(final Getter<Sound> getter, float volume, String identifier, long interval)
	{
		if(!Scheduler.isTracked(identifier, identifier))
		{
			Sound sound = getter.get();
			sound.play(volume);
			Scheduler.track(identifier, identifier, interval);
		}
	}
}
