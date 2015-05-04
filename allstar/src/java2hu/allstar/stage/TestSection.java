package java2hu.allstar.stage;

import java2hu.Game;
import java2hu.Position;
import java2hu.allstar.AllStarStageScheme;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.bullet.Bullet;
import java2hu.object.enemy.Enemy;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.util.MathUtil;
import java2hu.util.PathUtil;
import java2hu.util.PathUtil.PathTask;

public class TestSection implements SpecialFlowScheme<AllStarStageScheme>
{
	@Override
	public void executeFight(AllStarStageScheme scheme)
	{
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				final Enemy enemy = new Enemy(null, 10, 0, 0);
				final Position playerPos = new Position(Game.getGame().getPlayer());
				
				PathTask task = PathUtil.moveTo(enemy, 100, 0, 60);
				task.setOnDone(new Runnable()
				{
					@Override
					public void run()
					{
						Bullet bullet = new Bullet(new ThBullet(ThBulletType.ARROW, ThBulletColor.RED), enemy.getX(), enemy.getY());
						bullet.setDirectionDegTick(MathUtil.getAngle(bullet, playerPos), 5f);
						
						PathTask task = PathUtil.moveTo(enemy, -10, 0, 70);
						task.setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								Bullet bullet = new Bullet(new ThBullet(ThBulletType.ARROW, ThBulletColor.RED), enemy.getX(), enemy.getY());
								bullet.setDirectionDegTick(MathUtil.getAngle(bullet, Game.getGame().getPlayer()), 5f);
							}
						});
					}
				});
			}
		}, 1);
	}
}
