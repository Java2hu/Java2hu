package java2hu.allstar.enemies.day8.alice;

import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.overwrite.J2hMusic;
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
public class Alice extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Alice";
	
	public static Alice newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 96;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = new MovementAnimation(ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 5F, 1,2,3,4), ImageSplitter.getAnimationFromSprite(sprite, 0, chunkHeight, chunkHeight, chunkWidth, 5F, 5), 5f);
		Animation right = AnimationUtil.copyAnimation(left);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		bg.getTexture().setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3")));
		bgm.setLooping(true);
		
		final Alice boss = new Alice(100, nameTag, bg, fbs, idle, left, right, special, bgm, x, y);
		
		boss.setBgmPosition(103f);
		
		return boss;
	}
	
	public Sprite bg;
	
	public Alice(float maxHealth, TextureRegion nametag, final Sprite bg, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		
		this.bg = bg;
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
		// Use YuukaGeneral.executeFight(AllStarStageScheme scheme);
	}
}

