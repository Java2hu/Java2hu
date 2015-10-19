package java2hu.object.bullet;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.Position;
import java2hu.RNG;
import java2hu.ZIndex;
import java2hu.object.DrawObject;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.object.VelocityObject;
import java2hu.object.bullet.phase.PhaseAnimation;
import java2hu.object.bullet.phase.TouhouBreakAnimation;
import java2hu.object.bullet.phase.TouhouSpawnAnimation;
import java2hu.pathing.PathingHelper.Path;
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
public class Bullet extends VelocityObject
{
	protected IBulletType type;
	protected Animation animation;
	
	public Bullet(IBulletType type, float x, float y)
	{
		super(x, y);
		
		this.animation = type.getAnimation();
		
		this.type = type;
		
		init();
	}
	
	/**
	 * The animation should contain HitboxSprites as frames, else you will need to make a seperate class, because everything assumes so.
	 * @param animation
	 * @param x
	 * @param y
	 */
	public Bullet(final Animation animation, float x, float y)
	{
		this(animation, Color.WHITE.cpy(), x, y);
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
		
		init();
	}
	
	private void init()
	{
		this.setZIndex(ZIndex.BULLETS);
		spawn = new TouhouSpawnAnimation(this);
	}
	
	@Override
	public void onDraw()
	{
		if(currentAnimation != null && currentAnimation.isPlaying())
			return;
		
		J2hGame g = Game.getGame();
		
		HitboxSprite current = getCurrentSprite();
		
		
		int bufferRate = (int) Math.max(getScaledWidth(), getScaledHeight()) * 2;
		
		boolean hide = g.getMinX() + getX() < g.getMinX() - bufferRate || g.getMinY() + getY() < g.getMinY() - bufferRate || getX() > g.getMaxX() + bufferRate || getY() > g.getMaxY() + bufferRate;
		
		if(hide)
			return;
		
		if (setRotationFromPath)
		{
			Path path = getPathing().getCurrentPath();
			
			if (path != null)
			{
				Double angle = path.getLastAngle();
				
				if (angle != null)
				{
					current.setRotation((float) (angle + offsetDegree));
				}
			}
		}
		
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
	
	public boolean doDelete()
	{
		J2hGame g = Game.getGame();
		
		int bufferRate = getDeleteDistance();
		
		boolean delete = g.getMinX() + getX() < g.getMinX() - bufferRate || g.getMinY() + getY() < g.getMinY() - bufferRate || getX() > g.getMaxX() + bufferRate || getY() > g.getMaxY() + bufferRate;
		
		if(delete)
		{
			useDeathAnimation(false);
		}
		
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
	
	private boolean setRotationFromPath = false;
	private float offsetDegree = 0;
	
	/**
	 * Set this bullet to update from the path this bullet is bound to.
	 * @param enabled To enable or disable this feature (default: disabled)
	 * @param offsetDegree Offset for the resulting angle (in case your sprite is rotated)
	 */
	public void updateRotationFromPath(boolean enabled, float offsetDegree)
	{
		setRotationFromPath = enabled;
		this.offsetDegree = offsetDegree;
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
	
	public PhaseAnimation spawn;
	
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
			super.onDelete();
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
		final Bullet bullet = this;
		
		final TouhouBreakAnimation breakAni = new TouhouBreakAnimation(this)
		{
			@Override
			public boolean isPersistant()
			{
				return true;
			}
		};
		
		breakAni.onComplete(() -> { super.onDelete(); });
		breakAni.setPosition(bullet);
		
		game.spawn(breakAni);
		
		breakAni.setZIndex(getZIndex() - 1);
		
		breakAni.start();
		
		Position pos = new Position(bullet);
		
		spawnSwirl(pos, true);
	}
	
	public static void spawnSwirl(IPosition pos)
	{
		spawnSwirl(null, pos, false);
	}
	
	protected void spawnSwirl(IPosition pos, final boolean dispose)
	{
		spawnSwirl(this, pos, dispose);
	}

	protected static void spawnSwirl(Bullet bullet, IPosition pos, final boolean dispose)
	{
		int BREAK_SIDE = 64;
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
			BREAK_ANI = ImageSplitter.getAnimationFromSprite(BREAK, BREAK_SIDE, BREAK_SIDE, 2f, 1,2,3,4,5,6,7,8);
		}
		
		final float width = bullet != null ? bullet.getWidth() : BREAK_SIDE;
		final float height = bullet != null ? bullet.getHeight() : BREAK_SIDE;
		
		final Animation ani = AnimationUtil.copyAnimation(BREAK_ANI);
		
		Color effect = (bullet != null ? bullet.getType().getEffectColor() : null);
		
		if(effect == null && bullet != null)
			effect = bullet.deletionColor;
		
		if(effect == null)
			effect = Color.WHITE;
		
		effect = effect.cpy();
		
		for(TextureRegion r : ani.getKeyFrames())
		{
			HitboxSprite sprite = (HitboxSprite)r;
			
			sprite.setOrigin(0, 0);
			
			float longest = Math.max(width, height) + 100;
			
			sprite.setSize(longest, longest);
			
			sprite.setPosition(pos.getX() - (sprite.getWidth() / 2f), pos.getY() - (sprite.getHeight() / 2f));
			
			sprite.setColor(effect);
			sprite.setAlpha(0.3f);
			
			sprite.setOriginCenter();
		}
		
		final float rotationOffset = (float) (RNG.random() * 360f);
		
		StageObject obj = new FreeStageObject(pos.getX(), pos.getY())
		{
			int ticks = 0;

			{
				setName("Swirl" + (bullet != null ? " " + bullet.getName() : ""));
				setGlowing();
				
				if (bullet != null)
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
				
				HitboxSprite cur = bullet != null ? bullet.getCurrentSprite() : null;
				
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
			}
			
			@Override
			public void onDelete()
			{
				if(dispose && bullet != null)
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
		
		if (bullet != null)
		{
			obj.setBlendFunc(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
			obj.setZIndex(bullet.getZIndex());
		}
		
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
