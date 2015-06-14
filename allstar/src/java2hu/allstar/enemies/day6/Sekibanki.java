package java2hu.allstar.enemies.day6;

import java.util.ArrayList;
import java.util.Iterator;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.HorizontalScrollingBackground;
import java2hu.background.ScrollingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hObject;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.Getter;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.Scheduler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Sekibanki extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Sekibanki";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Flying Head \"You spin my head right round\"";
	
	public static Sekibanki newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 5,6,7,8);
		Animation right = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 5,6,7,8);

		for(TextureRegion reg : left.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 9,10,11,12,12,12,12,11,10,9);
		special.setPlayMode(PlayMode.NORMAL);
		
		// Headless variant
		Animation headlessIdle = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * chunkHeight, chunkHeight, chunkWidth, 0.2F, 1,2,3,4);

		Animation headlessLeft = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * chunkHeight, chunkHeight, chunkWidth, 0.2F, 5,6,7,8);
		Animation headlessRight = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * chunkHeight, chunkHeight, chunkWidth, 0.2F, 5,6,7,8);

		for(TextureRegion reg : headlessLeft.getKeyFrames())
			reg.flip(true, false);

		Animation headlessSpecial = ImageSplitter.getAnimationFromSprite(sprite, 0, 3 * chunkHeight, chunkHeight, chunkWidth, 10F, 9,10,11,12,12,12,12,11,10,9);
		headlessSpecial.setPlayMode(PlayMode.NORMAL);

		Texture bgt = Loader.texture(Gdx.files.internal(folder + "bg.png"));
		bgt.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		Sprite bg = new Sprite(bgt);
		
		Texture bget = Loader.texture(Gdx.files.internal(folder + "bge.png"));
		bget.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		Sprite bge = new Sprite(bget);

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Sekibanki boss = new Sekibanki(100, nameTag, bg, bge, fbs, idle, left, right, special, bgm, x, y);
		
		boss.headlessIdle = headlessIdle;
		boss.headlessLeft = headlessLeft;
		boss.headlessRight = headlessRight;
		boss.headlessSpecial = headlessSpecial;
		
		boss.addDisposable(headlessIdle);
		boss.addDisposable(headlessLeft);
		boss.addDisposable(headlessRight);
		boss.addDisposable(headlessSpecial);
		
		return boss;
	}
	
	public boolean hasHead = true;
	public Animation headlessIdle;
	public Animation headlessLeft;
	public Animation headlessRight;
	public Animation headlessSpecial;
	
	public Sekibanki(float maxHealth, TextureRegion nametag, final Sprite bg, final Sprite bge, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		addDisposable(bge);
		
		setAuraColor(Color.RED);
		
		final Sekibanki boss = this;
		
		Game.getGame().spawn(new ScrollingBackground(bg, -0.5f, -0.5f)
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return bg;
					}
				}
				, 0.01F));
				
				setZIndex(-2);
			}
			
			@Override
			public void onDraw()
			{
				super.onDraw();
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
		
		Game.getGame().spawn(new HorizontalScrollingBackground(bge, 0.3f, false)
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return bge;
					}
				}
				, 0f, 0.9f, 0.01F));
				setZIndex(-1);
			}
			
			@Override
			public void onDraw() 
			{
				super.onDraw();
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
	}
	
	@Override
	public void onDraw()
	{
		if(hasHead)
			super.onDraw();
		else
		{
			J2hGame g = Game.getGame();
			
			if(headlessSpecial != null && playSpecial)
			{
				HitboxSprite current = (HitboxSprite) headlessSpecial.getKeyFrame(getSpecialAnimationTime());
				current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
				current.draw(g.batch);
				
				if(!Game.getGame().isPaused())
					specialTimer++;
				
				if(headlessSpecial.getPlayMode() == PlayMode.NORMAL && headlessSpecial.isAnimationFinished(getSpecialAnimationTime()))
					playSpecial = false;
			}
			else if(getLastX() - getX() > 0)
			{
				HitboxSprite current = AnimationUtil.getCurrentSprite(headlessLeft, getAnimationTime(), true);
				current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
				current.draw(g.batch);
			}
			else if(getLastX() - getX() < 0)
			{
				HitboxSprite current = AnimationUtil.getCurrentSprite(headlessRight, getAnimationTime(), true);
				current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
				current.draw(g.batch);
			}
			else
			{
				HitboxSprite current = AnimationUtil.getCurrentSprite(headlessIdle, getAnimationTime(), true);
				current.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
				current.draw(g.batch);
			}
			
			getPlayerHitHitbox().setPosition(getX(), getY());
			
			if(System.currentTimeMillis() - getLastMoveTime() > 100)
			{
				lastX = getX();
				lastY = getY();
			}
			
			if(!playSpecial)
				specialTimer = 0;
		}
	}
	
	private float damageModifier = 0F;
	
	@Override
	public float getDamageModifier()
	{
		return damageModifier;
	}
	
	@Override
	public void setDamageModifier(float damageModifier)
	{
		this.damageModifier = damageModifier;
	}

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Sekibanki boss = this;
		
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
						
						bar.getObject().addSplit(0.7f);
						
						AllStarUtil.introduce(boss);
						
						boss.setHealth(0.1f);
						boss.healUp();
						BossUtil.backgroundAura(boss, boss.getBgAuraColor());
						
						Game.getGame().startSpellCard(new SekibankiNonSpell(boss));
					}
				}, 90);
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !boss.isOnStage();
			}
		});
		
		scheme.doWait();

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				try
				{
					return !boss.isDead();
				}
				catch(Exception e)
				{
					return false;
				}
			}
		});
		
		scheme.doWait();
		
		bar.getObject().split();
		boss.setHealth(boss.getMaxHealth());

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new SekibankiSpell(boss));
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				try
				{
					return !boss.isDead();
				}
				catch(Exception e)
				{
					return true;
				}
			}
		});
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				game.clearSpellcards();
				game.clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class SekibankiHead extends Boss
	{
		public static Polygon hitbox = null;
		
		public static SekibankiHead newInstance(float x, float y)
		{
			int chunkHeight = 64;
			int chunkWidth = 64;

			Texture sprite = getTexture();

			Animation idle = ImageSplitter.getAnimationFromSprite(sprite, 0, 6 * 160, chunkHeight, chunkWidth, 0.4F, 1,2,3,4);

			Animation left = ImageSplitter.getAnimationFromSprite(sprite, 0, 6 * 160, chunkHeight, chunkWidth, 0.4F, 5,6);
			Animation right = ImageSplitter.getAnimationFromSprite(sprite, 0, 6 * 160, chunkHeight, chunkWidth, 0.4F, 5,6);

			for(TextureRegion reg : right.getKeyFrames())
				reg.flip(true, false);

			Animation special = AnimationUtil.copyAnimation(idle, 20f);
			special.setPlayMode(PlayMode.NORMAL);

			final SekibankiHead boss = new SekibankiHead(1f, null, idle, right, left, special, x, y);
			
			if(hitbox == null)
				hitbox = HitboxUtil.makeHitboxFromSprite(idle.getKeyFrames()[0], HitboxUtil.StandardBulletSpecification.get());
			
			boss.playerHitHitbox = new Polygon(hitbox.getVertices());
			
			Rectangle bound = boss.playerHitHitbox.getBoundingRectangle();
			boss.playerHitHitbox.setOrigin(bound.getWidth() / 2, bound.getHeight() / 2);
			boss.playerHitHitbox.rotate(180);
			
			return boss;
		}
		
		public static Texture getTexture()
		{
			Texture texture = null;
			
			for(StageObject object : Game.getGame().getStageObjects())
				if(object instanceof Sekibanki)
				{
					texture = ((Sekibanki)object).idle.getKeyFrames()[0].getTexture(); // Get the animation texture from sekibanki's idle sprite.
					break;
				}
			
			if(texture == null)
			{
				System.out.println("No sekibanki instance found on screen, loading from file.");
				texture = Loader.texture(Gdx.files.internal("enemy/sekibanki/anm.png"));
			}
			
			return texture;
		}
		
		public SekibankiHead(float maxHealth, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, float x, float y)
		{
			super(fullBodySprite, idle, left, right, special, maxHealth, x, y);
		}
		
		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
		}
		
		@Override
		public void setX(float x)
		{
			lastX = this.x;
			this.x = x;
		}
		
		@Override
		public void setY(float y)
		{
			lastY = this.y;
			this.y = y;
		}
	}
	
	public static class SekibankiNonSpell extends Spellcard
	{	
		public SekibankiNonSpell(StageObject owner)
		{
			super(owner);
		}
		
		ArrayList<SekibankiBulletWorm> worms = new ArrayList<SekibankiBulletWorm>();
		float lastXChange = 0;
		float x;
		float y;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Sekibanki boss = (Sekibanki) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0.7f);
			
			if(tick % 200 == 160)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 200, (int)(getGame().getMaxX() / 2) + 200, Game.getGame().getHeight()/2 - 100, Game.getGame().getHeight()/2 + 100, 800);
			
			
			if(tick % 100 == 0)
			{
				x = boss.getX();
				y = boss.getY();
				lastXChange = 0f;
				
				for(int i = 0; i < 360; i += 30)
				{
					SekibankiBulletWorm worm = new SekibankiBulletWorm(i, (float)(x + Math.sin(Math.toRadians(i)) * 50), (float) (y + Math.cos(Math.toRadians(i)) * 50));
					
					int offset = i / 5;
				
					worm.spawnTick = Game.getGame().getTick() + offset;
					worm.endTick = Game.getGame().getTick() + 400 + offset;
					worms.add(worm);
				}
			}
			
			if(tick % 5 != 0)
				return;
			
			Iterator<SekibankiBulletWorm> it = worms.iterator();
			
			while(it.hasNext())
			{
				SekibankiBulletWorm worm = it.next();
				
				if(Game.getGame().getTick() > worm.endTick)
				{
					it.remove();
					continue;
				}
				
				if(Game.getGame().getTick() > worm.spawnTick)
					worm.update();
			}
		}
	}
	
	public static class SekibankiBulletWorm extends J2hObject
	{
		private float x, y, startRotation;
		
		public int id;
		
		public long spawnTick;
		public long endTick;
		
		public SekibankiBulletWorm(float startRotation, float x, float y)
		{
			this.startRotation = startRotation;
			this.x = x;
			this.y = y;
		}
		
		public void update()
		{
			startRotation += (Math.random() > 0.5f ? -1 : 1) * 10f;
			
			float min = 1f;
			float max = 30f;
			
			float multiplier = (float)Game.getGame().getTick() / 105 % 2;
			
			if(multiplier >= 1)
			multiplier = 1 - (multiplier - 1);
			
			multiplier = min + multiplier * (max - min);
			
			float velX = (float) (Math.sin(Math.toRadians(startRotation)) * multiplier);
			float velY = (float) (Math.cos(Math.toRadians(startRotation)) * multiplier);
			
			x += velX;
			y += velY;
			
			SekibankiBulletWormBullet bullet = new SekibankiBulletWormBullet(x, y)
			{
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(getTicksAlive() > 200)
						Game.getGame().delete(this);
				}
			};

			bullet.setZIndex(bullet.getZIndex() + this.id);
			this.id++;
			bullet.setRotationDeg((float) (Math.atan2(velY, velX) * (180 / Math.PI) - 90f));
			
			Game.getGame().spawn(bullet);
		}
	}
	
	public static class SekibankiBulletWormBullet extends Bullet
	{
		public SekibankiBulletWormBullet(float x, float y)
		{
			super(new ThBullet(ThBulletType.RAIN, ThBulletColor.RED), x, y);
			setGlowing();
		}
		
		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
			
			if(!Scheduler.isTracked("wormSpawn", "wormSpawn"))
			{
				long id = TouhouSounds.Enemy.RELEASE_1.play(0.5F);
				Scheduler.trackMillis("wormSpawn", "wormSpawn", (long) 200);
			}
		}
		
		@Override
		public void onDelete()
		{
			super.onDelete();
		}
	}

	public static class SekibankiSpell extends Spellcard
	{
		public SekibankiSpell(StageObject owner)
		{
			super(owner);
			
			((Sekibanki)owner).setDamageModifier(0.7f);
		}
		
		ArrayList<SekibankiHead> heads = new ArrayList<Sekibanki.SekibankiHead>();

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Sekibanki boss = (Sekibanki) getOwner();
			
			if(tick == 0)
			{
				BossUtil.moveTo(boss, Game.getGame().getWidth()/2, Game.getGame().getHeight() - 100, 500);

				BossUtil.bossAura(boss, Color.WHITE, new Color(0.0f, 0.2f, 1f, 1f));
			}
			
			if(tick < 30)
				return;
			
			if(tick == 30)
			{
				TouhouSounds.Enemy.HUM_2.play(1f);
				boss.hasHead = false;
				
				for(int i = 0; i < 360; i += 90)
				{
					SekibankiHead head = SekibankiHead.newInstance(boss.getX(), boss.getY() + 48);
					head.setZIndex(boss.getZIndex() + 1);
					Game.getGame().spawn(head);
					heads.add(head);
				}
			}
			
			int i = 0;
			
			boolean atBoss = tick % 1400 > 50 && tick % 1400 < 900;
			boolean idle = tick % 1400 > 900 && tick % 1400 < 1000 || atBoss && tick % 800 > 300 && tick % 800 < 500 || tick % 1400 <= 50;
			
			if(tick % 1400 == 950)
				TouhouSounds.Stage.TIMEOUT.play(1f);
			
			if(!idle && tick % 10 == 0)
				TouhouSounds.Enemy.BULLET_3.play(0.5f);

			for(SekibankiHead head : heads)
			{
				float degree = MathUtil.normalizeDegree(tick * 2 + i);

				float x = (float) (Math.sin(Math.toRadians(degree)) * (tick % 800));
				float y = (float) (Math.cos(Math.toRadians(degree)) * (tick % 400));

				head.setX(boss.getX() + x);
				head.setY(boss.getY() + y);

				if(tick % 20 <= 18 && !idle)
				{
					float[] modifiers = null;
					
					if(atBoss)
						// Small randomization to prevent safe spots.
						modifiers = new float[] { 0.15f, -0.15f };
					else
						modifiers = new float[] { -1f, -20f, 1f, 20f };

					float offset = 0.5f;
					
					for(float modifier : modifiers)
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ARROW, ThBulletColor.RED), head.getX(), head.getY())
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);
							}
							
							@Override
							public int getDeleteDistance()
							{
								return 1000;
							}
						};

						bullet.getSpawnAnimationSettings().setAlpha(0.1f);
						bullet.getSpawnAnimationSettings().setTime(10f);
						bullet.setGlowing();

						float dirRads = 0;
						
						if(atBoss)
							dirRads = (float) Math.toRadians(MathUtil.getAngle(bullet, boss) - modifier);
						else
						{
							double a = MathUtil.getDistance(boss, Game.getGame().getPlayer()) - Game.getGame().getPlayer().getHeight(); 
							a = Math.abs(a);

							double b = offset; 
							double c = Math.sqrt(a*a + b*b); 

							double angle = Math.acos((a*a + c*c - b*b) / (2.0*a*c)) * (180.0 / Math.PI); 

							dirRads = (float) (modifier * angle + Math.toRadians(MathUtil.getAngle(bullet, Game.getGame().getPlayer())));
						}
						
						bullet.getCurrentSprite().setHitboxOffsetX((float) (Math.cos(dirRads) * -8));
						bullet.getCurrentSprite().setHitboxOffsetY((float) (Math.sin(dirRads) * -8));

						bullet.setDirectionRadsTick(dirRads, atBoss ? tick % 800 > 400 ? 10f : 4f : 10f);
						bullet.setRotationFromVelocity(270f);
						Game.getGame().spawn(bullet);
					}
				}
				
				i += 90;
			}
		}
	}
}

