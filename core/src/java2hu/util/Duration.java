
package java2hu.util;

import java2hu.J2hGame;

public class Duration
{
	public static Duration ZERO = milliseconds(0);
	
	public static enum Unit
	{
		MILLISECOND(1d), TICK((1000d / J2hGame.LOGIC_TPS)), SECOND(1000d), MINUTE(60000d), HOUR(3600000d);
		
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

	public double getValue()
	{
		return milliseconds;
	}
	
	public int toMilliseconds()
	{
		return (int) getValue();
	}

	public int toTicks()
	{
		return (int) getValue(Unit.TICK);
	}

	public long toSeconds()
	{
		return getValue(Unit.SECOND);
	}

	public long toMinutes()
	{
		return getValue(Unit.MINUTE);
	}

	public long toHours()
	{
		return getValue(Unit.HOUR);
	}

	public long getValue(Unit unit)
	{
		return (long) (toMilliseconds() / unit.getMilliseconds());
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
	
	public Duration multiply(Duration duration)
	{
		return Duration.milliseconds(this.milliseconds * duration.milliseconds);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Duration && toMilliseconds() == ((Duration) o).toMilliseconds();
	}

	public static Duration milliseconds(double milliseconds)
	{
		return new Duration(milliseconds, Unit.MILLISECOND);
	}

	public static Duration ticks(double ticks)
	{
		return new Duration(ticks, Unit.TICK);
	}

	public static Duration seconds(double seconds)
	{
		return new Duration(seconds, Unit.SECOND);
	}

	public static Duration minutes(double minutes)
	{
		return new Duration(minutes, Unit.MINUTE);
	}

	public static Duration hours(double hours)
	{
		return new Duration(hours, Unit.HOUR);
	}
}
