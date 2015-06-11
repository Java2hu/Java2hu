package java2hu.events.game;

import java2hu.events.StageObjectCancellableEvent;
import java2hu.object.StageObject;

/**
 * Called when an object is spawned to the playing field.
 * Cancelling this event will cause the object to be refused to spawn.
 */
public class ObjectSpawnEvent extends StageObjectCancellableEvent
{
	public ObjectSpawnEvent(StageObject object)
	{
		super(object);
	}
}
