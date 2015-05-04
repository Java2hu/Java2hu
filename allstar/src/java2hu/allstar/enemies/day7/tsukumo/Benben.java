package java2hu.allstar.enemies.day7.tsukumo;

import java2hu.Game;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
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
public class Benben extends AllStarBoss
{
	public final static String FULL_NAME = "Benben Tsukumo";
	public final static String DATA_NAME = "benben";
	public final static FileHandle FOLDER = Gdx.files.internal("enemy/" + DATA_NAME + "/");
	
	public Benben(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
		
		setColor(new Color(97f/255f, 66f/255f, 50f/255f, 1f));
		
		int chunkHeight = 168;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(FOLDER.child("anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(FOLDER.child("fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(FOLDER.child("nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation leftStartup = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 17,18);
		Animation leftLoop = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 19,20,21);
		
		Animation rightStartup = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 9,10,11);
		Animation rightLoop = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 5F, 12,13);

		StartupLoopAnimation left = new StartupLoopAnimation(leftStartup, leftLoop, 5f);
		StartupLoopAnimation right = new StartupLoopAnimation(rightStartup, rightLoop, 5f);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 5,6,7,8);
		special.setPlayMode(PlayMode.NORMAL);
	
		Music bgm = null;//Gdx.audio.newMusic(Yatsuhashi.FOLDER.child("bgm.mp3"));
		
		set(nameTag, bgm);
		set(fbs, idle, left, right, special);
	}
	
	public void spawnBackground()
	{
		final Sprite bg = new Sprite(Loader.texture(FOLDER.child("bg.png")));
		final Sprite bge = new Sprite(Loader.texture(FOLDER.child("bge.png")));
		
		addDisposable(bg);
		addDisposable(bge);
		
		final Benben boss = this;
		
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
				setZIndex(-3);
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
				
				setZIndex(-2);
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
				setZIndex(-1);
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

