package java2hu.util;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Simple util to help with animations
 */
public class AnimationUtil extends J2hObject
{
	/**
	 * Assumes the animation is filled with hitbox sprites.
	 * @param animation
	 * @return
	 */
	public static HitboxSprite getCurrentSprite(Animation animation)
	{
		J2hGame g = Game.getGame();
		
		return (HitboxSprite) animation.getKeyFrame(g.getElapsedTime());
	}
	
	public static HitboxSprite getCurrentSprite(Animation animation, boolean loop)
	{
		return getCurrentSprite(animation, Game.getGame().getElapsedTime(), loop);
	}
	
	/**
	 * Assumes the animation is filled with hitbox sprites.
	 * @param animation
	 * @return
	 */
	public static HitboxSprite getCurrentSprite(Animation animation, float animationTime, boolean loop)
	{
		return (HitboxSprite) animation.getKeyFrame(animationTime, loop);
	}
	
	public static HitboxSprite getCurrentSprite(Animation animation, float animationTime)
	{
		return (HitboxSprite) animation.getKeyFrame(animationTime);
	}
	
	/**
	 * When using a class other than TextureRegion.class, the method is UNSAFE!
	 * It'll assume every texture region is the same class.
	 * @param ani
	 * @param clazz
	 * @param setter
	 */
	public static <T extends TextureRegion> void massAction(Class<T> clazz, Setter<T> setter, Animation... animations)
	{
		for(Animation ani : animations)
		for(TextureRegion r : ani.getKeyFrames())
		{
			setter.set((T) r);
		}
	}
	
	public static Animation copyAnimation(Animation animation)
	{
		return copyAnimation(animation, animation.getFrameDuration());
	}
	
	public static Animation copyAnimation(Animation animation, float frameDuration)
	{
		if(animation instanceof StartupLoopAnimation)
		{
			StartupLoopAnimation ani = (StartupLoopAnimation) animation;
			
			return new StartupLoopAnimation(ani);
		}
		else
		{
			return new Animation(frameDuration, copyFrames(animation.getKeyFrames()));
		}
	}
	
	public static TextureRegion[] copyFrames(TextureRegion[] animation)
	{
		// Create a new animation with the same frames, so they can have their own transformations and stuff.
		TextureRegion[] newFrames = new TextureRegion[animation.length];

		for(int i = 0; i < animation.length; i++)
		{
			TextureRegion original = animation[i];
			
			newFrames[i] = copy(original);
		}

		return newFrames;
	}
	
	public static Array<TextureRegion> copyFrames(Array<TextureRegion> animation)
	{
		Array<TextureRegion> newArray = new Array<TextureRegion>();
		
		for(int i = 0; i < animation.size; i++)
		{
			TextureRegion original = animation.get(i);
			
			newArray.add(copy(original));
		}
		
		return newArray;
	}
	
	public static TextureRegion copy(TextureRegion original)
	{
		TextureRegion newRegion;

		if(original.getClass() == HitboxSprite.class)
		{
			newRegion = new HitboxSprite(original);
		}
		else if(original.getClass() == Sprite.class)
			newRegion = new Sprite(new TextureRegion(original));
		else
			newRegion = new TextureRegion(original);

		return newRegion;
	}
	
	/**
	 * Gets all frames out of a folder, they should be formatted as 1.png, 2.png, 3.png, etc.
	 * It will stop reading as soon as the next frame isn't present.
	 * @param folder
	 * @return
	 */
	public static Array<TextureRegion> getFramesFromFolder(FileHandle folder)
	{
		int id = 1;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		while(true)
		{
			try
			{
				FileHandle frame = folder.child(id + ".png");

				if(!frame.exists())
					break;

				frames.add(new TextureRegion(Loader.texture(frame)));
				id++;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return frames;
	}
	
	/**
	 * Makes an animation from a folder. frames should be denoted with 1.png, 2.png, 3.png, etc.
	 * @param folder
	 * @return
	 */
	public static Animation makeSpriteAnimationFromFolder(float frameDuration, FileHandle folder)
	{
		Array<TextureRegion> frames = getFramesFromFolder(folder);
		
		Array<Sprite> spriteFrames = new Array<>();
		
		for(TextureRegion r : frames)
		{
			spriteFrames.add(new Sprite(r));
		}
		
		return new Animation(frameDuration, spriteFrames);
	}
	
	/**
	 * Makes an animation from a folder. frames should be denoted with 1.png, 2.png, 3.png, etc.
	 * @param folder
	 * @return
	 */
	public static Animation makeAnimationFromFolder(float frameDuration, FileHandle folder)
	{
		return new Animation(frameDuration, getFramesFromFolder(folder));
	}
}
