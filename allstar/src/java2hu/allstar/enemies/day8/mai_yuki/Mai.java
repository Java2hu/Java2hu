package java2hu.allstar.enemies.day8.mai_yuki;

import java2hu.Game;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.util.AnimationUtil;
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
public class Mai extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Mai";
	
	/**
	 * Spell Card Name
	 */
	final static String SPELLCARD_NAME = "";
	
	public static Mai newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 130;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 2);
		Animation right = AnimationUtil.copyAnimation(left);
		
		// Flip left
		for(TextureRegion reg : left.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 1F, 3);
		special.setPlayMode(PlayMode.NORMAL);
		
		// Load alternative set (open wings)
		
		Animation devilIdle = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 1F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation devilLeft = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 1F, 2);
		Animation devilRight = AnimationUtil.copyAnimation(devilLeft);

		for(TextureRegion reg : devilLeft.getKeyFrames())
			reg.flip(true, false);

		Animation devilSpecial = ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 1F, 3);
		special.setPlayMode(PlayMode.NORMAL);

		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final Mai boss = new Mai(100, nameTag, fbs, idle, left, right, special, devilIdle, devilLeft, devilRight, devilSpecial, bgm, x, y);
		
		return boss;
	}
	
	private Animation normalIdle;
	private Animation normalLeft;
	private Animation normalRight;
	private Animation normalSpecial;
	
	private Animation devilIdle;
	private Animation devilLeft;
	private Animation devilRight;
	private Animation devilSpecial;
	
	public Mai(float maxHealth, TextureRegion nametag, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Animation devilIdle, Animation devilLeft, Animation devilRight, Animation devilSpecial, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		
		addDisposable(devilIdle);
		addDisposable(devilLeft);
		addDisposable(devilRight);
		addDisposable(devilSpecial);
		
		final Mai boss = this;
		
		this.normalIdle = idle;
		this.normalLeft = left;
		this.normalRight = right;
		this.normalSpecial = special;
		
		this.devilIdle = devilIdle;
		this.devilLeft = devilLeft;
		this.devilRight = devilRight;
		this.devilSpecial = devilSpecial;
	}
	
	@Override
	public boolean isPC98()
	{
		return true;
	}
	
	public void setWingsClosed()
	{
		this.idle = normalIdle;
		this.left = normalLeft;
		this.right = normalRight;
		this.special = normalSpecial;
	}
	
	public void setWingsOpen()
	{
		this.idle = devilIdle;
		this.left = devilLeft;
		this.right = devilRight;
		this.special = devilSpecial;
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

