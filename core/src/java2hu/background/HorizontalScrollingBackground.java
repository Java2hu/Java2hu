package java2hu.background;

import java2hu.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Use {@link Background} instead.
 */
@Deprecated
public class HorizontalScrollingBackground extends ABackground
{
	public Sprite sprite;
	private float speed;
	private float current = 0F;
	private boolean leftToRight;
	
	public HorizontalScrollingBackground(Sprite sprite, float speed, boolean leftToRight)
	{
		super();
		
		this.sprite = sprite;
		this.speed = speed;
		this.leftToRight = leftToRight;
	}

	@Override
	public void onDraw()
	{
		float x1 = leftToRight ? Game.getGame().getMinX() + current : Game.getGame().getMinX() - current;
		float x2 = leftToRight ? Game.getGame().getMinX() - Game.getGame().getMaxX() + current : Game.getGame().getMinX() + Game.getGame().getMaxX() - current;  
		
		sprite.setBounds(x1, Game.getGame().getMinY(), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		sprite.setBounds(x2, Game.getGame().getMinY(), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
	}

	@Override
	public void onUpdate(long tick)
	{
		current += speed;
		
		if(current > Game.getGame().getMaxX())
			current = 0;
	}
}
