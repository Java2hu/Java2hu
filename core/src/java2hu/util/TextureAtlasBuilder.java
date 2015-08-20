package java2hu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;

/**
 * Build a file for a TextureAtlas.
 * Textures inserted in here have to reference a path, else they will simply be set to null.
 */
public class TextureAtlasBuilder extends J2hObject
{
	TextureAtlas atlas = new TextureAtlas();
	
	/**
	 * Use format: String TextureRegion|Animation for bulk usage.
	 * @param objects
	 */
	public void atlas(Object... objects)
	{
		for(int i = 0; i < objects.length; i += 2)
		{
			String id = (String) objects[i];
			Object o = objects[i + 1];
			
			if(o instanceof TextureRegion)
				atlas(id, (TextureRegion)o);
			
			if(o instanceof Animation)
				atlas(id, (Animation)o);
		}
	}
	
	public void atlas(String id, TextureRegion reg)
	{
		AtlasRegion r = atlas.addRegion(id, reg);
		r.flip(reg.isFlipX(), reg.isFlipY());
	}
	
	public void atlas(String id, Animation ani)
	{
		AnimationUtil.toAtlas(atlas, ani, id);
	}
	
	public void saveAtlas(FileHandle handle)
	{
		handle.writeString(getResult(handle.parent().path().replace("bin/", "")), false);
	}
	
	/**
	 * Returns a String in the TextureAtlas data file format, which can be used in the TextureAtlas constructors.
	 * @param The path the file will be put in, will be cut from paths.
	 */
	public String getResult(String path)
	{
		String nl = "\n";
		
		HashMap<String, ArrayList<AtlasRegion>> h = new HashMap<String, ArrayList<AtlasRegion>>();
		
		System.out.println(path);
		
		for(AtlasRegion r : atlas.getRegions())
		{
			String s = ((FileTextureData)r.getTexture().getTextureData()).getFileHandle().path();
			
			if(s.startsWith(path))
				s = s.replace(path, "");
			
			if(s.startsWith("/"))
				s = s.substring(1);

			if(!h.containsKey(s))
			{
				h.put(s, new ArrayList<TextureAtlas.AtlasRegion>());
			}
			
			ArrayList<TextureAtlas.AtlasRegion> list = h.get(s);
			
			list.add(r);
		}
		
		String s = "";
		
		for(Entry<String, ArrayList<AtlasRegion>> set : h.entrySet())
		{
			s += set.getKey() + nl;
		
			s += "format: RGBA8888" + nl;
			s += "filter: MipMapLinearNearest,Nearest" + nl;
			s += "repeat: xy" + nl;
			
			for(AtlasRegion r : set.getValue())
			{
				s += r.name + nl;
				s += "rotate: " + r.rotate + nl;
				
				int regionX = r.getRegionX();
				int regionY = r.getRegionY();
				int packedWidth = r.packedWidth;
				int packedHeight = r.packedHeight;
				int originalWidth = packedWidth;
				int originalHeight = packedHeight;
				
				s += "xy: " + regionX + ", " + regionY + nl;
				s += "size: " + packedWidth + ", " + packedHeight + nl;
				s += "orig: " + originalWidth + ", " + originalHeight + nl;
				s += "offset: " + ((int)r.offsetX) + ", " + ((int)r.offsetY) + nl;
				s += "index: " + r.index + nl;
			}
			
			s += "" + nl;
		}
		
		return s;
	}
	
	public TextureAtlas getAtlas()
	{
		return atlas;
	}
}
