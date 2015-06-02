package java2hu.allstar.enemies.day8;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
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
import java2hu.util.ImageSplitter;
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.PathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yumeko extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Yumeko";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Illusionary Existence \"Maid of Makai\"";
	
	public static Yumeko newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 108;
		int chunkWidth = 84;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = idle;
		Animation right = idle;

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 4);
		special.setPlayMode(PlayMode.LOOP);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Yumeko boss = new Yumeko(100, nameTag, bg, fbs, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	public Sprite bg;
	
	public Yumeko(float maxHealth, TextureRegion nametag, final Sprite bg, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		
		this.bg = bg;
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
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
		final Yumeko boss = this;
		
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
						BossUtil.backgroundAura(boss, boss.getBgAuraColor());
						
						Game.getGame().startSpellCard(new YumekoNonSpell(boss));
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
				
				Game.getGame().startSpellCard(new YumekoSpell(boss));
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
	
	public static class YumekoNonSpell extends Spellcard
	{	
		public YumekoNonSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Yumeko boss = (Yumeko) getOwner();
			
			if(tick % 200 == 160)
			{
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
			}
			
			{
				float interval = 50;
				if(tick % interval == 0)
				{
					final boolean aimed = false;

					float centerX = boss.getX();
					float centerY = 1000;

					int index = 0;
					float min = 180 + 25;
					float max = 360 - 25;
					float increment = aimed ? 20 : 5;
					float maxIndex = (max - min) / increment;
					for(float i = min; i < max; i += increment)
					{
						index++;
						final float rad = (float) Math.toRadians(i);
						final float xDest = (float) (centerX + Math.cos(rad) * 300f);
						final float yDest = (float) (centerY + Math.sin(rad) * 300f);

						int rightDelay = aimed ? (int) (index * 4f) : (int) (index * 2f);
						float invertedIndex = maxIndex - index;
						int leftDelay = aimed ? (int) (invertedIndex * 4f) : (int) (invertedIndex * 2f);

						final int delay = tick % (2 * interval) < interval ? rightDelay : leftDelay;

						Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, aimed ? ThBulletColor.RED : ThBulletColor.BLUE), boss.getX(), boss.getY())
						{
							float sizeMultiplier = 0;
							
							@Override
							public void onUpdateDelta(float delta)
							{
								super.onUpdateDelta(delta);
								
								if(getTicksAlive() < 100 + delay)
								{
									getCurrentSprite().rotate(1800f * delta);

									if(getTicksAlive() > 60 + delay)
										sizeMultiplier = (getTicksAlive() - (60 + delay)) / 40f;
								}
								
								if(aimed)
								{
									if(getTicksAlive() > 140 + delay && getTicksAlive() < 200 + delay)
										getCurrentSprite().rotate(1800f);
								}
							}

							float saved = 0;
							
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								if(getTicksAlive() == 1)
								{
									PathUtil.moveTo(this, xDest, yDest, 60 + delay);
									setRotationFromVelocity(180);
								}

								if(aimed)
								{
									if(getTicksAlive() == 100 + delay)
									{
										setDirectionDegTick(MathUtil.getAngle(this, player), 16f);
										setRotationFromVelocity(-90);
										sizeMultiplier = 0f;
									}

									if(getTicksAlive() == 140 + delay)
									{
										setDirectionDegTick(0, 0);
									}

									if(getTicksAlive() > 140 + delay && getTicksAlive() < 200 + delay)
									{
										float fullTime = 60f / 60f;
										
										sizeMultiplier = fullTime - saved;
									}

									if(getTicksAlive() == 200 + delay)
									{
										sizeMultiplier = 0f;
										setDirectionDegTick(MathUtil.getAngle(this, player), 16f);
										setRotationFromVelocity(-90);
									}
								}
								else
								{
									if(getTicksAlive() == 100 + delay)
									{
										if(game.getTick() % 3 == 0)
											TouhouSounds.Enemy.BULLET_2.play(0.5f);
										
										setDirectionRadsTick(rad , -16f);
										setRotationFromVelocity(-90);
									}
								}
							}

							@Override
							public void onDraw()
							{
								super.onDraw();

								if(sizeMultiplier > 0)
								{
									game.batch.end();
									game.shape.begin(ShapeType.Line);
									game.shape.setColor(1, 1, 1, 1f);
									
									game.shape.circle(getX(), getY(), 200 * (1 - sizeMultiplier));

									game.shape.end();
									game.batch.begin();
								}
							}
						};

						game.spawn(bullet);
					}
				}
			}
			
			if(tick > 100 && tick % 100 == 0)
			{
				float[] pos = new float[] { -400,0, +400,0 };
				
				for(int i = 0; i < pos.length; i += 2)
				{
					float x = boss.getX() + pos[i];
					float y = boss.getY() + pos[i + 1];
					
					for(float angle = 0; angle < 360; angle += 60)
					{
						final float delay = angle / 30f * 5f;
						float rad = (float) Math.toRadians(angle);
						float size = 100;

						Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), (float) (x + Math.cos(rad) * size), (float) (y + Math.sin(rad) * size))
						{
							float sizeMultiplier = 0;

							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								if(getTicksAlive() == 100 + delay)
								{
									sizeMultiplier = 0f;
									setDirectionDegTick(MathUtil.getAngle(this, player), 16f);
									setRotationFromVelocity(-90);
									
									TouhouSounds.Enemy.BULLET_1.play(1f);
								}
							}
							
							float saved = 0;
							
							@Override
							public void onUpdateDelta(float delta)
							{
								super.onUpdateDelta(delta);
								
								if(getTicksAlive() < 100 + delay)
								{
									float fullTime = 40f / 60f;
									
									getCurrentSprite().rotate(1800f * delta);

									if(getTicksAlive() > 60 + delay)
									{
										sizeMultiplier = fullTime - saved;
										saved += delta;
									}
								}
							}

							@Override
							public void onDraw()
							{
								super.onDraw();

								if(sizeMultiplier > 0)
								{
									game.batch.end();
									game.shape.begin(ShapeType.Line);
									game.shape.setColor(1, 1, 1, 1f);
									
									game.shape.circle(getX(), getY(), 200 * sizeMultiplier);

									game.shape.end();
									game.batch.begin();
								}
							}
						};

						game.spawn(bullet);
					}
				}
			}
		}
	}

	public static class YumekoSpell extends Spellcard
	{
		public YumekoSpell(final StageObject owner)
		{
			super(owner);
			
			final Sprite bg = ((Yumeko)owner).bg;
			
			Game.getGame().spawn(new DrawObject()
			{
				private Texture black = ImageUtil.makeDummyTexture(Color.BLACK, 1, 1);
				
				{
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
					return owner.isOnStage();
				}
			});
		}
		
		float lastAngle = 0;
		float size = 0;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Yumeko boss = (Yumeko) getOwner();
			
			float minSize = 200;
			final float maxSize = 490;
			final float centerX = Game.getGame().getWidth()/2f;
			final float centerY = Game.getGame().getHeight()/2f;
			
			if(tick == 0)
			{
				boss.setDamageModifier(0.45f);
				float angle = 90;
				PathUtil.moveTo(boss, (float) (centerX + Math.cos(Math.toRadians(angle)) * maxSize), (float) (centerY - 40 + Math.sin(Math.toRadians(angle)) * maxSize), 60);
			}
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			if(tick < 100 && tick % 8 == 0)
				TouhouSounds.Enemy.SPAWN.play();
			
			if(tick == 0)
			{
				boss.playSpecial(true);
				
				size = minSize + (maxSize - minSize);
				
				boolean[] bools = { true, false };
				
				for(final boolean bool : bools)
				{
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), boss.getX(), boss.getY())
					{
						float angle = 90;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							angle += bool ? 2 : -2;
							
							setX((float) (centerX + Math.cos(Math.toRadians(angle)) * maxSize));
							setY((float) (centerY + Math.sin(Math.toRadians(angle)) * maxSize));
							setRotationDeg(angle + (bool ? 0 : -180));
							
							if(getTicksAlive() < 100 && getTicksAlive() % 4 == 0)
							{
								final float finalAngle = angle;
								
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.ORB, ThBulletColor.RED), getX(), getY())
								{
									@Override
									public void onUpdate(long tick)
									{
										super.onUpdate(tick);
										
										setX((float) (centerX + Math.cos(Math.toRadians(finalAngle)) * size));
										setY((float) (centerY + Math.sin(Math.toRadians(finalAngle)) * size));
									};
								};
								
								bullet.setZIndex((int) (bullet.getZIndex() + getTicksAlive()));
								game.spawn(bullet);
							}
						}
					};

					game.spawn(bullet);
				}
			}
			
			if(tick < 140)
				return;
			
			tick -= 140;
			
			if(tick == 0)
			{
				boss.playSpecial(true);
				TouhouSounds.Enemy.RELEASE_3.play();
			}
			
			float sub = (tick - 140) / 10f;
			size = minSize + Math.max(0, maxSize - minSize - sub);

			if(tick % 200 == 0)
			{
				float maxPosSize = size + 100;
				float minPosSize = size - 50;
				float sizeX = (float) (minSize + Math.random() * (maxPosSize - minPosSize));
				float sizeY = (float) (minSize + Math.random() * (maxPosSize - minPosSize));
				float angle = lastAngle + (float) ((Math.random() * 2 - 1) * 40f);
				angle = Math.min(120, angle);
				angle = Math.max(60, angle);
				float rad = (float) Math.toRadians(angle);
				float x = (float) (centerX + Math.cos(rad) * sizeX);
				float y = (float) (centerY + Math.sin(rad) * sizeY);
				System.out.println(angle);
				lastAngle = angle;
				
				PathUtil.moveTo(boss, x, y, 60);
			}
			
			if(tick % 80 == 0)
			{
				boss.playSpecial(true);
				TouhouSounds.Enemy.BULLET_1.play();
				
				final float angle = MathUtil.getAngle(boss, player);
				final float finalTick = tick;
				
				for(int i = 0; i < 30; i += 7)
				{
					Runnable run = new Runnable()
					{
						@Override
						public void run() 
						{
							Bullet laser = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), boss.getX(), boss.getY());
							laser.setScale(2f, 2f);
							laser.setDirectionDegTick(angle, finalTick < 100 ? 10f : 15f);
							laser.setRotationFromVelocity(-90);
							game.spawn(laser);
						}
					};
					
					game.addTaskGame(run, i);
				}
			}
			
			if(tick % 80 == 30)
				boss.playSpecial(false);
			
			if(tick % 5 == 0)
			{
				TouhouSounds.Enemy.BULLET_3.play(0.6f);
				boolean opposite = false;
				float offset = (float) (Math.random() * 20f);
				
				float add = 15;
				add += (1 - (size - 300) / 200f) * 15;
				
				for(float angle = 0; angle < 720; angle += opposite ? add : 5)
				{
					opposite = angle > 360;
					
					if(!opposite && tick % 10 != 0)
						continue;
					
					if(opposite && tick % 70 < 30)
						continue;
					
					float newAngle = opposite ? 720 - angle : angle + offset;
					float rad = (float) Math.toRadians(newAngle);
					
					Bullet bullet = new Bullet(new ThBullet(opposite ? ThBulletType.KUNAI : ThBulletType.KNIFE, opposite ? ThBulletColor.BLUE : ThBulletColor.RED), (float) (centerX + Math.cos(rad) * size), (float) (centerY + Math.sin(rad) * size))
					{
						@Override
						public void onUpdateDelta(float delta)
						{
							getCurrentSprite().rotate(280 * delta);
							
							super.onUpdateDelta(delta);
						}
					};
					bullet.useSpawnAnimation(false);
					bullet.setDirectionDegTick(angle + (opposite ? tick / 8f % 360 : -90), opposite ? 2.5f : 20f);
					bullet.setRotationFromVelocity(-90);
					game.spawn(bullet);
				}
			}
		}
	}
}

