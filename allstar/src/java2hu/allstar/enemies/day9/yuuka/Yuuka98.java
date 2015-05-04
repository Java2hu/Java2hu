package java2hu.allstar.enemies.day9.yuuka;


import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.Position;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.background.BackgroundBossAura;
import java2hu.object.DrawObject;
import java2hu.object.bullet.Bullet;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.ImageSplitter;
import java2hu.util.ImageUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yuuka98 extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Yuuka";
	
	public static Yuuka98 newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 176;
		int chunkWidth = 176;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm98.png"));
		sprite.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation closeUmbrella = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10f, 1,2,3);
		
		Animation openUmbrella = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10f, 3,2,1);
		
		Animation windup = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 6f, 7,8);
		
		Animation swirl = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 6f, 13,14,15,16,17,18,19,20,21,22,23,24);

		Animation downOpenUmbrella = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 7,8,9);
		
		Animation forwardsOpenUmbrella = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 25);
		
		Animation invis = null; // To be made.
		
		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm98.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(16.5f);
		bgm.setLooping(true);
		
		final Yuuka98 boss = new Yuuka98(100, fbs, idle, closeUmbrella, openUmbrella, windup, swirl, downOpenUmbrella, forwardsOpenUmbrella, invis, bgm, x, y);
		
		return boss;
	}
	
	private Animation activeStance; // What she returns to after finishing the active animation. ie. idle/umbrella open/etc
	private Animation activeAnimation;
	private int activeAnimationTimer = 0;
	
	private Animation closeUmbrella;
	private Animation openUmbrella;
	private Animation windup;
	private Animation swirl;
	private Animation downOpenUmbrella;
	private Animation forwardsOpenUmbrella;
	private Animation invis;
	
	public Yuuka98(float maxHealth, Sprite fbs, Animation idle, Animation closeUmbrella, Animation openUmbrella, Animation windup, Animation swirl, Animation downOpenUmbrella, Animation forwardsOpenUmbrella, Animation invis, Music bgm, float x, float y)
	{
		super(maxHealth, null, fbs, idle, null, null, null, bgm, x, y);
		
		activeAnimation = null;
		activeStance = idle;
		
		this.closeUmbrella = closeUmbrella;
		this.openUmbrella = openUmbrella;
		this.windup = windup;
		this.swirl = swirl;
		this.downOpenUmbrella = downOpenUmbrella;
		this.forwardsOpenUmbrella = forwardsOpenUmbrella;
		this.invis = invis;
		
		addDisposable(idle);
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
	}
	
	public void spawnBackground(final BackgroundBossAura aura)
	{
		final Yuuka98 boss = this;

		final Color[] colors = { Color.BLUE.cpy().sub(0f, 0f, 0.2f, 0f), Color.MAGENTA.cpy().sub(0.2f, 0f, 0.2f, 0f), Color.GREEN.cpy().sub(0f, 0.2f, 0f, 0f) };

		Game.getGame().spawn(new DrawObject()
		{
			Texture[] textures = null;
			Sprite[] textureSprites = null;

			{
				textures = new Texture[colors.length];
				textureSprites = new Sprite[colors.length];

				int i = 0;

				for(Color color : colors)
				{
					textures[i] = ImageUtil.makeDummyTexture(color, 1, 1);
					textureSprites[i] = new Sprite(textures[i]);
					i++;
				}
				
				setZIndex(-101);
				setFrameBuffer(aura.getBackgroundBuffer());
			}
			
			Color lastColor = Color.BLACK;
			float alpha = 0f;

			@Override
			public void onDraw()
			{
				float changeTime = 800;

				int index = (int) (Game.getGame().getTick() / changeTime % textureSprites.length);
				Sprite selected = textureSprites[index];
				lastColor = colors[index];
				
				alpha = 2 * (Game.getGame().getTick() / changeTime % 1);

				if(alpha > 1)
					alpha = 2 - alpha;

				int xMax = 14;
				int yMax = 10;

				float width = Game.getGame().getWidth() / xMax;
				float height = Game.getGame().getHeight() / yMax;

				float speed = 3f;

				float offset = Game.getGame().getTick() * speed % (2 * height);

				selected.setSize(width, height);

				for(int x = 0; x < xMax; x++)
					for(int y = -2; y < yMax; y++)
					{
						float xPos = width * x;
						float yPos = height * y;

						boolean color = false;

						int y2 = y;

						if(y < 0)
							y2 = (int) (y + 4 * height); // So the math below doesn't screw up because y2 is under 0

						if(x % 2 == 0)
							color = y2 % 2 == 0;
						else
							color = y2 % 2 == 1;
						
						if(!color)
							continue;

						Sprite activeSprite = selected;
						activeSprite.setAlpha(alpha);

						activeSprite.setPosition(xPos, yPos + offset);
						activeSprite.draw(Game.getGame().batch);
					}
			}
			
			@Override
			public void onUpdate(long tick)
			{
				super.onUpdate(tick);
				
				if(getTicksAlive() == 5)
					for(int i = 0; i < 360; i += 3 + 3 * Math.random())
					{
						final int finalI = i;
						
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.STAR, ThBulletColor.WHITE), Game.getGame().getWidth()/2f, Game.getGame().getHeight()/2f)
						{							
							double offset = 330;
							double centerX = Game.getGame().getWidth()/2f + Math.cos(Math.toRadians(finalI)) * offset;
							double centerY = Game.getGame().getHeight()/2f + Math.sin(Math.toRadians(finalI)) * offset;
							double timer = 0;
							double timerIncrease = 1f + 1 * Math.random();
							int size = (int) offset;// + (200 * Math.random()));
							
							{
								setFrameBuffer(aura.getBackgroundBuffer());
								timer = finalI + 180f + (90 * Math.random() - 45);
							}
							
							@Override
							public void onUpdate(long tick)
							{
								timerIncrease = 0.6f;
								
								setX((float) (centerX + Math.cos(Math.toRadians(timer)) * size));
								setY((float) (centerY + Math.sin(Math.toRadians(timer)) * size));
								
								timer += timerIncrease;
								
								getCurrentSprite().setColor(lastColor);
								getCurrentSprite().setAlpha(0.6f * alpha);
							};
							
							@Override
							public void checkCollision()
							{
								// Nothing, vanity bullet.
							};
						};
						
						bullet.setZIndex(-100);
						bullet.getCurrentSprite().setColor(lastColor);
						bullet.getCurrentSprite().setScale(2f);
						bullet.getCurrentSprite().setAlpha(0.2f * alpha);
						
						Game.getGame().spawn(bullet);
					}
			}

			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
	}
	
	public Position getUmbrellaTip()
	{
		Position p = new Position(getX(), getY());
	
		if(activeAnimation == swirl)
		{
			int frame = activeAnimation.getKeyFrameIndex(activeAnimationTimer);
			
			if(frame == 0)
			{
				p.setX(p.getX() - 70);
				p.setY(p.getY() - 70);
			}
			else if(frame == 1)
			{
				p.setX(p.getX() - 30);
				p.setY(p.getY() - 75);
			}
			else if(frame == 2)
			{
				p.setX(p.getX() + 25);
				p.setY(p.getY() - 50);
			}
			else if(frame == 3)
			{
				p.setX(p.getX() + 55);
				p.setY(p.getY() - 20);
			}
			else if(frame == 4)
			{
				p.setX(p.getX() + 35);
				p.setY(p.getY() + 10);
			}
			else if(frame == 5)
			{
				p.setX(p.getX() + 0);
				p.setY(p.getY() + 25);
			}
			else if(frame == 6)
			{
				p.setX(p.getX() - 40);
				p.setY(p.getY() + 45);
			}
			else if(frame == 7)
			{
				p.setX(p.getX() - 30);
				p.setY(p.getY() + 65);
			}
			else if(frame > 7)
			{
				p.setX(p.getX() - 0);
				p.setY(p.getY() + 75);
			}
		}
		else if(activeStance == windup)
		{
			p.setX(p.getX() - 70);
			p.setY(p.getY() - 70);
		}
		else
		{
			p.setX(p.getX() - 0);
			p.setY(p.getY() + 75);
		}
		
		
		
		return p;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
			
		if(activeAnimation != null)
		{
			activeAnimationTimer++;
			
			if(activeAnimation.isAnimationFinished(activeAnimationTimer))
			{
				activeAnimation = null;
				activeAnimationTimer = 0;
			}
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1))
			closeUmbrella();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_2))
			openUmbrella();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_3))
			windUp();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_4))
			swirl();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_5))
			downOpenUmbrella();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_6))
			forwardsOpenUmbrella();
		
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_7))
			invis();
	}
	
	public void closeUmbrella()
	{
		activeAnimation = closeUmbrella;
		activeStance = closeUmbrella;
		activeAnimationTimer = 0;
	}
	
	public void openUmbrella()
	{
		activeAnimation = openUmbrella;
		activeStance = idle;
		activeAnimationTimer = 0;
	}
	
	public void windUp()
	{
		activeAnimation = windup;
		activeStance = windup;
		activeAnimationTimer = 0;
	}
	
	public void swirl()
	{
		activeAnimation = swirl;
		activeStance = closeUmbrella;
		activeAnimationTimer = 0;
	}
	
	public void downOpenUmbrella()
	{
		if(activeStance == downOpenUmbrella)
			return;
		
		activeAnimation = activeStance == windup ? null : downOpenUmbrella;
		activeStance = downOpenUmbrella;
	}
	
	public void forwardsOpenUmbrella()
	{
		activeAnimation = null;
		activeStance = forwardsOpenUmbrella;
		activeAnimationTimer = 0;
	}
	
	public void invis()
	{
		activeAnimation = invis;
		activeStance = idle;
		activeAnimationTimer = 0;
	}
	
	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		Animation toDraw = activeStance;
		int timer = 0;

		if(activeAnimation != null)
		{
			toDraw = activeAnimation;
			timer = activeAnimationTimer;
		}
		else
			timer = (int) (toDraw.getFrameDuration() * toDraw.getKeyFrames().length);

		HitboxSprite current = AnimationUtil.getCurrentSprite(toDraw, timer);
		current.setPosition(getDrawX() - getWidth() / 2, getDrawY() - getHeight() / 2);
		current.draw(g.batch);
	}

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		// Use YuukaGeneral.executeFight(AllStarStageScheme scheme);
	}
}

