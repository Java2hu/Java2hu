package java2hu;

import java2hu.util.HitboxUtil;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;

/**
 * This class combines a sprite and a hitbox, any transformation or translation applied to the sprite will also be applied to the hitbox.
 * It also contains some fixes for certain transformations.
 * TODO: Only works for cases I had to use so far, need to expand.
 */
public class HitboxSprite extends Sprite
{
	private Polygon hitbox;
	private float hitboxScaleOffsetX = 0F; 
	private float hitboxScaleOffsetY = 0F; 
	private float hitboxScaleOffsetModifierX = 1F;
	private float hitboxScaleOffsetModifierY = 1F; 
	private float hitboxOffsetX = 0f;
	private float hitboxOffsetY = 0f;
	
	public HitboxSprite(TextureRegion region)
	{
		super(construct(region));

		if(region instanceof HitboxSprite)
		{
			HitboxSprite sprite = ((HitboxSprite)region);

			copy(sprite);
		}
	}
	
	private static TextureRegion construct(TextureRegion region)
	{
		if(region instanceof HitboxSprite)
		{
			return new TextureRegion(region);
		}
		
		return region;
	}
	
	/**
	 * Copies all transformations from a previous sprite.
	 * @param sprite
	 */
	public void copy(HitboxSprite sprite)
	{
		set(sprite);
		
		if(sprite.hitbox == null)
			return;
		
		setHitbox(new Polygon(sprite.getHitbox().getVertices()));
		
		Polygon h = getHitbox();
		Polygon ori = sprite.getHitbox();
		
		h.setOrigin(getOriginX(), getOriginY());
		h.setRotation(ori.getRotation());
		h.setScale(ori.getScaleX(), ori.getScaleY());
		
		setHitboxOffsetX(sprite.getHitboxOffsetX());
		setHitboxScaleOffsetX(sprite.getHitboxScaleOffsetX());
		setHitboxScaleOffsetModifierX(sprite.getHitboxScaleOffsetModifierX());
		
		setHitboxOffsetY(sprite.getHitboxOffsetY());
		setHitboxScaleOffsetY(sprite.getHitboxScaleOffsetY());
		setHitboxScaleOffsetModifierY(sprite.getHitboxScaleOffsetModifierY());
	}
	
	public void setHitbox(Polygon hitbox)
	{
		if(hitbox == null)
			return;
			
		this.hitbox = hitbox;
		hitbox.setOrigin(getOriginX(), getOriginY());
		setRotation(getRotation());
	}
	
	public Polygon getHitbox()
	{
		return hitbox;
	}
	
	public void setHitboxScaleOffsetModifierX(float hitboxScaleOffsetModifierX)
	{
		this.hitboxScaleOffsetModifierX = hitboxScaleOffsetModifierX;
		applyScaleOffsets();
	}
	
	public void setHitboxScaleOffsetModifierY(float hitboxScaleOffsetModifierY)
	{
		this.hitboxScaleOffsetModifierY = hitboxScaleOffsetModifierY;
		applyScaleOffsets();
	}
	
	public void setHitboxScaleOffsetX(float hitboxScaleOffsetX)
	{
		this.hitboxScaleOffsetX = hitboxScaleOffsetX;
		applyScaleOffsets();
	}
	
	public void setHitboxScaleOffsetY(float hitboxScaleOffsetY)
	{
		this.hitboxScaleOffsetY = hitboxScaleOffsetY;
		applyScaleOffsets();
	}
	
	public void setHitboxScaleOffset(float hitboxScaleOffset)
	{
		setHitboxScaleOffsetX(hitboxScaleOffset);
		setHitboxScaleOffsetY(hitboxScaleOffset);
	}
	
	public void setHitboxScaleOffsetModifier(float hitboxScaleOffsetModifier)
	{
		setHitboxScaleOffsetModifierX(hitboxScaleOffsetModifier);
		setHitboxScaleOffsetModifierY(hitboxScaleOffsetModifier);
	}

	public void applyScaleOffsets()
	{
		if(hitbox != null)
			hitbox.setScale((getScaleX() + hitboxScaleOffsetX) * hitboxScaleOffsetModifierX, (getScaleY() + hitboxScaleOffsetY) * hitboxScaleOffsetModifierY);
	}
	
	public float getHitboxScaleOffsetModifierX()
	{
		return hitboxScaleOffsetModifierX;
	}
	
	public float getHitboxScaleOffsetModifierY()
	{
		return hitboxScaleOffsetModifierY;
	}
	
	public float getHitboxScaleOffsetX()
	{
		return hitboxScaleOffsetX;
	}
	
	public float getHitboxScaleOffsetY()
	{
		return hitboxScaleOffsetY;
	}
	
	public void setHitboxOffsetX(float hitboxOffsetX)
	{
		this.hitboxOffsetX = hitboxOffsetX;
		applyPositionOffsets();
	}
	
	public void setHitboxOffsetY(float hitboxOffsetY)
	{
		this.hitboxOffsetY = hitboxOffsetY;
		applyPositionOffsets();
	}
	
	public float getHitboxOffsetX()
	{
		return hitboxOffsetX;
	}
	
	public float getHitboxOffsetY()
	{
		return hitboxOffsetY;
	}
	
	private void applyPositionOffsets()
	{
		setPosition(getX(), getY());
	}
	
	@Override
	public void draw(Batch batch)
	{
		super.draw(batch);
	}
	
	public void drawHitbox()
	{
		if(hitbox == null)
			return;
		
		HitboxUtil.drawHitbox(hitbox);
	}
	
	
	@Override
	public void rotate(float degrees)
	{
		if(hitbox != null)
			hitbox.rotate(degrees);
		
		super.rotate(degrees);
	}
	
	@Override
	public void setRotation(float degrees)
	{
		if(hitbox != null)
			hitbox.setRotation(degrees);
		
		super.setRotation(degrees);
	}
	
	@Override
	public void setPosition(float x, float y)
	{
		if(hitbox != null)
			hitbox.setPosition(x + hitboxOffsetX, y + hitboxOffsetY);
		
		super.setPosition(x, y);
	}
	
	@Override
	public void setScale(float scaleXY)
	{
		setScale(scaleXY, scaleXY);
	}
	
	@Override
	public void setScale(float scaleX, float scaleY)
	{
		if(hitbox != null)
			hitbox.setScale((scaleX + hitboxScaleOffsetX) * hitboxScaleOffsetModifierX, (scaleY + hitboxScaleOffsetY) * hitboxScaleOffsetModifierY);
		
		super.setScale(scaleX, scaleY);
	}
	
	@Override
	public void scale(float amount)
	{
		setScale(getScaleX() + amount, getScaleY() + amount);
	}
	
	@Override
	public void setSize(float width, float height)
	{
		if(hitbox != null)
			setScale(((width / getWidth()) + hitboxScaleOffsetX) * hitboxScaleOffsetModifierX, ((height / getHeight()) + hitboxScaleOffsetY) * hitboxScaleOffsetModifierY);
		
		super.setSize(width, height);
	}
}
