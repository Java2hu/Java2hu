package java2hu.allstar.enemies.day8.alice;


import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.overwrite.J2hMusic;
import java2hu.util.ImageSplitter;

import javafx.util.Duration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Alice98 extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Alice";
	
	public static Alice98 newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm98.png"));
		sprite.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 1);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 2);
		
		Animation right = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 3);
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 8F, 4);
		
		Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm98.mp3")));
		bgm.setLooping(true);
		
		final Alice98 boss = new Alice98(100, fbs, idle, left, right, special, bgm, x, y);
		
		boss.setBgmPosition((float) Duration.seconds(42).toSeconds());
		
		return boss;
	}
	
	public Alice98(float maxHealth, Sprite fbs, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, null, fbs, idle, left, right, special, bgm, x, y);
		
		addDisposable(idle);
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
	public void executeFight(AllStarStageScheme scheme)
	{
		// Use AliceGeneral.executeFight(AllStarStageScheme scheme);
	}
}

