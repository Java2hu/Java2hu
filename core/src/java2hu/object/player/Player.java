package java2hu.object.player;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.object.StageObject;
import java2hu.util.HitboxUtil;
import java2hu.util.InputUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class Player extends StageObject
{
	private Polygon hitbox;
	public Animation idle;
	public Animation left;
	public Animation right;
	public Animation hitboxAnimation;
	private boolean showHitbox = false;
	private boolean isFocused = false;
	private int hitboxTimer = 0;
	
	public Player(Animation idle, Animation left, Animation right, float hitboxSize, float x, float y)
	{
		super(x, y);
		
		setZIndex(100);
		
		this.idle = idle;
		this.left = left;
		this.right = right;
		
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		
		Texture hitboxTexture = Loader.texture(Gdx.files.internal("sprites/hitbox.png"));
		
		Array<Sprite> rotate = new Array<Sprite>();
		
		for(int i = 0; i < 360; i++)
		{
			Sprite rotatedSprite = new Sprite(hitboxTexture);
			
			rotatedSprite.rotate(i);
			
			float scale = Math.max(hitboxSize / 3/1.5F, 0.8F);
			rotatedSprite.setScale(scale); // Scale the hitbox animation according to the hitbox size.
			
			rotate.add(rotatedSprite);
		}
		
		hitbox = HitboxUtil.rectangleHitbox(hitboxSize);
		hitboxAnimation = new Animation(0.03F, rotate, PlayMode.NORMAL);
		addDisposable(hitboxAnimation);
		currentTexture = (HitboxSprite) idle.getKeyFrame(0);
	}

	@Override
	public float getWidth()
	{
		return currentTexture.getRegionWidth();
	}

	@Override
	public float getHeight()
	{
		return currentTexture.getRegionHeight();
	}
	
	public Polygon getHitbox()
	{
		return hitbox;
	}
	
	public Animation getHitboxAnimation()
	{
		return hitboxAnimation;
	}
	
	public void showHitbox(boolean bool)
	{
		showHitbox = bool;
	}
	
	public boolean isFocused()
	{
		return isFocused;
	}
	
	public void setFocused(boolean isFocused)
	{
		this.isFocused = isFocused;
	}
	
	public abstract void shoot();
	
	private HitboxSprite currentTexture = null;
	private Animation lastAnimation = null;
	private long timerOffset = 0;
	
	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		if(getLastX() - getX() > 0)
		{
			if(lastAnimation != left)
			{
				lastAnimation = left;
				timerOffset = g.getTick();
			}
			
			currentTexture = (HitboxSprite) left.getKeyFrame(g.getTick() - timerOffset);
		}
		else if(getLastX() - getX() < 0)
		{
			if(lastAnimation != right)
			{
				lastAnimation = right;
				timerOffset = g.getTick();
			}
			
			currentTexture = (HitboxSprite) right.getKeyFrame(g.getTick() - timerOffset);
		}
		else
		{
			if(lastAnimation != idle)
			{
				lastAnimation = idle;
				timerOffset = g.getTick();
			}
			
			currentTexture = (HitboxSprite) idle.getKeyFrame(g.getTick() - timerOffset);
		}
		
		currentTexture.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
		currentTexture.draw(g.batch);
		if(showHitbox)
		{
			Sprite frame = (Sprite) hitboxAnimation.getKeyFrame(hitboxTimer);
			
			if(!hitboxAnimation.isAnimationFinished(hitboxTimer))
				hitboxTimer++;
			
			frame.setPosition(getX() - frame.getWidth() / 2, getY() - frame.getHeight() / 2);
			frame.draw(g.batch);
		}
		else
		{
			hitboxAnimation.setPlayMode(PlayMode.NORMAL);
			hitboxTimer = 0;
		}
	}

	@Override
	public void onUpdate(long tick)
	{
		Rectangle bound = hitbox.getBoundingRectangle();
		
		hitbox.setPosition(getX() - bound.width / 2, getY() - bound.height / 2);
		
		InputUtil.handleMovementArrowKeys(this, 9F, 4.0F);
		
		if(Gdx.input.isKeyPressed(Input.Keys.Z))
		{
			shoot();
		}
	}
	
	public void onHit(StageObject hit)
	{
		if(Scheduler.isTracked("playerDeath", this))
			return;
		
		onDeath(hit);
		
		Scheduler.trackMillis("playerDeath", this, (long) (2 * 1000)); // Death invincibility.
	}
	
	public void onDeath(StageObject killer)
	{
		
	}
	
	@Override
	public boolean isPersistant()
	{
		return true;
	}
}
