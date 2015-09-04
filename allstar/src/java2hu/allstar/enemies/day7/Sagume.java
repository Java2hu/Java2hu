package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ClearBackground;
import java2hu.background.SwirlingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.spellcard.BossSpellcard;
import java2hu.system.SaveableObject;
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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
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
	final static String SPELLCARD_NAME = "Calm Expanding Circle Danmaku";
	
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
		
		InputUtil.handleMovementArrowKeys(this, 20f, 10f); // To test.
		
		if(Gdx.input.isKeyJustPressed(Keys.V)) { playSpecial(true); };
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
	
	public static class NonSpell extends BossSpellcard<Sagume>
	{	
		public NonSpell(Sagume owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, Sagume boss)
		{
			final Player player = game.getPlayer();
			
		}
	}

	public static class Spell extends BossSpellcard<Sagume>
	{
		public Spell(Sagume owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, Sagume boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

