package java2hu.plugin.sprites;

import java2hu.object.StageObject;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Fade a sprite in.
 */
public class FadeInSprite extends GetterPlugin<Sprite>
{
	private float increase;
	private float alpha = 0F;
	private float start;
	private float end;
	
	public FadeInSprite(Getter<Sprite> getter, float increase)
	{
		this(getter, 0F, 1F, increase);
	}
	
	public FadeInSprite(Getter<Sprite> getter, float start, float end, float increase)
	{
		super(getter);
		
		this.increase = increase;
		this.start = start;
		this.end = end;
		
		Sprite get = get();
		get.setAlpha(this.start);
	}
	
	@Override
	public void update(StageObject object, long tick)
	{
		if(alpha > end)
			return;
		
		Sprite get = get();
		get.setAlpha(alpha);
		
		alpha += increase;
	}
}
