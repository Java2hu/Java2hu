package java2hu.allstar.enemies.day9.yuuka;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.object.DrawObject;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.util.AnimationUtil;
import java2hu.util.Getter;

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
public class SleepyYuuka extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "SleepyYuuka";
	
	public static SleepyYuuka newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = new Animation(1f, new HitboxSprite(new Sprite(sprite)));
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = AnimationUtil.copyAnimation(idle);
		Animation right = AnimationUtil.copyAnimation(idle);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = AnimationUtil.copyAnimation(idle);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		bg.getTexture().setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
		
		final SleepyYuuka boss = new SleepyYuuka(100, nameTag, bg, fbs, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	public SleepyYuuka(float maxHealth, TextureRegion nametag, final Sprite bg, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		
		final SleepyYuuka boss = this;
		
		Game.getGame().spawn(new DrawObject()
		{
			{
				addEffect(new FadeInSprite(new Getter<Sprite>()
				{
					@Override
					public Sprite get()
					{
						return bg;
					}
				}
				, 0f, 1f, 0.01F));
				setZIndex(-1);
			}
			
			@Override
			public void onDraw()
			{
				bg.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
				bg.draw(Game.getGame().batch);
			}
			
			@Override
			public boolean isPersistant()
			{
				return boss.isOnStage();
			}
		});
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
		
	}
}

