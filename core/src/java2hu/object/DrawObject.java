package java2hu.object;



public abstract class DrawObject extends StageObject
{
	public DrawObject()
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
