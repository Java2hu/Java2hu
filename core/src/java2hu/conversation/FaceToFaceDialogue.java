package java2hu.conversation;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Position;
import java2hu.conversation.DialogueParticipant.Facing;
import java2hu.events.EventHandler;
import java2hu.events.EventHandler.EventPriority;
import java2hu.events.EventListener;
import java2hu.events.input.KeyDownEvent;
import java2hu.object.DrawObject;
import java2hu.object.UpdateObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.MathUtil;
import java2hu.util.PathUtil;
import java2hu.util.PathUtil.PathTask;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class FaceToFaceDialogue extends UpdateObject implements EventListener
{
	private int index = -1;
	
	private ArrayList<Dialogue> dialogue = new ArrayList<FaceToFaceDialogue.Dialogue>();
	private DialogueParticipant leftHolder;
	private DialogueParticipant rightHolder;
	private DialogueTextBalloon textBalloon;
	
	private Runnable onDone;
	
	public FaceToFaceDialogue(DialogueTextBalloon textBalloon, DialogueParticipant left, DialogueParticipant right)
	{
		leftHolder = left;
		rightHolder = right;
		this.textBalloon = textBalloon;
		
		leftHolder.setDialogueManager(this);
		rightHolder.setDialogueManager(this);
		textBalloon.setDialogueManager(this);
		
		left.setDrawFacing(Facing.RIGHT);
		left.setAnchor(Facing.RIGHT);
		right.setDrawFacing(Facing.LEFT);
		right.setAnchor(Facing.LEFT);
		
		setZIndex(J2hGame.GUI_Z_ORDER - 10);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		Game.getGame().registerEvents(this);
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		
		Game.getGame().unregisterEvents(this);
	}
	
	public void addDialogue(Dialogue dialogue)
	{
		this.dialogue.add(dialogue);
	}

	public void addDialogue(ArrayList<Dialogue> list)
	{
		this.dialogue.addAll(list);
	}
	
	public static class Dialogue
	{
		public DialogueParticipant holder;
		public String text;
		public IDialogueFaceEnum face;
		public Runnable doBefore;
		public Runnable doAfter;
		
		public Dialogue(DialogueParticipant saidBy)
		{
			this(saidBy, null);
		}
		
		public Dialogue(DialogueParticipant saidBy, String text)
		{
			this(saidBy, text, null);
		}
		
		public Dialogue(DialogueParticipant saidBy, String text, IDialogueFaceEnum face)
		{
			this(saidBy, text, face, null, null);
		}
		
		public Dialogue(DialogueParticipant saidBy, String text, IDialogueFaceEnum face, Runnable doBefore, Runnable doAfter)
		{
			this.holder = saidBy;
			this.text = text;
			this.face = face;
			this.doBefore = doBefore;
			this.doAfter = doAfter;
		}
	}
	
	public DialogueParticipant getLeftHolder()
	{
		return leftHolder;
	}
	
	public DialogueParticipant getRightHolder()
	{
		return rightHolder;
	}
	
	public DialogueTextBalloon getTextBalloon()
	{
		return textBalloon;
	}
	
	public void setOnDone(Runnable onDone)
	{
		this.onDone = onDone;
	}
	
	public Runnable getOnDone()
	{
		return onDone;
	}
	
	int changeTimer = 0;
	DialogueParticipant active = null;
	
	PathTask leftTask;
	PathTask rightTask;
	
	@Override
	public void onDraw()
	{
		leftHolder.onDraw();
		rightHolder.onDraw();
		textBalloon.onDraw();
		
		super.onDraw();
	}
	
	boolean setPositions = false;
	boolean updatePositions = false;
	boolean hasStarted = false;
	
	float waitTimeLeft = -1;
	float waitTimeRight = -1;
	
	J2hGame game = Game.getGame();
	
	Position leftStart = new Position(game.getMinX(), game.getMinY());
	Position leftInactive = new Position(game.getMinX() - 50, game.getMinY() - 50);
	Position leftActive = new Position(game.getMinX(), game.getMinY());
	
	Position rightStart = new Position(game.getMaxX(), game.getMinY());
	Position rightInactive = new Position(game.getMaxX() + 50, game.getMinY() - 50);
	Position rightActive = new Position(game.getMaxX(), game.getMinY());

	@Override
	public void onUpdate(long tick)
	{
		final J2hGame game = Game.getGame();
		
		leftHolder.onUpdate(tick);
		rightHolder.onUpdate(tick);
		textBalloon.onUpdate(tick);
		
		if(cooldown > 0)
			cooldown--;
		
		if(isDone())
		{
			Game.getGame().delete(this);
			return;
		}
		
		if(!setPositions)
		{
			Sprite ls = leftHolder.getCurrent();
			Sprite rs = rightHolder.getCurrent();
			
			leftHolder.setPosition(leftStart);
			leftHolder.setX(leftHolder.getX() - ls.getWidth() * ls.getScaleX());
			leftHolder.setY(leftHolder.getY() - ls.getHeight() * ls.getScaleY());
			
			rightHolder.setPosition(rightStart);
			rightHolder.setX(rightHolder.getX() + rs.getWidth() * rs.getScaleX());
			rightHolder.setY(rightHolder.getY() - rs.getHeight() * rs.getScaleY());
			
			setPositions = true;
		}
		
		if(setPositions && index == -1 && !dialogue.isEmpty())
		{
			index++;
			runDialogue(dialogue.get(index));
			hasStarted = true;
		}
	}
	
	@Override
	public boolean isActiveDuringPause()
	{
		return true;
	}
	
	boolean hasLeftEntered = false;
	
	public void showNametagLeft()
	{
		showNametag(leftHolder);
	}
	
	public void showNametagRight()
	{
		showNametag(rightHolder);
	}
	
	private void showNametag(final DialogueParticipant h)
	{
		if(h.getNametag() != null)
		{
			DrawObject obj = new DrawObject()
			{
				float xOffset = h.getDrawFacing() == Facing.LEFT ? -50 : 50;
				float yOffset = 200;
				Sprite s = h.getNametag();
				
				@Override
				public void onDraw()
				{
					s.draw(Game.getGame().batch);
				}
				
				@Override
				public void onUpdate(long tick)
				{
					s.setPosition(h.getX() + xOffset, h.getY() + yOffset);
					
					xOffset += h.getDrawFacing() == Facing.LEFT ? -2 : 2;
					
					if(xOffset < -200 || xOffset > 200)
					{
						Game.getGame().delete(this);
					}
				}
			};
		}
	}

	public void enterLeft()
	{
		if(waitTimeLeft == -1)
			waitTimeLeft = getWaitTime(MathUtil.getDistance(leftHolder, leftInactive));
		
		leftTask = PathUtil.moveTo(leftHolder, leftInactive, (int) waitTimeLeft);
		leftTask.setOnDone(new Runnable()
		{
			@Override
			public void run()
			{
				hasLeftEntered = true;
				leftTask = null;
			}
		});
	}
	
	private float getWaitTime(float distance)
	{
		float time = distance / 10f;
		
		System.out.println(time);
		
		return time;
	}
	
	boolean hasRightEntered = false;
	
	public void enterRight()
	{
		if(waitTimeRight == -1)
			waitTimeRight = getWaitTime(MathUtil.getDistance(rightHolder, rightInactive));
		
		rightTask = PathUtil.moveTo(rightHolder, rightInactive, (int) waitTimeRight);
		rightTask.setOnDone(new Runnable()
		{
			@Override
			public void run()
			{
				hasRightEntered = true;
				rightTask = null;
			}
		});
	}
	
	private int cooldown = 0;

	@EventHandler(priority = EventPriority.LOW)
	public void keyDown(KeyDownEvent event)
	{
		int keyCode = event.getKey();
		
		if(keyCode == Keys.Z && !game.isPaused())
		{
			event.setCancelled(true);
			
			if(!isDone() && cooldown <= 0)
			{
				cooldown = 40;
				
				Dialogue old = dialogue.get(index);

				if(old.doAfter != null)
					old.doAfter.run();

				index++;
				TouhouSounds.Hud.OK.play();

				if(!isDone())
				{
					Dialogue current = dialogue.get(index);

					runDialogue(current);
				}
			}
		}
	}
	
	public void runDialogue(Dialogue dialogue)
	{
		if(dialogue.face != null)
			dialogue.holder.setFace(dialogue.face);
		
		active = dialogue.holder;
		
		if(dialogue.doBefore != null)
			dialogue.doBefore.run();
		
		textBalloon.setText(dialogue.text);

		if(active != null)
		{
			if(active == leftHolder)
			{
				textBalloon.setSpeechArrow(Facing.LEFT);
				
				if(hasLeftEntered)
				{
					final Runnable onDone = leftTask != null ? leftTask.getOnDone() : null;

					Runnable set = new Runnable()
					{
						@Override
						public void run()
						{
							if(onDone != null)
								onDone.run();

							leftTask = PathUtil.moveTo(leftHolder, leftActive, 30);
						}
					};

					if(leftTask == null)
						set.run();
					else
						leftTask.setOnDone(set);
				}
			}
			
			if(active == rightHolder)
			{
				textBalloon.setSpeechArrow(Facing.RIGHT);
				
				if(hasRightEntered)
				{
					final Runnable onDone = rightTask != null ? rightTask.getOnDone() : null;

					Runnable set = new Runnable()
					{
						@Override
						public void run()
						{
							if(onDone != null)
								onDone.run();

							rightTask = PathUtil.moveTo(rightHolder, rightActive, 30);
						}
					};

					if(rightTask == null)
						set.run();
					else
						rightTask.setOnDone(set);
				}
			}
		}

		if(active != leftHolder && hasLeftEntered)
		{
			final Runnable onDone = leftTask != null ? leftTask.getOnDone() : null;
			
			Runnable set = new Runnable()
			{
				@Override
				public void run()
				{
					if(onDone != null)
						onDone.run();
					
					leftTask = PathUtil.moveTo(leftHolder, leftInactive, 30);
				}
			};
			
			if(leftTask == null)
				set.run();
			else
				leftTask.setOnDone(set);
		}

		if(active != rightHolder && hasRightEntered)
		{
			final Runnable onDone = rightTask != null ? rightTask.getOnDone() : null;
			
			Runnable set = new Runnable()
			{
				@Override
				public void run()
				{
					if(onDone != null)
						onDone.run();
					
					rightTask = PathUtil.moveTo(rightHolder, rightInactive, 30);
				}
			};
			
			if(rightTask == null)
				set.run();
			else
				rightTask.setOnDone(set);
		}
		
		if(leftTask != null)
		{
			final Runnable onDone = leftTask != null ? leftTask.getOnDone() : null;
			
			leftTask.setOnDone(new Runnable()
			{
				@Override
				public void run()
				{
					leftTask = null;
					
					if(onDone != null)
						onDone.run();
				}
			});
		}

		if(rightTask != null)
		{
			final Runnable onDone = rightTask != null ? rightTask.getOnDone() : null;
			
			rightTask.setOnDone(new Runnable()
			{
				@Override
				public void run()
				{
					rightTask = null;
					
					if(onDone != null)
						onDone.run();
				}
			});
		}
	}
	
	public boolean ranOnDone = false;
	
	public boolean isDone()
	{
		boolean bool = index >= dialogue.size();
		
		if(!ranOnDone && bool && hasStarted)
		{
			if(onDone != null)
				onDone.run();
		}
		
		return bool;
	}
}
