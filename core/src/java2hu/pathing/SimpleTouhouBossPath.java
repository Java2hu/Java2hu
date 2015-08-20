package java2hu.pathing;

import java2hu.Game;
import java2hu.Position;
import java2hu.object.StageObject;
import java2hu.pathing.PathingHelper.Path;

import com.badlogic.gdx.math.Rectangle;

/**
 * Simple Touhou Boss Path.
 * Will follow the player inside a specified rectangle, roughly staying at the same x as the player.
 */
public class SimpleTouhouBossPath extends Path
{
	public SimpleTouhouBossPath(StageObject obj)
	{
		this(obj, 300, 200);
	}

	public static final int BARRIER_WIDTH = 100;
	public static final int BARRIER_HEIGHT = 100;
	
	/**
	 * Creates the default rectangle, with specified width.
	 * And expanding down from the top of the screen - {@value #BARRIER} for height.
	 * This path will follow the player's x coordinate.
	 */
	public SimpleTouhouBossPath(StageObject obj, float width, float height)
	{
		this(obj, getRectangle(width, height));
	}
	
	private static Rectangle getRectangle(float width, float height)
	{
		int minX = (int) Math.max(Game.getGame().getMinX() + BARRIER_WIDTH, Math.min(Game.getGame().getMaxX() - width - BARRIER_WIDTH, Game.getGame().getPlayer().getX() - (width / 2f)));
		
		int minY = (int) (Game.getGame().getMaxY() - BARRIER_HEIGHT - height);
		
		Rectangle box = new Rectangle(minX, minY, width, height);
		
		return box;
	}
	
	/**
	 * Create a simple boss path within the specified rectangle.
	 */
	public SimpleTouhouBossPath(StageObject obj, Rectangle rect)
	{
		super(obj, 200f);
		this.bounds = rect;
		
		double targetX = bounds.x + (Math.random() * bounds.width);
		double targetY = bounds.y + (Math.random() * bounds.height);
		
		addPosition(new Position(targetX, targetY));
		
		recalculate();
	}
	
	
	/**
	 * Create a simple boss path with a fixed movement rectangle centered around a point
	 */
	public SimpleTouhouBossPath(StageObject obj, Position center, float width, float height)
	{
		super(obj, 200f);
		
		float minX = center.getX() - (width / 2f);
		float minY = center.getY() - (height / 2f);
		
		Rectangle r = new Rectangle(minX, minY, width, height);
		
		this.bounds = r;
		
		double targetX = bounds.x + (Math.random() * bounds.width);
		double targetY = bounds.y + (Math.random() * bounds.height);
		
		addPosition(new Position(targetX, targetY));
		
		recalculate();
	}
	
	private Rectangle bounds;
	
	@Override
	public void tick()
	{
		super.tick();
	};
}
