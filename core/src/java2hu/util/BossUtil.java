package java2hu.util;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.Position;
import java2hu.SmartTimer;
import java2hu.background.BackgroundBossAura;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.overwrite.J2hObject;
import java2hu.pathing.PathingHelper.Path;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.sounds.TouhouSounds;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

/**
 * Simple Util to help with boss movement & effects
 */
public class BossUtil extends J2hObject
{
	/**
	 * Makes the boss move around randomly within the selected area.
	 * @param boss
	 * @param box
	 * @param millis
	 */
	public static void moveAroundRandomly(final Boss boss, Rectangle box, int millis)
	{
		moveAroundRandomly(boss, box, millis, false);
	}
	
	/**
	 * Makes the boss move around randomly within the selected area.
	 * @param boss
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param millis
	 */
	public static void moveAroundRandomly(final Boss boss, int minX, int maxX, int minY, int maxY, int millis)
	{
		moveAroundRandomly(boss, new Rectangle(minX, minY, maxX - minX, maxY - minY), millis, false);
	}
	
	/**
	 * Makes the boss move around randomly within the selected area.
	 * For debugging it's best to draw the box, to see where they can move in.
	 * If you want it to enable when you're in f3 debug mode, just pass Game.getGame().isDebugMode()
	 * @param boss
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param millis
	 * @param drawBox
	 */
	public static void moveAroundRandomly(final Boss boss, int minX, int maxX, int minY, int maxY, int millis, boolean drawBox)
	{
		moveAroundRandomly(boss, new Rectangle(minX, minY, maxX - minX, maxY - minY), millis, drawBox);
	}
	
	/**
	 * Makes the boss move around randomly within the selected area.
	 * For debugging it's best to draw the box, to see where they can move in.
	 * If you want it to enable when you're in f3 debug mode, just pass Game.getGame().isDebugMode()
	 * @param boss
	 * @param box
	 * @param millis
	 * @param drawBox
	 */
	public static void moveAroundRandomly(final Boss boss, final Rectangle box, final int millis, boolean drawBox)
	{
		boss.getPathing().setCurrentPath(new SimpleTouhouBossPath(boss, box));
		boss.getPathing().getCurrentPath().setTime(Duration.milliseconds(millis));

		if(drawBox)
		{
			Game.getGame().spawn(new DrawObject()
			{
				{
					setName("MoveAroundRandomly Cage");
				}
				
				@Override
				public void onDraw()
				{
					J2hGame g = Game.getGame();
					
					g.shape.begin(ShapeType.Line);
					
					g.shape.rect(box.getX(), box.getY(), box.getWidth(), box.getHeight());
					
					g.shape.end();
					
					if(getTicksAlive() > 200)
						Game.getGame().delete(this);
				}
			});
		}
	}
	
	/**
	 * Makes a boss smoothly move to a specified location over the specified milliseconds.
	 * This will create a pathway for the boss to follow over the selected timeframe, and use the scheduler to set the locations.
	 * Do not change the location of the boss during this time without cancelling the scheduled tasks. 
	 * It will look very choppy (because you set it somewhere else, next scheduled tasks puts it back)
	 * It will look like the boss is teleporting rapidly
	 * @param boss
	 * @param x
	 * @param y
	 * @param millis
	 */
	public static void moveTo(final Boss boss, final float x, final float y, float millis)
	{
		boss.getPathing().setCurrentPath(new Path(boss, Duration.milliseconds(millis))
		{
			{
				addPosition(new Position(x, y));
			}
		});
	}
	
	/**
	 * An explosion of glowing yellow maple leafs, useful for explosions!
	 * @param x
	 * @param y
	 */
	public static void mapleExplosion(float x, float y)
	{
		mapleExplosion(new Position(x, y));
	}
	
	/**
	 * An explosion of glowing yellow maple leafs, useful for explosions!
	 * @param x
	 * @param y
	 */
	public static void mapleExplosion(IPosition pos)
	{
		mapleExplosion(pos, 30, 5F, 3F, 0.5F);
	}
	
	/**
	 * An explosion of glowing yellow maple leafs, useful for explosions!
	 * @param x
	 * @param y
	 * @param spread - Max velocity that will be set to the leafs, so effectively the max distance from the origin.
	 * @param maxSize - Maximum size that will be randomly generated for the leafs.
	 * @param minSize - Minimum size that will be randomly generated for the leafs.
	 */
	public static void mapleExplosion(float x, float y, final int amount, final float spread, final float maxSize, final float minSize)
	{
		mapleExplosion(new Position(x, y), amount, spread, maxSize, minSize);
	}
	
	/**
	 * An explosion of glowing yellow maple leafs, useful for explosions!
	 * @param x
	 * @param y
	 * @param spread - Max velocity that will be set to the leafs, so effectively the max distance from the origin.
	 * @param maxSize - Maximum size that will be randomly generated for the leafs.
	 * @param minSize - Minimum size that will be randomly generated for the leafs.
	 */
	public static void mapleExplosion(IPosition pos, final int amount, final float spread, final float maxSize, final float minSize)
	{
		TouhouSounds.Enemy.EXPLOSION_2.play(0.1F);
		
		final TextureRegion leaf;
		final TextureRegion leafWhite;
		
		final Texture sprite = Loader.texture(Gdx.files.internal("sprites/explosion_maple.png"));

		leaf = new TextureRegion(sprite, 0, 0, 64, 64);
		leafWhite = new TextureRegion(sprite, 64, 0, 64, 64);
		
		final ArrayList<StageObject> objects = new ArrayList<StageObject>();

		for(int i = 0; i < amount; i++)
		{
			Game.getGame().spawn(new StageObject(pos.getX(), pos.getY())
			{
				{
					objects.add(this);
					
					setName("Maple Explosion");
				}

				{
					this.setZIndex(99999);
				}
				
				@Override
				public void onDelete()
				{
					super.onDelete();
					
					boolean canDispose = true;
					
					for(StageObject obj : objects)
					{
						if(obj == this)
							continue;
						
						if(obj.isOnStage())
						{
							canDispose = false;
							break;
						}
					}
					
					if(canDispose)
						sprite.dispose();
				};

				TextureRegion textureBack = new TextureRegion(leafWhite);
				TextureRegion textureFront =  new TextureRegion(leaf);
				float directionX = 0;
				float directionY = 0;
				float scale = 0f;
				float scaleX = 3F;
				float scaleY = 3F;
				float alpha = 0.5F;
				float rotation = 0F;

				float yRotation = (float) (360 * Math.random());

				@Override
				public void onUpdate(long tick)
				{
					if(alpha <= 0)
						Game.getGame().delete(this);

					yRotation += 4f * Math.random();

					if(directionX == 0 || directionY == 0)
					{
						scale = minSize;

						directionX = (float) ((Math.random() > 0.5 ? -Math.random() : Math.random()) * spread);
						directionY = (float) ((Math.random() > 0.5 ? -Math.random() : Math.random()) * spread);

						scale += (Math.random() > 0.5 ? -Math.random() : Math.random()) * (maxSize - minSize);

						directionX *= scale;
						directionY *= scale;
					}

					float rad = (float) Math.toRadians(yRotation);

					scaleX = (float) (scale * Math.cos(rad));
					scaleY = (float) (scale * Math.sin(rad));

					if(getTicksAlive() > 50)
					{
						alpha -= 0.03F;
					}

					rotation += 5F + 8F * Math.random();

					setX(getX() + directionX);
					setY(getY() + directionY);
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

				@Override
				public void setX(float x)
				{
					this.x = x;
				}

				@Override
				public void setY(float y)
				{
					this.y = y;
				}

				@Override
				public void onDraw()
				{
					Sprite textureBack;
					Sprite textureFront;

					textureBack = new Sprite(new TextureRegion(this.textureBack));
					textureFront = new Sprite(new TextureRegion(this.textureFront));

					textureFront.setRotation(rotation);
					textureFront.setAlpha(Math.max(alpha, 0F));
					textureFront.setScale(scaleX, scaleY);
					textureFront.setPosition(getX() - textureBack.getWidth() / 2, getY() - textureBack.getHeight() / 2);
					textureFront.draw(Game.getGame().batch);

					textureBack.setRotation(rotation);
					textureBack.setAlpha(Math.max(alpha - 0.1F, 0F));
					textureBack.setScale(scaleX, scaleY);
					textureBack.setPosition(getX() - textureBack.getWidth() / 2, getY() - textureBack.getHeight() / 2);
					textureBack.draw(Game.getGame().batch);
				}

				@Override
				public boolean isPersistant()
				{
					return true;
				}
			});
		}
	}
	
	/**
	 * An explosion of glowing yellow maple leafs, useful for explosions!
	 * @param x
	 * @param y
	 * @param maxSize - Maximum size that will be randomly generated for the leafs.
	 * @param minSize - Minimum size that will be randomly generated for the leafs.
	 */
	public static void mapleImplosion(final IPosition pos, final int amount, final float radius, final float maxSize, final float minSize)
	{
		final TextureRegion leaf;
		final TextureRegion leafWhite;
		
		final Texture sprite = Loader.texture(Gdx.files.internal("sprites/explosion_maple.png"));

		leaf = new TextureRegion(sprite, 0, 0, 64, 64);
		leafWhite = new TextureRegion(sprite, 64, 0, 64, 64);
		
		final ArrayList<StageObject> objects = new ArrayList<StageObject>();

		for(int i = 0; i < amount; i++)
		{
			float angle = (float) (Math.random() * 360);
			final float rotationRad = (float) Math.toRadians(angle);
			
			Game.getGame().spawn(new StageObject(pos.getX(), pos.getY())
			{
				{
					objects.add(this);
					
					setName("Maple Explosion");
				}

				{
					this.setZIndex(99999);
				}
				
				@Override
				public void onDelete()
				{
					super.onDelete();
					
					boolean canDispose = true;
					
					for(StageObject obj : objects)
					{
						if(obj == this)
							continue;
						
						if(obj.isOnStage())
						{
							canDispose = false;
							break;
						}
					}
					
					if(canDispose)
						sprite.dispose();
				};

				TextureRegion textureBack = new TextureRegion(leafWhite);
				TextureRegion textureFront =  new TextureRegion(leaf);
				float directionX = 0;
				float directionY = 0;
				float scale = 0f;
				float scaleX = 3F;
				float scaleY = 3F;
				float alpha = 0.5F;
				float rotation = 0F;

				float yRotation = (float) (360 * Math.random());

				@Override
				public void onUpdate(long tick)
				{
					if(alpha <= 0)
						Game.getGame().delete(this);

					yRotation += 4f * Math.random();

					if(directionX == 0 || directionY == 0)
					{
						scale = minSize;

						scale += (Math.random() > 0.5 ? -Math.random() : Math.random()) * (maxSize - minSize);
						
						setX((float) (pos.getX() + Math.cos(rotationRad) * (radius * Math.random())));
						setY((float) (pos.getY() + Math.sin(rotationRad) * (radius * Math.random())));
						
						float distance = MathUtil.getDistance(this, pos);

						float x = (pos.getX() - getX()) / distance * 1.8F;
						float y = (pos.getY() - getY()) / distance * 1.8F;
						
						x *= 6f;
						y *= 6f;

						directionX = x;
						directionY = y;
					}
					
					if(MathUtil.getDistance(this, pos) < 20)
					{
						directionX *= 0.01f;
						directionY *= 0.01f;
						
						alpha -= 0.1F;
					}

					float rad = (float) Math.toRadians(yRotation);

					scaleX = (float) (scale * Math.cos(rad));
					scaleY = (float) (scale * Math.sin(rad));

					if(getTicksAlive() > 50)
					{
						alpha -= 0.03F;
					}

					rotation += 5F + 8F * Math.random();

					setX(getX() + directionX);
					setY(getY() + directionY);
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

				@Override
				public void setX(float x)
				{
					this.x = x;
				}

				@Override
				public void setY(float y)
				{
					this.y = y;
				}

				@Override
				public void onDraw()
				{
					Sprite textureBack;
					Sprite textureFront;

					textureBack = new Sprite(new TextureRegion(this.textureBack));
					textureFront = new Sprite(new TextureRegion(this.textureFront));

					textureFront.setRotation(rotation);
					textureFront.setAlpha(Math.max(alpha, 0F));
					textureFront.setScale(scaleX, scaleY);
					textureFront.setPosition(getX() - textureBack.getWidth() / 2, getY() - textureBack.getHeight() / 2);
					textureFront.draw(Game.getGame().batch);

					textureBack.setRotation(rotation);
					textureBack.setAlpha(Math.max(alpha - 0.1F, 0F));
					textureBack.setScale(scaleX, scaleY);
					textureBack.setPosition(getX() - textureBack.getWidth() / 2, getY() - textureBack.getHeight() / 2);
					textureBack.draw(Game.getGame().batch);
				}

				@Override
				public boolean isPersistant()
				{
					return true;
				}
			});
		}
	}
	
	public static void startFight(final IPosition pos)
	{
		startFight(pos, Color.BLUE.cpy().mul(0.5f), Color.PURPLE.cpy().mul(0.5f));
	}
	
	public static void startFight(final IPosition pos, Color in, final Color out)
	{
		TouhouSounds.Enemy.ACTIVATE_3.play();
		
		charge(pos, in, false);
		
		mapleImplosion(pos, 20, 500f, 6f, 5.5f);
	}
	
	public static void chargeExplosion(final IPosition pos, Color color)
	{
		TouhouSounds.Enemy.EXPLOSION_3.play();
		
		charge(pos, color, true);
		
		for(int i = 0; i < 20; i += 5)
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				mapleExplosion(pos.getX(), pos.getY(), 3, 4f, 6f, 5.5f);
			}
		}, i + 1);
	}
	
	public static void addBossEffects(Boss boss, Color color, Color bgAura)
	{
		backgroundAura(boss, bgAura);
		bossAura(boss, Color.WHITE, color);
	}

	public static void charge(float x, float y, final Color color, final boolean outwards)
	{
		charge(new Position(x, y), color, outwards);
	}
	
	public static void charge(final IPosition pos, final Color color, final boolean outwards)
	{
		game.spawn(new DrawObject()
		{
			{
				setName("charge_circle");
				setZIndex(1000);
			}
			
			Mesh mesh;

			@Override
			public void onDraw()
			{
				if(mesh != null)
				{
					game.batch.end();
					
					MeshUtil.startShader();
					MeshUtil.renderMesh(mesh);
					MeshUtil.endShader();
					
					game.batch.begin();
				}
			}
			
			final int maxSize = 600;
			double size = outwards ? 0 : maxSize;
			Color white = color.cpy().mul(0.4f);
			
			@Override
			public void onUpdate(long tick)
			{
				super.onUpdate(tick);
				
				if(size > maxSize || size < 0)
				{
					game.delete(this);
				}
				
				mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(pos.getX(), pos.getY(), 30, 0, (float) size, color.cpy().mul(0.4f), color.cpy()));
				
				double increment = 20;
				
				if(outwards)
					size += increment;
				else
					size -= increment;
			}
		});
	}

	public static Texture bossAuraTexture = Loader.texture(Gdx.files.internal("sprites/enemy/enemy_aura.png"));
	public static Texture energyLeakTexture = Loader.texture(Gdx.files.internal("sprites/enemy/enemy_eff_aura.png"));
	
	{
		bossAuraTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		energyLeakTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
	}
	
	public static BossAura bossAura(Boss boss, Color aura, Color energyLeak)
	{
		BossAura ba = new BossAura();
		ba.boss = boss; 
		
		boss.addChild(ba);
		
		Animation auraAnimation = ImageSplitter.getAnimationFromSprite(bossAuraTexture, 48, 48, 0.08f, 1,2,3,4,5,6,7,8);
		
		for(TextureRegion r : auraAnimation.getKeyFrames())
		{
			((Sprite)r).setColor(aura);
			((Sprite)r).setScale(1f);
		}
		
		ba.aura = auraAnimation;
		
		ba.energyLeak = new Sprite(energyLeakTexture, 0, 0, 48, 64);
		ba.energyLeak.setColor(energyLeak);
		
		ba.setZIndex(1);
		
		Game.getGame().spawn(ba);
		
		return ba;
	}
	
	public static class BossAura extends DrawObject
	{
		public Boss boss;
		
		public Animation aura;
		
		public Sprite energyLeak;
		
		{
			setName("Boss Aura");
		}
		
		@Override
		public boolean isPersistant()
		{
			return true;
		}
		
		@Override
		public void onUpdate(long tick)
		{
			final J2hGame game = Game.getGame();
			
			final BossAura ba = this;
			
			if(game.getTick() % 1 == 0)
			{
				game.spawn(new DrawObject()
				{
					{
						ba.addChild(this);
						setZIndex(boss.getZIndex() - 2);
						setName("Boss Aura (Clouds)");
					}
					
					Animation aura = AnimationUtil.copyAnimation(ba.aura);
					
					float xMod = Math.random() > 0.5 ? -1 : 1;
					float yMod = Math.random() > 0.5 ? -1 : 1;
					float x = (float) (xMod * 150 * Math.cos(Math.toRadians(Math.random() * 360)));
					float y = (float) (yMod * 150 * Math.sin(Math.toRadians(Math.random() * 360)));
					
					float velX = 0;
					float velY = 0;
					
					float distance = 0f;
					
					@Override
					public void onDraw()
					{
						Sprite currentAura = AnimationUtil.getCurrentSprite(aura, true);
						currentAura.setPosition(boss.getX() + x - currentAura.getWidth() / 2, boss.getY() + y - currentAura.getHeight() / 2);
						currentAura.setAlpha(distance > 40 ? 0 : 1 - (distance - 10) / 30f);
						currentAura.setScale(0.8f);
						currentAura.draw(game.batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						if(!ba.isOnStage())
						{
							game.delete(this);
						}
						
						distance = MathUtil.getDistance(0, 0, x, y);
						
						if(distance < 2f)
						{
							game.delete(this);
						}

						velX = this.x / distance * 1F;
						velY = this.y / distance * 1F;
						
						x -= velX;
						y -= velY;
					}
				});
			}
			
			if(game.getTick() % 5 == 0)
			{
				game.spawn(new DrawObject()
				{
					{
						ba.addChild(this);
						setZIndex(boss.getZIndex() - 1);
						setName("Boss Aura (Energy Leak)");
						setShader(ShaderLibrary.GLOW.getProgram());
					}
					
					Sprite leak = new Sprite(energyLeak);
					
					float x = (float) (-leak.getWidth() / 2 + (Math.random() > 0.5 ? -1 : 1) * (Math.random() * 5));
					float y = (float) (-leak.getHeight() / 2 + (Math.random() > 0.5 ? -1 : 1) * (Math.random() * 5));
					
					float height = 0f;
					float heightIncrease = (float)(0.1f + 0.01f * Math.random());
					
					{
						leak.setPosition(boss.getX() + x, boss.getY() + y);
						leak.setOrigin(leak.getOriginX(), 0);
						leak.setScale((float) (2f + Math.random() * 1), 0);
						leak.setAlpha(0.4f);
					}
					
					@Override
					public void onDraw()
					{
						leak.setScale(leak.getScaleX(), height);
						leak.draw(game.batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						if(!ba.isOnStage())
						{
							game.delete(this);
						}
						
						height += heightIncrease;
						
						leak.setPosition(boss.getX() + x, boss.getY() + y);
						
						if(height > 3.5f)
							game.delete(this);
					}
				});
			}
		}

		@Override
		public void onDraw()
		{

		}
	}
	
	/**
	 * Distorts the background and places the "Magic Square" (Star of David) effect around them 
	 * @param boss
	 */
	public static BackgroundAura backgroundAura(final Boss boss, final Color color)
	{
		final Texture texture = Loader.texture(Gdx.files.internal("sprites/enemy/magic_square.png"));
		texture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
		
		BackgroundAura meshDrawer = new BackgroundAura()
		{
			ShaderProgram magicSquareShader;
			ShaderProgram auraShader;
			
			boolean useMagicSquare = true;
			
			{
				 // this shader tells opengl where to put things
		        String vertexShader = "attribute vec4 a_position;    \n"
		        					+ "attribute vec4 a_color;       \n"
		        					+ "attribute vec2 a_texCoords;   \n"
		    						+ "varying vec4 v_color;         \n"
		    						+ "varying vec2 v_texCoords;     \n"
		    						+ "uniform mat4 u_projTrans;     \n"
		                            + "void main()                   \n"
		                            + "{                             \n"
		                            + "   v_color = a_color;         \n"
		                            + "   v_texCoords = a_texCoords; \n"
		                            + "   gl_Position = u_projTrans * a_position;  \n"
		                            + "}                             \n";
		 
		        // this one tells it what goes in between the points (i.e
		        // colour/texture)
		        String fragmentShader = "#ifdef GL_ES				 \n"
		    						  + "#define LOWP lowp			 \n"
		    						  + "precision mediump float;	 \n"
		    						  + "#else						 \n"
		    						  + "#define LOWP 				 \n"
		    						  + "#endif						 \n"
		                              + "varying vec4 v_color;       \n"
		                              + "varying vec2 v_texCoords;   \n"
		                              + "uniform sampler2D u_texture;\n"
		                              + "void main()                 \n"
		                              + "{                           \n"
		                              + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);   \n"
		                              + "}";
		 
		        // make an actual shader from our strings
		        magicSquareShader = new ShaderProgram(vertexShader, fragmentShader);
		        
		        // check there's no shader compile errors
		        if (!magicSquareShader.isCompiled())
		            new IllegalStateException(magicSquareShader.getLog()).printStackTrace();
		        
				 // this shader tells opengl where to put things
		        vertexShader =		  "attribute vec4 a_position;    \n"
		        					+ "attribute vec4 a_color;       \n"
		    						+ "varying vec4 v_color;         \n"
		    						+ "uniform mat4 u_projTrans;     \n"
		                            + "void main()                   \n"
		                            + "{                             \n"
		                            + "   v_color = a_color;         \n"
		                            + "   gl_Position = u_projTrans * a_position;  \n"
		                            + "}                             \n";
		 
		        // this one tells it what goes in between the points (i.e
		        // colour/texture)
		        fragmentShader = 		"#ifdef GL_ES				 \n"
		    						  + "#define LOWP lowp			 \n"
		    						  + "precision mediump float;	 \n"
		    						  + "#else						 \n"
		    						  + "#define LOWP 				 \n"
		    						  + "#endif						 \n"
		                              + "varying vec4 v_color;       \n"
		                              + "void main()                 \n"
		                              + "{                           \n"
		                              + "  gl_FragColor = v_color;   \n"
		                              + "}";

		        // make an actual shader from our strings
		        auraShader = new ShaderProgram(vertexShader, fragmentShader);
		        
		        // check there's no shader compile errors
		        if (!auraShader.isCompiled())
		            new IllegalStateException(auraShader.getLog()).printStackTrace();
			}
			
			float timerX = 1f;
			float timerXIncrease = 0.1f;
			boolean timerXforwards = true;
			
			{
				setName("Background Aura");
			}
			
			Mesh magicSquareMesh;
			Mesh auraMesh;
			
			int size = 300;
			
			float rotation = 0;
			
			@Override
			public void onDraw()
			{
				Game.getGame().batch.end();
				
				float xMod = timerX;
				
				float yMod = 1f;
				
				float[] vertices = createAuraMesh(boss.getX(), boss.getY(), size);
				
				if(auraMesh == null)
				{
					auraMesh = new Mesh(false, vertices.length, 0, 
			    			new VertexAttribute(Usage.Position, 2, "a_position"),
			    			new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
					
					addDisposable(auraMesh);
				}
				
				auraMesh.setVertices(vertices);
				
				auraShader.begin();
			
				Gdx.gl.glEnable(GL20.GL_BLEND);
				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				
				auraShader.setUniformMatrix("u_projTrans", game.camera.camera.combined);
				auraMesh.render(auraShader, GL20.GL_TRIANGLES);
				
				auraShader.end();
				
				if(useMagicSquare)
				{
					vertices = createMagicSquareMesh(boss.getX(), boss.getY(), 230 * scale, rotation, xMod, yMod);
					
					if(magicSquareMesh == null)
					{
						magicSquareMesh = new Mesh(false, vertices.length, 0, 
				    			new VertexAttribute(Usage.Position, 2, "a_position"),
				    			new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
				    			new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
						
						addDisposable(magicSquareMesh);
					}
					
					magicSquareMesh.setVertices(vertices);

					magicSquareShader.begin();

					magicSquareShader.setUniformMatrix("u_projTrans", Game.getGame().camera.camera.combined);
					magicSquareShader.setUniformi("u_texture", 0);
					texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);

					Gdx.gl.glEnable(GL20.GL_BLEND);
					Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

					magicSquareMesh.render(magicSquareShader, GL20.GL_TRIANGLES);

					magicSquareShader.end();
				}
				
				Game.getGame().batch.begin();
			}
			
			SmartTimer timer = new SmartTimer(0.2f, -30f, -25f, 30f, 25f, 0.05f);
			
			float scale = 0;
			int ticksFast = 0;
			
			@Override
			public void onUpdate(long tick)
			{
				final int fastBefore = 50;
				final int fastTicks = 4;
				
				final int staggerBefore = 100;
				
				int effectiveTicks = fastBefore * fastTicks;
				
				timer.tick();
				
				rotation += 0.5f;
				
				if(scale < 1)
				{
					scale += 1f/effectiveTicks;
				}
				
				{
					float min = 0.6f;
					float max = 1f;

					float minSpeed = 0.02f;
					float maxSpeed = 1f;

					if(timerXforwards)
					{
						timerX += timerXIncrease * Math.max(MathUtil.getDifference(timerX, max), minSpeed) * Math.min(Math.max(MathUtil.getDifference(timerX, min), minSpeed), maxSpeed);

						if(timerX > max)
							timerXforwards = false;
					}
					else
					{
						timerX -= timerXIncrease * Math.max(MathUtil.getDifference(timerX, min), minSpeed) *  Math.min(Math.max(MathUtil.getDifference(timerX, max), minSpeed), maxSpeed);

						if(timerX < min)
							timerXforwards = true;
					}
				}
				
				if(getTicksAlive() < fastBefore && ticksFast < fastTicks)
				{
					ticksFast++;
					onUpdate(tick);
				}
				else if(getTicksAlive() >= fastBefore && getTicksAlive() < staggerBefore)
				{
					long over = getTicksAlive() - fastBefore;
					
					long ticks = (long) ((1 - (double)over / (double)(staggerBefore - fastBefore)) * fastTicks);

					if(ticksFast < ticks)
					{
						ticksFast++;
						onUpdate(tick);
					}
				}
				else
				{
					ticksFast = 0;
				}
			}
			
			public float[] createMagicSquareMesh(float x, float y, float radius, float rotation, float xMultiplier, float yMultiplier)
			{
				xMultiplier = 1/xMultiplier;
				yMultiplier = 1/yMultiplier;
				
				int size = 700; // How much triangles make up this mesh.
				
				Color color = Color.WHITE.cpy();
				color.a = 0.4f;
				
		        ArrayList<Float> verticesList = new ArrayList<Float>();
		        
		        float centerX = x;
		        float centerY = y;
		        
		        float increment = 360f / size;
		        float widthModifier = radius;
		        float heightModifier = radius;
		        
		        float textureOffsetX = 0.5f;
		        float textureOffsetY = 0.5f;
		        
		        // For every section one triangle starting from the middle to the ends.
		        for(float deg = 0; deg < 360 - increment; deg += increment)
		        {
			        double rad1 = Math.toRadians(deg);
			        double sin1 = Math.sin(rad1);
			        double cos1 = Math.cos(rad1);
			        
			        double rad1tex = Math.toRadians(deg + rotation);
			        double sin1tex = Math.sin(rad1tex);
			        double cos1text = Math.cos(rad1tex);
			        
			        double rad2 = Math.toRadians(deg + increment);
			        double sin2 = Math.sin(rad2);
			        double cos2 = Math.cos(rad2);
			        
			        double rad2tex = Math.toRadians(deg + increment + rotation);
			        double sin2tex = Math.sin(rad2tex);
			        double cos2tex = Math.cos(rad2tex);
		        	
		        	// Left top
		        	verticesList.add((float) (sin1 * widthModifier) + centerX);
		        	verticesList.add((float) (cos1 * heightModifier) + centerY);
		        	verticesList.add((float) (sin1tex / 2 * xMultiplier) + textureOffsetX);
		        	verticesList.add((float) (cos1text / 2 * yMultiplier) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Middle bot
		        	verticesList.add(centerX);
		        	verticesList.add(centerY);
		        	verticesList.add(textureOffsetX);
		        	verticesList.add(textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Right top
		        	verticesList.add((float) (sin2 * widthModifier) + centerX);
		        	verticesList.add((float) (cos2 * heightModifier) + centerY);
		        	verticesList.add((float) (sin2tex / 2 * xMultiplier) + textureOffsetX);
		        	verticesList.add((float) (cos2tex / 2 * yMultiplier) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        }
		    	
		    	float[] vertices = new float[verticesList.size()];
		    	
		    	int i = 0;
		    	
		    	for(Float flo : verticesList)
		    	{
		    		vertices[i] = flo;
		    		i++;
		    	}
				
				return vertices;
			}
			
			public float[] createAuraMesh(float x, float y, float radius)
			{
				int size = 130; // How much triangles make up this mesh.
				
				Color inner = color.cpy();
				inner.a = 0.6f;
				
				Color middle = color.cpy();
				middle.a = 0.2f;
				
				float middleMultiplier = 0.7f; // Between 0 and 1

				Color outer = color.cpy();
				outer.a = 0.0f;
				
		        ArrayList<Float> verticesList = new ArrayList<Float>();
		        
		        float centerX = x;
		        float centerY = y;
		        
		        float increment = 360f / size;
		        
		        // For every section one triangle starting from the middle to the ends.
		        for(float deg = 0; deg < 360 - increment; deg += increment)
		        {
			        double rad1 = Math.toRadians(deg);
			        double sin1 = Math.sin(rad1);
			        double cos1 = Math.cos(rad1);
			        
			        double rad2 = Math.toRadians(deg + increment);
			        double sin2 = Math.sin(rad2);
			        double cos2 = Math.cos(rad2);
		        	
		        	// Left top
		        	verticesList.add((float) (sin1 * (radius * middleMultiplier)) + centerX);
		        	verticesList.add((float) (cos1 * (radius * middleMultiplier)) + centerY);

		        	verticesList.add(middle.toFloatBits());
		        	
		        	// Middle bot
		        	verticesList.add(centerX);
		        	verticesList.add(centerY);

		        	verticesList.add(inner.toFloatBits());
		        	
		        	// Right top
		        	verticesList.add((float) (sin2 * (radius * middleMultiplier)) + centerX);
		        	verticesList.add((float) (cos2 * (radius * middleMultiplier)) + centerY);

		        	verticesList.add(middle.toFloatBits());
		        }
		        
		        // For every section one triangle starting from the middle to the ends.
		        for(float deg = 0; deg < 360 - increment; deg += increment)
		        {
		        	// Triangle 1
		        	
			        double rad1 = Math.toRadians(deg);
			        double sin1 = Math.sin(rad1);
			        double cos1 = Math.cos(rad1);
			        
			        double rad2 = Math.toRadians(deg + increment);
			        double sin2 = Math.sin(rad2);
			        double cos2 = Math.cos(rad2);
		        	
		        	// Left top
		        	verticesList.add((float) (sin1 * radius) + centerX);
		        	verticesList.add((float) (cos1 * radius) + centerY);

		        	verticesList.add(outer.toFloatBits());
		        	
		        	// left bot
		        	verticesList.add((float) (sin1 * (radius * middleMultiplier)) + centerX);
		        	verticesList.add((float) (cos1 * (radius * middleMultiplier)) + centerY);

		        	verticesList.add(middle.toFloatBits());
		        	
		        	// Right bot
		        	verticesList.add((float) (sin2 * (radius * middleMultiplier)) + centerX);
		        	verticesList.add((float) (cos2 * (radius * middleMultiplier)) + centerY);
		        	
		        	verticesList.add(middle.toFloatBits());
		        }
		        
		        // For every section one triangle starting from the middle to the ends.
		        for(float deg = 0; deg < 360 - increment; deg += increment)
		        {
		        	// Triangle 2
		        	
			        double rad1 = Math.toRadians(deg);
			        double sin1 = Math.sin(rad1);
			        double cos1 = Math.cos(rad1);
			        
			        double rad2 = Math.toRadians(deg + increment);
			        double sin2 = Math.sin(rad2);
			        double cos2 = Math.cos(rad2);
		        	
		        	// Right top
		        	verticesList.add((float) (sin2 * radius) + centerX);
		        	verticesList.add((float) (cos2 * radius) + centerY);

		        	verticesList.add(outer.toFloatBits());
		        	
		        	// Left top
		        	verticesList.add((float) (sin1 * radius) + centerX);
		        	verticesList.add((float) (cos1 * radius) + centerY);

		        	verticesList.add(outer.toFloatBits());
		        	
		        	// Right bot
		        	verticesList.add((float) (sin2 * (radius * middleMultiplier)) + centerX);
		        	verticesList.add((float) (cos2 * (radius * middleMultiplier)) + centerY);

		        	verticesList.add(middle.toFloatBits());
		        }
		    	
		    	float[] vertices = new float[verticesList.size()];
		    	
		    	int i = 0;
		    	
		    	for(Float flo : verticesList)
		    	{
		    		vertices[i] = flo;
		    		i++;
		    	}
				
				return vertices;
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}

			@Override
			public boolean isMagicSquareEnabled()
			{
				return useMagicSquare;
			}

			@Override
			public void setMagicSquareEnabled(boolean bool)
			{
				useMagicSquare = bool;
			}
		};
		
		meshDrawer.setZIndex(boss.getZIndex() - 10);
		
		Game.getGame().spawn(meshDrawer);
		
		return meshDrawer;
	}
	
	public static abstract class BackgroundAura extends DrawObject
	{
		public abstract boolean isMagicSquareEnabled();
		public abstract void setMagicSquareEnabled(boolean bool);
	}
	
	/**
	 * Places a circle around the boss, that decreases how long the spellcard has left.
	 * Also binds the object to a boss aura instance, which will distort it.
	 */
	public static DrawObject spellcardCircle(final Boss boss, final Spellcard card, BackgroundBossAura aura)
	{
		DrawObject obj = spellcardCircle(boss, card);
		obj.setFrameBuffer(aura.getBackgroundBuffer());
		obj.setZIndex(aura.getZIndex() - 1);
		
		return obj;
	}
	
	/**
	 * Places a circle around the boss, that decreases how long the spellcard has left.
	 */
	public static DrawObject spellcardCircle(final Boss boss, final Spellcard card)
	{
		final Texture text = Loader.texture(Gdx.files.internal("sprites/eff_line.png"));
	
		DrawObject obj = new DrawObject()
		{
			{
				addDisposable(text);
			}
			
			ShaderProgram program = ShaderLibrary.STANDARD.getProgram();
			Mesh mesh;
			
			float sizeMinLeft = -100;
			float sizeMaxLeft = 0;
			
			float sizeMinRight = 0;
			float sizeMaxRight = 100;
			
			float rotation = 0f;
			
			@Override
			public void onDraw()
			{
				float[] vertices = makeVertices();
				
				boolean createNewMesh = mesh == null || mesh.getMaxVertices() < vertices.length;
				
				if(createNewMesh)
				{
					if(mesh != null)
						mesh.dispose();
					
					mesh = new Mesh(false, vertices.length, 0,
			                new VertexAttribute(Usage.Position, 2, "a_position"),
			                new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"),
			                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
				}
				
				mesh.setVertices(vertices);
				
				if(mesh != null)
				{
					game.batch.end();
					
					program.begin();
					program.setUniformMatrix("u_projTrans", Game.getGame().camera.camera.combined);
					
					program.setUniformi("u_texture", 0);
					text.bind();
					
					Gdx.gl.glEnable(GL20.GL_BLEND);
					Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					
					mesh.render(program, GL20.GL_TRIANGLES);
					
					program.end();
					
					game.batch.begin();
				}
			}
			
			float size = 400;
			float speed = 350;
			
			float width = 30;
			
			@Override
			public void onUpdateDelta(float delta)
			{
				final boolean onStage = boss.isOnStage();
				
				if(!onStage)
				{
					speed *= 1.025f;
					
					if(sizeMaxRight > Math.max(game.getWidth(), game.getHeight()) * 2f)
					{
						game.delete(this);
					}
				}
				else
				{
					final double cardMultiplier = card.getTimeLeft().getValue() / card.getSpellcardTime().getValue();

					size = 50f + (float) (350 * cardMultiplier);
				}

				rotation += 60f * delta;
				
				sizeMaxRight += delta * speed;

				sizeMaxLeft += delta * speed;

				sizeMinRight += delta * speed;

				sizeMinLeft += delta * speed;
				
				if(onStage)
				{
					sizeMaxRight = Math.min(size, sizeMaxRight);
					sizeMaxLeft = Math.min(size, sizeMaxLeft);
					sizeMinRight = Math.min(size - width, sizeMinRight);
					sizeMinLeft = Math.min(size - width, sizeMinLeft);
				}
			}
			
			public float[] makeVertices()
			{
				int sections = 100; // How much triangles make up this mesh, also how much sections will bend
				
				Color color = new Color(2f, 2f, 2f, 1f);
				
		        ArrayList<Float> verticesList = new ArrayList<Float>();
		        
		        float centerX = boss.getX();
		        float centerY = boss.getY();
		        
		        float increment = 360f / sections;
		        
		        float textureOffsetX = 0f;
		        float textureOffsetY = 0f;
		        
		        int tick = -1;
		        
		        // For every section one triangle starting from the middle to the ends.
		        for(float deg = 0; deg < 360; deg += increment)
		        {
		        	tick++;
		        	
		        	float sizeMinRight = this.sizeMinRight;
		        	float sizeMinLeft = this.sizeMinLeft;
		        	
		        	float sizeMaxRight = this.sizeMaxRight;
		        	float sizeMaxLeft = this.sizeMaxLeft;
		        	
		        	float loopAmount = 20f;
		        	float degreeMul1 = (deg / 360f) * loopAmount;
		        	float degreeMul2 = ((deg + increment) / 360f) * loopAmount;
		        	
		        	/**
		        	 * Flip the values to make an arch instead of steps
		        	 */
		        	if(tick % 2 < 1)
		        	{
		        		float sizeToLeft = sizeMinRight;
		        		sizeMinRight = sizeMinLeft;
		        		sizeMinLeft = sizeToLeft;
		        		
		        		sizeToLeft = sizeMaxRight;
		        		sizeMaxRight = sizeMaxLeft;
		        		sizeMaxLeft = sizeToLeft;
		        	}
		        	
			        double rad1 = Math.toRadians(deg + rotation);
			        double sin1 = Math.sin(rad1);
			        double cos1 = Math.cos(rad1);
			        
			        double sinLeftTex = degreeMul2;
			        double cosBotTex = 0f;
			        
			        double rad2 = Math.toRadians(deg + increment + rotation);
			        double sin2 = Math.sin(rad2);
			        double cos2 = Math.cos(rad2);
			        
			        double sinRightTex = degreeMul1;
			        double cosTopTex = 0.125f;
			        
			        // Triangle one
			        
		        	// Left top
		        	verticesList.add((float) (sin1 * sizeMaxLeft) + centerX);
		        	verticesList.add((float) (cos1 * sizeMaxLeft) + centerY);
		        	verticesList.add((float) (cosTopTex) + textureOffsetX);
		        	verticesList.add((float) (sinLeftTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Right bot
		        	verticesList.add((float) (sin2 * sizeMinRight) + centerX);
		        	verticesList.add((float) (cos2 * sizeMinRight) + centerY);
		        	verticesList.add((float) (cosBotTex) + textureOffsetX);
		        	verticesList.add((float) (sinRightTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Right top
		        	verticesList.add((float) (sin2 * sizeMaxRight) + centerX);
		        	verticesList.add((float) (cos2 * sizeMaxRight) + centerY);
		        	verticesList.add((float) (cosTopTex) + textureOffsetX);
		        	verticesList.add((float) (sinRightTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Triangle two
		        	
		        	// Left bot
		        	verticesList.add((float) (sin1 * sizeMinLeft) + centerX);
		        	verticesList.add((float) (cos1 * sizeMinLeft) + centerY);
		        	verticesList.add((float) (cosBotTex) + textureOffsetX);
		        	verticesList.add((float) (sinLeftTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Right bot
		        	verticesList.add((float) (sin2 * sizeMinRight) + centerX);
		        	verticesList.add((float) (cos2 * sizeMinRight) + centerY);
		        	verticesList.add((float) (cosBotTex) + textureOffsetX);
		        	verticesList.add((float) (sinRightTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        	
		        	// Left top
		        	verticesList.add((float) (sin1 * sizeMaxLeft) + centerX);
		        	verticesList.add((float) (cos1 * sizeMaxLeft) + centerY);
		        	verticesList.add((float) (cosTopTex) + textureOffsetX);
		        	verticesList.add((float) (sinLeftTex) + textureOffsetY);
		        	verticesList.add(color.toFloatBits());
		        }
		    	
		    	float[] vertices = new float[verticesList.size()];
		    	
		    	int i = 0;
		    	
		    	for(Float flo : verticesList)
		    	{
		    		vertices[i] = flo;
		    		i++;
		    	}
				
				return vertices;
			}
			
			@Override
			public boolean isPersistant()
			{
				return true; // Disposes itself.
			}
		};
		
		obj.setZIndex(boss.getZIndex() - 15);
		
		game.spawn(obj);
		
		return obj;
	}

	public static Texture cloudTexture = bossAuraTexture;
	
	/**
	 * single way cloud arching towards an location 
	 * @param object
	 * @param ticks
	 */
	public static void cloudSpecial(final StageObject object, int ticks)
	{
		cloudSpecial(object, new Color(1f, 0.1f, 0.1f, 1f), new Color(0.1f, 0.1f, 1f, 1f), ticks);
	}
	
	/**
	 * single way cloud arching towards an location 
	 * @param object
	 * @param ticks
	 */
	public static void cloudSpecial(final StageObject object, final Color color1, final Color color2, int ticks)
	{
		TouhouSounds.Enemy.ACTIVATE_3.play();
		
		for(int tick = 0; tick < ticks; tick++)
		{
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					float startAngle = 20;
					
					Position start = getPositionAngle(object, 210, 700);
					
					boolean clockwise = false;
					
					cloudArch(object, object.getZIndex(), start, startAngle, 300, 200, clockwise, Color.BLACK.cpy(), color1, color2);
				}
			}, tick);
		}
	}
	
	private static void cloudArchBlack(IPosition to, int zIndexTo, IPosition start, float startAngle, boolean clockWise, Color color1, Color color2)
	{
		cloudArch(to, zIndexTo, start, startAngle, clockWise, Color.BLACK.cpy(), color1, color2);
	}
	
	private static void cloudArch(IPosition to, int zIndexTo, IPosition start, float startAngle, boolean clockWise, Color color1, Color color2, Color color3)
	{
		float vertical = 300;
		float horizontal = 300;
		
		cloudArch(to, zIndexTo, start, startAngle, vertical, horizontal, clockWise, color1, color2, color3);
	}
	
	private static void cloudArch(IPosition to, int zIndexTo, IPosition start, float startAngle, float verticalSpread, float horizontalSpread, boolean clockWise, Color color1, Color color2, Color color3)
	{
		cloudArch(to, zIndexTo, 5, start, startAngle, verticalSpread, horizontalSpread, clockWise, color1, color2, color3);
	}
	
	private static void cloudArch(IPosition to, int zIndexTo, int amount, IPosition start, float startAngle, float verticalSpread, float horizontalSpread, boolean clockWise, Color color1, Color color2, Color color3)
	{
		for(int i = 0; i < amount; i++)
		{
			float modifier = clockWise ? -1 : 1;
			
			float angleOffset = MathUtil.getAngle(start, to) + modifier * startAngle;

			float rad = (float) Math.toRadians(angleOffset);

			float offsetVertical = (float) (verticalSpread * ((Math.random() - 0.5f) * 2f));
			float offsetHorizontal = (float) (horizontalSpread * ((Math.random() - 0.5f) * 2f));

			Position pos = new Position(start);

			pos.setX((float) (pos.getX() + Math.cos(rad) * offsetHorizontal));
			pos.setY((float) (pos.getY() + Math.sin(rad) * offsetVertical));

			Cloud cloud = new Cloud(pos.getX(), pos.getY(), to, startAngle, clockWise);
			cloud.setSpeed((float) (10f + 5f * Math.random()));
			cloud.getCurrentSprite().setColor(Color.BLACK.cpy());

			boolean firstBefore = Math.random() > 0.5f;

			int first = zIndexTo - 11;
			int second = zIndexTo - 12;

			cloud.setZIndex(firstBefore ? first : second);

			game.spawn(cloud);

			pos = new Position(start);

			offsetVertical = (float) (verticalSpread * ((Math.random() - 0.5f) * 2f));
			offsetHorizontal = (float) (horizontalSpread * ((Math.random() - 0.5f) * 2f));

			pos.setX((float) (pos.getX() + Math.cos(rad) * offsetHorizontal));
			pos.setY((float) (pos.getY() + Math.sin(rad) * offsetVertical));

			cloud = new Cloud(pos.getX(), pos.getY(), to, startAngle, clockWise);
			cloud.setSpeed((float) (10f + 5f * Math.random()));
			cloud.getCurrentSprite().setColor(Math.random() > 0.5f ? color1 : color2);

			cloud.setZIndex(firstBefore ? second : first);

			game.spawn(cloud);
		}
	}
	
	/**
	 * 6 way dark clouds mixed with red and blue arching towards a position signing the entrance of a boss. (TH-14+ style)
	 * @param object
	 * @param ticks
	 */
	public static void cloudEntrance(final StageObject object, int ticks)
	{
		cloudEntrance(object, new Color(1f, 0.1f, 0.1f, 1f), new Color(0.1f, 0.1f, 1f, 1f), ticks);
	}
	
	/**
	 * 6 way dark clouds mixed with specified colors arching towards a position signing the entrance of a boss. (TH-14+ style)
	 * @param object
	 * @param ticks
	 */
	public static void cloudEntrance(final StageObject object, final Color color1, final Color color2, int ticks)
	{
		cloudEntrance(object, color1, color2, color1, color2, color1, color2, ticks);
	}
		
		
	/**
	 * 6 way dark clouds mixed with specified colors arching towards a position signing the entrance of a boss. (TH-14+ style)
	 * @param object
	 * @param ticks
	 */
	public static void cloudEntrance(final StageObject object, final Color color1, final Color color2, final Color color3, final Color color4, final Color color5, final Color color6, int ticks)
	{
		TouhouSounds.Enemy.ACTIVATE_3.play();

		for(int tick = 0; tick < ticks; tick += 3)
		{
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					Color[] colors1 = { color1, color2, color3 };
					Color[] colors2 = { color4, color5, color6 };
					
					int color1tick = 0;
					int color2tick = 0;
					
					for(boolean bool : new boolean[] { true, false })
					for(int i = 1 ; i <= 3; i++)
					{
						float startAngle = 40;

						Position start = getPositionAngle(object, 360 / 3f * i - 33, 500);

						boolean clockwise = bool;
						
						Color color = null;
						
						if(bool)
						{
							color = colors1[color1tick++];
						}
						else
						{
							color = colors2[color2tick++];
						}

						cloudArchBlack(object, object.getZIndex(), start, startAngle, clockwise, color, color);
					}
				}
			}, tick);
		}
	}
	
	private static Position getPositionAngle(IPosition center, float angle, float radius)
	{
		double rad = Math.toRadians(angle);
		
		double x = center.getX() + Math.cos(rad) * radius;
		double y = center.getY() + Math.sin(rad) * radius;
		
		return new Position(x, y);
	}
	
	public static class CloudOld extends StageObject
	{
		public static CloudOld rotateTo(StageObject object, Sprite texture, float distance, float degree)
		{
			float beginX = (float) (object.getX() + (Math.random() < 0.5 ? -1 : 1) * (Math.random() * 60) + (float) Math.sin(Math.toRadians(degree)) * distance);
			float beginY = (float) (object.getY() + (Math.random() < 0.5 ? -1 : 1) * (Math.random() * 200) + (float) Math.cos(Math.toRadians(degree)) * distance);
			
			boolean disposeTexture = false;
			
			if(texture == null)
			{
				Texture text = Loader.texture(Gdx.files.internal("sprites/enemy/enemy_aura.png"));
				texture = new Sprite(text, 0, 0, 46, 46);
				disposeTexture = true;
			}
			
			texture.setRotation((float) (Math.random() * 360f));
			
			CloudOld cloud = new CloudOld(texture, distance, degree, object.getX(), object.getY(), beginX, beginY);
			
			if(disposeTexture)
				cloud.addDisposable(texture.getTexture());
			
			return cloud;
		}
		
		{
			setName("Boss Cloud");
		}
		
		public float distance;
		public Sprite sprite;
		public float beginX;
		public float beginY;
		public float beginRotation;
		
		public CloudOld(Sprite sprite, float distance, float startRotation, float x, float y, float beginX, float beginY)
		{
			super(x, y);

			this.sprite = sprite;
			this.distance = distance;
			
			this.beginX = beginX;
			this.beginY = beginY;
			
			this.beginRotation = startRotation;
			
			START_ROTATION = 90F + beginRotation;
			END_ROTATION = 180F + beginRotation;
			ROTATE_SPEED = 2.8F;
			rotation = START_ROTATION;
			
			sprite.setScale(0f);
			sprite.setAlpha(0F);
			
			setShader(ShaderLibrary.GLOW.getProgram());
		}
		
		public float velX = 0;
		public float velY = 0;
		
		public final float ALPHA_INCREASE = 0.02f;
		public float alpha = 0F;
		public float maxAlpha = 1f;
		
		public final float SCALE_INCREASE = 0.015f;
		public float scale = 0F;
		
		@Override
		public void setX(float x)
		{
			lastX = this.x;
			this.x = x;
		}
		
		@Override
		public void setY(float y)
		{
			lastY = this.y;
			this.y = y;
		}
		
		@Override
		public void onDraw()
		{	
			sprite.setAlpha(Math.min(1, alpha));
			sprite.setScale(scale);
			
			sprite.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
			sprite.draw(Game.getGame().batch);
		}
		
		@Override
		public float getWidth()
		{
			return sprite.getWidth();
		}

		@Override
		public float getHeight()
		{
			return sprite.getHeight();
		}
		
		public float START_ROTATION;
		public float END_ROTATION;
		public float ROTATE_SPEED;
		public boolean rotateBackwards;
		
		public float rotation;

		@Override
		public void onUpdate(long tick)
		{
			setX(velX = beginX + (rotateBackwards ? -1 : 1) * (float)Math.sin(Math.toRadians(rotation)) * distance);
			setY(velY = beginY + (rotateBackwards ? -1 : 1) * (float)Math.cos(Math.toRadians(rotation)) * distance);
			
			if(rotateBackwards)
			{
				if(rotation > END_ROTATION)
				{
					rotation -= ROTATE_SPEED;
				}
			}
			else if(rotation < END_ROTATION)
			{
				rotation += ROTATE_SPEED;
			}
			
			if(alpha < maxAlpha)
				alpha += ALPHA_INCREASE;
			
			boolean done = false;
			
			if(rotateBackwards)
			{
				if(rotation <= END_ROTATION)
				{
					done = true;
				}
			}
			else if(rotation >= END_ROTATION)
			{
				done = true;
			}
			
			if(done)
			{
				if(scale <= 0.1f)
					Game.getGame().delete(this);
				else
					scale -= 0.03f;
			}
			else
			{
				if(scale < 0.5f)
					scale += SCALE_INCREASE;
			}
			
			sprite.setRotation((float) (Math.atan2(velY, velX) * (180 / Math.PI) + Math.random() * 20F));
		}
		
		public void setValues(float startRotation, float endRotation, float rotateSpeed, Color color)
		{
			START_ROTATION = startRotation;
			END_ROTATION = endRotation;
			rotation = START_ROTATION;
			rotateBackwards = startRotation > endRotation;
			ROTATE_SPEED = rotateSpeed;
			
			sprite.setColor(color);
		}
	}
	
	/**
	 * Single cloud used in the "cloud..." methods.
	 * Arches towards a specified position from a starting angle.
	 * If the starting angle is 0, it will not curve and go straight to the destination.
	 */
	public static class Cloud extends Bullet
	{
		private IPosition pos;
		
		private float startAngle;
		private float angle;
		
		private static Texture texture;
		
		private boolean clockwise;
		
		static
		{
			texture = new Texture("sprites/enemy/enemy_aura.png");
		}
		
		/**
		 * 
		 * @param x
		 * @param y
		 * @param xDest
		 * @param yDest
		 * @param startAngle - Added to the angle to the destination.
		 */
		public Cloud(float x, float y, IPosition posGetter, float startAngle, boolean clockwise)
		{
			super((Animation)null, x, y);
			
			this.clockwise = clockwise;
			
			HitboxSprite sprite = new HitboxSprite(new Sprite(texture, 0, 0, 46, 46));

			sprite.setRotation((float) (Math.random() * 360f));
			
			setBullet(new Animation(1f, sprite));
			
			this.pos = posGetter;
			
			this.startAngle = startAngle;
			angle = MathUtil.getAngle(x, y, pos.getX(), pos.getY()) - startAngle;
			
			setScale((float) (0.3f + 0.7f * Math.random()));
			
			useDeathAnimation(false);
			
			SpawnAnimationSettings s = getSpawnAnimationSettings();
			
			s.scaleUp();
			s.setAddedScale(0f);
			s.setTime(60);
			s.setAlpha(0.2f);
		}
		
		@Override
		public void checkCollision()
		{
		
		}
		
		@Override
		public boolean doDelete()
		{
			return false;
		}
		
		float scale = 1f;
		float alpha = 1f;
		
		@Override
		public void onDraw()
		{
			if(scale < 1f)
				getCurrentSprite().setScale(scale);
			
			if(alpha < 1f)
				getCurrentSprite().setAlpha(alpha);
			
			super.onDraw();
		}
		
		private float speed = 13f;
		
		public float getSpeed()
		{
			return speed;
		}
		
		/**
		 * Default = 13f
		 * Updating speed will make the whole start angle and start position different for the same effect.
		 * @param speed
		 */
		public void setSpeed(float speed)
		{
			this.speed = speed;
		}

		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
			
			int modifier = clockwise ? 1 : -1;
			@Deprecated
			float dist = MathUtil.getDistance(this, pos);
			
			if(dist < 20)
			{
//				if(alpha > 0.5f)
//					alpha = 0.5f;
				
				setDirectionDegTick(0, 0);
				
				scale -= 0.05f;
				alpha -= 0.05f;
				
				if(alpha <= 0 || scale <= 0)
					game.delete(this);
				
				return;
			}
			
			if(getTicksAlive() > 500)
				game.delete(this);
			
			float angle = MathUtil.getAngle(this, pos) + modifier * startAngle;
			
			double diff = angle - this.angle;
			
			double add = diff;
			
			this.angle += add;
		
			setDirectionDegTick(this.angle, this.speed);
		}
	}
}
