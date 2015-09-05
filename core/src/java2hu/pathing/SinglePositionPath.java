package java2hu.pathing;

import java2hu.IPosition;
import java2hu.object.StageObject;
import java2hu.pathing.PathingHelper.Path;
import java2hu.util.Duration;

public class SinglePositionPath extends Path
{
	public SinglePositionPath(StageObject object, IPosition pos, Duration time)
	{
		super(object, time);
		addPosition(pos);
		recalculate();
	}
	
	public SinglePositionPath(StageObject object, IPosition pos, float speedPerSecond)
	{
		super(object, speedPerSecond);
		addPosition(pos);
		recalculate();
	}
}
