package java2hu.gameflow;

import java2hu.Game;
import java2hu.conversation.FaceToFaceDialogue;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.system.SaveableObject;
import java2hu.util.Getter;

public class DialogueFlowScheme<T extends GameFlowScheme> implements SpecialFlowScheme<T>
{
	private Getter<FaceToFaceDialogue> getter;
	
	public DialogueFlowScheme(Getter<FaceToFaceDialogue> dialogueGetter)
	{
		this.getter = dialogueGetter;
	}
	
	@Override
	public void executeFight(T scheme)
	{
		final SaveableObject<FaceToFaceDialogue> dialogue = new SaveableObject<FaceToFaceDialogue>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				dialogue.setObject(getter.get());
				Game.getGame().spawn(dialogue.getObject());
			}
		}, 1);
		
		scheme.setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return dialogue.getObject() == null || dialogue.getObject().isOnStage() || !dialogue.getObject().isDone();
			}
		});
		
		scheme.doWait();
	}
}
