package java2hu.events.pathing;

import java2hu.events.StageObjectEvent;
import java2hu.object.StageObject;
import java2hu.pathing.PathingHelper.Path;

/**
 * Super class for all pathing related events.
 */
public class PathingEvent extends StageObjectEvent
{
	private Path path;
	
	public PathingEvent(StageObject object, Path path)
	{
		super(object);
		this.path = path;
	}
	
	public Path getPath()
	{
		return path;
	}
}
