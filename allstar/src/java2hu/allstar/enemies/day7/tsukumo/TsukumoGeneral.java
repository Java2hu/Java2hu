package java2hu.allstar.enemies.day7.tsukumo;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.ReturnSyncTask;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.BGMPlayer;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.Plugin;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.bullet.ThLaser;
import java2hu.touhou.bullet.ThLaserColor;
import java2hu.touhou.bullet.ThLaserType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.Scheduler;
import java2hu.util.SchemeUtil;

import com.badlogic.gdx.graphics.Color;

public class TsukumoGeneral implements SpecialFlowScheme<AllStarStageScheme>
{
	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();

		final SaveableObject<Benben> boss1SO = new SaveableObject<Benben>();
		final SaveableObject<Yatsuhashi> boss2SO = new SaveableObject<Yatsuhashi>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				boss1SO.setObject(new Benben(100, Game.getGame().getWidth()/2 - 200, 800));
				boss2SO.setObject(new Yatsuhashi(100, Game.getGame().getWidth()/2 + 200, 800));
			
				final BGMPlayer player = new BGMPlayer(boss2SO.getObject().getBackgroundMusic(), true)
				{
					@Override
					public boolean isPersistant()
					{
						return boss1SO.getObject().isOnStage() && boss2SO.getObject().isOnStage();
					}
				};
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						Game.getGame().spawn(player);
						player.play();
						player.fadeIn();
					}
				}, 30);
				
				boss2SO.getObject().setBackgroundMusic(null);
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return boss2SO.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		final Benben boss1 = boss1SO.getObject();
		final Yatsuhashi boss2 = boss2SO.getObject();
		
		final SaveableObject<CircleHealthBar> barBoss1 = new SaveableObject<CircleHealthBar>();
		final SaveableObject<CircleHealthBar> barBoss2 = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss1, boss1.getAuraColor(), boss2.getAuraColor(), 60);
				
				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						BossUtil.cloudEntrance(boss2, boss2.getAuraColor(), new Color(149f/255f, 141/255f, 81/255f, 1f), 60);
					}
				}, 30);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						barBoss1.setObject(new CircleHealthBar(boss1));
						barBoss2.setObject(new CircleHealthBar(boss2));
						
						g.spawn(boss1);
						
						g.spawn(barBoss1.getObject());
						
						barBoss1.getObject().addSplit(0.8f);
						
						AllStarUtil.introduce(boss1, boss2);
						
						BossUtil.addBossEffects(boss1, boss1.getAuraColor(), boss1.getBgAuraColor());
						
						boss1.healUp();
					}
				}, 70);
				
				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						barBoss2.setObject(new CircleHealthBar(boss2));
						
						g.spawn(boss2);
						
						g.spawn(barBoss2.getObject());
						
						barBoss2.getObject().addSplit(0.8f);
						
						boss2.healUp();
						
						BossUtil.addBossEffects(boss2, boss2.getAuraColor(), boss2.getBgAuraColor());
						
						Game.getGame().startSpellCard(new TsukumoNonSpell(boss1, boss2));
					}
				}, 100);
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss2.isOnStage();
			}
		});
		
		scheme.doWait();

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				try
				{
					return !boss2.isDead();
				}
				catch(Exception e)
				{
					return false;
				}
			}
		});
		
		scheme.doWait();
		
		barBoss1.getObject().split();
		barBoss2.getObject().split();
		
		boss1.setHealth(boss1.getMaxHealth());
		boss2.setHealth(boss2.getMaxHealth());
		
		final SaveableObject<FreeStageObject> circle1 = new SaveableObject<FreeStageObject>();
		final SaveableObject<FreeStageObject> circle2 = new SaveableObject<FreeStageObject>();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				
				if(!barBoss1.getObject().isOnStage())
					Game.getGame().spawn(barBoss1.getObject());
				
				if(!barBoss2.getObject().isOnStage())
					Game.getGame().spawn(barBoss2.getObject());
				
				AllStarUtil.presentSpellCard(boss2, "Stormy Harmony");
				
				final TsukumoSpell card = new TsukumoSpell(boss1, boss2);
				
				boss2.spawnBackground(scheme.getBossAura());
				
				Game.getGame().startSpellCard(card);
				
				circle1.setObject(BossUtil.spellcardCircle(boss1, card, scheme.getBossAura()));
				circle2.setObject(BossUtil.spellcardCircle(boss2, card, scheme.getBossAura()));
			}
		}, 1);
		
		final AllStarBoss boss = scheme.runAndReturnSync(new ReturnSyncTask<AllStarBoss>()
		{
			@Override
			public void run()
			{
				if(boss1.isDead())
				{
					setResult(boss1);
				}
				
				if(boss2.isDead())
				{
					setResult(boss2);
				}
				
				if(!isCompleted())
					Game.getGame().addTask(this, 1);
			}
		});
		
		final AllStarBoss bossAlive = boss == boss1 ? boss2 : boss1;
		final SaveableObject<FreeStageObject> circleDead = boss == boss2 ? circle2 : circle1;
		final SaveableObject<CircleHealthBar> barDead = boss == boss2 ? barBoss2 : barBoss1;
		
		SchemeUtil.deathAnimation(scheme, boss, boss.getAuraColor());
		
		Game.getGame().delete(barDead.getObject());
		Game.getGame().delete(circleDead.getObject());
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.mapleExplosion(boss);
				boss.setPosition(-1000, -1000);
				boss.addEffect(new Plugin<StageObject>()
				{
					@Override
					public void update(StageObject object, long tick)
					{
						object.setPosition(-1000, -1000);
					}
				});
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, bossAlive);

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().clearCircle(800f, bossAlive, ClearType.ALL);
			}
		}, 1);
		
		scheme.waitTicks(2);
		
		SchemeUtil.deathAnimation(scheme, bossAlive, boss.getAuraColor());
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				ObjectUtil.deathAnimation(bossAlive);
				
				Game.getGame().delete(boss);
				Game.getGame().delete(bossAlive);
				
				Game.getGame().clear(ClearType.ALL);
			}
		}, 5);
		
		scheme.getBossAura().clearAuras();
		
		scheme.waitTicks(10); // Prevent concurrency issues.
	}
	
	public static class TsukumoNonSpell extends Spellcard
	{
		Benben benben;
		Yatsuhashi yatsuhashi;
		
		public TsukumoNonSpell(Benben boss1, Yatsuhashi boss2)
		{
			super(boss1);

			this.benben = boss1;
			this.yatsuhashi = boss2;
			
			benben.setDamageModifier(0.8f);
			yatsuhashi.setDamageModifier(0.8f);
			
			setSpellcardTime(Duration.seconds(28));
		}
		
		public float offset = 1f;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			
			if(!yatsuhashi.isHealing() && !benben.isHealing())
			{
				if(yatsuhashi.getHealth() < benben.getHealth())
				{
					benben.setHealth(yatsuhashi.getHealth());
				}
				else if(benben.getHealth() < yatsuhashi.getHealth())
				{
					yatsuhashi.setHealth(benben.getHealth());
				}
			}
			
			if(tick % 10 == 0)
			{
				TouhouSounds.Notes.NOTE_1.play(0.5f);
			}
			
			if(tick % 30 == 0)
			{
				offset = (float) (Math.random() * 60);
			}
			
			if(tick % 5 == 0)
			{
				for(float i = offset; i < 360 + offset; i += 60)
				{
					final float finalI = i + tick % 30 + tick / 5f % 360;

					Bullet bullet = new Bullet(new ThBullet(ThBulletType.NOTE_EIGHT, ThBulletColor.PURPLE), yatsuhashi.getX(), yatsuhashi.getY())
					{
						float angle = finalI;

						@Override
						public void onUpdate(long tick)
						{
							setDirectionDegTick(angle, 5f);

							if(angle - finalI < 180)
								angle += 2f;
							else if(angle - finalI < 360)
								angle += 4f;

							super.onUpdate(tick);
						}
						
						@Override
						public void onDraw()
						{
							super.onDraw();
						}
					};

//					bullet.getSpawnAnimation().setAlpha(0.5f);
//					bullet.getSpawnAnimation().setAddedScale(0);
//					bullet.getSpawnAnimation().setTime(80);
					bullet.setGlowing();
					
					game.spawn(bullet);
					
					bullet = new Bullet(new ThBullet(ThBulletType.NOTE_EIGHT, ThBulletColor.BLUE), benben.getX(), benben.getY())
					{
						float angle = finalI;

						@Override
						public void onUpdate(long tick)
						{
							setDirectionDegTick(angle, 5f);

							if(angle - finalI < 180)
								angle += 2f;
							else if(angle - finalI < 360)
								angle += 4f;

							super.onUpdate(tick);
						}
					};

//					bullet.getSpawnAnimation().setAlpha(0.5f);
//					bullet.getSpawnAnimation().setAddedScale(0);
//					bullet.getSpawnAnimation().setTime(80);
					bullet.setGlowing();
					game.spawn(bullet);
				}
			}
		}
	}
	
	public static class TsukumoSpell extends Spellcard
	{	
		Benben benben;
		Yatsuhashi yatsuhashi;
		boolean alone = false;
		
		public TsukumoSpell(Benben boss1, Yatsuhashi boss2)
		{
			super(boss1);

			this.benben = boss1;
			this.yatsuhashi = boss2;
		
			setSpellcardTime(Duration.seconds(42));
		}
		
		@Override
		public void onTimeOut()
		{
			benben.setHealth(0f);
			yatsuhashi.setHealth(0f);
			
			super.onTimeOut();
		}
		
		int wait = 0;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			
			if(wait > 0)
			{
				wait--;
				
				if(wait <= 0)
				{
					setSpellcardTick(0);
				}
				return;
			}
			
			if(!alone)
			{
				if(benben.isDead())
				{
					alone = true;
					
					BossUtil.cloudSpecial(yatsuhashi, Color.PURPLE, Color.YELLOW, 60);
					BossUtil.moveTo(yatsuhashi, Game.getGame().getWidth()/2f, Game.getGame().getHeight() - 250, 800);
					
					wait = 60;
				}
				
				if(yatsuhashi.isDead())
				{
					alone = true;
					
					BossUtil.cloudSpecial(benben, new Color(97f/255f, 66f/255f, 50f/255f, 1f), Color.WHITE, 60);
					BossUtil.moveTo(benben, Game.getGame().getWidth()/2f, Game.getGame().getHeight() - 250, 800);
					
					wait = 60;
				}
			}
			
			if(tick % 300 == 0)
			{
				if(!alone)
				{
					BossUtil.moveAroundRandomly(yatsuhashi, 800, 1000, Game.getGame().getHeight() - 200, Game.getGame().getHeight() - 300, 1000);
					BossUtil.moveAroundRandomly(benben, 400, 600, Game.getGame().getHeight() - 200, Game.getGame().getHeight() - 300, 1000);
				}
				else
				{
					Boss left = benben.isDead() ? yatsuhashi : benben;
					
					BossUtil.moveAroundRandomly(left, 500, 700, Game.getGame().getHeight() - 200, Game.getGame().getHeight() - 300, 1000);
				}
			}
			
			if(!benben.isDead() && tick % (alone ? 100 : 600) >= 0 && tick % (alone ? 100 : 600) <= 60 && tick % 5 == 0)
			{
				float offset = tick % 600;
				
				for(float i = offset; i < 360 + offset; i += 30)
				{
					Bullet b = new Bullet(new ThBullet(ThBulletType.NOTE_QUARTER_REST, ThBulletColor.BLUE), benben.getX(), benben.getY())
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
						
							if(getTicksAlive() == 120)
							{
								setDirectionDegTick((float) (getVelocityRotationDeg() + Math.random() * 50 - 25), 7f);
								setRotationFromVelocity(-90);
								
								if(!Scheduler.isTracked("release1", "release1"))
								{
									TouhouSounds.Enemy.RELEASE_1.play();
									Scheduler.track("release1", "release1", (long) 10);
								}
							}
						}
					};
					
					b.setGlowing();
					b.setZIndex((int) (b.getZIndex() + i));
					b.setDirectionDegTick(i, 1f);
					b.setRotationFromVelocity(-90);
					
					game.spawn(b);
				}
			}

			if(!benben.isDead() && tick % (alone ? 300 : 600) >= (alone ? 50 : 206) && tick % (alone ? 300 : 600) <= (alone ? 150 : 260) && tick % (alone ? 12 : 14) == 0)
			{
				float offset = (tick % (alone ? 300 : 600) - 200) / (alone ? 5f : 4f);
				
				TouhouSounds.Notes.NOTE_1.play();
				
				for(float i = alone ? 5 : 0; i <= 720; i += alone ? 20 : 15)
				{
					float angle = i;
					
					if(angle % 720 >= 360)
					{
						angle = angle - offset;
					}
					else
					{
						angle = angle + offset;
					}
					
					Bullet b = new Bullet(new ThBullet(ThBulletType.NOTE_EIGHT, ThBulletColor.PURPLE), benben.getX(), benben.getY())
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
						}
					};
					
					b.setDirectionDegTick(angle, 6f);
					b.setRotationDeg(0);
					
//					b.getSpawnAnimation().setTime(50);
					b.setZIndex(b.getZIndex() + tick % 400);
					
					game.spawn(b);
				}
			}
			
			if(!yatsuhashi.isDead() && tick % (alone ? 5 : 30) == 0)
			{
				if(!Scheduler.isTracked("laser1", "laser1"))
				{
					TouhouSounds.Enemy.LAZER_2.play(0.5f);
					Scheduler.track("laser1", "laser1", (long) 10);
				}
				
				float angle = (float) (180 + Math.random() * 180);
				
				Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, Math.random() > 0.5f ? ThLaserColor.BLUE_LIGHT : ThLaserColor.PURPLE), yatsuhashi.getX(), yatsuhashi.getY(), 400f);
			
				laser.setDirectionDegTick(angle, 30f);
				laser.setGlowing();
				
				game.spawn(laser);
			}
			
			if(alone && !yatsuhashi.isDead() && tick % 100 == 0)
			{
				TouhouSounds.Notes.NOTE_2.play();
				
				float angle = (float) MathUtil.getAngle(yatsuhashi, player);
				
				for(int i = -10; i <= 10; i += 2)
				{
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.NOTE_EIGHT, ThBulletColor.YELLOW), yatsuhashi.getX(), yatsuhashi.getY());
					bullet.setDirectionDegTick(angle + i, 5f);
					bullet.setZIndex(bullet.getZIndex() + i);
					
					game.spawn(bullet);
				}
			}
			
			if(!yatsuhashi.isDead() && tick % 140 == 0)
			{
				for(int i = -2; i <= 2; i++)
				{
					final int finalI = i;
					
					Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.BLUE_LIGHT), -1000, -1000, 800)
					{
						boolean faster = alone;
						
						{
							setName("lasers");
							setUnitsPerPoint(1);
						}
						
						float angle = Float.NaN;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							float speed = faster ? 15f : 10f;
							
							if(getTicksAlive() == 100)
							{
								clearPath();
								
								setPosition(Game.getGame().getWidth() + 200, 500 + finalI * 100);

								angle = (float) (MathUtil.getAngle(this, player) + 5.1f);
								
								setDirectionDegTick(angle, 10f);
							}
							
							if(getTicksAlive() > 120 && getTicksAlive() < 300)
							{
								angle -= 0.3f;
								
								setDirectionDegTick(angle, speed);
							}
							
							if(getTicksAlive() == 400)
							{
								clearPath();
								
								setPosition(Game.getGame().getWidth()/2f + finalI * 100f, Game.getGame().getHeight() + 400);

								angle = (float) (MathUtil.getAngle(this, player) + 5.1f);
								
								setDirectionDegTick(angle, 10f);
							}
							
							if(getTicksAlive() > 420 && getTicksAlive() < 600)
							{
								angle += finalI * 0.01f;
								
								setDirectionDegTick(angle, speed);
							}
							
							if(getTicksAlive() == 700)
							{
								clearPath();
								
								setPosition(- 600, 500 + finalI * 100);

								angle = (float) (MathUtil.getAngle(this, player) - 5.1f);
								
								setDirectionDegTick(angle, 10f);
							}
							
							if(getTicksAlive() > 720 && getTicksAlive() < 900)
							{
								angle += finalI * 0.01f;
								
								setDirectionDegTick(angle, speed);
							}
							
							if(getTicksAlive() == 1000)
							{
								clearPath();
								
								setTextures(new ThLaser(ThLaserType.NORMAL, ThLaserColor.PURPLE).getAnimation());
								
								setPosition(- 800, Game.getGame().getHeight() + 800 + finalI * 100);

								angle = (float) (MathUtil.getAngle(this, player) - 5.1f);
								
								setDirectionDegTick(angle, 10f);
							}
							
							if(getTicksAlive() > 1020 && getTicksAlive() < 1200)
							{
								angle += finalI * 0.1f;
								
								setDirectionDegTick(angle, speed);
							}
							
							if(getTicksAlive() == 1300)
							{
								clearPath();
								
								setPosition(Game.getGame().getWidth() + 800, Game.getGame().getHeight() + 800 + finalI * 100);

								angle = (float) (MathUtil.getAngle(this, player) - 5.1f);
								
								setDirectionDegTick(angle, 10f);
							}
							
							if(getTicksAlive() > 1320 && getTicksAlive() < 1500)
							{
								angle += finalI * 0.1f;
								
								setDirectionDegTick(angle, speed);
							}
							
							if(getTicksAlive() > 1600)
								Game.getGame().delete(this);
						}
	
						@Override
						public void clearPath()
						{
							faster = alone; // Check if yatsuhashi is alone now.
							
							if(yatsuhashi.isDead())
								Game.getGame().delete(this);;
							
							super.clearPath();
						}
						
						@Override
						public boolean doDelete()
						{
							return false;
						}
					};
					
					laser.setZIndex(laser.getZIndex() + i);
					laser.setGlowing();

					game.spawn(laser);
				}
			}
		}
	}
}
