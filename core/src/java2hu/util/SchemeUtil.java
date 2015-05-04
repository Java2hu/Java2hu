package java2hu.util;

import java2hu.gameflow.GameFlowScheme;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;
import java2hu.system.SaveableObject;

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
}
