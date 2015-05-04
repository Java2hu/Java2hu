package java2hu.allstar.enemies.day8;

import java2hu.Game;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.dialogue.Marisa98DP;
import java2hu.allstar.dialogue.Marisa98DP.Marisa98DPFace;
import java2hu.allstar.dialogue.Shinki98DP;
import java2hu.allstar.dialogue.Shinki98DP.Shinki98DPFace;
import java2hu.conversation.DDCDialogueTextBalloon;
import java2hu.conversation.DDCDialogueTextBalloon.DDCBalloonType;
import java2hu.conversation.DialogueMaker;
import java2hu.conversation.DialogueParticipant;
import java2hu.conversation.FaceToFaceDialogue;
import java2hu.gameflow.DialogueFlowScheme;
import java2hu.gameflow.GameFlowScheme.WaitConditioner;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.BGMPlayer;
import java2hu.system.SaveableObject;
import java2hu.util.Getter;
import java2hu.util.PathUtil;

import com.badlogic.gdx.Gdx;

public class Day7Dialogue extends Getter<SpecialFlowScheme>
{
	@Override
	public SpecialFlowScheme get()
	{
		Getter<FaceToFaceDialogue> getter = new Getter<FaceToFaceDialogue>()
		{
			@Override
			public FaceToFaceDialogue get()
			{
				final DDCDialogueTextBalloon balloon = new DDCDialogueTextBalloon();

				final DialogueParticipant left = new Marisa98DP();
				final DialogueParticipant right = new Shinki98DP();

				final FaceToFaceDialogue d = new FaceToFaceDialogue(balloon, left, right);

				d.enterLeft();

				DialogueMaker m = new DialogueMaker(right);

				m.text("Well well well. That sure is a rare sight.").nextDialogue(right);
				m.enterStage().text("So for whaaaat reason could you be here Marisa?").nextDialogue(left);
				m.text("I could ask you the exact same thing!!\nHighly suspicious!").face(Marisa98DPFace.EXCITED).speechBubbleType(DDCBalloonType.SCREAM).nextDialogue(right);
				m.text("What... I live here...").face(Shinki98DPFace.ANNOYED).speechBubbleType(DDCBalloonType.NORMAL).nextDialogue(left);
				m.text("Well, I'll let you off the hook this time!").face(Marisa98DPFace.EXCITED).nextDialogue(right);
				m.text("Ehh... whatever.\nHowever, you won't be getting away this time.").face(Shinki98DPFace.HAND).nextDialogue(right);
				m.text("You see.. even we here in Makai know of the price that's on your head.").nextDialogue(left);
				m.text("Is that a threat?").face(Marisa98DPFace.HAPPY).nextDialogue(right);
				m.text("... (ignores) The point is that you sealed your fate by coming here.\nEveryone, including me is out for your bounty.").nextDialogue(left);
				m.text("But aren't you like a god already?\nWhat bounty would interest the likes of you.").speechBubbleType(DDCBalloonType.NORMAL).nextDialogue(right);
				m.text("You already know I can't tell you that.").nextDialogue(left);
				m.text("I guess I'll just have to force it out of ya then!").speechBubbleType(DDCBalloonType.SCREAM).nextDialogue(right);
				m.text("I'll face you soon, I can't just come in and put you down\nThat would ruin all the fun for my citizens...").speechBubbleType(DDCBalloonType.NORMAL).face(Shinki98DPFace.ARMS_SPREAD).nextDialogue(left);
				m.text("Ooooh, sounds like you're... scared?").face(Marisa98DPFace.EXCITED).nextDialogue(right);
				m.text("Bye bye Marisa.").face(Shinki98DPFace.IDLE).nextDialogue(left);
				m.text("Come back, you coward!").face(Marisa98DPFace.ANNOYED);

				d.addDialogue(m.getDialogue());

				return d;
			}
		};

		final DialogueFlowScheme<AllStarStageScheme> scheme = new DialogueFlowScheme<AllStarStageScheme>(getter)
		{
			@Override
			public void executeFight(AllStarStageScheme scheme)
			{
				final SaveableObject<BGMPlayer> bgm = new SaveableObject<>();
				final SaveableObject<Shinki> shinki = new SaveableObject<Shinki>();
				final SaveableObject<Boolean> done = new SaveableObject<Boolean>(false);

				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						((AllStarGame)Game.getGame()).setPC98(true);

						bgm.setObject
						(
								new BGMPlayer(Gdx.audio.newMusic(Gdx.files.internal("music/makai/dialogue.mp3")))
								{
									@Override
									public boolean isPersistant()
									{
										return done.getObject();
									}
								}
								);

						Game.getGame().spawn(bgm.getObject());
						bgm.getObject().getBgm().play();

						final Shinki shinkiBoss = Shinki.newInstance(-100, 900, true);
						Game.getGame().spawn(shinkiBoss);

						PathUtil.moveTo(shinkiBoss, Game.getGame().getWidth()/2f, 600, 40).setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								shinki.setObject(shinkiBoss);
							}
						});
					}
				}, 1);

				scheme.setWait(new WaitConditioner()
				{
					@Override
					public boolean returnTrueToWait()
					{
						return shinki.getObject() == null;
					}
				});

				scheme.doWait();

				super.executeFight(scheme);

				game.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						PathUtil.moveTo(shinki.getObject(), Game.getGame().getWidth() + 100, 900, 40).setOnDone(new Runnable()
						{
							@Override
							public void run()
							{
								Game.getGame().delete(shinki.getObject());
							}
						});
					}
				}, 5);

				scheme.waitTicks(60);

				game.addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						done.setObject(true);
						bgm.getObject().fade(bgm.getObject().getBgm().getVolume(), 0f, 60, true);
					}
				}, 1);
			}
		};

		return scheme;
	}
}
