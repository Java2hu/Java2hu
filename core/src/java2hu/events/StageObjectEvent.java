package java2hu.events;

import java2hu.object.StageObject;

public class StageObjectEvent extends Event
{
	private StageObject object;

	public StageObjectEvent(StageObject object)
	{
		this.object = object;
	}
	
	public StageObject getObject()
	{
		return object;
	}
}
