package java2hu.background;

import java2hu.object.StageObject;

public abstract class Background extends StageObject
{
	public Background()
	{
		super(0, 0);
	}
	
	@Override
	public void onUpdate(long tick)
	{
	}

	@Override
	public float getWidth()
	{
		return 0;
	}

	@Override
	public float getHeight()
	{
		return 0;
	}
}
