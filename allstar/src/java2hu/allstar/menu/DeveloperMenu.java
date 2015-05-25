package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.backgrounds.DreamWorldBG;
import java2hu.allstar.dialogue.MarisaDP;
import java2hu.allstar.dialogue.MarisaDP.MarisaDPFace;
import java2hu.allstar.players.Marisa;
import java2hu.conversation.DDCDialogueTextBalloon;
import java2hu.conversation.DDCDialogueTextBalloon.DDCBalloonType;
import java2hu.conversation.DialogueMaker;
import java2hu.conversation.DialogueParticipant;
import java2hu.conversation.FaceToFaceDialogue;
import java2hu.gameflow.DialogueFlowScheme;
import java2hu.gameflow.GameFlowScheme;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.menu.Menu;
import java2hu.object.StageObject;
import java2hu.object.enemy.greater.Boss;
import java2hu.overwrite.J2hObject;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class DeveloperMenu extends AllStarMenu
{
	MainMenu parent;

	int index = 0;
	final float START_Y = 650;
	final float Y_ADD = 50;
	
	final BitmapFont botFont = getFont(FontType.SMALL);
	final BitmapFont topFont = getFont(FontType.LARGE);
	final BitmapFont bigFont = getFont(FontType.MEDIUM);
	
	public static abstract class TestScheme extends GameFlowScheme
	{
		public TestScheme()
		{
			
		}
		
		public abstract void startSpellcard();
		
		@Override
		public void runScheme()
		{
			final J2hGame g = Game.getGame();
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					startSpellcard();
				}
			}, 5);
		}
	}
	
	{
		final DeveloperMenu screen = this;
		
		final J2hGame game = Game.getGame();
		
		final float startX = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2;
		final float startY = Game.getGame().getHeight() - 150;
		
		addButton("3D Background tester", new Runnable()
		{
			@Override
			public void run()
			{
				final Runnable run = new Runnable()
				{
					@Override
					public void run()
					{
						Game.getGame().spawn(new DreamWorldBG());
					}
				};
				
				Getter getter = new Getter<TestScheme>()
				{
					@Override
					public TestScheme get()
					{
						return new TestScheme()
						{
							@Override
							public GameFlowScheme getRestartInstance()
							{
								return get();
							}
							
							@Override
							public void startSpellcard()
							{
								run.run();
							}
						};
					}	
				};
				
				Game.getGame().setScheme((GameFlowScheme) getter.get());
				
				Game.getGame().getScheme().start();
			}
		});
		
		final Getter<SpecialFlowScheme> getter = new Getter<SpecialFlowScheme>()
		{
			@Override
			public SpecialFlowScheme get()
			{
				DialogueFlowScheme holder = new DialogueFlowScheme(new Getter<FaceToFaceDialogue>()
				{
					@Override
					public FaceToFaceDialogue get()
					{
						final DDCDialogueTextBalloon balloon = new DDCDialogueTextBalloon();

						final DialogueParticipant left = new MarisaDP();
						final DialogueParticipant right = new MarisaDP();

						final FaceToFaceDialogue d = new FaceToFaceDialogue(balloon, left, right);

						String ipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur a luctus risus, vel fringilla metus. Nunc commodo dui sed lectus viverra, in tincidunt felis tincidunt. Mauris metus ex, bibendum et nulla eu, pharetra iaculis arcu. Curabitur at malesuada libero, non bibendum tellus. Maecenas non luctus est. Suspendisse id lectus nisi. Donec pharetra orci ultrices, aliquam magna quis, iaculis lorem. Sed imperdiet feugiat pretium. In fringilla diam magna, vulputate dignissim lectus hendrerit eget. Suspendisse potenti. Nulla sit amet nisi vitae mi pharetra volutpat. Sed erat quam, rhoncus id elit id, sollicitudin laoreet purus. Donec sodales eleifend ex, a interdum elit consequat non. Maecenas nibh tellus, commodo sed risus eu, imperdiet commodo dui. Nam id mollis leo, vitae congue eros. Phasellus eget libero at eros laoreet commodo nec in eros.";

						String[] array = ipsum.split("\\. ");

						DialogueMaker m = new DialogueMaker(left);

						m.text("Hey, I'm about to say something really long and boring").enterStage().nextDialogue(right);
						m.text("k, ignored you anyways.").face(MarisaDPFace.SIGH).enterStage().nextDialogue(left);

						for(int i = 0; i < array.length - 2; i += 2)
						{
							String str = array[i] + "\n" + array[i + 1];

							m.text(str).face(MarisaDPFace.CONFUSED).nextDialogue(right);

							String[] replies = { "Very nice...", "Alright.", "Interesting...", "Good for you.", "Uhu", "Sooooo innnnteerreeessstttinnggg... -ZZZZ-" };

							m.text(replies[(int) (Math.random() * replies.length)]).face(MarisaDPFace.SIGH).nextDialogue(left);
						}

						m.text("Remember to take it easy!").face(MarisaDPFace.YUKKURI).speechBubbleType(DDCBalloonType.SCREAM).nextDialogue(right);
						m.text("What was that!?").face(MarisaDPFace.IDIOCY);

						d.addDialogue(m.getDialogue());

						return d;
					}
				});

				return holder;
			}
		};
		
		addButton("Simple Dialogue between 2xMarisa", new Runnable()
		{
			@Override
			public void run()
			{
				Getter<GameFlowScheme> schemeGetter = new Getter<GameFlowScheme>()
				{
					@Override
					public GameFlowScheme get()
					{
						return new GameFlowScheme()
						{
							@Override
							public GameFlowScheme getRestartInstance()
							{
								return get();
							}

							@Override
							public void runScheme()
							{
								getter.get().executeFight(this);
							}
						};
					}	
				};

				Game.getGame().setScheme(schemeGetter.get());

				Game.getGame().getScheme().start();
			}
		});
			
		TextBounds bound = botFont.getBounds("Exit");
		getButtonManager().addButton(new ShadowedTextButton(Game.getGame().getWidth()/2 - bound.width / 2, 100, botFont, "Exit", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().delete(screen);
			}
		})
		{
			{
				setFont(botFont);
			}
		});
		
		setZIndex(J2hGame.GUI_Z_ORDER + 2);
	}
	
	public DeveloperMenu(Menu parent)
	{
		super(parent, true);
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
		
		J2hGame game = Game.getGame();
		
		Color color = Color.WHITE;
		
		topFont.setColor(color);
		botFont.setColor(color);
		
		String text = "Developer Menu";
		TextBounds bounds = topFont.getBounds(text);
		
		topFont.setColor(Color.BLACK);
		topFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, 900);

		topFont.setColor(Color.WHITE);
		topFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, 903);
		
		text = "-----";
		bounds = botFont.getBounds(text);

		botFont.setColor(Color.BLACK);
		botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, 820);

		botFont.setColor(Color.WHITE);
		botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, 823);
	}
	
	public static abstract class SpellCardGetter extends J2hObject
	{
		public abstract Spellcard get(Boss boss);
	}
	
	public void addButton(String name, final Runnable stageRunner)
	{
		TextBounds bound = botFont.getBounds(name);
		final DeveloperMenu screen = this;
		
		getButtonManager().addButton(new ShadowedTextButton(Game.getGame().getWidth()/2 - bound.width / 2, START_Y - Y_ADD * index, botFont, name, new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().delete(screen);
				
				for(StageObject obj : Game.getGame().getStageObjects())
				{
					obj.onDelete();
				}
				
				Game.getGame().getSpellcards().clear();
				Game.getGame().getStageObjects().clear();
				Game.getGame().clearObjects();
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				
				Game.getGame().spawn(Marisa.newInstance(Game.getGame().getWidth()/2, Game.getGame().getHeight()/2));
				Game.getGame().setPaused(false);
				Game.getGame().setOutOfGame(false);
				
				stageRunner.run();
				
				TouhouSounds.Hud.OK.play();
				
				Menu parent = screen.getParent();
				
				while(parent != null)
				{
					Game.getGame().delete(parent);
					parent = parent.getParent();
				}
			}
		})
		{
			{
				setFont(botFont);
			}
		});
		
		index++;
	}
	
	
	public void writeLine(String text, float x, float y)
	{
		writeLine(text, botFont, x, y);
	}
	
	public void writeLine(String text, BitmapFont font, float x, float y)
	{
		TextBounds bounds = font.getBounds(text);
		
		font.setColor(Color.BLACK);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2 + 3, y);

		font.setColor(Color.WHITE);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2, y + 3);
	}
}
