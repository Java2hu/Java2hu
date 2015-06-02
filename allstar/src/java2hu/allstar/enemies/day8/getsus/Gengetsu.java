package java2hu.allstar.enemies.day8.getsus;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Gengetsu extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Gengetsu";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	public static Gengetsu newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 200;
		int chunkWidth = 500;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation handDown = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 3);
		Animation handLeft = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 1);
		Animation handUp = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 2);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(95f);
		bgm.setLooping(true);
		
		final Gengetsu boss = new Gengetsu(100, nameTag, bg, fbs, handDown, handLeft, handUp, bgm, x, y);
		
		return boss;
	}
	
	public Mugetsu sister;
	
	public Animation handDown;
	public Animation handLeft;
	public Animation handUp;
	
	public Sprite bg;
	
	public Gengetsu(float maxHealth, TextureRegion nametag, final Sprite bg, Sprite fullBodySprite, Animation handDown, Animation handLeft, Animation handUp, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, handDown, null, null, null, bgm, x, y);
		
		userBuffer = new FrameBuffer(Format.RGBA8888, 500, 200, false);
				
		Polygon hitbox = HitboxUtil.rectangleHitbox(100);
		Rectangle bounds = hitbox.getBoundingRectangle();
		hitbox.setOrigin(bounds.getWidth() / 2f, bounds.getHeight() / 2f);
		this.setHitbox(hitbox);
		
		addDisposable(userBuffer);
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(handDown);
		addDisposable(handLeft);
		addDisposable(handUp);
		addDisposable(bg);
		
		this.handDown = handDown;
		this.handLeft = handLeft;
		this.handUp = handUp;
		
		setAuraColor(Color.PINK);
		
		final Gengetsu boss = this;
		this.bg = bg;
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
	}
	
	float lastTickHealthSister = -1f;
	float lastTickHealth = -1f;
	
	public void setHandDown()
	{
		this.idle = handDown;
	}
	
	public void setHandLeft()
	{
		this.idle = handLeft;
	}
	
	public void setHandUp()
	{
		this.idle = handUp;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		if(sister != null && !isHealing() && !sister.isHealing())
		{
			if(lastTickHealthSister == -1f)
			{
				lastTickHealth = getHealth();
				lastTickHealthSister = sister.getHealth();
				return;
			}
			
			// She took damage or healed.
			if(lastTickHealthSister - sister.getHealth() != 0)
				setHealth(sister.getHealth());
			else if(lastTickHealth - getHealth() != 0)
				sister.setHealth(getHealth());
			
			lastTickHealthSister = sister.getHealth();
			lastTickHealth = getHealth();
		}
		
		if(lastX != x || lastY != y)
		{
			if(!warp)
				warpStartTime = game.getElapsedTime();
			
			warp = true;
		}
	}
	
	public boolean warp = false;
	float warpStartTime = 0f;
	
	private FrameBuffer userBuffer;
	
	@Override
	public void onDraw()
	{
		if(warp)
		{
			game.batch.flush();
			
			Color before = game.batch.getColor();
			
			game.batch.setColor(Color.WHITE);

			userBuffer.begin();
			
			HitboxSprite cur = getCurrentSprite();
			
			game.batch.draw(cur, 0, 0, game.getWidth(), game.getHeight());
			
			game.batch.flush();
			
			userBuffer.end();
			
			game.batch.flush();

			ShaderProgram prog = ShaderLibrary.WARP.getProgram();

			game.batch.setShader(prog);
			
			float deltaT = (game.getElapsedTime() - warpStartTime) / 3f;

			System.out.println(deltaT);
			
			prog.setUniformf("time", deltaT * 2f);

			if(deltaT > 0.5f)
				warp = false;
			
			game.batch.setShader(prog);

			game.camera.applyAspectRatio(false);

			Texture t = userBuffer.getColorBufferTexture();

			game.batch.draw(t, getDrawX() - cur.getWidth() / 2f, getDrawY() + cur.getHeight() / 2f, cur.getWidth(), -cur.getHeight());
		
			game.batch.setColor(before);
			game.batch.setShader(null);
		}
		else
		{
			super.onDraw();
		}
	}
	
	@Override
	public float getDamageModifier()
	{
		return super.getDamageModifier();
	}

	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		// Use GetsusGeneral.executeFight(...);
	}
}

