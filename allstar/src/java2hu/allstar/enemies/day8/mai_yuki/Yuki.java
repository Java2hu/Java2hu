package java2hu.allstar.enemies.day8.mai_yuki;

import java2hu.Game;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.util.ImageSplitter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yuki extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Yuki";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	public static Yuki newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 120;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 3);
		Animation right = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 4);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 2);
		special.setPlayMode(PlayMode.NORMAL);
		
		// Glowing
		
		Animation glowIdle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 5);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation glowLeft = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 7);
		Animation glowRight = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 8);

		Animation glowSpecial = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 6);
		special.setPlayMode(PlayMode.NORMAL);
		
		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Yuki boss = new Yuki(100, nameTag, fbs, idle, left, right, special, glowIdle, glowLeft, glowRight, glowSpecial, bgm, x, y);
		
		return boss;
	}
	
	private Animation normalIdle;
	private Animation normalLeft;
	private Animation normalRight;
	private Animation normalSpecial;
	
	private Animation glowIdle;
	private Animation glowLeft;
	private Animation glowRight;
	private Animation glowSpecial;
	
	public Yuki(float maxHealth, TextureRegion nametag, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Animation glowIdle, Animation glowLeft, Animation glowRight, Animation glowSpecial, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		
		this.normalIdle = idle;
		this.normalLeft = left;
		this.normalRight = right;
		this.normalSpecial = special;
		
		this.glowIdle = glowIdle;
		this.glowLeft = glowLeft;
		this.glowRight = glowRight;
		this.glowSpecial = glowSpecial;
		
		final Yuki boss = this;
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
	}
	
	public void setGlowingAura()
	{
		this.idle = glowIdle;
		this.left = glowLeft;
		this.right = glowRight;
		this.special = glowSpecial;
	}
	
	public void setNoAura()
	{
		this.idle = normalIdle;
		this.left = normalLeft;
		this.right = normalRight;
		this.special = normalSpecial;
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
		// Use MaiYukiGeneral.executeFight(...);
	}
}

