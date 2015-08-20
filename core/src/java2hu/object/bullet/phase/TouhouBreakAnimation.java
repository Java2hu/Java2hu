package java2hu.object.bullet.phase;

import java2hu.object.bullet.Bullet;
import java2hu.system.SaveableObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.util.Duration;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class TouhouBreakAnimation extends AnimationPhaseAnimation
{
	private static Animation getAnimation(Bullet bullet)
	{
		if(bullet.getType() instanceof ThBullet)
		{
			Animation ani = new ThBullet(ThBulletType.BALL_REFLECTING, ((ThBullet)bullet.getType()).getBulletColor().getSpawnAnimationColor()).getAnimation();
			
			return ani;
		}
		else
		{
			Animation ani = new ThBullet(ThBulletType.BALL_REFLECTING, ThBulletColor.getClosest(ThBulletColor.POSSIBLE_BREAK, bullet.getType().getEffectColor())).getAnimation();

			return ani;
		}
	}
	
	public TouhouBreakAnimation(Bullet bullet)
	{
		super(bullet, getAnimation(bullet), Duration.seconds(0.30f));
		
		setBlendFunc(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
	}
	
	@Override
	public void start()
	{
		animationPlaying = true;
		
		createTick = game.getTick();
		
		final Sprite current = (Sprite) this.ani.getKeyFrame(getTicksAlive());
		
		Polygon hitbox = bullet.getHitbox();
		Rectangle rect = hitbox != null ? hitbox.getBoundingRectangle() : current.getBoundingRectangle();

		final float modifier = 2f;
		float width = rect.getWidth() * modifier;
		float height = rect.getHeight() * modifier;
		
		float largest = Math.max(width, height);
		width = height = largest;
		
		final float scaleX = width / current.getWidth();
		final float scaleY = height / current.getHeight();
		
//		current.setBounds(bullet.getX() - width / 2f, bullet.getY() - height / 2f, width, height);
		current.setScale(0f, 0f);//scaleX * 0.5f, scaleY * 0.5f);
		
		current.setOriginCenter();
		
		current.setPosition(getX() - (current.getWidth() / 2f), getY() - (current.getHeight() / 2f));
		
		Color c = bullet.getDeletionColor().cpy();
		
		current.setColor(c);
		current.setAlpha(0.5f);
		
		final SaveableObject<ScaleAlphaPhaseAnimation> sani = new SaveableObject<ScaleAlphaPhaseAnimation>();
		
		Getter<Sprite> getter = new Getter<Sprite>()
		{
			@Override
			public Sprite get()
			{
				Sprite current = (Sprite) ani.getKeyFrame(getTicksAlive());
				
				return current;
			}
		};
		
		final ScaleAlphaPhaseAnimation ani = new ScaleAlphaPhaseAnimation(getter, this);
		
		sani.setObject(ani);
		
		ani.setTime(time);
		ani.setAddedScale(scaleX * 1.5f, scaleY * 1.5f);
		ani.setAlpha(1f);
		ani.start();
		ani.setZIndex(getZIndex());
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		Sprite current = (Sprite) ani.getKeyFrame(getTicksAlive());
		
		current.setRotation((getTicksAlive() / 5f) * 360f);
	}
	
	@Override
	public void onDraw()
	{

	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
	}
	
	@Override
	public void onComplete()
	{
		game.delete(this);
	}
}
