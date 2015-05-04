package java2hu.object.bullet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.sun.istack.internal.Nullable;

public interface IBulletType
{
	public Animation getAnimation();
	public @Nullable Color getEffectColor(); // Color used for effects such as death
}
