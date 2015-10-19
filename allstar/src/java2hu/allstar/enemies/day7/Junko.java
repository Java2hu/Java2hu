package java2hu.allstar.enemies.day7;

import java2hu.Border;
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
import java2hu.background.SwirlingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.bullet.ReflectingBullet;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.pathing.SinglePositionPath;
import java2hu.plugin.Plugin;
import java2hu.plugin.bullets.AcceleratingBullet;
import java2hu.spellcard.BossSpellcard;
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
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Junko extends AllStarBoss
{
	public final static String FULL_NAME = "Junko";
	public final static String DATA_NAME = "junko";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "\"Pure Path in a Twisted World\"";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	private Getter<StageObject> auraSpawner;
	
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
	public Junko(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 256;
		int chunkWidth = 192;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,5,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		boolean faceLeft = false; // Is the character moving left on the sprite.
		
		Animation dir1 = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 6), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 7,8,9), 8F);
		Animation dir2 = AnimationUtil.copyAnimation(dir1);
		
		Animation left = faceLeft ? dir1 : dir2; 
		Animation right = faceLeft ? dir2 : dir1;

		for(TextureRegion reg : dir2.getKeyFrames())
			reg.flip(true, false);

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 11,12), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 13,14,15), 8f);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(200, 0, 0));
		setBgAuraColor(AllStarUtil.from255RGB(200, 0, 0));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
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
						timer += 0.002f;

						timer %= 1;
					}

					@Override
					public boolean isPersistant()
					{
						return Junko.this.isOnStage();
					}
				};
				
				bg.setBlendFunc(GL20.GL_DST_COLOR, GL20.GL_DST_COLOR);
				
				game.spawn(bg);
				
				Background bg2 = new Background(bg2t);
				
				bg2.setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				bg2.setVelV(0.02d);
				
				bg2.setZIndex(bg.getZIndex() - 1);
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(bg2);
				
				
				ClearBackground clear = new ClearBackground(bg.getZIndex() - 2);
				
				clear.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(clear);
			}
		};
		
		auraSpawner = new Getter<StageObject>()
		{
			@Override
			public StageObject get()
			{
				Texture leaf = Loader.texture(FOLDER.child("aura.png"));
				final Junko boss = Junko.this;
				
				final DrawObject obj = new DrawObject()
				{
					private Sprite sprite = new Sprite(leaf);
					
					@Override
					public void onDraw()
					{
						float centerX = boss.getX();
						float centerY = boss.getY() - 30;
						
						sprite.setPosition(centerX, centerY);
						
						sprite.setOrigin(0, sprite.getHeight() / 2f);
						
						sprite.setRotation(170);
						sprite.setScale(2f);
						
						int amount = 5;
						int index = 2;
						
						int wait = 40;
						
						final float single = 30;
						
						final float totalTime = single * (index + 1);
						int ticksAlive = (int) (getTicksAlive() % (totalTime + wait));
						
						boolean doWait = ticksAlive >= totalTime;
						
						final float total = (ticksAlive % totalTime) /  totalTime;
						int target = (int) ((total * (index + 1)));

						for(int leaves = 0; leaves < amount; leaves++)
						{
							int leaveIndex = Math.abs(leaves - index);
							
							sprite.setColor(Color.PURPLE.cpy().add(0.4f, 0.3f, 0f, 0f));
							
							sprite.draw(game.batch);
							
							if(leaveIndex == target && !doWait)
							{
								float t = ((ticksAlive % single) / single);
								float tSize = t * 140;
								
								sprite.setPosition((float)(centerX + (Math.cos(Math.toRadians(sprite.getRotation())) * tSize)), (float) (centerY + (Math.sin(Math.toRadians(sprite.getRotation())) * tSize)));
								sprite.setAlpha(1f - t);
								sprite.setScale(2f + (t * 0.8f), 2f - (0.5f * (t > 0.25f ? (t * 2f) - 0.5f : 0f)));
								
								sprite.draw(game.batch);
								sprite.setAlpha(1f);
								sprite.setScale(2f);
								
								sprite.setPosition(centerX, centerY);
							}

							sprite.rotate(-40);
						}
					}
				};
				
				obj.setGlowing();
				
				obj.setZIndex(boss.getZIndex() - 5);
				
				game.spawn(obj);
				
				boss.addChild(obj);
				
				return obj;
			}
		};
	}
	
	public StageObject spawnAura()
	{
		return auraSpawner.get();
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
		final Junko boss = this;
		
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
						bar.getObject().setRadius(130);
						
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
	
	public static class NonSpell extends BossSpellcard<Junko>
	{	
		public NonSpell(Junko owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(22));
			owner.setDamageModifier(0.7f);
		}

		@Override
		public void tick(int tick, J2hGame game, Junko boss)
		{
			final Player player = game.getPlayer();
			
			if(tick < 60)
				return;
			
			tick -= 60;
			
			int fullPeriod = 260;
			
			final int moveTick = fullPeriod - 100; // Skip 1 period, and skip the second half flipped period until she moves
			
			tick +=  fullPeriod + moveTick;
			
			if(tick % fullPeriod == moveTick)
			{
				boss.getPathing().path(new SimpleTouhouBossPath(boss, new Position(game.getCenterX(), game.getMaxY() - 300), 200, 50));
			}
			
			int period = 70;
			
			boss.playSpecial(tick % fullPeriod < (2 * period) || tick % fullPeriod > fullPeriod - 20);
			
			if(tick % fullPeriod >= period || tick % 10 != 0)
				return;
			
			boolean flip = (tick % (3f * fullPeriod) <= (1f * fullPeriod) + (period / 2f));
			
			float mul = (tick % fullPeriod) / (float)period;
			
			int increase = 20;
			
			int offset = (int) (increase * mul);
			
			game.addTask(() -> { TouhouSounds.Enemy.RELEASE_1.play(0.5f); }, (int) Duration.seconds(0.25f).toTicks());
			
			for(int i = offset; i < 360 + offset; i += increase)
			{
				for(float offsetAngle : new float[] { -0.5f, 0, 0.5f })
				{
					Bullet bullet = new ReflectingBullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), boss.getX(), boss.getY(), 1)
					{
						final AcceleratingBullet effect = new AcceleratingBullet(() -> { return this; }, 1.026f);
						
						{
							addEffect(effect);
						}
						
						@Override
						public void onReflect(Border border, int reflectAmount)
						{
							super.onReflect(border, reflectAmount);
							
							removeEffect(effect);
							
							if(border == Border.TOP)
							{
								setVelocityX(-getVelocityX() * 3f);
								setVelocityY(getVelocityY() * 0.7f);
							}
						}
						
						@Override
						public boolean doReflect(Border border, int reflectAmount)
						{
							if(border == Border.BOT)
								return false;
							
							return super.doReflect(border, reflectAmount);
						}
					};
					
					bullet.setGlowing();
					
					float finalAngle = i + offsetAngle;
					
					if(flip)
						finalAngle = 360 - finalAngle;
					
					bullet.setDirectionDeg(finalAngle, 100f);
					
					if(offsetAngle == 0)
						bullet.update(0.5f);
					
					bullet.setZIndex((int) (bullet.getZIndex() + finalAngle));

					game.spawn(bullet);
				}
			}
		}
	}

	public static class Spell extends BossSpellcard<Junko>
	{
		public Spell(Junko owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(50));
			owner.playSpecial(false);
			owner.setDamageModifier(0.30f);
			owner.spawnAura();
		}

		@Override
		public void tick(int tick, J2hGame game, Junko boss)
		{
			final Player player = game.getPlayer();
			
			if(tick == 0)
				boss.getPathing().path(new SinglePositionPath(boss, new Position(game.getCenterX(), game.getCenterY() + 200), Duration.ticks(60)));
			
			if(tick == 90)
				BossUtil.chargeExplosion(boss, boss.getAuraColor());
			
			if(tick == 70)
				boss.playSpecial(true);
			
			if(tick < 100)
			{
				return;
			}
			
			tick -= 100;
			tick += 100; // To spawn lasers first tick.
			
			if(tick % 24 == 0)
			{
				TouhouSounds.Enemy.LAZER_1.play(0.25f);
			}
			
			if(tick % 100 == 0)
			{
				final boolean playerClose = MathUtil.getDistance(boss, player) < 200;
				
				int directions = 3;
				for(int dir = 0; dir < directions; dir++)
				{
					float dirAngle = (dir / (float)directions) * 360;
					
					float addAngle = ((tick / 100f) * (10f + (Math.min(1, tick / 500f) * 10)));
					
					dirAngle += addAngle;

					for(final boolean flip : RNG.BOOLS)
						for(boolean left : RNG.BOOLS)
						{
							float preAngle = dirAngle + (left ? 9 : -9);

							if(flip)
								preAngle = 360 - preAngle;

							final float finalAngle = preAngle;
							final float speed = 400f;

							Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.RED), boss.getX(), boss.getY(), 400);
							laser.setDirectionDeg(finalAngle, speed);
							laser.setThickness(60);//20);
							laser.setHitboxThickness(4);
							laser.setGlowing();
							laser.setMaxPoints(60);
							laser.setUnitsPerPoint(4f);
							laser.setLengthOnScreen(-1);
							
							if(!playerClose)
								laser.update(0.4f);

							laser.addEffect(new Plugin<Laser>()
							{
								@Override
								public void update(Laser laser, long tick)
								{
									float angle = (float) (Math.sin(RNG.multiplier(60, laser.getTicksAlive()) * Math.PI) * 280);

									if(flip)
										angle = 360 - angle;

									laser.setDirectionDeg(finalAngle + angle, speed);
								}
							});

							game.spawn(laser);
						}
				}
			}
			
			if(tick % 100 == 0)
			{
				int rows = (int) (10 + (Math.min(1, tick / 1000f) * 30));
				
				for(int row = 0; row < rows; row++)
				{
					float rowAngle = (row / (float)rows) * 360;
					
					rowAngle += (tick / 100f) * 5f;
					
					int waves = (int) (4 + (Math.min(1, tick / 1000f) * 4));

					for(int wave = 0; wave < waves; wave++)
					{
						final boolean flip = wave < (waves / 2);
						
						final boolean first = row == 0;
						final int finalWave = wave;
						final float finalAngle = flip ? 360 - rowAngle : rowAngle;
						
						Runnable r = () -> 
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), boss.getX(), boss.getY());
							bullet.setGlowing();
							bullet.setDirectionDeg(finalAngle + ((flip ? -1.2f : 1.2f) * finalWave), 240f);
							bullet.update(0.4f);

							game.spawn(bullet);
							
							if(first)
								TouhouSounds.Enemy.BREAK_1.play(0.5f);
						};
						
						
						game.addTaskGame(r, wave * 8);
					}
				}
			}
		}
	}
}

