package java2hu.allstar.enemies.day2;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.GravityBullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.plugin.sprites.ScalingSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.Getter;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Fujiwara No Mokou (IN)
 * Spell: "Honest Man's Death - "I just wanted the powerup...""
 */
public class Mokou extends AllStarBoss
{
	public static Mokou newInstance(float x, float y)
	{
		int chunkHeight = 160;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal("enemy/mokou/anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal("enemy/mokou/nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 12F, 5,6,7,8);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 9,10,11,12,12,12,12,12);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/bg.png")));
		Sprite bge = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/bge.png")));

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal("enemy/mokou/bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(26f);
		bgm.setLooping(true);
		
		Sprite phoenix = new Sprite(Loader.texture(Gdx.files.internal("enemy/mokou/aura.png")));
		phoenix.setScale(2F);

		final Mokou mokou = new Mokou(100, nameTag, fbs, idle, left, right, special, bgm, bg, bge, phoenix, x, y);
		
		return mokou;
	}
	
	private Sprite phoenix;
	public Sprite bg;
	public Sprite bge;
	
	public Mokou(float maxHealth, TextureRegion nametag, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, final Sprite bg, final Sprite bge, final Sprite phoenix, float x, float y)
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
		addDisposable(phoenix);
		
		this.setColor(new Color(140, 0, 0, 1));
		
		this.phoenix = phoenix;
		
		this.bg = bg;
		this.bge = bge;
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
	public void executeFight(AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Mokou mokou = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		final SaveableObject<BackgroundAura> aura = new SaveableObject<BackgroundAura>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				BossUtil.cloudEntrance(mokou, 60);

				g.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						bar.setObject(new CircleHealthBar(mokou));
						
						g.spawn(mokou);
						g.spawn(bar.getObject());
						
						bar.getObject().addSplit(0.7f);
						
						AllStarUtil.introduce(mokou);
						aura.setObject(BossUtil.backgroundAura(mokou));
						
						mokou.setHealth(0.1f);
						mokou.healUp();
						
						Game.getGame().startSpellCard(new MokouNonSpell(mokou));
					}
				}, 60);
				
//				Game.getGame().spawn(new MusicPositionTimer(mokou.getBackgroundMusic()));
			}
		}, 1);

		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !mokou.isOnStage();
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
					return !mokou.isDead();
				}
				catch(Exception e)
				{
					return false;
				}
			}
		});
		
		scheme.doWait();
		
		bar.getObject().split();
		mokou.setHealth(mokou.getMaxHealth());
		
		aura.getObject().setMagicSquareEnabled(false);

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().getSpellcards().clear();
				Game.getGame().clearObjects();
				AllStarUtil.presentSpellCard(mokou, "Honest Man's Death \"I just wanted the powerup\"");
				
				TouhouSounds.Enemy.HUM_2.play();
				
				Game.getGame().spawn(new DrawObject()
				{
					{
						setZIndex(-1);
					}
					
					{
						addEffect(new FadeInSprite(new Getter<Sprite>()
								{
							@Override
							public Sprite get()
							{
								return phoenix;
							}
						}
						, 0.01F));
						
						addEffect(new ScalingSprite(new Getter<Sprite>()
								{
							@Override
							public Sprite get()
							{
								return phoenix;
							}
						}
						, 3F, 2F, 0.02F));
					}
					
					@Override
					public boolean isPersistant()
					{
						return mokou.isOnStage();
					}
					
					@Override
					public void onDraw()
					{
						phoenix.setPosition(mokou.getDrawX() - phoenix.getWidth() / 2, mokou.getDrawY() - phoenix.getHeight() / 2);
						phoenix.draw(Game.getGame().batch);
					}
				});
				
				Game.getGame().startSpellCard(new MokouSpell(mokou));
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				try
				{
					return !mokou.isDead();
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
				Game.getGame().delete(mokou);
				
				game.clearSpellcards();
				game.clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(mokou.getX(), mokou.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class MokouNonSpell extends Spellcard
	{	
		public MokouNonSpell(StageObject owner)
		{
			super(owner);
		}

		float redRotation = 0F;
		float blueRotation = 40F;
		float purpleRotation = 80F;
		
		@Override
		public void tick(int tick)
		{
			if(tick < 60)
				return;
			
			if(tick % 80 == 0)
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 500);
			
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
			sprite.setScale(4F);
		}
		
		public MokouSpell(StageObject owner)
		{
			super(owner);
			
			load();
			
			final Mokou mokou = (Mokou)getOwner();
			
			mokou.setDamageModifier(0.7f);

			final Sprite bg = mokou.bg;
			final Sprite bge = mokou.bge;
			
			Game.getGame().spawn(new DrawObject()
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
					setZIndex(-3);
				}
				
				@Override
				public void onDraw()
				{
					bg.setPosition(0, 0);
					bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
					bg.draw(Game.getGame().batch);
				}
				
				@Override
				public boolean isPersistant()
				{
					return mokou.isOnStage();
				}
			});
			
			Game.getGame().spawn(new DrawObject()
			{
				{
					// Set size to contain the whole screen (even when rotated from any point)
					double maxDistance = Math.sqrt(480*480 + 640*640); // Distance from middle top any corner.
					
					bge.setSize((float)(maxDistance*2), (float)(maxDistance*2));
					bge.setAlpha(0.6F);
					bge.setOriginCenter();
					
					setZIndex(-2);
				}
				
				@Override
				public void onDraw()
				{
					bge.setPosition(640F - bge.getWidth() / 2, 480F - bge.getHeight() / 2);
					bge.draw(Game.getGame().batch);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					bge.rotate(0.4F);
				}
				
				@Override
				public boolean isPersistant()
				{
					return mokou.isOnStage();
				}
			});
			
//			Game.getGame().spawn(new DrawObject()
//			{
//				{
//					addEffect(new FadeInImageEffect(new Getter<Image>()
//							{
//						@Override
//						public Image run()
//						{
//							return bge;
//						}
//					}
//					,
//					new Setter<Image>()
//					{
//						@Override
//						public void run(Image t)
//						{
//							bge = t;
//						}
//					}, 0.01F));
//					setZIndex(-1);
//				}
//				
//				@Override
//				public void draw()
//				{
//					bge.rotate(0.4F);
//					
//					Stage.getGraphics().drawImage(bge, -1300, -1600);
//				}
//			});
		}

		@Override
		public void tick(int tick)
		{
			if(tick == 0)
			{
				DrawObject obj = new DrawObject()
				{
					Texture texture = Loader.texture(Gdx.files.internal("enemy/mokou/bar.png"));
					Sprite bar = new Sprite(texture, 512, 576, 846 - 512, 14);
					Sprite text = new Sprite(texture, 546, 526, 860 - 546, 34);
					
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
			
			if(tick % 10 == 0 && tick > 120)
				TouhouSounds.Enemy.BULLET_3.play(0.2F);
		
			{
				if(chaserBullet == null || !getGame().getBullets().contains(chaserBullet))
				{
					chaserBullet = new Bullet(AnimationUtil.copyAnimation(chaserBulletAnimation), getOwner().getX(), getOwner().getY())
					{
						{
							useSpawnAnimation(false);
							setZIndex(1001);
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
							
							x *= 2f;
							y *= 2f;
							
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

		public void halfRain(float x, float y, int tick, boolean right)
		{
			GravityBullet bullet = new GravityBullet(AnimationUtil.copyAnimation(oneUpAnimation), getOwner().getX() + x, getOwner().getY() + y - 50, 0.002F, 4F)
			{
				float scale = 0.2F;
				Animation originalAnimation;
				boolean randomized = false;
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(originalAnimation == null)
						originalAnimation = new Animation(getAnimation().getFrameDuration(), getAnimation().getKeyFrames());
					
					for(TextureRegion r : getAnimation().getKeyFrames())
						((HitboxSprite)r).setScale(scale);
					
					if(scale < 4)
						scale += 0.05F;
					
					if(Game.getGame().getPlayer().getY() > Game.getGame().getMaxY() * 0.4f)
					{

						float distance = MathUtil.getDistance(getX(), getY(), getGame().getPlayer().getX(), getGame().getPlayer().getY());

						float x = (getX() - getGame().getPlayer().getX()) / distance * 1.8F;
						float y = (getY() - getGame().getPlayer().getY()) / distance * 1.8F;
						
						x *= 2f;
						y *= 2f;

						setVelocityXTick(x);
						setVelocityYTick(y);
					}
					else
					{
						if(getVelocityXTick() == 0)
							setVelocityXTick((float) (Math.random() * 1F - 0.5F));

						if(getVelocityYTick() < getTerminalVelocity() && !randomized)
						{
							float randomizationX = 2F;
							float randomizationY = 0F;
							setVelocityYTick((float) (getVelocityYTick() + (Math.random() > 0.5 ? -(randomizationX * Math.random()) : randomizationX * Math.random())));
							setVelocityXTick((float) (getVelocityXTick() + (Math.random() > 0.5 ? -(randomizationY * Math.random()) : randomizationY * Math.random())));
							randomized = true;
						}
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
			
//			System.out.println(tick % 2000);
//			System.out.println(tick % 4000);
			
			float waveTime = 30; // How wide the pendulem is
			float divident = 80 - (y > 0 ? 20 : 0); // How much rotations it makes
			float max = waveTime / divident;
//			
			if(tick % (waveTime * 2) == waveTime)
				bullet.setVelocityXTick(max);
			else if(tick % (waveTime * 2) <= waveTime)
				bullet.setVelocityXTick(tick % waveTime / divident);
			else
				bullet.setVelocityXTick((waveTime - tick % waveTime) % waveTime / divident);
			
			bullet.setVelocityXTick(bullet.getVelocityXTick() * 20F);
			
//			if(bullet.getVelocityXTick() < 0.001F)
//				bullet.setVelocityXTick(-0.01F);
			
			if(tick % (waveTime * 4) >= waveTime * 2)
				right = !right;
			
			if(right)
				bullet.setVelocityXTick(-bullet.getVelocityXTick());
			
//			System.out.println(bullet.getVelocityXTick());

			getGame().spawn(bullet);
		}
	}
}

