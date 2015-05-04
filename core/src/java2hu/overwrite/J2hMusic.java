package java2hu.overwrite;

import java2hu.Game;
import java2hu.events.EventHandler;
import java2hu.events.EventHandler.EventPriority;
import java2hu.events.EventListener;
import java2hu.events.sound.MusicModifierChangeEvent;

import com.badlogic.gdx.audio.Music;

/**
 * Wrapper class for LibGDX's @Music
 * Automatically applies @J2hGame.getMusicModifier()
 */
public class J2hMusic implements Music, EventListener
{
	private Music m;
	
	public J2hMusic(Music music)
	{
		this.m = music;
		volume = m.getVolume();
		
		Game.getGame().registerEvents(this);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onVolumeChange(MusicModifierChangeEvent event)
	{
		m.setVolume(getOriginalVolume() * event.getNewValue());
	}
	
	public float mod()
	{
		return Game.getGame().getMusicModifier();
	}
	
	@Override
	public void play()
	{
		m.play();
	}

	@Override
	public void pause()
	{
		m.pause();
	}

	@Override
	public void stop()
	{
		m.stop();
	}

	@Override
	public boolean isPlaying()
	{
		return m.isPlaying();
	}

	@Override
	public void setLooping(boolean isLooping)
	{
		m.setLooping(isLooping);
	}

	@Override
	public boolean isLooping()
	{
		return m.isLooping();
	}

	@Override
	public void setVolume(float volume)
	{
		this.volume = volume;
		m.setVolume(volume * mod());
	}

	@Override
	public float getVolume()
	{
		return m.getVolume();
	}
	
	private float volume = 1f;
	
	public float getOriginalVolume()
	{
		return volume;
	}

	@Override
	public void setPan(float pan, float volume)
	{
		m.setPan(pan, volume * mod());
	}

	@Override
	public void setPosition(float position)
	{
		m.setPosition(position);
	}

	@Override
	public float getPosition()
	{
		return m.getPosition();
	}

	@Override
	public void dispose()
	{
		Game.getGame().unregisterEvents(this);
		m.dispose();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener)
	{
		m.setOnCompletionListener(listener);
	}
}
