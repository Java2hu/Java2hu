
package java2hu.util;

import java2hu.J2hGame;

public class Duration
{
	/**
	 * @return A duration with 0 milliseconds as value.
	 */
	public static Duration zero()
	{
		return milliseconds(0);
	}
	
	/**
	 * Convenience method to create a Duration with Unit milliseconds.
	 */
	public static Duration milliseconds(double milliseconds)
	{
		return new Duration(milliseconds, Unit.MILLISECOND);
	}

	/**
	 * Convenience method to create a Duration with Unit ticks.
	 */
	public static Duration ticks(double ticks)
	{
		return new Duration(ticks, Unit.TICK);
	}

	/**
	 * Convenience method to create a Duration with Unit seconds.
	 */
	public static Duration seconds(double seconds)
	{
		return new Duration(seconds, Unit.SECOND);
	}

	/**
	 * Convenience method to create a Duration with Unit minutes.
	 */
	public static Duration minutes(double minutes)
	{
		return new Duration(minutes, Unit.MINUTE);
	}

	/**
	 * Convenience method to create a Duration with Unit hours.
	 */
	public static Duration hours(double hours)
	{
		return new Duration(hours, Unit.HOUR);
	}
	
	public static enum Unit
	{
		MILLISECOND(1d), TICK((1000d / J2hGame.currentTPS)), SECOND(1000d), MINUTE(60000d), HOUR(3600000d);
		
		private final double milliseconds;

		Unit(double milliseconds)
		{
			this.milliseconds = milliseconds;
		}

		public double getMilliseconds()
		{
			return milliseconds;
		}
	}

	private final double milliseconds;

	public Duration(double value, Unit unit)
	{
		milliseconds = value * unit.getMilliseconds();
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(milliseconds);
	}

	/**
	 * @return The raw unrounded value of this duration in milliseconds.
	 */
	public double getValue()
	{
		return milliseconds;
	}
	
	/**
	 * @return how much rounded milliseconds this Duration consists of.
	 */
	public long toMilliseconds()
	{
		return (long) getValue();
	}

	/**
	 * @return how much rounded ticks this Duration consists of.
	 */
	public long toTicks()
	{
		return (long) getValue(Unit.TICK);
	}

	/**
	 * @return how much rounded seconds this Duration consists of.
	 */
	public long toSeconds()
	{
		return (long) getValue(Unit.SECOND);
	}

	/**
	 * @return how much rounded minutes this Duration consists of.
	 */
	public long toMinutes()
	{
		return (long) getValue(Unit.MINUTE);
	}

	/**
	 * @return how much rounded hours this Duration consists of.
	 */
	public long toHours()
	{
		return (long) getValue(Unit.HOUR);
	}

	/**
	 * Returns the value of the specific unit, unrounded.
	 */
	public double getValue(Unit unit)
	{
		return toMilliseconds() / unit.getMilliseconds();
	}
	
	public Duration add(Duration duration)
	{
		return Duration.milliseconds(this.milliseconds + duration.milliseconds);
	}
	
	public Duration subtract(Duration duration)
	{
		return Duration.milliseconds(this.milliseconds - duration.milliseconds);
	}
	
	public Duration divide(Duration duration)
	{
		return Duration.milliseconds(this.milliseconds / duration.milliseconds);
	}
	
	public Duration divide(double m)
	{
		return Duration.milliseconds(this.milliseconds / m);
	}
	
	public Duration multiply(Duration duration)
	{
		return Duration.milliseconds(this.milliseconds * duration.milliseconds);
	}
	
	public Duration multiply(double m)
	{
		return Duration.milliseconds(this.milliseconds * m);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Duration && getValue() == ((Duration) o).getValue();
	}
}
