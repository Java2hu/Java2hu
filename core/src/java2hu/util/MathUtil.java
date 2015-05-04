package java2hu.util;

import java2hu.IPosition;
import java2hu.overwrite.J2hObject;

/**
 * Face it, nobody likes you math, so lets make it easier.
 */
public class MathUtil extends J2hObject
{
	public static float getAngle(IPosition a, IPosition b)
	{
		if(a == null || b == null)
			return 0;
		
		return getAngle((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
	}

	public static float getAngle(float xa, float ya, float xb, float yb)
	{
		float deltaX = xa - xb;
		float deltaY = ya - yb;
		
		double distanceSquared = deltaX * deltaX + deltaY * deltaY;
		double distance = Math.sqrt(distanceSquared);
		
		double x = deltaX / distance;
		double y = deltaY / distance;
		
		float degree = (float) (Math.atan2(y, x) * 180 / Math.PI);
		
		return degree;
	}
	
	public static float getDistance(float xa, float ya, float xb, float yb)
	{
		float deltaX = xa - xb;
		float deltaY = ya - yb;
		
		double distanceSquared = deltaX * deltaX + deltaY * deltaY;
		double distance = Math.sqrt(distanceSquared);
		
		return (float) distance;
	}
	

	public static float getDistance(IPosition from, IPosition to)
	{
		return getDistance(from.getX(), from.getY(), to.getX(), to.getY());
	}
	
	public static boolean inBoundary(float x, float y, float xMin, float xMax, float yMin, float yMax)
	{
		return x > xMin && x < xMax && y > yMin && y < yMax;
	}
	
	/**
	 * Turns any angle into => 0 & <= 360 (It's exactly the same after all, but easier to understand.)
	 * ie. 950 > 230
	 * @param angle
	 * @return
	 */
	public static float normalizeDegree(float angle)
	{
		if(angle < 0)
			angle += 360;
		
		// reduce the angle  
		angle =  angle % 360; 

		// force it to be the positive remainder, so that 0 <= angle < 360  
		angle = (angle + 360) % 360;  
		
		return angle;
	}
	
	/**
	 * Returns the difference between two numbers.
	 * Ie. difference between 10 and 15 = 5
	 * But also between -5 and -10 = 5
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getDifference(double a, double b)
	{
		return Math.abs(a-b);
	}
	
	public static class SinCosTable
	{
		public int precision; // gradations per degree
		private int modulus;
		private double[] sin;
		
		public SinCosTable(int decimals)
		{
			precision = 1;
			
			for(int i = 0; i < decimals; i++)
			{
				precision = precision * 10;
			}
			
			modulus = Math.round(360f*precision);
			
			sin = new double[modulus];
			
		    for (float i = 0; i < 360; i += 1f / precision)
		    {
		        sin[toArrayPosition(i)]=Math.sin(Math.toRadians(i));
		    }
		}
		
		public int toArrayPosition(double degree)
		{
			degree = normalizeDegree((float) degree);
			
			int pos = (int) Math.round(degree * precision);
			
			return pos;
		}
		
		// Private function for table lookup
		private double sinLookup(double deg)
		{
		    return sin[toArrayPosition(deg)];
		}

		// These are your working functions:
		public double sin(double a)
		{
		    return sinLookup(a);
		}
		public double cos(double a)
		{
		    return sinLookup(a + 90f);
		}
	}
	
	private static SinCosTable table = new SinCosTable(2);
	
	public static double fastSin(double degree)
	{
		return table.sin(degree);
	}
	
	public static double fastCos(double degree)
	{
		return table.cos(degree);
	}
}
