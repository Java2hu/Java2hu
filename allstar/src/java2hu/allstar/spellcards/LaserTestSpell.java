package java2hu.allstar.spellcards;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.StageObject;
import java2hu.object.bullet.Laser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.bullet.ThLaser;
import java2hu.touhou.bullet.ThLaserType;
import java2hu.util.MathUtil;

public class LaserTestSpell extends Spellcard
{
	public LaserTestSpell(StageObject owner)
	{
		super(owner);
	}

	@Override
	public void tick(int tick)
	{
		final J2hGame game = Game.getGame();
		final Player player = game.getPlayer();
		final Boss boss = (Boss) getOwner();
		
		if(tick == 0)
		{
			Laser l = new Laser(new ThLaser(ThLaserType.LIGHTNING), boss.getX(), boss.getY(), 2000)
			{
				{
					setUnitsPerPoint(10);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					float distance = MathUtil.getDistance(getX(), getY(), getGame().getPlayer().getX(), getGame().getPlayer().getY());

					float x = (getX() - getGame().getPlayer().getX()) / distance * 1.8F;
					float y = (getY() - getGame().getPlayer().getY()) / distance * 1.8F;
					
					x *= 4f;
					y *= 4f;

					setVelocityXTick(x);
					setVelocityYTick(y);
				}
				
				@Override
				public void checkCollision()
				{

				}
			};
			
			game.spawn(l);
		}
	}
}
