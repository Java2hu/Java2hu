package java2hu.object.bullet;


public class StationaryLaser extends LaserDrawer
{
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	
	public StationaryLaser(LaserAnimation ani, float thickness, float hitboxThickness)
	{
		this(ani, thickness, hitboxThickness, 0, 0, 0, 0);
	}
	
	public StationaryLaser(LaserAnimation ani, float thickness, float hitboxThickness, float startX, float startY, float endX, float endY)
	{
		super(ani, thickness, hitboxThickness);
		
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}
	
	@Override
	public void update(long tick)
	{
		super.update(tick);
		
		getPoints().clear();
		
		addPoint(getStartX(), getStartY());
		addPoint(getEndX(), getEndY());
	}
	
	@Override
	public boolean doDelete()
	{
		return false;
	}
	
	public void setStartX(float startX)
	{
		this.startX = startX;
	}
	
	public float getStartX()
	{
		return startX;
	}
	
	public void setStartY(float startY)
	{
		this.startY = startY;
	}
	
	public float getStartY()
	{
		return startY;
	}
	
	public void setStart(float x, float y)
	{
		setStartX(x);
		setStartY(y);
	}
	
	public void setEndX(float endX)
	{
		this.endX = endX;
	}
	
	public float getEndX()
	{
		return endX;
	}
	
	public void setEndY(float endY)
	{
		this.endY = endY;
	}
	
	public float getEndY()
	{
		return endY;
	}
	
	public void setEnd(float x, float y)
	{
		setEndX(x);
		setEndY(y);
	}
	
	public void setDirectionDeg(float x, float y, float degree, float size)
	{
		setDirectionRads(x, y, (float) Math.toRadians(degree), size);
	}
	
	public void setDirectionRads(float x, float y, float rad, float size)
	{
		setStart(x, y);
		setEnd((float) (x + (Math.cos(rad) * size)), (float) (y + (Math.sin(rad) * size)));
	}
}
