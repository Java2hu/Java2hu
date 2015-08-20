package java2hu.util;

import java.util.LinkedList;

/**
 * A number that has a base value which is then manipulated by a set of {@link Manipulator}
 */
public class InfluencableNumber<T extends Number> extends Number
{
	public InfluencableNumber(T t)
	{
		setBaseValue(t);
	}
	
	private LinkedList<Manipulator> list = new LinkedList<InfluencableNumber.Manipulator>();
	
	public void manipulate(Manipulator man)
	{
		list.add(man);
	}
	
	/**
	 * Returns the list of manipulators, you can change orders and stuff here.
	 * @return
	 */
	public LinkedList<Manipulator> getManipulators()
	{
		return list;
	}
	
	private T base;
	
	/**
	 * Sets the base value of this number
	 */
	public void setBaseValue(T base)
	{
		this.base = base;
	}
	
	public static interface Manipulator
	{
		/**
		 * @param num - The current value in the calculation chain, possibly altered by other Manipulators.
		 * @return The new current value after manipulation.
		 */
		public Number manipulate(Number num);
	}
	
	public static class Multiplier implements Manipulator
	{
		/**
		 * A manipulator that multiplies the base.
		 * Uses a float, use {@link DMultiplier} for more precision 
		 */
		public Multiplier(float multiplier)
		{
			this.multiplier = multiplier;
		}
		
		private float multiplier = 1f;

		@Override
		public Number manipulate(Number num)
		{
			return num.doubleValue() * multiplier;
		}
	}
	
	public static class DMultiplier implements Manipulator
	{
		/**
		 * A manipulator that multiplies the base.
		 * Uses a float, use {@link DoubleMultiplier} for more precision 
		 */
		public DMultiplier(double multiplier)
		{
			this.multiplier = multiplier;
		}
		
		private double multiplier = 1d;

		@Override
		public Number manipulate(Number num)
		{
			return num.doubleValue() * multiplier;
		}
	}

	@Override
	public double doubleValue()
	{
		double value = base.doubleValue();
		
		for(Manipulator m : list)
		{
			value = m.manipulate(value).doubleValue();
		}
		
		return value;
	}

	@Override
	public float floatValue()
	{
		float value = base.floatValue();
		
		for(Manipulator m : list)
		{
			value = m.manipulate(value).floatValue();
		}
		
		return value;
	}

	@Override
	public int intValue()
	{
		int value = base.intValue();
		
		for(Manipulator m : list)
		{
			value = m.manipulate(value).intValue();
		}
		
		return value;
	}

	@Override
	public long longValue()
	{
		long value = base.longValue();
		
		for(Manipulator m : list)
		{
			value = m.manipulate(value).longValue();
		}
		
		return value;
	}
}
