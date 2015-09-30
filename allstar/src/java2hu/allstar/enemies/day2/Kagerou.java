package java2hu.allstar.enemies.day2;

import java.util.HashMap;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.helpers.ZIndexHelper;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
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
import java2hu.util.SchemeUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Kagerou extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Kagerou";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Werewolf Sign \"Pack Hunt\"";
	final static String NONSPELL_NAME = "Hairy Wolf \"Heavily Shedding Fur\"";
	
	protected boolean werewolf = false;
	protected Animation werewolfAniRight;
	protected Animation werewolfAniLeft;
	
	public Texture bg;
	public Texture bge;
	
	public Kagerou(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
	
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 192;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nametag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 6F, 1,2,3,4,5,6,5,4,3,2,1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation leftStart = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 9,10,11);
		Animation leftLoop = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 12,13,14);
		Animation left = new StartupLoopAnimation(leftStart, leftLoop, 12F);
		
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, 0, 2 * chunkHeight, 192, 192, 3F, 1,2,3,4,5,6,7,6,5,4,3,2,1);
		special.setPlayMode(PlayMode.NORMAL);
		
		bg = Loader.texture(Gdx.files.internal(folder + "bg.png"));
		bge = Loader.texture(Gdx.files.internal(folder + "bge.png"));

		Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3")));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(19f);
		bgm.setLooping(true);
		
		chunkWidth = 256;
		chunkHeight = 128;
		
		Array<HitboxSprite> sprites = new Array<HitboxSprite>();
		
		sprites.add(new HitboxSprite(new Sprite(sprite, 384, 576, chunkWidth, chunkHeight)));
		sprites.add(new HitboxSprite(new Sprite(sprite, 384 + chunkWidth, 576, chunkWidth, chunkHeight)));
		sprites.add(new HitboxSprite(new Sprite(sprite, 384, 576 + chunkHeight, chunkWidth, chunkHeight)));
		sprites.add(new HitboxSprite(new Sprite(sprite, 384 + chunkWidth, 576 + chunkHeight, chunkWidth, chunkHeight)));
		sprites.add(new HitboxSprite(new Sprite(sprite, 384, 576 + 2 * chunkHeight, chunkWidth, chunkHeight)));
		sprites.add(new HitboxSprite(new Sprite(sprite, 384 + chunkWidth, 576 + 2 * chunkHeight, chunkWidth, chunkHeight)));
		
		Animation werewolf = new Animation(3f, sprites);
		
		werewolfAniLeft = werewolf;
		
		Animation bot = AnimationUtil.copyAnimation(werewolf);
		
		for(TextureRegion r : bot.getKeyFrames())
			r.flip(false, true);
		
		werewolfAniRight = bot;
		
		set(nametag, bgm);
		set(fbs, idle, left, right, special);
		
		setAuraColor(AllStarUtil.from255RGB(175, 107, 175));
		setBgAuraColor(AllStarUtil.from255RGB(10, 10, 10).mul(6f));
		
		addDisposable(nametag);
		addDisposable(fbs);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		addDisposable(bge);
		addDisposable(bot);
	}
	
	@Override
	public void playSpecial(boolean bool)
	{
		super.playSpecial(bool);
		werewolf = bool;
		
		if(bool)
			TouhouSounds.Stage.WOLF.play();
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
	}
	
	@Override
	public void onDraw()
	{
		if(!werewolf || !special.isAnimationFinished(getSpecialAnimationTime() * 2))
			super.onDraw();
		else
		{
			J2hGame g = Game.getGame();
			
			boolean goingLeft = lastX - getX() < 0;
			
			HitboxSprite current = AnimationUtil.getCurrentSprite(goingLeft ? werewolfAniLeft : werewolfAniRight, getAnimationTime(), true);
			current.setOriginCenter();
			
			if(lastX - getX() != 0 && getY() - lastY != 0)
				current.setRotation((float) (Math.atan2(lastX - getX(), getY() - lastY) * (180 / Math.PI) + 90));
			else
			{
				current.setRotation(0f);
				werewolf = false;
				playSpecial = true;
			}
			
			current.setPosition(getX() - current.getWidth() / 2, getY() - current.getHeight() / 2);
			current.draw(g.batch);
		}
	}
	
	@Override
	public float getDamageModifier()
	{
		if(getTicksAlive() < 5 * 60)
			return 0.5F;
		
		return super.getDamageModifier();
	}
	
	public void spawnBackground(final BackgroundBossAura aura)
	{
		final Kagerou boss = this;
		
		game.spawn(new ClearBackground(-104)
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
		
		final Sprite bg = new Sprite(this.bg);
		
		Game.getGame().spawn(new DrawObject()
		{
			{
				setFrameBuffer(aura.getBackgroundBuffer());
				
				setBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_SRC_COLOR);
				
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return bg;
					}
				}, 0, 1f, 0.01F));
				setZIndex(-100);
			}
			
			@Override
			public void onDraw()
			{
				bg.setPosition(0, 0);
				bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
				bg.draw(Game.getGame().batch);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
		
		int id = 0;
		
		for(int i = -101; i >= -102; i--)
		{
			final int finalId = id++;
			final int finalI = i;

			Game.getGame().spawn(new Background(boss.bge)
			{
				{
					setFrameBuffer(aura.getBackgroundBuffer());
					setVelU((finalId == 0 ? -1f : 1f) * 0.01f);
					
					getSprite().setScale(0.5f);
					
					setBlendFunc(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
					getSprite().setColor(0.8f, 0.8f, 0.8f, 1f);

					setZIndex(finalI);

					addEffect(new FadeInSprite(new Getter<Sprite>()
					{
						@Override
						public Sprite get()
						{
							return getSprite();
						}
					}, 0f, 1f, 0.01f));
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
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Kagerou boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, Color.RED, Color.WHITE, 60);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						bar.setObject(new CircleHealthBar(boss));
						
						g.spawn(boss);
						g.spawn(bar.getObject());
						
						bar.getObject().addSplit(0.3f);
						
						AllStarUtil.introduce(boss);
						
						boss.healUp();
						BossUtil.addBossEffects(boss, boss.getAuraColor(), boss.getBgAuraColor());
						
						Game.getGame().startSpellCard(new KagerouNonSpell(boss));
					}
				}, 60);
			}
		}, 1);

		scheme.wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss.isOnStage();
			}
		});

		SchemeUtil.waitForDeath(scheme, boss);
		
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
				
				final KagerouSpell card = new KagerouSpell(boss);
				
				Game.getGame().startSpellCard(card);
				
				boss.spawnBackground(scheme.getBossAura());
				
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
	
	public static class KagerouNonSpell extends Spellcard
	{	
		public KagerouNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(34));
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Kagerou boss = (Kagerou) getOwner();
			
			boss.setDamageModifier(Math.min(tick / 1500f, 1f));
			
			if(tick == 50)
				AllStarUtil.presentSpellCard(boss, NONSPELL_NAME);
			
			if(tick % 160 == 0)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 80, (int)(getGame().getMaxX() / 2) + 80, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 1000);
			
			if(tick < 80)
				return;
			
			float period = 40f;
			final boolean fur = tick % (period * 5) != 0;
			
			if(tick % period != 0)
				return;
			
			TouhouSounds.Enemy.RELEASE_1.play(1f);
			
			final boolean left = tick % (2*period) < period;
			
			for(int i = 0; i < 360; i += 30)
			{
				float x = boss.getX() + (float) Math.cos(Math.toRadians(i));
				float y = boss.getY() + (float) Math.sin(Math.toRadians(i));
				
				final float dir = (float) Math.toRadians(i + (left ? -1 : 1) * 90);
				
				final boolean leader = i == 0;
				
				Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED), x, y)
				{
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						setZIndex(boss.getZIndex() + 100);
						
						if(this.getTicksAlive() == 30)
						{
							setVelocityXTick(0f);
							setVelocityYTick(0f);
						}
						
						if(this.getTicksAlive() == 45)
							setDirectionRadsTick(dir, 3f);
						
						if(this.getTicksAlive() > 60)
						{
							if(fur)
							{
								if(leader)
									TouhouSounds.Enemy.RELEASE_3.play(1f);
								
								for(int i = 0; i < 6; i++)
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE_LARGE, ThBulletColor.RED), getX(), getY())
									{
										float rot = (float) (360 * Math.random());
										
										@Override
										public void onUpdate(long tick)
										{
											super.onUpdate(tick);
											
											rot += 1f;
											
											if(rot > 360)
												rot = 0;
											
											setRotationFromVelocity(rot);
										};
									};
									
									bullet.setGlowing();
									bullet.getCurrentSprite().setScale(0.3f, 1f);
									
									bullet.setDirectionRadsTick(dir, 4f);
									
									float maxX = 1.5f;
									float addX = (float) (Math.random() * (maxX * 2) - maxX);
									
									float maxY = 0.7f;
									float addY = (float) (Math.random() * (maxY * 2) - maxY);
									
									bullet.setVelocityXTick(bullet.getVelocityXTick() + addX);
									bullet.setVelocityYTick(bullet.getVelocityYTick() + addY);
//									bullet.setRotationFromVelocity(0f);
									
									game.spawn(bullet);
								}
							}
							else
							{
								if(leader)
									TouhouSounds.Enemy.EXPLOSION_3.play(1f);
								
								for(int i = -10; i < 20; i += i < 5 ? 5 : 1)
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), getX(), getY())
									{
										@Override
										public void onUpdate(long tick)
										{
											super.onUpdate(tick);
										};
									};
									
									bullet.setGlowing();
									bullet.setDirectionRadsTick((float) (dir + Math.toRadians(45) + (left ? -1 : 1) * Math.toRadians(i * 2f)), Math.max(i / 2f, 1f) * 1f);
									
									game.spawn(bullet);
								}
							}
							
							game.delete(this);
						}
					}
					
					@Override
					public void onDraw()
					{
						super.onDraw();
					}
				};
				
				bullet.setZIndex(i);
				bullet.setDirectionRadsTick((float) Math.toRadians(i), 5f);
				bullet.setGlowing();
				
				game.spawn(bullet);
			}
		}
	}

	public static class KagerouSpell extends Spellcard
	{
		public KagerouSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(54));
		}
		
		private ZIndexHelper indexer = new ZIndexHelper();

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Kagerou boss = (Kagerou) getOwner();
			
			float x = Game.getGame().getWidth() / 2;
			float y = Game.getGame().getHeight() - 150;
			
			if(tick == 0)
				boss.setDamageModifier(0f);
			
			if(tick == 45)
			{
				boss.playSpecial(true);
				
				for(int shadow = 0; shadow < 6; shadow++)
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							final HashMap<Bullet, Double> distances = new HashMap<Bullet, Double>();
							
							final Getter<Double> longestLocation = new Getter<Double>()
							{
								@Override
								public Double get()
								{
									double distance = 0;
									
									for(Double d : distances.values())
									{
										if(d > distance)
											distance = d;
									}
									
									return distance;
								}
							};
							
							for(int i = 0; i < 360; i += 20)
							{
								final int finalI = i;

								Animation ani = AnimationUtil.copyAnimation(boss.werewolfAniRight);

								for(TextureRegion frame : ani.getKeyFrames())
								{
									HitboxSprite sprite = (HitboxSprite) frame;
									sprite.setColor(new Color(84f/255f, 164f/255f, 255f/255f, 0.2f));
								}
								
								final boolean last = i + 20 >= 360;

								Bullet wolf = new Bullet(ani, boss.getX() - (float)(Math.cos(Math.toRadians(finalI)) * 100), boss.getY() - (float)(Math.sin(Math.toRadians(finalI)) * 100))
								{
									@Override
									public void checkCollision()
									{
										// Vanity bullet, no collision.
									}

									@Override
									public boolean doDelete()
									{
										return false;
									}
									
									float distance;

									@Override
									public void onUpdate(long tick)
									{
										super.onUpdate(tick);

										if(getTicksAlive() >= 100)
										{
											float distance = (float) MathUtil.getDistance(this, player);

											distances.put(this, (double) distance);
												
											this.distance = distance;
											
											double longestDistance = longestLocation.get();
											
											if(getTicksAlive() == 101)
												if(longestDistance == this.distance)
													TouhouSounds.Stage.WOLF.play(0.4f);

											setDirectionRadsTick((float) Math.toRadians(MathUtil.getAngle(this, player)), (float) (this.distance / longestDistance * 20f));
											setRotationFromVelocity(180f);

											if(MathUtil.getDistance(this, player) < 20f)
												game.delete(this);
										}
									}
								};

								wolf.setDirectionRadsTick((float) Math.toRadians(finalI - 135f), 10f);
								wolf.setRotationFromVelocity(180f);
								wolf.useSpawnAnimation(false);
								
								wolf.setGlowing();
								wolf.useSpawnAnimation(false);
								wolf.useDeathAnimation(false);

								game.spawn(wolf);
							}
							
							
						}
					}, shadow * 2);
			}
			
			if(tick < 90)
				return;
			
			if(tick == 120)
				boss.setDamageModifier(0.5f);
			
			if(tick % 2 == 0 && boss.lastX - boss.getX() != 0 && boss.getY() - boss.lastY != 0)
			{
				final boolean container = tick % 10 == 0;
				final boolean sound = tick % 20 == 0;
				
				for(int i = -1; i <= 1; i++)
				{
					float rotation = (float) (Math.atan2(boss.lastX - boss.getX(), boss.getY() - boss.lastY) * (180 / Math.PI) + 90);
					
					final float addX = i * (float) Math.cos(Math.toRadians(rotation + 90)) * 2;
					final float addY = i * (float) Math.sin(Math.toRadians(rotation + 90)) * 2;
					
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED), boss.getX() + addX, boss.getY() + addY)
					{
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getTicksAlive() == 30)
							{
								setVelocityXTick(0);
								setVelocityYTick(0);
							}
							
							if(getTicksAlive() > 60)
							{
								game.delete(this);
								
								if(sound)
									TouhouSounds.Enemy.BREAK_1.play(0.3f);
								
								if(container)
									for(int i = 0; i < 5; i++)
									{
										Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, Math.random() > 0.5f ? ThBulletColor.WHITE : ThBulletColor.RED), getX(), getY());

										bullet.setDirectionRadsTick((float) Math.toRadians(Math.random() * 360), 1f);
										game.spawn(bullet);
									}
								
								for(int i = 0; i < 1; i++)
								{
									Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, Math.random() > 0.5f ? ThBulletColor.WHITE : ThBulletColor.RED), getX(), getY());
								
									bullet.setDirectionRadsTick((float) Math.toRadians(MathUtil.getAngle(this, player)), 10f);
									game.spawn(bullet);
								}
							}
						}
					};
					
					bullet.setVelocityXTick(addX);
					bullet.setVelocityYTick(addY);
					
					indexer.index(bullet);
					
					game.spawn(bullet);
				}
			}
			
			{
				float offsetX = 0;
				float offsetY = 0;
				
				float interval = 40f;
				float total = 8 * interval;
				
				boolean right = tick % total == 0;
				boolean middle = tick % total == 2 * interval;
				boolean left = tick % total == interval;
				
				boolean cloud = tick % total == total - 60;
				
				if(cloud)
					BossUtil.cloudSpecial(boss, Color.RED, Color.WHITE, 30);
				
				if(right || middle || left)
				{
					if(left)
					{
						offsetX = -400;
						offsetY = -250;
					}
					else if(right)
					{
						offsetX = 400;
						offsetY = -250;
						boss.playSpecial(true);
					}
					else if(middle)
						x = player.getX();
					
					float size = 100;
					Rectangle rect = new Rectangle(x + offsetX - size / 2, y + offsetY - size / 2, size, size);
					
					BossUtil.moveAroundRandomly(boss, rect, 600);
					boss.setX(boss.getX() + 0.01f);
					boss.setY(boss.getY() + 0.01f);
				}
			}
			
			if(tick % 500 != 0)
				return;
			
			for(int i = 0; i < 360; i += 20)
			{
				final int finalI = i;
				
				for(int shadow = 0; shadow < 6; shadow++)
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Animation ani = AnimationUtil.copyAnimation(boss.werewolfAniRight);

							for(TextureRegion frame : ani.getKeyFrames())
							{
								HitboxSprite sprite = (HitboxSprite) frame;
								sprite.setColor(new Color(84f/255f, 164f/255f, 255f/255f, 0.2f));
							}

							Bullet wolf = new Bullet(ani, player.getX() - (float)(Math.cos(Math.toRadians(finalI)) * 100), player.getY() - (float)(Math.sin(Math.toRadians(finalI)) * 100))
							{
								@Override
								public void checkCollision()
								{
									// Vanity bullet, no collision.
								}
							};
							
							wolf.setDirectionRadsTick((float) Math.toRadians(finalI - 135f), 10f);
							wolf.setRotationFromVelocity(180f);
							wolf.setGlowing();
							wolf.useSpawnAnimation(false);
							wolf.useDeathAnimation(false);

							game.spawn(wolf);
						}
					}, shadow * 2);
			}
			
			final float startRadius = 200;
			final float endRadius = 1200;
			
			for(float radius = startRadius; radius < endRadius; radius += 20)
			{
				final float finalRadius = radius;
				
				int time = (int) ((radius - startRadius) / 7f);
				
				if(radius % 60 == 0)
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							TouhouSounds.Enemy.BULLET_3.play();
						}
					}, time);
				
				
				game.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						float increase = Math.max(10f, 50 - (finalRadius - startRadius) / (endRadius - startRadius) * 30);
						
						for(float rotation = 0; rotation < 360; rotation += increase)
						{
							final float finalRotation = rotation;
							
							final float offset = 45f;
							
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), player.getX() - (float)(Math.cos(Math.toRadians(finalRotation)) * finalRadius), player.getY() - (float)(Math.sin(Math.toRadians(finalRotation)) * finalRadius))
							{
								int times = 120;
								float increase = offset / times;
								
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(getTicksAlive() > 100)
									{
										setRotationDeg(getRotationDeg() + increase);
										
										setDirectionRadsTick((float) Math.toRadians(getRotationDeg() - 90), 1f);
										
										if(getTicksAlive() > 400)
											game.delete(this);
									}
								}
							};
							
							bullet.setGlowing();

							float maxX = 100f;
							float addX = (float) (Math.random() * (maxX * 2) - maxX);

							float maxY = 100f;
							float addY = (float) (Math.random() * (maxY * 2) - maxY);

							bullet.setX(bullet.getX() + addX);
							bullet.setY(bullet.getY() + addY);
							
							bullet.setRotationDeg((float) (MathUtil.getAngle(bullet, player) + offset));

							game.spawn(bullet);
						}
					}
				}, time);
			}
			
//			for(int i = 0; i < 4; i++)
//			{
////				x = (float) (x + ((500 * Math.random()) - 250));
//				
//				GravityBullet bullet = new GravityBullet(TouhouBulletMaker.getSchematic(TouhouBulletType.HEART, TouhouBulletColor.RED), x, y, 0.01f, 10f)
//				{
//					@Override
//					public void update(long tick)
//					{
//						if(getTicksAlive() > 30)
//						{
//							float velYOld = getVelocityYTick();
//
//							setVelocityYTick((-velYOld) / 2);
//
//							super.update(tick);
//
//							setVelocityYTick(velYOld);
//						}
//						else
//						{
//							super.update(tick);
//						}
//						
//						if(getVelocityXTick() == 0)
//						{
//							float[] options = { 0.3f, 5f, 10f, -0.3f, -5f, -10f };
//							
//							setVelocityXTick(options[random.nextInt(options.length)]);
//						}
//						else
//						{
//							if(Math.random() > 0.6)
//								setVelocityXTick((float) ((getVelocityXTick() < 0 ? 0.3 : -0.3) + getVelocityXTick()));
//							else
//								setVelocityXTick((float) ((getVelocityXTick() > 0 ? 0.3 : -0.3) + getVelocityXTick()));
//						}
//
//						setVelocityXTick((float) (getVelocityXTick() * 1.02));
//						setRotationFromVelocity(90f);
//						
//						if(getTicksAlive() == 3)
//							setVelocityYTick(2f);
//					}
//				};
//				bullet.useSpawnAnimation(false);
//				bullet.setVelocityYTick(40f);
//				game.spawn(bullet);
//			}
		}
	}
}

