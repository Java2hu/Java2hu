package java2hu.allstar.enemies.day8.mai_yuki;

import java2hu.Border;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.J2hGame.ClearType;
import java2hu.SmartTimer;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.GravityBullet;
import java2hu.object.bullet.ReflectingBullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Getter;
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;

public class MaiYukiGeneral implements SpecialFlowScheme<AllStarStageScheme>
{
	private static String MAI_SPELLCARD_NAME = "Blue Gates of Death";
	private static String YUKI_SPELLCARD_NAME = "Red Maze of Death";
	
	private float x;
	private float y;

	public MaiYukiGeneral(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	private boolean done = false;

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final SaveableObject<Yuki> yuki = new SaveableObject<Yuki>();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				AllStarUtil.doPC98EnterAnimation(Color.WHITE);
				
				Game.getGame().spawn(new DrawObject()
				{
					private Texture bg = Loader.texture(Gdx.files.internal("enemy/yuki/bg.png"));
					private Texture black = ImageUtil.makeDummyTexture(Color.BLACK, 1, 1);
					private Sprite bge = new Sprite(bg);
					private Sprite blackSprite = new Sprite(black);
					
					{
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return bge;
							}
						}
						, 0f, 1f, 0.004F));
						
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return blackSprite;
							}
						}
						, 0f, 1f, 0.2F));

						addDisposable(black);
						bge.setOriginCenter();
						bge.setScale(2f);
						
						setZIndex(-2);
					}
					
					@Override
					public void onDraw()
					{
						blackSprite.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						blackSprite.draw(Game.getGame().batch);
						bge.setPosition(640F - bge.getWidth() / 2, 480F - bge.getHeight() / 2);
						bge.draw(Game.getGame().batch);
					}
					
					SmartTimer timer = new SmartTimer(0.01f, 0.5f, 0.7f, 3f, 2.8f, 0.001f);
					
					@Override
					public void onUpdate(long tick)
					{
						timer.tick();
						
						bge.setScale(timer.getTimer());
						bge.rotate(0.25f);
					}
					
					@Override
					public boolean isPersistant()
					{
						return !done;
					}
				});
			}
		}, 1);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				yuki.setObject(Yuki.newInstance(x, y));
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return yuki.getObject() == null;
			}
		});

		scheme.doWait();

		{
			final Yuki boss = yuki.getObject();

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
							
							AllStarUtil.presentSpellCard(boss, YUKI_SPELLCARD_NAME);

							Game.getGame().startSpellCard(new YukiSpell(boss));
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
					Game.getGame().getSpellcards().clear();
					Game.getGame().clearObjects();
					
					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Bullet killerKnife = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.BLUE), 0, 500)
							{
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									if(boss.isOnStage())
									{
										setDirectionDegTick(MathUtil.getAngle(this, boss), 20f);
										setRotationFromVelocity(-90);

										if(Intersector.overlapConvexPolygons(boss.getHitbox(), getHitbox()))
										{
											g.delete(boss);

											bloodExplosion(boss.getX(), boss.getY(), 10f, 2f, 0.5f);
										}
									}
								}
								
								@Override
								public boolean isPersistant()
								{
									return boss.isOnStage();
								}
							};
							
							killerKnife.getCurrentSprite().setScale(1.5f);
							
							Game.getGame().spawn(killerKnife);
						}
					}, 1);
				}
			}, 1);
			
			scheme.setWait(new WaitConditioner()
			{
				@Override
				public boolean returnTrueToWait()
				{
					try
					{
						return boss.isOnStage();
					}
					catch(Exception e)
					{
						return true;
					}
				}
			});
			
			scheme.doWait();
		}

		scheme.waitTicks(60);

		final SaveableObject<Mai> mai = new SaveableObject<Mai>();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				mai.setObject(Mai.newInstance(-100, 500));
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return mai.getObject() == null;
			}
		});

		scheme.doWait();

		final Mai boss = mai.getObject();

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();

				final CircleHealthBar bar = new CircleHealthBar(boss);

				Game.getGame().spawn(bar);
				Game.getGame().spawn(boss);
				
				AllStarUtil.introduce(boss);

				AllStarUtil.presentSpellCard(boss, MAI_SPELLCARD_NAME);
				
				BossUtil.moveTo(boss, x, y, 1400);

				Game.getGame().startSpellCard(new MaiSpell(boss));
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
				done = true;
				Game.getGame().delete(boss);
				
				Game.getGame().clearSpellcards();
				Game.getGame().clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);

		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static void bloodExplosion(float x, float y, final float spread, final float maxSize, final float minSize)
	{
		final Sound sound = Gdx.audio.newSound(Gdx.files.internal("enemy/mai/blood_sound.mp3"));
		sound.play();
		
		final Sprite blood;
		
		final Texture sprite = Loader.texture(Gdx.files.internal("enemy/mai/blood.png"));

		blood = new Sprite(sprite);

		for(int i = 0; i < 60; i++)
			
		Game.getGame().spawn(new StageObject(x, y)
		{
			{
				setName("Maple Explosion");
			}
			
			{
				addDisposable(sprite);
				addDisposable(sound);
				this.setZIndex(99999);
			}

			float directionX = 0;
			float directionY = 0;
			float size = 3F;
			float alpha = 0.5F;
			float rotation = 0F;
			
			@Override
			public void onUpdate(long tick)
			{
				if(alpha < 0)
					Game.getGame().delete(this);
				
				if(directionX == 0 || directionY == 0)
				{
					size = minSize;
					directionX = (float) ((Math.random() > 0.5 ? -Math.random() : Math.random()) * spread);
					directionY = (float) ((Math.random() > 0.5 ? -Math.random() : Math.random()) * spread);
					size += (Math.random() > 0.5 ? -Math.random() : Math.random()) * (maxSize - minSize);
					directionX *= size;
					directionY *= size;
				}
				
				if(getTicksAlive() > 50)
				{
					alpha -= 0.03F;
				}
				
				rotation += 1F + 1F * Math.random();
				
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
				blood.setColor(Color.RED);
				blood.setRotation(rotation);
				blood.setAlpha(Math.max(alpha - 0.1F, 0F));
				blood.setScale(size);
				blood.setPosition(getX() - blood.getWidth() / 2, getY() - blood.getHeight() / 2);
				blood.draw(Game.getGame().batch);
			}
		});
	}

	public static class YukiSpell extends Spellcard
	{
		public YukiSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Yuki boss = (Yuki) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0f);

			if(tick == 60)
			{
				TouhouSounds.Enemy.BREAK_2.play();
				boss.setGlowingAura();
				
				boss.setDamageModifier(0.5f);
			}
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			if(tick % 200 == 0)
			{
				BossUtil.moveAroundRandomly(boss, (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 500);
			}
			
			if(tick % 110 < 45 && tick % 15 == 0)
			{
				TouhouSounds.Enemy.BULLET_1.play();
				
				boolean right = Game.getGame().getTick() % 300 < 150;
				
				float step = 360 / 5f;
				float offset = Game.getGame().getTick() * 2 % step;
				
				for(float i = offset; i < 360 + offset; i += step)
				{
					for(float angle = 0; angle < 5; angle += 0.5f)
					{
						final float speed = 4f + (angle + 1) / 10 * 2f;
						
						ReflectingBullet bullet = new ReflectingBullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.RED).getAnimation(), boss.getX(), boss.getY(), 1)
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
							}
							
							@Override
							public void onReflect(Border border, int reflectAmount)
							{
								setVelocityXTick(getVelocityXTick() * 0.6f);
								setVelocityYTick(getVelocityYTick() * 0.6f);
								setBullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED));
								setRotationFromVelocity(-90);
								
								if(!Scheduler.isTracked("reflect", "reflect"))
								{
									TouhouSounds.Enemy.BREAK_1.play();
									Scheduler.track("reflect", "reflect", (long) 10);
								}
							}
						};
						
						bullet.setZIndex((int) (bullet.getZIndex() + angle * 2f));
						
						float totalAngle = i;
						
						if(right)
							totalAngle += angle;
						else
							totalAngle -= angle;
						
						bullet.setDirectionDegTick(totalAngle, speed);
						bullet.setRotationFromVelocity(-90);
						
						game.spawn(bullet);
					}
				}
			}
		}
	}

	public static class MaiSpell extends Spellcard
	{
		public MaiSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Mai boss = (Mai) getOwner();
			
			int startTime = 385;
			
			if(tick == 0)
			{
				boss.setDamageModifier(0f);
			}
			
			{
				int amount = 60;

				if(tick > 140 && tick < 140 + amount && tick % 4 == 0)
					TouhouSounds.Enemy.BULLET_3.play();

				if(tick == 140)
				{
					int[] ranges = { 10, 170 };

					for(int nr = 0; nr < ranges.length; nr += 2)
					{
						int min = ranges[nr];
						int max = ranges[nr + 1];

						for(int degree = min; degree < max; degree += 8)
						{
							for(int i = 0; i < amount; i++)
							{
								final float speed = 5f + i / 5f;
								final float finalDegree = degree;
								final boolean left = i % 2 == 0;

								final Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.BLUE), boss.getX(), boss.getY())
								{
									@Override
									public void onUpdate(long tick)
									{
										super.onUpdate(tick);

										if(getTicksAlive() == 30)
										{
											float add = (left ? -1 : 1) * 5;

											setDirectionDegTick(finalDegree + add, speed);
											setRotationFromVelocity(-90);
										}
									}
								};

								bullet.setDirectionDegTick(degree, speed);
								bullet.setRotationFromVelocity(-90f);

								//							if(left)
								//								bullet.setZIndex(bullet.getZIndex() + i);
								//							else
								bullet.setZIndex(bullet.getZIndex() + amount - i);

								Game.getGame().addTaskGame(new Runnable()
								{
									@Override
									public void run()
									{
										game.spawn(bullet);
									}
								}, i);
							}
						}
					}
				}
			}

			if(tick == startTime)
			{
				TouhouSounds.Enemy.BREAK_2.play();
				boss.setWingsOpen();
				
				boss.setDamageModifier(0.5f);
				
				for(int degree = 0; degree < 360; degree += 10)
				{
					Bullet fake = new Bullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.RED), boss.getX(), boss.getY())
					{
						@Override
						public void checkCollision()
						{
							// Fake bullet
						}
						
						float alpha = 1f;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							alpha -= 0.05f;
							
							if(alpha < 0)
							{
								game.delete(this);
							}
							
							getCurrentSprite().setAlpha(Math.max(0f, alpha));
						}
					};
					
					fake.useDeathAnimation(false);
					fake.useSpawnAnimation(false);
					fake.setDirectionDegTick(degree, 10f);
					
					game.spawn(fake);
				}
			}
			
			if(tick < startTime)
				return;
			
			tick -= startTime;
			
			if(tick % 200 < 100 && tick % 30 == 0)
			{
				BossUtil.moveAroundRandomly(boss, (int) (getGame().getMaxX() / 2) - 150, (int)(getGame().getMaxX() / 2) + 150, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 500);
			}
			
			if(tick % 200 < 100 && tick % 20 == 0)
			{
				TouhouSounds.Enemy.BREAK_1.play(0.7f);
				
				float min = 40;
				float step = 6;
				float max = 85;
				
				boolean[] bools = { true, false };
				
				for(boolean bool : bools)
				for(float angle = min; angle < max; angle += step)
				{
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.BLUE), boss.getX(), boss.getY());
					bullet.setDirectionDegTick(bool ? angle : 180 - angle, 14f - angle / max * 6f);
					
					game.spawn(bullet);
				}
			}
			
			if(tick % 200 > 140 && tick % 10 == 0)
			{
				float[] angles = { 170, 370, 200, 340, 220, 320, 250, 290 };
				
				for(float angle : angles)
				{
					ReflectingBullet bullet = new ReflectingBullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.BLUE).getAnimation(), boss.getX(), boss.getY(), 3)
					{
						@Override
						public void onReflect(Border border, int reflectAmount)
						{
							super.doReflect(border, reflectAmount);
							setRotationFromVelocity(-90);
							
							if(!Scheduler.isTracked("reflect", "reflect"))
							{
								TouhouSounds.Enemy.RELEASE_1.play();
								Scheduler.track("reflect", "reflect", (long) 10);
							}
						}
					};
					
					bullet.setDirectionDegTick(angle, 10f);
					bullet.setRotationFromVelocity(-90);
					game.spawn(bullet);
				}
				
				for(int i = 0; i < 5; i++)
				{
					GravityBullet gb = new GravityBullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, ThBulletColor.BLUE).getAnimation(), boss.getX(), boss.getY(), 0.01f, 4f);
					gb.setDirectionDegTick((float) (270 + (60 * Math.random() - 30)), 10f);
					game.spawn(gb);
				}
			}
		}
	}
}
