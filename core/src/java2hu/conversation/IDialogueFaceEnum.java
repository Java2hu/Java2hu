package java2hu.conversation;

import java2hu.conversation.DialogueParticipant.Facing;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Extend an enum with this interface to use it in a DialogueHolder.
 */
public interface IDialogueFaceEnum
{
	public Sprite getSprite();
	public Facing getFacing();
}
