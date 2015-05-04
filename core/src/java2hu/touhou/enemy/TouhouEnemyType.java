package java2hu.touhou.enemy;

import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.object.enemy.IEnemyType;
import java2hu.util.Getter;

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
					array.add(new HitboxSprite(new TextureRegion(texture, 0 + i * 64, 640, 64, 64)));
				}
				
				saved = new Animation(5f, array);
			}
			
			Animation ani = utils().animation().copyAnimation(saved);
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
				saved = utils().animation().copyAnimation(SMALL_FAIRY_BLUE.getRightAnimation());
				
				for(TextureRegion r : saved.getKeyFrames())
				{
					r.flip(true, false);
				}
			}
			
			return utils().animation().copyAnimation(saved);
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
					
					array.add(new HitboxSprite(new TextureRegion(texture, 5 * 64 + i * 64, 640, 64, 64)));
				}
				
				saved = new StartupLoopAnimation(startup, loop, 5f);
			}
			
			return utils().animation().copyAnimation(saved);
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
	
	private static Texture texture;
	
	static
	{
		 texture = Loader.texture(Gdx.files.internal("enemy.png"));
		 texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
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
