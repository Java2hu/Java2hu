package java2hu.touhou.bullet;

import java.util.HashMap;
import java2hu.Loader;
import java2hu.object.bullet.ILaserType;
import java2hu.object.bullet.LaserDrawer.LaserAnimation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.Array;

public class ThLaser implements ILaserType
{
	private ThLaserColor color;
	private ThLaserType type;
	
	/**
	 * Constructor for LIGHTNING type.
	 * @param type
	 */
	public ThLaser(ThLaserType type)
	{
		this(type, ThLaserColor.LIGHTNING);
	}
	
	/**
	 * Constructor for NORMAL type.
	 * @param type
	 * @param color
	 */
	public ThLaser(ThLaserType type, ThLaserColor color)
	{
		this.color = color;
		this.type = type;
	}
	
	private static HashMap<String, LaserAnimation> animations = new HashMap<String, LaserAnimation>();
	
	@Override
	public LaserAnimation getAnimation()
	{
		if(color == ThLaserColor.LIGHTNING)
			type = ThLaserType.LIGHTNING;
		
		String identifier = type.name() + " " + (color == null ? "" : color.name());
		
		if(animations.containsKey(identifier))
		{
			return animations.get(identifier);
		}
		
		FileHandle textureFolder = Gdx.files.internal("lasers/" + type.name() + "/");
		
		Array<Texture> array = new Array<Texture>();
		
		String[] fileNames;
		float frameDelay = 1f;
		
		if(type == ThLaserType.LIGHTNING)
		{
			fileNames = new String[]{ "1", "2", "3", "4" };
			
			frameDelay = 5f;
		}
		else
		{
			fileNames = new String[]{ color.name() };
			
			FileHandle file = textureFolder.child(color.name() + ".png");
			
			Texture text = Loader.texture(file);
			text.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		}
		
		for(String name : fileNames)
		{
			FileHandle frame = textureFolder.child(name + ".png");
			
			Texture text = Loader.texture(frame);
			text.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
		
			array.add(text);
		}
			
		LaserAnimation la = new LaserAnimation(frameDelay, array);
		
		animations.put(identifier, la);
		
		return getAnimation(); // It's now in the hashmap, and will load from that (also needs to clone it).
	}

	@Override
	public float getThickness()
	{
		return type.getThickness();
	}

	@Override
	public float getHitboxThickness()
	{
		return type.getHitboxThickness();
	}

	@Override
	public Color getColor()
	{
		return color.getColor();
	}
	
}
