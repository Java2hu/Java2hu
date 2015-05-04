package java2hu.conversation;

import java2hu.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Touhou Dialogue Holder, works by pasting a face over a base image (usually a blank face).
 * Usable for touhou versions > MoF
 */
public class TouhouDP<T extends IDialogueFaceEnum> extends DialogueParticipant<T>
{
	private Facing faceFacing = Facing.CENTER;
	private T face;
	private Sprite sprite;
	
	public TouhouDP(Sprite sprite, T face, Facing spriteFacing, Facing faceFacing)
	{
		super(sprite, spriteFacing);
		this.faceFacing = faceFacing;
		setFace(face);
	}
	
	@Override
	public void setFace(T face)
	{
		this.face = face;
		sprite = face.getSprite();
		faceFacing = face.getFacing();
	}
	
	public T getFace()
	{
		return face;
	}
	
	public Facing getFaceFacing()
	{
		return faceFacing;
	}
	
	public void setFaceFacing(Facing faceFacing)
	{
		this.faceFacing = faceFacing;
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
		
		positionFace(sprite);
		
		sprite.draw(Game.getGame().batch);
	}
	
	public void positionFace(Sprite sprite)
	{
		if(getFaceFacing() != getDrawFacing())
		{
			sprite.flip(true, false);
			
			setFaceFacing(getDrawFacing());
		}
		
		float x = getX();
		float y = getY();
		
		switch(getDrawFacing())
		{
			case RIGHT:
				x += getCurrent().getWidth() - sprite.getWidth() + getXOffset();
				break;
			case LEFT:
				x -= getCurrent().getWidth() - getXOffset();
				break;
			default:
				break;
		}
		
		y += getYOffset();
		y += getCurrent().getHeight() - sprite.getHeight();
		
		sprite.setPosition(x, y);
	}
}
