package java2hu.allstar.enemies.day8.alice;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Position;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.DrawObject;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.bullet.LaserDrawer.LaserAnimation;
import java2hu.object.bullet.StationaryLaser;
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
import java2hu.util.MathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class AliceGeneral implements SpecialFlowScheme<AllStarStageScheme>
{
	private float x;
	private float y;
	
	public AliceGeneral(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	private static String SPELLCARD_NAME = "Grimoire \"Recollection\"";
	
	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final SaveableObject<Alice> yuuka = new SaveableObject<Alice>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				yuuka.setObject(Alice.newInstance(x, y));
				((AllStarGame)Game.getGame()).setPC98(false);
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
		
		final Alice firstBoss = yuuka.getObject();
		
		{
			final Alice boss = firstBoss;

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

							bar.getObject().addSplit(0.5f);

							AllStarUtil.introduce(boss);
							
							boss.setDamageModifier(0.8f);

							boss.healUp();
							BossUtil.backgroundAura(boss, boss.getBgAuraColor());

							Game.getGame().startSpellCard(new AliceSpell(boss));
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
		}
		
		final SaveableObject<Alice98> yuuka98 = new SaveableObject<Alice98>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				
				yuuka98.setObject(Alice98.newInstance(x, y));
			}
		}, 2);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return yuuka98.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		final Alice98 boss = yuuka98.getObject();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(firstBoss);
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
			
				((AllStarGame)Game.getGame()).setPC98(true);
				
				final CircleHealthBar bar = new CircleHealthBar(boss);
				
				boss.setMaxHealth(200);
				boss.setHealing(true);
				boss.setPosition(firstBoss);
				
				float ticks = 40;
				
				for(int i = 0; i <= ticks; i++)
				{
					float percentage = i / ticks;
					
					final float health = percentage * 100;
					final boolean endHealing = i == ticks;
					
					Runnable run = new Runnable()
					{
						@Override
						public void run()
						{
							boss.setHealth(health);
							
							if(endHealing)
								boss.setHealing(false);
						}
					};
					
					Game.getGame().addTaskGame(run, i);
				}
				
				Game.getGame().spawn(bar);
				Game.getGame().spawn(boss);
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new Alice98Spell(boss));
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
				return boss.isHealing();
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
				Game.getGame().delete(boss);
				
				Game.getGame().clearSpellcards();
				Game.getGame().clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class AliceSpell extends Spellcard
	{	
		public AliceSpell(StageObject owner)
		{
			super(owner);
		}

		@SuppressWarnings("unused")
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Alice boss = (Alice) getOwner();
			
			if(tick % 50 == 0)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 600);
			
			if(tick > 10 && tick % 4 == 0)
				TouhouSounds.Enemy.BULLET_1.play(0.3f);
			
			if(tick == 0)
			{
				boolean[] bools = { true, false };
				float increment = 360 / 3f;

				for(final boolean bool : bools)
					for(float i = 0; i < 3; i ++)
					{
						final float finalI = i * increment;

						StageObject obj = new StageObject((float) (Math.cos(Math.toRadians(i)) * 100), (float) (Math.sin(Math.toRadians(i)) * 100))
						{
							final Texture texture = boss.idle.getKeyFrames()[0].getTexture();
							Sprite fairy = new Sprite(texture, 258, 324, 61, 61);

							{
								if(bool)
									fairy.flip(false, true);
							}

							float angle = finalI;

							@Override
							public void onUpdate(long tick)
							{
								setX((float) (boss.getX() + Math.cos(Math.toRadians(angle)) * 100));
								setY((float) (boss.getY() + Math.sin(Math.toRadians(angle)) * 100));

								angle += (bool ? -1f : 1f) * 3f;

								fairy.setPosition(getX() - getWidth() / 2f, getY() - getHeight() / 2f);

								if(tick % 2 == 0)
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.RED), getX(), getY());
									bullet.setDirectionDegTick(MathUtil.getAngle(boss, bullet), 6f);
									bullet.setRotationFromVelocity(-90);
									game.spawn(bullet);
								}
							}

							@Override
							public void onDraw()
							{
								fairy.draw(Game.getGame().batch);
							}

							@Override
							public float getWidth()
							{
								return fairy.getWidth();
							}

							@Override
							public float getHeight()
							{
								return fairy.getHeight();
							}
						};

						obj.setZIndex(100);

						game.spawn(obj);
					}

				for(float i = 0; i < 3; i++)
				{
					final float finalI = i * increment;

					FreeStageObject obj = new FreeStageObject((float) (Math.cos(Math.toRadians(i)) * 100), (float) (Math.sin(Math.toRadians(i)) * 100))
					{
						final Texture texture = boss.idle.getKeyFrames()[0].getTexture();
						Sprite fairy = new Sprite(texture, 258, 324, 61, 61);

						float angle = finalI;

						@Override
						public void onUpdate(long tick)
						{
							float timer = tick * 2 % 400;
							
							if(timer > 200)
								timer = 400 - timer;
							
							float distance = timer + 100;
							setX((float) (boss.getX() + Math.cos(Math.toRadians(angle)) * distance));
							setY((float) (boss.getY() + Math.sin(Math.toRadians(angle)) * distance));

							angle += 1f;

							fairy.setPosition(getX() - getWidth() / 2f, getY() - getHeight() / 2f);

							if(tick % 6 == 0)
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), getX(), getY());
								bullet.setDirectionDegTick(MathUtil.getAngle(boss, bullet) + 2, 6f);
								bullet.setRotationFromVelocity(-90);
								game.spawn(bullet);
							}
						}

						@Override
						public void onDraw()
						{
							fairy.draw(Game.getGame().batch);
						}

						@Override
						public float getWidth()
						{
							return fairy.getWidth();
						}

						@Override
						public float getHeight()
						{
							return fairy.getHeight();
						}
					};

					obj.setZIndex(100);

					game.spawn(obj);
				}

			}
		}
	}

	public static class Alice98Spell extends Spellcard
	{
		public Alice98Spell(StageObject owner)
		{
			super(owner);
			
			Alice98 boss = (Alice98)owner;
			
			boss.setDamageModifier(0.4f);
			
			Game.getGame().spawn(new DrawObject()
			{
				boolean left = true;
				boolean changed = false;
				
				@Override
				public void onDraw()
				{
					J2hGame game = Game.getGame();
					
					game.batch.end();
					
					game.shape.begin(ShapeType.Line);
					
					ShapeRenderer s = game.shape;
					
					Gdx.gl.glLineWidth(5f);
					
					int sizeIncrement = 600;
					int startSize = 850;
					
					Object[] values = { 
							// Size, delay
							0, 0, new Color(0.8f, 0.8f, 0.8f, 0.2f),
							sizeIncrement, 10, new Color(0.6f, 0.6f, 0.6f, 0.4f),
							2*sizeIncrement, 20, new Color(0.4f, 0.4f, 0.4f, 0.6f),
							3*sizeIncrement, 30, new Color(0.2f, 0.2f, 0.2f, 0.8f),
							4*sizeIncrement, 40, new Color(0.8f, 0.2f, 0.2f, 1f),
					};
					
					float rotation = Game.getGame().getTick() % 360f;
					
					for(int index = 0; index < values.length; index += 3)
					{
						int size = startSize + (int)values[index];
						int offset = (int)values[index + 1];
						Color color = (Color)values[index + 2];

						s.setColor(color);
						
						float scale = (Game.getGame().getTick() + offset) / 200f % 1f;
						
						if(index == values.length - 3)
							if(!changed && scale < 0.2f)
							{
								changed = true;
								left = !left;
							}
							else if(scale > 0.2f && scale < 0.4f)
								changed = false; // Next time the scale is small, it'll switch again.
						
						float radius = (startSize + size) * scale;

						float centerX = Game.getGame().getWidth()/2f;
						float centerY = Game.getGame().getHeight()/2f;

						s.circle(centerX, centerY, radius);

						float increment = 360 / 6f;
						
						rotation += 10;

						for(float i = 0; i < 360f; i += increment)
						{
							float rot = i + (left ? 1 : -1) * rotation;
							s.line(centerX + (float)(Math.cos(Math.toRadians(rot)) * radius), centerY + (float)(Math.sin(Math.toRadians(rot)) * radius), centerX + (float)(Math.cos(Math.toRadians(rot + 2 * increment)) * radius), centerY + (float)(Math.sin(Math.toRadians(rot + 2 * increment)) * radius));
						}

						s.setColor(Color.WHITE);
					}
					
					s.end();
					
					game.batch.begin();
				}
			});
		}
		
		private Position saved = null;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Alice98 boss = (Alice98) getOwner();
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			float total = 2300;
			float partTick = tick % total;
			
			float fireStart = 0;
			float fireEnd = 1000;
			
			float greenStart = 1100;
			float greenEnd = 2000;
			
			float lasers = 1800;
			
//			partTick = greenStart + (tick % (greenEnd - greenStart));
			
			if(tick % 200 == 0)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 1200);
			
			boolean fire = partTick > fireStart && partTick < fireEnd;
			boolean green = partTick > greenStart && partTick < greenEnd;
			
			if(fire)
			{
				if(tick > 10 && tick % 4 == 0)
					TouhouSounds.Enemy.BULLET_1.play(0.3f);
				
				if(tick % 40 == 30)
				{
					TouhouSounds.Enemy.BULLET_4.play(1f);
					
					saved = new Position(player.getX(), player.getY());
					boolean lessDense = tick % 400 > 300;

					for(int i = -20; i < 20; i += lessDense ? 5 : 2)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ORB, ThBulletColor.RED), boss.getX(), boss.getY());
						bullet.setDirectionDegTick(MathUtil.getAngle(bullet, saved) + i, 5f);
						bullet.setRotationFromVelocity(-90);
						bullet.setZIndex(bullet.getZIndex() + i + 20);
						game.spawn(bullet);
					}
				}

				if(tick % 5 == 0 && saved != null)
				{
					float fullRotationTime = 50f;
					float modifier = tick % fullRotationTime / (fullRotationTime / 2f);
					
					if(modifier > 1)
						modifier = 2 - modifier;
					
					for(float i = -180; i < 180; i +=  i < 20 && i >= -20 ? 0.5f : 5)
					{
						float value = 50 * modifier;
						value = Math.max(12.5f, 12.5f + value);
						
						if(i > -value && i < value)
							continue;

						Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE_LARGE, ThBulletColor.RED), boss.getX(), boss.getY());
						bullet.setDirectionDegTick(MathUtil.getAngle(bullet, saved) + i, 20f);
						bullet.setRotationFromVelocity(-90);
						bullet.setZIndex((int) (bullet.getZIndex() + i + 180));
						game.spawn(bullet);
					}
				}
			}
			else
				saved = null;
			
			if(partTick == lasers)
				for(int i = -140; i <= 140; i += 20)
				{
					if(i > -20 && i < 20)
						continue;
					
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.FIREBALL, ThBulletColor.PURPLE), boss.getX(), boss.getY())
					{
						long stuckTick = 0;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(stuckTick == 0)
							{
								if(!game.inBoundary(getX(), getY()))
								{
									setVelocityXTick(0);
									setVelocityYTick(0);
									stuckTick = Game.getGame().getTick();
								}
							}
							else if(game.getTick() - stuckTick > 100)
							{
								TouhouSounds.Enemy.LAZER_2.play(0.5f);
								
								LaserAnimation ani = new LaserAnimation(1, new ThBullet(ThBulletType.LAZER_STATIONARY, ThBulletColor.PURPLE).getAnimation().getKeyFrames()[0].getTexture());

								StationaryLaser laser = new StationaryLaser(ani, 0, 0)
								{
									boolean hitbox = false;
									float timer = 1;

									@Override
									public void onUpdate(long tick)
									{
										super.onUpdate(tick);

										if(getTicksAlive() < 10)
										{
											setThickness(2);
											setHitboxThickness(0);
										}
										else if(getTicksAlive() == 120)
											TouhouSounds.Enemy.EXPLOSION_3.play(0.5f);
										else if(getTicksAlive() > 120 && getTicksAlive() < 160)
										{
											if(timer < 14)
											{
												timer *= 1.1f;

												setThickness(timer);
												setHitboxThickness(timer / 3f);
											}

											if(!hitbox && timer > 8)
												hitbox = true;
										}
										else if(getTicksAlive() > 200)
											if(timer > 0)
											{
												timer -= 0.2f;

												setThickness(timer);
												setHitboxThickness(timer / 3f);
												
												if(hitbox && timer < 8)
													hitbox = false;
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

								laser.setStart(getX(), getY());

								float deg = MathUtil.getAngle(player, this);
								
								laser.setEnd(getX() + (float) (Math.cos(Math.toRadians(deg)) * 2000f), getY() + (float) (Math.sin(Math.toRadians(deg)) * 2000f));
								
								Game.getGame().spawn(laser);

								Game.getGame().delete(this);
							}
						}
					};
					bullet.setDirectionDegTick(MathUtil.getAngle(bullet, player) + 180 + i, 20f);
					bullet.setRotationFromVelocity(-90);
					bullet.setZIndex(bullet.getZIndex() + i + 90);
					game.spawn(bullet);
				}
			
			if(green)
			{
				if(tick > 10 && tick % 10 == 0)
					TouhouSounds.Enemy.BULLET_3.play(0.8f);
				
				if(partTick % 1 == 0  && partTick % 27 < 24)
				{
					float centerX = Game.getGame().getWidth()/2f;
					float centerY = Game.getGame().getHeight()/2f + 200;

					float degree = tick * 10 % (3 * 180f);
					
					if(degree > 360)
						degree = 720 - degree;
					
					degree += 180 + 90;
					
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), centerX, centerY);
					bullet.setDirectionDegTick(degree, 2f);
					bullet.setRotationFromVelocity(-90);
					bullet.setZIndex(bullet.getZIndex() + 100);
					game.spawn(bullet);
					
					bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), centerX, centerY);
					bullet.setDirectionDegTick(180 - degree, 2f);
					bullet.setRotationFromVelocity(-90);
					bullet.setZIndex(bullet.getZIndex() + 100);
					game.spawn(bullet);
					
					bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), centerX, centerY);
					bullet.setDirectionDegTick(degree, 5f);
					bullet.setRotationFromVelocity(-90);
					bullet.setZIndex(bullet.getZIndex() + 100);
					game.spawn(bullet);
					
					bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.GREEN), centerX, centerY);
					bullet.setDirectionDegTick(180 - degree, 5f);
					bullet.setRotationFromVelocity(-90);
					bullet.setZIndex(bullet.getZIndex() + 100);
					game.spawn(bullet);
				}
				
				if(tick % 100 == 0 && greenEnd - partTick > 100)
				{
					TouhouSounds.Enemy.HUM_1.play(0.8f);
					
					float offset = (float) Math.random() * 10f;
					
					for(int i = -20; i <= 180; i += 10)
					{
						final int finalI = (int) (i + offset);

						Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.GREEN), boss.getX(), boss.getY(), 300)
						{
							{
								setUnitsPerPoint(2);
							}
							
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								float size = 150;
								
								float timer = game.getTick() % size;

								if(timer > size/2f)
									timer = size - timer;

								setDirectionDegTick(finalI + timer, 4f);
							}
						};
						laser.setHitboxThickness(laser.getHitboxThickness() - 2);
						laser.setDirectionDegTick(i, 4f);
						game.spawn(laser);
					}
				}
			}
		}
	}
}
