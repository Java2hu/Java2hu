package java2hu.allstar.enemies.day7.tsukumo;

import java2hu.Game;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.background.BackgroundBossAura;
import java2hu.background.ScrollingBackground;
import java2hu.object.DrawObject;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;

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

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yatsuhashi extends AllStarBoss
{
	public final static String FULL_NAME = "Yatsuhashi Tsukumo";
	public final static String DATA_NAME = "yatsuhashi";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	public Yatsuhashi(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		setAuraColor(new Color(170f/255f, 160f/255f, 201f/255f, 1f));
		
		int chunkHeight = 194;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs2.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4,5,6,7,8);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation leftStartup = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 17,18,19);
		Animation leftLoop = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 20,21,22,23);
		
		Animation rightStartup = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 9,10,11);
		Animation rightLoop = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 12,13,14,15);

		MovementAnimation left = new MovementAnimation(leftStartup, leftLoop, 5f);
		MovementAnimation right = new MovementAnimation(rightStartup, rightLoop, 5f);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4,5,6,7,8);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = Gdx.audio.newMusic(FOLDER.child("bgm.mp3"));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
	}
	
	@Override
	public float getDrawX()
	{
		return getX() + 20;
	}
	
	public void spawnBackground(final BackgroundBossAura aura)
	{
		final Sprite bg = new Sprite(Loader.texture(FOLDER.child("bg.png")));
		final Sprite bge = new Sprite(Loader.texture(FOLDER.child("bge.png")));
		
		addDisposable(bg);
		addDisposable(bge);
		
		final Yatsuhashi boss = this;
		
		Game.getGame().spawn(new DrawObject()
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
				, 0, 1f, 0.01F));
				
				bg.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
				setFrameBuffer(aura.getBackgroundBuffer());
				
				setZIndex(-7);
			}
			
			@Override
			public void onDraw()
			{
				bg.draw(Game.getGame().batch);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
		
		Game.getGame().spawn(new ScrollingBackground(bge, -0.2f, 0.2f)
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
				, 0, 1f, 0.01F));
				setFrameBuffer(aura.getBackgroundBuffer());
				
				setZIndex(-6);
			}
			
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
			
			@Override
			public void onDraw()
			{
				Game.getGame().batch.enableBlending();
				Game.getGame().batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR);
				
				super.onDraw();
				
				Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				if(getTicksAlive() < 30)
					return;
				
				super.onUpdate(tick);
			}
		});
		
		Game.getGame().spawn(new ScrollingBackground(bge, 0.2f, 0.2f)
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
				, 0, 1f, 0.01F));
				setFrameBuffer(aura.getBackgroundBuffer());
				
				setZIndex(-5);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
			
			@Override
			public void onDraw()
			{
				Game.getGame().batch.enableBlending();
				Game.getGame().batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR);
				
				super.onDraw();
				
				Game.getGame().batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
	}

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		// Use TsukumoGeneral
	}
}

