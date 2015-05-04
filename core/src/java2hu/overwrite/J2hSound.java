package java2hu.overwrite;

import java2hu.Game;

import com.badlogic.gdx.audio.Sound;

/**
 * Wrapper class for LibGDX's @Sound
 * Automatically applies @J2hGame.getSoundModifier()
 */
public class J2hSound implements Sound
{
	private Sound s;
	
	public J2hSound(Sound sound)
	{
		this.s = sound;
	}
	
	private float mod()
	{
		return Game.getGame().getSoundModifier();
	}

	@Override
	public long play()
	{
		return s.play(mod());
	}

	@Override
	public long play(float volume)
	{
		return s.play(volume * mod());
	}

	@Override
	public long play(float volume, float pitch, float pan)
	{
		return s.play(volume * mod(), pitch, pan);
	}

	@Override
	public long loop()
	{
		return s.loop(mod());
	}

	@Override
	public long loop(float volume)
	{
		return s.loop(volume * mod());
	}

	@Override
	public long loop(float volume, float pitch, float pan)
	{
		return s.loop(volume * mod(), pitch, pan);
	}

	@Override
	public void stop()
	{
		s.stop();
	}

	@Override
	public void pause()
	{
		s.pause();
	}

	@Override
	public void resume()
	{
		s.resume();
	}

	@Override
	public void dispose()
	{
		s.dispose();
	}

	@Override
	public void stop(long soundId)
	{
		s.stop();
	}

	@Override
	public void pause(long soundId)
	{
		s.pause();
	}

	@Override
	public void resume(long soundId)
	{
		s.resume();
	}

	@Override
	public void setLooping(long soundId, boolean looping)
	{
		s.setLooping(soundId, looping);
	}

	@Override
	public void setPitch(long soundId, float pitch)
	{
		s.setPitch(soundId, pitch);
	}

	@Override
	public void setVolume(long soundId, float volume)
	{
		s.setVolume(soundId, volume * mod());
	}

	@Override
	public void setPan(long soundId, float pan, float volume)
	{
		s.setPan(soundId, pan, volume * mod());
	}

	@Override
	public void setPriority(long soundId, int priority)
	{
		s.setPriority(soundId, priority);
	}
}
