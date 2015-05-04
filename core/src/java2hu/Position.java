package java2hu;


public class Position implements IPosition
{
	private float x;
	private float y;
	
	public Position(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Position(IPosition pos)
	{
		this(pos.getX(), pos.getY());
	}

	public Position(double x, double y)
	{
		this((float)x, (float)y);
	}

	@Override
	public float getX() 
	{
		return x;
	}
	
	@Override
	public float getY()
	{
		return y;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public Position sub(Position pos)
	{
		add(new Position(-pos.getX(), -pos.getY()));
		
		return this;
	}
	
	public Position add(Position pos)
	{
		if(Float.isNaN(pos.getX()) || Float.isNaN(pos.getY()))
		{
			return this;
		}
		
		setX(getX() + pos.getX());
		setY(getY() + pos.getY());
		
		return this;
	}
	
	public Position div(float dividant)
	{
		setX(getX() / dividant);
		setY(getY() / dividant);
		
		return this;
	}
	
	public Position div(Position pos)
	{
		setX(getX() / pos.getX());
		setY(getY() / pos.getY());
		
		return this;
	}
	
	public Position mul(double dividant)
	{
		setX(getX() * (float)dividant);
		setY(getY() * (float)dividant);
		
		return this;
	}
	
	public Position mul(Position pos)
	{
		setX(getX() * pos.getX());
		setY(getY() * pos.getY());
		
		return this;
	}
	
	@Override
	public Position clone()
	{
		Position newPos = new Position(this);
		
		return newPos;
	}
}
