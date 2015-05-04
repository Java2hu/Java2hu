package java2hu;

import java2hu.overwrite.J2hObject;

/**
 * Timer that goes from a constant speed to a (de)acceleration after specified points before turning back
 * Very useful for things that swing, but looks unnatural because the velocity suddenly shifts. (ie. Sukuna's bowl)
 */
public class SmartTimer extends J2hObject
{
	boolean countingUp = true;
	
	float speed = 0.2f;
	float slowPointMax = 25f;
	float max = 30;
	float min = -30;
	float slowPointMin = -25f;
	float slowSpeed = 0.05f;
	
	float lastTimer = 0f;
	float timer = 0f;
	
	public SmartTimer(float speed, float min, float max)
	{
		this(speed, min, min * 0.75f, max, max * 0.75f, speed);
	}
	
	public SmartTimer(float speed, float min, float slowPointMin, float max, float slowPointMax, float slowSpeed)
	{
		this.speed = speed;
		this.min = min;
		this.slowPointMin = slowPointMin;
		this.max = max;
		this.slowPointMax = slowPointMax;
		this.slowSpeed = slowSpeed;
		
		timer = min;
	}
	
	public float getTimer()
	{
		return timer;
	}
	
	public void setTimer(float timer)
	{
		this.timer = timer;
	}
	
	/**
	 * Get difference between last tick and this tick.
	 * @return
	 */
	public float getDeltaTimer()
	{
		return timer - lastTimer;
	}
	
	public void reset()
	{
		timer = min;
	}
	
	public void tick()
	{
		lastTimer = timer;
		
		if(countingUp)
		{
			if(timer > slowPointMax)
			{
				float multiplier = (speed - slowSpeed) / (max - slowPointMax);

				timer += Math.max(speed - (max - slowPointMax - (max - timer)) * multiplier, slowSpeed);
			}
			else if(timer < slowPointMin)
			{
				float multiplier = (speed - slowSpeed) / (slowPointMin - min);

				timer += Math.max(speed - (slowPointMin - min - (timer - min)) * multiplier, slowSpeed);
			}
			else
				timer += speed;

			if(timer > max)
				countingUp = false;
		}
		else
		{
			if(timer < slowPointMin)
			{
				float multiplier = (speed - slowSpeed) / (slowPointMin - min);

				timer -= Math.max(speed - (slowPointMin - min - (timer - min)) * multiplier, slowSpeed);
			}
			else if(timer > slowPointMax)
			{
				float multiplier = (speed - slowSpeed) / (max - slowPointMax);

				timer -= Math.max(speed - (max - slowPointMax - (max - timer)) * multiplier, slowSpeed);
			}
			else
				timer -= speed;
			
			if(timer < min)
				countingUp = true;
		}
	}
	
	public boolean isCountingUp()
	{
		return countingUp;
	}
	
	public float getMax()
	{
		return max;
	}
	
	public void setMax(float max)
	{
		this.max = max;
	}
	
	public float getSlowPointMax()
	{
		return slowPointMax;
	}
	
	public void setSlowPointMax(float slowPointMax)
	{
		this.slowPointMax = slowPointMax;
	}
	
	public float getMin()
	{
		return min;
	}
	
	public void setMin(float min)
	{
		this.min = min;
	}
	
	public float getSlowPointMin()
	{
		return slowPointMin;
	}
	
	public void setSlowPointMin(float slowPointMin)
	{
		this.slowPointMin = slowPointMin;
	}
	
	public float getSlowSpeed()
	{
		return slowSpeed;
	}
	
	public void setSlowSpeed(float slowSpeed)
	{
		this.slowSpeed = slowSpeed;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}
}
