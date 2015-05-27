package java2hu.object.bullet;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;

import shaders.ShaderLibrary;

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
		super(x, y);
		
		this.animation = animation;
		
		this.type = new IBulletType()
		{
			@Override
			public Color getEffectColor()
			{
				return null;
			}
			
			@Override
			public Animation getAnimation()
			{
				return animation;
			}
		};
		
		this.setZIndex(1000);
	}
	
	@Override
	public void onDraw()
	{
		if(animationPlaying)
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
		
		if(!animationPlaying)
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
		float perSecond = velocityX * game.LOGIC_TPS;
		
		this.velocityX = perSecond;
	}
	
	/**
	 * Legacy method to set velocity from ticks (and is calculated to velocity per second)
	 * @param velocityY
	 */
	@Deprecated
	public void setVelocityYTick(float velocityY)
	{
		float perSecond = velocityY * game.LOGIC_TPS;
		
		this.velocityY = perSecond;
	}
	
	/**
	 * Legacy method to return the velocity x from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityXTick()
	{
		return velocityX / game.LOGIC_TPS;
	}
	
	/**
	 * Legacy method to return the velocity y from ticks (and is calculated from velocity per second to velocity per tick)
	 */
	@Deprecated
	public float getVelocityYTick()
	{
		return velocityY / game.LOGIC_TPS;
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
	
	public void setRotationDeg(float rotation)
	{
		for(TextureRegion t : animation.getKeyFrames())
		{
			HitboxSprite s = (HitboxSprite) t;
			s.setRotation(rotation);
		}
	}
	
	public void setRotationRads(float rotation)
	{
		setRotationDeg((float) Math.toDegrees(rotation));
	}
	
	public void setScale(float scale)
	{
		setScale(scale, scale);
	}
	
	public void setScale(float scaleX, float scaleY)
	{
		for(TextureRegion t : animation.getKeyFrames())
		{
			HitboxSprite s = (HitboxSprite) t;
			s.setScale(scaleX, scaleY);
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
	
	protected boolean animationPlaying = false;
	protected boolean useSpawnAnimation = true;
	protected boolean useDeleteAnimation = true;
	protected SpawnAnimationSettings spawnAnimationSettings = new SpawnAnimationSettings();
	
	public static class SpawnAnimationSettings extends J2hObject
	{
		private float time = 10f;
		private float addedScaleX = 2f;
		private float addedScaleY = 2f;
		private float alpha = -0.5f;
		private boolean scaleDown = true;
		
		/**
		 * Time the spawn animation takes, default: 10 ticks
		 * @param time
		 */
		public void setTime(float time)
		{
			this.time = time;
		}
		
		public float getTime()
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
		
		public void scaleDown()
		{
			scaleDown = true;
		}
		
		public void scaleUp()
		{
			scaleDown = false;
		}
	}
	
	public void useSpawnAnimation(boolean useSpawnAnimation)
	{
		this.useSpawnAnimation = useSpawnAnimation;
	}
	
	public void useDeathAnimation(boolean bool)
	{
		this.useDeleteAnimation = bool;
	}
	
	public SpawnAnimationSettings getSpawnAnimationSettings()
	{
		return spawnAnimationSettings;
	}
	
	public void spawnAnimation()
	{	
		final float originalScaleX = getCurrentSprite().getScaleX();
		final float originalScaleY = getCurrentSprite().getScaleY();
		final float originalAlpha = getCurrentSprite().getColor().a;
		
		animationPlaying = true;
		
		final Bullet bullet = this;
		
		DrawObject obj = new DrawObject()
		{
			{
				setName("Bullet spawn animation");
			}
			
			private float time = bullet.getSpawnAnimationSettings().time;
			private float alpha = bullet.getSpawnAnimationSettings().alpha;
			
			private boolean scaleDown = bullet.getSpawnAnimationSettings().scaleDown;
		
			private float scaleX = !scaleDown ? bullet.getSpawnAnimationSettings().addedScaleX : originalScaleX + bullet.getSpawnAnimationSettings().addedScaleX;
			private float scaleY = !scaleDown ? bullet.getSpawnAnimationSettings().addedScaleY : originalScaleY + bullet.getSpawnAnimationSettings().addedScaleY;
			private float scaleDecreaseX = Math.abs(scaleX - originalScaleX) / time;
			private float scaleDecreaseY = Math.abs(scaleY - originalScaleY) / time;
			private float alphaIncrease = (float) (MathUtil.getDifference(alpha, originalAlpha) / time);
			
			@Override
			public void onDraw()
			{
				J2hGame g = Game.getGame();
				
				HitboxSprite current = new HitboxSprite(getCurrentSprite());
				
				current.setPosition(bullet.getX() - current.getWidth() / 2, bullet.getY() - current.getHeight() / 2);
				current.setScale(scaleX, scaleY);
				current.setAlpha(Math.min(Math.max(alpha, 0), 1));
				current.setHitboxScaleOffsetModifier(0f);
				
				current.draw(g.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				if(scaleDown)
				{
					scaleX -= scaleDecreaseX;
					scaleY -= scaleDecreaseY;
				}
				else
				{
					scaleX += scaleDecreaseX;
					scaleY += scaleDecreaseY;
				}
				
				alpha += alphaIncrease;
				
				if(getTicksAlive() > time || !bullet.isOnStage())
				{
					animationPlaying = false;
					Game.getGame().delete(this);
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
				return false;
			}
		};
		
		obj.setZIndex(getZIndex());
		obj.setShader(getShader());
		
		Game.getGame().spawn(obj);
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
	
	public void deleteAnimation()
	{
		if(BREAK_ANI == null)
		{
			BREAK = Loader.texture(Gdx.files.internal("sprites/bullet_break.png"));
			BREAK_ANI = ImageSplitter.getAnimationFromSprite(BREAK, 64, 64, 3f, 1,2,3,4,5,6,7,8);
		}
		
		final Bullet bullet = this;
		
		DrawObject obj = new DrawObject()
		{
			int ticks = 0;
			Color effect;
			
			{
				effect = getType().getEffectColor();
				
				if(effect == null)
					effect = Color.WHITE;
				
				effect = effect.cpy();
			}
			
			@Override
			public void onDraw()
			{
				J2hGame g = Game.getGame();
				
				HitboxSprite current = (HitboxSprite) BREAK_ANI.getKeyFrame(ticks / 2f);
				
				current.setOrigin(0, 0);
				
				float longest = Math.max(bullet.getWidth(), bullet.getHeight()) + 100;
				
				current.setSize(longest, longest);
				
				current.setPosition(bullet.getX() - (current.getWidth() / 2f), bullet.getY() - (current.getHeight() / 2f));
				
				current.setColor(effect);
				current.setAlpha(0.3f);
				
				current.draw(g.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				ticks++;
				
				if(BREAK_ANI.isAnimationFinished(ticks / 2f))
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
		
		obj.setShader(ShaderLibrary.GLOW.getProgram());
		
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
