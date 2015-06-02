package java2hu.allstar.spellcards;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Position;
import java2hu.SmartTimer;
import java2hu.allstar.enemies.day1.Cirno;
import java2hu.allstar.menu.SpellVault.TestScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.Plugin;
import java2hu.spellcard.PhaseSpellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.util.BossUtil;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;

public class BlazeItScheme extends TestScheme
{
	@Override
	public void runScheme()
	{
		spawnPlayer();
		
		final J2hGame g = Game.getGame();
		final BlazeItScheme scheme = this;
		
		final Cirno boss = runAndReturnSync(new SpawnBossTask<Cirno>()
		{
			@Override
			public Cirno get() { return new Cirno(100, Game.getGame().getCenterX(), Game.getGame().getCenterY() + 400); }
		});
		
		System.out.println(boss);
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		runAndWaitSync(new SyncTask()
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
						
						startSpellcard(boss);
					}
				}, 60);
				
				setCompleted(true);
			}
		});
		
		SchemeUtil.waitForDeath(this, boss);

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
	
	@Override
	public void startSpellcard(Boss boss)
	{
		Game.getGame().startSpellCard(new BlazeItCard((Cirno)boss));
	}
	
	public static class BlazeItCard extends PhaseSpellcard<Cirno>
	{
		public BlazeItCard(Cirno owner)
		{
			super(owner);
			
			addPhase(new Phase<Cirno>(60 * 4)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick % 200 != 0)
						return;
					
					for(double mod = 0d; mod <= 1d; mod += 0.005d)// + ((mod > modA && mod <= modA + 0.1d) ? 0.3d : 0d))
					{
						double a = 500d;

						double t = (mod * (2d * Math.PI));

						{
							double x = 0;
							double y = 0;

							// X
							{
								x = a * (sin(t) + 1) * cos(t) * ((9d/10d) * cos(8*t) + 1) * ((1d/10d) * cos(24 * t) + 1) * ((1/10d) * cos(200 * t) + (9d/10d));
							}

							// y
							{
								y = a * sin(t) * (sin(t) + 1) * ((9d/10d) * cos(8*t) + 1) * ((1d/10d) * cos(24 * t) + 1) * ((1/10d) * cos(200 * t) + (9d/10d));
							}
							
							x *= 0.2d;
							y *= 0.2d;

							Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_OUTLINE, ThBulletColor.GREEN), (float)(game.getCenterX() + x), (float)(game.getCenterY() + y));
							
//							bullet.useSpawnAnimation(false);
							
							final float angle = MathUtil.getAngle(game.getCenterX(), game.getCenterY(), bullet.getX(), bullet.getY());
							
							bullet.setDirectionDeg(angle, 400f);
							
							bullet.addEffect(new Plugin<Bullet>()
							{
								SmartTimer timer = new SmartTimer(0.1f, -1f, 1f);
								
								double addAngle = 0d;
								
								@Override
								public void update(Bullet object, long tick)
								{
									timer.tick();
									
									int intAngle = (int)angle;
									
//									if(intAngle % 6 > 2)
									{
										if(addAngle == 0)
										{
											addAngle = 1d;
										}
										
										addAngle += 2d * (1d - (Math.min(1d, object.getTicksAlive() / 500d)));
//										addAngle = MathUtil.normalizeDegree((float) addAngle);
									}
									
									float newAngle = (float) (angle + addAngle + (timer.getTimer() * 10d));
									
									newAngle = MathUtil.normalizeDegree(newAngle);
									
									object.setDirectionDeg(newAngle, 400f);
								}
							});
							
							

							game.spawn(bullet);
						}
					}
				}
			});
			
			addPhase(new Phase<Cirno>(60 * 4)
			{
				@Override
				public void tick(int tick, J2hGame game, Cirno boss)
				{
					if(tick % 50 != 0)
						return;
					
					Position[] positions = { 
							new Position(game.getCenterX() - 400, game.getCenterY()),
							new Position(game.getCenterX() + 400, game.getCenterY()),
							new Position(game.getCenterX() - 400, game.getCenterY() - 400),
							new Position(game.getCenterX() + 400, game.getCenterY() - 400),
							new Position(game.getCenterX() - 400, game.getCenterY() + 400),
							new Position(game.getCenterX() + 400, game.getCenterY() + 400),
					};
					
					int index = (int) Math.min(positions.length - 1, (tick / 50d));
					
					Position p = positions[index];
					
					for(double mod = 0d; mod <= 1d; mod += 0.005d)// + ((mod > modA && mod <= modA + 0.1d) ? 0.3d : 0d))
					{
						double a = 50d;

						double t = (mod * (2d * Math.PI));

						{
							double x = 0;
							double y = 0;

							// X
							{
								x = a * (sin(t) + 1) * cos(t) * ((9d/10d) * cos(8*t) + 1) * ((1d/10d) * cos(24 * t) + 1) * ((1/10d) * cos(200 * t) + (9d/10d));
							}

							// y
							{
								y = a * sin(t) * (sin(t) + 1) * ((9d/10d) * cos(8*t) + 1) * ((1d/10d) * cos(24 * t) + 1) * ((1/10d) * cos(200 * t) + (9d/10d));
							}
							
							x *= 0.2d;
							y *= 0.2d;

							Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_OUTLINE, ThBulletColor.GREEN), (float)(p.getX() + x), (float)(p.getY() + y));
							
//							bullet.useSpawnAnimation(false);
							
							final float angle = MathUtil.getAngle(game.getCenterX(), game.getCenterY(), bullet.getX(), bullet.getY());
							
							bullet.setDirectionDeg(angle, 200f);
							
							bullet.addEffect(new Plugin<Bullet>()
							{
								SmartTimer timer = new SmartTimer(0.1f, -1f, 1f);
								
								double addAngle = 0d;
								
								@Override
								public void update(Bullet object, long tick)
								{
									timer.tick();
									
									int intAngle = (int)angle;
									
//									if(intAngle % 6 > 2)
									{
										if(addAngle == 0)
										{
											addAngle = 1d;
										}
										
										addAngle += 2d * (1d - (Math.min(1d, object.getTicksAlive() / 500d)));
//										addAngle = MathUtil.normalizeDegree((float) addAngle);
									}
									
									float newAngle = (float) (angle + addAngle + (timer.getTimer() * 10d));
									
									newAngle = MathUtil.normalizeDegree(newAngle);
									
									object.setDirectionDeg(newAngle, 200f);
								}
							});
							
							

							game.spawn(bullet);
						}
					}
				}
			});
		}
	}
}
