package java2hu.allstar.stage;

import java.util.ArrayList;
import java2hu.Border;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.BGMPlayer;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.ReflectingBullet;
import java2hu.object.enemy.Enemy;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.enemy.TouhouEnemyType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.MathUtil;
import java2hu.util.PathUtil;
import java2hu.util.PathUtil.PathTask;
import java2hu.util.SoundUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class MakaiStage implements SpecialFlowScheme<AllStarStageScheme>
{
	public void spawnDeathDanmaku(StageObject obj)
	{
		final J2hGame game = Game.getGame();
		final Player player = game.getPlayer();
		
		float offset = (float) MathUtil.getAngle(obj, player);
		for(float angle = offset; angle < 360 + offset; angle += 30)
		{
			for(int i = 0; i < 3; i++)
			{
				final Bullet bullet = new Bullet(new ThBullet(ThBulletType.BUTTERFLY, ThBulletColor.BLUE), obj.getX(), obj.getY());
				bullet.setDirectionDegTick(angle, 10f);
				bullet.setRotationFromVelocity(-90);
				
				game.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						game.spawn(bullet);
					}
				}, i * 10);
			}
		}
		
	}
	
	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		final SaveableObject<BGMPlayer> bgm = new SaveableObject<BGMPlayer>();
		
		final SaveableObject<Boolean> stageInProgress = new SaveableObject<Boolean>(true);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				bgm.setObject
				(
					new BGMPlayer(Gdx.audio.newMusic(Gdx.files.internal("music/makai/bgm.mp3")))
					{
						@Override
						public boolean isPersistant()
						{
							return stageInProgress.getObject();
						}
					}
				);
				
				Game.getGame().spawn(bgm.getObject());
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return bgm.getObject() == null;
			}
		});
		
		scheme.doWait();
		
		bgm.getObject().getBgm().setVolume(1f * Game.getGame().getMusicModifier());
		
		final J2hGame game = Game.getGame();
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				bgm.getObject().getBgm().play();
			}
		}, 1);
		
		final Player player = game.getPlayer();
		final long startTick = game.getTick();
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				DrawObject obj = new DrawObject()
				{
					Texture texture = Loader.texture(Gdx.files.internal("scenes/makai stage/stage title.png"));
					Sprite back = new Sprite(texture, 0, 0, 180, 128);
					Sprite text = new Sprite(texture, 180, 0, 204, 128);
					
					{
						addDisposable(texture);
						
						texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
						
						Rectangle b = back.getBoundingRectangle();
						back.setPosition(Game.getGame().getWidth()/2f - b.getWidth() / 2f, Game.getGame().getHeight()/2f - b.getHeight() / 2f);
						b = text.getBoundingRectangle();
						text.setPosition(Game.getGame().getWidth()/2f - b.getWidth() / 2f, Game.getGame().getHeight()/2f - b.getHeight() / 2f);
						
						back.setScale(2f);
						back.setAlpha(0);
						text.setAlpha(0);
					}
					
					@Override
					public void onDraw()
					{
						back.draw(game.batch);
						text.draw(game.batch);
					}
					
					float rotation = 0;
					float alpha = 0;
					
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						if(getTicksAlive() < 100)
						{
							alpha = Math.min(1, alpha + 0.01f);
							
							back.setAlpha(alpha);
							text.setAlpha(alpha);
						}
						
						if(getTicksAlive() > 100)
						{
							if(rotation < 180)
							{
								rotation += 1.5f;
							}

							back.setRotation(Math.min(180, rotation));
						}
						
						if(getTicksAlive() > 230)
						{
							alpha = Math.max(0, alpha - 0.01f);
							
							if(alpha <= 0)
							{
								game.delete(this);
							}
						}
						
						back.setAlpha(alpha);
						text.setAlpha(alpha);
					}
				};
				
				obj.setZIndex(J2hGame.GUI_Z_ORDER);
				
				game.spawn(obj);
			}
		}, 1);
		
		scheme.waitTicks(10);
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				final Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 50, Game.getGame().getWidth()/2f, 1000)
				{
					CircleHealthBar bar;
					boolean inPlace = false;
					
					{
						final Enemy thisEnemy = this;
						PathTask task = utils().movement().moveTo(this, getX(), getY() - 200, 80);

						task.setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								inPlace = true;
							}
						});
						
						bar = new CircleHealthBar(this);
						bar.setRadius(30);
						bar.setRingColor(Color.BLUE);
						bar.setHealthColor(new Color(0f, 0f, 0.7f, 1f));
						game.spawn(bar);
					}

					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);

						final Enemy enemy = this;
						
						int soundTime = 10 - (int)((50 - getHealth()) / 50f * 8f);
						
						if(!inPlace)
						{
							setHealth(50);
						}
						
						if(inPlace && tick % soundTime == 0)
						{
							TouhouSounds.Enemy.RELEASE_1.play();
						}
					
						if(inPlace && tick % 1 == 0)
						{
							if(getHealth() > 25f && tick % 2 < 1)
								return;
							
							float minAngle = 10;
							float maxAngle = 40;
							float angle = minAngle + (float)(Math.random() * (maxAngle - minAngle));
							
							float startAngle = (float) (Math.random() * 360f);
							
							for(float f = 0; f < angle; f += 6)
							{
								final float shootAngle = startAngle + f;
								
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.BLUE), enemy.getX(), enemy.getY());
								bullet.setDirectionDegTick(shootAngle, 5f + (50 - getHealth()) / 50f * 4f);
								bullet.setRotationFromVelocity(-90);
								game.spawn(bullet);
							}
						}
					}

					@Override
					public void onDelete()
					{
						spawnDeathDanmaku(this);
						
						if(bar != null)
							game.delete(bar);
						
						super.onDelete();
					}
				};

				game.spawn(enemy);
			}
		}, 5 * 60);
		
		scheme.waitTicks(16 * 60 - 10);
		
		final float heartBeatIntervalSeconds = 1.37f;
		final float heartBeatIntervalTicks = heartBeatIntervalSeconds * 60f;
		
		float[] values = { 
				1350, 750, -400, -50, 0,
				-50, 750, +400, 1350, heartBeatIntervalTicks,
				1350, 50, -400, -50, 2 * heartBeatIntervalTicks,
				-50, 50, +400, 1350, 3 * heartBeatIntervalTicks,
				1350, 450, -400, -50, 4 * heartBeatIntervalTicks,
				-50, 450, +400, 1350, 5 * heartBeatIntervalTicks,
				1350, 750, -400, -50, 6 * heartBeatIntervalTicks,
				-50, 750, +400, 1350, 6 * heartBeatIntervalTicks,
				1350, 50, -400, -50, 7 * heartBeatIntervalTicks,
				-50, 50, +400, 1350, 7 * heartBeatIntervalTicks,
				1350, 750, -400, -50, 7.5f * heartBeatIntervalTicks,
				-50, 750, +400, 1350, 8f * heartBeatIntervalTicks,
				1350, 50, -400, -50, 8.5f * heartBeatIntervalTicks,
				-50, 50, +400, 1350, 9f * heartBeatIntervalTicks,
				1350, 750, -400, -50, 9.5f * heartBeatIntervalTicks,
				-50, 750, +400, 1350, 10f * heartBeatIntervalTicks,
				1350, 50, -400, -50, 10.5f * heartBeatIntervalTicks,
				-50, 50, +400, 1350, 11f * heartBeatIntervalTicks,
		};
		
		for(int i = 0; i < values.length; i += 5)
		{
			final float startX = values[i];
			final float startY = values[i + 1];
			final float offsetX = values[i + 2];
			final float endX = values[i + 3];
			final float delay = values[i + 4];
			
			game.addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					final Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 10, startX, startY)
					{
						{
							final Enemy thisEnemy = this;
							PathTask task = utils().movement().moveTo(this, getX() + offsetX, getY(), 80);

							task.setOnDone(new Runnable()
							{
								@Override
								public void run()
								{
									PathTask task = utils().movement().moveTo(thisEnemy, endX, getY(), 100);

									task.setOnDone(new Runnable()
									{
										@Override
										public void run()
										{
											useDeathSound(false);
											game.delete(thisEnemy);
										}
									});
								}
							});
						}

						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);

							final Enemy enemy = this;

							if(getTicksAlive() == 80)
							{
								float total = 200;
								for(int i = 0; i < total; i++)
								{
									boolean left = i > total / 2f;
									
									float angle = (float) MathUtil.getAngle(this, player);
									float offsetAngle = angle + (left ? 90 : 270);
									float rad = (float) Math.toRadians(offsetAngle);
									float cos = (float) Math.cos(rad);
									float sin = (float) Math.sin(rad);

									float size = 70;

									float sizeX = (float) (size * Math.random());
									float sizeY = (float) (size * Math.random());
									float averageSize = (sizeX + sizeY) / 2f;

									cos *= size * Math.random();
									sin *= size * Math.random();

									Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.BLUE), enemy.getX() + cos, enemy.getY() + sin);
									bullet.useSpawnAnimation(false);
									
									bullet.setDirectionDegTick(angle, 2f + averageSize / 6f);
									game.spawn(bullet);
								}
							}
						}

						@Override
						public void onDelete()
						{
							super.onDelete();
						}
					};

					game.spawn(enemy);
				}
			}, (int) delay);
		}
		
		scheme.waitTicks(13 * 60);
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 0; i <= 20; i++)
				{
					int delay = i * 2;
					final int finalI = i;
					final float offset = Game.getGame().getWidth() / 19f;
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							final float finalY = 1000;
							final float finalX = finalI * offset;
							
							final Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 10, finalX, finalY)
							{
								float row = -2;
								PathTask lastTask = null;
								
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									final Enemy enemy = this;
									
									if(row > 1)
									if(getTicksAlive() % 3 == 0 && getTicksAlive() % 45 <= 30)
									{
										SoundUtil.playSoundSingle(TouhouSounds.Enemy.BULLET_3, 0.5f, "bullet", 10);
										Bullet bullet = new Bullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.BLUE), enemy.getX(), enemy.getY());
										bullet.setDirectionDegTick(90, 7f);
										game.spawn(bullet);
									}
									
									if(lastTask == null || lastTask.isDone())
									{
										boolean left = row % 2 == 0;
										final float targetX = left ? finalX - 50 : finalX + 50;
										
										final float y = finalY - row * 50;
										
										lastTask = utils().movement().moveTo(this, getX(), y, 20);
										
										System.out.println(row);
										
										lastTask.setOnDone(new Runnable()
										{
											@Override
											public void run()
											{
												row++;
												
												if(y < 0)
												{
													Game.getGame().delete(enemy);
												}
												
												lastTask = utils().movement().moveTo(enemy, targetX, y, 60);
											}
										});
									}
								}
								
								@Override
								public void onDelete()
								{
									if(row > 10)
										spawnDeathDanmaku(this);
									
									super.onDelete();
								}
							};
							
							game.spawn(enemy);
						}
					}, delay);
				}
			}
		}, 1);
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				final SaveableObject<Enemy> lastEnemy = new SaveableObject<>();
				
				boolean[] bools = { true, false };
				for(final boolean bool : bools)
				{
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							final float finalY = 800;
							final float finalX = bool ? -50 : Game.getGame().getWidth() + 50;
							
							final Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 1, finalX, finalY)
							{
								boolean inPlace = false;
								
								{
									final Enemy thisEnemy = this;
									
									PathTask task = PathUtil.moveTo(thisEnemy, finalX + (bool ? 100 : -100), finalY, 20);
									
									task.setOnDone(new Runnable()
									{
										@Override
										public void run()
										{
											inPlace = true;
										}
									});
								}
								
								private Enemy brother = null;
								
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									final Enemy enemy = this;
									
									if(brother == null)
									if(lastEnemy.getObject() == null)
									{
										lastEnemy.setObject(this);
									}
									else if(lastEnemy.getObject() != this)
									{
										brother = lastEnemy.getObject();
										lastEnemy.setObject(this);
									}
									
									final Polygon brotherHitbox;
									
									if(brother != null)
									{
										brotherHitbox = brother.getPlayerHitHitbox();
									}
									else
									{
										brotherHitbox = null;
									}
									
									if(inPlace && tick % 60 == 0)
									{
										SoundUtil.playSoundSingle(TouhouSounds.Enemy.BREAK_1, 0.5f, "break", 10);
										
										for(int i = 0; i < 4; i++)
										{
											int delay = (int) (i * 7f);
											final float finalAngle = (float) MathUtil.getAngle(enemy, player);
											
											game.addTaskGame(new Runnable()
											{
												@Override
												public void run()
												{
													Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.BLUE), enemy.getX(), enemy.getY())
													{
														@Override
														public void onUpdate(long tick)
														{
															super.onUpdate(tick);
															
															if(Intersector.overlapConvexPolygons(brotherHitbox, getHitbox()))
															{
																game.delete(this);
																game.delete(brother);
															}
														}
													};
													bullet.setDirectionDegTick(finalAngle, 8f);
													bullet.setRotationFromVelocity(-90f);
													game.spawn(bullet);
												}
											}, delay);
										}
									}
								}
								
								@Override
								public void onDelete()
								{
									super.onDelete();
								}
							};
							
							game.spawn(enemy);
						}
					}, 1);
				}
			}
		}, 4 * 60);
		
		scheme.waitTicks(21 * 60);

		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 1, Game.getGame().getWidth() + 50, 800)
				{
					float angle = 0;
					
					{
						final Enemy thisEnemy = this;
						PathTask task = utils().movement().moveTo(this, -50, getY(), 200);

						task.setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								useDeathSound(false);
								game.delete(thisEnemy);
							}
						});	
					}
					
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						if(tick % 1 == 0)
						{
							SoundUtil.playSoundSingle(TouhouSounds.Enemy.BULLET_4, 0.5f, "bullet_4", 10);
							
							angle += 5f;
							
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.BLUE), getX(), getY());
							bullet.setDirectionDegTick(angle, 10f);
							game.spawn(bullet);
							
							bullet = new Bullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.BLUE), getX(), getY());
							bullet.setDirectionDegTick(angle + 180, 10f);
							game.spawn(bullet);
						}
					}
				};
				
				game.spawn(enemy);
			}
		}, 1);
		
		scheme.waitTicks(6 * 60);
		
		{
			boolean[] bools = { true, false };
			
			for(final boolean bool : bools)
				for(int i = 0; i < 20; i++)
				{
					final int delay = (int) (i * 60f);
					final int startX = bool ? -100 : Game.getGame().getWidth() + 100;
					final int startY = 500;
					final int endX = bool ? 200 : Game.getGame().getWidth() - 200;
					final int endY = 1000;
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Enemy enemy = new Enemy(TouhouEnemyType.SMALL_FAIRY_BLUE, 4, startX, startY)
							{
								{
									final Enemy thisEnemy = this;
									PathTask task = utils().movement().moveTo(this, endX, endY, 100);

									task.setOnDone(new Runnable()
									{
										@Override
										public void run()
										{
											useDeathSound(false);
											game.delete(thisEnemy);
										}
									});	
								}

								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(tick % 4 == 0)
									{
										SoundUtil.playSoundSingle(TouhouSounds.Enemy.BULLET_1, 0.5f, "bullet_4", 10);
										
										float angle = (float) MathUtil.getAngle(this, player);
										
										Bullet bullet = new Bullet(new ThBullet(ThBulletType.BUTTERFLY, ThBulletColor.BLUE), getX(), getY());
										bullet.setDirectionDegTick(angle, 4f);
										bullet.setRotationFromVelocity(-90);
										game.spawn(bullet);

										bullet = new Bullet(new ThBullet(ThBulletType.BUTTERFLY, ThBulletColor.BLUE), getX(), getY());
										bullet.setDirectionDegTick(angle + 180, 4f);
										bullet.setRotationFromVelocity(-90);
										game.spawn(bullet);
									}
								}
							};

							game.spawn(enemy);
						}
					}, delay);
				}
		}
		
		scheme.waitTicks(20 * 60);
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				HitboxSprite sprite = (HitboxSprite) new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED).getAnimation().getKeyFrames()[0];
				
				Array<TextureRegion> array = new Array<TextureRegion>();
				
				for(float size = 0.5f; size < 1.5f; size += 0.01f)
				{
					HitboxSprite clone = new HitboxSprite(sprite);
					
					clone.setScale(size);
					array.add(clone);
				}
				
				for(float size = 1.5f; size > 0.5f; size -= 0.01f)
				{
					HitboxSprite clone = new HitboxSprite(sprite);
					
					clone.setScale(size);
					array.add(clone);
				}
				
				final Animation ani = new Animation(0.5f, array);
				ani.setPlayMode(PlayMode.LOOP);
				
				Enemy enemy = new Enemy(ani, ani, ani, ani, 100, -100, 100)
				{
					boolean hover = false;
					long startTick = 0;
					int phase = 0;
					
					{
						ArrayList<Object> path = new ArrayList<Object>();
						
						float centerX = 0;
						float centerY = 600;
						
						for(float angle = 270; angle < 390; angle += 10)
						{
							float rad = (float) Math.toRadians(angle);
							
							path.add((float) (centerX + Math.cos(rad) * 800));
							path.add((float) (centerY + Math.sin(rad) * 500));
							path.add(10);
						}
						
						final Enemy thisEnemy = this;
						PathTask task = utils().movement().path(thisEnemy, path);

						task.setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								hover = true;
								y = thisEnemy.getY();
								startTick = game.getTick();
								phase = 1;
								speed = 5f;
							}
						});	
					}
					
					boolean right = true;
					int round = 1;
					float y = 0;
					float speed = 1f;

					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						setHealth(100);
						
						if(hover)
						{
							{
								setX(getX() + (right ? speed : -speed));
								setY((float) (y + Math.sin(tick / 20f % (2 * Math.PI)) * 100));
								
								if(x >= Game.getGame().getWidth() - getCurrentSprite().getWidth())
								{
									right = false;
								}
								
								if(!right && phase == 1 && round == 2 && getX() < 800)
								{
									phase = 0;
								}
								
								if(!right  && round == 2 && getX() < 200)
								{
									phase = 2;
									speed = 4f;
								}
								
								if(x < 0 + getCurrentSprite().getWidth())
								{
									right = true;
									round++;
								}
							}
						}
						
						if(phase == 1)
						{
							if(tick % 30 == 0)
							{
								for(float angle = 180 + 20; angle < 360 - 20; angle += 15f)
								{
									ReflectingBullet bullet = new ReflectingBullet(new ThBullet(ThBulletType.HEART, ThBulletColor.RED).getAnimation(), getX(), getY(), 99)
									{
										@Override
										public boolean doReflect(Border border, int reflectAmount)
										{
											return border != Border.BOT;
										}
										
										@Override
										public void onReflect(Border border, int reflectAmount)
										{
											super.onReflect(border, reflectAmount);
											
											setRotationFromVelocity(-90);
											SoundUtil.playSoundSingle(TouhouSounds.Enemy.RELEASE_3, 0.5f, "release", 10);
										}
									};
									
									bullet.setDirectionDegTick(angle, 6f);
									
									game.spawn(bullet);
								}
							}
						}
						else if(phase == 2)
						{
							if(tick % 20 == 0)
							{
								TouhouSounds.Enemy.BULLET_3.play();
								
								for(float angle = 0; angle < 360; angle += 6f)
								{
									boolean add = angle / 8f % 6 == 0;
									
									final float finalAngle = angle;
									final float x = getX();
									final float y = getY();
									final float speed = 7f;
									
									final Enemy enemy = this;
									
									game.addTaskGame(new Runnable()
									{
										@Override
										public void run()
										{
											Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), x, y);
											
											bullet.setDirectionDegTick(finalAngle, speed);
											bullet.setRotationFromVelocity(-90);
											
											game.spawn(bullet);
										}
									}, 10);
									
									if(add)
									{
										Bullet bullet = new Bullet(new ThBullet(ThBulletType.CRYSTAL, ThBulletColor.BLUE), x, y);
										
										bullet.setDirectionDegTick(angle, speed);
										bullet.setRotationFromVelocity(-90);
										
										game.spawn(bullet);
									}
								}
							}
						}
					}
				};
				
				enemy.setZIndex(10000);
				enemy.setPlayerHitHitbox(null);

				game.spawn(enemy);
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				long elapsedTicks = game.getTick() - startTick;
				
				return elapsedTicks < 98 * 60;
			}
		});
		
		scheme.doWait();

		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				game.clearSpellcards();
				game.clear(ClearType.ALL_OBJECTS);
				
				stageInProgress.setObject(false);
				
				bgm.getObject().fade(bgm.getObject().getBgm().getVolume(), 0f, 300, true);
			}
		}, 1);
	}
}
