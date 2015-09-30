package java2hu.allstar.enemies.day7;

import java2hu.AnimationBuilder;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.RNG;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
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
import java2hu.util.BossUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.InputUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Clownpiece extends AllStarBoss
{
	public final static String FULL_NAME = "Clownpiece";
	public final static String DATA_NAME = "clownpiece";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
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
	public Clownpiece(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 192;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));
		
		AnimationBuilder builder = new AnimationBuilder(sprite, chunkWidth, chunkHeight);
		
		Animation idle = builder.simple(PlayMode.LOOP_PINGPONG, 6f, 1,2,3,4,5,6,7);
		
		Animation left = builder.movement(17,18,-1, 19,20,21,-1, 8f);
		Animation right = builder.movement(9,10,-1, 11,12,13,-1, 8f);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * chunkHeight, chunkHeight + 22, chunkWidth, 8f, 1,2,3,4,5,6,7,6,5,4,3,2,1);

		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(0, 0, 0));
		setBgAuraColor(AllStarUtil.from255RGB(0, 0, 0));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		backgroundSpawner = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Texture bg1t = Loader.texture(FOLDER.child("bg1.png"));
				Texture bg2t = Loader.texture(FOLDER.child("bg2.png"));
				
				Background bg1 = new Background(bg1t)
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
						
						super.onDraw();
					}
				};
				
				bg1.getSprite().getTexture().setWrap(TextureWrap.MirroredRepeat, TextureWrap.Repeat);
				bg1.setEndU(2);
				
				Background bg2 = new Background(bg2t)
				{
					@Override
					public void onDraw()
					{
						setBlendFunc(GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
						
						super.onDraw();
					}
				};
				
				final float width = game.getWidth() / 2f;
				final float height = game.getHeight() / 2f;
				
				float size = (float) Math.sqrt((width * width) + (height * height));
				
				float scaleX = size / bg2t.getWidth();
				float scaleY = (size / bg2t.getHeight()) * 1.5f;
				
				bg2.setRotationDegs(-10);
				bg2.getSprite().setAlpha(0.5f);
				bg2.getSprite().setScale(scaleX, scaleY);
				bg2.setZIndex(bg1.getZIndex() - 1);
				
				bg1.setFrameBuffer(t.getBackgroundBuffer());
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(bg1);
				game.spawn(bg2);
				
				ClearBackground clear = new ClearBackground(bg1.getZIndex() - 3);
				
				clear.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(clear);
				
				// Set backgrounds sprite to the framebuffer of the boss aura to make use of the background bubbles.
				// Note: If their z-index is below zero, but not on the background framebuffer they will not render at all.
			}
		};
	}
	
	@Override
	public float getDrawY()
	{
		return super.getDrawY() + (playSpecial ? 22 : 0);
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		InputUtil.handleMovementArrowKeys(this, 20f, 10f); // To test.
		
		if(Gdx.input.isKeyJustPressed(Keys.V)) { playSpecial(!playSpecial); };
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
		final Clownpiece boss = this;
		
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
	
	public static class NonSpell extends BossSpellcard<Clownpiece>
	{	
		public NonSpell(Clownpiece owner)
		{
			super(owner);
//			setSpellcardTime(Duration.seconds(...));
		}

		@Override
		public void tick(int tick, J2hGame game, Clownpiece boss)
		{
			final Player player = game.getPlayer();
			
			if (tick % 2 == 0)
			{
				double mul = RNG.multiplierMirror(20, tick);
				
				if (mul < 0)
					mul = 1f - (mul + 1f);
				
				int angleRadius = 60;
				
				float angle = (float) ((angleRadius * 2) * mul);
				
				for (float xOffset : new float[] { -700, 700 })
				{
					Bullet bullet = new Bullet(ThBullet.make(ThBulletType.STAR_LARGE, ThBulletColor.BLUE), game.getCenterX() + xOffset, player.getY());
					bullet.setDirectionDeg(angle - (xOffset < 0 ? 180 + angleRadius : angleRadius), 400f);

					game.spawn(bullet);
				}
			}
			
		}
	}

	public static class Spell extends BossSpellcard<Clownpiece>
	{
		public Spell(Clownpiece owner)
		{
			super(owner);
//			setSpellcardTime(Duration.seconds(...));
		}

		@Override
		public void tick(int tick, J2hGame game, Clownpiece boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

