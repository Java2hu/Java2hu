package java2hu.conversation;

import java2hu.conversation.DialogueParticipant.Facing;
import java2hu.object.FreeStageObject;

public abstract class DialogueTextBalloon<T extends ITextBalloonType> extends FreeStageObject
{
	private String text;
	
	public DialogueTextBalloon()
	{
		super(0, 0);
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	private Facing speechArrow = Facing.LEFT;
	
	public Facing getSpeechArrow()
	{
		return speechArrow;
	}
	
	public void setSpeechArrow(Facing speechArrow)
	{
		this.speechArrow = speechArrow;
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
	
	public abstract void setType(T t); 
}
