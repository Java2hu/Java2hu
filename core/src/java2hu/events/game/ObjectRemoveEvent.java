package java2hu.events.game;

import java2hu.events.StageObjectCancellableEvent;
import java2hu.object.StageObject;

/**
 * Called when an object is removed from the playing field.
 * Cancelling this event will cause the object to remain on screen.
 */
public class ObjectRemoveEvent extends StageObjectCancellableEvent
{
	public ObjectRemoveEvent(StageObject object)
	{
		super(object);
	}
}
