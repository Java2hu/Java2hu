package java2hu.allstar.enemies.day5;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.Position;
import java2hu.RNG;
import java2hu.SmartTimer;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.pathing.SinglePositionPath;
import java2hu.plugin.Plugin;
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
import java2hu.util.ImageSplitter;
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

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Iku extends AllStarBoss
{
	public final static String FULL_NAME = "Iku Nagae";
	public final static String DATA_NAME = "iku";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Heavenly Clothing \"Legendary Scarlet Cloth\"";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public Iku(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 190;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4,3,2,1);
		idle.setPlayMode(PlayMode.LOOP);
		
		boolean faceLeft = false; // Is the character moving left on the sprite.
		
		Animation dir1 = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 5,6,7), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 8), 8F);
		Animation dir2 = AnimationUtil.copyAnimation(dir1);
		
		Animation left = faceLeft ? dir1 : dir2; 
		Animation right = faceLeft ? dir2 : dir1;

		for(TextureRegion reg : dir2.getKeyFrames())
			reg.flip(true, false);

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 9,10,11), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 12), 8f);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(124, 128, 253));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		setBgmPosition(24f);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Texture bg1t = Loader.texture(FOLDER.child("bg1.png"));
				Texture bg2t = Loader.texture(FOLDER.child("bg2.png"));
				
				Background bg1 = new Background(bg1t);
				bg1.setFrameBuffer(t.getBackgroundBuffer());
				
				Background bg2 = new Background(bg2t)
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_DST_ALPHA);
						
						super.onDraw();
					}
				};
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				bg2.setEndU(2f);
				bg2.setEndV(2f);
				
				float speed = 0.05f;
				
				bg2.setVelU(-speed);
				bg2.setVelV(speed);
				bg2.setZIndex(bg1.getZIndex() + 1);
				
				game.spawn(bg1);
				game.spawn(bg2);
				
				// Set backgrounds sprite to the framebuffer of the boss aura to make use of the background bubbles.
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
		final Iku boss = this;
		
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
	
	public static class NonSpell extends BossSpellcard<Iku>
	{	
		public NonSpell(Iku owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(25));
		}

		@Override
		public void tick(int tick, J2hGame game, Iku boss)
		{
			final Player player = game.getPlayer();
			
			int wait = 60;
			
			if(tick < wait)
				return;
			
			tick -= wait;
			
			int interval = 200;
			
			if(tick % interval == (interval - 80))
			{
				boss.getPathing().path(new SimpleTouhouBossPath(boss));
			}
			
			if(tick % interval == 130)
			{
				boss.playSpecial(false);
			}
			
			if(tick % interval == 0)
			{
				TouhouSounds.Enemy.ACTIVATE_3.play();
				
				boss.playSpecial(true);
				
				int amount = 26;
				
				for(int ray = 0; ray < 2; ray++)
				for(int i = 0; i < amount; i++)
				{
					final float mul = (float)i / (float)amount;
					
					final float angle = mul * 360;
					
					final int finalRay = ray;
					
					float rad = (float) Math.toRadians(angle);
					
					float rayOffset = (ray - 0.5f) * 5;
					
					float distance = (100 + rayOffset);
					
					float x = (float) (boss.getX() + (Math.cos(rad) * distance));
					float y = (float) (boss.getY() + (Math.sin(rad) * distance));
					
					Laser laser = new Laser(new ThLaser(ThLaserType.NORMAL, ThLaserColor.RED), x, y, 200)
					{
						@Override
						public boolean doDelete()
						{
							return getTicksAlive() > 200 && super.doDelete();
						}
					};
				
					laser.setUnitsPerPoint(2);
					laser.setThickness(0f);

					
					final float dirAngle = angle + 120 + ((ray - 0.5f) * -5);
					
					laser.setDirectionDeg(dirAngle, 400f);
					
					laser.addEffect(new Plugin<Laser>()
					{
						float curAngle = 0;
						
						@Override
						public void update(final Laser object, long tick)
						{
							curAngle -= 3f;
							
							object.setThickness(object.getThickness() + 0.1f);
							
							final float degree = dirAngle + curAngle;
							
							if(curAngle <= -320)
							{
								object.setDirectionDeg(degree - 90, 520f);
								
								delete();
								
								game.addTask(new Runnable() { @Override public void run() { object.progress(Duration.seconds(finalRay * 0.02f)); } }, 1); // Fix offset
								
								game.addTask(new Runnable() { @Override public void run() { object.setLengthOnScreen(1200f); object.setThickness(10f); } }, 10);
								
								return;
							}
							
							object.setDirectionDeg(degree, 400f);
						}
					});
					
					laser.setGlowing();
					
					game.spawn(laser);
				}
			}
			
			if(tick % interval == 120)
			{
				float bendAngle = (float) (30f + (60f * Math.random()));
				
				TouhouSounds.Enemy.EXPLOSION_3.play();
				
				int rows = 20;
				int subRows = 12;
				
				for(boolean mirror : new boolean[] { true, false })
				for(int row = 0; row < rows; row++)
				{
					float angle = (((float)row) / (float)rows) * 360f;
					float rad = (float) Math.toRadians(angle);
					
					for(int amount = 0; amount < subRows; amount++)
					{
						float subAngle = angle + ((mirror ? -1 : 1) * (amount * 10));
						float mul = (amount / (float)subRows);
						
						float x = boss.getX() - (float) (Math.cos(rad) * 20);
						float y = boss.getY() - (float) (Math.sin(rad) * 20);
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), x, y);
						
						bullet.setDirectionDeg((subAngle + ((mirror ? -amount : amount) * -bendAngle)), 250f + (350f * mul));
//						bullet.setGlowing();
						
						bullet.setZIndex(bullet.getZIndex() + amount);
						
						game.spawn(bullet);
					}
				}
			}
		}
	}

	public static class Spell extends BossSpellcard<Iku>
	{
		public Spell(Iku owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(50));
			owner.setDamageModifier(0.5f);
		}
		
		private SmartTimer timer = new SmartTimer(0.025f, -1f, -0.7f, 1f, 0.7f, 0.001f);

		@Override
		public void tick(int tick, J2hGame game, Iku boss)
		{
			final Player player = game.getPlayer();
			
			int wait = 80;
			
			if(tick == 0)
			{
				boss.playSpecial(false);
				boss.getPathing().path(new SinglePositionPath(boss, new Position(game.getCenterX(), game.getCenterY() + 300), Duration.ticks(wait * 0.75f)));
			}
			
			if(tick < wait)
				return;
			
			tick -= wait;
			
			if(tick == 0)
			{
				boss.playSpecial(true);
			}
			
			timer.tick();
			
			{
				double mul = timer.getTimer();
				
				float sinMul = 60f;
				float cosMul = 200f;
				
				float angleOffset = 90;
				
				for(boolean b : RNG.BOOLS)
				{
					float angle = (float) (mul * 120) + angleOffset;
					
					if(b)
					{
						angle = 180 - angle;
					}
					
					float rad = (float) Math.toRadians(angle);
					
					float x = (float) (boss.getX() - (Math.cos(rad) * cosMul));
					float y = (float) (boss.getY() - (Math.sin(rad) * sinMul));
					
					if(tick % 2 == 0)
					{
						if(!b && tick % 6 == 0)
							TouhouSounds.Enemy.LAZER_2.play(0.2f);
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE_LARGE, ThBulletColor.RED), x, y);
						
						bullet.useDeathAnimation(false);

						float dir = angle + ((b ? -1f : 1f) * 30);//(float) ((mul * 60) + angleOffset);
						float speed = 250f;

						bullet.setDirectionDeg(dir, speed);
						bullet.setGlowing();

						bullet.setRotationFromVelocity();

						bullet.addEffect(new Plugin<Bullet>()
						{
							float scaleY = 0.1f;

							@Override
							public void update(Bullet object, long tick)
							{
								if(scaleY > 5f)
								{
									delete();
									return;
								}

								scaleY += 0.1f;

								object.setScale(0.4f, scaleY);
							}
						});

						game.spawn(bullet);
					}
					
					if(tick > 200 && tick % 4 <= 1)
					{
						float rotation = (float) (RNG.multiplier(200, tick) * 360f);
						
						if(b)
							rotation = 180 - rotation;
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), x, y);
						
						bullet.setDirectionDeg(rotation, 450f);
						bullet.setRotationFromVelocity();
						
						game.spawn(bullet);
					}
				}
			}
		}
	}
}

