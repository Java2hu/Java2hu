package java2hu.touhou.bullet;

import java.util.ArrayList;
import java.util.Collections;
import java2hu.util.MathUtil;

import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.Lists;

/**
 * Possible bullet colors for Touhou bullets
 */
public enum ThBulletColor
{
	BLACK(Color.BLACK), 
	RED_DARK(new Color(0.4f, 0f, 0f, 1f)), 
	RED(Color.RED),
	PURPLE(new Color(0.5f, 0f, 0.5f, 1f)), 
	PINK(Color.PINK), 
	BLUE_DARK(new Color(0,0,0.5f, 1f)), 
	BLUE(Color.BLUE), 
	CYAN(Color.CYAN), 
	CYAN_LIGHT(Color.CYAN), 
	GREEN(Color.GREEN), 
	GREEN_LIGHT(Color.GREEN), 
	GREEN_LIGHTER(Color.GREEN), 
	YELLOW(Color.YELLOW), 
	YELLOW_LIGHT(Color.YELLOW), 
	ORANGE(Color.ORANGE), 
	WHITE(Color.WHITE);

	Color color;
	
	private ThBulletColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Returns the ThBulletColor from the possible list, that is closest in color to the target
	 * @param possible
	 * @param target
	 * @return
	 */
	public static ThBulletColor getClosest(ArrayList<ThBulletColor> possible, Color target)
	{
		return getClosest(possible, null, target);
	}
	
	/**
	 * Returns the ThBulletColor from the possible list, that is closest in color to the target
	 * @param possible
	 * @param target
	 * @return
	 */
	public static ThBulletColor getClosest(ArrayList<ThBulletColor> possible, ThBulletColor target)
	{
		if(possible.contains(target))
			return target;
		
		return getClosest(possible, target, null);
	}
	
	
	/**
	 * Returns the ThBulletColor from the possible list, that is closest to targetBC OR targetC if targetBC is null.
	 * @return
	 */
	private static ThBulletColor getClosest(ArrayList<ThBulletColor> possible, ThBulletColor targetBC, Color targetC)
	{
		Color color = targetBC != null ? targetBC.getColor() : targetC;
		
		if(color == null)
			return possible.contains(ThBulletColor.WHITE) ? ThBulletColor.WHITE : possible.get(0);
		
		class Data implements Comparable<Data>
		{
			public Data(ThBulletColor from, Color to)
			{
				rDiff = MathUtil.getDifference(from.getColor().r, to.r);
				gDiff = MathUtil.getDifference(from.getColor().g, to.g);
				bDiff = MathUtil.getDifference(from.getColor().b, to.b);
				
				color = from;
			}
			
			double rDiff;
			double gDiff;
			double bDiff;
			
			ThBulletColor color;

			public double combinedValue()
			{
				return rDiff + gDiff + bDiff;
			}
			
			@Override
			public int compareTo(Data o)
			{
				final double d = combinedValue() - o.combinedValue();
				
				if(d < 0)
					return -1;
				else if(d > 0)
					return 1;
				else
					return 0;
			}
		}
		
		ArrayList<Data> datas = new ArrayList<>();
		
		for(ThBulletColor c : possible)
		{
			datas.add(new Data(c, color));
		}
		
		Collections.sort(datas);
		
		return datas.get(0).color;
	}
	
	/**
	 * List of possible break animation ThBulletColor's
	 */
	public static ArrayList<ThBulletColor> POSSIBLE_BREAK = Lists.newArrayList(WHITE, ORANGE, YELLOW_LIGHT, YELLOW, GREEN_LIGHTER, GREEN_LIGHT, GREEN, CYAN_LIGHT, CYAN, BLUE, BLUE_DARK, PINK, PURPLE, RED, RED_DARK, BLACK);
	
	/**
	 * Returns closest bullet color for the break animation.
	 * The bullet type being used is BALL_REFLECTING
	 */
	public ThBulletColor getBreakAnimationColor()
	{
		return getClosest(POSSIBLE_BREAK, this);
	}
	
	/**
	 * List of possible spawn animation ThBulletColor's
	 */
	public static ArrayList<ThBulletColor> POSSIBLE_SPAWN = Lists.newArrayList(WHITE, YELLOW, GREEN, CYAN, BLUE, PURPLE, RED, BLACK);
	
	/**
	 * Returns closest bullet color for spawn animations.
	 * The bullet type being used is ORB_MEDIUM
	 */
	public ThBulletColor getSpawnAnimationColor()
	{
		return getClosest(POSSIBLE_SPAWN, this);
	}
}
