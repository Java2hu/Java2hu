package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.helpers.ZIndexHelper;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.IBulletType;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.ui.CircleHealthBar;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.Scheduler;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

/**
 * Seija Kijin (DDC)
 * Spell: 
 */
public class Seija extends AllStarBoss
{
	public static Seija newInstance(float x, float y)
	{
		String basedir = "enemy/seija/";
		
		int chunkHeight = 80 * 2;
		int chunkWidth = 64 * 2;

		Texture sprite = Loader.texture(Gdx.files.internal(basedir + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);

		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(basedir + "fbs.png")));
		Sprite nameTag = new Sprite(Loader.texture(Gdx.files.internal(basedir + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 6F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation rightStart = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 24F, 1,2,3,4,5,6);
		Animation rightLoop = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 24F, 7,8);
		Animation right = new MovementAnimation(rightStart, rightLoop, 8f);
		
		Animation leftStart = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight * 2, chunkHeight, chunkWidth, 24F, 1,2,3,4);
		Animation leftLoop = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight * 2, chunkHeight, chunkWidth, 24F, 5,6); // Last frame is broken...
		Animation left = new MovementAnimation(leftStart, leftLoop, 8f);
	
		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight * 3, chunkHeight, chunkWidth, 8F, 1,2,3,4), ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight * 3, chunkHeight, chunkWidth, 8F, 5,6,7,8), 8f);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(basedir + "bg.png")));
		
		bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
		
		Sprite bge = new Sprite(Loader.texture(Gdx.files.internal(basedir + "bge.png")));

		bge.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal("enemy/seija/bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Seija boss = new Seija(nameTag, fbs, bg, bge, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	public Sprite bg;
	public Sprite bge;

	public Seija(Sprite nametag, Sprite fullBodySprite, final Sprite bg, final Sprite bge, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(100, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(bg);
		addDisposable(bge);
		
		this.bg = bg;
		this.bge = bge;
	}
	
	public void spawnBackground(final BackgroundBossAura aura)
	{
		final Seija seija = this;
		
		Game.getGame().spawn(new Background(seija.bg.getTexture())
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return seija.bg;
					}
				}
				, 0F, 1F, 0.01F));
				setVelV(0.01f);
				setFrameBuffer(aura.getBackgroundBuffer());
				setZIndex(-6);
			}
			
			@Override
			public boolean isPersistant()
			{
				return seija.isOnStage();
			}
			
			@Override
			public void onDraw()
			{
				super.onDraw();
			}
		});
		
		Game.getGame().spawn(new Background(seija.bge.getTexture())
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return seija.bge;
					}
				}
				, 0F, 1F, 0.01F));
				
				setFrameBuffer(aura.getBackgroundBuffer());
				setZIndex(-5);
				setVelV(0.01f);
			}
			
			@Override
			public boolean isPersistant()
			{
				return seija.isOnStage();
			}
			
			@Override
			public void onDraw()
			{
				Game.getGame().batch.enableBlending();
				Game.getGame().batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR);
				
				super.onDraw();
				
				Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
		
		Game.getGame().spawn(new Background(seija.bge.getTexture())
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return seija.bge;
					}
				}
				, 0F, 1F, 0.01F));
				
				setFrameBuffer(aura.getBackgroundBuffer());
				setZIndex(-5);
				setVelV(0.01f);
			}
			
			@Override
			public boolean isPersistant()
			{
				return seija.isOnStage();
			}
			
			@Override
			public void onDraw()
			{
				float offset = 0.0306f;
			
				setStartU(offset);
				setEndU(1f + offset);
				
				setRotation(180f);
				
				Game.getGame().batch.enableBlending();
				Game.getGame().batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);
				
				super.onDraw();
				
				Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		this.setAuraColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1));
		Game.getGame().batch.setColor(getAuraColor());
	}
	
	@Override
	public void onDraw()
	{
		boolean wasEnabled = false;
		
		final boolean moving = lastX != x || lastY != y;
		
		if(moving)
		{
			wasEnabled = playSpecial;
			playSpecial = false;
		}
		
		super.onDraw();
		
		if(moving)
		{
			playSpecial = wasEnabled;
		}
	}
	
	private float damageModifier = 1F;
	
	@Override
	public float getDamageModifier()
	{
		return damageModifier;
	}
	
	@Override
	public void setDamageModifier(float damageModifier)
	{
		this.damageModifier = damageModifier;
	}
	
	private SeijaSpell spell;
	
	@Override
	public void onDelete()
	{
		if(spell != null)
			spell.revertRotation();
		
		super.onDelete();
	}
	
	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Seija boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, 60);

				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						g.spawn(boss);
						
						bar.setObject(new CircleHealthBar(boss));
						g.spawn(bar.getObject());
						
						bar.getObject().addSplit(0.7f);

						AllStarUtil.introduce(boss);
						
						boss.setHealth(0.1f);
						boss.healUp();
						
						BossUtil.addBossEffects(boss, AllStarUtil.from255RGB(62f, 225f, 195f), boss.getBgAuraColor());

						g.startSpellCard(new SeijaNonSpell(boss));
					}
				}, 90);
			}
		}, 5);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !Game.getGame().getStageObjects().contains(boss);
			}
		});
		
		scheme.doWait();

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss.isDead();
			}
		});
		
		scheme.doWait();
		
		boss.setHealth(boss.getMaxHealth());
		bar.getObject().split();
		
		final SaveableObject<SeijaSpell> spell = new SaveableObject<SeijaSpell>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				g.clear(ClearType.ALL_OBJECTS, ClearType.SPELLS, ClearType.TASKS);
				AllStarUtil.presentSpellCard(boss, "Twisted Mind \"Boxed Fear\"");
				BossUtil.moveTo(boss, (Game.getGame().getMaxX() - Game.getGame().getMinX()) / 2, Game.getGame().getHeight() - 300, 1000);
				
				final SeijaSpell card = new SeijaSpell(boss);
				spell.setObject(card);
				g.startSpellCard(spell.getObject());
				
				boss.spell = spell.getObject();
				
				boss.spawnBackground(scheme.getBossAura());
				BossUtil.spellcardCircle(boss, card, scheme.getBossAura());
			}
		}, 5);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().clearCircle(800f, boss, ClearType.ALL);
			}
		}, 1);
		
		scheme.waitTicks(2);
		
		boss.playSpecial(false);
		SchemeUtil.deathAnimation(scheme, boss, boss.getAuraColor());
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				ObjectUtil.deathAnimation(boss);
				
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
			}
		}, 5);
		
		scheme.waitTicks(10); // Prevent concurrency issues.
	}
	
	public static class SeijaNonSpell extends Spellcard
	{
		public SeijaNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(26));
		}
		
		float red = 0F;
		float blue = 0F;
		
		private ZIndexHelper indexer = new ZIndexHelper(10000);
		
		@Override
		public void tick(int tick)
		{	
			int roundTime = 300;
			
			tick += roundTime - 100;
			
			if(tick % 10 == 0 && tick > roundTime && tick % roundTime < 100)
				TouhouSounds.Enemy.BULLET_2.play(0.3F);
			
			if(tick % 200 == 0)
				getOwner().getPathing().path(new SimpleTouhouBossPath(getOwner()));
			
			if(tick % roundTime > roundTime - 40)
				((Boss)getOwner()).playSpecial(true);
			
			if(tick % roundTime != 0 || tick < roundTime)
				return;
			
			final float x = getOwner().getX();
			final float y = getOwner().getY();
			final float increase = 30F;
			final float speed = 2.5F;
			
			int between = 0;
			
			for(int i = 0; i < 360; i += 6F)
				for(float nr = 0; nr < 20; nr += 1.1F)
				{
					float sin = (float) (Math.sin(Math.toRadians(i)) * nr * 20);
					float cos = (float) (Math.cos(Math.toRadians(i)) * nr * 20);
					
					final float finalRed = red;
					final float finalI = i;
					final float finalNr = nr;

					final Bullet redBullet = new Bullet(new ThBullet(ThBulletType.RICE, ThBulletColor.RED_DARK), x + cos, y + sin)
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getTicksAlive() == 120)
							{
								setBullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.RED_DARK));
								spawnAnimation();

								setDirectionRadsTick((float) Math.toRadians(finalRed + finalI + finalNr), 5F);
								setRotationFromVelocity(90f);
								
								if(!Scheduler.isTracked("seija1", null))
								{
									Scheduler.trackMillis("seija1", null, (long) 100);

									TouhouSounds.Enemy.RELEASE_1.play(0.3F);
								}
							}
							
							if(getTicksAlive() == 180)
							{
								setBullet(new ThBullet(ThBulletType.RICE, ThBulletColor.RED_DARK));
								spawnAnimation();
								
								setDirectionRadsTick((float) Math.toRadians(finalRed + finalI + finalNr), speed);
								setRotationFromVelocity(90f);

								if(!Scheduler.isTracked("seija2", null))
								{
									Scheduler.trackMillis("seija2", null, (long) 120);

									TouhouSounds.Enemy.RELEASE_3.play(0.6F);
								}
							}
						}
					};
					redBullet.setDirectionRadsTick((float) Math.toRadians(red + i + nr), speed);
					redBullet.setRotationDeg(red + i + 90F);
					redBullet.setRotationFromVelocity();
					indexer.index(redBullet);
					
					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Game.getGame().spawn(redBullet);
						}
					}, (int) (nr * 5));
				}
			
			for(int i = 360; i > 0; i -= 6F)
				for(float nr = 0; nr < 20; nr += 2F)
				{
					float sin = (float) (Math.sin(Math.toRadians(i)) * nr * 20);
					float cos = (float) (Math.cos(Math.toRadians(i)) * nr * 20);
					
					final float finalRed = red;
					final float finalI = i;
					final float finalNr = nr;

					final Bullet blueBullet = new Bullet(new ThBullet(ThBulletType.RICE, ThBulletColor.BLUE_DARK), x - cos, y - sin)
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getTicksAlive() == 200)
							{
								setBullet(new ThBullet(ThBulletType.RAIN, ThBulletColor.BLUE_DARK));
								setDirectionRadsTick((float) Math.toRadians(finalRed + finalI + finalNr), 5F);
								setRotationFromVelocity();
								spawnAnimation();
							}
							
							if(getTicksAlive() == 260)
							{
								setBullet(new ThBullet(ThBulletType.RICE, ThBulletColor.BLUE_DARK));
								setDirectionRadsTick((float) Math.toRadians(finalRed + finalI + finalNr), speed);
								setRotationFromVelocity();
								spawnAnimation();
							}
						}
						
						@Override
						public void setBullet(IBulletType type)
						{
							super.setBullet(type);
						}
					};
					blueBullet.setDirectionRadsTick((float) -Math.toRadians(red + i + nr), speed);
					blueBullet.setRotationDeg(-(red + i + 90F));
					indexer.index(blueBullet);

					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Game.getGame().spawn(blueBullet);
						}
					}, (int) (nr * 5));
				}
		}
	}
	
	public static class SeijaSpell extends Spellcard
	{
		float rotation = 0F;
		float lastRotation = 0F;
		
		public SeijaSpell(StageObject owner)
		{
			super(owner);
			
			setSpellcardTime(Duration.seconds(40));
			
			((Seija)owner).setDamageModifier(0.5F);
			
			Game.getGame().setTransformation(new Setter<Float>()
			{
				@Override
				public void set(Float f)
				{
					J2hGame g = Game.getGame();
					
					if(g.isPaused())
						return;
					
					OrthographicCamera c = g.camera.camera;
					// Rotate around: g.getBoundary().getWidth() / 2, g.getBoundary().getHeight() / 2
					c.rotate(rotation - lastRotation);
			
//					for(int i = 0; i < 10; i++)
//					{
//						dummySprite.setSize(g.getBoundary().getWidth() - (2 * i), g.getBoundary().getHeight() - (2 * i));
//						dummySprite.setPosition(i, i);
//						dummySprite.draw(g.batch);
//					}
					
					lastRotation = rotation;
					rotation += f * ((Math.random() * 0.1F + 0.3F) * 1.15 * 60);
				}
			});
			
			final Seija seija = (Seija)getOwner();
			
			Game.getGame().spawn(new DrawObject()
			{
				private Texture dummy = ImageUtil.makeDummyTexture(Color.BLACK, 1, 1);
				private Sprite dummySprite = new Sprite(dummy);
				
				{
					setZIndex(J2hGame.GUI_Z_ORDER + 1);
				}
				
				@Override
				public void onDraw()
				{
					J2hGame g = Game.getGame();
					OrthographicCamera c = g.camera.camera;
					
					Rectangle rect = g.getBoundary();
					
					dummySprite.setSize((int)rect.getWidth(), (int)rect.getHeight());
					
					dummySprite.setPosition(0, -rect.getHeight());
					dummySprite.draw(g.batch);
					
					dummySprite.setPosition(-rect.getWidth(), 0);
					dummySprite.draw(g.batch);
					
					dummySprite.setPosition(rect.getWidth(), 0);
					dummySprite.draw(g.batch);
					
					dummySprite.setPosition(0, rect.getHeight());
					dummySprite.draw(g.batch);
					
//					dummySprite.setPosition(0, -rect.getHeight());
//					dummySprite.draw(g.batch);
					
//					dummySprite.setRegion((int)0, (int)-rect.getHeight(), (int)rect.getWidth(), (int)rect.getHeight());
//					dummySprite.draw(g.batch);
//					
//					dummySprite.setRegion((int)-rect.getWidth(), (int)0, (int)rect.getWidth(), (int)rect.getHeight());
//					dummySprite.draw(g.batch);
//					
//					
//					dummySprite.setRegion((int)0, (int)rect.getHeight(), (int)rect.getWidth(), (int)rect.getHeight());
//					dummySprite.draw(g.batch);
//					
//					dummySprite.setRegion((int)rect.getWidth(), (int)0, (int)rect.getWidth(), (int)rect.getHeight());
//					dummySprite.draw(g.batch);
				}
			});
			
			TouhouSounds.Enemy.HUM_1.play(1F);
		}
		
		public void revertRotation()
		{
			Game.getGame().setTransformation(null);
			Game.getGame().camera.camera.rotate(360 - lastRotation);
		}
		
		int rotationAmount = 0;
		
		@Override
		public void tick(int tick)
		{	
			if(tick % 200 == 0 && tick > 60)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 250, Game.getGame().getHeight() - 50, 900);
			
			if(tick == 200 - 30)
				BossUtil.cloudSpecial(getOwner(), 30);
			
			if(tick % 200 != 0)
				return;
			
			if(rotationAmount < 3000)
				rotationAmount++;
			
			for(int i = 0; i < 360; i += 10)
				try
				{
					final int finalI = i;
					
					Game.getGame().spawn(new SeijaGravityBullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.RED).getAnimation(), new Getter<Float>()
					{
						@Override
						public Float get()
						{
							return rotation + finalI;
						}
					}, getOwner().getX(), getOwner().getY(), 0.4F, 0.1F, 3F + (float)rotationAmount / (float)100)
					{
						{
							this.setGlowing();
							this.setDeletionColor(Color.RED);
						}
					});
					
					if(tick == 200)
						Game.getGame().spawn(new SeijaGravityBullet(new ThBullet(ThBulletType.BALL_1, ThBulletColor.RED).getAnimation(), new Getter<Float>()
						{
							@Override
							public Float get()
							{
								return rotation + finalI;
							}
						}, getOwner().getX(), getOwner().getY(), 0.4F, 0.1F, 2F + (float)rotationAmount / (float)100)
						{
							{
								this.setGlowing();
								this.setDeletionColor(Color.RED);
							}
						});
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}
	}
	
	public static class SeijaGravityBullet extends Bullet
	{
		private float decrease;
		private float terminalVelocity;
		private float speed = 0F;
		private Getter<Float> gravityGetter;
		
		public SeijaGravityBullet(Animation scheme, Getter<Float> gravityGetter, float x, float y, float start, float decrease, float terminalVelocity)
		{
			super(scheme, x, y);
			
			setDecrease(decrease);
			setTerminalVelocity(terminalVelocity);
			
			this.speed = start;
			this.gravityGetter = gravityGetter;
		}

		public float getTerminalVelocity()
		{
			return terminalVelocity;
		}

		public void setTerminalVelocity(float terminalVelocity)
		{
			this.terminalVelocity = terminalVelocity;
		}

		public float getDecrease()
		{
			return decrease;
		}

		public void setDecrease(float decrease)
		{
			this.decrease = decrease;
		}

		@Override
		public void setX(float x)
		{
			if(!Game.getGame().inBoundary(x, y))
				return;
			
			super.setX(x);
		}
		
		@Override
		public void setY(float y)
		{
			if(!Game.getGame().inBoundary(x, y))
				return;
			
			super.setY(y);
		}
		
		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
			
			if(tick % 10 == 0)
				if(!Scheduler.isTracked("seija1", null))
				{
					Scheduler.trackMillis("seija1", null, (long) 1000);

					TouhouSounds.Enemy.RELEASE_1.play(0.3F);
				}
			
			float gravity = MathUtil.normalizeDegree(gravityGetter.get());
			
			if(speed < terminalVelocity)
				speed += decrease;
			
			setDirectionRadsTick((float) Math.toRadians(-gravity - 90F), speed);
		}
	}

}

