package java2hu.allstar.enemies.day1;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
import java2hu.background.HorizontalScrollingBackground;
import java2hu.background.ScrollingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.GravityBullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.Scheduler;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Wakasagihime extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Wakasagihime";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Deep Unknown \"Small Fry\"";
	
	private Setter<BackgroundBossAura> background;
	
	public Wakasagihime(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		FileHandle folder = Gdx.files.internal("enemy/" + BOSS_NAME.toLowerCase() + "/");
		
		int chunkHeight = 160;
		int chunkWidth = 160;

		Texture sprite = Loader.texture(folder.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		
		Sprite fbs = new Sprite(Loader.texture(folder.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(folder.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4,5,6);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 7,8,9,10,11,12);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);
		
		left.setPlayMode(PlayMode.LOOP);
		right.setPlayMode(PlayMode.LOOP);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 13,14,15,16,17,18,19,20);
		special.setPlayMode(PlayMode.NORMAL);

		final Sprite bg = new Sprite(Loader.texture(folder.child("bg.png")));
		bg.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		bg.setRegionWidth(bg.getRegionWidth() * 2);
		bg.setRegionHeight(bg.getRegionHeight() * 2);
		
		final Sprite bge1 = new Sprite(Loader.texture(folder.child("bge1.png")));
		bge1.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		bge1.setRegionWidth(bge1.getRegionWidth() * 2);
		bge1.setRegionHeight(bge1.getRegionHeight() * 2);
		
		final Sprite bge1d = new Sprite(Loader.texture(folder.child("bge1d.png")));
		bge1d.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		bge1d.setRegionWidth(bge1d.getRegionWidth() * 2);
		bge1d.setRegionHeight(bge1d.getRegionHeight() * 2);
		
		final Sprite bge2 = new Sprite(Loader.texture(folder.child("bge2.png")));
		bge2.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		bge2.setRegionWidth(bge2.getRegionWidth() * 2);
		bge2.setRegionHeight(bge2.getRegionHeight() * 2);

		Music bgm = new J2hMusic(Gdx.audio.newMusic(folder.child("bgm.mp3")));
		bgm.setLooping(true);
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		addDisposable(bge1);
		addDisposable(bge1d);
		addDisposable(bge2);
		
		setAuraColor(new Color(52 / 256f, 99 / 256f, 229 / 256f, 1.0f));
		setBgAuraColor(AllStarUtil.from255RGB(40, 161, 220));

		final Wakasagihime boss = this;
		
		background = new Setter<BackgroundBossAura>()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void set(final BackgroundBossAura aura)
			{
				final FrameBuffer combineBuffer = new FrameBuffer(Format.RGBA8888, Game.getGame().getWidth(), Game.getGame().getHeight(), false);
				
				final float fadeSpeed = 0.01f;
				
				
				game.spawn(new ClearBackground(-15)
				{
					{
						setFrameBuffer(aura.getBackgroundBuffer());
						
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return getSprite();
							}
						}, 0, 1f, 0.01F));
					}
				});
				
				Game.getGame().spawn(new DrawObject()
				{
					{
						setFrameBuffer(aura.getBackgroundBuffer());
						
						setZIndex(-5);
					}
					
					Sprite sprite;
					
					@Override
					public void onDraw()
					{
						if(sprite == null)
						{
							sprite = new Sprite(combineBuffer.getColorBufferTexture());
							
							addEffect(new FadeInSprite(new Getter<Sprite>()
							{
								@Override
								public Sprite get()
								{
									return sprite;
								}
							}, 0f, 1f, fadeSpeed));
						}

						game.batch.flush();
						sprite.setColor(1f, 1f, 1f, 1f);
						
						sprite.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						sprite.draw(game.batch);
						
						game.batch.enableBlending();
					}
					
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
				
				Game.getGame().spawn(new ScrollingBackground(bge1d, 0.2f, 0f)
				{
					{
						bge1d.setAlpha(0.4f);
						
						setFrameBuffer(combineBuffer);
						
						setZIndex(-10);
						
						this.y += 50;
					}
					
					@Override
					public void onDraw()
					{
						super.onDraw();
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
				
				Game.getGame().spawn(new ScrollingBackground(bg, -0.3f, -0.3f)
				{
					{
						bg.setAlpha(0.6f);
						
						setFrameBuffer(combineBuffer);
						
						setZIndex(-11);
					}
					
					@Override
					public void onDraw()
					{
						super.onDraw();
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
				
				Game.getGame().spawn(new HorizontalScrollingBackground(bge1, 0.2f, false)
				{
					{
						bge1.setAlpha(1f);
						
						setFrameBuffer(combineBuffer);
						
						setZIndex(-12);
					}
					
					@Override
					public void onDraw()
					{
						super.onDraw();
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
				
				Game.getGame().spawn(new ScrollingBackground(bge2, 0.3f, -0.3f)
				{
					{
						bge2.setAlpha(2f);
						
						setFrameBuffer(combineBuffer);
						
						setZIndex(-13);
					}
					
					@Override
					public void onDraw()
					{
						super.onDraw();
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
			}
		};
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
	public float getDamageModifier()
	{
		if(getTicksAlive() < 5 * 60)
			return 0.5F;
		
		return super.getDamageModifier();
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Wakasagihime boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, boss.getAuraColor(), boss.getBgAuraColor(), 60);
				
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
						
						g.addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								BossUtil.addBossEffects(boss, getAuraColor(), getBgAuraColor());
								BossUtil.startFight(boss);

								Game.getGame().startSpellCard(new WakasagihimeNonSpell(boss));
							}
						}, 40);
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

		SchemeUtil.waitForDeath(scheme, boss);
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL_OBJECTS, ClearType.SPELLS, ClearType.TASKS);
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				background.set(scheme.getBossAura());
				
				final WakasagihimeSpell card = new WakasagihimeSpell(boss);
				
				game.startSpellCard(card);
				System.out.println("Circle");
				BossUtil.spellcardCircle(boss, card, scheme.getBossAura());
			}
		}, 1);
		
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
	
	public static class WakasagihimeNonSpell extends Spellcard
	{	
		public WakasagihimeNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(25));
		}
		
		@SuppressWarnings("unused")
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Wakasagihime boss = (Wakasagihime) getOwner();

			if(tick == 0)
				boss.setDamageModifier(0f);
			
			if(tick < 60)
				return;
			
			if(tick == 60)
			{
				boss.setDamageModifier(0.8f);
				tick += 340; // Skip to the next cyclus
			}
			
			if(tick % 300 == 220)
			{
				TouhouSounds.Enemy.BULLET_4.play(1f);
				
				for(int i = 180; i < 360; i += 2)
				{
					GravityBullet bullet = new GravityBullet(new ThBullet(ThBulletType.UNKNOWN_3, ThBulletColor.BLUE).getAnimation(), boss.getX(), boss.getY(), 0.001f, 6f)
					{
						boolean changed = false;
						
						@Override
						public void update(long tick)
						{
							super.update(tick);
							
							if(!changed && getVelocityYTick() >= 0)
							{
								setVelocityXTick((float) (Math.random() * 10f - 5f));
								
								changed = true;
							}
							
							setRotationFromVelocity(-90f);
						}
					};

					bullet.getCurrentSprite().setScale(1.5f, 3f);
					bullet.setDirectionRadsTick((float) Math.toRadians(i), 5f);
					bullet.setGlowing();
					
					Game.getGame().spawn(bullet);
				}
			}
			
			if(tick % 400 == 300)
			{
				final Rectangle movableArea = Game.getGame().getBoundary();
				movableArea.setHeight(200);
				movableArea.setWidth(300);
				
				movableArea.setY(Game.getGame().getMaxY() - movableArea.getHeight() - 50);
				movableArea.setX(Game.getGame().getMaxX() - 400 - (Game.getGame().getMinX() + 400));
				
				BossUtil.moveAroundRandomly(boss, movableArea, 500);
			}
			
			if(tick % 400 == 330)
				BossUtil.cloudSpecial(boss, Color.BLUE, boss.getAuraColor(), 30);
			
			if(tick % 400 <= 300 && tick % 6 == 0)
			{
				TouhouSounds.Enemy.BULLET_1.play(0.4f);
			}
			
			if(tick % 400 == 0)
				for(int i = 0; i < 2 * 640; i += 8)
				{
					final boolean back = i > 640;
					
					final int rotation = i;
					
					Runnable run1 = new Runnable()
					{
						@Override
						public void run()
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.BLUE_DARK), (float) (boss.getX() + Math.cos(Math.toRadians(rotation)) * 50), (float) (boss.getY() + Math.sin(Math.toRadians(rotation)) * 50));
							bullet.setDirectionRadsTick((float) Math.toRadians(rotation - 90), 5f);
							bullet.setRotationFromVelocity(270f);
							Game.getGame().spawn(bullet);
						};
					};
					
					int time = (int) (i / 360f * 60);
					
					if(back)
						time = (int) (time + (i - 640) / 360f * 60);
					
					Game.getGame().addTaskGame(run1, time);
					
					final int totalTimes = 5;
					int increment = 6;
					
					for(int addTime = increment; addTime <= increment * totalTimes; addTime += increment)
					{
						final int finalAddTime = addTime;
						
						final boolean last = addTime + increment > increment * totalTimes;
						
						Runnable run2 = new Runnable()
						{
							@Override
							public void run()
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.POINTER, last ? ThBulletColor.BLUE_DARK : ThBulletColor.BLUE), (float) (boss.getX() + Math.cos(Math.toRadians(rotation)) * 50), (float) (boss.getY() + Math.sin(Math.toRadians(rotation)) * 50));
								bullet.setDirectionRadsTick((float) Math.toRadians(rotation - (90 + finalAddTime / 3f)), 5f);
								bullet.setRotationFromVelocity(270f);
								Game.getGame().spawn(bullet);
							};
						};
						
						Game.getGame().addTaskGame(run2, time + (back ? -1 : 1) * addTime);
					}
				}
		}
	}

	public static class WakasagihimeSpell extends Spellcard
	{
		public WakasagihimeSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(50));
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Wakasagihime boss = (Wakasagihime) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0f);
			
			if(tick == 60)
			{
				Bullet fish = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.BLUE_DARK), boss.getX(), boss.getY())
				{
					float alpha = 1f;
					
					@Override
					public void update(long tick)
					{
						super.update(tick);
						
						getCurrentSprite().setAlpha(alpha);
						
						alpha -= 0.01f;
						
						if(alpha <= 0)
						{
							TouhouSounds.Stage.WATER.play();
							Game.getGame().delete(this);
						}
					}
				};
				
				fish.setDirectionRadsTick((float) Math.toRadians(180 * Math.random()), 2f);
				fish.useSpawnAnimation(false);
				fish.useDeathAnimation(false);
				fish.setRotationFromVelocity(-90f);
				
				game.spawn(fish);
			}
			
			float y = (float) (player.getY() + (Math.random() > 0.5 ? -1 : 1) * (Math.random() * 260 + 200));
			float x = (float) (player.getX() + Math.cos(Math.toRadians(Math.random() * 360)) * 500);
			
			float rotation = MathUtil.getAngle(x, y, player.getX(), player.getY());
			boolean firstFish = tick == 150;
			
			if(firstFish)
			{
				boss.setDamageModifier(0.4f);
				
				x = player.getX();
				
				y = player.getY() - 50;
				
				rotation = MathUtil.getAngle(x, y, player.getX(), player.getY());
			}
			
			if(tick > 120 && tick % 300 == 0)
			{
				boss.getPathing().setCurrentPath(new SimpleTouhouBossPath(boss));
			}
			
			if(tick > 120 && tick % 150 == 0)
			{
				TouhouSounds.Stage.WATER.play();
				
				float posX = (float) (x + Math.cos(Math.toRadians(rotation - 90f)));
				float posY = (float) (y + Math.sin(Math.toRadians(rotation - 90f)));
				
				final float waveOffset = 8;
				
				if(!firstFish)
				for(int i = 0; i < 360; i += waveOffset)
				{
					Bullet bubble = new Bullet(new ThBullet(ThBulletType.BULLET, ThBulletColor.BLUE), posX, posY);
					bubble.setDirectionRadsTick((float) Math.toRadians(i), 2f);
					bubble.setRotationFromVelocity(-90f);
					game.spawn(bubble);
				}
				
				int body = 100;
				int tail = 20;
				
				final SaveableObject<Boolean> hasBroken = new SaveableObject<Boolean>();
				hasBroken.setObject(false);
				
				for(int i = 0; i < body + tail; i += 1)
				{
					int size = i;
					
					if(size >= body / 2 && size < body)
						size = body - i;
					else if(size >= body)
						size = i - body;
					
					size += 5f * size;
					
					final boolean isTail = size >= body;
					
					for(float amount = -(size * 0.5f); amount <= size * 0.5f; amount += 10)
					{
						if(i < body / 2 && amount > -Math.min(body / 2, MathUtil.getDifference(i, body / 2)) && amount < Math.min(body / 2, MathUtil.getDifference(i, body / 2)))
							continue;
						
						final Bullet fish = new Bullet(new ThBullet(ThBulletType.POINTER, ThBulletColor.BLUE_DARK), (float) (x + Math.cos(Math.toRadians(rotation - 90f)) * amount), (float) (y + Math.sin(Math.toRadians(rotation - 90f)) * amount))
						{
							float color = -0.5f;
							
							@Override
							public void update(long tick)
							{
								super.update(tick);
								
								getCurrentSprite().setColor(new Color(color, color, color, (color + 0.5f) * 5f));
								
								if(color < 1)
									color += 0.08f;
								
								if(isTail && !game.inBoundary(getX(), getY()) && tick % 70 == 0 && !hasBroken.getObject())
								{
									hasBroken.setObject(true);
									
									for(int i = 0; i < 360; i += waveOffset)
									{
										Bullet bubble = new Bullet(new ThBullet(ThBulletType.BULLET, ThBulletColor.BLUE), getX(), getY());
										bubble.setDirectionRadsTick((float) Math.toRadians(i), 2f);
										bubble.setRotationFromVelocity(-90f);
										game.spawn(bubble);
									}

									if(!Scheduler.isTracked("break", "break"))
									{
										TouhouSounds.Enemy.BREAK_2.play();
										Scheduler.track("break", "break", (long) 10);
									}
								}
							}
							
							public boolean hasHitbox()
							{
								return color >= 0.2f;
							}
							
							@Override
							public void onDraw()
							{
								super.onDraw();
								
//								if(hasHitbox())
//								{
//									Game.getGame().batch.end();
//									
//									Game.getGame().shape.begin(ShapeType.Line);
//									
//									HitboxUtil.drawHitbox(getCurrentSprite().getHitbox());
//									
//									Game.getGame().shape.end();
//									
//									Game.getGame().batch.begin();
//								}
							}
							
							@Override
							public void checkCollision()
							{
								if(!hasHitbox())
									return;
								
								super.checkCollision();
							}
							
							@Override
							public boolean doDelete()
							{
								if(color < 0)
									return false;
								
								return super.doDelete();
							}
						};
						
						fish.getCurrentSprite().setColor(new Color(-0.5f, -0.5f, -0.5f, 0));
						fish.useSpawnAnimation(false);
						fish.setDirectionRadsTick((float) Math.toRadians(rotation), 10f);
						fish.setRotationFromVelocity(270f);
						fish.setGlowing();
						
						game.addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								game.spawn(fish);
							}
						}, i);
					}
				}
			}
		}
	}
}

