package java2hu.background.bg3d.controllers;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class RotationPerspectiveCamera extends PerspectiveCamera
{
	float rotation = 0;
	
	public RotationPerspectiveCamera()
	{
		super();
	}
	
	public RotationPerspectiveCamera(float fieldOfViewY, float viewportWidth, float viewportHeight)
	{
		super(fieldOfViewY, viewportWidth, viewportHeight);
	}
	@Deprecated
	@Override
	public void rotate(float angle, float axisX, float axisY, float axisZ)
	{

	}
	
	@Deprecated
	@Override
	public void rotate(Matrix4 transform)
	{

	}
	
	@Deprecated
	@Override
	public void rotate(Quaternion quat)
	{

	}
	
	@Deprecated
	@Override
	public void rotate(Vector3 axis, float angle)
	{

	}
	
	@Deprecated
	@Override
	public void rotateAround(Vector3 point, Vector3 axis, float angle)
	{
		
	}
	
	public void setRotation(float angle)
	{
		super.rotate(new Vector3(1, 1, 1), -rotation);
		super.rotate(new Vector3(1, 1, 1), angle);
		
		rotation = angle;
	}
}
