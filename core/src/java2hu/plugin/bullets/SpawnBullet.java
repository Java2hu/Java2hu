package java2hu.plugin.bullets;

import java2hu.Game;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.plugin.GetterPlugin;
import java2hu.util.Getter;

public class SpawnBullet extends GetterPlugin<Bullet>
{
	private int interval;
	
	public SpawnBullet(Getter<Bullet> getter, int interval)
	{
		super(getter);
		
		this.interval = interval;
	}
	
	@Override
	public void update(StageObject object, long tick)
	{
		if(tick % interval == 0)
		{
			Bullet bullet = get();
			
			Game.getGame().spawn(bullet);
		}
	}
}
