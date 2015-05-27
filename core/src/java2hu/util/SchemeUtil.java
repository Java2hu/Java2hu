package java2hu.util;

import java2hu.gameflow.GameFlowScheme;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;
import java2hu.plugin.Plugin;
import java2hu.system.SaveableObject;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.graphics.Color;

public class SchemeUtil extends J2hObject
{
	/**
	 * Spawns an object and waits for it to spawn.
	 * @param scheme
	 * @param getter
	 */
	public static void spawn(GameFlowScheme scheme, final Getter<StageObject> getter)
	{
		final SaveableObject<StageObject> bar = new SaveableObject<StageObject>();
		
		game.addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				bar.setObject(getter.get());
				
				game.spawn(bar.getObject());
			}
		}, 1);
		
		scheme.wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return bar.getObject() == null;
			}
		});
	}
	
	/**
	 * Waits for the passed living object to die. (reach <= 0 hp)
	 * @param obj
	 */
	public static void waitForDeath(GameFlowScheme scheme, final LivingObject obj)
	{
		scheme.wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return !obj.isDead();
			}
		});
	}
	
	/**
	 * Waits for the passed living object to die. (reach <= 0 hp)
	 * And then plays a death animation, where the object moves to the left and explodes after a few seconds.
	 * @param obj
	 */
	public static void deathAnimation(GameFlowScheme scheme, final LivingObject obj, final Color color)
	{
		final int ticksWait = 60;
		
		obj.addEffect(new Plugin<StageObject>()
		{
			long spawnTick = 0;
			
			float x = (float) (1f * ((2 * Math.random()) - 1));
			float y = (float) (1f * ((2 * Math.random()) - 1));
			
			@Override
			public void update(StageObject object, long tick)
			{
				if(spawnTick == 0)
				{
					TouhouSounds.Enemy.EXPLOSION_2.play(0.5f);
					spawnTick = tick;
				}
				
				tick -= spawnTick;
				
				if(tick == ticksWait)
				{
					BossUtil.charge(object, color, true);
					TouhouSounds.Enemy.EXPLOSION_2.play(0.5f);
				}
				
				if(tick >= ticksWait)
					return;
				
				object.setX(object.getX() + x);
				object.setY(object.getY() + y);
			}
		});
		
		scheme.waitTicks(ticksWait);
	}
}
