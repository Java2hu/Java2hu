package java2hu.object.player;

import java.util.HashSet;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.object.FreeStageObject;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.IBulletType;
import java2hu.util.AnimationUtil;
import java2hu.util.ImageSplitter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;

public class PlayerBullet extends Bullet
{
	public PlayerBullet(Animation animation, Color color, float x, float y)
	{
		super(animation, color, x, y);
		init();
	}

	public PlayerBullet(Animation animation, float x, float y)
	{
		super(animation, x, y);
		init();
	}

	public PlayerBullet(IBulletType type, float x, float y)
	{
		super(type, x, y);
		init();
	}
	
	private void init()
	{
		useDeathAnimation(false);
	}
	
	private static Texture TEXT_BREAK;
	private static Animation BREAK_ANI;
	
	@Override
	public void deleteAnimation()
	{
		if(TEXT_BREAK == null)
		{
			TEXT_BREAK = Loader.texture(Gdx.files.internal("sprites/eff_splash.png"));
			BREAK_ANI = ImageSplitter.getAnimationFromSprite(TEXT_BREAK, 16, 48, 2f, 1,2,3,4,5,6,7,8,9);
		}
		
		final Bullet bullet = this;
		
		final Animation ani = AnimationUtil.copyAnimation(BREAK_ANI);
		
		Color effect = getType().getEffectColor();
		
		if(effect == null)
			effect = getDeletionColor();
		
		if(effect == null)
			effect = Color.WHITE;
		
		effect = effect.cpy();
		
		for(TextureRegion r : ani.getKeyFrames())
		{
			HitboxSprite sprite = (HitboxSprite)r;
			
			sprite.setOriginCenter();
			
			sprite.setPosition(bullet.getX() - (sprite.getWidth() / 2f), bullet.getY() - sprite.getHeight());
			sprite.rotate(-bullet.getRotationDeg());
			sprite.setScale(2f);
			
			sprite.setColor(effect);
		}
		
		StageObject obj = new FreeStageObject(bullet.getX(), bullet.getY())
		{
			int ticks = 0;

			{
				setName("Death animation " + bullet.getName());
				setPosition(bullet);
				setGlowing();
				
				bullet.setOwnedBy(this);
			}
			
			@Override
			public void draw()
			{
				onDraw();
			}
			
			@Override
			public void onDraw()
			{
				J2hGame g = Game.getGame();
				
				HitboxSprite current = (HitboxSprite) ani.getKeyFrame(ticks);

				current.setPosition(getX() - (current.getWidth() / 2f), getY() - current.getHeight());
				current.draw(g.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				ticks++;
				
				if(ani.isAnimationFinished(ticks) || !isOnStageRaw())
				{
					Game.getGame().delete(this);
				}
			}
			
			@Override
			public void onUpdateDelta(float delta)
			{
				super.onUpdateDelta(delta);
				
				setY(getY() - (-300f * delta));
			}
			
			@Override
			public void onDelete()
			{
				bullet.disposeAll();
			}
			
			@Override
			public boolean isPersistant()
			{
				return true; // Deletes itself, no need to get it removed by anything else.
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
		};
		
		obj.setGlowing();
		obj.setZIndex(getZIndex());
		
		Game.getGame().spawn(obj);
	}
	
	private float damage;
	
	/**
	 * Get the damage dealt to the hit LivingObject.
	 */
	public float getDamage()
	{
		return damage;
	}
	
	/**
	 * Sets the damage dealt to the hit LivingObject.
	 */
	public void setDamage(float damage)
	{
		this.damage = damage;
	}
	
	@Override
	public void checkCollision()
	{
		HashSet<StageObject> stageObjects = new HashSet<StageObject>(Game.getGame().getStageObjects());

		for(StageObject obj : stageObjects)
		{
			if(obj instanceof LivingObject)
			{
				LivingObject lo = (LivingObject) obj;

				if(Intersector.overlapConvexPolygons(lo.getHitbox(), getCurrentSprite().getHitbox()))
				{
					lo.decreaseHealth(getDamage());
					lo.onHit();
					Game.getGame().delete(this);
					deleteAnimation();
				}
			}
		}
	}
}
