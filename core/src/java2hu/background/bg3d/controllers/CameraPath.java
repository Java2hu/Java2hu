package java2hu.background.bg3d.controllers;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.object.UpdateObject;

import com.badlogic.gdx.math.Vector3;

public class CameraPath extends UpdateObject
{
	public RotationPerspectiveCamera camera;
	public ArrayList<Vector3> positions = new ArrayList<Vector3>();
	// In movetime, put moveTime(1) for position(1) to position(2), moveTime(2) for position(2) to position(3), the last index is ignored.
	public ArrayList<Integer> moveTime = new ArrayList<Integer>();
	public ArrayList<Vector3> lookVector = new ArrayList<Vector3>();
	public ArrayList<Float> rotation = new ArrayList<Float>();
	
	public CameraPath(RotationPerspectiveCamera camera, Vector3 startPos, Vector3 startLookVector)
	{
		this(camera, startPos, startLookVector, 0);
	}
	
	public CameraPath(RotationPerspectiveCamera camera, Vector3 startPos, Vector3 startLookVector, float startRotation)
	{
		this.camera = camera;
		positions.add(startPos);
		lookVector.add(startLookVector);
		rotation.add(startRotation);
		
		camera.position.set(startPos);
		camera.lookAt(startLookVector);
		camera.setRotation(startRotation);
		camera.update();
	}
	
	private Vector3 lastPosition;
	private Vector3 lastLookVector;
	private float lastRotation = 0;
	
	/**
	 * Add a path location to the complete path.
	 * This method can take 'null' and it will take the last used value, or a default (new Vector3() or 0)
	 * @param pos
	 * @param lookVector
	 * @param rotation - Float so it can be null
	 * @param moveTime - Integer so it can be null
	 */
	public void addPath(Vector3 pos, Vector3 lookVector, Float rotation, Integer moveTime)
	{
		if(pos == null)
			pos = lastPosition;
		
		if(lookVector == null)
			lookVector = lastLookVector;
		
		if(rotation == null)
			rotation = lastRotation;
		
		this.positions.add(pos);
		this.moveTime.add(moveTime);
		this.lookVector.add(lookVector);
		this.rotation.add(rotation);
		
		lastPosition = pos.cpy();
		lastLookVector = lookVector.cpy();
		lastRotation = rotation;
		
		camera.position.set(lastPosition);
		camera.lookAt(lastLookVector);
	}
	
	public boolean isDone()
	{
		if(positions.size() <= index + 1)
			return true;
		
		if(moveTime.size() <= index)
			return true;
		
		if(lookVector.size() <= index + 1)
			return true;
		
		if(rotation.size() <= index + 1)
			return true;
		
		return false;
	}
	
	float nextCheckTime = 0;
	int index = 0;
	
	public void onUpdate(long tick)
	{
		if(Game.getGame().getTick() < nextCheckTime)
			return;
		
		if(isDone())
		{
			return;
		}
		
		float moveTime = (float)this.moveTime.get(index);
		
		float rotationFrom = this.rotation.get(index);
		float rotationTo = this.rotation.get(index + 1);
		float part = rotationTo - rotationFrom;
		part = part / moveTime;
		
		final Vector3 from = this.positions.get(index).cpy();
		final Vector3 fromLook = this.lookVector.get(index).cpy();
		
		final Vector3 to = this.positions.get(index + 1).cpy();
		final Vector3 toLook = this.lookVector.get(index + 1).cpy();
		
		final Vector3 cross = to.sub(from);
		final Vector3 step = new Vector3(cross.x / moveTime, cross.y / moveTime, cross.z / moveTime);
		
		final Vector3 crossLook = toLook.sub(fromLook);
		final Vector3 stepLook = new Vector3(crossLook.x / moveTime, crossLook.y / moveTime, crossLook.z / moveTime);
		
		for(int i = 0; i < moveTime; i++)
		{
			final Vector3 pos = new Vector3(step.x * (float)i, step.y * (float)i, step.z * (float)i);
			final Vector3 look = new Vector3(stepLook.x * (float)i, stepLook.y * (float)i, stepLook.z * (float)i);
			final float rotation = rotationFrom + (part * i);
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					camera.position.set(from.cpy().add(pos));
					setLookVector(fromLook.cpy().add(look));
					camera.setRotation(rotation);
					camera.update();
				}
			}, i);
		}
		
		nextCheckTime = Game.getGame().getTick() + moveTime;
		index++;
	}
	
	public void setLookVector(Vector3 vector)
	{
		camera.direction.set(vector);
	}
}
