package java2hu.spellcard;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.Duration;

public abstract class Spellcard extends J2hObject
{
	private StageObject owner;
	private Duration timeLeft = null;
	private int tick = 0;
	
	public Spellcard(StageObject owner)
	{
		this.owner = owner;
	}
	
	@Override
	public J2hGame getGame()
	{
		return Game.getGame();
	}
	
	public StageObject getOwner()
	{
		return owner;
	}
	
	public void run()
	{
		final Duration timeLeft = getTimeLeft();
		
		if(timeLeft != null)
			setTimeLeft(timeLeft.subtract(Duration.ticks(1)));

		tick(tick++);
	}
	
	public int getSpellcardTick()
	{
		return tick;
	}
	
	public void setSpellcardTick(int tick)
	{
		this.tick = tick;
	}
	
	public static final Duration BLINK = Duration.seconds(10);
	public static final Duration FAST_BLINK = Duration.seconds(5);
	
	private Duration time = null;
	
	/**
	 * Sets the time this spellcard will take, and starts the timer.
	 * Use null to disable a time for this card (default)
	 * Once the timer runs out, {@link #onTimeOut()} will be called, which by default removes the spellcard and sets the boss's hp to zero.
	 */
	public void setSpellcardTime(Duration time)
	{
		this.time = time;
		setTimeLeft(time);
	}
	
	public Duration getSpellcardTime()
	{
		return time;
	}
	
	/**
	 * Sets the time left for this spellcard, or null to disable the timer (default).
	 * Once the timer runs out, {@link #onTimeOut()} will be called, which by default removes the spellcard and sets the boss's hp to zero.
	 */
	public void setTimeLeft(Duration timeLeft)
	{
		this.timeLeft = timeLeft;
		
		if(timeLeft == null)
			return;
		
		double ticks = timeLeft.toTicks();
		
		if(ticks < BLINK.toTicks() && timeLeft.toMilliseconds() > 500) // Don't play the count sound at 0.00
		{
			if(ticks % Duration.seconds(1).toTicks() == 0)
			{
				boolean fast = ticks <= FAST_BLINK.toTicks();
				
				if(fast)
					TouhouSounds.Stage.TIMING_OUT_2.play();
				else
					TouhouSounds.Stage.TIMING_OUT_1.play();
			}
		}
		
		Game.getGame().setTimer(timeLeft);
		
		if(!timedOut && timeLeft.toMilliseconds() < 0)
		{
			timedOut = true;
			onTimeOut();
		}
	}
	
	/**
	 * May be null if timer disabled.
	 * @return
	 */
	public Duration getTimeLeft()
	{
		return timeLeft;
	}
	
	private boolean timedOut = false;
	
	public boolean isTimedOut()
	{
		return timedOut;
	}
	
	public void onTimeOut()
	{
		TouhouSounds.Stage.TIMEOUT.play();
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				game.setTimer(Duration.ZERO);
			}
		}, 20);
		
		game.getSpellcards().remove(this);
		onRemove();
		
		if(owner instanceof LivingObject)
		{
			((LivingObject) owner).setHealth(0);
		}
	}
	
	public void onCapture()
	{
		TouhouSounds.Stage.CARD_GET.play();
	}
	
	public void onRemove()
	{
		if(!timedOut)
		{
			onCapture();
		}
		
		Game.getGame().clear(ClearType.PLUGINS);
	}
	
	public abstract void tick(int tick);
}
