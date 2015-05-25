package java2hu.allstar.enemies.day5;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
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
import java2hu.spellcard.BossSpellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Seiran extends AllStarBoss
{
	public final static String FULL_NAME = "Seiran";
	public final static String DATA_NAME = "seiran";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "...";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public Seiran(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 192;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4,5,4,3,2,1);
		idle.setPlayMode(PlayMode.LOOP);
		
		boolean faceLeft = false; // Is the character moving left on the sprite.
		
		Animation dir1 = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 6,7,8,9,10);
		Animation dir2 = AnimationUtil.copyAnimation(dir1);
		
		Animation left = faceLeft ? dir1 : dir2; 
		Animation right = faceLeft ? dir2 : dir1;

		for(TextureRegion reg : dir2.getKeyFrames())
			reg.flip(true, false);

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 11,12,13,14,15), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 13,14), 8f);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setColor(new Color(17 / 255f, 119 / 255f, 204 / 255f, 1.0f));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Background bg = new Background(Loader.texture(FOLDER.child("bg.png")))
				{
					@Override
					public void onDraw()
					{
						Game.getGame().batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR);
						
						super.onDraw();
						
						Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					}
				};
				
				bg.setFrameBuffer(t.getBackgroundBuffer());
				bg.setVelV(0.1d);
				bg.setEndU(1d);
				bg.getSprite().setAlpha(0.99f);
				bg.setZIndex(bg.getZIndex() + 100);
				game.spawn(bg);
				
				// Layer 1
				{
					Background bge = new Background(Loader.texture(FOLDER.child("bge.png")))
					{
						@Override
						public void onDraw()
						{
							Game.getGame().batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
							
							super.onDraw();
							
							Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
						}
					};
					bge.setFrameBuffer(t.getBackgroundBuffer());
					bge.setRotationDegs(5);
					bge.getSprite().setAlpha(1f);
					bge.setZIndex(bg.getZIndex() - 2);
					game.spawn(bge);
				}
				
				// Layer 2
				{
					Background bge = new Background(Loader.texture(FOLDER.child("bge.png")));
					bge.setFrameBuffer(t.getBackgroundBuffer());
					bge.setRotationDegs(-5);
					bge.getSprite().setAlpha(1f);
					bge.setZIndex(bg.getZIndex() - 4);
					game.spawn(bge);
				}
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
	
	public void triangle(double dirDeg, int rows, double speed, double spreadSpeed)
	{
		double rad = Math.toRadians(dirDeg - 90);
		
		for(int row = 1; row <= rows; row++)
		{
			int bullets = row;
			
			double offset = bullets / 2d;
			
			for(int i = 0; i < bullets; i++)
			{
				double number = (i - offset);
				
				double x = Math.cos(rad) * (number * spreadSpeed);
				double y = Math.sin(rad) * (number * spreadSpeed);
				
				final Bullet bullet = new Bullet(new ThBullet(ThBulletType.BULLET, ThBulletColor.CYAN), (float)(this.getX() + x), (float)(this.getY() + y));
				bullet.setDirectionDeg((float)(dirDeg + (spreadSpeed * (number / 4d))), (float)speed);
				bullet.setRotationFromVelocity(-90);
				
				game.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						game.spawn(bullet);
					}
				}, (int) ((row) * (1600d / speed)));
			}
		}
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Seiran boss = this;
		
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
						BossUtil.addBossEffects(boss, color);
						
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
				
				Game.getGame().startSpellCard(new Spell(boss));
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class NonSpell extends BossSpellcard<Seiran>
	{	
		public NonSpell(Seiran owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, final J2hGame game, final Seiran boss)
		{
			final Player player = game.getPlayer();
			
			if(tick < 100)
			{
				return;
			}
			
			tick += 240;
			
			if(tick % 400 == 240)
			{
				boss.playSpecial(false);
				
				float width = 400;
				
				int minX = (int) Math.max(game.getMinX() + 300, Math.min(game.getMaxX() - width - 300, boss.getX() - 200));
				
				int minY = (int) (game.getCenterY() + 200);
				
				Rectangle box = new Rectangle(minX, minY, width, 200);
				
				BossUtil.moveAroundRandomly(boss, box, 800);
			}
			
			if(tick % 400 == 340)
			{
				BossUtil.charge(boss, boss.getColor(), true);
				TouhouSounds.Enemy.EXPLOSION_3.play(0.6f);
				
				boss.playSpecial(true);
			}
			
			if(tick % 400 == 0)
			{
				double degreeToPlayer = MathUtil.getAngle(boss, player);
				
				for(float i = 0; i < Math.PI * 4; i += 0.3d)
				{
					final float finalDegree = (float) ((Math.sin(i) * 90) + degreeToPlayer);
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							TouhouSounds.Enemy.RELEASE_1.play(0.5f);
							
							boss.triangle(finalDegree, 6, 400, 8d);
						}
					}, (int) ((i / Math.PI) * 120d));
				}
			}
			
			if(tick % 200 == 0)
			{
				TouhouSounds.Enemy.RELEASE_3.play(1f);
				
				int amount = 10;
				
				for(float offset : new float[] { -90, -60, -40, -30, -20, -15, -12.5f, 0, 12.5f, 15, 20, 30, 40, 60, 90 })
				for(int i = 0; i < amount; i++)
				{
					final Bullet bullet = new Bullet(new ThBullet(ThBulletType.RICE_LARGE, ThBulletColor.BLUE), boss.getX(), boss.getY());
					
					bullet.setShader(ShaderLibrary.GLOW.getProgram());
					
					final float degree = offset + MathUtil.getAngle(boss, player);
					
					bullet.setDirectionDeg(degree, (float) 400d);
					bullet.setRotationFromVelocity();

					int spawn = (int) ((i / (double)amount) * 100d);
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							game.spawn(bullet);
						}
					}, spawn);
					
					for(float f = 0; f < 10; f += 0.1f)
					{
						final float bend = (offset > 0 ? -1f : (offset != 0 ? 1f : 0)) * f * 10f;
						
						game.addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								bullet.setDirectionDeg(degree + bend, 500f);
								bullet.setRotationFromVelocity();
							}
						}, (int) (spawn + (f * 40)));
					}
				}
					
			}
		}
	}

	public static class Spell extends BossSpellcard<Seiran>
	{
		public Spell(Seiran owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, Seiran boss)
		{
			final Player player = game.getPlayer();
			
			if(tick == 0)
			{
				boss.playSpecial(false);
			}
		}
	}
}

