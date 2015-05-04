package java2hu.conversation;

import java.util.ArrayList;
import java2hu.conversation.FaceToFaceDialogue.Dialogue;

/**
 * Easy dialogue maker.
 * @param <T> The class of the Dialogue Participant (DP)
 * @param <Y> The class of the Face enum (Can't derive it from DP, sadly...)
 * @param <V> The text balloon type of your text balloon provider.
 */
public class DialogueMaker
{
	private ArrayList<Dialogue> dialogues = new ArrayList<Dialogue>();
	private DialogueParticipant<IDialogueFaceEnum> h;
	
	public DialogueMaker(DialogueParticipant<IDialogueFaceEnum> dialogueHolder)
	{
		nextDialogue(dialogueHolder);
	}
	
	// Current dialogue
	private Dialogue d;
	
	public DialogueMaker nextDialogue(DialogueParticipant<IDialogueFaceEnum> nextTalker)
	{
		h = nextTalker;
		
		d = new Dialogue(h);
		dialogues.add(d);
		
		return this;
	}
	
	public ArrayList<Dialogue> finish()
	{
		return dialogues;
	}
	
	public ArrayList<Dialogue> getDialogue()
	{
		return dialogues;
	}
	
	public DialogueMaker text(String text)
	{
		d.text = text;
		return this;
	}
	
	public DialogueMaker face(IDialogueFaceEnum face)
	{
		d.face = face;
		return this;
	}
	
	public DialogueMaker doAfter(Runnable doAfter)
	{
		d.doAfter = doAfter;
		return this;
	}
	
	public DialogueMaker doBefore(Runnable doBefore)
	{
		d.doBefore = doBefore;
		return this;
	}
	
	/**
	 * Do before this dialogue, but keep any other runnables already set to be ran.
	 * @param doBefore - The inserting runnable
	 * @param runAfterExisting - If your runnable should be ran after any previously existing runnables.
	 * @return
	 */
	public DialogueMaker doBeforeStack(final Runnable doBefore, final boolean runAfterExisting)
	{
		final Runnable run = d.doBefore;
		
		final Runnable newRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				if(runAfterExisting)
				{
					if(run != null)
						run.run();
					
					doBefore.run();
				}
				else
				{
					doBefore.run();
					
					if(run != null)
						run.run();
				}
			}
		};
		
		d.doBefore = newRunnable;
		return this;
	}
	
	/**
	 * Do after this dialogue, but keep any other runnables already set to be ran.
	 * @param doAfter - The inserting runnable
	 * @param runAfterExisting - If your runnable should be ran after any previously existing runnables.
	 * @return
	 */
	public DialogueMaker doAfterStack(final Runnable doAfter, final boolean runAfterExisting)
	{
		final Runnable run = d.doBefore;
		
		final Runnable newRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				if(runAfterExisting)
				{
					if(run != null)
						run.run();
					
					doAfter.run();
				}
				else
				{
					doAfter.run();
					
					if(run != null)
						run.run();
				}
			}
		};
		
		d.doAfter = newRunnable;
		return this;
	}
	
	
	public DialogueMaker speechBubbleType(final ITextBalloonType type)
	{
		doBeforeStack(new Runnable()
		{
			@Override
			public void run()
			{
				h.getDialogueManager().getTextBalloon().setType(type);
			}
		}, true);
		
		return this;
	}
	
	public DialogueMaker enterStage()
	{
		final DialogueParticipant<IDialogueFaceEnum> current = h;
		
		doBeforeStack(new Runnable()
		{
			@Override
			public void run()
			{
				if(current.getDialogueManager().getLeftHolder() == current)
				{
					current.getDialogueManager().enterLeft();
				}
				else if(current.getDialogueManager().getRightHolder() == current)
				{
					current.getDialogueManager().enterRight();
				}
			}
		}, true);
		
		return this;
	}
}
