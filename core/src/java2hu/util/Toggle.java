package java2hu.util;

public class Toggle
{
	private boolean value;
	
	public Toggle()
	{
		this(false); // Standard boolean value
	}
	
	public Toggle(boolean state)
	{
		value = state;
	}
	
	public boolean getValue()
	{
		return value;
	}
	
	public void setValue(boolean value)
	{
		this.value = value;
	}
	
	public boolean toggle()
	{
		value = !value;
		
		return getValue();
	}
}
