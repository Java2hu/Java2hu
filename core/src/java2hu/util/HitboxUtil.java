package java2hu.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A Util for hitboxes.
 * Has a lot of simple methods to make your life easier when it comes to those damn hitboxes.
 */
public class HitboxUtil extends J2hObject
{
	/**
	 * Makes an hitbox out of an image, uses a lot of gimmicky stuff to make a simply polygon hitbox.
	 * This might very well prove to be too much for smaller pc's, and then you'll need to save hitboxes made by this method and load them.
	 * It doesn't do the job perfectly, but decent enough to make great hitboxes that don't glitch when testing for intersection.
	 * @param currentTexture
	 * @param spec
	 * @return
	 * @deprecated Old algorithm, use the Hitbox Maker instead, then load it with HitboxUtil.loadHitbox(FileHandle handle)
	 */
	@Deprecated
	public static Polygon makeHitboxFromSprite(TextureRegion currentTexture, HitboxSpecification spec)
	{
		Pixmap map = null;

		TextureData data = currentTexture.getTexture().getTextureData();

		if(data.isPrepared())
			map = data.consumePixmap();
		else
		{
			data.prepare();
			map = data.consumePixmap();
		}

		HashMap<Integer, HashSet<Integer>> points = new HashMap<Integer, HashSet<Integer>>();

		for(int xc = currentTexture.getRegionX(); xc < currentTexture.getRegionX() + currentTexture.getRegionWidth(); xc++)
		{
			for(int yc = currentTexture.getRegionY(); yc < currentTexture.getRegionY() + currentTexture.getRegionHeight(); yc++)
			{
				int rbga = map.getPixel(xc, yc);

				Color color = new Color();
				Color.rgba8888ToColor(color, rbga);

				if(spec.isHitbox(color, xc, yc))
				{
					if(!points.containsKey(xc - currentTexture.getRegionX()))
						points.put(xc - currentTexture.getRegionX(), new HashSet<Integer>());
					
					HashSet<Integer> set = points.get(xc - currentTexture.getRegionX());
					
					set.add(yc - currentTexture.getRegionY());
				}
			}
		}
		
		if(data.disposePixmap())
			map.dispose();
		
		// Remove all points that have no adjascent points that are not part of the hitbox.
		for(Entry<Integer, HashSet<Integer>> set : points.entrySet())
		{
			int posX = set.getKey();
			
			Iterator<Integer> it = set.getValue().iterator();
			
			while(it.hasNext())
			{
				int posY = it.next();
				
				// Check if the north is empty.
				{
					HashSet<Integer> ys = null;
					
					for(Entry<Integer, HashSet<Integer>> ySet : points.entrySet())
					{
						if(ySet.getKey() != posX + 1)
							continue;
						
						ys = ySet.getValue();
					}
					
					if(ys == null)
						continue;

					if(!ys.contains(posY))
						continue;
				}
				
				// Check if the south is empty.
				{
					HashSet<Integer> ys = null;
					
					for(Entry<Integer, HashSet<Integer>> ySet : points.entrySet())
					{
						if(ySet.getKey() != posX - 1)
							continue;
						
						ys = ySet.getValue();
					}

					if(ys == null)
						continue;
					
					if(!ys.contains(posY))
						continue;
				}
				
				// Check if the east is empty.
				{
					if(!set.getValue().contains(posY + 1))
						continue;
				}
				
				// Check if the west is empty.
				{
					if(!set.getValue().contains(posY - 1))
						continue;
				}
				
				it.remove();
			}
		}

		ArrayList<Float> pointsList = new ArrayList<Float>();
		
		int lastX = 2;
		int lastY = 0;
		
		int lastDeltaX = 0;
		int lastDeltaY = 0;

		for(Entry<Integer, HashSet<Integer>> set : points.entrySet())
		{
			for(Integer integer : set.getValue())
			{
				lastX = set.getKey();
				lastY = integer;
				break;
			}
		}
		
		// Make a path for the polygon that connects the most sensible track. (So around the shape.)
		while(!points.isEmpty())
		{
			double closest = Integer.MAX_VALUE;
			
			int closestX = 0;
			int closestY = 0;
			int closestDeltaScore = Integer.MAX_VALUE;
			int closestDeltaX = 0;
			int closestDeltaY = 0;
			
			Point lastPoint = new Point(lastX, lastY);
			
			for(Entry<Integer, HashSet<Integer>> set : points.entrySet())
			{
				for(Integer integer : set.getValue())
				{
					int curX = set.getKey();
					int curY = integer;
					
					if(curX == lastX && curY == lastY)
						continue;
					
					int deltaX = curX - lastX;
					int deltaY = curY - lastY;
					
					double distance = MathUtil.getDistance((float)lastPoint.getX(), (float)lastPoint.getY(), curX, curY);
					
					if(distance < closest)
					{
						closest = distance;
						closestX = curX;
						closestY = curY;
						closestDeltaX = deltaX;
						closestDeltaY = deltaY;
						
						int deltaScore = 0;
						
						if(deltaX == lastDeltaX)
							deltaScore++;
						
						if(deltaY == lastDeltaY)
							deltaScore++;
						
						closestDeltaScore = deltaScore;
					}
					else if(distance == closest)
					{
						int deltaScore = 0;
						
						deltaScore += MathUtil.getDifference(lastDeltaX, deltaX);
						deltaScore += MathUtil.getDifference(lastDeltaY, deltaY);
						
						if(closestDeltaScore > deltaScore)
						{
							closest = distance;
							closestX = curX;
							closestY = curY;
							closestDeltaScore = deltaScore;
							closestDeltaX = deltaX;
							closestDeltaY = deltaY;
						}
					}
				}
			}
			
			pointsList.add((float)lastX);
			pointsList.add((float)lastY);
			
			{
				Iterator<Entry<Integer, HashSet<Integer>>> it = points.entrySet().iterator();
				
				while(it.hasNext())
				{
					Entry<Integer, HashSet<Integer>> set = it.next();
					
					if(set.getKey() != lastX)
						continue;
					
					HashSet<Integer> ySet = set.getValue();

					ySet.remove(lastY);

					if(ySet.isEmpty())
					{
						it.remove();
					}
				}
			}
			
			lastX = closestX;
			lastY = closestY;
			lastDeltaX = closestDeltaX;
			lastDeltaY = closestDeltaY;
		}
		
		float[] verticles = new float[pointsList.size()];

		int index = 0;
		
		for(Float flo : pointsList)
		{
			verticles[index] = flo;
			index++;
		}

		Polygon poly = new Polygon(verticles);
		
		poly.rotate(180F);
		
		return poly;
	}
	
	public static abstract class HitboxSpecification extends J2hObject
	{
		public abstract boolean isHitbox(Color color, int x, int y);
	}
	
	public static class StandardBulletSpecification extends HitboxSpecification
	{
		@Override
		public boolean isHitbox(Color color, int x, int y)
		{
			return color.a > 0.4;
		}
		
		private static StandardBulletSpecification instance = new StandardBulletSpecification();
		
		public static StandardBulletSpecification get()
		{
			return instance;
		}
	}
	
	public static Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
	
	public static Polygon loadHitbox(FileHandle handle)
	{
		float[] generalVertices = gson.fromJson(handle.readString(), float[].class);
		
		Polygon poly = new Polygon(generalVertices);
		
		Rectangle b = poly.getBoundingRectangle();
		
		poly.setOrigin(b.width / 2f, b.height / 2f);
		
		return poly;
	}
	
	public static Polygon rectangleToPolygon(Rectangle rect)
	{
		Polygon poly = new Polygon(new float[] { 
				0, 0,
				rect.getWidth(), 0,
				rect.getWidth(), rect.getHeight(),
				0, rect.getHeight()});
		
		return poly;
	}
	
	/**
	 * Creates a hitbox from the bounds of a texture region.
	 * @param region
	 * @return
	 */
	public static Polygon textureRegionPolygon(TextureRegion region)
	{
		Polygon poly = rectangleToPolygon(new Rectangle(0, 0, region.getRegionWidth(), region.getRegionHeight()));

		return poly;
	}
	
	public static Polygon rectangleHitbox(float radius)
	{
		radius *= 2;
		
		Polygon poly = new Polygon(new float[] { 
				0, 0,
				0, radius,
				radius, radius,
				radius, 0,});
		
		return poly;
	}
	
	private static Mesh mesh;
	
	public static void drawHitbox(Polygon hitbox)
	{
		J2hGame g = Game.getGame();
		
		if(hitbox == null)
			return;
		
		float[] vertices = hitbox.getTransformedVertices();
		
		if(vertices.length < 6)
			return;
		
		Gdx.gl.glLineWidth(3f);
		
		g.shape.flush();
		g.shape.setColor(Color.RED);
		g.shape.polygon(vertices);
	}
}
