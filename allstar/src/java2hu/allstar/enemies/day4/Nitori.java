package java2hu.allstar.enemies.day4;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.RNG;
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
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.ObjectUtil;
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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Nitori extends AllStarBoss
{
	public final static String FULL_NAME = "Nitori Kawashiro";
	public final static String DATA_NAME = "nitori";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public Nitori(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 164;
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

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 9,10,11), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 12), 8f);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		bgm.setLooping(true);
		
		setAuraColor(AllStarUtil.from255RGB(34, 85, 170));
		setBgAuraColor(AllStarUtil.from255RGB(136, 187, 204));
		
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
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						float mul = (float) (RNG.multiplier(100, tick) * 2f);

						if (mul > 1f)
							mul = 2f - mul;
						
						Color color = new Color(0f, 0.5f + (mul * 0.5f), 1f - (mul * 0.5f), 1f);
						
						getSprite().setColor(color);
					}
				};
				
				bg1.setFrameBuffer(t.getBackgroundBuffer());
				
				bg1.setEndU(2f);
				bg1.setEndV(2f);
				
				bg1.setVelU(0.05f);
				bg1.setVelV(0.07f);
				
				
				Background bg2 = new Background(bg2t);
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				bg2.setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_COLOR);
				
				bg2.setZIndex(bg1.getZIndex() + 1);

				
				game.spawn(bg1);
				game.spawn(bg2);
			}
		};
	}
	
	@Override
	public float getDrawY()
	{
		return super.getDrawY() - 15;
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
		final Nitori boss = this;
		
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
	
	public static class NonSpell extends BossSpellcard<Nitori>
	{	
		public NonSpell(Nitori owner)
		{
			super(owner);
//			setSpellcardTime(Duration.seconds(...));
		}

		@Override
		public void tick(int tick, J2hGame game, Nitori boss)
		{
			final Player player = game.getPlayer();
			
		}
	}

	public static class Spell extends BossSpellcard<Nitori>
	{
		public Spell(Nitori owner)
		{
			super(owner);
//			setSpellcardTime(Duration.seconds(...));
		}

		@Override
		public void tick(int tick, J2hGame game, Nitori boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

