package java2hu.allstar.enemies.day9.yuuka;

import java2hu.Game;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Position;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.GravityBullet;
import java2hu.object.bullet.Laser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
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
import java2hu.util.Getter;
import java2hu.util.MathUtil;
import java2hu.util.MeshUtil;
import java2hu.util.SchemeUtil;

import shaders.ShaderLibrary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;

public class YuukaGeneral implements SpecialFlowScheme<AllStarStageScheme>
{
	private float x;
	private float y;
	
	public YuukaGeneral(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	private static String SPELLCARD_NAME = "Passing of the Seasons - \"Sunflower's Odyssey\"";
	
	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final SaveableObject<Yuuka> yuuka = new SaveableObject<Yuuka>();
		final SaveableObject<Yuuka98> yuuka98 = new SaveableObject<Yuuka98>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				((AllStarGame)Game.getGame()).setPC98(false);
				yuuka.setObject(Yuuka.newInstance(x, y));
				yuuka.getObject().spawnBackground(scheme.getBossAura());
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return yuuka.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		{
			final Yuuka boss = yuuka.getObject();

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
							scheme.getBossAura().setAura(0, new Getter<IPosition>()
							{
								@Override
								public IPosition get()
								{
									IPosition pos = yuuka98.getObject();
									
									if(pos == null)
										pos = yuuka.getObject();
									
									return pos;
								}
							});
							
							bar.setObject(new CircleHealthBar(boss));

							g.spawn(boss);
							g.spawn(bar.getObject());

							bar.getObject().addSplit(0.5f);

							AllStarUtil.introduce(boss);
							
							boss.setDamageModifier(0.8f);

							boss.healUp();
							BossUtil.backgroundAura(boss);

							Game.getGame().startSpellCard(new YuukaNonSpell(boss));
						}
					}, 60);
				}
			}, 1);

			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					return !boss.isOnStage();
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
						return !boss.isDead();
					}
					catch(Exception e)
					{
						return false;
					}
				}
			});

			scheme.doWait();

			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					Game.getGame().delete(boss);
					
					Game.getGame().getSpellcards().clear();
					Game.getGame().clearObjects();
					
					BossUtil.mapleExplosion(boss.getX(), boss.getY());
				}
			}, 1);
		}
		
		scheme.waitTicks(60);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				yuuka98.setObject(Yuuka98.newInstance(x, y));
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return yuuka98.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		final Yuuka98 boss = yuuka98.getObject();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().clear(ClearType.ALL);
				
				boss.spawnBackground(scheme.getBossAura());
			
				((AllStarGame)Game.getGame()).setPC98(true);
				
				final CircleHealthBar bar = new CircleHealthBar(boss);
				
				boss.setMaxHealth(200);
				
				Game.getGame().spawn(bar);
				Game.getGame().spawn(boss);
				
				BossUtil.backgroundAura(boss);
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new Yuuka98Spell(boss));
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class YuukaNonSpell extends Spellcard
	{	
		public YuukaNonSpell(StageObject owner)
		{
			super(owner);
		}

		@SuppressWarnings("unused")
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Yuuka boss = (Yuuka) getOwner();
			
			if(tick % 300 == 200)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
			
			if(tick % 300 < 160 && tick % 2 == 0)
				TouhouSounds.Enemy.RELEASE_1.play(0.4f);
			
			if(tick % 300 == 0)
			{
				TouhouSounds.Enemy.LAZER_2.play();
				
				for(float angle = 0; angle < 360; angle += 45)
				{	
					final float speedIncrease = 1.012f;
					final float finalAngle = angle;
					
					Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.YELLOW), boss.getX(), boss.getY(), 700)
					{
						float rotationStart = finalAngle + 20f;
						float rotation = rotationStart;
						float rotationEnd = finalAngle + 4 * 360f + 20f;
						float rotationAdd = 4f;
						float speed = 8f;
						
						@Override
						public boolean doDelete()
						{
							return false;
						}
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(rotation < rotationEnd)
							{
								rotation += rotationAdd;

								setDirectionDegTick(rotation, speed);
							}
							
							rotationAdd *= 0.995f;
							speed += 0.01f;
							
							if(speed > 20)
								Game.getGame().delete(this);
						};
					};
					
					Game.getGame().spawn(laser);
					
					for(float i = 180f + angle; i > angle; i -= i < 90 + angle ? 4 : 6)
					{
						float speed = 3f * ((i - angle) / (1f * 800f));
						
						if(speed < 0.1f)
							continue;
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.YELLOW), boss.getX(), boss.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
								
								setVelocityXTick(getVelocityXTick() * speedIncrease);
								setVelocityYTick(getVelocityYTick() * speedIncrease);
							}
						};
						bullet.setShader(ShaderLibrary.GLOW.getProgram());
						bullet.setDirectionDegTick(360 - i + 30, speed);
						bullet.getSpawnAnimationSettings().setAddedScale(5f);

						Game.getGame().spawn(bullet);
						
						if(i > 90 + angle)
						{
							bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.YELLOW), boss.getX(), boss.getY())
							{
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									setVelocityXTick(getVelocityXTick() * speedIncrease);
									setVelocityYTick(getVelocityYTick() * speedIncrease);
									
									setZIndex((int) (this.getZIndex() + MathUtil.getDistance(this, boss)));
								}
							};

							bullet.setDirectionDegTick(360 - i + 30, speed + 0.1f);
							bullet.setRotationFromVelocity();

							Game.getGame().spawn(bullet);
						}
					}

					for(float i = 180f + angle; i > angle; i -= i < 90 + angle ? 4 : 6)
					{
						float speed = 3f * ((i - angle) / (1f * 800f));
						
						if(speed < 0.1f)
							continue;
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.YELLOW), boss.getX(), boss.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
								
								setVelocityXTick(getVelocityXTick() * speedIncrease);
								setVelocityYTick(getVelocityYTick() * speedIncrease);
								
								setZIndex((int) (this.getZIndex() + MathUtil.getDistance(this, boss)));
							}
						};
						bullet.setShader(ShaderLibrary.GLOW.getProgram());
						bullet.getSpawnAnimationSettings().setAddedScale(5f);
						bullet.setDirectionDegTick(i, speed);

						Game.getGame().spawn(bullet);
						
						if(i > 90 + angle)
						{
							bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.YELLOW), boss.getX(), boss.getY())
							{
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									setVelocityXTick(getVelocityXTick() * speedIncrease);
									setVelocityYTick(getVelocityYTick() * speedIncrease);
									
									setZIndex((int) (this.getZIndex() + MathUtil.getDistance(this, boss)));
								}
							};
							

							bullet.setDirectionDegTick(i, speed + 0.1f);
							bullet.setRotationFromVelocity();

							Game.getGame().spawn(bullet);
						}
					}
				}
			}
		}
	}

	public static class Yuuka98Spell extends Spellcard
	{
		public Yuuka98Spell(StageObject owner)
		{
			super(owner);
		}
		
		private Position saved = null;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Yuuka98 boss = (Yuuka98) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0.4f);
			
			{
				final float pTickPeriod = 1000;
				final float pTick = tick % pTickPeriod;
				
				if(pTick >= 0 && pTick < 620 && pTick % 120 == 0)
					BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
				
				if(pTick > 340 && pTick < 560 && tick % 4 == 0 && MathUtil.getDistance(boss, player) < 200)
				{
					float[] offsets = { -10, 0, 10 };
					
					Position tip = boss.getUmbrellaTip();
					
					for(float f : offsets)
					{
						Bullet gib = new Bullet(new ThBullet(ThBulletType.HEART, ThBulletColor.RED), tip.getX(), tip.getY());
						gib.useSpawnAnimation(false);
						gib.setDirectionDegTick(MathUtil.getAngle(tip, player) + f, 20f);
						gib.setRotationFromVelocity();

						Game.getGame().spawn(gib);
					}
				}
				
				int autumn = 130;
				
				if(pTick > autumn && pTick < autumn + 180 && pTick % 40 == 0)
					TouhouSounds.Enemy.EXPLOSION_1.play(0.5f);
				
				if(pTick > autumn && pTick < autumn + 180 && pTick % 7 == 0)
				{
					int interval = 40;
					
					Position umbrellaPoint = boss.getUmbrellaTip();
					float offset;
					
					for(int i = -1; i <= 1; i += 2)
					{
						final int finalI = i;
						
						Laser laser = new Laser(new ThLaser(ThLaserType.LIGHTNING), umbrellaPoint.getX(), umbrellaPoint.getY(), 500)
						{
							float start = finalI == 1 ? -300f * ((pTick - 240) / 100f) - 100 : -300 * ((pTick - 240) / 100f) - 100;
							
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
								
								setDirectionDegTick(start, 10f);
								
								if(finalI == 1)
									start += 0.6f;
								else
									start -= 0.6f;
							}
						};
						
						Game.getGame().spawn(laser);
					}
					
					offset = (float) (Math.random() * (2 * interval));
					
					for(float i = offset; i < 360 + offset; i += interval)
					{
						final float alphaMax = 0.58f;
						
						float[] addedDistance = { 0, 20, 40 };
						
						for(float add : addedDistance)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.BLACK), (float) (umbrellaPoint.getX() + Math.cos(Math.toRadians(i)) * add), (float) (umbrellaPoint.getY() + Math.sin(Math.toRadians(i)) * add))
							{
								@Override
								public void checkCollision()
								{
									// Vanity bullet
								}

								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									float time = 80;

									if(getTicksAlive() >= 100)
									{
										float alpha = alphaMax * ((getTicksAlive() - 100) / time);

										getCurrentSprite().setAlpha(alphaMax - alpha);

										if(getCurrentSprite().getColor().a <= 0)
											Game.getGame().delete(this);
									}
								}
							};

							bullet.getCurrentSprite().setColor(Color.GRAY);
							bullet.getCurrentSprite().setAlpha(alphaMax);
							bullet.setDirectionDegTick(i, (float) (1f + 1f * Math.random()));

							Game.getGame().spawn(bullet);
						}
					}
				}
				
				int winter = 180;
				
				if(pTick > winter && pTick < winter + 100 && pTick % 40 == 0)
					TouhouSounds.Enemy.NOISE.play();
				
				if(pTick > winter && pTick < winter + 100 && pTick % 20 == 0)
				{
					final int max = 60;
					
					for(int i = 1; i <= max; i++)
					{
						final boolean icicle = Math.random() > 0.8f;
						
						final int finalI = i;
						
						float offset = (float) (Game.getGame().getWidth() / max + 50 * Math.random());
						offset = (float) (offset * 0.8f + Math.random() * (offset * 0.2f));
						offset = offset * finalI - 25;

						Bullet bullet = new GravityBullet(icicle ? new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.WHITE) : new ThBullet(ThBulletType.BALL_2, ThBulletColor.WHITE), offset, Game.getGame().getHeight() + 100, (float) (0.04f + Math.random() * 0.04f), (float) (4f + 4f * Math.random()))
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
								
								if(icicle)
									setRotationFromVelocity();
							}
						};
						bullet.setVelocityXTick((float) (Math.random() * 2f - 1f));
						
						Game.getGame().spawn(bullet);
					}
				}
				
				int spring = 340;
				
				if(pTick == spring)
					boss.closeUmbrella();
				
				if(pTick == spring + 40)
				{
					boss.windUp();
				
					saved = new Position(player.getX(), player.getY());
					
					DrawObject obj = new DrawObject()
					{
						float size = 100;
						float decrease = 2f;
						Mesh mesh;
						
						@Override
						public void onDraw()
						{
							if(!isOnStage())
								return;
							
							boolean addDispose = mesh == null;
							
							mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(saved.getX(), saved.getY(), 20, 0, size, Color.YELLOW));
							
							if(addDispose)
								addDisposable(mesh);
							
							Game.getGame().batch.end();
							
							MeshUtil.startShader();
							
							MeshUtil.renderMesh(mesh);
							
							MeshUtil.endShader();
							
							Game.getGame().batch.begin();
						}
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							size -= decrease;
							
							if(size < 0)
								Game.getGame().delete(this);
						}
					};
					
					obj.setZIndex(2);
					
					Game.getGame().spawn(obj);
					
					TouhouSounds.Enemy.HUM_2.play();
				}
				
				if(pTick == spring + 80)
					boss.swirl();
				
				Position umbrellaPoint = boss.getUmbrellaTip();
				
				if(pTick > spring + 80)
				{
					if(pTick % 5 == 0 && pTick < spring + 150)
						TouhouSounds.Enemy.RELEASE_1.play();
					
					float offset = Game.getGame().getTick() / 8f % 40;
					
					if(pTick % 4 == 0 && pTick < spring + 120)
						for(float i = offset; i < 360 + offset; i += 5)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_MEDIUM, ThBulletColor.BLUE), umbrellaPoint.getX(), umbrellaPoint.getY());
							bullet.setDirectionDegTick(MathUtil.getAngle(bullet.getX(), bullet.getY(), saved.getX(), saved.getY()) + i, 11f);
							bullet.setRotationFromVelocity();

							Game.getGame().spawn(bullet);
						}
					
					offset = (pTick - spring - 80) / 20f % 100;
					
					if(pTick % 5 == 0 && (pTick == spring + 85 || pTick > spring + 90) && pTick < spring + 150)
						for(float i = offset; i < 360 + offset; i += 3)
						{
							ThBulletColor color = pTick == spring + 85 ? ThBulletColor.YELLOW : ThBulletColor.BLUE;
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_MEDIUM, color), umbrellaPoint.getX(), umbrellaPoint.getY());
							bullet.setDirectionDegTick(MathUtil.getAngle(bullet.getX(), bullet.getY(), saved.getX(), saved.getY()) + i, 6f - (pTick - spring - 80 + 1) / 20f);
							bullet.setRotationFromVelocity();

							Game.getGame().spawn(bullet);
						}
				}
			
				if(pTick == spring + 150)
					boss.openUmbrella();
				
				
				if(pTick == spring + 260)
				{
					final int max = 5;
					
					for(int i = 1; i <= max; i++)
					{
						final int finalI = i;
						
						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								float offset = Game.getGame().getWidth() / max;
								offset = (float) (offset * 0.8f + Math.random() * (offset * 0.2f));
								offset = offset * finalI;
								
								YuukaBulletWorm worm = new YuukaBulletWorm(offset, 0);
								
								worm.spawnTick = Game.getGame().getTick();
								worm.endTick = worm.spawnTick + 350;
								
								Game.getGame().spawn(worm);
							}
						}, i * 20);
					}
				}
				
				int summer = 700;
				
				if(pTick == spring + 330)
					boss.closeUmbrella();
				
				if(pTick == summer)
					BossUtil.moveTo(boss, (float) (Game.getGame().getWidth()/2 * 1.4), Game.getGame().getHeight() - boss.getHeight() / 2, 1020);
				
				if(pTick == summer + 70)
				{
					saved = boss.getUmbrellaTip();
					
					TouhouSounds.Enemy.ACTIVATE_2.play();
					
					DrawObject obj = new DrawObject()
					{
						float size = 0;
						float maxSize = 40;
						float increase = 2f;
						float decrease = 2f;

						Mesh mesh;
						
						@Override
						public void onDraw()
						{
							if(!isOnStage())
								return;
							
							boolean addDispose = mesh == null;
							
							mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(saved.getX(), saved.getY(), 20, 0, size, Color.YELLOW));
							
							if(addDispose)
								addDisposable(mesh);
							
							Game.getGame().batch.end();
							
							MeshUtil.startShader();
							
							MeshUtil.renderMesh(mesh);
							
							MeshUtil.endShader();
							
							Game.getGame().batch.begin();
						}
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getTicksAlive() < 180)
							{
								if(size < maxSize)
									size += increase;
							}
							else
							{
								size -= decrease;
								
								if(size < 0)
									Game.getGame().delete(this);
							}
						}
					};
					
					obj.setZIndex(10000);
					Game.getGame().spawn(obj);
				}
					
				
				if(pTick == summer + 310)
					boss.swirl();
				
				if(pTick == summer + 370)
					boss.openUmbrella();
				
				if(pTick == summer + 80)
					// Delay it so that bullets don't move if yuuka moves.
					for(int delay = 0; delay <= 80; delay += 4)
					{
						Runnable run = new Runnable()
						{
							@Override
							public void run()
							{
								float offset = Game.getGame().getTick() % 20;

								for(float i = offset; i < 360 + offset; i += 20)
								{
									final float finalI = i;
									
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.YELLOW), saved.getX(), saved.getY())
									{
										@Override
										public void update(long tick)
										{
											if(getTicksAlive() == 60)
												setDirectionDegTick(finalI, 10f);

											super.update(tick);
										}
									};

									bullet.getCurrentSprite().setScale(1f, 3f);
									bullet.setDirectionDegTick(finalI, 0.2f);
									bullet.setRotationFromVelocity();

									Game.getGame().spawn(bullet);
								}
							}
						};
						
						if(delay == 0)
							run.run();
						else
							Game.getGame().addTaskGame(run, delay);
					}
			}
		}
	}
	
	public static class YuukaBulletWorm extends StageObject
	{
		public int id = 10;
		
		public long spawnTick;
		public long endTick;
		public float rotation = 30f;
		public float size = 1f;
		
		public float centerX;
		public float centerY;

		public YuukaBulletWorm(float x, float y)
		{
			super(x, y);
			this.x = x;
			this.y = y;
		}
		
		public void update()
		{
			if(Game.getGame().getTick() % 5 != 0)
				return;
			
			float multiplier = getTicksAlive() / 60f;
			
			float velX = (float) Math.cos(Math.toRadians(20f + 10f * multiplier + 5 * Math.random()) * 20f);
			float velY = 15f;//((float) (Math.sin(Math.toRadians(20f + (10f * multiplier)) * 20f)));
			
			if(getTicksAlive() >= 65 && getTicksAlive() <= 70)
			{
				float radians = (float) Math.toRadians(45);
				float speed = 15f;
				
				velX = (float) (Math.cos(radians) * speed);
				velY = (float) (Math.sin(radians) * speed);
				
				centerX = x - 5;
				centerY = y + 70;
			}
			
			final boolean orange = getTicksAlive() > 70;
			
			if(getTicksAlive() > 70)
			{
				float radians = (float) Math.toRadians(rotation * size);
				float speed = 20f;
				
				velX = (float) (Math.cos(radians) * speed);
				velY = (float) (Math.sin(radians) * speed);
				
				rotation += 18f + 2f * Math.random();
				size *= 1.01f;
			}
			
			final YuukaBulletWorm worm = this;
			
			if(getTicksAlive() >= 160 && getTicksAlive() < 165)
				for(int i = 0; i < 360; i += 20)
				{
					Bullet petal = new Bullet(new ThBullet(ThBulletType.RICE_LARGE, ThBulletColor.YELLOW), (float) (centerX + Math.cos(Math.toRadians(i)) * 70), (float) (centerY + Math.sin(Math.toRadians(i)) * 70))
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(!worm.isOnStage() && getVelocityXTick() == 0.0)
								setDirectionDegTick(getRotationDeg() + 90f, 8f);
						}
					};
					
					petal.setRotationDeg(i - 40);
					
					Game.getGame().spawn(petal);
				}
			
			x += velX;
			y += velY;
			

			final boolean aim = endTick - Game.getGame().getTick() < 50;
			
			Bullet bullet = new Bullet(new ThBullet(aim ? ThBulletType.RICE : ThBulletType.RICE_LARGE, aim ? ThBulletColor.BLACK : orange ? ThBulletColor.ORANGE : ThBulletColor.YELLOW), x, y)
			{
				{
					getCurrentSprite().setColor(orange ? Color.DARK_GRAY : new Color(0.9f, 0.9f, 0.9f, 1f));
					if(aim)
						getCurrentSprite().setScale(0.7f);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(!worm.isOnStage() && getVelocityXTick() == 0.0 && getTicksAlive() > 60)
						if(aim)
						{
							setDirectionDegTick(MathUtil.getAngle(this, Game.getGame().getPlayer()), 7f);
							TouhouSounds.Player.ATTACK_6.play(0.4f);
						}
						else
							setDirectionDegTick(getRotationDeg() + 90f, 8f);
				}
			};

			bullet.setZIndex(this.id);
			this.id++;
			bullet.setRotationDeg((float) (Math.atan2(velY, velX) * (180 / Math.PI) - 90f));
			
			Game.getGame().spawn(bullet);
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
		public void onDraw()
		{
			
		}

		@Override
		public void onUpdate(long tick)
		{
			if(endTick < Game.getGame().getTick())
			{
				Game.getGame().delete(this);
				return;
			}
			
			update();
		}
	}
}
