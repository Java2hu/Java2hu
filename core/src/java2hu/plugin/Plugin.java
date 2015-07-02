package java2hu.plugin;

import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;

/**
 * A plugin is a class that can be added to a StageObject, and will run the update method on every tick.
 */
public abstract class Plugin<T extends StageObject> extends J2hObject
{
	public abstract void update(T object, long tick);
	
	public boolean isPersistant()
	{
		return false;
	}
	
	private boolean delete = false;
	
	public void delete()
	{
		delete = true;
	}
	
	public boolean doDelete()
	{
		return delete;
	}

	public void onDelete()
	{
		
	}
}
