package java2hu.allstar.enemies.day5;

import java2hu.Game;
import java2hu.HitboxSprite;
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
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.bullet.Bullet;
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
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.Duration;
import java2hu.util.Duration.Unit;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
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
public class Tenshi extends AllStarBoss
{
	public final static String FULL_NAME = "Tenshi Hinanawi";
	public final static String DATA_NAME = "tenshi";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Heaven Sign \"Keystone of the God Shrine\"";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	private Animation keyStoneBullet;
	
	public Tenshi(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		boolean faceLeft = false; // Is the character moving left on the sprite.
		
		Animation dir1 = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 5,6,7), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 8), 8F);
		Animation dir2 = AnimationUtil.copyAnimation(dir1);
		
		Animation left = faceLeft ? dir1 : dir2; 
		Animation right = faceLeft ? dir2 : dir1;

		for(TextureRegion reg : dir2.getKeyFrames())
			reg.flip(true, false);
		
		Animation keyStone = ImageSplitter.getAnimationFromSprite(sprite, chunkWidth, chunkHeight * 3, 96, 96, 8F, 1,2,3);
		
		Array<TextureRegion> hitboxAnimation = new Array<TextureRegion>();
		
		for(TextureRegion r : keyStone.getKeyFrames())
		{
			final HitboxSprite h = new HitboxSprite(r);
			h.setScale(1f);
			hitboxAnimation.add(h);
		}

		keyStoneBullet = new Animation(8f, hitboxAnimation);
		
		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 9,10,11,12,13);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(85, 119, 204));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		setBgmPosition(29.3f);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Texture bg1t = Loader.texture(FOLDER.child("bg1.png"));
				Texture bg2t = Loader.texture(FOLDER.child("bg2.png"));
				
				Background bg = new Background(bg1t);
				Background bg1 = new Background(bg2t);
				Background bg2 = new Background(bg2t);
				
				bg1.setRotationDegs(-20);
				bg2.setRotationDegs(20);
				
				bg1.setZIndex(bg.getZIndex() + 1);
				bg2.setZIndex(bg.getZIndex() + 2);
				
				bg1.getSprite().setAlpha(0.2f);
				bg2.getSprite().setAlpha(0.2f);
				
				bg.setFrameBuffer(t.getBackgroundBuffer());
				bg1.setFrameBuffer(t.getBackgroundBuffer());
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(bg);
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
		final Tenshi boss = this;
		
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
	
	public static class NonSpell extends BossSpellcard<Tenshi>
	{	
		public NonSpell(Tenshi owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(24));
			owner.setDamageModifier(2f);
		}
		
		float angleOffset = 0;

		@Override
		public void tick(int tick, J2hGame game, Tenshi boss)
		{
			final Player player = game.getPlayer();

			if(tick == 50)
			{
				TouhouSounds.Enemy.ACTIVATE_3.play();
				boss.playSpecial(true);
			}
			
			int wait = 60;
			
			if(tick < wait)
				return;
			
			tick -= wait;
			
			int time = 40;
			int idle = 100;
			int total = time + idle;
			
			int totalTick = (tick % total);
			
			if(totalTick == total - 10)
			{
				TouhouSounds.Enemy.ACTIVATE_3.play();
				boss.playSpecial(true);
			}
			
			if(totalTick == time + 10)
			{
				boss.getPathing().path(new SimpleTouhouBossPath(boss));
			}
			
			if(totalTick == 0)
			{
				angleOffset = (float) (MathUtil.getAngle(player, boss) + 90);
			}
			
			if(totalTick < time)
			{
				if(tick % 3 == 0)
				{
					TouhouSounds.Enemy.RELEASE_1.play();
				}
				
				float timeMul = (totalTick) / (float)time;
				
				int subtick = totalTick;
				
				for(int i = 0; i < 3; i++)
				for(boolean bool : RNG.BOOLS)
				{
					float angle = (float) (RNG.multiplier(time, subtick) * (180 + 280)) + 120f;
					float mul = i / 3f;
					
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_LARGE_HOLLOW, ThBulletColor.RED), boss.getX(), boss.getY());
					bullet.setDirectionDeg((bool ? 180 - angle : angle) + angleOffset, 800f + (-700 * timeMul) + (150f * mul));
					bullet.setZIndex(bullet.getZIndex() + i);
					bullet.setGlowing();

					game.spawn(bullet);
				}
			}
			
			int interval = 12;
			
			int[][] amount = { {0}, {-20, -10, 0, 10, 20 }, {-50, -35, -15, 0, 15, 35, 50 }, {0}, {0}, {0} };
			
			if(totalTick % interval == 0 && totalTick < (interval * amount.length))
			{
				TouhouSounds.Enemy.BULLET_1.play();
				
				final int id = (totalTick % (amount.length * interval)) / interval;
				
				int[] offsets = amount[id];
				
				for(int i = 0; i < offsets.length; i++)
				{
					float offset = offsets[i];
					
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED), boss.getX(), boss.getY());
					bullet.setDirectionDeg(angleOffset + 90 + offset, 400f);
					bullet.progress(Duration.seconds(0.2f));
					
					game.spawn(bullet);
				}
			}
		}
	}

	public static class Spell extends BossSpellcard<Tenshi>
	{
		public Spell(Tenshi owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(43));
			owner.setDamageModifier(0.5f);
		}

		@Override
		public void tick(int tick, J2hGame game, Tenshi boss)
		{
			final Player player = game.getPlayer();
			
			int wait = 60;
			
			if(tick == 0)
			{
				Position pos = new Position(game.getCenterX(), game.getCenterY() + 200);

				SinglePositionPath p = new SinglePositionPath(boss, pos, Duration.ticks(wait));

				boss.getPathing().path(p);
			}
			
			if(tick < wait)
				return;
			
			tick -= wait;
			
			final float shrinkTime = 800;
			final float progressMul = 1f - (tick > shrinkTime ? 1f : ((tick - 1) % shrinkTime) / shrinkTime);
			
			int interval = 60;
			
			if(getTimeLeft().toSeconds() < 20)
			{
				float mul = (float) (1f - (getTimeLeft().getValue(Unit.SECOND) / 20f));
				
				int value = 30;
				
				if(getTimeLeft().toSeconds() < 4)
				{
					interval = 15;
					
					if(tick % 30 == 0)
					{
						BossUtil.cloudEntrance(boss, Color.RED, Color.BLUE, 30);
					}
				}
				else
				{
					interval = (int) (interval - (value * mul));
					interval = Math.round(interval / 5) * 5;
				}
			}
			
			if(tick % interval == 30)
			{
				float range = 20;
				
				Position pos = new Position(game.getCenterX(), game.getCenterY() + 200).add(new Position(range * RNG.randomMirror(), range * RNG.randomMirror()));
				
				SinglePositionPath p = new SinglePositionPath(boss, pos, Duration.ticks(30));
				
				boss.getPathing().path(p);
			}
			
			if(tick % interval == 0)
			{
				boss.playSpecial(true);
				
				TouhouSounds.Enemy.ACTIVATE_3.play();
				
				for(final boolean bool : RNG.BOOLS)
				{
					Bullet bullet = new Bullet(AnimationUtil.copyAnimation(boss.keyStoneBullet), boss.getX(), boss.getY() + 100);
					bullet.setDirectionDeg(bool ? 0 : 180, 500f);
					bullet.setRotationFromVelocity(180);
					bullet.setZIndex(bullet.getZIndex() + 5);
					bullet.useSpawnAnimation(false);
					
					final double xDist = (MathUtil.getDifference(boss.getX(), player.getX()) / 10f) + 30f;
					final boolean above = bullet.getY() < player.getY();
					
					bullet.addEffect(new Plugin<Bullet>()
					{
						@Override
						public void update(Bullet object, long tick)
						{
							final float aboveMul = above ? -1f : 1f;
							
							int length = (int) (20 + (60 * progressMul));
							
							if(length < (xDist * 0.5f))
							{
								length = (int) xDist;
							}
							
							final int breakTime = length;
							final int rotateTime = 40;
							
							if(object.getTicksAlive() % 8 == 0 && bool)
							{
								TouhouSounds.Enemy.RELEASE_1.play(0.1f);
							}
							
							if(object.getTicksAlive() < breakTime && object.getTicksAlive() % 2 == 0)
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.BLUE), object.getX(), object.getY());
								bullet.addEffect(new Plugin<Bullet>()
								{
									@Override
									public void update(Bullet object, long tick)
									{
										if(object.getTicksAlive() == 60)
										{
											object.setDirectionDeg((float) (30 * RNG.randomMirror()) + (90f * aboveMul), (float) (100f + (100 * RNG.random())));
										}
									}
								});
								bullet.setGlowing();
								
								game.spawn(bullet);
							}
							else if(object.getTicksAlive() >= breakTime + rotateTime && object.getTicksAlive() % 5 == 0)
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, ThBulletColor.RED), object.getX(), object.getY());
								bullet.addEffect(new Plugin<Bullet>()
								{
									@Override
									public void update(Bullet object, long tick)
									{
										if(object.getTicksAlive() == 60)
										{
											object.setDirectionDeg((float) (30 * RNG.randomMirror()) + 90f, 100f);
											object.setVelocityY(object.getVelocityY() * 2f * aboveMul);
											object.setVelocityX(object.getVelocityX() * 0.5f);
										}
									}
								});
								bullet.setGlowing();
								
								game.spawn(bullet);
							}
						
							if(object.getTicksAlive() >= breakTime * 0.75f && object.getTicksAlive() < breakTime + rotateTime)
							{
								float mul = 1f - (object.getTicksAlive() - breakTime * 0.75f) / (breakTime * 0.25f);
								
								object.setVelocityX(object.getVelocityX() * (mul * 1.1f));
							}
							else if(object.getTicksAlive() == breakTime + rotateTime)
							{
								object.setDirectionDeg(aboveMul * 90f, 300f);
							}
							
							if(object.getTicksAlive() >= breakTime && object.getTicksAlive() <= breakTime + rotateTime)
							{
								object.setRotationDeg(object.getRotationDeg() + (aboveMul * ((bool ? 90f : -90f) / rotateTime)));
							}
						}
					});
					
					game.spawn(bullet);
				}
			}
		}
	}
}

