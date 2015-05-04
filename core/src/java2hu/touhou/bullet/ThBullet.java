package java2hu.touhou.bullet;

import java.io.File;
import java.util.HashMap;
import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.object.bullet.IBulletType;
import java2hu.util.AnimationUtil;
import java2hu.util.HitboxUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ThBullet implements IBulletType
{
	private ThBulletType type;
	private ThBulletColor color;
	
	public ThBullet(ThBulletType type, ThBulletColor color)
	{
		this.type = type;
		this.color = color;
	}
	
	public Color getColor()
	{
		return color.color;
	}
	
	private static HashMap<String, Animation> animations = new HashMap<String, Animation>();
	private static Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
	
	public static class AnimationData
	{
		public float intervalTicks;
	}

	@Override
	public Animation getAnimation()
	{
		String identifier = type.name() + " " + color.name();
		
		if(animations.containsKey(identifier))
		{
			return AnimationUtil.copyAnimation(animations.get(identifier));
		}
		
		float modifierX = 1f;
		float modifierY = 1f;
		
		if(type instanceof ThBulletType)
		{
			ThBulletType bt = type;
			
			modifierX = bt.getOffsetModifierX();
			modifierY = bt.getOffsetModifierY();
		}
		
		FileHandle textureFolder = Gdx.files.internal("bullets/" + type.name() + "/");
		FileHandle folder = textureFolder.child(color.name());
		
		if(!folder.exists())
			return null;
		
		FileHandle generalVerticesFile = textureFolder.child("general.vertices");
		boolean hasGeneralVertices = generalVerticesFile.exists();
		
		FileHandle animationDataFile = textureFolder.child("animation.data");
		boolean isAnimation = animationDataFile.exists();
		
		AnimationData animationData = null;
		
		if(isAnimation)
		{
			String json = animationDataFile.readString();
			
			// Code to make a sample file.
//			if(json.equals(" ") || json.replaceAll(" ", "").isEmpty())
//			{
//				if(type == ThBulletType.NOTE_EIGHT)
//					System.out.println(true);
//				
//				animationData = new AnimationData();
//				animationData.intervalTicks = 1f;
//				
//				String newJson = gson.toJson(animationData);
//				
//				File file = new File("bin/" + animationDataFile.path());
//				
//				file.delete();
//				
//				try
//				{
//					file.createNewFile();
//				}
//				catch (IOException e1)
//				{
//					e1.printStackTrace();
//				}
//				
//				FileWriter w = null;
//				
//				try
//				{
//					w = new FileWriter(file);
//					
//					w.write(newJson);
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//				finally
//				{
//					if(w != null)
//						try
//						{
//							w.close();
//						}
//						catch (IOException e)
//						{
//							e.printStackTrace();
//						}
//				}
//			}
//			else
			{
				animationData = gson.fromJson(json, AnimationData.class);
			}
		}
		
		float frameInterval = isAnimation ? animationData.intervalTicks : 1;
		
		int id = 1;
		
		Array<HitboxSprite> frames = new Array<HitboxSprite>();
		
		Gson gson = HitboxUtil.gson;
		
		float[] generalVertices = null;
		
		if(hasGeneralVertices)
		{
			generalVertices = gson.fromJson(generalVerticesFile.readString(), float[].class);
		}
		
		while(true)
		{
			try
			{
				FileHandle frame = folder.child(id + ".png");

				if(!frame.exists())
					break;
				
				Polygon poly = null;
				
				FileHandle verticesFile = folder.child(id + ".json");
				
				if(verticesFile.exists())
				{
					FileHandle data = verticesFile;
					
					if(generalVertices != null)
					{
						new File("bin/" + data.path()).delete();
					}
					else
					{
						String json = data.readString();

						poly = gson.fromJson(json, Polygon.class);
					}
				}
				
				if(generalVertices != null)
				{
					poly = new Polygon(generalVertices);
					
					Rectangle b = poly.getBoundingRectangle();
					
					poly.setOrigin(b.width / 2f, b.height / 2f);
				}

				Texture text = Loader.texture(frame);
				text.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
				
				HitboxSprite sprite = new HitboxSprite(new Sprite(text));
				
				if(poly != null)
				{
					sprite.setHitbox(poly);
					
					sprite.setHitboxScaleOffsetModifierX(modifierX);
					sprite.setHitboxScaleOffsetModifierY(modifierY);
				}

				frames.add(sprite);
				id++;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		Animation ani = new Animation(frameInterval, frames);
		
		animations.put(identifier, ani);
		
		return getAnimation(); // It's now in the hashmap, and will load from that (also so it clones it).
	}

	@Override
	public Color getEffectColor()
	{
		return color != null ? color.getColor() : null;
	}
}
