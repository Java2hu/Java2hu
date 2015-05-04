
package java2hu.allstar.spellcards;

import java.util.Random;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.object.bullet.Laser;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.bullet.ThLaser;
import java2hu.touhou.bullet.ThLaserColor;
import java2hu.touhou.bullet.ThLaserType;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;

import shaders.ShaderLibrary;

/**
 * Remake of Shou's curving lasers in her most notable spell card.
 * 2 sets of curving lasers spawn from shou and curve outwards, then move straight downwards
 * Then a few circles of BALL_LARGE bullets are spawns from the boss outwards, getting to the lower part of the screen at almost the same time as the curving lasers.
 * This is a good example if you want to use curving lasers.
 */
public class ShouCurvingNonSpell extends Spellcard
{
	public ShouCurvingNonSpell(StageObject owner)
	{
		super(owner);
	}

	@Override
	public void tick(int tick)
	{
		final J2hGame game = Game.getGame();
		final Player player = game.getPlayer();
		final Boss boss = (Boss) getOwner();
		
		if(tick >= 60)
		{
			tick -= 60;
		}
		else
			return;
		
		float chance = 1f / (ThLaserColor.values().length + 1);
		
		boolean lightning = Math.random() < chance;
		
		ThLaserType type = lightning ? ThLaserType.LIGHTNING : ThLaserType.NORMAL;
		ThLaserColor sub = lightning ? ThLaserColor.LIGHTNING : ThLaserColor.values()[new Random().nextInt(ThLaserColor.values().length)];
		
		float total = 200;
		
		if(tick % total == 160)
			BossUtil.moveAroundRandomly((Boss)getOwner(), (int) (getGame().getMaxX() / 2) - 80, (int)(getGame().getMaxX() / 2) + 80, Game.getGame().getHeight() - 100, Game.getGame().getHeight() - 300, 500);
		
		if(tick % total == 30)
		{
			TouhouSounds.Enemy.LAZER_1.play();
			
			for(int i = 350; i > 200; i -= 10)
			{
				final float finalAngle = i;

				game.spawn(new Laser(new ThLaser(sub == ThLaserColor.LIGHTNING ? ThLaserType.LIGHTNING : type, sub), boss.getX(), boss.getY(), 500)
				{
					boolean left = false;
					float angle = finalAngle;
					int slowTicks = 0;

					{
						setLengthOnScreen(-1);
						setMaxPoints(30);
						setUnitsPerPoint(5);
						setZIndex(boss.getZIndex() - 1);
					}

					@Override
					public void onUpdate(long tick)
					{
						float speed = 15f;
						
						float threshold = finalAngle + (left ? -1 : 1) * 180f;
						
						if(left ? angle > threshold : angle < threshold)
						{
							speed = 10f;
							
							angle = angle + (left ? -1 : 1) * 5f;
							
							if(left ? angle <= threshold : angle >= threshold)
							{
								slowTicks = 45;
							}
						}

						setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
						super.onUpdate(tick);
						
						if(slowTicks > 0)
							slowTicks--;
					}
				});
			}
		}
		
		if(tick % total == 0)
		{	
			TouhouSounds.Enemy.LAZER_1.play();
			
			for(int i = 340; i > 180; i -= 10)
			{
				final float finalAngle = i;

				game.spawn(new Laser(new ThLaser(type, sub), boss.getX(), boss.getY(), 500)
				{
					boolean left = true;
					float angle = finalAngle;
					int slowTicks = 0;

					{
						setLengthOnScreen(-1);
						setMaxPoints(30);
						setUnitsPerPoint(5);
						setZIndex(boss.getZIndex() - 1);
					}

					@Override
					public void onUpdate(long tick)
					{
						float speed = 10f;
						
						float threshold = finalAngle + (left ? -1 : 1) * 180f;
						
						if(angle > threshold)
						{
							speed = 10f;
							
							angle = angle + (left ? -1 : 1) * 5f;
							
							if(angle <= threshold)
							{
								slowTicks = 45;
							}
						}

						setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
						super.onUpdate(tick);
						
						if(slowTicks > 0)
							slowTicks--;
					}
				});
			}
		}
		
		if(tick % total == 60)
		{
			TouhouSounds.Enemy.LAZER_1.play();
			
			for(int i = 400; i > 270; i -= 8)
			{
				final float finalAngle = i;

				game.spawn(new Laser(new ThLaser(type, sub), boss.getX(), boss.getY(), 400)
				{
					boolean left = false;
					float angle = finalAngle;
					int slowTicks = 0;
					boolean done = false;

					{
						setLengthOnScreen(-1);
						setMaxPoints(20);
						setUnitsPerPoint(-1);
						setZIndex(boss.getZIndex() - 1);
					}

					@Override
					public void onUpdate(long tick)
					{
						float speed = 10f;
						
						float threshold = finalAngle + (left ? -1 : 1) * 360f;
						
						if((left ? angle > threshold : angle < threshold) && !done)
						{
							speed = 10f;
							
							angle = angle + (left ? -1 : 1) * 5f;
							
							if(left ? angle <= threshold : angle >= threshold)
							{
								slowTicks = 25;
								done = true;
							}
						}
						else if(done)
						{
							angle = (float) (angle + (Math.random() > 0.5f ? -1 : 1) * (1f + Math.random() * 1f));
						}

						setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
						super.onUpdate(tick);
						
						if(slowTicks > 0)
							slowTicks--;
					}
				});
			}

			for(int i = 260; i > 130; i -= 8)
			{
				final float finalAngle = i;

				game.spawn(new Laser(new ThLaser(type, sub), boss.getX(), boss.getY(), 400)
				{
					boolean left = true;
					float angle = finalAngle;
					int slowTicks = 0;
					boolean done = false;

					{
						setLengthOnScreen(-1);
						setMaxPoints(20);
						setUnitsPerPoint(-1);
						setZIndex(boss.getZIndex() - 1);
					}

					@Override
					public void onUpdate(long tick)
					{
						float speed = 10f;
						
						float threshold = finalAngle + (left ? -1 : 1) * 360f;
						
						if(angle > threshold && !done)
						{
							speed = 10f;
							
							angle = angle + (left ? -1 : 1) * 5f;
							
							if(angle <= threshold)
							{
								slowTicks = 25;
								done = true;
							}
						}
						else if(done)
						{
							angle = (float) (angle + (Math.random() > 0.5f ? -1 : 1) * (1f + Math.random() * 1f));
						}

						setDirectionRadsTick((float) Math.toRadians(angle), slowTicks > 0 ? 2f : speed);
						super.onUpdate(tick);
						
						if(slowTicks > 0)
							slowTicks--;
					}
				});
			}
		}
		
		if(tick % total == 80)
		{
			TouhouSounds.Enemy.RELEASE_1.play();
			
			float increase = 6;
			float offset = (float) (Math.random() * increase);
			
			for(float angle = offset; angle < 360 + offset; angle += angle <= 140 && angle > 40 ? increase : increase / 2)
			{
				for(int i = 0; i < 6; i++)
				{
					final float finalAngle = angle;
					final int pos = i + 1;
					
					Game.getGame().addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							Bullet bullet = new Bullet(new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.BLUE), boss.getX(), boss.getY());
							bullet.setDirectionRadsTick((float) Math.toRadians(finalAngle), 3f + pos * 1.6f);
							bullet.setZIndex(bullet.getZIndex() + pos);
							bullet.setShader(ShaderLibrary.GLOW.getProgram());
							game.spawn(bullet);
						}
					}, i * 5);
				}
			}
		}
	}
}
