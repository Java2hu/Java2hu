package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.background.HorizontalScrollingBackground;
import java2hu.background.ScrollingBackground;
import java2hu.background.SwirlingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.Scheduler;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Sukuna Shinmyoumaru (DDC)
 * Spell: Mallet - Size doesn't mean anything.
 */
public class Sukuna extends AllStarBoss
{
	public boolean clone = false;
	public Sprite bg;
	public Sprite bge;
	public Setter<BackgroundBossAura> background;
	
	public Sukuna(float maxHealth, float x, float y)
	{
		this(false, maxHealth, x, y);
	}
	
	public Sukuna(boolean clone, float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		this.setAuraColor(new Color(226F/255F, 104F/255F, 74/255F, 1));
		this.setBgmPosition(15f);
		
		this.clone = clone;
		
		String basedir = "enemy/sukuna/";
		
		int chunkHeight = 192;
		int chunkWidth = 192;

		Texture sprite = Loader.texture(Gdx.files.internal(basedir + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);

		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(basedir + "fbs.png")));
		Sprite nameTag = new Sprite(Loader.texture(Gdx.files.internal(basedir + "nametag.png")));

		Animation right = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, 0, 2 * chunkHeight, chunkHeight, chunkWidth, 6F, 1,2,3,4,5,6), ImageSplitter.getAnimationFromSprite(sprite, 0, 2 * chunkHeight, chunkHeight, chunkWidth, 6F, 7,8,9), 4f);
		Animation left = AnimationUtil.copyAnimation(right);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 3F, 
				// Move to left
				12,13,14,15,16,17,18,19,
				// Move back to center
				18,17,16,15,14,13,12,1,
				// Move to right
				21,22,23,24,25,26,27,28,29,
				// Move back
				28,27,26,25,24,23,22,21);
		
		Animation idle = AnimationUtil.copyAnimation(special, 4f);
		idle.setPlayMode(PlayMode.LOOP);
		special.setPlayMode(PlayMode.LOOP);
		
		bg = new Sprite(Loader.texture(Gdx.files.internal(basedir + "bg.png")));
		
		bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
		
		bge = new Sprite(Loader.texture(Gdx.files.internal(basedir + "bge2.png")));
		
		bge.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
		
		Music bgm = null;
		
		if(!clone)
		{
			bgm = Gdx.audio.newMusic(Gdx.files.internal("enemy/sukuna/bgm.mp3"));
			bgm.setVolume(1f * Game.getGame().getMusicModifier());
			
			bgm.setLooping(true);
		}
		
		set(fbs, idle, left, right, special);
		set(nameTag, bgm);
		
		background = new Setter<BackgroundBossAura>()
				{
					@Override
					public void set(final BackgroundBossAura t)
					{
						Game.getGame().spawn(new HorizontalScrollingBackground(bg, 0.2F, false)
						{
							{
								addEffect(new FadeInSprite(new Getter<Sprite>()
								{
									@Override
									public Sprite get()
									{
										return bg;
									}
								}
								, 0, 0.5f, 0.01F));
								
								setZIndex(-6);
								
								setFrameBuffer(t.getBackgroundBuffer());
							}
							
							@Override
							public boolean isPersistant()
							{
								return Sukuna.this.isOnStage();
							}
						});
						
						Game.getGame().spawn(new ScrollingBackground(bge, 0.4f, -0.4f)
						{
							{
								addEffect(new FadeInSprite(new Getter<Sprite>()
								{
									@Override
									public Sprite get()
									{
										return bge;
									}
								}
								, 0, 0.2f, 0.002F));
								
								setFrameBuffer(t.getBackgroundBuffer());
								
								setZIndex(-4);
							}
							
							@Override
							public boolean isPersistant()
							{
								return Sukuna.this.isOnStage();
							}
						});
						
						{
							final Texture texture = Loader.texture(Gdx.files.internal("enemy/sukuna/bge1.png"));

							Sukuna.this.addDisposable(texture);
							
							final Color color = Color.WHITE.cpy();
							color.a = 0;

							SwirlingBackground bge = new SwirlingBackground(texture, true, color)
							{
								float timer = 0;

								{
									setFrameBuffer(t.getBackgroundBuffer());
									
									setZIndex(-5);
								}

								@Override
								public float getTimer()
								{
									return timer;
								}

								@Override
								public void updateTimer()
								{
									if(color.a < 1f)
									{
										color.a += 0.01f;
										color.a = Math.min(1, color.a);
									}
									
									timer += 0.002f;

									timer %= 1;
								}

								@Override
								public boolean isPersistant()
								{
									return Sukuna.this.isOnStage();
								}
							};

							Game.getGame().spawn(bge);
						}
						
					}
				};
	}
	
	@Override
	public void onSpawn()
	{
		if(!clone)
			super.onSpawn();
	}
	
	private SukunaSpell spell;
	
	@Override
	public void onDelete()
	{
		if(spell != null)
			spell.revert(Game.getGame().getPlayer());
		
		super.onDelete();
	}
	
//	public static float[] makeSwirlMesh(float currentSize, Color color)
//	{
//		int size = 600;
//        ArrayList<Float> verticesList = new ArrayList<Float>();
//        
//        float UVSize = 1f;
//        
//        float xDegreeOffset = 45f;
//        float yDegreeOffset = 45f;
//        
//        float xOffset = 0f;
//        float yOffset = 0.1f;
//        
//        float centerX = 0f;
//        float centerY = 0.6f;
//        
//        float increment = 360f / size;
//        float widthModifier = 2f;
//        float heightModifier = 4f;
//        
//        float standardMultiplier = 0.8f;
//        
//        for(float deg = 0; deg <= 360; deg += increment)
//        {
//        	// Left top
//        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * widthModifier) + centerX);
//        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * heightModifier) + centerY);
//        	verticesList.add(0f);
//        	verticesList.add((float) ((Math.sin(Math.toRadians(deg + xDegreeOffset)) * standardMultiplier) + MathUtil.getDifference(UVSize, currentSize)) - xOffset);
//        	verticesList.add((float) ((Math.cos(Math.toRadians(deg + yDegreeOffset)) * standardMultiplier) + MathUtil.getDifference(UVSize, currentSize)) - yOffset);
//        	
//        	verticesList.add(color.toFloatBits());
//        	
//        	// Left Bottom
//        	verticesList.add((float) centerX);
//        	verticesList.add((float) centerY);
//        	verticesList.add(0f);
//        	verticesList.add(currentSize - xOffset);
//        	verticesList.add(currentSize - yOffset);
//        	
//        	verticesList.add(color.toFloatBits());
//        	
//        	// Right top
//        	verticesList.add((float) (Math.sin(Math.toRadians(deg + increment)) * widthModifier) + centerX);
//        	verticesList.add((float) (Math.cos(Math.toRadians(deg + increment)) * heightModifier) + centerY);
//        	verticesList.add(0f);
//        	verticesList.add((float) ((Math.sin(Math.toRadians(deg + xDegreeOffset)) * standardMultiplier) + MathUtil.getDifference(UVSize, currentSize)) - xOffset);
//        	verticesList.add((float) ((Math.cos(Math.toRadians(deg + yDegreeOffset)) * standardMultiplier) + MathUtil.getDifference(UVSize, currentSize)) - yOffset);
//        	
//        	verticesList.add(color.toFloatBits());
//        }
//    	
//    	float[] vertices = new float[verticesList.size()];
//    	
//    	int i = 0;
//    	
//    	for(Float flo : verticesList)
//    	{
//    		vertices[i] = flo;
//    		i++;
//    	}
//		
//		return vertices;
//	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
	}
	
	private float damageModifier = 0F;
	
	@Override
	public float getDamageModifier()
	{
		return damageModifier;
	}
	
	@Override
	public void setDamageModifier(float damageModifier)
	{
		this.damageModifier = damageModifier;
	}
	
	@Override
	public boolean isPersistant()
	{
		return clone ? false : super.isPersistant();
	}
	
	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame stage = Game.getGame();
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		final SaveableObject<BackgroundAura> aura = new SaveableObject<BackgroundAura>();
		
		{
			final Sukuna boss = this;
			final J2hGame g = Game.getGame();

			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					BossUtil.cloudEntrance(boss, 60);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Game.getGame().spawn(boss);
							
							bar.setObject(new CircleHealthBar(boss));
							g.spawn(bar.getObject());
							
							bar.getObject().addSplit(0.4f);
							
							AllStarUtil.introduce(boss);

							boss.setHealth(0.1f);
							boss.healUp();
							aura.setObject(BossUtil.backgroundAura(boss, boss.getBgAuraColor()));
							
							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().startSpellCard(new SukunaNonSpell(boss));
								}
							}, 60);
						}
					}, 90);
				}
			}, 1);

			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !Game.getGame().getStageObjects().contains(boss);
				}
			});
			
			scheme.doWait();

			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !boss.isDead();
				}
			});
			
			scheme.doWait();
			
			boss.setHealth(boss.getMaxHealth());
			bar.getObject().split();
			
			aura.getObject().setMagicSquareEnabled(false);
			
			final SaveableObject<SukunaSpell> spell = new SaveableObject<SukunaSpell>();
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					Game.getGame().clear(ClearType.SPELLS, ClearType.TASKS, ClearType.ALL_OBJECTS);
					
					AllStarUtil.presentSpellCard(boss, "Mallet \"Size doesn't mean anything\"");
					
					boss.background.set(scheme.getBossAura());
					spell.setObject(new SukunaSpell(boss));
					Game.getGame().startSpellCard(spell.getObject());
					
					BossUtil.spellcardCircle(boss, spell.getObject(), scheme.getBossAura());
					
					boss.spell = spell.getObject();
				}
			}, 1);
			
			SchemeUtil.waitForDeath(scheme, boss);
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					Game.getGame().clearCircle(800f, boss, ClearType.ALL);
				}
			}, 1);
			
			scheme.waitTicks(2);
			
			boss.playSpecial(false);
			SchemeUtil.deathAnimation(scheme, boss, boss.getAuraColor());
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					ObjectUtil.deathAnimation(boss);
					
					Game.getGame().delete(boss);
					
					Game.getGame().clear(ClearType.ALL);
				}
			}, 5);
			
			scheme.waitTicks(10); // Prevent concurrency issues.
		}
	}
	
	public static class SukunaNonSpell extends Spellcard
	{
		public SukunaNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(41));
		}
	
		public int offset = 0;
		
		@Override
		public void tick(int tick)
		{	
			final Sukuna boss = (Sukuna)getOwner();
			final J2hGame game = Game.getGame();
			
			if(tick == 0)
			{
				BossUtil.moveTo((Boss) getOwner(), (Game.getGame().getMaxX() - Game.getGame().getMinX()) / 2, Game.getGame().getHeight() - 300, 300);
				
				((AllStarBoss)getOwner()).playSpecial(true);
			}
			
			if(tick % 250 == 0 && tick > 60)
			{
				boss.getPathing().setCurrentPath(new SimpleTouhouBossPath(boss));
			}
			
			if(tick % 180 == 0)
				boss.playSpecial(true);
			
			final int timeOutOne = (int) Duration.seconds(21).toTicks();
			final int timeOutTwo = (int) Duration.seconds(33).toTicks();
			
			if(tick == timeOutOne || tick == timeOutTwo)
				TouhouSounds.Enemy.ACTIVATE_1.play(1F);
			
			if(tick == timeOutOne)
				AllStarUtil.presentSpellCard(boss, "Thousand Needles \"Horrible Death\"");
			
			int speed = 400;
			
			if(tick > timeOutOne + 100)
				speed = 250;
			
			if(tick > timeOutTwo + 100)
				speed = 180;
			
			if(tick % speed == speed - 50)
				BossUtil.cloudSpecial(boss, 50);
			
			if(tick % speed != 0)
				return;
			
			boolean lastPurple = false;
			
			for(int i = 350; i >= 190; i -= 5)
			{
				Bullet bullet = new ReflectingLaserBullet(lastPurple ? ThBulletColor.BLUE : ThBulletColor.PURPLE, boss.getX(), boss.getY(), 4F, i);

				lastPurple = !lastPurple;
				
				bullet.setX(bullet.getX() + bullet.getCurrentSprite().getWidth());
				bullet.setY(bullet.getY() + bullet.getCurrentSprite().getHeight());

				game.spawn(bullet);
			}
			
			if(!Scheduler.isTracked("laser", "laser"))
			{
				TouhouSounds.Enemy.LAZER_1.play(0.4F);
				Scheduler.trackMillis("laser", "laser", (long) 200);
			}
			
			int times = Math.min(2 + tick / 200, 14);
			
			boss.setDamageModifier(times/6F);
			
			for(int i = 0; i < times; i++)
			{
				final int finalI = i;
				
				Runnable run = new Runnable()
				{
					@Override
					public void run()
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.WHITE), boss.getX(), boss.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								if(getTicksAlive() < 60)
									setScale(getCurrentSprite().getScaleX() * 1.02F);

								if(getTicksAlive() == 100)
								{
									setDirectionRadsTick((float) Math.toRadians(MathUtil.getAngle(this, game.getPlayer())), 10F);
									setRotationFromVelocity(-90F);
								}
							}
						};

						TouhouSounds.Enemy.BULLET_2.play(0.5F);

						bullet.useSpawnAnimation(false);
						bullet.setScale(0.5F);
						bullet.setDirectionRadsTick((float) Math.toRadians(finalI % 2 == 0 ? 180 : 360), 1.4F);
						bullet.setRotationFromVelocity(-90F);

						game.spawn(bullet);
					};
				};
				
				game.addTaskGame(run, i * 8);
			}
		}
	}
	
	public static class ReflectingLaserBullet extends Bullet
	{
		public boolean waitUntilOutside = false;
		public boolean didClone = false;

		public ThBulletColor color;
		public float speed;
		public float width = 0.1F;
		public final float width_increase = 0.2F;
		public final float height = 8;
		
		public ReflectingLaserBullet(ThBulletColor color, float x, float y, float speed, float angle)
		{
			super(new ThBullet(ThBulletType.RICE_LARGE, color), x, y);
			
			useSpawnAnimation(false);
			setGlowing();
			
			for(TextureRegion r : getAnimation().getKeyFrames())
			{
				HitboxSprite h = (HitboxSprite) r;
				h.setHitboxScaleOffsetModifier(1F);
				h.setHitboxScaleOffsetModifierX(0.5F);
				h.setHitboxScaleOffsetModifierY(0.8F);
			}
			
			setDirectionRadsTick((float) Math.toRadians(angle), speed);
			setRotationFromVelocity(-90F);
			
			this.speed = speed;
			this.color = color;
			
			final ReflectingLaserBullet laser = this;
			
			Bullet bullet = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, color), x, y)
			{
				{
					useDeathAnimation(false);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					J2hGame g = Game.getGame();
					
					if(!laser.waitUntilOutside)
					{
						int bufferRate = (int) Math.max(laser.getWidth(), laser.getHeight()) + 100;

						boolean delete = g.getMinX() + getX() < g.getMinX() - bufferRate || g.getMinY() + getY() < g.getMinY() - bufferRate || getX() > g.getMaxX() + bufferRate || getY() > g.getMaxY() + bufferRate;

						if(delete)
							g.delete(this);
					}
					
					float rotation = (float) Math.toDegrees(Math.atan2(laser.getVelocityYTick(), laser.getVelocityXTick()));
					rotation = MathUtil.normalizeDegree(rotation);
					
					float modifier = laser.getCurrentSprite().getHeight() * laser.getCurrentSprite().getScaleY();
					modifier *= 0.51f; // Half
					
					float xPos = (float) (laser.getX() - Math.cos(Math.toRadians(rotation)) * modifier);
					float yPos = (float) (laser.getY() - Math.sin(Math.toRadians(rotation)) * modifier);
					
					setX(xPos);
					setY(yPos);
					
					getCurrentSprite().setScale(laser.getCurrentSprite().getScaleX() * 0.4f, laser.getCurrentSprite().getScaleX() * 0.4f);
					
					if(!laser.isOnStage())
						Game.getGame().delete(this);
				}
			};
			
			Color newColor = color.getColor().cpy();
			newColor.mul(2f, 1f, 2f, 0.4f);
			
//			bullet.getCurrentSprite().setAlpha(0.5f);
			bullet.useSpawnAnimation(false);
			
			Game.getGame().spawn(bullet);
		}
		
		@Override
		public void onDelete()
		{
			
		}
		
		@Override
		public void onDraw()
		{
			super.onDraw();
		}
		
		@Override
		public void onUpdateDelta(float delta)
		{
			super.onUpdateDelta(delta);
		}
		
		@Override
		public void onUpdate(long tick)
		{
			J2hGame g = Game.getGame();
			
//			System.out.println(getY());
			
			checkCollision();
			
			Rectangle bound = getCurrentSprite().getBoundingRectangle();
			
			if(didClone)
			{
				int bufferRate = (int) Math.max(getWidth(), getHeight()) + 80;

				boolean delete = g.getMinX() + getX() < g.getMinX() - bufferRate || g.getMinY() + getY() < g.getMinY() - bufferRate || getX() > g.getMaxX() + bufferRate || getY() > g.getMaxY() + bufferRate;

				if(delete)
					g.delete(this);
			}
			
			if(getCurrentSprite().getScaleY() < height)
			{
				float speedMultiplier = 1.02f;
				
				if(waitUntilOutside)
				{
					float fullSpeed = (float) Math.pow(speedMultiplier, (height - 1)/0.1F);
					setVelocityX(getVelocityX() * fullSpeed);
					setVelocityY(getVelocityY() * fullSpeed);
					
					for(TextureRegion r : getAnimation().getKeyFrames())
					{
						HitboxSprite h = (HitboxSprite) r;
						h.setScale(width, height);
					}
				}
				else
				{
					setVelocityX(getVelocityX() * speedMultiplier);
					setVelocityY(getVelocityY() * speedMultiplier);

					for(TextureRegion r : getAnimation().getKeyFrames())
					{
						HitboxSprite h = (HitboxSprite) r;
						h.setScale(width, h.getScaleY() + 0.1f);
					}
				}
			}
			
			if(didClone)
				return;
			
			double xAdd = bound.getWidth()/2;
			double yAdd = bound.getHeight()/2;
			double maxX = getX();
			double maxY = getY();
			
			float entry = (float) Math.toDegrees(Math.atan2(getVelocityYTick(), getVelocityXTick()));
			entry = MathUtil.normalizeDegree(entry);
			
			if(entry > 0 && entry <= 90)
			{
				maxX -= xAdd;
				maxY -= yAdd;
			}
			else if(entry > 90 && entry <= 180)
			{
				maxX += xAdd;
				maxY -= yAdd;
			}
			else if(entry > 180 && entry <= 270)
			{
				maxX += xAdd;
				maxY += yAdd;
			}
			else if(entry > 270 && entry <= 360)
			{
				maxX -= xAdd;
				maxY += yAdd;
			}
			
			boolean hitRight = maxX > Game.getGame().getWidth();
			boolean hitLeft = maxX < 0;
			boolean hitTop = maxY > Game.getGame().getHeight();
			boolean hitBot = maxY < 0;
			
			if(hitRight || hitLeft || hitTop || hitBot)
			{
				if(waitUntilOutside)
					return;
				
				if(hitBot)
				{
					didClone = true;
					return;
				}
				
				float newDirection = getRotationDeg(); // Reflecting: angle in = angle out

				if(hitRight)
				{
					float center = 90F;
					
					newDirection = entry - center; // Angle in
					newDirection = center - newDirection; // Angle out
					
					xAdd = xAdd + bound.getWidth() / 2;
					yAdd = yAdd - bound.getHeight() / 2;
				}
				else if(hitTop)
				{
					float center = 180F;
					
					newDirection = entry - center; // Angle in
					newDirection = center - newDirection; // Angle out
					
					xAdd = xAdd - bound.getWidth() / 2;
					yAdd = yAdd + bound.getHeight() / 2;
				}
				else if(hitLeft)
				{
					float center = 90F;
					
					newDirection = entry - center; // Angle in
					newDirection = center - newDirection; // Angle out
					
					xAdd = xAdd - bound.getWidth() - bound.getWidth() / 2;
					yAdd = yAdd - bound.getHeight() + bound.getHeight() / 2;
				}
//				else if(hitBot)
//				{
//					float center = 180F;
//					
//					newDirection = entry - center; // Angle in
//					newDirection = center - newDirection; // Angle out
//					
//					xAdd = xAdd - bound.getWidth() + (bound.getWidth() / 2);
//					yAdd = yAdd - bound.getHeight() - (bound.getHeight() / 2);
//				}
				
				if(!Scheduler.isTracked("laser", "laser"))
				{
					TouhouSounds.Enemy.LAZER_1.play(0.4F);
					Scheduler.trackMillis("laser", "laser", (long) 200);
				}
				
				final ReflectingLaserBullet laser = new ReflectingLaserBullet(color, (float)(getX() + xAdd), (float)(getY() + yAdd), speed, newDirection);

				laser.waitUntilOutside = true;
				laser.width = width + width_increase;

				Game.getGame().spawn(laser);
				
				float rotation = newDirection;
				rotation = MathUtil.normalizeDegree(rotation);
				
				float modifierY = getCurrentSprite().getHeight() * getCurrentSprite().getScaleY();
				
				float modifierX = modifierY * 0.67f;
				
				modifierY *= 0.53f;
				
				float xPos = (float) (getX() - Math.cos(Math.toRadians(rotation)) * modifierX);				
				float yPos = (float) (getY() - Math.sin(Math.toRadians(rotation)) * modifierY);
				
				double minX = xPos;
				double minY = yPos;
				
				final float scale = 0.6f;
				
				Bullet orb = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, color), (float)(hitRight ?  Game.getGame().getWidth() : hitLeft ? 0 : minX), (float)(hitTop ?  Game.getGame().getHeight() : minY))
				{
					{
						useDeathAnimation(false);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						if(tick % 10 == 5)
							getCurrentSprite().setScale(laser.getCurrentSprite().getScaleX() * scale);		
						
						if(tick % 10 == 0)
							getCurrentSprite().setScale(laser.getCurrentSprite().getScaleX() * (scale - 0.1f));
						
						if(getTicksAlive() > 30)
							Game.getGame().delete(this);
					}
					
					{
						setZIndex(laser.getZIndex() + 1);
					}
				};
				
				orb.getCurrentSprite().setScale(laser.getCurrentSprite().getScaleX() * scale);
				orb.useSpawnAnimation(false);
				
				Game.getGame().spawn(orb);

				didClone = true;
			}
			else if(waitUntilOutside)
				waitUntilOutside = false;
		}
	}
	
	public static class SukunaSpell extends Spellcard
	{
		private Sprite bowlFront;
		private Sprite bowlBack;
		
		public SukunaSpell(StageObject owner)
		{
			super(owner);
			
			final Sukuna sukuna = (Sukuna)owner;
			final J2hGame game = Game.getGame();
			
			setSpellcardTime(Duration.seconds(45));
			
			BossUtil.backgroundAura(sukuna, sukuna.getBgAuraColor());
			
			sukuna.setDamageModifier(0.5F);
			
			Texture texture = Loader.texture(Gdx.files.internal("enemy/sukuna/anm.png")); // Just get her animations texture from another.
			
			sukuna.addDisposable(texture);
			
			bowlFront = new Sprite(texture, 0, 6 * 192, 512, 310);
			bowlBack = new Sprite(texture, 512, 6 * 192, 512, 310);
			
			final float MAX_ROT = 10;
			final float START_ROT = 0;
			final float MIN_ROT = -10;
			final float ROT_SPEED = 0.3F;
			final float ALPHA_START = 0F;
			final float ALPHA_SPEED = 0.01F;
			final float SCALE_START = 0F;
			final float SCALE_SPEED = 0.01F;
			
			bowlBack.setScale(SCALE_START);
			bowlFront.setScale(SCALE_START);
			
			DrawObject drawBack = new DrawObject()
			{
				float maxRot = MAX_ROT;
				float rot = START_ROT;
				float minRot = MIN_ROT;
				boolean up = true;
				
				float alpha = ALPHA_START;
				
				@Override
				public void onDraw()
				{
					bowlBack.setRotation(rot);
					
					bowlBack.setPosition(sukuna.getX() - bowlFront.getWidth() / 2, sukuna.getY() - bowlFront.getHeight() / 2 - 105);

					bowlBack.draw(game.batch);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(up)
					{
						rot += ROT_SPEED * Math.max(MathUtil.getDifference(rot, maxRot) / 10, 0.2f) * Math.min(Math.max(MathUtil.getDifference(rot, minRot) / 10, 0.2f), 1f);
						
						if(rot > maxRot)
							up = false;
					}
					else
					{
						rot -= ROT_SPEED * Math.max(MathUtil.getDifference(rot, minRot) / 10, 0.2f) *  Math.min(Math.max(MathUtil.getDifference(rot, maxRot) / 10, 0.2f), 1f);
						
						if(rot < minRot)
							up = true;
					}
					
					if(bowlBack.getScaleX() < 1)
						bowlBack.setScale(bowlBack.getScaleX() + SCALE_SPEED);
					
					if(alpha < 1)
					{
						bowlBack.setAlpha(alpha);
					
						alpha += ALPHA_SPEED;
					}
				}
			};
			
			drawBack.setZIndex(99);
			
			// Sukuna's Z-index = 100
			
			Color color = Color.BLACK.cpy();
			color.a = 0.3f;
			
			final Texture dummy = ImageUtil.makeDummyTexture(color, 1, 1);
			
			DrawObject drawFront = new DrawObject()
			{
				float maxRot = MAX_ROT;
				float rot = START_ROT;
				float minRot = MIN_ROT;
				boolean up = true;
				
				float alpha = ALPHA_START;
				
				@Override
				public void onDraw()
				{
					bowlFront.setRotation(rot);
					bowlFront.draw(game.batch);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(up)
					{
						rot += ROT_SPEED * Math.max(MathUtil.getDifference(rot, maxRot) / 10, 0.2f) * Math.min(Math.max(MathUtil.getDifference(rot, minRot) / 10, 0.2f), 1f);
						
						if(rot > maxRot)
							up = false;
					}
					else
					{
						rot -= ROT_SPEED * Math.max(MathUtil.getDifference(rot, minRot) / 10, 0.2f) *  Math.min(Math.max(MathUtil.getDifference(rot, maxRot) / 10, 0.2f), 1f);
						
						if(rot < minRot)
							up = true;
					}
					
					bowlFront.setPosition(sukuna.getX() - bowlFront.getWidth() / 2, sukuna.getY() - bowlFront.getHeight() / 2 - 100);
					
					if(bowlFront.getScaleX() < 1)
						bowlFront.setScale(bowlFront.getScaleX() + SCALE_SPEED);
					
					if(alpha < 1)
					{
						bowlFront.setAlpha(alpha);
					
						alpha += ALPHA_SPEED;
					}
					
					if(game.getTick() % 20 == 0)
						for(int i = -120; i < 120; i += 60 * Math.random())
						{
							final int finalI = i;
							
							DrawObject particle = new DrawObject()
							{
								private float yStart = (float) (sukuna.getY() - 170 - Math.random() * 80);
								private float xStart = sukuna.getX() + finalI;
								private Sprite sprite = new Sprite(dummy);
								
								@Override
								public void onDraw()
								{
									sprite.draw(game.batch);
								}
								
								@Override
								public void onUpdate(long tick)
								{
									sprite.setPosition(xStart, yStart);
									sprite.setSize(30, 30);
									sprite.rotate((float) Math.random() * 5);
									yStart -= 4F;

									sprite.setAlpha(Math.max(0, sprite.getColor().a - 0.02f));
									
									if(sprite.getColor().a < 0)
										game.delete(this);
								};
							};
							
							particle.setZIndex(102);
							
							game.spawn(particle);
						}
				}
			};
			
			drawFront.setZIndex(101);
			
			originalHitboxScaleX = game.getPlayer().getHitbox().getScaleX();
			originalHitboxScaleY = game.getPlayer().getHitbox().getScaleY();
			
			game.spawn(drawFront);
			game.spawn(drawBack);
		}
		
		private Sukuna clone1;
		private Sukuna clone2;
		private float bulletScale = 2F;
		private float originalHitboxScaleX = 1F;
		private float originalHitboxScaleY = 1F;
		private ThBulletColor[] rainbow = new ThBulletColor[] { ThBulletColor.RED, ThBulletColor.ORANGE, ThBulletColor.YELLOW, ThBulletColor.GREEN, ThBulletColor.BLUE, ThBulletColor.PURPLE }; 
		private int rainbowTimer = 0;
		
		@Override
		public void tick(int tick)
		{
			final Sukuna boss = (Sukuna)getOwner();
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			
			if(tick == 0)
			{
				TouhouSounds.Player.GROW.play(1F);
				BossUtil.moveTo((Boss) getOwner(), (Game.getGame().getMaxX() - Game.getGame().getMinX()) / 2, Game.getGame().getHeight() - 300, 300);
			}
			
			if(tick < 60)
				return;
			
			if(clone1 == null)
			{
				TouhouSounds.Enemy.ACTIVATE_3.play(1f);
				
				clone1 = new Sukuna(true, 0f, boss.getX() - 400, boss.getY() - 100);
				
				for(TextureRegion r : clone1.idle.getKeyFrames())
					((HitboxSprite)r).setAlpha(0.5F);

				game.spawn(clone1);
			}
			
			if(clone2 == null)
			{
				TouhouSounds.Enemy.ACTIVATE_3.play(1f);
				
				clone2 = new Sukuna(true, 0f, boss.getX() + 400, boss.getY() - 100);
				
				for(TextureRegion r : clone2.idle.getKeyFrames())
					((HitboxSprite)r).setAlpha(0.5F);
				
				game.spawn(clone2);
			}
			
			if(tick % 150 == 0 && tick > 60)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 50, (int)(getGame().getMaxX() / 2) + 50, Game.getGame().getHeight() - 200, Game.getGame().getHeight() - 300, 800);
			
			if(tick % 50 == 0)
			{
				int[] timers = new int[] { 10,70, 110,170, 190,250, 290,350 };
				
				for(int i = 0; i < timers.length; i += 2)
					for(int rotation = timers[i]; rotation < timers[i + 1]; rotation += (2.5 - bulletScale) * 20)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.KUNAI, rainbow[rainbowTimer]), boss.getX(), boss.getY());
						bullet.getSpawnAnimationSettings().setAddedScale(5f);
						bullet.getSpawnAnimationSettings().setTime(30f);
						bullet.setDirectionRadsTick((float) Math.toRadians(rotation), 2f);
						bullet.setRotationFromVelocity(-90f);
						
						bullet.setGlowing();
						game.spawn(bullet);
					}
				
				TouhouSounds.Enemy.BULLET_2.play(0.5F);
				
				rainbowTimer++;
				
				if(rainbowTimer >= rainbow.length)
					rainbowTimer = 0;
			}
			
			if(tick % 80 == 0)
				if(bulletScale <= 1)
				{
					TouhouSounds.Enemy.ACTIVATE_3.play(1f);
					
					int offset = (int) (Math.random() * 20);
					
					for(int i = offset; i < 360 + offset; i += 15)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, ThBulletColor.GREEN), boss.getX(), boss.getY());
						bullet.setDirectionRadsTick((float) Math.toRadians(i), 2f);
						bullet.setScale(Math.max(bulletScale - 0.3f, 0.5f));
						bullet.setZIndex(bullet.getZIndex() + i);
						game.spawn(bullet);
					}
				}
			
			if(tick % 100 == 0)
			{
				Object[] settings = new Object[] { 
						boss, ThBulletColor.BLUE,
						clone1, ThBulletColor.PURPLE,
						clone2, ThBulletColor.RED,
				};
				
				for(int i = 0; i < settings.length; i += 2)
				{
					Boss target = (Boss) settings[i];
					
					int offset = (int) MathUtil.getAngle(target, game.getPlayer());
					
					for(int rotation = offset; rotation < 360 + offset; rotation += 20)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, (ThBulletColor) settings[i + 1]), target.getX(), target.getY());
						
						bullet.setZIndex(bullet.getZIndex() + rotation);
						bullet.setDirectionRadsTick((float) Math.toRadians(rotation), 4f);
						bullet.setScale(bulletScale);
						bullet.getSpawnAnimationSettings().setAlpha(-1f);
						bullet.getSpawnAnimationSettings().setAddedScale(3f);
						bullet.getSpawnAnimationSettings().setTime(30f);
						
						game.spawn(bullet);
					}
				}
				
				TouhouSounds.Enemy.RELEASE_1.play(0.8F);
				
				float scale = 5 - bulletScale;
				
				setScale(player, scale);
				setHitboxScale(player, scale * scale, scale * scale);
				boss.setDamageModifier(Math.max((1f - bulletScale) * 1.5f, 0));
				
				if(bulletScale > 0.5)
					bulletScale -= 0.1f;
			}
		}
		
		public void revert(Player player)
		{
			setScale(player, 1f);
			setHitboxScale(player, originalHitboxScaleX, originalHitboxScaleY);
		}
		
		public void setScale(Player player, float scale)
		{
			for(TextureRegion r : player.idle.getKeyFrames())
				((HitboxSprite)r).setScale(scale);
			
			for(TextureRegion r : player.left.getKeyFrames())
				((HitboxSprite)r).setScale(scale);
			
			for(TextureRegion r : player.right.getKeyFrames())
				((HitboxSprite)r).setScale(scale);
			
			for(TextureRegion r : player.hitboxAnimation.getKeyFrames())
				((Sprite)r).setScale(scale);
		}
		
		public void setHitboxScale(Player player, float scaleX, float scaleY)
		{
			player.getHitbox().setScale(scaleX, scaleY);
		}
	}
}

