package java2hu.object.bullet.phase;

import java2hu.object.bullet.Bullet;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.util.Duration;
import java2hu.util.Duration.Unit;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public class TouhouSpawnAnimation extends AnimationPhaseAnimation
{
	private static Animation getAnimation(Color color)
	{
		Animation ani = new ThBullet(ThBulletType.ORB_MEDIUM, ThBulletColor.WHITE).getAnimation();
		
		for(TextureRegion r : ani.getKeyFrames())
		{
			Sprite s = (Sprite)r;
			
			s.setColor(color);
		}
		
		return ani;
	}
	
	public TouhouSpawnAnimation(Bullet bullet)
	{
		super(bullet, getAnimation(bullet.getDeletionColor()), Duration.seconds(0.3f));
	}
	
	@Override
	public void start()
	{
		OwnedObjectData data = new OwnedObjectData();
		
		data.drawAfter = true;
		
		animationPlaying = false;
		
		createTick = game.getTick();
		bullet.addOwnedObject(this, data);
		
		final Sprite current = (Sprite) this.ani.getKeyFrame(getTicksAlive());
		
		Polygon hitbox = bullet.getHitbox();
		Rectangle rect = hitbox != null ? hitbox.getBoundingRectangle() : current.getBoundingRectangle();

		final float modifier = 2f;
		float width = rect.getWidth() * modifier;
		float height = rect.getHeight() * modifier;
		
		float scaleX = width / current.getWidth();
		float scaleY = height / current.getHeight();
		
//		current.setBounds(bullet.getX() - width / 2f, bullet.getY() - height / 2f, width, height);
		current.setScale(scaleX, scaleY);
		
		current.setOriginCenter();
		current.setColor(bullet.getDeletionColor().cpy().mul(2f));
		current.setAlpha(1f);
		
		ScaleAlphaPhaseAnimation ani = new ScaleAlphaPhaseAnimation(new Getter<Sprite>()
		{
			@Override
			public Sprite get()
			{
				int min = 2;
				
				double mul = 1f * ((getTicksAlive() - min) / time.getValue(Unit.TICK));
				
				if(getTicksAlive() <= min)
				{
					mul = 0f;
				}
				
//				current.setAlpha((float) (targetAlpha - (targetAlpha * mul)));
				
				bullet.getCurrentSprite().setAlpha(targetAlpha * (float) mul);
				
				current.setPosition(bullet.getX() - current.getWidth() / 2f, bullet.getY() - current.getHeight() / 2f);
				
				current.setOriginCenter();
				
				current.setRotation(bullet.getRotationDeg());
				
				return current;
			}
		}, bullet);
		
		ani.setGlowing();
		
		ani.setTime(time);
		ani.setAddedScale(2f);
		ani.setAlpha(0f);
		ani.start();
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
