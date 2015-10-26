package java2hu.allstar.enemies.day2;

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
import java2hu.util.InputUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Duration;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Kaguya extends AllStarBoss
{
	public final static String FULL_NAME = "Kaguya Houraisan";
	public final static String DATA_NAME = "kaguya";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Final Request \"Five Divine Treasures\"";
	
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
	public Kaguya(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 160;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));
		
		AnimationBuilder builder = new AnimationBuilder(sprite, chunkWidth, chunkHeight);

		Animation idle = builder.simpleLoop(8f, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		boolean faceLeft = false; // Is the character moving left on the sprite.
		
		Animation dir1 = builder.movement(4,5,-1, 6,-1, 8f);
		Animation dir2 = AnimationUtil.copyAnimation(dir1);
		
		Animation left = faceLeft ? dir1 : dir2; 
		Animation right = faceLeft ? dir2 : dir1;

		for(TextureRegion reg : dir2.getKeyFrames())
			reg.flip(true, false);

		Animation special = builder.simpleLoop(8f, 7, 8, 9);
		special.setPlayMode(PlayMode.NORMAL);
	
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
				
				Background bg1 = new Background(bg1t);
				Background bg2 = new Background(bg2t);
				
				bg1.setFrameBuffer(t.getBackgroundBuffer());
				bg2.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(bg1);
				game.spawn(bg2);
				
				// Set backgrounds sprite to the framebuffer of the boss aura to make use of the background bubbles.
				// Note: If their z-index is below zero, but not on the background framebuffer they will not render at all.
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
		final Kaguya boss = this;
		
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
	
	public static class NonSpell extends BossSpellcard<Kaguya>
	{	
		public NonSpell(Kaguya owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(20));
		}

		@Override
		public void tick(int tick, J2hGame game, Kaguya boss)
		{
			final Player player = game.getPlayer();
			
			if (tick % 50 == 0) {
				TouhouSounds.Enemy.BULLET_1.play(0.3F, 1F, 0F);
				float offset = (float) RNG.random() * 6;

				for (int i = 0; i < 30; i++) {
					final float angle = 12 * i + offset;
					Bullet b = new Bullet(ThBullet.make(ThBulletType.RICE_LARGE, ThBulletColor.BLUE), boss.getX(), boss.getY()) {
						float velocity = 800f;
						boolean bounced = false;

						@Override
						public void onUpdate(long tick)
						{
							if (!bounced && getY() < -5 && getX() > game.getMinX() && getX() < game.getMaxX()) {
								TouhouSounds.Enemy.RELEASE_1.play(0.3F, 1F, 0F);
								Bullet r = new Bullet(ThBullet.make(ThBulletType.RICE_LARGE, ThBulletColor.RED), getX(), getY());
								r.setDirectionDeg(-90 + RNG.random() * 20, 100f);
								r.setRotationFromVelocity();
								r.useSpawnAnimation(false);
								game.spawn(r);
								bounced = true;
							}

							if (velocity > 75f) velocity -= 10;
							setDirectionDeg(angle, velocity);
							setRotationFromVelocity();
							super.onUpdate(tick);
						}
					};
					game.spawn(b);
				}
			}
		}
	}

	public static class Spell extends BossSpellcard<Kaguya>
	{
		public Spell(Kaguya owner)
		{
			super(owner);
//			setSpellcardTime(Duration.seconds(...));
		}

		@Override
		public void tick(int tick, J2hGame game, Kaguya boss)
		{
			final Player player = game.getPlayer();
			
		}
	}
}

