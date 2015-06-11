package java2hu.object.enemy.greater;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.MovementAnimation;
import java2hu.SmartTimer;
import java2hu.StartupLoopAnimation;
import java2hu.object.LivingObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.HitboxUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public abstract class Boss extends LivingObject
{
	public Sprite fullBodySprite = null;
	
	// Support for: Animation, MovementAnimation
	public Animation idle = null;

	public Animation left = null;

	public Animation right = null;
	
	public Animation special = null;
	public boolean playSpecial = false;
	public int specialTimer = 0;
	
	public Polygon playerHitHitbox;
	
	/**
	 * Assumes all animations consist of HitboxSprite frames. (Don't need to contain hitboxes though)
	 * @param maxHealth
	 * @param fullBodySprite
	 * @param idle - Support for: Animation, StartupLoopAnimation, MovementAnimation
	 * @param left - Support for: Animation, StartupLoopAnimation, MovementAnimation
	 * @param right - Support for: Animation, StartupLoopAnimation, MovementAnimation
	 * @param special
	 * @param x
	 * @param y
	 */
	public Boss(Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, float maxHealth, float x, float y)
	{
		super(HitboxUtil.textureRegionPolygon(idle.getKeyFrames()[0]), maxHealth, x, y);
		
		playerHitHitbox = HitboxUtil.rectangleHitbox(30);
		
		this.fullBodySprite = fullBodySprite;
		this.idle = idle;
		this.left = left;
		this.right = right;
		this.special = special;
		this.setZIndex(100);
	}
	
	/**
	 * Barebone constructor, to load assets in constructor.
	 * Be sure to call the set() method after constructing with the correct assets.
	 * @param maxHealth
	 * @param x
	 * @param y
	 */
	public Boss(float maxHealth, float x, float y)
	{
		super(null, maxHealth, x, y);
		
		playerHitHitbox = HitboxUtil.rectangleHitbox(30);
		
		this.setZIndex(100);
	}
	
	protected void set(Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special)
	{
		this.setHitbox(HitboxUtil.textureRegionPolygon(idle.getKeyFrames()[0]));
		this.fullBodySprite = fullBodySprite;
		this.idle = idle;
		this.left = left;
		this.right = right;
		this.special = special;
	}
	
	public void playSpecial(boolean bool)
	{
		if(special == null)
			return;
		
		playSpecial = bool;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		J2hGame g = Game.getGame();
		
		hoverTime.tick();
		
		if(System.currentTimeMillis() - getLastMoveTime() > 100)
		{
			lastX = getX();
			lastY = getY();
		}
		
		Rectangle b = getPlayerHitHitbox().getBoundingRectangle();
		getPlayerHitHitbox().setPosition(getX() - b.width / 2, getY() - b.height / 2);
		
		if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getPlayerHitHitbox()))
			g.getPlayer().onHit(this);
		
		if(lastAnimation instanceof StartupLoopAnimation)
			((StartupLoopAnimation)lastAnimation).increase(1);
		
		if(lastAnimation instanceof MovementAnimation)
		{
			if(getPathing().getCurrentPath() != null)
				((MovementAnimation)lastAnimation).checkEnd(getPathing().getCurrentPath().getTimeLeft());
		}
		
		if(special != null && playSpecial)
		{
			if(!Game.getGame().isPaused())
				specialTimer++;
			
			if(special.getPlayMode() == PlayMode.NORMAL && special.isAnimationFinished(getSpecialAnimationTime()))
				playSpecial = false;
		}
	}
	
	protected SmartTimer hoverTime = new SmartTimer(0.1f, -1f, -0.5f, 1f, 0.5f, 0.01f);
	
	private Animation lastAnimation = null;
	private float timeOffset = 0;
	
	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		float drawX = getDrawX();
		float drawY = getDrawY();
		
		if(special != null && playSpecial)
		{
			HitboxSprite current = (HitboxSprite) special.getKeyFrame(getSpecialAnimationTime());
			current.setPosition(drawX - current.getWidth() / 2, drawY - current.getHeight() / 2);
			current.draw(g.batch);
			
			lastAnimation = special;
		}
		else
		{
			boolean left = getLastX() - getX() > 0;
			boolean right = getLastX() - getX() < 0;
			
			Animation selected = left ? this.left : right ? this.right : this.idle;
			
			if(selected == null)
				selected = this.idle;
			
			if(selected instanceof StartupLoopAnimation)
			{
				StartupLoopAnimation ani = (StartupLoopAnimation) selected;
				
				if(lastAnimation != selected)
				{
					ani.reset();
					lastAnimation = selected;
				}
				
				HitboxSprite sprite = (HitboxSprite) ani.getCurrentFrame();
				
				ani.draw(g.batch, drawX - sprite.getWidth() / 2f, drawY - sprite.getHeight() / 2f);
			}
			else
			{
				if(lastAnimation != selected)
				{
					timeOffset = getAnimationTime();
					lastAnimation = selected;
				}
				
				HitboxSprite current = AnimationUtil.getCurrentSprite(selected, getAnimationTime() - timeOffset);
				current.setPosition(drawX - current.getWidth() / 2f, drawY - current.getHeight() / 2f);
				current.draw(g.batch);
			}
		}
		
		if(!playSpecial)
			specialTimer = 0;
	}
	
	public float getDrawY()
	{
		return getY() + hoverTime.getTimer() * 5f;
	}
	
	public float getDrawX()
	{
		return getX();
	}
	
	public Sprite getFullBodySprite()
	{
		return fullBodySprite;
	}
	
	public HitboxSprite getCurrentSprite()
	{
		return (HitboxSprite) idle.getKeyFrame(getAnimationTime());
	}
	
	public float getAnimationTime()
	{
		return Game.getGame().getTick();
	}
	
	public float getSpecialAnimationTime()
	{
		return specialTimer;
	}
	
	public Polygon getPlayerHitHitbox()
	{
		return playerHitHitbox;
	}
	
	@Override
	public float getWidth()
	{
		return getCurrentSprite().getWidth();
	}

	@Override
	public float getHeight()
	{
		return getCurrentSprite().getHeight();
	}
	
	@Override
	public boolean isPersistant()
	{
		return true;
	}
	
	@Override
	public void onHit()
	{
		super.onHit();
		
		if(Scheduler.isTracked(this, "damageSound"))
			return;
		
		if(getHealth()/getMaxHealth() < 0.2)
		{
			TouhouSounds.Player.DAMAGE_2.play(0.3F);
		}
		else
		{
			TouhouSounds.Player.DAMAGE_1.play(0.3F);
		}
		
		Scheduler.trackMillis(this, "damageSound", (long) 100);
	}
}
