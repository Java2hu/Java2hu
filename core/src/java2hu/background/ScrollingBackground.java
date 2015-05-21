package java2hu.background;

import java2hu.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class ScrollingBackground extends ABackground
{
	protected Sprite sprite;
	protected float x = Game.getGame().getMinX();
	protected float y = Game.getGame().getMinY();
	protected float xChange;
	protected float yChange;
	
	/**
	 * An unidirectional moving background, this is a lot more intensive
	 * than the other backgrounds, because this spawns a 3x3 field to cover all the movement, in comparison to 2 for the others
	 * @param sprite
	 * @param xChange
	 * @param yChange
	 */
	public ScrollingBackground(Sprite sprite, float xChange, float yChange)
	{
		super();
		
		this.sprite = sprite;
		this.xChange = xChange;
		this.yChange = yChange;
	}

	@Override
	public void onDraw()
	{
		boolean left = xChange < 0;
		boolean bottom = yChange < 0;
		
		// (0, 0)
		sprite.setBounds(x, y, Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (0, -1)
		sprite.setBounds(x, y - (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (0, 1)
		sprite.setBounds(x, y + (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (-1, 0)
		sprite.setBounds(x - (Game.getGame().getMaxX()), y, Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (1, 0)
		sprite.setBounds(x + (Game.getGame().getMaxX()), y, Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (-1, -1)
		sprite.setBounds(x - (Game.getGame().getMaxX()), y - (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (-1, 1)
		sprite.setBounds(x - (Game.getGame().getMaxX()), y + (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
		
		// (1, 1)
		sprite.setBounds(x + (Game.getGame().getMaxX()), y + (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);

		// (1, -1)
		sprite.setBounds(x + (Game.getGame().getMaxX()), y - (Game.getGame().getMaxY()), Game.getGame().getMaxX() - Game.getGame().getMinX(), Game.getGame().getMaxY() - Game.getGame().getMinY());
		sprite.draw(Game.getGame().batch);
	}

	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		x += xChange;
		y += yChange;
		
		if(x < Game.getGame().getMinX())
			x = Game.getGame().getMaxX();
		else if(x > Game.getGame().getMaxX())
			x = Game.getGame().getMinX();
		
		if(y < Game.getGame().getMinY())
			y = Game.getGame().getMaxY();
		else if(y > Game.getGame().getMaxY())
			y = Game.getGame().getMinY();
	}
}
