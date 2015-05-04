package java2hu.touhou.bullet;


public enum ThLaserType
{
	NORMAL(10, 5), LIGHTNING(20, 8);
	
	private float thickness = 0;
	private float hitboxThickness = 0;

	private ThLaserType(float thickness, float hitboxThickness)
	{
		this.thickness = thickness;
		this.hitboxThickness = hitboxThickness;
	}
	
	public float getThickness()
	{
		return thickness;
	}

	public float getHitboxThickness()
	{
		return hitboxThickness;
	}
}
