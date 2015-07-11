package java2hu.allstar.enemies.day2;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.Background;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.helpers.ZIndexHelper;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.GravityBullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.pathing.SimpleTouhouBossPath;
import java2hu.plugin.Plugin;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.BossUtil.BossEffectsResult;
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Mokou extends AllStarBoss
{
	public Mokou(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal("enemy/mokou/anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal("enemy/mokou/nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 5,6), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 7,8), 8f);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 9,10,11,12,12,12,12,12);
		special.setPlayMode(PlayMode.NORMAL);

		final Texture bg = Loader.texture(Gdx.files.internal("enemy/mokou/bg.png"));
		final Texture bge = Loader.texture(Gdx.files.internal("enemy/mokou/bge.png"));
		
		addDisposable(bg);
		addDisposable(bge);
		
		backgroundSetter = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(BackgroundBossAura t)
			{
				Background back = new Background(bg);
				back.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(back);
				
				final float width = game.getWidth() / 2f;
				final float height = game.getHeight() / 2f;
				
				float size = (float) Math.sqrt((width * width) + (height * height));
				
				final Sprite moonSprite = new Sprite(bge);
				
				size *= 2f;

				moonSprite.setPosition(game.getCenterX() - (size / 2f), game.getCenterY() - (size / 2f));
				moonSprite.setSize(size, size);
				
				moonSprite.setOriginCenter();
				
				DrawObject moon = new DrawObject()
				{
					@Override
					public void onDraw()
					{
						moonSprite.draw(game.batch);
					}
					
					@Override
					public void onUpdateDelta(float delta)
					{
						moonSprite.rotate(20f * delta);
					}
				};
				
				moon.setZIndex(back.getZIndex() + 1);
				
				moon.setFrameBuffer(t.getBackgroundBuffer());
				
				game.spawn(moon);
			}
		};
		

		Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal("enemy/mokou/bgm.mp3")));
		bgm.setLooping(true);
		
		setBgmPosition(25f);
		
		final Sprite phoenix = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/aura.png")));
		phoenix.setOriginCenter();
		phoenix.setScale(2F);
		
		phoenixSpawner = new Getter<StageObject>()
		{
			@Override
			public StageObject get()
			{
				final Mokou boss = Mokou.this;
				
				StageObject obj = new DrawObject()
				{
					@Override
					public void onDraw()
					{
						phoenix.setColor(Color.WHITE);
						phoenix.setPosition(boss.getX() - (phoenix.getWidth() / 2f), boss.getY() - (phoenix.getHeight() / 2f));
						phoenix.setScale(2f);
						phoenix.draw(game.batch);
						
						final float pulseTimeSeconds = 0.6f;
						final float scaler = ((game.getElapsedTime() % pulseTimeSeconds) / pulseTimeSeconds) * 1.5f;
						float alpha = ((scaler > 1.25f ? (scaler - 1.25f) / 0.25f : 0f) * 1f);
						
						phoenix.setColor(2f, 0.5f, 0.5f, 0.5f + (alpha * -0.5f));
						
						phoenix.setScale(1f + scaler);
						
						phoenix.draw(game.batch);
					}
					
					@Override
					public float getWidth()
					{
						return 0;
					}
					
					@Override
					public float getHeight()
					{
						return 0;
					}
				};
				
				obj.setZIndex(boss.getZIndex() - 2);
				
				game.spawn(obj);
				
				boss.addChild(obj);
				
				return obj;
			}
		};
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
		
		setAuraColor(AllStarUtil.from255RGB(255, 0, 0));
		setBgAuraColor(AllStarUtil.from255RGB(255, 0, 0));
	}
	
	public Setter<BackgroundBossAura> backgroundSetter;
	public Getter<StageObject> phoenixSpawner;
	
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
		final Mokou boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		final SaveableObject<BackgroundAura> aura = new SaveableObject<BackgroundAura>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(boss, boss.getAuraColor(), boss.getBgAuraColor(), 60);

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
						
						BossEffectsResult r = BossUtil.addBossEffects(boss, boss.getAuraColor(), boss.getBgAuraColor());
						
						aura.setObject(r.bgAura);
						
						boss.setHealth(0.1f);
						boss.healUp();
						
						Game.getGame().startSpellCard(new MokouNonSpell(boss));
					}
				}, 60);
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
		
		aura.getObject().setMagicSquareEnabled(false);

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				
				AllStarUtil.presentSpellCard(boss, "Honest Man's Death \"I just wanted the powerup\"");
				
				TouhouSounds.Enemy.HUM_2.play();
				
				final MokouSpell card = new MokouSpell(boss);
				
				Game.getGame().startSpellCard(card);
				
				BossUtil.spellcardCircle(boss, card, scheme.getBossAura());
				
				boss.backgroundSetter.set(scheme.getBossAura());
				boss.phoenixSpawner.get();
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
	
	public static class MokouNonSpell extends Spellcard
	{	
		public MokouNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(20));
		}
		
		private ZIndexHelper indexer = new ZIndexHelper();

		float redRotation = 0F;
		float blueRotation = 40F;
		float purpleRotation = 80F;
		
		@Override
		public void tick(int tick)
		{
			if(tick < 60)
				return;
			
			if(tick % 100 == 0)
			{
				final SimpleTouhouBossPath path = new SimpleTouhouBossPath(getOwner());
				
				path.setTime(Duration.ticks(60));
				
				getOwner().getPathing().setCurrentPath(path);
			}
			
			if(tick % 16 == 0)
				TouhouSounds.Enemy.BULLET_1.play(0.3F, 1F, 0F);
			
			if(tick % 2 != 0 )
				return;
			
			if(tick == 100)
				((Mokou)getOwner()).setDamageModifier(1f);

			float x = getOwner().getX();
			float y = getOwner().getY();
			float increase = 5F;
			float speed = 4F;
			
			redRotation += increase;
			blueRotation += increase;
			purpleRotation += increase;

			for(int addRotation = 0; addRotation < 360; addRotation += 360 / 3)
			{
				double rotation = -Math.toRadians(redRotation + addRotation);
				
				Bullet red = new Bullet(new ThBullet(ThBulletType.SEAL, ThBulletColor.RED), (float) (x - Math.sin(rotation) * 60), (float) (y + Math.cos(rotation) * 60));
			
				for(TextureRegion reg : red.getAnimation().getKeyFrames())
					((HitboxSprite)reg).setHitboxScaleOffsetModifier(0.5F);
				
				red.setDirectionRadsTick((float) rotation, speed);
				red.setRotationFromVelocity(90F);
				indexer.index(red);

				Game.getGame().spawn(red);
			}

			for(int addRotation = 0; addRotation < 360; addRotation += 360 / 3)
			{
				double rotation = -Math.toRadians(blueRotation + addRotation);
				
				Bullet blue = new Bullet(new ThBullet(ThBulletType.SEAL, ThBulletColor.BLUE), (float) (x - Math.sin(rotation) * 60), (float) (y + Math.cos(rotation) * 60));
				
				for(TextureRegion reg : blue.getAnimation().getKeyFrames())
					((HitboxSprite)reg).setHitboxScaleOffsetModifier(0.5F);
				
				blue.setDirectionRadsTick((float) rotation, speed + 1F);
				blue.setRotationFromVelocity(90F);
				indexer.index(blue);

				Game.getGame().spawn(blue);
			}

			for(int addRotation = 0; addRotation < 360; addRotation += 360 / 3)
			{
				double rotation = -Math.toRadians(purpleRotation + addRotation);
				
				Bullet purple = new Bullet(new ThBullet(ThBulletType.SEAL, ThBulletColor.PURPLE), (float) (x - Math.sin(rotation) * 60), (float) (y + Math.cos(rotation) * 60));
				
				for(TextureRegion reg : purple.getAnimation().getKeyFrames())
					((HitboxSprite)reg).setHitboxScaleOffsetModifier(0.5F);
				
				purple.setDirectionRadsTick((float) rotation, speed - 1F);
				purple.setRotationFromVelocity(90F);
				indexer.index(purple);

				Game.getGame().spawn(purple);
			}
		}
	}

	public static class MokouSpell extends Spellcard
	{
		private static Animation oneUpAnimation;
		private static Animation chaserBulletAnimation;
		private Bullet chaserBullet;
		
		public void load()
		{
			TextureRegion texture = null;
			texture = new TextureRegion(Loader.texture(Gdx.files.internal("enemy/mokou/1up.png")));
			
			HitboxSprite sprite = new HitboxSprite(texture);
			oneUpAnimation = new Animation(1, sprite);
			sprite.setHitbox(HitboxUtil.loadHitbox(Gdx.files.internal("enemy/mokou/hitbox.vertices")));
			sprite.setHitboxScaleOffsetModifier(0.5F);
			
			texture = new TextureRegion(Loader.texture(Gdx.files.internal("enemy/mokou/bomb.png")));
			
			sprite = new HitboxSprite(texture);
			
			chaserBulletAnimation = new Animation(1, sprite);
			sprite.setHitbox(HitboxUtil.loadHitbox(Gdx.files.internal("enemy/mokou/hitbox.vertices")));
			sprite.setHitboxScaleOffsetModifier(0.75F);
			sprite.setScale(4F);
		}
		
		public MokouSpell(StageObject owner)
		{
			super(owner);
			
			load();
			
			final Mokou mokou = (Mokou)getOwner();
			
			mokou.setDamageModifier(0.7f);
			setSpellcardTime(Duration.seconds(60));
		}

		@Override
		public void tick(int tick)
		{
			if(tick == 0)
			{
				DrawObject obj = new DrawObject()
				{
					Texture texture = Loader.texture(Gdx.files.internal("enemy/mokou/bar.png"));
					Sprite bar = new Sprite(texture, 0, 54, 348, 22);
					Sprite text = new Sprite(texture, 0, 0, 348, 54);
					
					@Override
					public void onDraw()
					{
						bar.setPosition(10, Game.getGame().getMaxY() * 0.4f);
						bar.setSize(Game.getGame().getMaxX() - 20, bar.getHeight());
						bar.draw(Game.getGame().batch);
						
						text.setPosition(Game.getGame().getWidth() / 2 - text.getWidth() / 2, Game.getGame().getMaxY() * 0.4f + 40);
						text.draw(Game.getGame().batch);
					}
					
					float alpha = 0f;
					
					@Override
					public void onUpdate(long tick)
					{
						if(alpha < 1)
						{
							alpha += 0.01f;
							
							bar.setAlpha(Math.min(alpha, 1f));
							text.setAlpha(Math.min(alpha, 1f));
						}
					}
					
					{
						bar.setAlpha(0f);
						text.setAlpha(0f);
						
						setZIndex(1);
					}
				};
				
				Game.getGame().spawn(obj);
			}
			
			if(tick < 60)
				return;
			
			if(tick % 200 == 0)
			{
				final SimpleTouhouBossPath path = new SimpleTouhouBossPath(getOwner());
				
				path.setTime(Duration.ticks(100));
				
				getOwner().getPathing().setCurrentPath(path);
			}
			
			if(tick % 10 == 0 && tick > 120)
				TouhouSounds.Enemy.BULLET_3.play(0.2F);
		
			{
				if(chaserBullet == null || !chaserBullet.isOnStage())
				{
					chaserBullet = new Bullet(AnimationUtil.copyAnimation(chaserBulletAnimation), getOwner().getX(), getOwner().getY())
					{
						{
							useSpawnAnimation(false);
							setZIndex(1001);
							setDeletionColor(Color.GREEN);
						}
						
						float rotation = 0;
						boolean complete = false;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(rotation < 360)
							{
								rotation += 4F;
								setRotationDeg(rotation);
							}
							
							if(getVelocityYTick() < 0F && !complete)
							{
								setVelocityYTick(getVelocityYTick() + 0.15F);
								
								return;
							}
							else
								complete = true;

							float distance = MathUtil.getDistance(getX(), getY(), getGame().getPlayer().getX(), getGame().getPlayer().getY());

							float x = (getX() - getGame().getPlayer().getX()) / distance * 1.8F;
							float y = (getY() - getGame().getPlayer().getY()) / distance * 1.8F;
							
							float speed = 1.8f;
							
							x *= speed;
							y *= speed;
							
							if(Game.getGame().getPlayer().getY() > Game.getGame().getMaxY() * 0.4f && getY() > Game.getGame().getMaxY() * 0.4f)
							{
								x *= 4;
								y *= 4;
							}

							setVelocityXTick(x);
							setVelocityYTick(y);
						}
						
						@Override
						public void onHit()
						{
							super.onHit();
							
							TouhouSounds.Stage.BONUS_1.play();
						}
					};
					
					chaserBullet.setVelocityYTick(-6F);
					
					((Boss)getOwner()).playSpecial(true);
					
					getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							TouhouSounds.Stage.BONUS_2.play();
						}
					}, 10);

					getGame().spawn(chaserBullet);
				}
				
				if(tick < 120 || tick % 4 != 0)
					return;

				halfRain(-60, 0, tick, false);
				halfRain(60, 0, tick, true);

				halfRain(-60, 50, tick, false);
				halfRain(60, 50, tick, true);
				
			}
		}
		
		private ZIndexHelper indexer = new ZIndexHelper();

		public void halfRain(float x, float y, int tick, boolean right)
		{
			float waveTime = 30; // How wide the pendulem is
			float divident = 80 - (y > 0 ? 20 : 0); // How much rotations it makes
			float max = waveTime / divident;
			
			final boolean finalRight = right;
			
			if(tick % (waveTime * 4) >= waveTime * 2)
				right = !right;
			
			GravityBullet bullet = new GravityBullet(AnimationUtil.copyAnimation(oneUpAnimation), getOwner().getX() + x, getOwner().getY() + y - 50, 0.002F, 4F)
			{
				float scale = 4F;
				Animation originalAnimation;

				{
					getCurrentSprite().setRotation(finalRight ? 270 : -270);
					
					addEffect(new Plugin<GravityBullet>()
					{
						@Override
						public void update(GravityBullet object, long tick)
						{
							object.getCurrentSprite().rotate(finalRight ? -2f : 2f);
							
							if(finalRight ? object.getCurrentSprite().getRotation() <= 0 : object.getCurrentSprite().getRotation() >= 0)
							{
								object.getCurrentSprite().setRotation(0);
								delete();
							}
						}
					});
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(originalAnimation == null)
						originalAnimation = new Animation(getAnimation().getFrameDuration(), getAnimation().getKeyFrames());
					
					for(TextureRegion r : getAnimation().getKeyFrames())
						((HitboxSprite)r).setScale(scale);
					
					if(Game.getGame().getPlayer().getY() > Game.getGame().getMaxY() * 0.4f)
					{
						float distance = MathUtil.getDistance(getX(), getY(), getGame().getPlayer().getX(), getGame().getPlayer().getY());

						float x = (getX() - getGame().getPlayer().getX()) / distance * 1.8F;
						float y = (getY() - getGame().getPlayer().getY()) / distance * 3F;
						
						x *= 2f;
						y *= 2f;

						setVelocityXTick(x);
						setVelocityYTick(y);
					}
					else
					{
						if(getVelocityXTick() == 0)
							setVelocityXTick((float) (Math.random() * 1F - 0.5F));
					}
				}
				
				@Override
				public void onHit()
				{
					super.onHit();
					
					TouhouSounds.Player.EXTEND.play();
				}
			};

			bullet.setVelocityYTick(-6F);
			bullet.useSpawnAnimation(false);
			bullet.setDeletionColor(Color.PURPLE);
			indexer.index(bullet);
			

			if(tick % (waveTime * 2) == waveTime)
				bullet.setVelocityXTick(max);
			else if(tick % (waveTime * 2) <= waveTime)
				bullet.setVelocityXTick(tick % waveTime / divident);
			else
				bullet.setVelocityXTick((waveTime - tick % waveTime) % waveTime / divident);
			
			bullet.setVelocityXTick(bullet.getVelocityXTick() * 20F);
			
			if(right)
				bullet.setVelocityXTick(-bullet.getVelocityXTick());
			
			getGame().spawn(bullet);
		}
	}
}

