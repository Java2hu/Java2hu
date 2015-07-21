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
public class BGMPlayer extends StageObject implements EventListener
{
	private Music bgm;
	private boolean pauseOnPause;
	
	public BGMPlayer(final Music mediaPlayer)
	{
		this(mediaPlayer, true);
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
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		game.registerEvents(this);
	}
	
	boolean wasPlaying = false;
	
	@EventHandler
	public void onPause(PauseGameEvent event)
	{
		if(!pauseOnPause)
			return;
		
		wasPlaying = bgm.isPlaying();
		
		if(wasPlaying)
			bgm.pause();
	}
	
	@EventHandler
	public void onUnPause(UnPauseGameEvent event)
	{
		if(!pauseOnPause)
			return;
		
		if(wasPlaying)
			bgm.play();
		
		wasPlaying = false;
	}
	
	@Override
	public void onDelete()
	{
		bgm.stop();
		
		game.unregisterEvents(this);
	}

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
	 * Standard fadeout over 60 ticks.
	 */
	public void fadeOut()
	{
		float start = bgm.getVolume();
		float end = 0f;
		int ticks = 60;
		
		fade(start, end, ticks, true);
	}
	
	/**
	 * Standard fadein over 60 ticks.
	 */
	public void fadeIn()
	{
		fade(0f, 1f, 60, false);
	}
	
	private int fadeDelay = 0;
	
	/**
	 * The delay before a fade starts, default: 0 ticks
	 * @return
	 */
	public int getFadeDelay()
	{
		return fadeDelay;
	}
	
	public void setFadeDelay(int fadeDelay)
	{
		this.fadeDelay = fadeDelay;
	}
	
	public void play()
	{
		getBgm().play();
	}
	
	/**
	 * Standard fadein over 60 ticks.
	 */
	public void fadeIn(float targetVolume)
	{
		float start = 0f;
		int ticks = 60;
		
		fade(start, targetVolume, ticks, false);
	}
	
	public void fade(final float start, final float end, int overTicks, final boolean disposeOnFinish)
	{
		if(bgm.isPlaying())
			bgm.setVolume(start);
		
		final float increase = (end - start)/overTicks;
		
		for(float i = 0; i < overTicks; i++)
		{
			final int finalI = (int) i;
			
			Game.getGame().addTask(new Runnable()
			{
				@Override
				public void run()
				{
					if(bgm.isPlaying())
						bgm.setVolume(start + finalI * increase);
				}
			}, (int) i + getFadeDelay());
		}
		
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				if(bgm.isPlaying())
					bgm.setVolume(end);
				
				if(disposeOnFinish)
					bgm.dispose();
			}
		}, overTicks + getFadeDelay());
	}
}
