package java2hu.object.bullet;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.RNG;
import java2hu.ZIndex;
import java2hu.object.DrawObject;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.phase.PhaseAnimation;
import java2hu.object.bullet.phase.TouhouSpawnAnimation;
import java2hu.util.AnimationUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * A simple bullet
 */
public class Bullet extends StageObject
{
	protected IBulletType type;
	protected Animation animation;
	
	protected float velocityX;
	protected float velocityY;
	
	public Bullet(IBulletType type, float x, float y)
	{
		this(type.getAnimation(), x, y);
		this.type = type;
	}
	
	/**
	 * The animation should contain HitboxSprites as frames, else you will need to make a seperate class, because everything assumes so.
	 * @param animation
	 * @param x
	 * @param y
	 */
	public Bullet(final Animation animation, float x, float y)
	{
		this(animation, null, x, y);
	}
	
	public Bullet(final Animation animation, final Color color, float x, float y)
	{
		super(x, y);
		
		this.animation = animation;
		
		this.type = new IBulletType()
		{
			@Override
			public Color getEffectColor()
			{
				return color;
			}
			
			@Override
			public Animation getAnimation()
			{
				return animation;
			}
		};
		
		this.setZIndex(ZIndex.BULLETS);
	}
	
	@Override
	public void onDraw()
	{
		if(currentAnimation != null && currentAnimation.isPlaying())
			return;
		
		J2hGame g = Game.getGame();
		
		HitboxSprite current = getCurrentSprite();
		
		if(current != null)
		{
			current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
			
			current.draw(g.batch);
		}
	}

	@Override
	public void onUpdate(long tick)
	{
		J2hGame g = Game.getGame();
		
		HitboxSprite current = getCurrentSprite();
		
		if(current != null)
		{
			current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
		}
		
		if(doDelete())
			g.delete(this);
		
		if(currentAnimation == null || !currentAnimation.isPlaying() || currentAnimation.hasHitbox())
			checkCollision();
	}
	
	@Override
	public void onUpdateDelta(float delta)
	{
		super.onUpdateDelta(delta);
		
		this.setX(getX() - velocityX * delta);
		this.setY(getY() - velocityY * delta);
	}
	
	public boolean doDelete()
	{
		J2hGame g = Game.getGame();
		
		int bufferRate = getDeleteDistance();
		
		boolean delete = g.getMinX() + getX() < g.getMinX() - bufferRate || g.getMinY() + getY() < g.getMinY() - bufferRate || getX() > g.getMaxX() + bufferRate || getY() > g.getMaxY() + bufferRate;
		
		return delete;
	}
	
	public int getDeleteDistance()
	{
		return 200;
	}
	
	/**
	 * Ran by the update method, used to check collisions with the player.
	 * if it shouldn't collide, or collide with something else, replace this when extending!
	 */
	public void checkCollision()
	{
		J2hGame g = Game.getGame();
		
		if(getHitbox() == null || g.getPlayer() == null)
			return;
		
		if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getHitbox()))
		{
			g.getPlayer().onHit(this);
			onHit();
			g.delete(this);
		}
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityX
	 */
	@Deprecated
	public void setVelocityXTick(float velocityX)
	{
		float perSecond = velocityX * game.currentTPS;
		
		this.velocityX = perSecond;
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityY
	 */
	@Deprecated
	public void setVelocityYTick(float velocityY)
	{
		float perSecond = velocityY * game.currentTPS;
		
		this.velocityY = perSecond;
	}
	
	/**
	 * Legacy method to return the velocity x from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityXTick()
	{
		return velocityX / game.currentTPS;
	}
	
	/**
	 * Legacy method to return the velocity y from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityYTick()
	{
		return velocityY / game.currentTPS;
	}
	
	public void setVelocityX(float velocityX)
	{
		this.velocityX = velocityX;
	}
	
	public void setVelocityY(float velocityY)
	{
		this.velocityY = velocityY;
	}
	
	public float getVelocityX()
	{
		return velocityX;
	}
	
	public float getVelocityY()
	{
		return velocityY;
	}
	
	/**
	 * Will set the rotation based on the current velocity, with an offset of 0.
	 * If your bullet comes off weird, like 90 degree away from what it should be, use that offset as the first argument to fix it.
	 * This is used to easily rotate bullets to an intuitive direction
	 */
	public void setRotationFromVelocity()
	{
		setRotationFromVelocity(90f); // Standard offset is 90f.
	}
	
	/**
	 * Sets rotation based on the current velocity + offset
	 * This is used to easily rotate bullets to an intuitive direction
	 * @param offsetDegree
	 */
	public void setRotationFromVelocity(float offsetDegree)
	{
		setRotationDeg((float) (Math.atan2(velocityY, velocityX) * (180 / Math.PI) - offsetDegree));
	}
	
	/**
	 * Send a bullet in the direction derived from the degree supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionDegTick(float degree, float speed)
	{
		setDirectionRadsTick((float) Math.toRadians(degree), speed);
	}
	
	/**
	 * Send a bullet in the direction derived from the radians supplied with the set speed (ticks).
	 * @param radians
	 * @param speed
	 */
	@Deprecated
	public void setDirectionRadsTick(float radians, float speed)
	{
		setVelocityXTick((float) (Math.cos(radians) * speed));
		setVelocityYTick((float) (Math.sin(radians) * speed));
	}
	
	/**
	 * Send a bullet in the direction derived from the degree supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionDeg(float degree, float speed)
	{
		setDirectionRads((float) Math.toRadians(degree), speed);
	}
	
	/**
	 * Send a bullet in the direction derived from the radians supplied with the set speed.
	 * @param radians
	 * @param speed
	 */
	public void setDirectionRads(float radians, float speed)
	{
		setVelocityX((float) (Math.cos(radians) * speed));
		setVelocityY((float) (Math.sin(radians) * speed));
	}
	
	/**
	 * Halts the movement of this bullet, making it stand still.
	 */
	public void haltMovement()
	{
		setVelocityX(0);
		setVelocityY(0);
	}
	
	public void setRotationDeg(final float rotation)
	{
		applyAnimationChange(new Setter<HitboxSprite>()
		{
			@Override
			public void set(HitboxSprite t)
			{
				t.setRotation(rotation);
			};
		});
	}
	
	public void setRotationRads(float rotation)
	{
		setRotationDeg((float) Math.toDegrees(rotation));
	}
	
	public void setScale(float scale)
	{
		setScale(scale, scale);
	}
	
	public void setScale(final float scaleX, final float scaleY)
	{
		applyAnimationChange(new Setter<HitboxSprite>()
		{
			@Override
			public void set(HitboxSprite t)
			{
				t.setScale(scaleX, scaleY);
			};
		});
	}
	
	public void setAlpha(final float alpha)
	{
		applyAnimationChange(new Setter<HitboxSprite>()
		{
			@Override
			public void set(HitboxSprite t)
			{
				t.setAlpha(alpha);
			};
		});
	}
	
	/**
	 * Apply a setter to all frames of the bullet's animation, usefull if you need to alter a specific property of all the frames.
	 */
	public void applyAnimationChange(Setter<HitboxSprite> change)
	{
		for(TextureRegion t : animation.getKeyFrames())
		{
			HitboxSprite s = (HitboxSprite) t;
			change.set(s);
		}
	}
	
	@Override
	public void setX(float x)
	{
		this.lastX = this.x;
		this.x = x;
	}
	
	@Override
	public void setY(float y)
	{
		this.lastY = this.y;
		this.y = y;
	}
	
	public void setBullet(IBulletType type)
	{
		setBullet(type.getAnimation());
	}
	
	public void setBullet(Animation animation)
	{
		if(animation == null)
			return;

		this.animation = animation;
	}
	
	/**
	 * Returns the sprite of the bullet right now, assumes the Animation consists of HitboxSprites
	 * @return
	 */
	public HitboxSprite getCurrentSprite()
	{
		J2hGame g = Game.getGame();
		
		return (HitboxSprite) animation.getKeyFrame(g.getTick(), true);
	}
	
	public Polygon getHitbox()
	{
		return getCurrentSprite().getHitbox();
	}
	
	public IBulletType getType()
	{
		return type;
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
	
	public float getScaledWidth()
	{
		return getWidth() * getCurrentSprite().getScaleX();
	}
	
	public float getScaledHeight()
	{
		return getHeight() * getCurrentSprite().getScaleY();
	}
	
	public Animation getAnimation()
	{
		return animation;
	}
	
	public float getRotationDeg()
	{
		return getCurrentSprite().getRotation();
	}
	
	public float getRotationRads()
	{
		return (float) Math.toRadians(getRotationDeg());
	}
	
	public float getVelocityRotationDeg()
	{
		return (float) Math.toDegrees(getVelocityRotationRads());
	}
	
	public float getVelocityRotationRads()
	{
		return (float) Math.atan2(getVelocityYTick(), getVelocityXTick());
	}
	
	/**
	 * Returns a box surrounding the bullet, based on the getX(), getY(), getWidth() and getHeight() methods.
	 * @return
	 */
	public Rectangle getTextureBox()
	{
		Rectangle rect = new Rectangle(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
		
		return rect;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		if(!useSpawnAnimation)
			return;
		
		spawnAnimation();
	}
	
	protected PhaseAnimation currentAnimation = null;
	
	protected boolean useSpawnAnimation = true;
	protected boolean useDeleteAnimation = true;
	
	public void useSpawnAnimation(boolean useSpawnAnimation)
	{
		this.useSpawnAnimation = useSpawnAnimation;
	}
	
	public void useDeathAnimation(boolean bool)
	{
		this.useDeleteAnimation = bool;
	}
	
	public PhaseAnimation getSpawnAnimation()
	{
		return spawn;
	}
	
	public void setSpawnAnimation(PhaseAnimation spawn)
	{
		this.spawn = spawn;
	}
	
	public PhaseAnimation spawn = new TouhouSpawnAnimation(this);
	
	public void spawnAnimation()
	{	
		spawn.start();
		currentAnimation = spawn;
	}
	
	public void onHit()
	{
		
	}
	
	/**
	 * What to do when a bullet deletes.
	 * Default: Spawn a DrawObject that scales down within a second and deletes when the scale is < 0
	 */
	@Override
	public void onDelete()
	{
		if(!useDeleteAnimation)
		{
			disposeAll();
			return;
		}
		
		deleteAnimation();
	}
	
	private static Texture BREAK = null;
	private static Animation BREAK_ANI = null;
	
	private Color deletionColor = null;
	
	/**
	 * If this bullet isn't initiated with a BulletType that contains a color for the bullet, this value is used instead.
	 * If you set this to null, white will be used.
	 */
	public void setDeletionColor(Color deletionColor)
	{
		this.deletionColor = deletionColor;
	}
	
	/**
	 * If this bullet isn't initiated with a BulletType that contains a color for the bullet, this value is used instead.
	 * Can be null, in which case white is used.
	 */
	public Color getDeletionColor()
	{
		final Color color = deletionColor != null ? deletionColor : (getType() != null ? getType().getEffectColor() : null);
		
		return color != null ? color : Color.WHITE;
	}
	
	public void deleteAnimation()
	{
		if(BREAK_ANI == null)
		{
			BREAK = new Texture(Gdx.files.internal("sprites/bullet_break.png"))
			{
				@Override
				public void dispose()
				{
					new Exception().printStackTrace();
					
					super.dispose();
				}
			};
			BREAK_ANI = ImageSplitter.getAnimationFromSprite(BREAK, 64, 64, 2f, 1,2,3,4,5,6,7,8);
		}
		
		final Bullet bullet = this;
		
		final float width = bullet.getWidth();
		final float height = bullet.getHeight();
		
		final float rotationOffset = (float) (RNG.random() * 360f);
		
		final Animation ani = AnimationUtil.copyAnimation(BREAK_ANI);
		
		Color effect = getType().getEffectColor();
		
		if(effect == null)
			effect = deletionColor;
		
		if(effect == null)
			effect = Color.WHITE;
		
		effect = effect.cpy();
		
		for(TextureRegion r : ani.getKeyFrames())
		{
			HitboxSprite sprite = (HitboxSprite)r;
			
			sprite.setOrigin(0, 0);
			
			float longest = Math.max(width, height) + 100;
			
			sprite.setSize(longest, longest);
			
			sprite.setPosition(bullet.getX() - (sprite.getWidth() / 2f), bullet.getY() - (sprite.getHeight() / 2f));
			
			sprite.setColor(effect);
			sprite.setAlpha(0.3f);
			
			sprite.setOriginCenter();
		}
		
		setGlowing();
		
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
				game.batch.setBlendFunction(getBlendFuncSrc(), getBlendFuncDst());
				
				J2hGame g = Game.getGame();
				
				HitboxSprite current = (HitboxSprite) ani.getKeyFrame(ticks/2f);

				current.setPosition(getX() - (current.getWidth() / 2f), getY() - (current.getHeight() / 2f));
				current.setRotation(rotationOffset);
				current.draw(g.batch);
				
				current.setRotation(180 + rotationOffset);
				current.draw(g.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				ticks++;
				
				HitboxSprite cur = bullet.getCurrentSprite();
				
				if(cur != null)
				{
					final float multiplier = 0.8f;
					
					cur.setScale(Math.max(0, cur.getScaleX() * multiplier), Math.max(0, cur.getScaleY() * multiplier));
				}
				
				if(ani.isAnimationFinished(ticks / 2f) || !isOnStageRaw())
				{
					Game.getGame().delete(this);
				}
			}
			
			@Override
			public void onUpdateDelta(float delta)
			{
				super.onUpdateDelta(delta);
				
				setX(getX() - (bullet.getVelocityX() * delta));
				setY(getY() - (bullet.getVelocityY() * delta));
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
	
	/**
	 * Older delete animation, scale the bullet out to nothing
	 */
	public void oldDeleteAnimation()
	{
		final Bullet bullet = this;
		
		DrawObject obj = new DrawObject()
		{
			float scaleX = getCurrentSprite().getScaleX();
			float scaleY = getCurrentSprite().getScaleY();
			float scaleDecreaseX = scaleX / 50;
			float scaleDecreaseY = scaleY / 50;
			
			@Override
			public void onDraw()
			{
				J2hGame g = Game.getGame();
				
				HitboxSprite current = getCurrentSprite();
				
				current.setPosition(bullet.getX() - current.getWidth() / 2, bullet.getY() - current.getHeight() / 2);
				current.setScale(scaleX, scaleY);
				
				current.draw(g.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				scaleX -= scaleDecreaseX;
				scaleY -= scaleDecreaseY;
				
				if(scaleX <= 0 || scaleY <= 0)
				{
					Game.getGame().delete(this);
				}
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
		};
		
		obj.setShader(getShader());
		
		Game.getGame().spawn(obj);
	}
}
