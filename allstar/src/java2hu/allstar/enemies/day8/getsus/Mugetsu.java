package java2hu.allstar.enemies.day8.getsus;

import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.util.ImageSplitter;

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
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Mugetsu extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Mugetsu";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	public static Mugetsu newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 200;
		int chunkWidth = 136;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 1);
		
		Animation left = new StartupLoopAnimation(ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 24F, 2,3,4,5,6), ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 24F, 7), 10f);
		Animation right = left;
		
		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 2);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		
		final Mugetsu boss = new Mugetsu(100, nameTag, bg, fbs, idle, left, right, special, null, x, y);
		
		return boss;
	}
	
	public Sprite bg;
	
	public Mugetsu(float maxHealth, TextureRegion nametag, final Sprite bg, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		
		setAuraColor(Color.BLUE);
		
		final Mugetsu boss = this;
		this.bg = bg;
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
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

