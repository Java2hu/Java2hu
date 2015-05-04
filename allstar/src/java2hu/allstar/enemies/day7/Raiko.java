package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.SwirlingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.plugin.sprites.ScalingSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Getter;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.MeshUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * Raiko Horikawa (DDC)
 * Spell: "Shockwave - \"Percussion Orchestra\""
 */
public class Raiko extends AllStarBoss
{
	public static Raiko newInstance(float x, float y)
	{
		int chunkHeight = 96 * 2;
		int chunkWidth = 80 * 2;

		Texture sprite = Loader.texture(Gdx.files.internal("enemy/raiko/anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/fbs.png")));
		Sprite nameTag = new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation right = ImageSplitter.getAnimationFromSprite(sprite, 0, 96 * 2, chunkHeight, chunkWidth, 12F, 1,2,3,4,5);
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, 0, 2 * 96 * 2, chunkHeight, chunkWidth, 12F, 1,2,3,4,5);
		
		Animation special = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * 96 * 2, chunkHeight, chunkWidth, 10F, 1,2,3,4,5);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/bg.png")));
		bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal("enemy/raiko/bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(23.96f);
		bgm.setLooping(true);
		
		final Raiko raiko = new Raiko(nameTag, fbs, bg, idle, left, right, special, bgm, x, y);
		
		return raiko;
	}
	
	public Sprite bg;

	public Raiko(Sprite nametag, Sprite fullBodySprite, final Sprite bg, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(100, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		this.setColor(new Color(196F/255F, 71F/255F, 71F/255F, 1));
		
		this.bg = bg;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
	}
	
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
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame stage = Game.getGame();
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		{
			final Raiko raiko = this;
			final J2hGame g = Game.getGame();

			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					BossUtil.cloudEntrance(raiko, Color.WHITE, Color.RED, 60);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							bar.setObject(new CircleHealthBar(raiko));
							g.spawn(bar.getObject());
							
							bar.getObject().addSplit(0.7f);
							
							Game.getGame().spawn(raiko);
							
							AllStarUtil.introduce(raiko);
							
							raiko.setHealth(0.1f);
							raiko.healUp();
							BossUtil.backgroundAura(raiko);

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().startSpellCard(new RaikoNonSpell(raiko));
								}
							}, 60);
						}
					}, 60);
				}
			}, 1);

			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !Game.getGame().getStageObjects().contains(raiko);
				}
			});
			
			scheme.doWait();

			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !raiko.isDead();
				}
			});
			
			scheme.doWait();

			raiko.setHealth(raiko.getMaxHealth());
			bar.getObject().split();
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					game.clear(ClearType.ALL);
					
					AllStarUtil.presentSpellCard(raiko, "Shockwave - \"Percussion Orchestra\"");
					Game.getGame().startSpellCard(new RaikoSpell(raiko));
				}
			}, 1);
			
			
			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !raiko.isDead();
				}
			});
			
			scheme.doWait();
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					Game.getGame().delete(raiko);
					
					BossUtil.mapleExplosion(raiko.getX(), raiko.getY());
					
					Game.getGame().clear(ClearType.ALL);
				}
			}, 1);
			
			scheme.waitTicks(5); // Prevent concurrency issues.
		}
	}
	
	private Sprite shockwave = new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/anm.png")), 0, 1024 - 256, 256, 256);
	
	{
		addDisposable(shockwave);
	}
	
	public void shockWave(final StageObject boss)
	{
		final Sprite shockwaveSprite = new Sprite(this.shockwave);
		
		DrawObject shockwave = new DrawObject()
		{
			{
				setZIndex(10000);
				addEffect(new ScalingSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return shockwaveSprite;
					}
				}, 0F, 1F, 0.05F));
			}
			
			@Override
			public void onUpdate(long tick)
			{
				if(getTicksAlive() > 20)
					Game.getGame().delete(this);
				
				super.onUpdate(tick);
			}

			@Override
			public void onDraw()
			{
				shockwaveSprite.setX(boss.getX() - shockwaveSprite.getWidth() / 2);
				shockwaveSprite.setY(boss.getY() - shockwaveSprite.getHeight() / 2);
				shockwaveSprite.draw(Game.getGame().batch);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		};
		
		shockwave.setZIndex(10000);
		
		Game.getGame().spawn(shockwave);
		
		TouhouSounds.Enemy.EXPLOSION_1.play(2F);
	}
	
	public static class RaikoNonSpell extends Spellcard
	{
		public RaikoNonSpell(StageObject owner)
		{
			super(owner);
		}
		
		public int offset = 0;
		
		@Override
		public void tick(int tick)
		{	
			final Raiko boss = (Raiko)getOwner();
			
			if(tick == 0)
			{
				boss.setDamageModifier(0F);
				
				BossUtil.moveTo((Boss) getOwner(), (Game.getGame().getMaxX() - Game.getGame().getMinX()) / 2, Game.getGame().getHeight() - 300, 300);
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						boss.shockWave(getOwner());
					}
				}, 30);
				
				((AllStarBoss)getOwner()).playSpecial(true);
			}
			
			if(tick < 40)
				return;
			
			if(tick == 40)
				boss.setDamageModifier(1F);
			
			if(tick % 10 == 0)
				TouhouSounds.Enemy.BULLET_3.play(0.3F);
			
			if(tick % 20 == 0)
				TouhouSounds.Notes.NOTE_2.play(0.7F);
			
			if(tick % 200 == 0)
			{
				boss.shockWave(getOwner());
				
				final Rectangle movableArea = Game.getGame().getBoundary();
				movableArea.setHeight(100);
				movableArea.setWidth(300);
				
				movableArea.setY(Game.getGame().getMaxY() - movableArea.getHeight() - 50);
				movableArea.setX(Game.getGame().getMaxX() - 400 - (Game.getGame().getMinX() + 400));

				BossUtil.moveAroundRandomly((Boss)getOwner(), movableArea, 2000);
			}
			
			float step = (float) Math.min(getSpellcardTick() * getSpellcardTick() * 0.000001, 5.4f);
			
			if(tick % (15 + (int)(step * 1.8f)) == 0)
			{
				offset += 4;
				offset = offset % 60;
				
				int nextOffset = (offset + 4) % 60;
				
				if(nextOffset == 48 || nextOffset == 20)
					BossUtil.cloudSpecial(getOwner(), 20);
				
				int offset = this.offset;
				
				if(offset > 30)
					offset = -offset;
				
				for(int i = 0; i <= 360; i += 20)
					try
					{
						Bullet rain = new Bullet(new ThBullet(ThBulletType.NOTE_QUARTER_REST, ThBulletColor.BLUE), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i), 4F);
						rain.setRotationFromVelocity(90F);
						
						Game.getGame().spawn(rain);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
			
				for(int i = -offset; i <= 360 -offset; i += 12)
					try
					{
						float speed = 10f;
						
						if(step > 0.5f)
						{
							speed -= step * 3f;
							speed = Math.max(1.5f, speed);
						}
						
						Bullet rain = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.CYAN), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i), speed);
						rain.setRotationDeg(i + 90F);

						Game.getGame().spawn(rain);
						
						rain = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.CYAN), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i-step), speed);
						rain.setRotationDeg(i-step + 105F);
						
						final Bullet finalRainMinus = rain;
						
						rain = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.CYAN), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i+step), speed);
						rain.setRotationDeg(i+step + 75F);
						
						final Bullet finalRainPlus = rain;
						
						rain = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.CYAN), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i-2 * step), speed);
						rain.setRotationDeg(i-2 * step + 120F);
						
						final Bullet finalRainMinusMinus = rain;
						
						rain = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.CYAN), getOwner().getX(), getOwner().getY());
						rain.setDirectionRadsTick((float) Math.toRadians(i+2 * step), speed);
						rain.setRotationDeg(i+2 * step + 60F);
						
						final Bullet finalRainPlusPlus = rain;
						
						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								Game.getGame().spawn(finalRainMinus);
								Game.getGame().spawn(finalRainPlus);
								
								Game.getGame().addTaskGame(new Runnable()
								{
									@Override
									public void run()
									{
										Game.getGame().spawn(finalRainMinusMinus);
										Game.getGame().spawn(finalRainPlusPlus);
									}
								}, 3);
							}
						}, 3);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		}
	}
	
	public static class RaikoSpell extends Spellcard
	{
		private SwirlingBackground bge;
		private HitboxSprite drum;
		private HitboxSprite drumSkull;
		private HitboxSprite drumSkullRed;
		
		public RaikoSpell(final StageObject owner)
		{
			super(owner);
			
			final Raiko raiko = (Raiko)owner;
			
			raiko.setDamageModifier(0.3f);

			drum = new HitboxSprite(new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/drum.png"))));
			drumSkull = new HitboxSprite(new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/drumSkull.png"))));
			drumSkullRed = new HitboxSprite(new Sprite(Loader.texture(Gdx.files.internal("enemy/raiko/drumSkullRed.png"))));

			drum.setHitbox(HitboxUtil.loadHitbox(Gdx.files.internal("enemy/raiko/barrel.vertices")));
			drum.setHitboxScaleOffsetModifier(0.5f);
			
			drumSkull.setHitbox(HitboxUtil.loadHitbox(Gdx.files.internal("enemy/raiko/barrel.vertices")));
			drumSkull.setHitboxScaleOffsetModifier(0.5f);
			
			drumSkullRed.setHitbox(HitboxUtil.loadHitbox(Gdx.files.internal("enemy/raiko/barrel.vertices")));
			drumSkullRed.setHitboxScaleOffsetModifier(0.5f);
			
			Game.getGame().spawn(new DrawObject()
			{
				{
					addEffect(new FadeInSprite(new Getter<Sprite>()
					{
						@Override
						public Sprite get()
						{
							return raiko.bg;
						}
					}
					, 0, 1f, 0.01F));
					setZIndex(-2);
				}
				
				@Override
				public void onDraw()
				{
					raiko.bg.draw(Game.getGame().batch);
				}
				
				@Override
				public boolean isPersistant()
				{
					return raiko.isOnStage();
				}
			});
			
			final Texture texture = Loader.texture(Gdx.files.internal("enemy/raiko/bge.png"));
			
			owner.addDisposable(texture);
			
			final Color color = Color.WHITE.cpy();
			
			color.a = 0f;
			
			SwirlingBackground bge = new SwirlingBackground(texture, true, color)
			{
				float timer = 0;
				
				{
					setZIndex(-1);
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
					
					timer -= 0.002f;

					timer %= 1;
				}
				
				@Override
				public boolean isPersistant()
				{
					return owner.isOnStage();
				}
				
				@Override
				public void applyGl20Changes()
				{
					Gdx.graphics.getGL20().glBlendEquation(GL20.GL_FUNC_ADD);
//					Gdx.graphics.getGL20().glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
				}
				
				@Override
				public void removeGl20Changes()
				{
					Gdx.graphics.getGL20().glBlendEquationSeparate(GL20.GL_FUNC_ADD, GL20.GL_FUNC_ADD);
				}
			};
			
			this.bge = bge;
		}

		private float activationDistance = 300F;

		@Override
		public void tick(int tick)
		{
			final Raiko boss = (Raiko)getOwner();
			
			if(tick == 0)
			{
				((AllStarBoss)getOwner()).playSpecial(true);
				
				Game.getGame().spawn(new DrawObject()
				{
					Mesh mesh;
					
					@Override
					public void onDraw()
					{
						Player player = Game.getGame().getPlayer();
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(player.getX(), player.getY(), 100, activationDistance, activationDistance + 1f, Color.GRAY));
						
						Game.getGame().batch.end();
						
						MeshUtil.startShader();
						
						MeshUtil.renderMesh(mesh);
						
						MeshUtil.endShader();
						
						Game.getGame().batch.begin();
					}
				});
				
				Game.getGame().spawn(bge);
			}
			
			float frequency = 400f;		
			
			if(tick % frequency == 80 && tick > 60)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 450, Game.getGame().getHeight() - 200, 900);
			
			if(tick % frequency == frequency - 100)
				BossUtil.cloudSpecial(getOwner(), Color.WHITE, Color.RED, 50);
			
			if(tick % frequency == 0)
			{
				TouhouSounds.Enemy.NOISE.play();
				
				boss.shockWave(getOwner());
				
				int increase = 36;
				float degree = 100;
				float dividant = 15f;
				
				int time = (int) (tick / frequency);
			
				if(time == 1)
				{
					increase = 30;
					degree = 140f;
				}
				if(time == 2)
				{
					increase = 20;
					degree = 220f;
					dividant = 7f;
				}
				if(time == 3)
				{
					increase = 16;
					degree = 300f;
				}
				if(time >= 4)
				{
					increase = 16;
					degree = 460f;
					dividant = 2f;
				}
				
				boolean right = tick % (4*frequency) < 2*frequency;
				
				int begin = 100 + (right ? 0 : 140);
				
				for(int i = begin; i <= degree + (right ? 0 : 140); i += increase)
				{
					final int finalI = i;
					
					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								Bullet rain = new Bullet(new Animation(1, new HitboxSprite(drum)), getOwner().getX(), getOwner().getY())
								{
									boolean sentToPlayer = false;
									
									@Override
									public void onUpdate(long tick)
									{
										super.onUpdate(tick);
										
//										setBullet(finalSkullRed);
										
										if(!sentToPlayer)
										{
											int shrink = 40;

											boolean closeToBoundary = !Game.getGame().inBoundary(getX() - shrink, getY() - shrink);

											if(!closeToBoundary)
												closeToBoundary = !Game.getGame().inBoundary(getX() + shrink, getY() + shrink);

											if(!closeToBoundary)
												closeToBoundary = !Game.getGame().inBoundary(getX() + shrink, getY() - shrink);

											if(!closeToBoundary)
												closeToBoundary = !Game.getGame().inBoundary(getX() - shrink, getY() + shrink);

											if(closeToBoundary)
											{
												sentToPlayer = true;

												setBullet(new Animation(1, new HitboxSprite(drumSkull)));
												
												float degree = (float) Math.toRadians(MathUtil.getAngle(this, Game.getGame().getPlayer()));
												
												setDirectionRadsTick(degree, 8F);
												setRotationRads(degree);

												final Bullet bullet = this;
												
												boss.shockWave(this);
											}
										}
										else
										{
											if(tick % 10 != 0)
												return;
											
											Player player = Game.getGame().getPlayer();
											
											float distance = MathUtil.getDistance(this.getX(), this.getY(), player.getX(), player.getY());
											
											if(distance < activationDistance)
											{
												setBullet(new Animation(1, new HitboxSprite(drumSkullRed)));
												setRotationFromVelocity(0f);
												return;
											}
											else
											{
												setBullet(new Animation(1, new HitboxSprite(drumSkull)));
												setRotationFromVelocity(0f);
											}
											
											boolean insideBoundary = Game.getGame().inBoundary(getX(), getY());
											
											if(!insideBoundary)
												return;
											
											if(!Scheduler.isTracked("spawnBulletSound", null))
											{
												Scheduler.trackMillis("spawnBulletSound", null, (long) 200);

												TouhouSounds.Enemy.BULLET_3.play(0.3F);
											}
											
											try
											{
												Bullet seeker = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), (float) (getX() + (Math.random() > 0.5 ? -(Math.random() * 10) : Math.random() * 10)), (float) (getY()  + (Math.random() > 0.5 ? -(Math.random() * 10) : Math.random() * 10)))
												{
													boolean set = false;
													
													@Override
													public void onUpdate(long tick)
													{
														super.onUpdate(tick);
														
														if(!set)
														{
															Player player = Game.getGame().getPlayer();
															
															float degree = MathUtil.getAngle(this, player);
															
															setDirectionRadsTick((float) Math.toRadians(degree), 4F);
															
															float distance = MathUtil.getDistance(this.getX(), this.getY(), player.getX(), player.getY());
															
															if(distance < activationDistance)
															{
																set = true;
																setBullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.RED));
																setRotationDeg(MathUtil.normalizeDegree(degree - 90F - 180F));
															}
														}
													};
												};
												
												Game.getGame().spawn(seeker);
											}
											catch (Exception e)
											{
												e.printStackTrace();
											}
										}
									}
								};
								
								rain.setDirectionRadsTick((float) Math.toRadians(finalI), 4F);
								rain.setRotationDeg(finalI);
								rain.getSpawnAnimationSettings().setAddedScale(3f);
								rain.getSpawnAnimationSettings().setTime(20f);
								
								Game.getGame().spawn(rain);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}, (int) ((i - begin) / dividant));
				}
			}
		}
	}
}

