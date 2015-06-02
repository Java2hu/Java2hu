package java2hu.allstar.enemies.day9.yumemi;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.Loader;
import java2hu.Position;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.BackgroundBossAura;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.DrawObject;
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
import java2hu.util.Converter;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.SchemeUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yumemi extends AllStarBoss
{
	public final static String FULL_NAME = "Yumemi Okazaki";
	public final static String DATA_NAME = "yumemi";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	private Setter<BackgroundBossAura> backgroundSpawner;
	
	public Yumemi(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, 132, 0, 226, 220, 1F, 1);
		
		for(TextureRegion r : idle.getKeyFrames())
		{
			((HitboxSprite)r).setScale(0.8f);
		}
		
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation special = ImageSplitter.getAnimationFromSprite(sprite, 354, 0, 226, 220, 1F, 1);
	
		for(TextureRegion r : special.getKeyFrames())
		{
			((HitboxSprite)r).setScale(0.8f);
		}
		
		Music bgm = new J2hMusic(Gdx.audio.newMusic(FOLDER.child("bgm.mp3")));
		
		setAuraColor(new Color(137 / 255f, 0 / 255f, 0 / 255f, 1.0f));
		
		set(nameTag, bgm);
		set(fbs, idle, idle, idle, special);
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
		final Yumemi boss = this;
		
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
						BossUtil.backgroundAura(boss, boss.getBgAuraColor());
						
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
				
				AllStarUtil.presentSpellCard(boss, SPELLCARD_NAME);
				
				Game.getGame().startSpellCard(new Spell(boss));
			}
		}, 1);
		
		SchemeUtil.waitForDeath(scheme, boss);
		
		scheme.doWait();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(boss);
				
				Game.getGame().clear(ClearType.ALL);
				
				BossUtil.mapleExplosion(boss.getX(), boss.getY());
			}
		}, 1);
		
		scheme.waitTicks(5); // Prevent concurrency issues.
	}
	
	public static class NonSpell extends BossSpellcard<Yumemi>
	{	
		public NonSpell(Yumemi owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, Yumemi boss)
		{
			final Player player = game.getPlayer();
			
		}
	}

	public static class Spell extends BossSpellcard<Yumemi>
	{
		private ArrayList<Position> lastPositions = new ArrayList<Position>();
		
		public Spell(Yumemi owner)
		{
			super(owner);
		}

		@Override
		public void tick(int tick, J2hGame game, final Yumemi boss)
		{
			final Player player = game.getPlayer();
			
			int total = 2000;
			
			if(tick % total < 900 && tick % 5 == 0)
			{
				for(int i = 0; i < 5; i++)
				{
					Bullet bullet = new Bullet(new ThBullet(ThBulletType.BUTTERFLY, ThBulletColor.RED), boss.getX(), boss.getY());

					bullet.setDirectionDeg((float) (MathUtil.getAngle(bullet, player) + (Math.random() * 80 - 40)), (float) (300f + 300f * Math.random()));

					game.spawn(bullet);
				}
			}
			
			if(tick % 10 == 0)
			{
				Position position = new Position(player);
				
				lastPositions.add(position);
			}
			
			int crossDelta = 200;
			
			if(tick % crossDelta == 0 && !lastPositions.isEmpty())
			{
				final Position start = lastPositions.get(0);
				
				lastPositions.remove(0);
				
				DrawObject obj = new DrawObject()
				{
					int index = 0;
					
					private ArrayList<Position> positions = new ArrayList<Position>(lastPositions);
					private Position nextPos = null;
					private double nextAngle = 0;
					
					private Position pos = start;
					
					@Override
					public void onDraw()
					{
						game.batch.end();
						
						setZIndex(boss.getZIndex() - 2);
						
						game.shape.begin(ShapeType.Line);
						game.shape.setColor(Color.WHITE);
						
						float width = 2 + getTicksAlive() / 20f * 2;
						
						Gdx.gl.glLineWidth(width);
						game.shape.line(boss.getX(), boss.getY(), pos.getX(), pos.getY());
						
						game.shape.end();
						
						game.batch.begin();
					}
					
					@Override
					public void onUpdate(long tick)
					{
						double angle = Math.toRadians(MathUtil.getAngle(nextPos, pos));
						
						double difference = MathUtil.getDifference(angle, nextAngle);
						
						if(nextPos == null || difference > 0.1d || Double.isNaN(angle))
						{
							if(positions.isEmpty())
							{
								game.delete(this);
								return;
							}
							
							nextPos = positions.get(0);
							
							positions.remove(0);
							
							nextAngle = Math.toRadians(MathUtil.getAngle(nextPos, pos));
							angle = nextAngle;
						}
						
						float speed = 10;
						
						pos.add(new Position(speed * Math.cos(angle), speed * Math.sin(angle)));
						
						if(tick % 30 == 0)
							spawnCross(pos);
					}
				};
				
				lastPositions.clear();
				
				game.spawn(obj);
			}
		}
	}
	
	private static Texture cross = null;
	
	public static void spawnCross(final IPosition pos)
	{
		if(cross == null)
		{
			cross = Loader.texture(FOLDER.child("anm.png"));
		}
		
		final HitboxSprite sc = new HitboxSprite(new TextureRegion(cross, 64, 0, 64, 64));
		
		sc.setOriginCenter();
		
		final HitboxSprite sv = new HitboxSprite(new TextureRegion(cross, 32, 64, 64, 1));
		
		sv.setOriginCenter();
		
		final HitboxSprite sh = new HitboxSprite(new TextureRegion(cross, 0, 64, 1, 64));
		
		sh.setOriginCenter();
		
		final HitboxSprite endHor = new HitboxSprite(new TextureRegion(cross, 0, 0, 64, 12));
		
		endHor.setOriginCenter();
		
		final HitboxSprite endVer = new HitboxSprite(new TextureRegion(cross, 0, 0, 12, 64));
		
		endVer.setOriginCenter();
		
		DrawObject obj = new DrawObject()
		{
			IPosition center = new Position(pos);
			
			final int deleteTime = 160;
			final int deletePhase = 40;
			
			@Override
			public void onDraw()
			{
				cross.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				
				Converter<Long, Float> cMul = new Converter<Long, Float>()
				{
					@Override
					public Float convert(Long t1)
					{
						return (float) (t1 / 60f * 0.05d);
					}
				};
			
				int wait = 100;
				
				float mul = getTicksAlive() < wait ? cMul.convert(getTicksAlive()) : (getTicksAlive() - wait) / 4f + cMul.convert((long) wait);
				
				float botMul = Math.min(1, mul / 2f);
				
				if(delete)
				{
					long ticksNew = getTicksAlive() - deleteTime;
					mul = 1 - ticksNew / (float)deletePhase;
					botMul = mul;
				}
				
				mul = Math.min(1, mul);
				
				int topSize = (int) (80 * mul);
				int leftSize = (int) (80 * mul);
				int rightSize = (int) (80 * mul);
				int botSize = 10 + (int) (150 * botMul);
				
				float alpha = 1f;
				float scale = 1.20f;
				
				sc.setPosition(center.getX(), center.getY());
				sc.setAlpha(alpha);
				sc.setScale(scale);

				sc.draw(game.batch);
				
				float iMul = 1f;
				
				float height = sv.getHeight() * scale;
				
				for(int i = 0; i <= topSize; i++)
				{
					float x = sc.getX();
					
					float yOffset = (sc.getHeight() * scale) - 32 * (scale - 1f);
					
					float y = sc.getY() + yOffset + (height * iMul * i);
					
					if(i == topSize)
					{
						endHor.setPosition(x, y);
						endHor.setRotation(0);
						
						endHor.setScale(scale);
						endHor.setAlpha(alpha);
						
						endHor.draw(game.batch);
						continue;
					}
					
					sv.setPosition(x, y);
					
					sv.setAlpha(alpha);
					sv.setScale(scale);
					
					sv.draw(game.batch);
				}
				
				for(int i = 0; i <= botSize; i++)
				{
					float x = sc.getX();
					
					float yOffset = 28 * (scale - 1f);
					
					float y = sc.getY() - yOffset - height * iMul * i;
					
					if(i == botSize)
					{
						endHor.setPosition(x, y);
						endHor.setRotation(180);
						endHor.setAlpha(alpha);
						endHor.setScale(scale);
						endHor.draw(game.batch);
						continue;
					}
					
					sv.setAlpha(alpha);
					sv.setPosition(x, y);
					sv.setScale(scale);
					sv.draw(game.batch);
				}
				
				for(int i = 0; i <= leftSize; i++)
				{
					float xOffset = 32 * (scale - 1f);
					
					boolean isEnd = i == leftSize;
					
					float x = sc.getX() - xOffset - (sh.getWidth() * scale * iMul * (isEnd ? i - 1 : i));
					float y = sc.getY();
					
					if(isEnd)
					{
						endVer.setPosition(x - ((endVer.getWidth() * scale) * iMul) + 15 * (scale - 1f), y);
						
						endVer.setRotation(0);
						endVer.setOriginCenter();
						endVer.setScale(scale);
						endVer.setAlpha(alpha);
						
						endVer.draw(game.batch);
						continue;
					}
					
					sh.setAlpha(alpha);
					sh.setPosition(x, y);
					sh.setScale(scale);
					sh.draw(game.batch);
				}
				
				for(int i = 0; i <= rightSize; i++)
				{
					boolean isEnd = i == rightSize;
					
					float xOffset = 32 * (scale - 1f);
					
					float x = sc.getX() + sc.getWidth() + xOffset + (sh.getWidth() * scale * iMul * (isEnd ? i - 1 : i));
					float y = sc.getY();
					
					if(isEnd)
					{
						endVer.setPosition(x, y);
						
						endVer.setRotation(180);
						endVer.setOriginCenter();
						endVer.setScale(scale);
						endVer.setAlpha(alpha);
						
						endVer.draw(game.batch);
						continue;
					}
					
					sh.setAlpha(alpha);
					sh.setPosition(x, y);
					sh.setScale(scale);
					sh.draw(game.batch);
				}
			}
			
			private boolean delete = false;
			private long lastTicksAlive = 0;
			
			@Override
			public void onUpdate(long tick)
			{
				super.onUpdate(tick);
				
				if(getTicksAlive() > deleteTime + deletePhase)
				{
					game.delete(this);
				}
				else if(getTicksAlive() > deleteTime)
				{
					delete = true;
				}
			}
		};
		
		obj.setName("cross");
		obj.setZIndex(100);
		
		game.spawn(obj);
	}
}

