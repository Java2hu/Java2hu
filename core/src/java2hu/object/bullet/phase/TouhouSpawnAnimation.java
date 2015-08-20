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

public class TouhouSpawnAnimation extends AnimationPhaseAnimation
{
	private static Animation getAnimation(Bullet bullet)
	{
		if(bullet.getType() instanceof ThBullet)
		{
			Animation ani = new ThBullet(ThBulletType.ORB_MEDIUM, ((ThBullet)bullet.getType()).getBulletColor().getBreakAnimationColor()).getAnimation();

			return ani;
		}
		else
		{
			Animation ani = new ThBullet(ThBulletType.ORB_MEDIUM, ThBulletColor.getClosest(ThBulletColor.POSSIBLE_SPAWN, bullet.getType().getEffectColor())).getAnimation();

			return ani;
		}
	}
	
	public TouhouSpawnAnimation(Bullet bullet)
	{
		super(bullet, getAnimation(bullet), Duration.seconds(0.25f));
		
		setBlendFunc(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
	}
	
	@Override
	public void start()
	{
		OwnedObjectData data = new OwnedObjectData();
		
		data.drawAfter = true;
		
		if(bullet.getTicksAlive() < 20)
			animationPlaying = true;
		
		createTick = game.getTick();
		
		bullet.addOwnedObject(this, data);
		
		final Sprite current = (Sprite) this.ani.getKeyFrame(getTicksAlive());
		
		Polygon hitbox = bullet.getHitbox();
		Rectangle rect = hitbox != null ? hitbox.getBoundingRectangle() : current.getBoundingRectangle();

		final float modifier = 3f;
		float width = rect.getWidth() * modifier;
		float height = rect.getHeight() * modifier;
		
		final float scaleX = width / current.getWidth();
		final float scaleY = height / current.getHeight();
		
		current.setScale(scaleX, scaleY);
		current.setRotation(bullet.getRotationDeg());
		
		current.setOriginCenter();
		
		Color c = bullet.getDeletionColor().cpy();
		
		float min = Math.min(c.g, Math.min(c.r, c.b));
		c.r -= min;
		c.g -= min;
		c.b -= min;
		
		float mul = 0.8f;
		float start = (1f - mul) + 0.3f;
		
		Color color = new Color(start + (c.r * mul), start + (c.g * mul), start + (c.b * mul), 0f);
		
		current.setColor(color);
		current.setAlpha(1f);
		
		final SaveableObject<ScaleAlphaPhaseAnimation> sani = new SaveableObject<ScaleAlphaPhaseAnimation>();
		
		Getter<Sprite> getter = new Getter<Sprite>()
		{
			@Override
			public Sprite get()
			{
				Sprite current = (Sprite) ani.getKeyFrame(getTicksAlive());
				
				int over = 5;
				
				int ticks = (int) ((time.toTicks() - over) - getTicksAlive());
				
				double mul = 1f - (ticks <= 0 ? -(float)ticks / over : 0f);
				
				ScaleAlphaPhaseAnimation ani = sani.getObject();
				
				if(ani == null)
					return current;
				
				if(ticks <= 0)
				{
					animationPlaying = false;
					ani.setAlpha((float) Math.max(0, mul));
				}
				
				current.setPosition(bullet.getX() - current.getWidth() / 2f, bullet.getY() - current.getHeight() / 2f);
				
				current.setOriginCenter();
				
				current.setRotation(bullet.getRotationDeg());
				
				return current;
			}
		};
		
		final ScaleAlphaPhaseAnimation ani = new ScaleAlphaPhaseAnimation(getter, bullet);
		
		sani.setObject(ani);
		
		ani.setTime(time);
		ani.setAddedScale(scaleX * 3f, scaleY * 3f);
		ani.setAlpha(-0.1f);
		ani.start();
		
		bullet.removeOwnedObject(ani);
		bullet.addOwnedObject(ani, data);
	}
	
	@Override
	public void onDraw()
	{
//		if(!bullet.isOnStageRaw())
//			return;
//		
//		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
//
//		J2hGame g = Game.getGame();
//
//		Sprite current = (Sprite) ani.getKeyFrame(getTicksAlive());
//		
//		current.setPosition(bullet.getX() - current.getWidth() / 2f, bullet.getY() - current.getHeight() / 2f);
//		
//		current.setOriginCenter();
//		
//		current.setRotation(bullet.getRotationRads());
//
//		int min = 2;
//		
//		double mul = 1f * ((getTicksAlive() - min) / time.getValue(Unit.TICK));
//		
//		if(getTicksAlive() <= min)
//		{
//			mul = 0f;
//		}
//		
//		current.setAlpha((float) (targetAlpha - (targetAlpha * mul)));
//		
//		bullet.getCurrentSprite().setAlpha(targetAlpha * (float) mul);
//
//		current.draw(g.batch);
//		
//		game.batch.setBlendFunction(bullet.getBlendFuncSrc(), bullet.getBlendFuncDst());
	}
	
	private float targetAlpha = 1f;
	
	public void setTargetAlpha(float targetAlpha)
	{
		this.targetAlpha = targetAlpha;
	}
	
	public float getTargetAlpha()
	{
		return targetAlpha;
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		
		bullet.getCurrentSprite().setAlpha(targetAlpha);
	}
}
