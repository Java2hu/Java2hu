package java2hu.events.pathing;

import java2hu.events.StageObjectEvent;
import java2hu.object.StageObject;
import java2hu.pathing.PathingHelper.Path;

/**
 * Called when a path has been finished for an object.
 */
public class PathingFinishEvent extends StageObjectEvent
{
	private Path path;
	
	public PathingFinishEvent(StageObject object, Path path)
	{
		super(object);
		this.path = path;
	}
	
	public Path getPath()
	{
		return path;
	}
}
