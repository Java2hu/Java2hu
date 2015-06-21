package java2hu.allstar.enemies.day7;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.spellcard.BossSpellcard;
import java2hu.system.SaveableObject;
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

public class Ringo extends AllStarBoss
{
	public final static String FULL_NAME = "Ringo";
	public final static String DATA_NAME = "ringo";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "...";
	
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
			public void set(BackgroundBossAura t)
			{
				final Background bg = new Background(Loader.texture(FOLDER.child("bg.png")));
				bg.setBlendFunc(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR);
				
				bg.setFrameBuffer(t.getBackgroundBuffer());
				bg.setVelV(0.05d);
				bg.setVelU(-0.05d);
				bg.getSprite().setScale(0.75f);
				bg.getSprite().setAlpha(1f);
				bg.setZIndex(bg.getZIndex());
				game.spawn(bg);
				
				float speed = 10;
				
				// Layer 1
				final Background bge = new Background(Loader.texture(FOLDER.child("bge.png")));
				bge.setFrameBuffer(t.getBackgroundBuffer());
				bge.getSprite().setScale(1.5f);
				bge.setRotationDegs(speed);
				bge.getSprite().setAlpha(1f);
				bge.setZIndex(bg.getZIndex() - 2);
				bge.setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR);
				game.spawn(bge);

				// Layer 2
				{
					Background bgeTwo = new Background(Loader.texture(FOLDER.child("bge.png")));
					bgeTwo.setBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_DST_ALPHA);
					bgeTwo.getSprite().setScale(1.5f);
					bgeTwo.setFrameBuffer(t.getBackgroundBuffer());
					bgeTwo.setRotationDegs(-speed);
					bgeTwo.getSprite().setAlpha(1f);
					bgeTwo.setZIndex(bg.getZIndex() - 4);
					game.spawn(bgeTwo);
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
	
	public static class NonSpell extends BossSpellcard<Ringo>
	{	
		public NonSpell(Ringo owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(25));
		}

		@Override
		public void tick(int tick, J2hGame game, Ringo boss)
		{
			final Player player = game.getPlayer();

		}
	}

	public static class Spell extends BossSpellcard<Ringo>
	{
		public Spell(Ringo owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(50));
		}

		@Override
		public void tick(int tick, J2hGame game, Ringo boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

