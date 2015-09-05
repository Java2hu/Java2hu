package java2hu.util;
import java2hu.HitboxSprite;
import java2hu.overwrite.J2hObject;
import java2hu.util.HitboxUtil.StandardBulletSpecification;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import javax.annotation.Nullable;

/**
 * Used to split images from eachother, like a .png containing multiple sprites.
 */
public class ImageSplitter extends J2hObject
{
	/**
	 * Creates an animation from the selected frames.
	 * @param sprite
	 * @param chunkHeight
	 * @param chunkWidth
	 * @param frameDuration
	 * @param frames - Enter null to get all frames
	 * @return
	 */
	public static Animation getAnimationFromSprite(Texture sprite, int chunkHeight, int chunkWidth, float frameDuration, @Nullable Integer... frames)
	{
		return getAnimationFromSprite(sprite, false, 0, 0, chunkHeight, chunkWidth, frameDuration, frames);
	}
	
	/**
	 * Creates an animation from the selected frames.
	 * @param sprite
	 * @param makeHitbox - Make a hitbox from the sprite
	 * @param chunkHeight
	 * @param chunkWidth
	 * @param frameDuration
	 * @param frames - Enter null to get all frames
	 * @return
	 */
	public static Animation getAnimationFromSprite(Texture sprite, boolean makeHitbox, int chunkHeight, int chunkWidth, float frameDuration, @Nullable Integer... frames)
	{
		return getAnimationFromSprite(sprite, makeHitbox, 0, 0, chunkHeight, chunkWidth, frameDuration, frames);
	}
	
	/**
	 * Creates an animation from the selected frames.
	 * @param sprite
	 * @param makeHitbox - Make a hitbox from the sprite
	 * @param chunkHeight
	 * @param chunkWidth
	 * @param frameDuration
	 * @param frames - Enter null to get all frames
	 * @return
	 */
	public static Animation getAnimationFromSprite(Texture sprite, int startX, int startY, int chunkHeight, int chunkWidth, float frameDuration, @Nullable Integer... frames)
	{
		return getAnimationFromSprite(sprite, false, startX, startY, chunkHeight, chunkWidth, frameDuration, frames);
	}
	
	/**
	 * Creates an animation from the selected frames.
	 * @param sprite
	 * @param makeHitbox - Make a hitbox from the sprite
	 * @param chunkHeight
	 * @param chunkWidth
	 * @param frameDuration
	 * @param frames - Enter null to get all frames
	 * @return
	 */
	public static Animation getAnimationFromSprite(Texture sprite, boolean makeHitbox, int startX, int startY, int chunkHeight, int chunkWidth, float frameDuration, @Nullable Integer... frames)
	{
		Array<TextureRegion> frameArray = new Array<TextureRegion>();
		
		int rows = (sprite.getHeight() - startY) / chunkHeight;
		int cols = (sprite.getWidth() - startX) / chunkWidth;
		
		int count = 0;
		
		if(frames == null)
		{
			frames = new Integer[rows * cols];
			
			for(int i = 0; i < rows * cols; i++)
			{
				frames[i] = i;
			}
		}
		
		for(int frame : frames)
		{
			for (int y = 0; y < rows; y++)
			{  
				for (int x = 0; x < cols; x++)
				{  
					count++;
					
					if(count == frame)
					{
						HitboxSprite sub = new HitboxSprite(new TextureRegion(sprite, startX + chunkWidth * x, startY + chunkHeight * y, chunkWidth, chunkHeight));

						sub.setOriginCenter();
						
						if(makeHitbox)
							sub.setHitbox(HitboxUtil.makeHitboxFromSprite(sub, StandardBulletSpecification.get()));
						
						frameArray.add(sub);
					}
				}  
			}
			
			count = 0;
		}
		
		return new Animation(frameDuration, frameArray);
	}
}
