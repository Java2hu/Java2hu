package java2hu.touhou.enemy;

import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.object.enemy.IEnemyType;
import java2hu.util.AnimationUtil;
import java2hu.util.Getter;
import java2hu.util.ImageSplitter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public enum TouhouEnemyType implements IEnemyType
{
	SMALL_FAIRY_BLUE(new Getter<Animation>()
	{
		private Animation saved = null;
		
		@Override
		public Animation get()
		{
			if(saved == null)
			{
				Array<TextureRegion> array = new Array<TextureRegion>();

				for(int i = 0; i < 4; i++)
				{
					array.add(new HitboxSprite(new TextureRegion(enemyTexture, 0 + i * 64, 640, 64, 64)));
				}
				
				saved = new Animation(5f, array);
			}
			
			Animation ani = AnimationUtil.copyAnimation(saved);
			ani.setPlayMode(PlayMode.LOOP);
			
			return ani;
		}
	},
	new Getter<Animation>()
	{
		private Animation saved = null;
		
		@Override
		public Animation get()
		{
			if(saved == null)
			{
				saved = AnimationUtil.copyAnimation(SMALL_FAIRY_BLUE.getRightAnimation());
				
				for(TextureRegion r : saved.getKeyFrames())
				{
					r.flip(true, false);
				}
			}
			
			return AnimationUtil.copyAnimation(saved);
		}
	},
	new Getter<Animation>()
	{
		private Animation saved = null;
		
		@Override
		public Animation get()
		{
			if(saved == null)
			{
				Array<TextureRegion> startup = new Array<TextureRegion>();
				Array<TextureRegion> loop = new Array<TextureRegion>();
				
				for(int i = 0; i < 7; i++)
				{
					Array<TextureRegion> array = i < 3 ? startup : loop;
					
					array.add(new HitboxSprite(new TextureRegion(enemyTexture, 5 * 64 + i * 64, 640, 64, 64)));
				}
				
				saved = new StartupLoopAnimation(startup, loop, 5f);
			}
			
			return AnimationUtil.copyAnimation(saved);
		}
	},
	new Getter<Animation>()
	{
		@Override
		public Animation get()
		{
			return null;
		}
	});
	
	private static Texture auraTexture;
	private static Texture enemyTexture;
	
	public static Texture getAuraTexture()
	{
		return auraTexture;
	}
	
	public static Texture getEnemyTexture()
	{
		return enemyTexture;
	}
	
	private static Animation auraAnimation;
	
	public static Animation getAuraAnimation()
	{
		return AnimationUtil.copyAnimation(auraAnimation);
	}
	
	static
	{
		 auraTexture = Loader.texture(Gdx.files.internal("sprites/enemy/enemy_aura.png"));
		 auraTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		 
		 auraAnimation = ImageSplitter.getAnimationFromSprite(auraTexture, 48, 48, 0.25f, 1,2,3,4,5,6,7,8);
		 auraAnimation.setPlayMode(PlayMode.LOOP);
		
		 enemyTexture = Loader.texture(Gdx.files.internal("enemy.png"));
		 enemyTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
	}
	
	private Getter<Animation> idle, left, right, special;
	
	private TouhouEnemyType(Getter<Animation> idle, Getter<Animation> left, Getter<Animation> right, Getter<Animation> special)
	{
		this.idle = idle;
		this.left = left;
		this.right = right;
		this.special = special;
	}

	@Override
	public Animation getIdleAnimation()
	{
		return idle.get();
	}

	@Override
	public Animation getLeftAnimation()
	{
		return left.get();
	}

	@Override
	public Animation getRightAnimation()
	{
		return right.get();
	}

	@Override
	public Animation getSpecialAnimation()
	{
		return special.get();
	}
}
