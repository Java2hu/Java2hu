package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.Position;
import java2hu.RNG;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
import java2hu.background.SwirlingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.helpers.ZIndexHelper;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.PathingHelper.Path;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.pathing.SinglePositionPath;
import java2hu.pathing.VelocityPath;
import java2hu.plugin.Plugin;
import java2hu.spellcard.BossSpellcard;
import java2hu.spellcard.PhaseSpellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.enemy.FlyingYingYangOrb;
import java2hu.touhou.enemy.FlyingYingYangOrb.FlyingYingYangOrbColor;
import java2hu.touhou.sounds.TouhouSounds;
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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Sagume extends AllStarBoss
{
	public final static String FULL_NAME = "Sagume Kishin";
	public final static String DATA_NAME = "sagume";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Calm Expanding Danmaku";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	/**
	 * How to set up:
	 * Set chunkHeight and chunkWidth to the height and width per frame.
	 * 
	 * Fill in {@link #FULL_NAME}, {@link #DATA_NAME}
	 * 
	 * Set the frames, changing faceLeft for the movement frames.
	 * 
	 * If the boss has inconsistent frame size, you need to replace every seperately.
	 * If the boss has seperate movement frames, overwrite left and right.
	 */
	public Sagume(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 192;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4,5,6);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 17,18), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 19,20,21), 8F);
		Animation right = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 9,10), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 11,12,13), 8F);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 25,26,27,28,29,30,31,32);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setBgmPosition(111f - 33f);
		
		setBgAuraColor(AllStarUtil.from255RGB(72, 56, 255));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Texture bg1t = Loader.texture(FOLDER.child("bg1.png"));
				Texture bg2t = Loader.texture(FOLDER.child("bg2.png"));
				
				SwirlingBackground bg = new SwirlingBackground(bg1t)
				{
					float timer = 0;

					{
						setFrameBuffer(t.getBackgroundBuffer());
						
						setZIndex(-5);
					}

					@Override
					public float getTimer()
					{
						return timer;
					}

					@Override
					public void updateTimer()
					{
						setBlendFunc(GL20.GL_ONE, GL20.GL_ZERO);
						
						timer += 0.002f;

						timer %= 1;
					}

					@Override
					public boolean isPersistant()
					{
						return Sagume.this.isOnStage();
					}
				};
				
				bg.setBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
				
				game.spawn(bg);
				
				bg.setZIndex(bg.getZIndex() - 2);
				
				Background bg2 = new Background(bg2t)
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
						
						super.onDraw();
					}
				};
				
				bg2.setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				
				bg2.setZIndex(bg.getZIndex() + 1);
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(bg2);
				
				
				ClearBackground clear = new ClearBackground(bg.getZIndex() - 5);
				
				clear.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(clear);
			}
		};
	}
	
	@Override
	public float getDrawX()
	{
		return super.getDrawX() - 15;
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
	
	public ChangeableSpellCardName spellCardNameChanger = null;

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Sagume boss = this;
		
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
				game.clear(ClearType.ALL);
				
				backgroundSpawner.set(scheme.getBossAura());
				
				boss.spellCardNameChanger = presentChangeableSpellCard(boss, SPELLCARD_NAME);
				
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
	
	public static ChangeableSpellCardName presentChangeableSpellCard(final Boss boss, final String spellName)
	{
		TouhouSounds.Enemy.ACTIVATE_1.play();
		
		final Sprite image = new Sprite(boss.getFullBodySprite());
		float scale = 900 / image.getHeight();
		image.setScale(scale);
		
		final J2hGame stage = Game.getGame();
		
		final SaveableObject<Boolean> fade = new SaveableObject<Boolean>();
		fade.setObject(false);
	
		stage.spawn(new StageObject(0, 0)
		{
			float x = stage.getMaxX();

			float alpha = 0.60F;
			
			{
				setZIndex(J2hGame.GUI_Z_ORDER - 1);
			}
			
			@Override
			public void onDraw()
			{
				image.setAlpha(alpha);
				image.setOrigin(getWidth() / 2, getHeight() / 2);
				image.setPosition(x, stage.getMinY());
				image.draw(stage.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				float distance = MathUtil.getDistance(x + image.getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getY() + stage.getBoundary().getHeight() / 2) / 50;
				
				x -= 1F + (distance * 2f);
				
				fade.setObject(x < stage.getMinX());
				
				if(fade.getObject())
				{
					alpha -= 0.04F;
					
					if(alpha < 0.05)
						stage.delete(this);
				}
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
		});
		
		return spawnChangeableSpellName(spellName, new Getter<Boolean>()
		{
			@Override
			public Boolean get()
			{
				return fade.getObject();
			}
		});
	}
	
	private static final class ChangeableSpellCardName extends DrawObject
	{
		private final Getter<Boolean> start;
		private final BitmapFont spellCardFont;

		private final Sprite sprite;
		
		private String spellName;
		private String newSpellName;
		
		private double transform = 0d;
		
		float y = Game.getGame().getHeight() - 800;

		private ChangeableSpellCardName(Getter<Boolean> start, Texture spellcard, BitmapFont spellCardFont, String spellName, Sprite sprite)
		{
			this.start = start;
			this.spellCardFont = spellCardFont;
			this.spellName = spellName;
			this.sprite = sprite;
			
			addDisposable(spellcard);
			setZIndex(J2hGame.GUI_Z_ORDER + 1);
		}
		
		public void transform(String newSpellName)
		{
			if (newSpellName.equalsIgnoreCase(spellName))
				return;
			
			this.newSpellName = newSpellName;
		}
		
		/**
		 * Returns the spell name that this will change to.
		 */
		public String getNewSpellName()
		{
			return newSpellName;
		}
		
		public String getSpellName()
		{
			return spellName;
		}

		@Override
		public void onDraw()
		{
			game.batch.setProjectionMatrix(Game.getGame().standardProjectionMatrix);
			
			sprite.setPosition(game.getMinX() + game.getMaxX() / 2 - sprite.getWidth() / 2f, y - sprite.getHeight() * 0.8f);
			sprite.draw(game.batch);
			
			if (newSpellName == null)
			{
				drawName(spellName, 1f);
			}
			else
			{
				drawName(spellName, (float) (1f - transform));
				drawName(newSpellName, (float) (transform));
			}
			
			game.batch.setProjectionMatrix(game.camera.camera.combined);
		}

		@Override
		public void onUpdate(long tick)
		{
			if(!start.get())
				return;
			
			if(y < Game.getGame().getHeight() - 30)
			{	
				y += 15F;
			}
			
			if (newSpellName != null) {
				transform = Math.min(1, transform + 0.02f);
				
				if (transform >= 1) {
					
					spellName = newSpellName;
					newSpellName = null;
					transform = 0;
				}
			}
		}
		
		private void drawName(String spellName, float opacity)
		{
			TextBounds b = spellCardFont.getBounds(spellName);
			
			float x = game.getMinX() + game.getMaxX() / 2 - b.width / 2;
			
			final Color first = Color.BLACK.cpy();
			first.a = opacity;
			
			spellCardFont.setColor(first);
			spellCardFont.draw(Game.getGame().batch, spellName, x - 3, y - 3);
			
			final Color second = Color.WHITE.cpy();
			second.a = opacity;
			
			spellCardFont.setColor(second);
			spellCardFont.draw(Game.getGame().batch, spellName, x, y);
		}
	}
	
	private static ChangeableSpellCardName spawnChangeableSpellName(final String spellName, final Getter<Boolean> start)
	{
		final Texture spellcard = Loader.texture(Gdx.files.internal("spellcard.png"));
		final Sprite sprite = new Sprite(spellcard);
		
		final BitmapFont spellCardFont = AllStarUtil.spellCardFont;

		final ChangeableSpellCardName object = new ChangeableSpellCardName(start, spellcard, spellCardFont, spellName, sprite);
		
		game.spawn(object);
		
		return object;
	}
	
	public static class NonSpell extends BossSpellcard<Sagume>
	{	
		public NonSpell(Sagume owner)
		{
			super(owner);
			
			owner.setDamageModifier(0.6f);
			
			setSpellcardTime(Duration.seconds(35));
		}
		
		private ZIndexHelper indexer = new ZIndexHelper(1000);

		@Override
		public void tick(int tick, J2hGame game, Sagume boss)
		{
			final Player player = game.getPlayer();
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			if(tick % 400 == 0)
				boss.getPathing().path(new SimpleTouhouBossPath(boss));
			
			if(tick > 200 && tick % 30 == 0) 
			{
				TouhouSounds.Enemy.HUM_1.play(0.8f);
				
				FlyingYingYangOrb s = new FlyingYingYangOrb(FlyingYingYangOrbColor.PURPLE, 20, boss.getX(), boss.getY())
				{
					@Override
					public void decreaseHealth(float decrease)
					{
						super.decreaseHealth(decrease);
						
						boss.decreaseHealth(decrease);
					};
				};
				
				s.skipEntrance();
				
				s.addEffect(new Plugin<FlyingYingYangOrb>()
				{
					@Override
					public void update(FlyingYingYangOrb object, long tick)
					{
						if(object.getTicksAlive() > 100 && !game.inBoundary(object.getX(), object.getY()))
							object.deleteSilent();
					}
				});
				
				VelocityPath p = new VelocityPath(s);
				
				final float angle = (float) (MathUtil.getAngle(s, game.getPlayer()) + RNG.randomMirror() * 10);
				
				final float startSpeed = 500f;
				
				p.setDirectionDeg(angle, startSpeed);
				
				s.getPathing().path(p);
				
				game.spawn(s);
			}
			
			if(tick % 100 == 0)
			{
				for(boolean flip : RNG.BOOLS)
				{
					FlyingYingYangOrb s = new FlyingYingYangOrb(flip ? FlyingYingYangOrbColor.BLUE : FlyingYingYangOrbColor.RED, 100, boss.getX(), boss.getY());

					Path p = new Path(s, Duration.seconds(6))
					{
						@Override
						public void onDone()
						{
							super.onDone();
							
							s.deleteSilent();
						}
					};

					float dist = 0;

					Position last = null;
					
					float startAngle = 90;

					for(float angle = 0; angle < 360; angle += 5)
					{
						float rad = (float) Math.toRadians((flip ? -angle : angle) + startAngle);

						dist += 3;

						final Position pos = new Position(boss.getX() + (Math.cos(rad) * dist), boss.getY() + (Math.sin(rad) * dist));

						last = pos;

						p.addPosition(pos);
					}
					
					for(float angle = 0; angle < 360; angle += 5)
					{
						float rad = (float) Math.toRadians(flip ? 180 - angle : angle);

						dist += 2;

						final Position pos = new Position(last.getX() + (Math.cos(rad) * dist), last.getY() + (Math.sin(rad) * dist));

						p.addPosition(pos);
					}
					
					for(float angle = 0; angle < 360; angle += 5)
					{
						float rad = (float) Math.toRadians(flip ? 180 - angle : angle);

						dist += 4;

						final Position pos = new Position(last.getX() + (Math.cos(rad) * dist), last.getY() + (Math.sin(rad) * dist));

						p.addPosition(pos);
					}

					s.getPathing().path(p);

					game.spawn(s);
					
					s.addEffect(new Plugin<FlyingYingYangOrb>()
					{
						@Override
						public void update(FlyingYingYangOrb object, long tick)
						{
							if (!Scheduler.isTracked("release", "release"))
							{
								TouhouSounds.Enemy.BULLET_1.play(0.2f);
								Scheduler.track("release", "release", (long) 8);
							}
							
							if(tick % 10 == 0)
							{
								for (int i = 0; i < 3; i++)
								for (boolean up : RNG.BOOLS)
								{
									Bullet b = new Bullet(ThBullet.make(ThBulletType.SEAL, flip ? ThBulletColor.BLUE : ThBulletColor.RED), object.getX(), object.getY());
									
									b.setDirectionDeg((flip ? 180 : 0) + MathUtil.getAngle(object.getLastX(), object.getLastY(), object.getX(), object.getY()) + (flip ? 180 : 0), 200f);
									
									if (up)
										b.setVelocityY(-b.getVelocityY());
									
									b.update((i / 3f) * 0.5f); 
									
									b.setRotationFromVelocity();
									indexer.index(b);
									
									game.spawn(b);
								}
							}
						}
					});
				}
			}
		}
	}

	public static class Spell extends PhaseSpellcard<Sagume>
	{
		public Spell(Sagume owner)
		{
			super(owner);
			
			owner.setDamageModifier(0.6f);
			setSpellcardTime(Duration.seconds(40));
			
			addPhase(new Phase<Sagume>(100)
			{
				@Override
				public void tick(int tick, J2hGame game, Sagume boss)
				{
					if (tick == 0)
					{
						boss.spellCardNameChanger.transform(SPELLCARD_NAME);
					}
					
					if (tick % 8 == 0)
					{
						TouhouSounds.Enemy.RELEASE_1.play();
						
						for (int i = 0; i < 20; i++)
						{
							double rot = RNG.multiplierMirror(100, (tick + (i * 2))) * 360;

							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BULLET, i % 2 == 0 ? ThBulletColor.RED : ThBulletColor.BLUE), boss.getX(), boss.getY())
							{
								@Override
								public int getDeleteDistance()
								{
									return 10;
								}
							};
							
							bullet.setDirectionDeg((float) (rot + i * 30), 200f);
							bullet.setRotationFromVelocity(-90);
							
							bullet.setGlowing();
							bullet.update(i * 0.1f);
							
							final int finalI = i;
							
							bullet.addEffect(new Plugin<Bullet>()
							{
								@Override
								public void update(Bullet object, long tick)
								{
									if (object.getTicksAlive() == 80)
									{
										if (!Scheduler.isTracked("hum", "hum"))
										{
											TouhouSounds.Enemy.HUM_2.play(0.5f);
											Scheduler.track("hum", "hum", (long) 8);
										}
										
										game.delete(object);
										
										FlyingYingYangOrb s = new FlyingYingYangOrb(finalI % 2 == 0 ? FlyingYingYangOrbColor.RED : FlyingYingYangOrbColor.BLUE, 1, object.getX(), object.getY());
										
										s.addEffect(new Plugin<FlyingYingYangOrb>()
										{
											@Override
											public void update(FlyingYingYangOrb object, long tick)
											{
												if(object.getTicksAlive() > 100 && !game.inBoundary(object.getX(), object.getY()))
													object.deleteSilent();
											}
										});
										
										VelocityPath p = new VelocityPath(s);
										
										final float angle = (float) (MathUtil.getAngle(s, game.getPlayer()) + RNG.randomMirror() * 3);
										
										final float startSpeed = 600f;
										final float addSpeed = 400f;
										
										p.setDirectionDeg(angle, startSpeed);
										
										s.getPathing().path(p);
										
										game.spawn(s);
										
										for(int i = 0; i < addSpeed; i += 200)
										{
											int tickWait = (int) ((i / addSpeed) * Duration.seconds(2).toTicks());
											
											final int finalI = i;
										
											game.addTaskGame(() ->
											{
												p.setDirectionDeg(angle, startSpeed + finalI);
												
												s.getPathing().path(p);
											}, tickWait);
										}
										
										
									}
								}
							});

							game.spawn(bullet);
						}
					}
					
					if (tick == 90)
					{
						boss.playSpecial(true);
						
						TouhouSounds.Stage.TIMEOUT.play(1f, 0.7f, 0.7f);
						
						boss.spellCardNameChanger.transform("Fierce Aimed Impurity Detector");
					}
				}
			});
			
			addPhase(new Phase<Sagume>(200)
			{
				@Override
				public void tick(int tick, J2hGame game, Sagume boss)
				{
					// Wait period
				}
			});
			
			addPhase(new Phase<Sagume>(360)
			{
				private boolean right = false;
				
				@Override
				public void tick(int tick, J2hGame game, Sagume boss)
				{
					if (tick == 0)
					{
						right = !right;
						boss.spellCardNameChanger.transform("Predictable Danmaku Maze");
					}
					
					if (tick == 100)
					{
						boss.spellCardNameChanger.transform("Impurity Prison");
						
						boss.playSpecial(true);
						
						TouhouSounds.Stage.TIMEOUT.play(1f, 0.7f, 0.7f);
					}
					
					boolean after = tick > 98 && tick < 300 && tick % 2 == 0;
					
					boolean before = tick < 140 && (tick % 50 < 20);
					
					if (before)
						after = false;
					
					if (before || after)
					{
						if(tick % 5 == 0 && !after || after && tick % 20 == 0)
							TouhouSounds.Enemy.BULLET_2.play(0.4f);
						
						int directions = 10;
						
						for (int dir = 0; dir < directions; dir++)
						{
							double rot = RNG.multiplierMirror(80, (tick)) * 360;
							
							rot += (((float)dir / directions) * 360);
							
							double rad = Math.toRadians(rot);

							double startX = game.getPlayer().getX() + (Math.cos(rad) * 800);
							double startY = game.getPlayer().getY() + (Math.sin(rad) * 800);

							Bullet bullet = new Bullet(new ThBullet(after ? ThBulletType.ORB_LARGE : ThBulletType.BALL_2, right ? ThBulletColor.BLUE : ThBulletColor.RED), (float)startX, (float)startY)
							{
								@Override
								public int getDeleteDistance()
								{
									return 10;
								}

								@Override
								public boolean doDelete()
								{
									if (getTicksAlive() < 200)
										return false;

									return super.doDelete();
								}
							};
							
							bullet.setGlowing();
							bullet.setScale(2f);
							
							bullet.addEffect(new Plugin<Bullet>()
							{
								@Override
								public void update(Bullet object, long tick)
								{
									if (object.getTicksAlive() > 60)
									{
										float mul = 2f - Math.min(1f, (object.getTicksAlive() - 60f) / 60f);
										
										bullet.setScale(mul);
									}
								}
							});
							
							bullet.setZIndex(bullet.getZIndex() + tick);

							float speed = 600f - Math.min(550, ((tick / 160f) * 550f));
							float offset = ((tick - 100) / 100f) * 20f;
							
							if (after)
							{
								speed = 1000f;
								offset = 15 + (((tick - 140f) / (300f - 140f)) * 15);
							}
							
							bullet.setDirectionDeg((MathUtil.getAngle(bullet, game.getPlayer())) + (right ? -offset : offset), speed);
							bullet.setRotationFromVelocity(-90);
							
							game.spawn(bullet);
						}
					}
				}
			});
		}
		
		@Override
		public void tick(int tick, J2hGame game, Sagume boss)
		{
			if (tick == 0)
			{
				float startX = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2;
				float startY = game.getHeight() - 200;

				boss.getPathing().path(new SinglePositionPath(boss, new Position(startX, startY), Duration.ticks(60)));
			}
			
			if (tick < 80)
				return;
			
			tick -= 80;
			
			super.tick(tick, game, boss);
		}
	}
}

