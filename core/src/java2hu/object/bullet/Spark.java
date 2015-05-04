package java2hu.object.bullet;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;

public class Spark extends Bullet
{
	public Spark(Animation animation, float x, float y)
	{
		super(animation, x, y);
	}
	
	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		HitboxSprite current = getCurrentSprite();
		
		current.setPosition(getX(), getY());
		current.getHitbox().setPosition(getX(), getY());
		current.draw(g.batch);
	}
	
	@Override
	public void onUpdate(long tick)
	{
		J2hGame g = Game.getGame();
		
		checkCollision();
		
		this.setX(getX() - velocityX);
		this.setY(getY() - velocityY);
		
		if(doDelete())
			g.delete(this);
		
		super.onUpdate(tick);
	}
	
	public boolean doDelete()
	{
		return false;
	}
	
	public void checkCollision()
	{
		J2hGame g = Game.getGame();
		
		if(getHitbox() == null)
			return;
		
		if(Intersector.overlapConvexPolygons(g.getPlayer().getHitbox(), getHitbox()))
		{
			g.getPlayer().onHit(this);
			onHit();
			g.delete(this);
		}
	}
}
