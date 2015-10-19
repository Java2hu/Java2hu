package java2hu.plugin.sprites;

import java2hu.object.StageObject;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class RotatingSprite extends GetterPlugin<Sprite>
{
	private float rotationPerSecond;
	
	public RotatingSprite(Getter<Sprite> getter, float rotationPerSecond)
	{
		super(getter);
		
		this.rotationPerSecond = rotationPerSecond;
	}
	
	@Override
	public void update(StageObject object, float delta)
	{
		get().rotate(rotationPerSecond * delta);
	}
	
	public float getRotationPerSecond()
	{
		return rotationPerSecond;
	}
	
	public void setRotationPerSecond(float rotationPerSecond)
	{
		this.rotationPerSecond = rotationPerSecond;
	}
}
