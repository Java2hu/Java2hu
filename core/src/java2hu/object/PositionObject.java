package java2hu.object;

public class PositionObject extends StageObject
{
	public PositionObject(float x, float y)
	{
		super(x, y);
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

	@Override
	public void onDraw()
	{
	}

	@Override
	public void onUpdate(long tick)
	{
	}
}
