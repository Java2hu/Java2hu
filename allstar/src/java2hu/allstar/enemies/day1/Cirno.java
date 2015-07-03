package java2hu.allstar.enemies.day1;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.RNG;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.AllStarStageScheme.SpawnBossTask;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.background.VerticalScrollingBackground;
import java2hu.gameflow.GameFlowScheme.ReturnSyncTask;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.PositionObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.Plugin;
import java2hu.spellcard.PhaseSpellcard;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Cirno extends AllStarBoss
{
	public final static String FULL_NAME = "Cirno";
	public final static String DATA_NAME = "cirno";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Ice Sign \"Frozen Wall\"";
	
	public Setter<BackgroundBossAura> backgroundSpawner;
	
	public Cirno(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 5,6,7), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 15F, 8), 10f);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : left.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 9,10,11,10,9);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		
		setAuraColor(new Color(0 / 255f, 102 / 255f, 187 / 255f, 1.0f));
		setBgAuraColor(AllStarUtil.from255RGB(40, 161, 220));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
			{
				final Sprite bg = new Sprite(Loader.texture(FOLDER.child("bg.png")));
				bg.setRegion(0f, 0f, 3f, 3f);
				bg.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
				
				game.spawn(new VerticalScrollingBackground(bg, 0.7f, false)
				{
					{
						setFrameBuffer(t.getBackgroundBuffer());
						setZIndex(-100);
					}
				});
			}
		};
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Cirno boss = this;
		
		RNG.setSeed(10);
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, boss.getAuraColor(), boss.getBgAuraColor(), 60);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						bar.setObject(new CircleHealthBar(boss));
						
						g.spawn(boss);
						g.spawn(bar.getObject());
						
						bar.getObject().addSplit(0.8f);
						
						AllStarUtil.introduce(boss);
						
						boss.healUp();
						BossUtil.addBossEffects(boss, boss.getAuraColor(), boss.getBgAuraColor());
						
						Game.getGame().startSpellCard(new NonSpell(boss));
					}
				}, 60);
			}
		}, 1);

		scheme.wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss.isOnStage();
			}
		});

		SchemeUtil.waitForDeath(scheme, boss);
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		Spell card = scheme.runAndReturnSync(new ReturnSyncTask<Spell>()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL);
				
				backgroundSpawner.set(scheme.getBossAura());
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				final Spell card = new Spell(boss);
				
				Game.getGame().startSpellCard(card);

				BossUtil.spellcardCircle(boss, card, scheme.getBossAura());
				
				setResult(card);
			}
		});
		
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
		
		if(card.isTimedOut())
		{
			scheme.waitTicks(60);
			
			ObjectUtil.deathAnimation(new PositionObject(game.getMinX(), game.getMinY()));
			ObjectUtil.deathAnimation(new PositionObject(game.getMinX(), game.getMaxY()));
			ObjectUtil.deathAnimation(new PositionObject(game.getMaxX(), game.getMaxY()));
			ObjectUtil.deathAnimation(new PositionObject(game.getMaxX(), game.getMinY()));
			
			scheme.waitTicks(80);
			
			scheme.getBossAura().clearAuras();
			
			final Cirno newBoss = scheme.runAndReturnSync(new SpawnBossTask<Cirno>()
			{
				@Override
				public Cirno get()
				{
					return new Cirno(100, game.getCenterX(), game.getCenterY());
				}
			});
			
			game.spawn(newBoss);
			
			scheme.runSync(new Runnable()
			{
				@Override
				public void run()
				{
					AllStarUtil.presentSpellCard(newBoss, "Nobody ignores me, I'm the strongest after all!");
					
					final Spellcard cardTwo = null; // Commence very strong timeout spell

					BossUtil.spellcardCircle(newBoss, cardTwo, scheme.getBossAura());
					
					BossUtil.chargeExplosion(newBoss, Color.BLUE);
				}
			});
			
			SchemeUtil.waitForDeath(scheme, newBoss);
		}
	}
	
	public static class NonSpell extends PhaseSpellcard<Cirno>
	{	
		public NonSpell(Cirno owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(25));
			
			final Player player = game.getPlayer();
			
//			addPhase(new Phase<Cirno>(40)
//					{
//						@Override
//						public void tick(final int tick, final J2hGame game, final Cirno boss)
//						{
//							if(tick % 30 != 0)
//								return;
//							
//							for(float angle = 200; angle <= 340; angle += 1)
//							{
//								final float finalAngle = angle;
//								
//								Runnable run = new Runnable()
//								{
//									@Override
//									public void run()
//									{
//										float rad = (float) Math.toRadians(finalAngle);
//										
//										float distance = 500;
//										
//										float x = (float) (boss.getX() + (Math.cos(rad) * distance));
//										float y = (float) (boss.getY() + (Math.sin(rad) * 0.5f * distance));
//										
//										Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), x, y);//, 0.001f, 5f);
//										bullet.setDirectionDeg((float) ((180f - finalAngle) - (((Math.random() < 0.5f) ? -1f : 1f) * Math.random() * 90f)), 300f);
//										
//										game.spawn(bullet);
//										
//										bullet.addEffect(new Plugin<Bullet>()
//										{
//											Float angle = null;
//											boolean done = false;
//											double dir;
//											
//											public void update(Bullet object, long tick)
//											{
//												if(done)
//													return;
//												
//												if(angle == null && object.getVelocityYTick() >= 0f)
//												{
//													float straightAngle = MathUtil.getAngle(object, game.getPlayer());
//													
//													if(angle == null)
//													{
//														angle = object.getVelocityRotationDeg();
//													}
//													
//													double diff = MathUtil.getDifference(straightAngle, angle);
//												}
//												
//												if(angle != null)
//												{
//													float straightAngle = MathUtil.getAngle(object, game.getPlayer());
//													
//													double diff = MathUtil.getDifference(straightAngle, angle);
//													double diffNorm = MathUtil.normalizeDegree((float) diff);
//													
//													dir = (diff > 0 ? 1f : -1f);
//													
//													double maxAngle = 1f;
//													
//													double addAngle = dir * maxAngle;
//													
//													angle = MathUtil.normalizeDegree((float)(angle.doubleValue() + addAngle));
//													
//													if(diffNorm <= maxAngle || diffNorm >= 360f - maxAngle)
//													{
//														angle = straightAngle;
//													}
//													
//													double dist = MathUtil.getDistance(object, game.getPlayer());
//													
//													if(dist < 200 || diffNorm > 180)
//														done = true;
//													
//													object.setDirectionDeg(angle, 400f);
//												}
//											};
//										});
//									}
//								};
//								
//								game.addTaskGame(run, (int) (((angle - 200f) / 140f) * 60f));
//							}
//						};
//					});
//			
//			if(true)
//				return;
			
			addPhase(new Phase<Cirno>(40)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick % 4 == 0)
						TouhouSounds.Enemy.BREAK_2.play(0.5f);
					
					if(tick % 2 != 0)
						return;
					
					for(double i = -(60 + RNG.random() * 10); i < 60 + RNG.random() * 10; i += RNG.random() * 30f)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), boss.getX(), boss.getY());
						
						double modifier = Math.min(0.75f, (float)tick / (float)getPhaseInterval());
						
						float speed = (float) (200f + 600f * modifier);
						
						bullet.setDirectionDeg((float) (MathUtil.getAngle(bullet, player) + i), speed);
						bullet.setRotationFromVelocity(-90f + (float) ((RNG.random() < 0.5f ? -1 : 1) * RNG.random() * 10));
						
						game.spawn(bullet);
					}
				};
			});
			
			final int delay = 20;
			
			addPhase(new Phase<Cirno>(delay * 3)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick % delay != 0)
						return;
					
					TouhouSounds.Enemy.BULLET_1.play(1f);
					
					int time = tick / delay;
					
					time = Math.max(0, Math.min(2, time));
					
					ThBullet[] bullets = { new ThBullet(ThBulletType.BALL_2, ThBulletColor.BLUE), new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.BLUE), new ThBullet(ThBulletType.BALL_LARGE_HOLLOW, ThBulletColor.BLUE) };
					
					for(int i = 0; i < 360; i += 10)
					{
						Bullet bullet = new Bullet(bullets[time], boss.getX(), boss.getY());
						
						bullet.setDirectionDeg(i, 500f);
						bullet.setGlowing();
						
						game.spawn(bullet);
					}
				}
			});
			
			addPhase(new Phase<Cirno>(100)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick != 0)
						return;
					
					final SimpleTouhouBossPath p = new SimpleTouhouBossPath(boss);
					p.setTime(Duration.ticks(80));
					boss.getPathing().setCurrentPath(p);
				}
			});
		}
		
		@Override
		public void tick(int tick)
		{
			if(tick < 60)
				return;
			
			tick -= 60;
			
			super.tick(tick);
		}
	}

	public static class Spell extends PhaseSpellcard<Cirno>
	{
		public Spell(Cirno owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(50));
//			setSpellcardTime(Duration.seconds(1));
			
			addPhase(new Phase<Cirno>(30)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick == 0)
					{
						boss.playSpecial(true);
						
						TouhouSounds.Enemy.BULLET_1.play(1f);
						
						float angle = MathUtil.getAngle(boss, game.getPlayer());
						
						for(int i = 0; i < 360; i += 360 / 6f)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.WHITE), boss.getX(), boss.getY());
							
							bullet.setGlowing();

							bullet.setScale(1f, 3f);
							
							bullet.setDirectionDeg(i + angle, 600f);
							bullet.setRotationFromVelocity(-90);
							
							game.spawn(bullet);
						}
					}
					
					final int delay = 10;
					
					if(tick < 3 * delay && tick % delay == 0)
					{
						final int wave = (tick % (3 * delay)) / delay;
						
						for(int i = (int) (0 + 360f / 12f); i < (int) (360 + 360f / 12f); i += 360 / 24f)
						{
							int r = (int) (i / (360f / 24f));
							
							if(r % 2 == 0)
								continue;
							
							for(int amount = 0; amount < 3; amount++)
							{
								final int finalAmount = amount;
								
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), boss.getX(), boss.getY());

								bullet.addEffect(new Plugin<Bullet>()
								{
									@Override
									public void update(Bullet object, long tick)
									{
										if(object.getTicksAlive() == 40 - (wave * delay))
										{
											float angle = object.getVelocityRotationDeg();
											
											if(finalAmount == 0)
											{
												angle -= 15;
											}
											else if(finalAmount == 2)
											{
												angle += 15;
											}
											
											object.setDirectionDeg(angle, 200f);
											object.setRotationFromVelocity();
										}
									}
								});
								
								float angle = i;
								
								if(finalAmount == 0)
								{
									angle -= 1;
								}
								else if(finalAmount == 2)
								{
									angle += 1;
								}
								
								bullet.setDirectionDeg(angle, 400f);
								bullet.setRotationFromVelocity();

								game.spawn(bullet);
							}
						}
					}
				}
			});
			
			addPhase(new Phase<Cirno>(60)
			{
				@Override
				public void tick(int tick, final J2hGame game, Cirno boss)
				{
					if(tick % 5 == 0)
						TouhouSounds.Enemy.ICE_1.play(0.5f);
					
					if(tick > 40 && tick % 8 == 0)
					{
						TouhouSounds.Enemy.BULLET_1.play(1f);
						TouhouSounds.Enemy.ICE_2.play(0.5f);
					}
					
					if(tick == 0)
					{
						for(int i = 0; i < 5; i++)
						for(final boolean bool : i == 0 ? new boolean[]{ true, false } : new boolean[] { RNG.random() < 0.5f ? false : true })
						{
							int pos = 0;
							
							while(pos == 0 || MathUtil.getDistance(pos, 0, game.getPlayer().getX(), 0) < 150)
							{
								pos = (int) (game.getWidth() * RNG.random());
							}

							final int maxTime = 60;

							for(int time = 0; time < maxTime; time += 4)
							{
								final int finalTime = time;
								final float x = pos;
								final float y = bool ? game.getHeight() + 100 : -100;

								for(final boolean side : new boolean[]{ true, false })
									game.addTaskGame(new Runnable()
									{
										@Override
										public void run()
										{
											Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE, ThBulletColor.WHITE), x + (side ? -10 : 10), y);
 
											bullet.setDirectionDeg(bool ? 90 : 270, 500f);
											bullet.setGlowing();

											bullet.addEffect(new Plugin<Bullet>()
											{
												float offset = (float) (((RNG.random() * 2) - 1) * 30f);
												
												@Override
												public void update(Bullet object, long tick)
												{
													float modifier = (float)finalTime / (float)maxTime;

													if(object.getTicksAlive() > maxTime - finalTime)
													{
														final float degree = bool ? 90 + (side ? -1 : 1) * 30f : 270 + (side ? 1 : -1) * 30f;
														object.setDirectionDeg(degree + offset, 100f + 250f * modifier);
														object.setRotationFromVelocity();
													}
												}
											});

											bullet.setRotationDeg((side ? -1 : 1) * (bool ? -1 : 1) * 140);

											game.spawn(bullet);
										};
									}, time);
							}
						}
					}
				}
			});
			
			addPhase(new Phase<Cirno>(40)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick != 0)
						return;
					
					SimpleTouhouBossPath p = new SimpleTouhouBossPath(boss);
					p.setTime(Duration.ticks(40));
					
					boss.getPathing().setCurrentPath(p);
				}
			});
		}
		
		@Override
		public void tick(int tick)
		{
			if(tick < 60)
				return;
			
			tick -= 60;
			
			super.tick(tick);
		}
	}
}

