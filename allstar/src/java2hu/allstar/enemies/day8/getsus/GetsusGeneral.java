package java2hu.allstar.enemies.day8.getsus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java2hu.Border;
import java2hu.Game;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.Position;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.UpdateObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.bullet.LaserDrawer.LaserAnimation;
import java2hu.object.bullet.StationaryLaser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.Plugin;
import java2hu.plugin.sprites.FadeInSprite;
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
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.Scheduler;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;

public class GetsusGeneral implements SpecialFlowScheme<AllStarStageScheme>
{
	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();

		final SaveableObject<Mugetsu> mugetsuSO = new SaveableObject<Mugetsu>();
		final SaveableObject<Gengetsu> gengetsuSO = new SaveableObject<Gengetsu>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				mugetsuSO.setObject(Mugetsu.newInstance(Game.getGame().getWidth()/2 - 200, 800));
				gengetsuSO.setObject(Gengetsu.newInstance(Game.getGame().getWidth()/2 + 200, 800));
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return gengetsuSO.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		final Mugetsu mugetsu = mugetsuSO.getObject();
		final Gengetsu gengetsu = gengetsuSO.getObject();
		
		gengetsu.sister = mugetsu;
		
		final SaveableObject<CircleHealthBar> barMugetsu = new SaveableObject<CircleHealthBar>();
		final SaveableObject<CircleHealthBar> barGengetsu = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().spawn(new DrawObject()
				{
					final Sprite bg = gengetsuSO.getObject().bg;
					
					{
						setFrameBuffer(scheme.getBossAura().getBackgroundBuffer());
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return bg;
							}
						}
						, 0f, 0.1f, 0.004F));
						setZIndex(-1);
						setShader(ShaderLibrary.WATER.getProgram());
					}
					
					@Override
					public void onDraw()
					{
						ShaderLibrary.WATER.getProgram().setUniformf("time", Game.getGame().getTick() / 10 % 30 - 15);
						
						bg.setPosition(0, 400);
						bg.setSize(Game.getGame().getWidth(), 460);
						bg.setColor(0.5f, 0.5f, 0.5f, bg.getColor().a);
						bg.draw(Game.getGame().batch);
					}
					
					@Override
					public boolean isPersistant()
					{
						return gengetsuSO.getObject().isOnStage();
					}
				});
				
				Game.getGame().spawn(new DrawObject()
				{
					private Texture black = ImageUtil.makeDummyTexture(Color.BLACK, 1, 1);
					private Sprite bg = mugetsu.bg;
					
					{
						setFrameBuffer(scheme.getBossAura().getBackgroundBuffer());
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return bg;
							}
						}
						, 0.01F));
						setZIndex(-2);
						addDisposable(black);
					}
					
					@Override
					public void onDraw()
					{
						Game.getGame().batch.draw(black, 0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						
						bg.setPosition(0, 400);
						bg.setSize(Game.getGame().getWidth(), 460);
						bg.draw(Game.getGame().batch);
					}
					
					@Override
					public boolean isPersistant()
					{
						return mugetsu.isOnStage();
					}
				});
				
				BossUtil.cloudEntrance(mugetsu, Color.BLUE, Color.PINK, 30);
				
				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						BossUtil.cloudEntrance(gengetsu, Color.RED, Color.PINK, 30);
					}
				}, 30);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						barMugetsu.setObject(new CircleHealthBar(mugetsu));
						barGengetsu.setObject(new CircleHealthBar(gengetsu));
						
						g.spawn(mugetsu);
						
						g.spawn(barMugetsu.getObject());
						
//						scheme.getBossAura().setAura(0, new Getter<IPosition>()
//						{
//							@Override
//							public IPosition get()
//							{
//								return mugetsu;
//							}
//						});
						
						barMugetsu.getObject().addSplit(0.8f);
						
						AllStarUtil.introduce(mugetsu, gengetsu);
						
						mugetsu.healUp();
					}
				}, 60);
				
				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						barGengetsu.setObject(new CircleHealthBar(gengetsu));
						
						g.spawn(gengetsu);
						
						g.spawn(barGengetsu.getObject());
						
//						scheme.getBossAura().setAura(1, new Getter<IPosition>()
//						{
//							@Override
//							public IPosition get()
//							{
//								return gengetsu;
//							}
//						});
						
						barGengetsu.getObject().addSplit(0.8f);
						
						gengetsu.healUp();
						
						Game.getGame().startSpellCard(new GetsusNonSpell(mugetsu, gengetsu));
					}
				}, 90);
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !gengetsu.isOnStage();
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
					return !gengetsu.isDead();
				}
				catch(Exception e)
				{
					return false;
				}
			}
		});
		
		scheme.doWait();
		
		barMugetsu.getObject().split();
		barGengetsu.getObject().split();
		
		mugetsu.setHealth(mugetsu.getMaxHealth());
		gengetsu.setHealth(gengetsu.getMaxHealth());
		
		gengetsu.lastTickHealth = gengetsu.getMaxHealth();
		gengetsu.lastTickHealthSister = mugetsu.getMaxHealth();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				
				if(!barMugetsu.getObject().isOnStage())
					Game.getGame().spawn(barMugetsu.getObject());
				
				if(!barGengetsu.getObject().isOnStage())
					Game.getGame().spawn(barGengetsu.getObject());
				
				AllStarUtil.presentSpellCard(mugetsu, gengetsu, "Memories of a Nightmare - \"Galactic Storm\"");
				
				Game.getGame().startSpellCard(new GetsusSpell(mugetsu, gengetsu));
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				try
				{
					return !gengetsu.isDead();
				}
				catch(Exception e)
				{
					return true;
				}
			}
		});
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(mugetsu);
				Game.getGame().delete(gengetsu);
				
				Game.getGame().clearSpellcards();
				Game.getGame().clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(mugetsu.getX(), mugetsu.getY());
				BossUtil.mapleExplosion(gengetsu.getX(), gengetsu.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static void rapeTimeWarning()
	{
		final Texture texture = Loader.texture(Gdx.files.internal("enemy/gengetsu/rapewarning.png"));
		final Sound sound = Gdx.audio.newSound(Gdx.files.internal("enemy/gengetsu/rapewhistle.mp3"));
		
		final Sprite whistle = new Sprite(texture, 45, 0, 168, 128);
		
		whistle.setScale(2f);
		
		final Sprite line = new Sprite(texture, 0, 220, 256, 38);
		line.setOrigin(0, line.getHeight() / 2f);
		line.setScale(3f);
		
		TouhouSounds.Stage.WARNING_1.play(0.5f);
		sound.play();
		
		DrawObject draw = new DrawObject()
		{
			float xWhistle = 0;
			float xLine = 0;
			
			@Override
			public void onDraw()
			{
				whistle.setPosition(xWhistle, Game.getGame().getHeight()/2f);
				whistle.draw(Game.getGame().batch);
				
				whistle.setPosition(xWhistle + Game.getGame().getWidth()/2f, Game.getGame().getHeight()/2f);
				whistle.draw(Game.getGame().batch);
				
				whistle.setPosition(xWhistle + Game.getGame().getWidth(), Game.getGame().getHeight()/2f);
				whistle.draw(Game.getGame().batch);
				
				whistle.setPosition(xWhistle + Game.getGame().getWidth() + Game.getGame().getWidth()/2f, Game.getGame().getHeight()/2f);
				whistle.draw(Game.getGame().batch);
				
				line.setPosition(xLine - line.getWidth() * line.getScaleX(), Game.getGame().getHeight()/2f + 220f);
				line.draw(Game.getGame().batch);
				
				line.setPosition(xLine, Game.getGame().getHeight()/2f + 220f);
				line.draw(Game.getGame().batch);
				
				line.setPosition(xLine + line.getWidth() * line.getScaleX(), Game.getGame().getHeight()/2f + 220f);
				line.draw(Game.getGame().batch);
				
				line.setPosition(xLine - line.getWidth() * line.getScaleX(), Game.getGame().getHeight()/2f - 150f);
				line.draw(Game.getGame().batch);
				
				line.setPosition(xLine, Game.getGame().getHeight()/2f - 150f);
				line.draw(Game.getGame().batch);
				
				line.setPosition(xLine + line.getWidth() * line.getScaleX(), Game.getGame().getHeight()/2f - 150f);
				line.draw(Game.getGame().batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				xWhistle -= 10;
				
				if(xWhistle < -Game.getGame().getWidth())
					xWhistle = 0;
				
				xLine += 5;
				
				if(xLine > line.getWidth() * line.getScaleX())
					xLine = 0;
				
				if(getTicksAlive() > 250)
					Game.getGame().delete(this);
			}
		};
		
		draw.addDisposable(texture);
		draw.addDisposable(sound);
		
		draw.setZIndex(J2hGame.GUI_Z_ORDER);
		
		Game.getGame().spawn(draw);
	}
	
	public static class GetsusNonSpell extends Spellcard
	{	
		Mugetsu mugetsu;
		Gengetsu gengetsu;
		
		public GetsusNonSpell(Mugetsu mugetsu, Gengetsu gengetsu)
		{
			super(mugetsu);
			
			this.mugetsu = mugetsu;
			this.gengetsu = gengetsu;
			
			mugetsu.setDamageModifier(0.5f);
			gengetsu.setDamageModifier(0.5f);
		}
		
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();

			float total = 930;

			float rapeTime = 2950;

			if(tick < rapeTime - 100)
			{
				if(tick % total == 300)
				{
					TouhouSounds.Enemy.HUM_1.play();
					
					BossUtil.moveAroundRandomly(gengetsu, 100, 1180, Game.getGame().getHeight() - 200, Game.getGame().getHeight() - 400, 1000);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == gengetsu)
								delete = chb;
						}

					final CircleHealthBar finalCHB = delete;

					if(finalCHB != null)
					{
						Game.getGame().getStageObjects().remove(finalCHB);

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								finalCHB.setX(gengetsu.getX());
								finalCHB.setY(gengetsu.getY());
								
								Game.getGame().spawn(finalCHB);
							}
						}, 60);
					}
				}
				
				if(tick % total == 400 + 190)
					TouhouSounds.Enemy.EXPLOSION_1.play();
				
				if(tick % total == 400 + 240)
					TouhouSounds.Enemy.SLASH.play();
				
				if(tick % total >= 630 && tick % total < 700 && tick % 20 == 0)
				{
					LaserAnimation ani = new LaserAnimation(1, new ThBullet(ThBulletType.LAZER_STATIONARY, ThBulletColor.RED).getAnimation().getKeyFrames()[0].getTexture());
					
					StationaryLaser laser = new StationaryLaser(ani, 0, 0)
					{
						boolean hitbox = false;
						float timer = 1;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getTicksAlive() < 60)
							{
								if(timer < 14)
								{
									timer *= 1.05f;

									setThickness(timer);
									setHitboxThickness(timer / 2f);
								}

								if(!hitbox && timer > 8)
									hitbox = true;
							}
							else if(getTicksAlive() > 60)
								if(timer > 0)
								{
									timer -= 0.2f;

									setThickness(timer);
									setHitboxThickness(timer / 2f);
								}
								else
									Game.getGame().delete(this);
						}
						
						@Override
						public void checkCollision()
						{
							if(hitbox)
								super.checkCollision();
						}
					};
					
					laser.setZIndex(mugetsu.getZIndex() - 1);
					
					TouhouSounds.Enemy.LAZER_2.play();
					
					laser.setStart(mugetsu.getX(), mugetsu.getY());
					laser.setEnd((float)(mugetsu.getX() - Math.cos(Math.toRadians(MathUtil.getAngle(mugetsu, player))) * 2000), (float) (mugetsu.getY() - Math.sin(Math.toRadians(MathUtil.getAngle(mugetsu, player))) * 2000));
				
					Game.getGame().spawn(laser);
				}
				
				if(tick % total == 450)
					gengetsu.setHandLeft();
				
				if(tick % total == 700)
					gengetsu.setHandDown();

				if(tick % total == 450)
				{
					gengetsu.playSpecial(true);

					float x = gengetsu.getX() - 135;
					float y = gengetsu.getY() + 135;

					final float radius = 100;
					final int amount = 600;
					
					for(int i = 0; i < amount; i++)
					{
						final float rad = (float) Math.toRadians(Math.random() * 360);

						float handX = gengetsu.getX() - 50;
						float handY = gengetsu.getY() + 50;

						final float bulX = (float) (x + Math.cos(rad) * (Math.random() * radius));
						final float bulY = (float) (y + Math.sin(rad) * (Math.random() * radius));

						final float distance = MathUtil.getDistance(x, y, bulX, bulY);

						if(i % 9 <= 2)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), handX, handY)
							{
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(getTicksAlive() < 130)
									{
										float angle = MathUtil.getAngle(getX(), getY(), bulX, bulY);
										float distance = MathUtil.getDistance(getX(), getY(), bulX, bulY);

										float speed = distance / (180 - getTicksAlive());

										setDirectionDegTick(angle, speed);
										setRotationFromVelocity(-90f);
									}

									if(getTicksAlive() == 130)
										setDirectionDegTick(0, 0);

									if(getTicksAlive() == 140)
									{
										setDirectionRadsTick(-rad, 6f * (distance / radius) * 2f);
										setRotationFromVelocity(-90f);
									}
								}
							};

							bullet.setZIndex(bullet.getZIndex() + 2);

							Game.getGame().spawn(bullet);
						}
						else if(i % 9 > 3 && i % 9 <= 6)
						{
							final int finalI = i;
							
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.STAR_LARGE, ThBulletColor.YELLOW), handX, handY)
							{
								float originalDeg = (float)finalI / (float)amount * 360;
								float deg = originalDeg;

								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(getTicksAlive() < 130)
									{
										float angle = MathUtil.getAngle(getX(), getY(), bulX, bulY);
										float distance = MathUtil.getDistance(getX(), getY(), bulX, bulY);

										float speed = distance / (180 - getTicksAlive());

										setDirectionDegTick(angle, speed);
										setRotationFromVelocity(-90f);
									}

									if(getTicksAlive() == 130)
										setDirectionDegTick(0, 0);

									if(getTicksAlive() > 220)
									{
										if(deg - originalDeg < 180)
											deg += 1f;

										setDirectionDegTick(deg, (8f + (float)finalI / (float)amount * 360 % 20 / 20 * 4f) * 0.8f);
										setRotationFromVelocity(-90f);
									}
								}
							};

							bullet.setScale(0.5f);
							bullet.setZIndex(bullet.getZIndex() + 1);

							Game.getGame().spawn(bullet);
						}
						else if(i % 9 >= 7)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.UNKNOWN_3, ThBulletColor.RED), handX, handY)
							{
								float deg = (float)Math.toDegrees(rad);
								float speed = 5f;

								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(getTicksAlive() < 130)
									{
										float angle = MathUtil.getAngle(getX(), getY(), bulX, bulY);
										float distance = MathUtil.getDistance(getX(), getY(), bulX, bulY);

										float speed = distance / (180 - getTicksAlive());

										setDirectionDegTick(angle, speed);
										setRotationFromVelocity(90f);
									}

									if(getTicksAlive() == 130)
										setDirectionDegTick(0, 0);

									if(getTicksAlive() == 250)
									{
										deg = (float) (60 + Math.random() * 45);
										setDirectionDegTick(deg, speed);
										setRotationFromVelocity(-90f);
										speed += 6f;
									}

									if(!Game.getGame().inBoundary(getX(), getY()))
									{
										setDirectionDegTick(-deg, speed);
										setRotationFromVelocity(-90f);
										speed += 6f;

										if(speed > 45)
											Game.getGame().delete(this);
										
										if(!Scheduler.isTracked("bouncingBullet", "bouncingBullet"))
										{
											TouhouSounds.Enemy.BULLET_3.play();
											Scheduler.track("bouncingBullet", "bouncingBullet", (long) 10);
										}

										deg = -deg;
									}
								}
							};

							bullet.setZIndex(bullet.getZIndex() + 3);

							Game.getGame().spawn(bullet);
						}
					}
				}

				if(tick % total == 60)
				{
					TouhouSounds.Enemy.HUM_1.play();
					
					BossUtil.moveTo(mugetsu, mugetsu.getX() + 1, mugetsu.getY() + 10, 1000);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.moveAroundRandomly(mugetsu, 100, 1180, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 500);
						}
					}, 50);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == mugetsu)
								delete = chb;
						}

					final CircleHealthBar finalCHB = delete;

					if(finalCHB != null)
					{
						Game.getGame().getStageObjects().remove(finalCHB);

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								Game.getGame().spawn(finalCHB);
								finalCHB.onUpdate(game.getTick());
							}
						}, 100);
					}
				}
				
				if(tick % total > 140 && tick % total < 280 && tick % 8 == 0)
					TouhouSounds.Enemy.BREAK_1.play();
				
				if(tick % total == 300)
					TouhouSounds.Enemy.RELEASE_2.play();
				{
					boolean first = tick % total == 220;
					boolean last = tick % total == 260;
					
				if(first || last)
				{
					if(first)
						gengetsu.setHandUp();
					else if(last)
						gengetsu.setHandDown();
					
					for(int i = 0; i < 720; i += 5)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.BLUE), gengetsu.getX(), gengetsu.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
							}
						};
						
						bullet.setDirectionDegTick(i, 3f);

						Game.getGame().spawn(bullet);
					}
				}
				}

				if(tick % total == 140)
				{
					boolean close = false;

					for(float i = -90; i <= 270; i += close ? 1 : 4f + MathUtil.getDifference(i, 90) / 90f * 1.5f)
					{
						double max = MathUtil.getDifference(180, 90);

						for(int nr = 5; nr <= 30; nr++)
						{
							final int finalNr = nr;
							final float finalI = i;

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE, ThBulletColor.BLUE), mugetsu.getX(), mugetsu.getY());

									bullet.getCurrentSprite().setScale(0.5f, 1f);

									if(finalI < 90)
										bullet.setDirectionDegTick((float) (finalI + MathUtil.getDifference(finalI, 90) * 1.5f * finalNr / 90f), 0.5f + finalNr / 3f);
									else
										bullet.setDirectionDegTick((float) (finalI - MathUtil.getDifference(finalI, 90) * 1.5f * finalNr / 90f), 0.5f + finalNr / 3f);

									bullet.setRotationFromVelocity();

									Game.getGame().spawn(bullet);
								}
							}, (int) ((max - MathUtil.getDifference(close ? i - 1 : i, 90)) * 1.5f));
						}

						close = !close;
					}

					for(int i = 0; i < 720; i += 6)
					{
						final int finalI = i;

						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.YELLOW), mugetsu.getX(), mugetsu.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								if(getTicksAlive() == 100)
									setDirectionDegTick(finalI > 360 ? -finalI : finalI, 2f);

								if(getTicksAlive() == 200)
									setDirectionDegTick(finalI > 360 ? -(finalI + 90f) : finalI + 80f, 6f);	
							}
						};

						Game.getGame().spawn(bullet);
					}
				}
			}

			if(tick == rapeTime - 100)
			{
				game.clear(ClearType.BULLETS);
				rapeTimeWarning();
			}

			// RAPE TIME >:D
			if(tick > rapeTime)
			{
				if(tick % 300 == 0)
				{
					BossUtil.moveAroundRandomly(gengetsu, 100, 1180, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 1000);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == gengetsu)
								delete = chb;
						}

					final CircleHealthBar finalCHB = delete;

					if(finalCHB != null)
					{
						Game.getGame().getStageObjects().remove(finalCHB);

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								Game.getGame().spawn(finalCHB);
							}
						}, 60);
					}
				}

				if(tick % 16 == 0)
				{
					final Sprite muS = mugetsu.getCurrentSprite();
					final Sprite genS = gengetsu.getCurrentSprite();
					
					muS.setColor(Color.RED);
					genS.setColor(Color.RED);
					
					Game.getGame().spawn(new UpdateObject()
					{
						@Override
						public void onUpdate(long tick)
						{
							if(getTicksAlive() == 8)
							{
								muS.setColor(Color.WHITE);
								genS.setColor(Color.WHITE);
								Game.getGame().delete(this);
							}
						}

						@Override
						public boolean isPersistant()
						{
							return true;
						}
					});
				}
				
				if(tick % 2 == 0 && tick % 100 < 90)
					TouhouSounds.Enemy.BULLET_3.play();

				if(tick % 4 == 0 && tick % 100 < 90)
				{
					float step = 15f;
					float offset = (float) (Math.random() * step);

					for(float i = offset; i < 360 + offset; i += step)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.HEART, ThBulletColor.BLUE), mugetsu.getX(), mugetsu.getY())
						{

						};

						bullet.getCurrentSprite().setScale(0.3f, 1f);
						bullet.setDirectionDegTick(i, 14f);
						bullet.setRotationFromVelocity(-90f);

						Game.getGame().spawn(bullet);
					}
					
					if(tick % 100 == 0)
						gengetsu.setHandUp();
					
					if(tick % 100 == 40)
						gengetsu.setHandDown();

					if(tick % 100 < 40 && tick % 8 == 0)
						for(float i = 0; i < 360 + 0; i += 20)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), gengetsu.getX(), gengetsu.getY())
							{
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(getTicksAlive() == 60)
									{
										setDirectionDegTick(MathUtil.getAngle(this, Game.getGame().getPlayer()), 30f);
										setRotationFromVelocity(-90f);
									}
								}
							};

							//					bullet.getCurrentSprite().setScale(0.3f, 1f);
							bullet.setDirectionDegTick(i, 1f + tick % 100 / 10f);
							bullet.setRotationFromVelocity(-90f);

							Game.getGame().spawn(bullet);
						}
				}
			}
		}
	}
	
	public static class GetsusSpellOld extends Spellcard
	{
		Mugetsu mugetsu;
		Gengetsu gengetsu;
		Random rand = new Random();
		ArrayList<Position> marks = new ArrayList<Position>();
		
		public GetsusSpellOld(Mugetsu mugetsu, Gengetsu gengetsu)
		{
			super(mugetsu);

			this.mugetsu = mugetsu;
			this.gengetsu = gengetsu;
			
			gengetsu.setHandUp();
			
			mugetsu.setDamageModifier(1.1f);
			gengetsu.setDamageModifier(1.1f);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();

			int rapeTimeTime = 1400;
			final boolean rapeTime = tick > rapeTimeTime;
			
			if(tick == rapeTimeTime - 100)
			{
				rapeTimeWarning();
				
				game.clear(ClearType.BULLETS);
				
				{
					BossUtil.moveTo(gengetsu, gengetsu.getX() + 1, gengetsu.getY() + 10, 1000);

					TouhouSounds.Enemy.HUM_1.play();

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.moveTo(gengetsu, Game.getGame().getWidth()/2f + 60, 800, 1000);
						}
					}, 50);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == gengetsu)
								delete = chb;
						}
					
					Game.getGame().delete(delete);
				}

				{
					BossUtil.moveTo(mugetsu, mugetsu.getX() + 1, mugetsu.getY() + 10, 1000);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.moveTo(mugetsu, Game.getGame().getWidth()/2 - 60, 800, 1000);
						}
					}, 50);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == mugetsu)
								delete = chb;
						}
					
					Game.getGame().delete(delete);
				}
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						CircleHealthBar hb = new CircleHealthBar(gengetsu)
						{
							@Override
							public void setPositionUpdate()
							{
								setX(Game.getGame().getWidth()/2);
								setY(800);
							}
						};
						hb.addSplit(0.8f);
						hb.split();
						hb.setRadius(150);
						
						Game.getGame().spawn(hb);
						hb.onUpdate(Game.getGame().getTick());
					}
				}, 120);
			}

			if(!rapeTime)
			{
				if(tick >= rapeTimeTime - 100)
					return;
				
				if(tick == 0)
				{
					{
						TouhouSounds.Enemy.HUM_1.play();

						BossUtil.moveTo(gengetsu, Game.getGame().getWidth()/2f + 200, 800, 2000);

						CircleHealthBar delete = null;

						for(StageObject obj : Game.getGame().getStageObjects())
							if(obj instanceof CircleHealthBar)
							{
								CircleHealthBar chb = (CircleHealthBar) obj;

								if(chb.getOwner() == gengetsu)
									delete = chb;
							}

						final CircleHealthBar finalCHB = delete;

						if(finalCHB != null)
						{
							Game.getGame().getStageObjects().remove(finalCHB);

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().spawn(finalCHB);
									finalCHB.onUpdate(Game.getGame().getTick());
								}
							}, 60);
						}
					}

					{
						BossUtil.moveTo(mugetsu, mugetsu.getX() + 1, mugetsu.getY() + 10, 1000);

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								BossUtil.moveTo(mugetsu, Game.getGame().getWidth()/2 - 200, 800, 1000);
							}
						}, 50);

						CircleHealthBar delete = null;

						for(StageObject obj : Game.getGame().getStageObjects())
							if(obj instanceof CircleHealthBar)
							{
								CircleHealthBar chb = (CircleHealthBar) obj;

								if(chb.getOwner() == mugetsu)
									delete = chb;
							}

						final CircleHealthBar finalCHB = delete;

						if(finalCHB != null)
						{
							Game.getGame().getStageObjects().remove(finalCHB);

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().spawn(finalCHB);
									finalCHB.onUpdate(Game.getGame().getTick());
								}
							}, 120);
						}
					}
				}

				if(tick < 100)
					return;
				
				tick -= 100;

				if(tick % 10 == 0)
				{
					if(!marks.isEmpty())
						TouhouSounds.Enemy.ICE_1.play();

					if(marks.size() > 20)
						while(marks.size() > 20)
							marks.remove(0);

					for(final Position pos : marks)
					{
						int[] delays = { 10 };
						final float[] offsets = { 0 };

						int i = 0;

						for(int delay : delays)
						{
							final int finalI = i;

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.HEART, ThBulletColor.RED), mugetsu.getX(), mugetsu.getY());

									bullet.setDirectionDegTick(MathUtil.getAngle(mugetsu, pos) + offsets[finalI], 20f);
									bullet.setRotationFromVelocity(-90f);
									bullet.useSpawnAnimation(false);

									Game.getGame().spawn(bullet);
								}
							}, delay);

							i++;
						}

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								marks.remove(pos);
							}
						}, 300);
					}
				}

				if(tick % 1 == 0)
					if(player.getY() > 500)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_LARGE_HOLLOW, ThBulletColor.RED), mugetsu.getX(), mugetsu.getY());

						bullet.setDirectionDegTick(MathUtil.getAngle(mugetsu, player), 40f);
						bullet.setRotationFromVelocity(-90f);
						bullet.useSpawnAnimation(false);

						Game.getGame().spawn(bullet);

						bullet = new Bullet(new ThBullet(ThBulletType.BALL_LARGE_HOLLOW, ThBulletColor.RED), mugetsu.getX(), mugetsu.getY());

						bullet.setDirectionDegTick(MathUtil.getAngle(mugetsu, player) - 5, 40f);
						bullet.setRotationFromVelocity(-90f);
						bullet.useSpawnAnimation(false);

						Game.getGame().spawn(bullet);

						bullet = new Bullet(new ThBullet(ThBulletType.BALL_LARGE_HOLLOW, ThBulletColor.RED), mugetsu.getX(), mugetsu.getY());

						bullet.setDirectionDegTick(MathUtil.getAngle(mugetsu, player) + 5, 40f);
						bullet.setRotationFromVelocity(-90f);
						bullet.useSpawnAnimation(false);

						Game.getGame().spawn(bullet);
					}

				if(tick % (6 * 40) < 4 * 40 && tick % 2 == 0)
					TouhouSounds.Enemy.BULLET_3.play();

				if(tick % (6 * 40) < 4 * 40 && tick % 40 == 0)
				{
					final float x = gengetsu.getX() - 25;// + (float) (Math.cos(Math.toRadians(selected)) * 300);
					final float y = gengetsu.getY() + 100;// + (float) (Math.sin(Math.toRadians(selected)) * 50);

					final float[] chances = { 0.25f, 0.2f, 0.15f, 0.1f };

					final int layerCount = 4;

					for(int layer = 0; layer < layerCount; layer++)
					{
						final int finalLayer = layer;
						final float step = 1f;
						float offset = 90f;

						final HashMap<Float, Double> randoms = new HashMap<Float, Double>();
						
						final float finalOffset = offset;
						final float offsetAngle = (float) (Math.random() * 12f);
						
						for(float i = offsetAngle; i < 2 * 360 + offsetAngle; i += 11)
						{
							final float finalI = i;
							
							int timer = (int) i;
							
							int fullRotations = timer / 360;
							
							int partial = timer % 360;
							
							if(partial > 180)
								partial = 360 - partial;
							
							timer = fullRotations * 360 + partial;
							
							if(fullRotations % 2 == 1)
								timer = (fullRotations + 1) * 360 - timer;

							timer = (int) (timer / 360f * 40f);
							
							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									float offset = finalOffset + finalI;

//										Bullet bullet = new Bullet(TouhouBulletType.CRYSTAL, TouhouBulletColor.RED, x, y);
//										bullet.setDirectionDeg(offset, 5f);
//										Game.getGame().spawn(bullet);

									for(float angle = 0; angle < 10; angle += step)
									{
										final float finalAngle = angle;
										final float finalOffset = offset;

										if(!randoms.containsKey(angle))
											randoms.put(angle, Math.random());

										double random = randoms.get(angle);

										if(random > chances[layerCount - 1 - finalLayer])
											continue;

										Runnable run = new Runnable()
										{
											@Override
											public void run()
											{
												boolean[] bools = { true, false };

												for(boolean bool : bools)
												{
													Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, ThBulletColor.BLUE), x, y)
													{
														boolean pointer = false;
														Position pos;

														@Override
														public void checkCollision()
														{
															J2hGame g = Game.getGame();

															if(getHitbox() == null || pointer)
																return;

															if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getHitbox()))
															{
																TouhouSounds.Enemy.SPAWN.play();

																setBullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.RED));
																setDirectionDegTick(0, 0);
																setRotationDeg(180);
																setZIndex(10000);
																pointer = true;
																pos = new Position(getX(), getY());

																marks.add(pos);
															}
														}

														@Override
														public void onUpdate(long tick)
														{
															super.onUpdate(tick);

															if(pointer && !marks.contains(pos) || rapeTime)
																Game.getGame().delete(this);
														}
													};

													bullet.setDirectionDegTick(finalOffset + offsetAngle + (bool ? -1 : 1) * finalAngle, 4f + finalLayer * 0.5f);
													bullet.setRotationFromVelocity();
													
													Game.getGame().spawn(bullet);
												}
											}
										};

										Game.getGame().addTaskGame(run, (int) (angle / 180 * 3f) + finalLayer * 10);
									};

								}
							}, timer);
						}
					}



							//				if(tick % 4 == 0)
							//				{
							//					int gaps = 40;
							//					float selected = rand.nextInt(gaps) * (360f / gaps);
							//
							//					{
							//						final float x = (Game.getGame().getWidth()/2f) + (float) (Math.cos(Math.toRadians(selected)) * 300);
							//						final float y = 900 + (float) (Math.sin(Math.toRadians(selected)) * 50);
							//
							//						{
							//							final float step = 2f;
							//							final float offset = (float) (Math.random() * (float) (step));
							//
							//							boolean cut = false;
							//							
							//							for(float angle = offset; angle < 360 + offset; angle += (cut ? (8 * step) : step))
							//							{
							//								if(angle % (8 * step) < step)
							//									cut = Math.random() > (!cut ? 0f : 0.2f);
							//									
							//								if(cut)
							//									continue;
							//								
							//								final float finalAngle = angle;
							//								
							//								Runnable run = new Runnable()
							//								{
							//									public void run()
							//									{
							//										Bullet bullet = new Bullet(TouhouBulletType.DOT_SMALL_MOON, TouhouBulletColor.BLUE, x, y)
							//										{
							//											boolean pointer = false;
							//											Position pos;
							//
							//											public void checkCollision()
							//											{
							//												Java2huGame g = Game.getGame();
							//
							//												if(getHitbox() == null || pointer)
							//													return;
							//
							//												if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getHitbox()))
							//												{
							//													TouhouSounds.Enemy.SPAWN.play();
							//
							//													setBullet(TouhouBulletType.POINTER, TouhouBulletColor.RED);
							//													setDirectionDeg(0, 0);
							//													setRotationDegree(180);
							//													setZIndex(10000);
							//													pointer = true;
							//													pos = new Position(getX(), getY());
							//
							//													marks.add(pos);
							//												}
							//											}
							//
							//											@Override
							//											public void onUpdate(long tick)
							//											{
							//												super.onUpdate(tick);
							//
							//												if(pointer && !marks.contains(pos) || rapeTime)
							//												{
							//													Game.getGame().delete(this);
							//												}
							//											}
							//										};
							//
							//										bullet.setDirectionDeg(finalAngle, 5f);
							//
							//										Game.getGame().spawn(bullet);
							//									}
							//								};
							//								
							//								Game.getGame().addTaskGame(run, (int) (angle / 3f));
							//							};
							//						}
							//						else
							//						{
							//							float step = 30;
							//							float offset = (float) (Math.random() * step);
							//
							//							for(float angle = offset; angle < 360 + offset; angle += step)
							//							{
							//								Bullet bullet = new Bullet(TouhouBulletType.DOT_SMALL_MOON, TouhouBulletColor.BLUE, x, y)
							//								{
							//									boolean pointer = false;
							//									Position pos;
							//
							//									public void checkCollision()
							//									{
							//										Java2huGame g = Game.getGame();
							//
							//										if(getHitbox() == null)
							//											return;
							//
							//										if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getHitbox()))
							//										{
							//											setBullet(TouhouBulletType.POINTER, TouhouBulletColor.RED);
							//											setDirectionDeg(0, 0);
							//											setRotationDegree(180);
							//											setZIndex(10000);
							//											pointer = true;
							//											pos = new Position(getX(), getY());
							//
							//											marks.add(pos);
							//										}
							//									}
							//
							//									@Override
							//									public void onUpdate(long tick)
							//									{
							//										super.onUpdate(tick);
							//
							//										if(pointer && !marks.contains(pos))
							//										{
							//											Game.getGame().delete(this);
							//										}
							//									}
							//								};
							//
							//								bullet.useSpawnAnimation(false);
							//								bullet.getCurrentSprite().setScale(1.7f);
							//								bullet.setDirectionDeg(angle, 12f);
							//
							//								Game.getGame().spawn(bullet);
							//							}
							//						}
				}
			}
			else
			{
				if(tick % 8 == 0)
				{
					mugetsu.getCurrentSprite().setColor(Color.RED);
					gengetsu.getCurrentSprite().setColor(Color.RED);
				}

				if(tick % 8 == 4)
				{
					mugetsu.getCurrentSprite().setColor(Color.WHITE);
					gengetsu.getCurrentSprite().setColor(Color.WHITE);
				}
				
				if(tick % 9 == 0)
					TouhouSounds.Enemy.BULLET_3.play();

				if(tick % 5 == 0)
				{
					Boss caster;
					
					if(tick % 6 <= 2)
						caster = gengetsu;
					else
						caster = mugetsu;
					
					float[] posX = { -50, -50,  50, 50,  50, -50 };
					float[] posY = { -50,   0,  50,  0, -50,  50 };

					int selected = rand.nextInt(posX.length);

					{
						float x = caster.getX() + posX[selected];
						float y = caster.getY() + posY[selected];

						boolean ball = tick % 16 < 4; 

						{
							float step = 20;
							float offset = tick % (2 *step) - step;;

							for(float angle = offset; angle < 360 + offset; angle += step)
							{
								Bullet bullet = new Bullet(new ThBullet(ball ? ThBulletType.BALL_BIG : ThBulletType.DOT_SMALL_MOON, ThBulletColor.RED), x, y);

								bullet.useSpawnAnimation(false);
								bullet.getCurrentSprite().setScale(ball ? 1f : 1.7f);
								bullet.setDirectionDegTick(angle, 14f);

								Game.getGame().spawn(bullet);
							}
						}
					}
				}
			}
		}
	}

	public static class GetsusSpell extends Spellcard
	{
		Mugetsu mugetsu;
		Gengetsu gengetsu;
		Random rand = new Random();
		ArrayList<Position> marks = new ArrayList<Position>();
		
		public GetsusSpell(Mugetsu mugetsu, Gengetsu gengetsu)
		{
			super(mugetsu);

			this.mugetsu = mugetsu;
			this.gengetsu = gengetsu;
			
			gengetsu.setHandUp();
			
			mugetsu.setDamageModifier(1.1f);
			gengetsu.setDamageModifier(1.1f);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();

			int rapeTimeTime = 1400;
			final boolean rapeTime = tick > rapeTimeTime;
			
			if(tick == rapeTimeTime - 100)
			{
				rapeTimeWarning();
				
				game.clear(ClearType.BULLETS);
				
				{
					BossUtil.moveTo(gengetsu, gengetsu.getX() + 1, gengetsu.getY() + 10, 1000);

					TouhouSounds.Enemy.HUM_1.play();

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.moveTo(gengetsu, Game.getGame().getWidth()/2f + 60, 800, 1000);
						}
					}, 50);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == gengetsu)
								delete = chb;
						}
					
					Game.getGame().delete(delete);
				}

				{
					BossUtil.moveTo(mugetsu, mugetsu.getX() + 1, mugetsu.getY() + 10, 1000);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.moveTo(mugetsu, Game.getGame().getWidth()/2 - 60, 800, 1000);
						}
					}, 50);

					CircleHealthBar delete = null;

					for(StageObject obj : Game.getGame().getStageObjects())
						if(obj instanceof CircleHealthBar)
						{
							CircleHealthBar chb = (CircleHealthBar) obj;

							if(chb.getOwner() == mugetsu)
								delete = chb;
						}
					
					Game.getGame().delete(delete);
				}
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						CircleHealthBar hb = new CircleHealthBar(gengetsu)
						{
							@Override
							public void setPositionUpdate()
							{
								setX(Game.getGame().getWidth()/2);
								setY(800);
							}
						};
						hb.addSplit(0.8f);
						hb.split();
						hb.setRadius(150);
						
						Game.getGame().spawn(hb);
						hb.onUpdate(Game.getGame().getTick());
					}
				}, 120);
			}

			if(!rapeTime)
			{
				if(tick >= rapeTimeTime - 100)
					return;
				
				if(tick == 0)
				{
					{
						TouhouSounds.Enemy.HUM_1.play();

						BossUtil.moveTo(gengetsu, Game.getGame().getWidth()/2f + 200, 800, 2000);

						CircleHealthBar delete = null;

						for(StageObject obj : Game.getGame().getStageObjects())
							if(obj instanceof CircleHealthBar)
							{
								CircleHealthBar chb = (CircleHealthBar) obj;

								if(chb.getOwner() == gengetsu)
									delete = chb;
							}

						final CircleHealthBar finalCHB = delete;

						if(finalCHB != null)
						{
							Game.getGame().getStageObjects().remove(finalCHB);

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().spawn(finalCHB);
									finalCHB.onUpdate(Game.getGame().getTick());
								}
							}, 60);
						}
					}

					{
						BossUtil.moveTo(mugetsu, mugetsu.getX() + 1, mugetsu.getY() + 10, 1000);

						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								BossUtil.moveTo(mugetsu, Game.getGame().getWidth()/2 - 200, 800, 1000);
							}
						}, 50);

						CircleHealthBar delete = null;

						for(StageObject obj : Game.getGame().getStageObjects())
							if(obj instanceof CircleHealthBar)
							{
								CircleHealthBar chb = (CircleHealthBar) obj;

								if(chb.getOwner() == mugetsu)
									delete = chb;
							}

						final CircleHealthBar finalCHB = delete;

						if(finalCHB != null)
						{
							Game.getGame().getStageObjects().remove(finalCHB);

							Game.getGame().addTaskGame(new Runnable()
							{
								@Override
								public void run()
								{
									Game.getGame().spawn(finalCHB);
									finalCHB.onUpdate(Game.getGame().getTick());
								}
							}, 120);
						}
					}
				}

				if(tick < 100)
					return;
				
				tick -= 100;
				
				if(tick % 130 >= 0 && tick % 130 <= 10 && tick % 2 == 0)
				{
					float playerAngle = MathUtil.getAngle(mugetsu, player);
					
					final String id = "kunaiRelease";
					
					if(!Scheduler.isTracked(id, id))
					{
						TouhouSounds.Enemy.RELEASE_1.play();
						
						Scheduler.track(id, id, 20L);
					}
					
					for(float angle = -60; angle <= 60; angle += 5f)
					{
						Bullet knife = new Bullet(new ThBullet(ThBulletType.KUNAI, ThBulletColor.BLUE), mugetsu.getX(), mugetsu.getY());
						knife.setDirectionDeg(angle + playerAngle, 200);
						knife.setRotationFromVelocity(-90);
						
						final float finalAngle = angle;
						
						knife.addEffect(new Plugin<Bullet>()
						{
							@Override
							public void update(Bullet object, long tick)
							{								
								float mul = finalAngle > 0 ? 1 : -1;
								int add = (int) (MathUtil.getDifference(finalAngle, 0) / 3f);
								
								if(object.getTicksAlive() == 90 + add)
								{
									if(!Scheduler.isTracked(id, id))
									{
										TouhouSounds.Enemy.RELEASE_1.play();
										
										Scheduler.track(id, id, 20L);
									}
									
									float angle = MathUtil.getAngle(object, player);
									
									object.setDirectionDeg(angle + mul * 90, 300f);
									object.setRotationFromVelocity(-90);
								}
								
								if(object.getTicksAlive() == 150 + add)
								{
									if(!Scheduler.isTracked(id, id))
									{
										TouhouSounds.Enemy.RELEASE_1.play();
										
										Scheduler.track(id, id, 20L);
									}
									
									float angle = MathUtil.getAngle(object, player);
									float distance = MathUtil.getDistance(object, player);
									
									float meanDistance = 500;
									
									float distanceMod = Math.max(1, distance / meanDistance);
									
									object.setDirectionDeg(angle, 600f * distanceMod);
									object.setRotationFromVelocity(-90);
								}
							}
						});
						
						game.spawn(knife);
					}
				}
				
				if(tick % 200 >= 0 && tick % 200 < 40 && tick % 20 == 0)
				{
					TouhouSounds.Enemy.BULLET_2.play();
					
					final float playerAngle = MathUtil.getAngle(gengetsu, player);
					
					for(float angle = -90; angle <= 90; angle += 10)
					{
						final float finalAngle = playerAngle + angle;
						
						Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.RED_LIGHT), gengetsu.getX(), gengetsu.getY(), 300);
						laser.setDirectionDeg(finalAngle, 200f);
						laser.setMaxPoints(-1);
						laser.setUnitsPerPoint(-1);
						
						laser.addEffect(new Plugin<Laser>()
						{
							@Override
							public void update(Laser object, long tick)
							{
								if(object.getTicksAlive() >= 30 && object.getTicksAlive() <= 60)
								{
									float mul = (object.getTicksAlive() - 30f) / 30f;
									
									object.setDirectionDeg(finalAngle, 200 + mul * 1000f);
								}
							}
						});
						
						game.spawn(laser);
						
						Position edge = null;
						Border border = null;
						
						for(float t = 0; t < 100; t += 0.01f)
						{
							Position pos = new Position(laser);
							pos.add(new Position(-laser.getVelocityX() * t, -laser.getVelocityY() * t));
							
							if(!game.inBoundary(pos.getX(), pos.getY()))
							{
								edge = pos;

								if(pos.getX() < game.getMinX())
								{
									border = Border.LEFT;
								}
								else if(pos.getX() > game.getMaxX())
								{
									border = Border.RIGHT;
								}
								else if(pos.getY() < game.getMinY())
								{
									border = Border.BOT;
								}
								else
								{
									border = Border.TOP;
								}
								
								System.out.println(border);

								break;
							}
						}
						
						final Position finalEdge = edge;
						final Border finalBorder = border;
						final float playerY = game.getPlayer().getY();
						final boolean tryToAimAtPlayer = Math.random() > 0.6f;
						
						Runnable run = new Runnable()
						{
							@Override
							public void run()
							{
								String id = "lineBulletSound";
								
								if(!Scheduler.isTracked(id, id))
								{
									TouhouSounds.Enemy.BULLET_1.play();
									
									Scheduler.track(id, id, 5L);
								}

								float angle = finalBorder == Border.BOT ? -90 : finalBorder == Border.TOP ? 90 : finalBorder == Border.LEFT ? 180 : 0;
								
								boolean aimAtPlayer = finalAngle - playerAngle == 0 && tryToAimAtPlayer;
								
								if(aimAtPlayer) // This makes it so they can't just graze vertically continually
								{
									angle = 180;
									finalEdge.setY(playerY);
									finalEdge.setX(0);
								}
								
								final float rad = (float) Math.toRadians(angle);
								
								final Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.WHITE), (float) (finalEdge.getX() + Math.cos(rad) * 100), (float) (finalEdge.getY() + Math.sin(rad) * 100));
								
								bullet.setDirectionRads(rad, 1000f);
								bullet.setRotationFromVelocity();
								
								float lineLength = 1500;
								
								final IPosition start = new Position(bullet);
								final IPosition end = new Position((float) (bullet.getX() - Math.cos(rad) * lineLength), (float) (bullet.getY() - Math.sin(rad) * lineLength));
								
								DrawObject line = new DrawObject()
								{
									@Override
									public void onDraw()
									{
										game.batch.end();
										
										game.shape.begin(ShapeType.Line);
										
										Gdx.gl.glLineWidth(5f);
										
										game.shape.setColor(Color.WHITE);
										
										game.shape.line(start.getX(), start.getY(), end.getX(), end.getY());
										
										game.shape.end();
										
										game.batch.begin();
									}
								};
								
								line.setName("dirlaser");
								
								line.addEffect(new Plugin<StageObject>()
								{
									@Override
									public void update(StageObject object, long tick)
									{
										if(object.getTicksAlive() >= 60)
											game.delete(object);
									}
								});
								
								game.spawn(line);

								game.addTaskGame(new Runnable()
								{
									@Override
									public void run()
									{
										game.spawn(bullet);
									}
								}, 20);
							}
						};
						
						for(int i = 0; i < 40; i += 2)
						{
							game.addTaskGame(run, 80 + i);
						}
					}
				}
			}
			else
			{
				if(tick % 8 == 0)
				{
					mugetsu.getCurrentSprite().setColor(Color.RED);
					gengetsu.getCurrentSprite().setColor(Color.RED);
				}

				if(tick % 8 == 4)
				{
					mugetsu.getCurrentSprite().setColor(Color.WHITE);
					gengetsu.getCurrentSprite().setColor(Color.WHITE);
				}
				
				if(tick % 9 == 0)
					TouhouSounds.Enemy.BULLET_3.play();

				if(tick % 5 == 0)
				{
					Boss caster;
					
					if(tick % 6 <= 2)
						caster = gengetsu;
					else
						caster = mugetsu;
					
					float[] posX = { -50, -50,  50, 50,  50, -50 };
					float[] posY = { -50,   0,  50,  0, -50,  50 };

					int selected = rand.nextInt(posX.length);

					{
						float x = caster.getX() + posX[selected];
						float y = caster.getY() + posY[selected];

						boolean ball = tick % 16 < 4; 

						{
							float step = 20;
							float offset = tick % (2 *step) - step;;

							for(float angle = offset; angle < 360 + offset; angle += step)
							{
								Bullet bullet = new Bullet(new ThBullet(ball ? ThBulletType.BALL_BIG : ThBulletType.DOT_SMALL_MOON, ThBulletColor.RED), x, y);

								bullet.useSpawnAnimation(false);
								bullet.getCurrentSprite().setScale(ball ? 1f : 1.7f);
								bullet.setDirectionDegTick(angle, 14f);

								Game.getGame().spawn(bullet);
							}
						}
					}
				}
			}
		}
	}
}
