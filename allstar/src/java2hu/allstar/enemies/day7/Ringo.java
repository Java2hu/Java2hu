package java2hu.allstar.enemies.day7;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.Position;
import java2hu.RNG;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.helpers.ZIndexHelper;
import java2hu.object.bullet.Bullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.Plugin;
import java2hu.spellcard.BossSpellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Ringo extends AllStarBoss
{
	public final static String FULL_NAME = "Ringo";
	public final static String DATA_NAME = "ringo";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Rabbit Sign \"All-you-can-eat Dango\"";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public Ringo(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4,5);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 11,12), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 13,14,15), 8f);
		Animation right = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 6,7), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 8,9,10), 8f);

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 16,17), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 18,19,20), 8f);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(104, 19, 52).mul(6f));
		setBgAuraColor(AllStarUtil.from255RGB(40, 40, 40));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
			{
				final Background bg = new Background(Loader.texture(FOLDER.child("bg.png")))
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);
						
						super.onDraw();
					};
				};

				bg.setFrameBuffer(t.getBackgroundBuffer());
				bg.setVelV(0.05d);
				bg.setVelU(-0.05d);
				bg.getSprite().setScale(0.75f);
				bg.getSprite().setAlpha(1f);
				bg.setZIndex(bg.getZIndex() - 20);
	
				game.spawn(bg);
				
				float speed = 10;
				
				// Layer 1
				final Background bge = new Background(Loader.texture(FOLDER.child("bge.png")))
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);
						
						super.onDraw();
					};
				};
				
				bge.setFrameBuffer(t.getBackgroundBuffer());
				bge.getSprite().setScale(1.5f);
				bge.setRotationDegs(speed);
				bge.getSprite().setAlpha(1f);
				bge.setZIndex(bg.getZIndex() - 2);

				game.spawn(bge);

				// Layer 2
				{
					Background bgeTwo = new Background(Loader.texture(FOLDER.child("bge.png")))
					{
						@Override
						public void onDraw()
						{
							setBlendFunc(GL20.GL_SRC_COLOR, GL20.GL_ZERO);
							
							super.onDraw();
						};
					};

					bgeTwo.getSprite().setScale(1.5f);
					bgeTwo.setFrameBuffer(t.getBackgroundBuffer());
					bgeTwo.setRotationDegs(-speed);
					bgeTwo.getSprite().setAlpha(1f);
					bgeTwo.setZIndex(bg.getZIndex() - 4);
					game.spawn(bgeTwo);
				}
				
				Game.getGame().spawn(new ClearBackground(bg.getZIndex() - 10)
				{
					{
						setFrameBuffer(t.getBackgroundBuffer());
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
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Ringo boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, AllStarUtil.from255RGB(255, 183, 0), AllStarUtil.from255RGB(255, 232, 0), 60);

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
						BossUtil.addBossEffects(boss, boss.getAuraColor(), boss.getBgAuraColor());
						
						Game.getGame().startSpellCard(new NonSpell(boss));
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
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				boss.playSpecial(false);
				
				game.clear(ClearType.ALL);
				
				backgroundSpawner.set(scheme.getBossAura());
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				final Spell card = new Spell(boss);
				
				Game.getGame().startSpellCard(card);
				
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
	
	public static class NonSpell extends BossSpellcard<Ringo>
	{	
		public NonSpell(Ringo owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(22));
		}

		@Override
		public void tick(int tick, J2hGame game, Ringo boss)
		{
			final Player player = game.getPlayer();
			
			if(tick == 100)
			{
				boss.playSpecial(true);
				
				BossUtil.chargeExplosion(boss, boss.getAuraColor());
			}
			
			if(tick < 130)
			{
				return;
			}
			
			final int COLOR_PERIOD = 400;

			tick -= 130;
			
			if(tick % 8 == 0)
			{
				TouhouSounds.Enemy.BULLET_1.play(0.3f);
			}
			
			if(tick % 6 == 0)
			{
				int amount = 30;

				for(int i = 0; i < amount; i++)
				{
					float angle = ((float)i / (float)amount) * 360;
					
					double mul = RNG.multiplierMirror(1000, tick);
					
					angle += (Math.sin(RNG.multiplier(200, tick) * Math.PI) * (50 + (200 * mul)));

					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, RNG.booleanMultiplier(COLOR_PERIOD, tick) ? ThBulletColor.BLUE : ThBulletColor.PURPLE), boss.getX(), boss.getY());

					final float speed = 400f;
					
					bullet.setDirectionDeg(angle, speed * 2f);
					bullet.setZIndex(bullet.getZIndex() + i);

					bullet.addEffect(new Plugin<Bullet>()
					{
						@Override
						public void update(Bullet object, long tick)
						{
							if(object.getTicksAlive() == 15)
							{
								object.setDirectionDeg(object.getVelocityRotationDeg(), speed);
							}
							
							object.getCurrentSprite().rotate(2f);
							
							float mul = 1f;//Math.min(1, object.getTicksAlive() / 10f);
							
							object.setScale(mul);
						}
					});
					
					bullet.useSpawnAnimation(false);
					bullet.setGlowing();
					
					game.spawn(bullet);
				}
			}
			
			if(tick % 10 == 0)
			{
				int directions = 6;
				
				for(int i = 0; i < directions; i++)
				{
					float angle = ((float)i / (float)directions) * 360;
					
					ArrayList<Bullet> circle = circle(boss.getX(), boss.getY(), 2, 20);
					
					for(Bullet bullet : circle)
					{
						bullet.setDirectionDeg((float) (angle + (RNG.multiplier(600, tick)) * 360f), 300f);
						bullet.setBullet(ThBullet.make(ThBulletType.DOT_SMALL_MOON, RNG.booleanMultiplier(COLOR_PERIOD, tick) ? ThBulletColor.BLUE : ThBulletColor.PURPLE));
						bullet.setGlowing();
					}
				}
			}
		}
		
		private ArrayList<Bullet> circle(float x, float y, int amount, float size)
		{
			ArrayList<Bullet> list = new ArrayList<Bullet>();
			
			float angleAdd = 360f / amount;
			float angle = angleAdd;
			
			for(int i = 0; i <= amount; i++)
			{
				float rad = (float) Math.toRadians(angle);
				
				Bullet bullet = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, ThBulletColor.BLUE), (float) (x + (Math.cos(rad) * size)), (float) (y + (Math.sin(rad) * size)));
				
				game.spawn(bullet);
				list.add(bullet);
				
				angle += angleAdd;
			}
			
			return list;
		}
	}

	public static class Spell extends BossSpellcard<Ringo>
	{
		public Spell(Ringo owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(56));
		}

		@Override
		public void tick(int tick, J2hGame game, final Ringo boss)
		{
			final Player player = game.getPlayer();
			
			if(tick == 60)
			{
				boss.playSpecial(true);
				BossUtil.charge(boss, boss.getAuraColor(), false);
				
				TouhouSounds.Enemy.ACTIVATE_3.play();
			}
			
			if(tick == 100)
				boss.playSpecial(false);
			
			if(tick < 100)
			{
				return;
			}
			
			tick -= 100;
			
			if(tick % 50 == 0)
			{
				for(final boolean bool : new boolean[] { true, false })
				{
					Bullet bullet = ThBullet.makeBullet(ThBulletType.BALL_BIG, ThBulletColor.PURPLE, boss);
					bullet.setScale(0.2f, 4f);
					
					final float finalAngle = MathUtil.getAngle(bullet, player) + ((bool ? 1f : -1f) * 60f);
					final float finalRad = (float) Math.toRadians(finalAngle);
					
					bullet.setDirectionDeg(finalAngle, 300f);
					bullet.setRotationFromVelocity();
					bullet.getSpawnAnimationSettings().setAlpha(-0.5f);
					bullet.getSpawnAnimationSettings().setTime(60f);
					bullet.getSpawnAnimationSettings().setAddedScale(0f);
					bullet.setGlowing();
					
					bullet.addEffect(new Plugin<Bullet>()
					{
						private ZIndexHelper indexer = new ZIndexHelper();
						
						@Override
						public void update(Bullet object, long tick)
						{
							if(object.getTicksAlive() < 12)
								return;
							
							tick = object.getTicksAlive() - 12;
							
							final float mul = - (1f * (Math.min(object.getTicksAlive() - 30, 200f) / 200f));
							
							if(tick % 6 == 0 && tick % (10 * 6) <= (4 * 6))
							{
								TouhouSounds.Enemy.RELEASE_1.play(0.1f);
								
								final int returnTime = 200;
								
								final Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.WHITE), (float)(object.getX() - (Math.cos(finalRad) * 140)), (float) (object.getY() - (Math.sin(finalRad) * 140)))
								{
									@Override
									public void checkCollision()
									{
										if(getTicksAlive() > returnTime)
										{
											return;
										}
										
										super.checkCollision();
									};
								};
								
								bullet.addEffect(new Plugin<Bullet>()
								{
									@Override
									public void update(Bullet object, long tick)
									{
										float speed = 300f + -200f * (mul);
										
										if(object.getTicksAlive() < 40)
										{
											speed = 0f;
										}

										if(object.getTicksAlive() > returnTime)
										{
											Position mouth = new Position(boss).add(new Position(0, 35f));
											
											bullet.setDirectionDeg(MathUtil.getAngle(bullet, mouth), 500f);
											
											double dist = MathUtil.getDistance(bullet, mouth);
											float mul = (float) (dist < 20 ? (dist / 20f) : 1f);
											
											bullet.setScale(0.4f * mul);
											bullet.getCurrentSprite().setAlpha(0.5f);
											bullet.useDeathAnimation(false);
											
											if(dist < 2)
											{
												game.delete(object);
											}
										}
										else
										{
											bullet.setDirectionDeg(finalAngle + ((bool ? 0.12f : -0.12f) * (object.getTicksAlive())) + (((bool ? -90f : 90f))), speed);
										}
									}
								});
								
								indexer.index(bullet);
								
								game.spawn(bullet);
							}
						}
					});
					
					game.spawn(bullet);
				}
			}
			
			if(tick % 14 == 0)
			{
				Bullet bullet = ThBullet.makeBullet(ThBulletType.BALL_BIG, ThBulletColor.BLUE, boss);
				
				bullet.setDirectionDeg(MathUtil.getAngle(bullet, player), 400f);
				bullet.setGlowing();
				
				game.spawn(bullet);
			}
			
			if(tick % 200 == 0)
			{
				boss.getPathing().setCurrentPath(new SimpleTouhouBossPath(boss));
			}
		}
	}
}

