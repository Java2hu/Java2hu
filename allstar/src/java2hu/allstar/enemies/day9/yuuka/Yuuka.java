package java2hu.allstar.enemies.day9.yuuka;

import java2hu.Game;
import java2hu.Loader;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.background.BackgroundBossAura;
import java2hu.background.VerticalScrollingBackground;
import java2hu.object.DrawObject;
import java2hu.plugin.sprites.FadeInSprite;
import java2hu.util.AnimationUtil;
import java2hu.util.BossUtil.BackgroundAura;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Simple boss, should be used as a template for other bosses instead of constant copying.
 */
public class Yuuka extends AllStarBoss
{
	/**
	 * Boss Name
	 */
	final static String BOSS_NAME = "Yuuka";
	
	public static Yuuka newInstance(float x, float y)
	{
		String folder = "enemy/" + BOSS_NAME.toLowerCase() + "/";
		
		int chunkHeight = 128;
		int chunkWidth = 128;

		Texture sprite = Loader.texture(Gdx.files.internal(folder + "anm.png"));
		sprite.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Sprite fbs = new Sprite(Loader.texture(Gdx.files.internal(folder + "fbs.png")));
		fbs.setScale(2F);

		TextureRegion nameTag = new TextureRegion(Loader.texture(Gdx.files.internal(folder + "nametag.png")));

		Animation idle = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 1,2,3,4);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation left = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 10F, 5,6,7,8);
		Animation right = AnimationUtil.copyAnimation(left);

		for(TextureRegion reg : right.getKeyFrames())
			reg.flip(true, false);

		Animation special = ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, 20F, 9,10);
		special.setPlayMode(PlayMode.NORMAL);

		Sprite bg = new Sprite(Loader.texture(Gdx.files.internal(folder + "bg.png")));
		bg.getTexture().setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		Sprite bge = new Sprite(Loader.texture(Gdx.files.internal(folder + "bge.png")));
		bge.getTexture().setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
		Music bgm = Gdx.audio.newMusic(Gdx.files.internal(folder + "bgm.mp3"));
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setPosition(78.2f);
		bgm.setLooping(true);
		
		final Yuuka boss = new Yuuka(100, nameTag, bg, bge, fbs, idle, left, right, special, bgm, x, y);
		
		return boss;
	}
	
	private Setter<BackgroundBossAura> backgroundSetter;
	
	public Yuuka(float maxHealth, TextureRegion nametag, final Sprite bg, final Sprite bge, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, Music bgm, float x, float y)
	{
		super(maxHealth, nametag, fullBodySprite, idle, left, right, special, bgm, x, y);
		
		addDisposable(nametag);
		addDisposable(fullBodySprite);
		addDisposable(idle);
		addDisposable(left);
		addDisposable(right);
		addDisposable(special);
		addDisposable(bg);
		addDisposable(bge);
		
		final Yuuka boss = this;
		
		backgroundSetter = new Setter<BackgroundBossAura>()
		{
			@Override
			public void set(final BackgroundBossAura t)
			{
				bge.getTexture().setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
				
				Game.getGame().spawn(new VerticalScrollingBackground(bge, 2f, false)
				{
					{
						setFrameBuffer(t.getBackgroundBuffer());
						setZIndex(-101);
					}
				});
				
				Game.getGame().spawn(new DrawObject()
				{
					{
						setFrameBuffer(t.getBackgroundBuffer());
						addEffect(new FadeInSprite(new Getter<Sprite>()
						{
							@Override
							public Sprite get()
							{
								return bg;
							}
						}
						, 0f, 0.4f, 0.01F));
						setZIndex(-100);
					}
					
					@Override
					public void onDraw()
					{
						bg.setBounds(130, 0, bg.getRegionWidth() * 4f, bg.getRegionHeight() * 4f);
						bg.draw(Game.getGame().batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						super.onUpdate(tick);
						
						bg.setOriginCenter();
						bg.rotate(0.2f);
					}
					
					@Override
					public boolean isPersistant()
					{
						return boss.isOnStage();
					}
				});
			}
		};
	}
	
	public void spawnBackground(BackgroundBossAura backgroundBossAura)
	{
		backgroundSetter.set(backgroundBossAura);
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

