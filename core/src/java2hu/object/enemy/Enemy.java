package java2hu.object.enemy;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.StartupLoopAnimation;
import java2hu.object.LivingObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.HitboxUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * A simple enemy!
 * Also the sort of base class of all stage part enemies.
 */
public class Enemy extends LivingObject
{
	// Support for: Animation, StartupLoopAnimation
	public Animation idle, left, right = null;
	public Animation special = null;
	public boolean playSpecial = false;
	public int specialTimer = 0;

	public Polygon playerHitHitbox;
	
	public Enemy(IEnemyType type, float maxHealth, float x, float y)
	{
		this(type == null ? null : type.getIdleAnimation(), type == null ? null : type.getLeftAnimation(), type == null ? null : type.getRightAnimation(), type == null ? null : type.getSpecialAnimation(), maxHealth, x, y);
	}

	/**
	 * Assumes all animations consist of HitboxSprite frames. (Don't need to contain hitboxes though)
	 * @param maxHealth
	 * @param fullBodySprite
	 * @param idle - Support for: Animation, StartupLoopAnimation
	 * @param left - Support for: Animation, StartupLoopAnimation
	 * @param right - Support for: Animation, StartupLoopAnimation
	 * @param special
	 * @param x
	 * @param y
	 */
	public Enemy(Animation idle, Animation left, Animation right, Animation special, float maxHealth, float x, float y)
	{
		super(idle == null ? null : HitboxUtil.textureRegionPolygon(idle.getKeyFrames()[0]), maxHealth, x, y);

		playerHitHitbox = HitboxUtil.rectangleHitbox(10); // SMALL! Easy player collision is infuriating to the player.

		this.idle = idle;
		this.left = left;
		this.right = right;
		this.special = special;
		this.setZIndex(100);
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

		if(getPlayerHitHitbox() != null)
		{
			Rectangle b = getPlayerHitHitbox().getBoundingRectangle();
			getPlayerHitHitbox().setPosition(getX() - b.width / 2, getY() - b.height / 2);
		}

		if(System.currentTimeMillis() - getLastMoveTime() > 100)
		{
			lastX = getX();
			lastY = getY();
		}

		if(getPlayerHitHitbox() != null)
		if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getPlayerHitHitbox()))
		{
			g.getPlayer().onHit(this);
			onHitPlayer();
		}
		
		if(doKill())
		{
			Game.getGame().delete(this);
		}

		if(lastAnimation instanceof StartupLoopAnimation)
			((StartupLoopAnimation)lastAnimation).increase(1);

		if(special != null && playSpecial)
		{
			if(!Game.getGame().isPaused())
				specialTimer++;

			if(special.getPlayMode() == PlayMode.NORMAL && special.isAnimationFinished(getSpecialAnimationTime()))
				playSpecial = false;
		}
	}
	
	public void onHitPlayer()
	{
		Game.getGame().delete(this);
	}
	
	public boolean doKill()
	{
		return getHealth() <= 0;
	}
	
	/**
	 * @return if this enemy does not take damage.
	 */
	public boolean isInvulnerable()
	{
		return false;
	}
	
	/**
	 * @return if collision should be checked on this enemy.
	 */
	public boolean doCheckCollision()
	{
		return true;
	}
	
	private boolean useDeathSound = true;
	
	public void useDeathSound(boolean useDeathSound)
	{
		this.useDeathSound = useDeathSound;
	}
	
	public boolean useDeathSound()
	{
		return useDeathSound;
	}
	
	public void deleteSilent()
	{
		useDeathSound(false);
		game.delete(this);
	}
	
	@Override
	public void onDelete()
	{
		if(useDeathSound())
		{
			if(Scheduler.isTracked("breakSound", "breakSound"))
				return;

			TouhouSounds.Enemy.BREAK_1.play(0.5F);

			Scheduler.trackMillis("breakSound", "breakSound", (long) 500);
		}
		
		super.onDelete();
	}

	private Animation lastAnimation = null;
	private float timeOffset = 0;

	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();

		if(special != null && playSpecial)
		{
			HitboxSprite current = (HitboxSprite) special.getKeyFrame(getSpecialAnimationTime());
			current.setPosition(getX() - current.getWidth() / 2, getY() - current.getHeight() / 2);
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

				ani.draw(g.batch, getX() - sprite.getWidth() / 2f, getY() - sprite.getHeight() / 2f);
			}
			else
			{
				if(lastAnimation != selected)
				{
					timeOffset = getAnimationTime();
					lastAnimation = selected;
				}

				HitboxSprite current = AnimationUtil.getCurrentSprite(selected, getAnimationTime() - timeOffset);
				current.setPosition(getX() - current.getWidth() / 2f, getY() - current.getHeight() / 2f);
				current.draw(g.batch);
			}
		}

		if(!playSpecial)
			specialTimer = 0;
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
	
	public void setPlayerHitHitbox(Polygon playerHitHitbox)
	{
		this.playerHitHitbox = playerHitHitbox;
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
		return false;
	}
	
	@Override
	public void onHit()
	{
		super.onHit();
		
		if(Scheduler.isTracked(this, "damageSound"))
			return;
		
		TouhouSounds.Player.DAMAGE_1.play(0.3F);
		
		Scheduler.trackMillis(this, "damageSound", (long) 100);
	}
}
