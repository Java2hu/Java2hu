package java2hu.conversation;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java2hu.Game;
import java2hu.object.FreeStageObject;

public class DialogueParticipant<T extends IDialogueFaceEnum> extends FreeStageObject
{
	private Sprite nametag = null;
	private Facing spriteFacing = Facing.CENTER;
	private Facing drawFacing = Facing.CENTER;
	private Sprite current;
	private T faceEnum;
	private Facing anchor = Facing.CENTER;
	private float xOffset = 0;
	private float yOffset = 0;
	
	public static enum Facing
	{
		LEFT, CENTER, RIGHT;
	}
	
	public DialogueParticipant(T face, Facing spriteFacing)
	{
		super(0, 0);
		this.spriteFacing = spriteFacing;
		setFace(face);
	}
	
	public DialogueParticipant(Sprite sprite, Facing spriteFacing)
	{
		super(0, 0);
		this.spriteFacing = spriteFacing;
		setCurrent(sprite);
	}
	
	public Sprite getNametag()
	{
		return nametag;
	}
	
	public void setNametag(Sprite nametag)
	{
		this.nametag = nametag;
	}
	
	public void setFace(T face)
	{
		current = face.getSprite();
	}
	
	public T getFacesEnum()
	{
		return faceEnum;
	}
	
	private FaceToFaceDialogue dialogueManager;
	
	public FaceToFaceDialogue getDialogueManager()
	{
		return dialogueManager;
	}
	
	public void setDialogueManager(FaceToFaceDialogue dialogueManager)
	{
		this.dialogueManager = dialogueManager;
	}

	@Override
	public float getWidth()
	{
		return current != null ? current.getWidth() * current.getScaleX() : 0;
	}

	@Override
	public float getHeight()
	{
		return current != null ? current.getHeight() * current.getScaleY() : 0;
	}
	
	public Facing getAnchor()
	{
		return anchor;
	}
	
	public void setAnchor(Facing anchor)
	{
		this.anchor = anchor;
	}
	
	public Sprite getCurrent()
	{
		return current;
	}
	
	public void setCurrent(Sprite sprite)
	{
		this.current = sprite;
	}
	
	public Facing getSpriteFacing()
	{
		return spriteFacing;
	}
	
	public Facing getDrawFacing()
	{
		return drawFacing;
	}
	
	public void setDrawFacing(Facing drawFacing)
	{
		this.drawFacing = drawFacing;
	}
	
	public float getXOffset()
	{
		return xOffset;
	}
	
	public void setXOffset(float xOffset)
	{
		this.xOffset = xOffset;
	}
	
	public float getYOffset()
	{
		return yOffset;
	}
	
	public void setYOffset(float yOffset)
	{
		this.yOffset = yOffset;
	}

	@Override
	public void onDraw()
	{
		float x = getX();
		float y = getY() + getYOffset();
		
		if(getSpriteFacing() != getDrawFacing() && getSpriteFacing() != Facing.CENTER && getDrawFacing() != Facing.CENTER)
		{
			current.flip(true, false);
			spriteFacing = getDrawFacing();
		}
		
		current.setOrigin(0, 0);
		
		switch(anchor)
		{
			case RIGHT:
				x += getXOffset();
				break;
			case LEFT:
				x -= getWidth() + getXOffset();
				break;
			default:
				break;
		}
		
		current.setPosition(x, y);
		current.draw(Game.getGame().batch);
	}

	@Override
	public void onUpdate(long tick)
	{

	}
}
