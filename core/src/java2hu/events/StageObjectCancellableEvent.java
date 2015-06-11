package java2hu.events;

import java2hu.object.StageObject;

public class StageObjectCancellableEvent extends CancellableEvent
{
	private StageObject object;

	public StageObjectCancellableEvent(StageObject object)
	{
		this.object = object;
	}
	
	public StageObject getObject()
	{
		return object;
	}
}
