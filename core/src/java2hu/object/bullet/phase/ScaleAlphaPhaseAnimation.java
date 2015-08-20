package java2hu.object.bullet.phase;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.util.Duration;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A phase animation that scales up or down and turns alpha over a specific time
 */
public class ScaleAlphaPhaseAnimation extends StageObject implements PhaseAnimation
{
	public ScaleAlphaPhaseAnimation(final Bullet bullet)
	{
		this(new Getter<Sprite>()
		{
			@Override
			public Sprite get()
			{
				return bullet.getCurrentSprite();
			}
		}, bullet);
	}
	
	public ScaleAlphaPhaseAnimation(Getter<Sprite> getter, StageObject owner)
	{
		super(owner.getX(), owner.getY());
		this.getter = getter;
		this.owner = owner;
		
		setBlendFunc(owner.getBlendFuncSrc(), owner.getBlendFuncDst());
	}
	
	public boolean animationPlaying = true;
	
	private Getter<Sprite> getter;
	private StageObject owner;
	
	{
		setName("ScaleAlphaPhaseAnimation");
	}
	
	@Override
	public void start()
	{
		animationPlaying = true;
		
		Sprite s = getter.get();
		
		targetScaleX = s.getScaleX();
		targetScaleY = s.getScaleY();
		targetAlpha = s.getColor().a;
		
		scaleX = addedScaleX;
		scaleY = addedScaleY;
		
		scaleDecreaseX = (targetScaleX - scaleX) / time;
		scaleDecreaseY = (targetScaleY - scaleY) / time;
		alphaIncrease = (targetAlpha - alpha) / time;
		
		createTick = game.getTick();
		
		owner.addOwnedObject(this);
	}
	
	private float scaleX = 0;
	private float scaleY = 0;

	private float scaleDecreaseX = 0;
	private float scaleDecreaseY = 0;
	private float alphaIncrease = 0;

	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		Sprite s = getter.get();

		Sprite current = s;

		current.setPosition(owner.getX() - current.getWidth() / 2, owner.getY() - current.getHeight() / 2);
		current.setScale(scaleX, scaleY);
		current.setAlpha(Math.min(Math.max(alpha, 0), 1));

		current.draw(g.batch);
	}

	@Override
	public void onUpdate(long tick)
	{
		scaleX += scaleDecreaseX;
		scaleY += scaleDecreaseY;

		alpha += alphaIncrease;

		if(getTicksAlive() > time || !owner.isOnStageRaw())
		{
			owner.removeOwnedObject(this);
		
			animationPlaying = false;
		}
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
	
	private int time = 10;
	private float addedScaleX = 2f;
	private float addedScaleY = 2f;
	private float targetScaleX = 1f;
	private float targetScaleY = 1f;
	private float alpha = -0.5f;
	private float targetAlpha = 1f;
	
	public boolean scaleDownX()
	{
		return targetScaleX < addedScaleX;
	}
	
	public boolean scaleDownY()
	{
		return targetScaleY < addedScaleY;
	}
	
	public boolean decreasingOpacity()
	{
		return targetAlpha < alpha;
	}
	
	/**
	 * Time the spawn animation takes, default: 10 ticks
	 * @param time
	 */
	public void setTime(int time)
	{
		this.time = time;
	}
	
	/**
	 * Time the spawn animation takes, default: 10 ticks
	 * @param time
	 */
	public void setTime(Duration dur)
	{
		this.time = (int) dur.toTicks();
	}
	
	public int getTime()
	{
		return time;
	}
	
	/**
	 * Starting scale of the spawn animation in x, goes down to the original scale, default: 2x
	 * @param addedScaleX
	 */
	public void setAddedScaleX(float addedScaleX)
	{
		this.addedScaleX = addedScaleX;
	}

	public float getAddedScaleX()
	{
		return addedScaleX;
	}
	
	/**
	 * Starting scale of the spawn animation in y, goes down to the original scale, default: 2x
	 * @param addedScaleY
	 */
	public void setAddedScaleY(float addedScaleY)
	{
		this.addedScaleY = addedScaleY;
	}
	
	public float getAddedScaleY()
	{
		return addedScaleY;
	}
	
	/**
	 * Starting scale of the spawn animation, goes down to the original scale, default: 2x
	 * @param addedScale
	 */
	public void setAddedScale(float addedScale)
	{
		setAddedScaleX(addedScale);
		setAddedScaleY(addedScale);
	}
	
	/**
	 * Starting scale of the spawn animation, goes down to the original scale, default: 2x
	 * @param addedScale
	 */
	public void setAddedScale(float addedScaleX, float addedScaleY)
	{
		setAddedScaleX(addedScaleX);
		setAddedScaleY(addedScaleY);
	}
	
	/**
	 * Starting alpha of the spawn animation, the lower the alpha below 0, the longer it will be invisible. default: -0.5f
	 * @param alpha
	 */
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
	
	public float getAlpha()
	{
		return alpha;
	}

	@Override
	public boolean isPlaying()
	{
		return animationPlaying;
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
	public float getWidth()
	{
		return getter.get().getWidth();
	}

	@Override
	public float getHeight()
	{
		return getter.get().getHeight();
	}
}
