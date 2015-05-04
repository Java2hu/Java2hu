package java2hu.object.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * All these methods should be made null safe by implementations and make an enemy that works without those animations.
 */
public interface IEnemyType
{
	public Animation getIdleAnimation();
	public Animation getLeftAnimation();
	public Animation getRightAnimation();
	public Animation getSpecialAnimation();
}
