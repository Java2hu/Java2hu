package java2hu.plugin.sprites;

import java2hu.object.StageObject;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Scales a sprite
 */
public class ScalingSprite extends GetterPlugin<Sprite>
{
	private float start;
	private float end;
	private float speed;
	private float current = Float.NaN;
	
	public ScalingSprite(Getter<Sprite> getter, float start, float end, float speed)
	{
		super(getter);
		
		this.start = start;
		this.end = end;
		this.speed = speed;
	}
	
	@Override
	public void update(StageObject object, long tick)
	{
		Sprite sprite = get();
		
		if(Float.isNaN(current))
		{
			sprite.setScale(start);
			current = start;
		}
		else if(end < start)
		{
			if(current > end)
			{
				current = current - speed;
				sprite.setScale(current);
			}
			else
			{
				sprite.setScale(end);
				return;
			}
		}
		else if(end > start)
		{
			if(current < end)
			{
				current = current + speed;
				sprite.setScale(current);
			}
			else
			{
				sprite.setScale(end);
				return;
			}
		}
	}
}
