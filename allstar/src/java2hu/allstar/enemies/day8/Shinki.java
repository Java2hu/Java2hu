package java2hu.allstar.enemies.day8;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.ZIndex;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.LaserDrawer.LaserAnimation;
import java2hu.object.bullet.StationaryLaser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.object.ui.CircleHealthBar;
import java2hu.overwrite.J2hMusic;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.spellcard.Spellcard;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.Duration;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.ImageUtil;
import java2hu.util.MathUtil;
import java2hu.util.MeshUtil;
import java2hu.util.ObjectUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Shinki extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Shinki";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "Devil's Citation - \"Rain in Pandæmonium\"";
	
	public static Shinki newInstance(float x, float y, final boolean conversation)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 2);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 1);
		Animation right = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 3);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 4);
		special.setPlayMode(PlayMode.NORMAL);
		
		Animation specialWings = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 5);
		idle.setPlayMode(PlayMode.LOOP);
		
		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));

		Sprite wingsNormal = new Sprite(sprite, 0, 128, 512, 192);
		Sprite wingsDemon = new Sprite(sprite, 0, 128 + 192, 512, 192);
		
		Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3")));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(24.5f);
		bgm.setLooping(true);
		
		final Shinki boss = new Shinki(100, nameTag, bg, wingsNormal, wingsDemon, fbs, idle, left, right, special, specialWings, bgm, x, y)
		{
			@Override
			public void onSpawn()
			{
				if(!conversation)
					super.onSpawn();
			}
		};
		
		return boss;
	}
	
	private Animation specialNormal;
	private Animation specialWings;
	private Sprite wingsNormal;
	private Sprite wingsDemon;
	private BackgroundAura aura;
	
	public Sprite bg;
	
	public Setter<BackgroundBossAura> backgroundSetter;
	
	public Shinki(float maxHealth, TextureRegion nametag, final Sprite bg, final Sprite wingsNormal, final Sprite wingsDemon, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Animation specialWings, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		this.specialNormal = special;
		this.specialWings = specialWings;
		this.wingsNormal = wingsNormal;
		this.wingsDemon = wingsDemon;
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(wingsNormal);
		addDisposable(wingsDemon);
		
		this.bg = bg;
		
		backgroundSetter = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
			{
				Game.getGame().spawn(new DrawObject()
				{
					{
						bg.setPosition(0, 0);
						bg.setBounds(game.getMinX(), game.getMinY(), game.getWidth(), game.getHeight());
						setFrameBuffer(t.getBackgroundBuffer());
						setZIndex(ZIndex.BACKGROUND_LAYER_2);
						
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return bg;
							}
						}
						, 0, 1f, 0.01F));
					}
					
					@Override
					public void onDraw()
					{
						bg.draw(Game.getGame().batch);
					}
				});
			}
		};
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		final Shinki boss = this;
		
		Game.getGame().spawn(new DrawObject()
		{
			Texture[] background = new Texture[3];
			float offset = 0;
			
			{
				makeBackgrounds();
				this.setZIndex(-1);
			}
			
			public void makeBackgrounds()
			{
				background[0] = ImageUtil.makeDummyTexture(new Color(1f, 0f, 0f, 0.2f), 1, 1);
				background[1] = ImageUtil.makeDummyTexture(new Color(0f, 0f, 1f, 0.2f), 1, 1);
				background[2] = ImageUtil.makeDummyTexture(new Color(1f, 0f, 1f, 0.2f), 1, 1);
			}
			
			@Override
			public void onDraw()
			{
				J2hGame game = Game.getGame();
				
				long tick = game.getTick();
				
				{
					Texture t = background[(int) (tick % 6000 / (6000f / 3f))];
					game.batch.draw(t, 0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
				}
				
				game.batch.end();
				
				game.shape.begin(ShapeType.Line);
				
				ShapeRenderer s = game.shape;
				
				if(tick % 3000 <= 1500)
					rotation();
				else
					outwards();
				
				s.setColor(Color.WHITE);
				
				s.end();
				
				game.batch.begin();
			}
			
			public void rotation()
			{
				J2hGame game = Game.getGame();
				
				long tick = game.getTick();
				
				float pos = tick % 100 / 100f;
				
				ShapeRenderer s = game.shape;
				
				Gdx.gl.glLineWidth(1f);
				
				float startX = Game.getGame().getWidth() / 2f - 400 + offset;
				float startY = Game.getGame().getHeight() / 2f + 300;

				int size = 500;
				
				float rad = (float) Math.toRadians(360 - 360 * pos);
				float radDown = (float) Math.toRadians(360 - (360 * pos - 180));
				
				// Left
				s.setColor(Color.WHITE);
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
				
				s.setColor(Color.GRAY);
				rad = (float) Math.toRadians(360 - 360 * pos - 30f);
				radDown = (float) Math.toRadians(360 - (360 * pos - 180) - 30f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
				
				s.setColor(Color.DARK_GRAY);
				rad = (float) Math.toRadians(360 - 360 * pos - 60f);
				radDown = (float) Math.toRadians(360 - (360 * pos - 180) - 60f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));

				s.setColor(Color.DARK_GRAY);
				rad = (float) Math.toRadians(360 - 360 * pos - 90f);
				radDown = (float) Math.toRadians(360 - (360 * pos - 180) - 90f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
				
				startX = Game.getGame().getWidth() / 2f + 400 + offset;
				startY = Game.getGame().getHeight() / 2f + 300;
				
				// Right
				rad = (float) Math.toRadians(360 * pos);
				radDown = (float) Math.toRadians(360 * pos - 180);
				s.setColor(Color.WHITE);
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
				
				s.setColor(Color.GRAY);
				rad = (float) Math.toRadians(360 * pos - 30f);
				radDown = (float) Math.toRadians(360 * pos - 180 - 30f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
				
				s.setColor(Color.DARK_GRAY);
				rad = (float) Math.toRadians(360 * pos - 60f);
				radDown = (float) Math.toRadians(360 * pos - 180 - 60f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));

				s.setColor(Color.DARK_GRAY);
				rad = (float) Math.toRadians(360 * pos - 90f);
				radDown = (float) Math.toRadians(360 * pos - 180 - 90f);
				startY -= 200;
				s.line((float) (startX + Math.cos(rad) * (size / 2f)), (float) (startY + Math.sin(rad) * (size / 2f)), (float) (startX + Math.cos(radDown) * (size / 2f)), (float) (startY + Math.sin(radDown) * (size / 2f)));
			}
			
			public void outwards()
			{
				J2hGame game = Game.getGame();
				
				long tick = game.getTick();
				
				float posLeft = tick % 120 / 120f;
				float posRight = posLeft;
				
				Gdx.gl.glLineWidth(2f);
				
				float leftStartX = Game.getGame().getWidth() / 2f + offset;
				float leftStartY = Game.getGame().getHeight() / 2f;
				float leftMaxHeight = 1000f;
				float leftModifier = 0.8f;
				
				float rightStartX = Game.getGame().getWidth() / 2f + offset;
				float rightStartY = Game.getGame().getHeight() / 2f;
				float rightMaxHeight = 1000f;
				float rightModifier = 0.8f;
				int maxX = 800;
				
				ShapeRenderer s = game.shape;
				
				// Left
				s.setColor(Color.WHITE);
				s.line(leftStartX - posLeft * maxX, leftStartY - leftMaxHeight * posLeft / 2f, leftStartX - posLeft * maxX, leftStartY + leftMaxHeight * posLeft / 2f);
				
				s.setColor(Color.GRAY);
				posLeft *= leftModifier;
				s.line(leftStartX - posLeft * maxX, leftStartY - leftMaxHeight * posLeft / 2f, leftStartX - posLeft * maxX, leftStartY + leftMaxHeight * posLeft / 2f);
				
				s.setColor(Color.DARK_GRAY);
				posLeft *= leftModifier;
				s.line(leftStartX - posLeft * maxX, leftStartY - leftMaxHeight * posLeft / 2f, leftStartX - posLeft * maxX, leftStartY + leftMaxHeight * posLeft / 2f);

				// Right
				s.setColor(Color.WHITE);
				s.line(rightStartX + posRight * maxX, rightStartY - rightMaxHeight * posRight / 2f, rightStartX + posRight * maxX, rightStartY + rightMaxHeight * posRight / 2f);
				
				s.setColor(Color.GRAY);
				posRight *= rightModifier;
				s.line(rightStartX + posRight * maxX, rightStartY - rightMaxHeight * posRight / 2f, rightStartX + posRight * maxX, rightStartY + rightMaxHeight * posRight / 2f);
				
				s.setColor(Color.DARK_GRAY);
				posRight *= rightModifier;
				s.line(rightStartX + posRight * maxX, rightStartY - rightMaxHeight * posRight / 2f, rightStartX + posRight * maxX, rightStartY + rightMaxHeight * posRight / 2f);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
	}
	
	public void setDemonWings()
	{
		activeWings = wingsDemon;
		special = specialWings;
		
		if(aura != null)
			aura.setMagicSquareEnabled(false);
	}
	
	public void setNormalWings()
	{
		special = specialWings;
		
		wingsAnimation(wingsNormal);
		
		if(aura != null)
			aura.setMagicSquareEnabled(false);
	}
	
	public void setNoWings()
	{
		activeWings = null;
		special = specialNormal;
		
		if(aura != null)
			aura.setMagicSquareEnabled(true);
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
	}
	
	Sprite activeWings;
	
	@Override
	public void onDraw()
	{
		if(activeWings == null)
			super.onDraw();
		else
		{
			J2hGame g = Game.getGame();
			
			activeWings.setPosition(getX() - activeWings.getWidth() / 2 + 5, getY() + hoverTime.getTimer() * 2.5f - activeWings.getHeight() / 2 + 22);
			activeWings.draw(g.batch);
			
			super.onDraw();
		}
	}
	
	public void wingsAnimation(final Sprite wings)
	{
		final Shinki boss = this;
		
		TouhouSounds.Enemy.NOISE.play(2f);
		
		DrawObject circle = new DrawObject()
		{
			float radius = 1000;
			float scale = 3f;
			float scaleDecrease = 0.03f;
			float alpha = 0f;
			float alphaIncrease = 0.05f;

			Color color = Color.WHITE.cpy();
			Mesh mesh;
			
			@Override
			public void onDraw()
			{
				float[] vertices = MeshUtil.makeCircleVertices(boss.getX(), boss.getY(), 60, 0, radius * scale, color);
				
				mesh = MeshUtil.makeMesh(mesh, vertices);
				
				Game.getGame().batch.end();
				
				MeshUtil.startShader();
				
//				Gdx.gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ZERO);
				
				MeshUtil.renderMesh(mesh);
				
				MeshUtil.endShader();
				
				Game.getGame().batch.begin();
			}
			
			@Override
			public void onUpdate(long tick)
			{
				super.onUpdate(tick);
				
				boss.playSpecial(true);
				
				scale -= scaleDecrease;
				
				if(scale < 0)
				{
					if(scale > 1.4f && scale < 1.5f)
						TouhouSounds.Enemy.NOISE.play(2f);
					
					Game.getGame().delete(this);
					TouhouSounds.Enemy.EXPLOSION_3.play();
					boss.activeWings = wings;
				}
				
				color.set(color.r, color.g, color.b, alpha);
				
				if(alpha < 1)
					alpha = Math.min(1, alpha += alphaIncrease);
			}
			
			@Override
			public boolean isPersistant()
			{
				return true;
			}
		};
		
		Game.getGame().spawn(circle);
	}

	@Override
	public void executeFight(final AllStarStageScheme scheme)
	{
		final J2hGame g = Game.getGame();
		final Shinki boss = this;
		
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
						
						aura = BossUtil.backgroundAura(boss, boss.getBgAuraColor());
						
						if(activeWings != null)
							aura.setMagicSquareEnabled(false);
						
						Game.getGame().startSpellCard(new ShinkiNonSpell(boss));
					}
				}, 60);
			}
		}, 5 + 20);

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
				Game.getGame().clear(ClearType.ALL);
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				final ShinkiSpell card = new ShinkiSpell(boss);
				
				Game.getGame().startSpellCard(card);

				BossUtil.spellcardCircle(boss, card, scheme.getBossAura());
				
				boss.backgroundSetter.set(scheme.getBossAura());
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				boss.setNoWings();
				
				Game.getGame().clearCircle(800f, boss, ClearType.ALL);
			}
		}, 1);
		
		scheme.waitTicks(2);
		
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
	
	public static class ShinkiNonSpell extends Spellcard
	{	
		public ShinkiNonSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(52));
		}
		
		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Shinki boss = (Shinki) getOwner();
			
			if(tick == 0)
				boss.setDamageModifier(0.5f);
			
			if(tick < 60)
				return;
			else
				tick -= 60;
			
			if(tick % 2000 < 1000 && (tick % 2000 > 80 || tick < 200))
			{
				int pTick = tick % 1000; // partial tick
				
				if(pTick % 120 == 0)
					BossUtil.moveAroundRandomly((Boss)getOwner(), (int) Math.max(boss.getX() - 200, 100), (int)Math.min(boss.getX() + 200, 1180), Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 800);
				
				if(pTick % 2 == 0)
				{
					float[] pos = { -50,0, -80,50, -110,100, 50,0, 80,50, 110,100  };
					
					for(int i = 0; i < pos.length; i += 2)
					{
						float x = pos[i];
						float y = pos[i + 1];
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ARROW, ThBulletColor.RED), boss.getX() + x, boss.getY() + y);
						bullet.setDirectionDegTick((pTick % 40 - 20) * (360 / 20f), 10f);
						bullet.setRotationFromVelocity(-90f);
						
						game.spawn(bullet);
					}
				}
				
				if(pTick % 160 < 100)
					boss.playSpecial(true);
				
				if(pTick % 160 == 0)
					for(int i = 0; i < 10; i++)
						game.addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								TouhouSounds.Enemy.BREAK_2.play(0.5f);
								
								for(int i = 2; i <= 4; i++)
								{
									float[] addAngle = { -40, -20, 0, 20, 40 };
									
									for(float angle : addAngle)
									{
										Bullet bullet = new Bullet(new ThBullet(ThBulletType.BUTTERFLY, ThBulletColor.RED), boss.getX(), boss.getY());
										bullet.setDirectionDegTick(MathUtil.getAngle(bullet, player) + angle, 2f + i * 3f);
										bullet.setGlowing();
										game.spawn(bullet);
									}
								}
							}
						}, i * 10);
			}
			else if(tick % 2000 >= 1100)
			{
				int pTick = (tick - 100) % 1000; // partial tick
				
				if(pTick == 0 && boss.activeWings != boss.wingsNormal)
					boss.setNormalWings();
				
				if(pTick % 100 == 0)
					BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 100, (int)(getGame().getMaxX() / 2) + 100, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 800);
				
				if(pTick % 20 == 0 && pTick > 100)
				{
					int[] ranges = { 10, 65, 115, 170 };
					
					for(int i = 0; i < ranges.length; i += 2)
					{
						int min = ranges[i];
						int max = ranges[i + 1];
						
						for(int degree = min; degree < max; degree += 4)
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), boss.getX(), boss.getY());
							bullet.setDirectionDegTick(degree, 10f);
							bullet.setRotationFromVelocity(-90f);
							game.spawn(bullet);
						}
					}
				}
				
				if(pTick > 100)
					boss.playSpecial(true);
				
				if(pTick % 35 == 0 && pTick > 100)
				{
					TouhouSounds.Enemy.LAZER_1.play(0.5f);
					
					CheetosLaser laser = new CheetosLaser(boss.idle.getKeyFrames()[0].getTexture(), (float) (boss.getX() + (Math.random() * 500 - 250)), boss.getY(), 15)
					{
						boolean done = false;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(!done)
							{
								float distance = MathUtil.getDistance(getX(), getY(), getGame().getPlayer().getX(), getGame().getPlayer().getY());

								float x = (getX() - getGame().getPlayer().getX()) / distance * 1.8F;
								float y = (getY() - getGame().getPlayer().getY()) / distance * 1.8F;

								float speed = 7f;
								
								x *= speed;
								y *= speed;

								setVelocityXTick(x);
								setVelocityYTick(y);
								
								if(distance < 60)
									done = true;
							}
						}
					};
					
					game.spawn(laser);
				}
			}
		}
	}
	
	private static Polygon hitbox;
	
	private static Animation getCheetosAnimation(Texture texture)
	{
		if(hitbox == null)
		{
			Polygon old = ((HitboxSprite) new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED).getAnimation().getKeyFrames()[0]).getHitbox();
			
			hitbox = new Polygon(old.getVertices());
		}
		
		Polygon poly = new Polygon(hitbox.getVertices());
		
		HitboxSprite r = new HitboxSprite(new TextureRegion(texture, 512, 128, 64, 64));
		r.setHitbox(poly);
		r.setHitboxScaleOffsetModifier(0.7f);
		
		return new Animation(1, r);
	}
	
	public static class CheetosLaser extends Bullet
	{
		int size;
		float[] lastPositions;
		ArrayList<Bullet> clones = new ArrayList<Bullet>();
		
		public CheetosLaser(Texture texture, float x, float y, int size)
		{
			super(getCheetosAnimation(texture), x, y);
			this.size = size * 2;
			this.lastPositions = new float[size * 2];
			setGlowing();
		}
		
		@Override
		public void onDelete()
		{
			super.onDelete();
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					for(Bullet clone : clones)
						Game.getGame().delete(clone);
				}
			}, 5);
		}
		
		@Override
		public int getDeleteDistance()
		{
			return size * 20;
		}
		
		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
			
			float[] newLastPositions = new float[lastPositions.length];

			for(int i = 2; i < lastPositions.length; i++)
				newLastPositions[i - 2] = lastPositions[i];

			newLastPositions[newLastPositions.length - 1] = getY();
			newLastPositions[newLastPositions.length - 2] = getX();

			lastPositions = newLastPositions;
			
			if(clones.size() < lastPositions.length)
				for(int i = 0; i < lastPositions.length; i += 2)
					if(clones.size() <= i / 2)
					{
						Bullet bullet = new Bullet(getCheetosAnimation(getAnimation().getKeyFrames()[0].getTexture()), x, y);
						bullet.getCurrentSprite().setColor(Color.YELLOW);
						clones.add(i / 2, bullet);
						Game.getGame().spawn(bullet);
					}

			for(int i = 0; i < lastPositions.length; i += 2)
			{
				float x = lastPositions[i];
				float y = lastPositions[i + 1];

				if(x == 0 && y == 0)
					continue;
				
				Bullet bullet = clones.get(i / 2);
				
				bullet.setX(x);
				bullet.setY(y);
			}
			
			if(clones.size() > lastPositions.length)
				for(int i = lastPositions.length; i < clones.size(); i++)
				{
					Bullet bullet = clones.get(i);
					Game.getGame().delete(bullet);
				}
		}
	}

	public static class ShinkiSpell extends Spellcard
	{
		public ShinkiSpell(StageObject owner)
		{
			super(owner);
			setSpellcardTime(Duration.seconds(60));
		}

		Bullet laser1;
		Bullet laser2;

		@Override
		public void tick(int tick)
		{
			final J2hGame game = Game.getGame();
			final Player player = game.getPlayer();
			final Shinki boss = (Shinki) getOwner();
			
			if(tick == 0)
			{
				BossUtil.moveTo(boss, Game.getGame().getWidth()/2f, 800, 1000);
				boss.setDamageModifier(0.4f);
			}
			
			if(tick == 50)
				BossUtil.cloudEntrance(boss, Color.MAGENTA, Color.MAGENTA, 20);
			
			if(tick > 20)
				boss.playSpecial(true);
			
			if(tick == 300 || tick == 1000)
				TouhouSounds.Enemy.HUM_2.play();
			
			if(tick > 250 && tick % 100 == 0)
			{
				float[] xPos = { 0, Game.getGame().getWidth() };
				
				for(float x : xPos)
				{
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BULLET, ThBulletColor.PURPLE), x, 0);
					bullet.setDirectionRadsTick((float) Math.toRadians(MathUtil.getAngle(bullet, player)), 2f);
					bullet.setRotationFromVelocity(-90f);
					
					game.spawn(bullet);
				}
			}
			
			if(tick == 1000)
			{
				boolean[] left = { true, false };
				
				for(final boolean bool : left)
				{
					LaserAnimation ani = new LaserAnimation(1, new ThBullet(ThBulletType.LAZER_STATIONARY, ThBulletColor.RED).getAnimation().getKeyFrames()[0].getTexture());
					
					final StationaryLaser laser = new StationaryLaser(ani, 0, 0)
					{
						long doneTick = -1;
						
						@Override
						public void onUpdate(long tick)
						{
							super.onUpdate(tick);
							
							if(getThickness() <= 20)
							{
								setThickness(getThickness() + 0.5f);
								
								setHitboxThickness(getThickness() / 2f);
								return;
							}
							
							if(doneTick == -1)
								doneTick = tick;
							
							float addRotation = ((tick - doneTick + 100 + 32) % 200 - 100) / 8f; // 32 = 4 * 8, so it starts with the right rotation to flow over into rotating.
							
							if(addRotation > 0)
								addRotation = -addRotation;
							
							addRotation += 4;
							
							setDirectionDeg(boss.getX() + (bool ? -130 : 130), boss.getY(), 270f + (bool ? addRotation : -addRotation), 1000);
						}
					};
					
					laser.setGlowing();
					laser.setZIndex(8);
					laser.setDirectionDeg(boss.getX() + (bool ? -130 : 130), boss.getY(), 270f, 1000);
					laser.setPosition(boss);
					
					game.spawn(laser);
				}
			}
			
			if(tick == 140)
			{
				TouhouSounds.Enemy.EXPLOSION_3.play();
				boss.setDemonWings();
				
				for(int i = 0; i < 30; i++)
				{
					Bullet ball = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, ThBulletColor.PURPLE), (float) (boss.getX() + (Math.random() * 100 - 50)), (float) (boss.getY() + Math.random() * 100));
					ball.setZIndex(ball.getZIndex() + 10);
					
					ball.setVelocityXTick((float) (Math.random() * 20 - 10f));
					ball.setVelocityYTick((float) (4f + Math.random() * 8f));
					ball.useSpawnAnimation(false);
					
					game.spawn(ball);
				}
			}
			
			if(tick > 200 && tick % 30 == 0)
			{
				Bullet ball = new Bullet(new ThBullet(ThBulletType.ORB_LARGE, ThBulletColor.RED), (float) (boss.getX() + (Math.random() * 500 - 250)), (float) (boss.getY() + Math.random() * 100));
				ball.setZIndex(ball.getZIndex() + 10);
				
				ball.setDirectionRadsTick((float) Math.toRadians(MathUtil.getAngle(ball, player)), 15f);

				ball.useSpawnAnimation(false);
				
				game.spawn(ball);
			}
			
			if(tick % 10 == 0)
				TouhouSounds.Enemy.BREAK_1.play(0.3f);
			
			if(tick % 3 == 0)
			{
				float addRotation = ((tick - 40 + 50) % 100 - 50) / 3f;

				if(tick < 140)
					addRotation = 140 - tick;

				if(addRotation > 0)
					addRotation = -addRotation;

				int[] positions = { -300, -100, 100, 300 };

				for(int xAdd : positions)
				{
					float[] rot = tick < 140 ? new float[]{ 90 } : new float[]{ 90, 20, 160 };

					for(float f : rot)
					{
						Bullet knife = new Bullet(new ThBullet(ThBulletType.KNIFE, ThBulletColor.RED), boss.getX() + xAdd, boss.getY() + 50);

						knife.setDirectionRadsTick((float) Math.toRadians(f + (xAdd < 0 ? addRotation : -addRotation)), 20f);
						knife.setRotationFromVelocity(-90);
						knife.setZIndex(boss.getZIndex() - 3);
						knife.setGlowing();
						
						Bullet spawner = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.RED), boss.getX() + xAdd, boss.getY() + 50)
						{
							@Override
							public void onUpdate(long tick)
							{
								super.onUpdate(tick);

								HitboxSprite sprite = getCurrentSprite();

								sprite.setScale(sprite.getScaleX() * 0.9f);

								if(getTicksAlive() > 15)
									game.delete(this);
							}
						};

						spawner.setZIndex(boss.getZIndex() - 2);
						spawner.getCurrentSprite().setScale(3f);
						spawner.setVelocityXTick(knife.getVelocityXTick());
						spawner.setVelocityYTick(knife.getVelocityYTick());
						spawner.useSpawnAnimation(false);
						spawner.useDeathAnimation(false);
						game.spawn(spawner);

						game.spawn(knife);
					}

					for(int i = 0; i < 2; i++)
					{
						Bullet small = new Bullet(new ThBullet(ThBulletType.DOT_SMALL_MOON, ThBulletColor.RED), boss.getX() + xAdd, boss.getY());
						small.setVelocityXTick((float) (Math.random() * 20 - 10f));

						small.setVelocityYTick((float) -(1f + Math.random() * 2f));
						small.setGlowing();
						small.setZIndex(small.getZIndex() - 10);

						game.spawn(small);
					}
				}
			}
		}
	}
}

