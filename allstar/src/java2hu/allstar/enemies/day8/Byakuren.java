package java2hu.allstar.enemies.day8;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.SmartTimer;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.VerticalScrollingBackground;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.plugin.sprites.FadeInSprite;
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
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.PathUtil;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Byakuren extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Byakuren";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Mana \"Sorcery Shield\"";
	
	public static Byakuren newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 256;
		int chunkWidth = 192;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 7F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation right = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1f, 5,6,7,8), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1f, 8), 8);
		Animation left = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1f, 9,10,11,12), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1f, 12), 8);

		Animation special = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 13,14), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 15,16), 10f);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		Sprite bge = new Sprite(Loader.texture(Gdx.files.internal(folder + "bge.png")));

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(7.5f);
		bgm.setLooping(true);
		
		final Byakuren boss = new Byakuren(100, nameTag, bg, bge, fbs, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	public Sprite bg;
	public Sprite bge;
	
	public Byakuren(float maxHealth, TextureRegion nametag, final Sprite bg, final Sprite bge, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
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
		final Byakuren boss = this;
		
		final SaveableObject<CircleHealthBar> bar = new SaveableObject<CircleHealthBar>();
		final SaveableObject<BackgroundAura> aura = new SaveableObject<BackgroundAura>();
		
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
						
						bar.getObject().setRadius(140);
						bar.getObject().addSplit(0.8f);
						
						AllStarUtil.introduce(boss);
						
						boss.healUp();
						aura.setObject(BossUtil.backgroundAura(boss, boss.getBgAuraColor()));
						
						Game.getGame().startSpellCard(new ByakurenNonSpell(boss));
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

		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().clear(ClearType.SPELLS, ClearType.TASKS, ClearType.ALL_OBJECTS);
				Game.getGame().delete(aura.getObject());
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new ByakurenSpell(boss));
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
				
				Game.getGame().clearSpellcards();
				Game.getGame().clear(ClearType.ALL_OBJECTS);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class ByakurenNonSpell extends Spellcard
	{	
		public ByakurenNonSpell(StageObject owner)
		{
			super(owner);
		}
		
		Texture texture;
		Animation cloudAnimation;
		
		{
			texture = Loader.texture(Gdx.files.internal("sprites/enemy/enemy_aura.png"));
			
			Array<TextureRegion> array = new Array<TextureRegion>();
			
			for(int i = 0; i < 8; i++)
			{
				Sprite sprite = new Sprite(texture, i * 48, 0, 48, 48);
				array.add(sprite);
			}
			
			getOwner().addDisposable(texture);
			
			cloudAnimation = new Animation(5, array);
			cloudAnimation.setPlayMode(PlayMode.LOOP);
		}
		
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Byakuren boss = (Byakuren) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0f);
			
			if(tick == 60)
				boss.setDamageModifier(0.5f);
			
			if(tick < 60)
				return;

			float time = 300;
			int specialPlay = (int) (time - 60);
			
			tick = tick - 60 + specialPlay;
			
			if(tick % time == time - 100 - 30)
			{
				boss.playSpecial(false);
			}

			if(tick % time == time - 100)
			{
				BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 300, (int)(getGame().getMaxX() / 2) + 300, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 200, 800);
			}
			
			if(tick % time == specialPlay)
			{
				TouhouSounds.Enemy.ACTIVATE_2.play();
				boss.playSpecial(true);
			}
			
			if(tick % time < 140 && tick % 4 == 0)
				TouhouSounds.Enemy.RELEASE_1.play();
			
			if(tick % time == 0)
			{
				boolean[] bools = {true, false};
				
				float offset = (float) (game.getTick() * Math.random() % 360f);
				
				for(boolean left : bools)
				for(float angle = 0; angle <= 360; angle += 360 / 14f)
				for(float addAngle = 0; addAngle < 3 * 360; addAngle += 3.5f)
				{
					if(addAngle % 5 >= 1)
						continue;
					
					final float degree = angle + offset + (left ? -1 : 1) * addAngle;
					final ThBulletColor color = left ? ThBulletColor.BLUE : ThBulletColor.PURPLE;
					
					float mul = addAngle / 400f;
					final float multiplier = mul > 1 ? 2 - mul : mul;
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							final Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_2, color), boss.getX(), boss.getY())
							{
								@Override
								public void onDraw()
								{
									if(getShader() != null)
									{
										getShader().setUniformf("blurSize", 0.04f);
										getShader().setUniformf("intensity", 2f);
									}
									
									super.onDraw();
								}
								
								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);
									
									float scale = 0;
									
									if(tick % 10 == 0)
									{
										scale = 0.72f;
									}
									else if(tick % 10 == 5)
									{
										scale = 0.8f;
									}
									
									if(scale != 0)
									{
										for(TextureRegion r : getAnimation().getKeyFrames())
										{
											((Sprite)r).setScale(scale);
										}
									}
								}
							};
							
							bullet.getSpawnAnimationSettings().setTime(20);
							bullet.getSpawnAnimationSettings().setAlpha(-1f);
							bullet.getSpawnAnimationSettings().setAddedScale(0.5f);
							bullet.setShader(ShaderLibrary.GLOW.getProgram());
							bullet.setDirectionDegTick(degree, 7f);

							game.spawn(bullet);
							
							DrawObject obj = new DrawObject()
							{
								Animation animation = AnimationUtil.copyAnimation(cloudAnimation);
								float offsetX = 0;
								float offsetY = 0;
								
								{
									float newDegree = degree;
									float newRad = (float) Math.toRadians(newDegree);
									float size = 1.8f;
									offsetX = (float) (Math.cos(newRad) * 7 * size);
									offsetY = (float) (Math.sin(newRad) * 7 * size);
									
									animation.setPlayMode(PlayMode.LOOP);
									
									Color newColor = color.getColor().cpy();
									
									if(color == ThBulletColor.RED)
									{
										newColor.r *= 1.5f;
										newColor.b *= 1.5f;
										newColor.g *= 1.5f;
									}
									else
									{

									}
									
									for(TextureRegion r : animation.getKeyFrames())
									{
										Sprite sprite = (Sprite)r;
										sprite.setScale(size);
										sprite.setColor(newColor);
										sprite.setAlpha(0.4f);
										sprite.setRotation(degree - 90);
									}
								}
								
								@Override
								public void onDraw()
								{
									if(bullet.getTicksAlive() < 30)
										return;
									
									Sprite sprite = (Sprite) animation.getKeyFrame(Game.getGame().getTick());
									
									sprite.setPosition(bullet.getX() + offsetX - sprite.getWidth() / 2f, bullet.getY() + offsetY - sprite.getHeight() / 2f);
									sprite.draw(Game.getGame().batch);
								}
								
								@Override
								public void onUpdate(long tick)
								{
									if(!bullet.isOnStage())
										game.delete(this);
								}
							};
							
							obj.setZIndex(bullet.getZIndex() - 1);
							game.spawn(obj);
						}
					}, (int) (addAngle / 7f) + (left ? 14 : 0));
				}
			}
		}
	}

	public static class ByakurenSpell extends Spellcard
	{
		public ByakurenSpell(StageObject owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Byakuren boss = (Byakuren) getOwner();
			
			if(tick == 0)
			{
				boss.playSpecial(false);
				PathUtil.moveTo(boss, Game.getGame().getWidth()/2f, 700, 50);
				boss.setDamageModifier(0f);
			}
			
			if(tick == 60)
			{
				VerticalScrollingBackground bg = new VerticalScrollingBackground(boss.bg, 1f, false);
				bg.addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return boss.bg;
					}
				}, 0.02f));
				bg.setZIndex(-3);
				game.spawn(bg);
				
				DrawObject bge = new DrawObject()
				{
					Sprite bge = boss.bge;
					
					{
						bge.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						bge.setOriginCenter();
					}
					
					@Override
					public void onDraw()
					{
						bge.draw(game.batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						bge.rotate(0.4f);
					}
				};
				
				bge.setZIndex(-2);
				game.spawn(bge);
				
				DrawObject aura = new DrawObject()
				{
					Byakuren byakuren = boss;
					
					Sprite back;
					Sprite middle;
					
					Sprite flowerBotLeft;
					Sprite flowerBotRight;
					
					Sprite flowerTopLeft;
					Sprite flowerTopRight;
					
					{
						Texture aura1 = Loader.texture(Gdx.files.internal("enemy/byakuren/aura.png"));
						Texture aura2 = Loader.texture(Gdx.files.internal("enemy/byakuren/aura2.png"));
						
						addDisposable(aura1);
						addDisposable(aura2);
						
						back = new Sprite(aura2);
						back.setScale(0f);
						middle = new Sprite(aura1, 0, 0, 386, 256);
						middle.setScale(0, 2);
						
						flowerBotLeft = new Sprite(aura1, 386, 0, 72, 80);
						flowerBotLeft.setScale(0f);
						flowerBotRight = new Sprite(flowerBotLeft);
						flowerBotRight.setScale(0f);
						flowerBotRight.flip(true, false);
						
						flowerTopLeft = new Sprite(aura1, 386, 80, 72, 80);
						flowerTopLeft.setScale(0f);
						flowerTopRight = new Sprite(flowerTopLeft);
						flowerTopRight.setScale(0f);
						flowerTopRight.flip(true, false);
						
						TouhouSounds.Enemy.HUM_2.play(1f);
					}
					
					boolean backSpawned = false;
					float scaleBack = 0;
					float rotationBack = 180;
					
					boolean middleSpawned = false;
					float scaleMiddleX = 0;
					
					boolean flowersSpawned = false;
					float scaleFlowers = 0;
					
					private SmartTimer backTimer = new SmartTimer(0.04f, 0f, 0.2f, 1f, 0.8f, 0.01f);
					private SmartTimer middleTimer = new SmartTimer(0.04f, 0f, 0.2f, 1f, 0.8f, 0.004f);
					private SmartTimer flowerTimer = new SmartTimer(0.04f, 0f, 0.2f, 1f, 0.8f, 0.02f);
					
					@Override
					public void onDraw()
					{
						J2hGame game = Game.getGame();
						SpriteBatch batch = game.batch;
						
						back.setPosition(byakuren.getX() - back.getWidth() / 2f, byakuren.getY() - back.getHeight() / 2f);
						back.draw(batch);
						
						middle.setPosition(byakuren.getX() - middle.getWidth() / 2f, byakuren.getY() - middle.getHeight() / 2f);
						middle.draw(batch);
						
						flowerTopLeft.setPosition(byakuren.getX() - 170, byakuren.getY() + 140);
						flowerTopLeft.draw(batch);
						
						flowerTopRight.setPosition(byakuren.getX() + 90, byakuren.getY() + 140);
						flowerTopRight.draw(batch);
						
						flowerBotLeft.setPosition(byakuren.getX() - 275, byakuren.getY() - 115);
						flowerBotLeft.draw(batch);
						
						flowerBotRight.setPosition(byakuren.getX() + 195, byakuren.getY() - 115);
						flowerBotRight.draw(batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						if(!backSpawned)
						{
							if(scaleBack < 2)
							{
								scaleBack += 0.05f;
								back.setScale(scaleBack);
							}

							if(rotationBack < 360)
							{
								rotationBack = 180 + Math.min(180, scaleBack / 2f * 180f);
								back.setRotation(rotationBack);
							}
							
							if(scaleBack >= 2 && rotationBack >= 360)
								backSpawned = true;
						}
						else if(!middleSpawned)
						{
							if(scaleMiddleX < 2)
							{
								scaleMiddleX += 0.08f;
								middle.setScale(scaleMiddleX, 2);
							}
							
							if(scaleMiddleX >= 2)
								middleSpawned = true;
						}
						else if(!flowersSpawned)
						{
							if(scaleFlowers < 2)
							{
								scaleFlowers += 0.1f;
								
								flowerTopLeft.setScale(scaleFlowers);
								flowerTopRight.setScale(scaleFlowers);
								flowerBotLeft.setScale(scaleFlowers);
								flowerBotRight.setScale(scaleFlowers);
							}
							
							if(scaleFlowers >= 2)
							{
								TouhouSounds.Enemy.BREAK_1.play();
								flowersSpawned = true;
							}
						}
						else
						{
							// Start pulsing.
							backTimer.tick();
							middleTimer.tick();
							flowerTimer.tick();
							
							middle.setScale(scaleMiddleX + middleTimer.getTimer() * 0.1f, 2);
							
							float flowerRotation = flowerTimer.getTimer() * 4;
							flowerTopLeft.setRotation(flowerRotation);
							flowerTopRight.setRotation(flowerRotation);
							flowerBotLeft.setRotation(flowerRotation);
							flowerBotRight.setRotation(flowerRotation);
							
							back.setScale(2f + backTimer.getTimer() * 0.2f);
						}
					}
				};
				
				game.spawn(aura);
			}
			
			if(tick == 100)
			{
				TouhouSounds.Enemy.ACTIVATE_2.play();
				boss.playSpecial(true);
			}
			
			if(tick == 150)
				boss.setDamageModifier(0.5f);
			
			if(tick < 150)
				return;
			
			tick -= 150;
			
			int time = 200;
			
			if(tick % time == time - 60)
			{
				TouhouSounds.Enemy.ACTIVATE_2.play();
				boss.playSpecial(true);
			}
			
			if(tick % time == 20)
			{
				boss.playSpecial(false);
			}
			
			if(tick % time == 0)
			{
				TouhouSounds.Enemy.RELEASE_1.play();
				
				float increment = 4f;
				
				boolean top = tick % (2f * time) < time;
				boolean[] bools = { true, false };
				
				for(final boolean left : bools)
				{
					final float xPos = boss.getX() + (top ? left ? 130 : -130 : left ? 225 : -235);
					final float yPos = boss.getY() + (top ? 180 : -70);
					
					final ArrayList<Bullet> bullets = new ArrayList<Bullet>();
					
					for(int i = 0; i < 360; i += increment)
					{
						final float finalI = i;

						final boolean knife = i % (8 * (int)increment) == 0;

						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.SEAL, knife ? ThBulletColor.YELLOW_LIGHT : ThBulletColor.BLUE), xPos, yPos)
							{
								float degreeStart = finalI;
								float degree = finalI;

								boolean turned = false;

								@Override
								public void onUpdate(long tick)
								{
									super.onUpdate(tick);

									if(!turned)
									{
										if(left)
										{
											if(degreeStart - degree < 180)
											{
												setDirectionDegTick(degree, 3f);
												setRotationFromVelocity(-90f);

												degree -= 0.3f;
											}
										}
										else
										{
											if(degree - degreeStart < 180)
											{
												setDirectionDegTick(degree, 3f);
												setRotationFromVelocity(-90f);

												degree += 0.3f;
											}
										}
									}

									if(knife && getTicksAlive() > 60)
									{
										if(!turned)
										{
											if(MathUtil.getDifference(getX(), player.getX()) < 5)
											{
												setBullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.YELLOW));
												setDirectionDegTick(90, -0.1f);
												setRotationFromVelocity(90f);
												turned = true;
											}
										}
										else
										{
											setVelocityYTick(getVelocityYTick() + 0.05f);
										}
									}
								}
							};

							bullet.setDirectionDegTick(i, 5f);
							bullet.setRotationFromVelocity(-90f);
							bullet.setZIndex(bullet.getZIndex() + i);
							game.spawn(bullet);
							
							if(!knife)
								bullets.add(bullet);
						}
					}
					
					DrawObject obj = new DrawObject()
					{
						float radius = 20;
						
						@Override
						public void onDraw()
						{
							game.batch.end();
							
							Gdx.gl.glLineWidth(2f);
							game.shape.setColor(Color.BLUE);
							game.shape.begin(ShapeType.Line);
							game.shape.circle(xPos, yPos, radius);
							game.shape.setColor(Color.WHITE);
							game.shape.end();
							
							game.batch.begin();
						}
						
						@Override
						public void onUpdate(long tick)
						{
							Bullet first = getFirst();
							
							if(first == null)
							{
								Game.getGame().delete(this);
								return;
							}
							
							radius = MathUtil.getDistance(first.getX(), first.getY(), xPos, yPos);
						}
						
						public Bullet getFirst()
						{
							for(Bullet b : bullets)
							{
								if(b.isOnStage())
									return b;
							}
							
							return null;
						}
					};
					
					game.spawn(obj);
				}
			}
		}
	}
}

