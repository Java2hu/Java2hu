package java2hu.allstar.enemies.day8;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.Position;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.ScrollingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
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
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.Scheduler;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Shou extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Shou";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Treasure Sign \"Treasure Gathering Laser\"";
	
	public static Shou newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 224;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 5,6,7), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 8), 10f);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		for(int i = 0; i < 4; i++)
		{
			frames.add(new HitboxSprite(new TextureRegion(sprite, i * chunkWidth, 2 * chunkHeight, chunkWidth, chunkHeight)));
		}
		
		frames.add(new HitboxSprite(new TextureRegion(sprite, 0, 3 * chunkHeight, chunkWidth, chunkHeight)));
		
		for(int i = 0; i < 4; i++)
		{
			frames.add(new HitboxSprite(new TextureRegion(sprite, chunkWidth, 3 * chunkHeight, 192, chunkHeight)));
			frames.add(new HitboxSprite(new TextureRegion(sprite, chunkWidth + 192, 3 * chunkHeight, 192, chunkHeight)));
		}

		Animation special = new Animation(10f, frames);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		Sprite bge = new Sprite(Loader.texture(Gdx.files.internal(folder + "bge.png")));

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Shou boss = new Shou(100, nameTag, bg, bge, fbs, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	public Sprite bg;
	public Sprite bge;
	
	public Shou(float maxHealth, TextureRegion nametag, final Sprite bg, final Sprite bge, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		addDisposable(bge);
		
		this.bg = bg;
		this.bge = bge;
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

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Shou boss = this;
		
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
						BossUtil.backgroundAura(boss);
						
						Game.getGame().startSpellCard(new ShouNonSpell(boss));
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
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new ShouSpell(boss));
			}
		}, 1);
		
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
	
	public static class ShouNonSpell extends Spellcard
	{	
		public ShouNonSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Shou boss = (Shou) getOwner();
			
			if(tick == 0)
			{
				boss.setDamageModifier(0.7f);
			}
			
			if(tick % 200 == 170)
			{
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 200, (int)(getGame().getMaxX() / 2) + 200, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
			}
			
			if(tick % 200 == 0)
			{
				boss.playSpecial(true);
			}
			
			if(tick % 200 == 40)
			{
				TouhouSounds.Enemy.LAZER_1.play();
				
				boolean[] bools = { true, false };
				
				for(final boolean left : bools)
				for(int i = 0; i < 360; i += 20)
				{
					final float finalAngle = i;

					game.spawn(new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.YELLOW), boss.getX(), boss.getY(), 400)
					{
						{
							setUnitsPerPoint(6);
							setMaxPoints(40);
							setUpdateSkip(-1);
						}
						boolean straight = false;
						float angle = finalAngle;
						float beforeStraightAngle = 0;
						int slowTicks = 0;

						@Override
						public void onUpdate(long tick)
						{
							float speed = 15f;
							
							if(!straight)
							{
								float threshold = finalAngle + (left ? -1 : 1) * 180f;

								if(left ? angle > threshold : angle < threshold)
								{
									speed = 10f;

									angle = angle + (left ? -1 : 1) * 5f;

									if(left ? angle <= threshold : angle >= threshold)
									{
										slowTicks = 45;
										straight = true;
										beforeStraightAngle = angle;
									}
								}
							}
							else if(slowTicks <= 0)
							{
								float threshold = beforeStraightAngle - (left ? -1 : 1) * 140f;

								if(left ? angle < threshold : angle > threshold)
								{
									speed = 4f;

									angle = angle - (left ? -1 : 1) * 2;
								}
							}

							setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
							super.onUpdate(tick);
							
							if(slowTicks > 0)
								slowTicks--;
						}
					});
				}
			}
			
			if(tick % 200 == 140)
			{
				TouhouSounds.Enemy.RELEASE_1.play();
				
				float increase = 8;
				float offset = (float) (Math.random() * increase);
				
				for(float angle = offset; angle < 360 + offset; angle += increase)
				{
					for(int i = 0; i < 6; i++)
					{
						final float finalAngle = angle;
						final int pos = i + 1;
						
						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.PURPLE), boss.getX(), boss.getY());
								bullet.setDirectionRadsTick((float) Math.toRadians(finalAngle), 3f + pos * 1.6f);
								bullet.setZIndex(bullet.getZIndex() + pos);
								bullet.useSpawnAnimation(false);
								game.spawn(bullet);
							}
						}, i * 5);
					}
				}
			}
		}
	}

	public static class ShouSpell extends Spellcard
	{
		public ShouSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Shou boss = (Shou) getOwner();
			
			if(tick == 0)
			{
				boss.setDamageModifier(0.6f);
				
				game.spawn(new DrawObject()
				{
					{
						boss.bg.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						setZIndex(-2);
						
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return boss.bg;
							}
						}, 0.1f));
					}
					
					@Override
					public void onDraw()
					{
						boss.bg.draw(game.batch);
					}
				
				});
				
				game.spawn(new ScrollingBackground(boss.bge, -2f, -2f)
				{
					{
						setZIndex(-1);
						
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return boss.bge;
							}
						}, 0.1f));
					}
				});
			}
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			if(tick % 200 == 160)
			{
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 200, (int)(getGame().getMaxX() / 2) + 200, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
			}
			
			if(tick % 200 == 0)
			{
				TouhouSounds.Enemy.LAZER_1.play();
				
				boolean[] bools = { true, false };
				
				float increment = 40;
				float offset = (float) (Math.random() * increment);

				for(final boolean left : bools)
				for(float i = offset; i < 360 + offset; i += increment)
				{
					final float finalAngle = i;

					game.spawn(new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.WHITE), boss.getX(), boss.getY(), 600)
					{
						boolean straight = false;
						float angle = finalAngle;
						int slowTicks = 0;

						{
							setShader(ShaderLibrary.GLOW.getProgram());
							setThickness(30);
						}

						@Override
						public void onUpdate(long tick)
						{
							float speed = 10f;

							if(!straight)
							{
								float threshold = finalAngle + (left ? -1 : 1) * 180f;

								if(left ? angle > threshold : angle < threshold)
								{
									speed = 15f;

									angle = angle + (left ? -1 : 1) * 5f;

									if(left ? angle <= threshold : angle >= threshold)
									{
										slowTicks = 45;
									}
								}
							}

							setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
							super.onUpdate(tick);

							if(slowTicks > 0)
								slowTicks--;
							
							ArrayList<Position> points = getPoints();
							
							int index = 0;
							for(int i = 0; i < points.size(); i += 50)
							{
								Position pos = points.get(i);
								
								float x = pos.getX();
								float y = pos.getY();
								
								if(!game.inBoundary(x, y))
								{
									if(!Scheduler.isTracked("spawnCoin", "spawnCoin") && x > -20 && x < Game.getGame().getWidth() + 20 && y > -20 && y < Game.getGame().getHeight() + 20)
									{
										Scheduler.track("spawnCoin", "spawnCoin", (long) 20);
										TouhouSounds.Player.ITEM_2.play();
									}

									for(int amount = 0; amount < 2; amount++)
									{
										final int xMovement = 800;
										final int yMovement = 200;
										
										final float xDest = (float) (x + ((Math.random() - 0.5f) * 2) * xMovement);
										final float yDest = (float) (y + ((Math.random() - 0.5f) * 2) * yMovement);

										Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.WHITE), x, y)
										{
											boolean reachedDest = false;

											@Override
											public void onUpdate(long tick)
											{
												super.onUpdate(tick);

												if(!reachedDest)
												{
													setDirectionDegTick(MathUtil.getAngle(xDest, yDest, this.getX(), this.getY()), 5f);

													if(getTicksAlive() == 100)
													{
														reachedDest = true;

														ThBulletColor[] colors = {ThBulletColor.YELLOW, ThBulletColor.ORANGE, ThBulletColor.WHITE };

														clearShader();
														setBullet(new ThBullet(ThBulletType.DISK, colors[(int) (Math.random() * colors.length)]));
														setDirectionDegTick(MathUtil.getAngle(this, boss), 4f);
													}
												}
												else
												{
													setDirectionDegTick(MathUtil.getAngle(this, boss), 4f);

													if(MathUtil.getDistance(this, boss) < 100)
														game.delete(this);
												}
											};
										};
										bullet.setShader(ShaderLibrary.GLOW.getProgram());
										bullet.setDirectionDegTick(MathUtil.getAngle(bullet.getX(), bullet.getY(), xDest, yDest), 4f);
										game.spawn(bullet);
									}
								}
								
								index += 2;
							}
						}
					});
				}
			}
		}
	}
}

