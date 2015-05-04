package java2hu.touhou.bullet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.overwrite.J2hObject;
import java2hu.util.AnimationUtil;
import java2hu.util.HitboxUtil;
import java2hu.util.HitboxUtil.HitboxSpecification;
import util.PNG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * LEGACY: This dissects ZUN's bullet sprites into useable Animations, we now use the combination between IBulletType and IBulletSubType.
 * All data created in this class has been dumped into the assets/bullets folder, so we don't need to calculate it every time.
 * 
 * Loads all the touhou bullets from the assets, all of these can be spawned by new Bullet(TouhouTouhouBulletType, TouhouTouhouBulletColor)
 * This contains a lot of hard coding of the bullet textures, and have to be precise.
 * 
 * A bullet duo is basically a specification used to represent the Animation that is in the map.
 * The bullet duo is the key, and the animation the value.
 * 
 * Every bullet created will get a sprite based hitbox according to the standard hitbox specification in the HitboxUtil
 */
public class LegacyTouhouBulletMaker extends J2hObject
{
	private static HashMap<BulletDuo, Animation> map = new HashMap<BulletDuo, Animation>()
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7536399335371414119L;

		@Override
		public boolean containsKey(Object key)
		{
			return get(key) != null;
		}

		@Override
		public Animation get(Object key)
		{
			for(java.util.Map.Entry<BulletDuo, Animation> set : map.entrySet())
				if(set.getKey().equals(key))
					return set.getValue();

			return null;
		}
	};

//	@LoadOnStartup
	public static void load()
	{
		new Runnable()
		{
			@SuppressWarnings("unused")
			@Override
			public void run()
			{
				System.out.println("[BulletLoader] Loading bullets");
				long start = System.currentTimeMillis();
				
				ThBulletType type = null;
				
				{
					final String file = "sprites/bullets/bullet1.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 32;
					int chunkWidth = 32;

					int index = 1;

					type = ThBulletType.LAZER_STATIONARY;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.POINTER;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BALL_1;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BALL_2;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.RAIN;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.KUNAI;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.CRYSTAL;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.SEAL;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BULLET;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.RICE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.STAR;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BALL_REFLECTING;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 13;

					type = ThBulletType.DISK;

					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 2 * 16;
					index += 4;

					type = ThBulletType.DOT_MEDIUM;

					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					Texture texture = Game.getGame().assets.get("sprites/bullets/bullet1.png");

					int chunkHeight = 16;
					int chunkWidth = 16;

					int index = 1;

					type = ThBulletType.DOT_SMALL_FILLED;

					index += 2 * 12 * 32;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.DOT_SMALL_OUTLINE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 16;

					type = ThBulletType.DOT_SMALL_FILLED;

					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.DOT_SMALL_OUTLINE;

					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 16;
					index += 4 * 32;

					type = ThBulletType.DOT_SMALL_MOON;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 24;

					type = ThBulletType.DOT_SMALL_MOON;

					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					final String file = "sprites/bullets/bullet2.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 64;
					int chunkWidth = 64;

					int index = 1;

					type = ThBulletType.STAR_LARGE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BALL_BIG;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BUTTERFLY;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.KNIFE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.RICE_LARGE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.ORB_MEDIUM;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					Texture texture = Game.getGame().assets.get("sprites/bullets/bullet2.png");

					int chunkHeight = 128;
					int chunkWidth = 128;

					int index = 1;

					index += 3 * 4;

					type = ThBulletType.BALL_LARGE_HOLLOW;

					HitboxSpecification largeOrb = new HitboxSpecification()
					{
						@Override
						public boolean isHitbox(Color color, int x, int y)
						{
							return color.a > 0 && color.a < 0.5;
						}
					};

					TextureRegion region;
					add(type, ThBulletColor.RED, region = getTexture(texture, chunkHeight, chunkWidth, index++), HitboxUtil.makeHitboxFromSprite(region, largeOrb));
					add(type, ThBulletColor.BLUE, region = getTexture(texture, chunkHeight, chunkWidth, index++), HitboxUtil.makeHitboxFromSprite(region, largeOrb));
					add(type, ThBulletColor.GREEN, region = getTexture(texture, chunkHeight, chunkWidth, index++), HitboxUtil.makeHitboxFromSprite(region, largeOrb));
					add(type, ThBulletColor.YELLOW, region = getTexture(texture, chunkHeight, chunkWidth, index++), HitboxUtil.makeHitboxFromSprite(region, largeOrb));
				}

				{
					final String file = "sprites/bullets/bullet3.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 64;
					int chunkWidth = 64;

					int index = 1;

					type = ThBulletType.HEART;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.ARROW;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.ORB;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));

					index += 8;

					type = ThBulletType.UNKNOWN_1;

					Array<TextureRegion> animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 4; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.RED, new Animation(1, animation));

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 4; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.PINK, new Animation(1, animation));

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 4; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.PURPLE, new Animation(1, animation));

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 4; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.ORANGE, new Animation(1, animation));

					index += 8;

					type = ThBulletType.UNKNOWN_2;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					Texture texture = Game.getGame().assets.get("sprites/bullets/bullet3.png");

					int chunkHeight = 32;
					int chunkWidth = 32;

					int index = 1;

					index += 12 * 16;

					type = ThBulletType.UNKNOWN_3;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PINK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE_DARK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN_LIGHTER, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW_LIGHT, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.ORANGE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					final String file = "sprites/bullets/bullet4.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 128;
					int chunkWidth = 128;

					int index = 1;

					type = ThBulletType.ORB_LARGE;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}
				
				{
					final String file = "sprites/bullets/bullet4.png";

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 64;
					int chunkWidth = 64;

					type = ThBulletType.ORB_SHADE;

					add(type, ThBulletColor.WHITE, new TextureRegion(texture, 448, 448, chunkHeight, chunkWidth));
				}

				{
					final String file = "sprites/bullets/bullet5.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 64;
					int chunkWidth = 64;

					int index = 1;

					type = ThBulletType.NOTE_EIGHT;

					Array<TextureRegion> animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 3; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.RED, new Animation(1, animation));

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 3; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.BLUE, new Animation(1, animation));

					index += 2;

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 3; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.GREEN, new Animation(1, animation));

					animation = new Array<TextureRegion>();

					for(int frameNr = 0; frameNr < 3; frameNr++)
					{
						TextureRegion frame = getTexture(texture, chunkHeight, chunkWidth, index++);

						animation.add(makeFullHitboxSpriteFromTexture(frame));
					}

					add(type, ThBulletColor.PURPLE, new Animation(1, animation));

					index += 2;

					type = ThBulletType.NOTE_QUARTER_REST;

					add(type, ThBulletColor.BLACK, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.RED, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.PURPLE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.BLUE, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.CYAN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.GREEN, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.YELLOW, getTexture(texture, chunkHeight, chunkWidth, index++));
					add(type, ThBulletColor.WHITE, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					final String file = "sprites/bullets/item.png";

					Game.getGame().assets.load(file, Texture.class);
					Game.getGame().assets.finishLoading();

					Texture texture = Game.getGame().assets.get(file);

					int chunkHeight = 64;
					int chunkWidth = 64;

					int index = 1;

					type = ThBulletType.POWER_LARGE;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.ONEUP_SECTION;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.ONEUP;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BOMB_SECTION;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.BOMB;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));

					type = ThBulletType.FULL_POWER;

					add(type, null, getTexture(texture, chunkHeight, chunkWidth, index++));
				}

				{
					Texture texture = Game.getGame().assets.get("sprites/bullets/item.png");

					add(ThBulletType.POWER_SMALL, null, getSubImage(texture, 384, 4, 32, 32));
					add(ThBulletType.POINT_SMALL, null, getSubImage(texture, 416, 4, 32, 32));
					add(ThBulletType.POWER_MEDIUM, null, getSubImage(texture, 450, 2, 44, 44));
					add(ThBulletType.POINT_MEDIUM, null, getSubImage(texture, 416, 96, 32, 32));

					add(ThBulletType.GRAZE_LARGE, null, getSubImage(texture, 384, 96, 32, 32));
					add(ThBulletType.GRAZE_MEDIUM, null, getSubImage(texture, 416, 64, 32, 32));
					add(ThBulletType.GRAZE_SMALL, null, getSubImage(texture, 384, 64, 32, 32));
				}

				long end = System.currentTimeMillis();

				System.out.println("[BulletLoader] Done!" + (end - start) + "ms");
				
				// After this, it saves the now made bullets onto the new format.
				// Saved to "bullets/TYPE/COLOR/"
				// Since they're technically animations, the frames are saved with number.png, ie. 1.png, 2.png, 3.png.
				// Data for the bullet is stored in the frame number.json, ie. 1.json, 2.json, 3.json
				// This is mostly used to save the hitbox of the bullet.
				// Uncomment to make it work
//				if(true)
//					return;
				
				File folder = new File("bullets");
				
				if(folder.exists())
					folder.delete();
				
				folder.mkdir();
				
				Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
				
				for(ThBulletType bt : ThBulletType.values())
					for(ThBulletColor bc : ThBulletColor.values())
					{
						BulletDuo duo = new BulletDuo(bt, bc);
						
						if(map.containsKey(duo))
						{
							Animation ani = map.get(duo);
							
							String dirPath = "bullets/" + bt.toString() + "/" + bc.toString() + "/";
							File dir = new File(dirPath);
							
							if(!dir.exists())
								dir.mkdirs();
							
							int frame = 1;
							
							for(TextureRegion r : ani.getKeyFrames())
							{
								HitboxSprite s = (HitboxSprite)r;
								
								Game.getGame().batch.begin();
								
								Gdx.gl.glClearColor(0, 0, 0, 0);
							    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
								
								s.setOriginCenter();
								s.rotate(180);
								s.setPosition(10, 10);
								s.draw(Game.getGame().batch);
								
								Game.getGame().batch.end();
								
								try
								{
									byte[] png = PNG.toPNG(ScreenUtils.getFrameBufferPixmap(0, 0, s.getRegionWidth() + 20, s.getRegionHeight() + 20));
									
									File file = new File(dirPath + frame + ".png");
									
									if(file.exists())
										file.delete();
									
									file.createNewFile();
									
									FileOutputStream out = new FileOutputStream(file);
									out.write(png);
									
									out.close();
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							
								frame++;
							}
							
							System.out.println("Done! " + bt + "  " + bc);
							
						}
					}
			}
		}.run();
	}

	static TextureRegion getSubImage(Texture image, int x, int y, int width, int height)
	{
		TextureRegion texture = new TextureRegion(image, x, y, width, height);
		
		return texture;
	}
	
	static TextureRegion getTexture(Texture image, int chunkHeight, int chunkWidth, int id)
	{
		int rows = image.getHeight() / chunkHeight;
		int cols = image.getWidth() / chunkWidth;

		int count = 0;

		for (int y = 0; y < rows; y++)
			for (int x = 0; x < cols; x++)
			{  
				count++;

				if(count == id)
				{
					TextureRegion texture = new TextureRegion(image, chunkWidth * x, chunkHeight * y, chunkWidth, chunkHeight);
					
					return texture;
				}
			}

		return null;
	}
	
	static TextureRegion getTexture(Texture image)
	{
		return new TextureRegion(image);
	}

	static void add(ThBulletType type, ThBulletColor color, Animation animation)
	{
		map.put(new BulletDuo(type, color), animation);
	}
	
	static void add(ThBulletType type, ThBulletColor color, TextureRegion texture)
	{
		Polygon gon = HitboxUtil.makeHitboxFromSprite(texture, HitboxUtil.StandardBulletSpecification.get());
		
		add(type, color, texture, gon);
	}

	static void add(ThBulletType type, ThBulletColor color, TextureRegion texture, Polygon hitbox)
	{
		BulletDuo duo = new BulletDuo(type, color);

		HitboxSprite sprite = makeHitboxSpriteFromTexture(texture, hitbox);
		
		Animation animation = new Animation(1, sprite);

		map.put(duo, animation);
	}
	
	static HitboxSprite makeHitboxSpriteFromTexture(TextureRegion texture, Polygon hitbox)
	{
		HitboxSprite sprite = new HitboxSprite(texture);
		sprite.setOriginCenter();
		sprite.setHitbox(hitbox);
		sprite.setHitboxScaleOffsetModifier(0.5F);
		
		return sprite;
	}

	static HitboxSprite makeFullHitboxSpriteFromTexture(TextureRegion texture)
	{
		Polygon gon = HitboxUtil.makeHitboxFromSprite(texture, HitboxUtil.StandardBulletSpecification.get());
		texture.getTexture().setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		return makeHitboxSpriteFromTexture(texture, gon);
	}
	
	public static Animation getSchematic(ThBulletType type, ThBulletColor color)
	{
		BulletDuo duo = new BulletDuo(type, color);

		Animation saved = null;
		
		if(!map.containsKey(duo))
		{
			ThBulletColor old = duo.color;
			
			for(ThBulletColor c : ThBulletColor.values())
			{
				duo.color = c;
				
				if(map.containsKey(duo))
				{
					saved = map.get(duo);
					
					Animation copy = AnimationUtil.copyAnimation(saved);
					
					for(TextureRegion r : copy.getKeyFrames())
						if(r instanceof HitboxSprite || r instanceof Sprite)
							((Sprite)r).setColor(color.getColor());
					
					return copy;
				}
			}
		}
		else
			saved = map.get(duo);
		
		if(saved == null)
			new Exception("No combination of BulletType:" + type + " + BulletColor:" + color + ".").printStackTrace();
		
		return AnimationUtil.copyAnimation(saved);
	}

	public static class BulletDuo extends J2hObject
	{
		public ThBulletType type;
		public ThBulletColor color;

		public BulletDuo(ThBulletType type, ThBulletColor color)
		{
			this.type = type;
			this.color = color;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof BulletDuo)
			{
				BulletDuo info = (BulletDuo) obj;

				if(info.type == type && info.color == color)
					return true;
			}

			return false;
		}
	}

	public static boolean schematicExists(BulletDuo duo)
	{
		if(!map.containsKey(duo))
		{
			ThBulletColor old = duo.color;
			
			for(ThBulletColor c : ThBulletColor.values())
			{
				duo.color = c;
				
				if(map.containsKey(duo))
					return true;
			}
		}
		else
			return true;
		
		return false;
	}
}
