package java2hu.system;

import java2hu.overwrite.J2hObject;

public class SaveableObject<T> extends J2hObject
{
	private T object;
	
	public SaveableObject()
	{
		
	}
	
	public SaveableObject(T t)
	{
		setObject(t);
	}

	public void setObject(T t)
	{
		object = t;
	}
	
	public T getObject()
	{
		return object;
	}
}
