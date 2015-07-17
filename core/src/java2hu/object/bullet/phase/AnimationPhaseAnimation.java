package java2hu.object.bullet.phase;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.UpdateObject;
import java2hu.object.bullet.Bullet;
import java2hu.util.Duration;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A phase animation that plays an animation for a specific time.
 */
public class AnimationPhaseAnimation extends UpdateObject implements PhaseAnimation
{
	/**
	 * Create a phase animation of the animation with the time of the animation, in ticks, as duration.
	 * The animation must contain {@link Sprite}'s!
	 */
	public AnimationPhaseAnimation(Bullet bullet, Animation ani)
	{
		this(bullet, ani, Duration.ticks(ani.getAnimationDuration()));
		
		setBlendFunc(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
	}
	
	/**
	 * Create a phase animation of the animation with the specified time as duration.
	 * The animation must contain {@link Sprite}'s!
	 */
	public AnimationPhaseAnimation(Bullet bullet, Animation ani, Duration time)
	{
		this.bullet = bullet;
		
		if(time != null)
			this.time = time;
		
		this.ani = ani;
	}
	
	public boolean animationPlaying = true;
	
	protected Bullet bullet;
	protected Animation ani;
	protected Duration time = Duration.seconds(10);
	
	{
		setName("AnimationPhaseAnimation");
	}
	
	@Override
	public void start()
	{
		animationPlaying = true;
		
		createTick = game.getTick();
		bullet.addOwnedObject(this);
	}
	
	@Override
	public void onDraw()
	{
		if(!bullet.isOnStageRaw())
			return;
		
		game.batch.setBlendFunction(getBlendFuncSrc(), getBlendFuncDst());

		J2hGame g = Game.getGame();

		Sprite current = (Sprite) ani.getKeyFrame(getTicksAlive());

		if(scaleAnimationToBullet)
		{
			float width = bullet.getScaledWidth();
			float height = bullet.getScaledHeight();
			
			current.setSize(width, height);
			current.setOriginCenter();
		}
		
		current.setPosition(bullet.getX() - current.getWidth() / 2, bullet.getY() - current.getHeight() / 2);

		current.draw(g.batch);
		
		game.batch.setBlendFunction(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
	}
	
	@Override
	public void onUpdate(long tick)
	{
		if(getTicksAlive() > time.toTicks())
		{
			animationPlaying = false;
			bullet.removeOwnedObject(this);
		}
	}
	
	private boolean scaleAnimationToBullet = true;

	public boolean doScaleAnimationToBullet()
	{
		return scaleAnimationToBullet;
	}

	public void setScaleAnimationToBullet(boolean scaleAnimationToBullet)
	{
		this.scaleAnimationToBullet = scaleAnimationToBullet;
	}

	@Override
	public void onDelete()
	{
		// Don't destroy assets.
	}

	@Override
	public boolean isPersistant()
	{
		return true;
	}
	
	private boolean hasHitbox = false;
	
	public void hasHitbox(boolean hasHitbox)
	{
		this.hasHitbox = hasHitbox;
	}

	@Override
	public boolean hasHitbox()
	{
		return hasHitbox;
	}

	@Override
	public boolean isPlaying()
	{
		return animationPlaying;
	}
}
