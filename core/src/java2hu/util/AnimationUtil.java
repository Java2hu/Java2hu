package java2hu.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.MovementAnimation;
import java2hu.StartupLoopAnimation;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
		
		return (HitboxSprite) animation.getKeyFrame(g.getTotalElapsedTime());
	}
	
	public static HitboxSprite getCurrentSprite(Animation animation, boolean loop)
	{
		return getCurrentSprite(animation, Game.getGame().getTotalElapsedTime(), loop);
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
	 * Loads an animation from a texture atlas.
	 * The format is { name1, name2, name3... }, where name is the name parameter.
	 * Save an animation with {@link #toAtlas(TextureAtlas, Animation, String)}
	 * @param <T> - The class to create this object from, this works by calling a constructor with TextureRegion as the only argument.
	 */
	public static <T extends TextureRegion> Animation fromAltas(TextureAtlas atlas, Class<T> type, String name, float frameDuration)
	{
		Array<T> array = new Array<T>();
		
		int i = 0;
		
		while(true)
		{
			TextureRegion r = atlas.findRegion(name + i);
			
			if(r == null)
			{
				if(i == 0)
				{
					// Check if this is a StartupLoopAnimation or MovementAnimation
					TextureRegion check = atlas.findRegion(name + "_start0"); // Check if a start0 frame exists.
					
					if(check != null)
					{
						Animation start = fromAltas(atlas, type, name + "_start", frameDuration);
						Animation loop = fromAltas(atlas, type, name + "_loop", frameDuration);
						Animation end = fromAltas(atlas, type, name + "_end", frameDuration);
						
						if(end.getKeyFrames().length == 0)
						{
							return new StartupLoopAnimation(start, loop, frameDuration);
						}
						else
						{
							return new MovementAnimation(start, loop, end, frameDuration);
						}
					}
				}
				break;
			}
			
			i++;
			
			if(type == TextureRegion.class)
			{
				array.add((T) r);
				continue;
			}
			
			T t = null;
			Constructor<T> c = null;

			try
			{
				c = type.getConstructor(TextureRegion.class);
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}

			if(c == null)
				System.out.println(type.getSimpleName() + " does not have a TextureRegion constructor! Please make sure you can construct it with only TextureRegion as argument.");

			try
			{
				t = c.newInstance(r);
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
			
			array.add(t);
		}
		
		return new Animation(frameDuration, array);
	}
	
	/**
	 * Maps an animation to the TextureAtlas.
	 * In case of a normal animation, it's frames will be saved as { name1, name2, name3... }.
	 * In case of a StartupLoopAnimation, it's frames will be saved as { name_start1, name_start2, name_loop1, name_loop2... }
	 * In case of a MovementAnimation, same as StartupLoopAnimation, except with _end frames as well.
	 */
	public static void toAtlas(TextureAtlas atlas, Animation ani, String name)
	{
		if(ani instanceof StartupLoopAnimation)
		{
			AnimationUtil.toAtlas(atlas, new Animation(0f, ((StartupLoopAnimation)ani).getStartFrames()), name + "_start");
			AnimationUtil.toAtlas(atlas, new Animation(0f, ((StartupLoopAnimation)ani).getLoopFrames()), name + "_loop");

			if(ani instanceof MovementAnimation)
			{
				AnimationUtil.toAtlas(atlas, new Animation(0f, ((MovementAnimation) ani).getEndFrames()), name + "_end");
			}
			
			return;
		}
		
		int i = 0;
		
		for(TextureRegion r : ani.getKeyFrames())
		{
			ImageUtil.addToTextureAtlas(atlas, name + i++, r);
		}
	}
	
	/**
	 * Conduct an action on all frames inside the specified animations.
	 * All frames in the animation should be the same type of class,
	 * or have the same super class, because this method will cast them to the generic type.
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
		if(animation instanceof MovementAnimation)
		{
			MovementAnimation ani = (MovementAnimation) animation;
			
			return new MovementAnimation(ani);
		}
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
