package java2hu.object;

import java2hu.Game;
import java2hu.events.EventHandler;
import java2hu.events.EventListener;
import java2hu.events.game.PauseGameEvent;
import java2hu.events.game.UnPauseGameEvent;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/**
 * Stage object holding a MediaPlayer instance, which will automatically handle pausing/unpausing.
 */
public abstract class BGMPlayer extends StageObject implements EventListener
{
	private Music bgm;
	private boolean pauseOnPause;
	
	public BGMPlayer(final Music mediaPlayer)
	{
		this(mediaPlayer, false);
	}
	
	public BGMPlayer(final Music mediaPlayer, boolean pauseOnPause)
	{
		super(0, 0);
		this.pauseOnPause = pauseOnPause;
		this.bgm = mediaPlayer;
		
		addDisposable(new Disposable()
		{
			@Override
			public void dispose()
			{
				mediaPlayer.dispose();
			}
		});
		
		if(pauseOnPause)
			game.registerEvents(this);
	}
	
	boolean wasPlaying = false;
	
	@EventHandler
	public void onPause(PauseGameEvent event)
	{
		wasPlaying = bgm.isPlaying();
		
		bgm.pause();
	}
	
	@EventHandler
	public void onUnPause(UnPauseGameEvent event)
	{
		if(wasPlaying)
			bgm.play();
	}
	
	@Override
	public void onDelete()
	{
		bgm.stop();
	}

	@Override
	public abstract boolean isPersistant();

	@Override
	public float getWidth()
	{
		return 0;
	}

	@Override
	public float getHeight()
	{
		return 0;
	}

	@Override
	public void onDraw()
	{
		
	}

	@Override
	public void onUpdate(long tick)
	{
		
	}
	
	public Music getBgm()
	{
		return bgm;
	}
	
	public void setBgm(Music bgm)
	{
		this.bgm = bgm;
	}
	
	/**
	 * Standard fadeout over 60 seconds.
	 */
	public void fadeOut()
	{
		float start = bgm.getVolume();
		float end = 0f;
		int ticks = 60;
		
		fade(start, end, ticks, true);
	}
	
	/**
	 * Standard fadein over 60 seconds.
	 */
	public void fadeIn()
	{
		fade(0f, 1f, 60, false);
	}
	
	/**
	 * Standard fadein over 60 seconds.
	 */
	public void fadeIn(float targetVolume)
	{
		float start = 0f;
		int ticks = 60;
		
		fade(start, targetVolume, ticks, false);
	}
	
	public void fade(final float start, final float end, int overTicks, final boolean disposeOnFinish)
	{
		final float increase = (end - start)/overTicks;
		
		for(float i = 0; i < overTicks; i++)
		{
			final int finalI = (int) i;
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					if(bgm.isPlaying())
						bgm.setVolume(start + finalI * increase);
					
					if(disposeOnFinish && bgm.getVolume() < 0.01f)
						bgm.dispose();
				}
			}, (int) i);
		}
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				if(bgm.isPlaying())
					bgm.setVolume(end);
				
				if(disposeOnFinish)
					bgm.dispose();
			}
		}, overTicks);
	}
}
