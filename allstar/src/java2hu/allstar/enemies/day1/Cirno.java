package java2hu.allstar.enemies.day1;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.background.VerticalScrollingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.bullet.Bullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.plugin.Plugin;
import java2hu.spellcard.PhaseSpellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
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
	final static String SPELLCARD_NAME = "Ice Sign - \"Frozen Wall\"";
	
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

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 15F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 15F, 5,6,7,8);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : left.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 9,10,11);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		
		setAuraColor(new Color(0 / 255f, 102 / 255f, 187 / 255f, 1.0f));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
			{
				final Sprite bg = new Sprite(Loader.texture(FOLDER.child("bg.png")));
				bg.setRegion(0f, 0f, 3f, 3f);
				
				game.spawn(new VerticalScrollingBackground(bg, 2f, false)
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
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, 60);

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

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL);
				
				backgroundSpawner.set(scheme.getBossAura());
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new Spell(boss));
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
				ObjectUtil.deathAnimation(boss);
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class NonSpell extends PhaseSpellcard<Cirno>
	{	
		public NonSpell(Cirno owner)
		{
			super(owner);
			
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
					
					for(double i = -(60 + Math.random() * 10); i < 60 + Math.random() * 10; i += Math.random() * 30f)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), boss.getX(), boss.getY());
						
						double modifier = Math.min(0.75f, (float)tick / (float)getPhaseInterval());
						
						float speed = (float) (200f + 600f * modifier);
						
						bullet.setDirectionDeg((float) (MathUtil.getAngle(bullet, player) + i), speed);
						bullet.setRotationFromVelocity(-90f + (float) ((Math.random() < 0.5f ? -1 : 1) * Math.random() * 10));
						
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
					
					BossUtil.moveAroundRandomly(boss, (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 800);
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
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.BLUE), boss.getX(), boss.getY());
							
							bullet.setDirectionDeg(i + angle, 600f);
							
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
						for(int i = 0; i < 2; i++)
						for(final boolean bool : i == 0 ? new boolean[]{ true, false } : new boolean[] { Math.random() < 0.5f ? false : true })
						{
							int pos = (int) (game.getWidth() * Math.random());

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
											Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.WHITE), x + (side ? -10 : 10), y);

											bullet.setDirectionDeg(bool ? 90 : 270, 500f);

											bullet.addEffect(new Plugin<Bullet>()
											{
												@Override
												public void update(Bullet object, long tick)
												{
													float modifier = (float)finalTime / (float)maxTime;

													if(object.getTicksAlive() > maxTime - finalTime)
													{
														object.setDirectionDeg(bool ? 90 + (side ? -1 : 1) * 30f : 270 + (side ? 1 : -1) * 30f, 300f + 300f * modifier);
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
					
					BossUtil.moveAroundRandomly(boss, (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 300, Game.getGame().getHeight() - 600, 600);
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

