package java2hu.background;

import java2hu.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class VerticalScrollingBackground extends ABackground
{
	protected Sprite sprite;
	protected float speed;
	protected float current = 0F;
	protected boolean topToBottom;
	
	public VerticalScrollingBackground(Sprite sprite, float speed, boolean topToBottom)
	{
		super();
		
		this.sprite = sprite;
		this.speed = speed;
		this.topToBottom = topToBottom;
	}

	@Override
	public void onDraw()
	{
		float y1 = topToBottom ? Game.getGame().getMinY() - current : Game.getGame().getMinY() + current;
		float y2 = topToBottom ? Game.getGame().getMinY() + Game.getGame().getMaxY() - current : Game.getGame().getMinY() - Game.getGame().getMaxY() + current;  
		
		sprite.setBounds(Game.getGame().getMinX(), y1, Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		sprite.setBounds(Game.getGame().getMinX(), y2, Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
	}

	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		current += speed;
		
		if(current > Game.getGame().getMaxY())
			current = 0;
	}
}
