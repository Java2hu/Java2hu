package java2hu.plugin.sprites;

import java2hu.object.StageObject;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Fade a sprite out.
 */
public class FadeOutSprite extends GetterPlugin<Sprite>
{
	private float decrease;
	private float alpha = 0F;
	private float start;
	private float end;
	
	public FadeOutSprite(Getter<Sprite> getter, float increase)
	{
		this(getter, 1F, 0F, increase);
	}
	
	public FadeOutSprite(Getter<Sprite> getter, float start, float end, float decrease)
	{
		super(getter);
		
		this.decrease = decrease;
		this.start = start;
		this.end = end;
		
		Sprite get = get();
		get.setAlpha(this.start);
	}
	
	@Override
	public void update(StageObject object, long tick)
	{
		if(alpha < end)
			return;
		
		Sprite get = get();
		get.setAlpha(alpha);
		
		alpha -= decrease;
	}
}
