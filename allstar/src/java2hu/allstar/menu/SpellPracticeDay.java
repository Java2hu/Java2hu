package java2hu.allstar.menu;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.Days;
import java2hu.allstar.Days.CharacterData;
import java2hu.allstar.Days.EnvironmentType;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.background.bg3d.Background3D;
import java2hu.gameflow.GameFlowScheme;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.menu.Menu;
import java2hu.menu.ShadowedTextButton;
import java2hu.system.SaveableObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.Getter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class SpellPracticeDay extends AllStarMenu
{
	private int day;
	
	public int getDay()
	{
		return day;
	}
	
	public SpellPracticeDay(Menu parent, int day)
	{
		super(parent, false);
		this.day = day;
		
		final SpellPracticeDay screen = this;
		
		final J2hGame game = Game.getGame();
		
		x = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2;
		
		ArrayList<CharacterData> datas = Days.getDay(day);
		
		if(datas != null)
		{
			for(CharacterData data : datas)
			{
				if(data.bossGetter != null)
				{
					addBossMatch(data.name, data.environment, data.bossGetter);
				}
				else if(data.specialGetter != null)
				{
					addSpecialBossMatch(data.name, data.environment, data.specialGetter);
				}
			}
		}
		
		TextBounds bound = medFont.getBounds("Back");
		
		getButtonManager().addButton(new ShadowedTextButton(x - 100, 100, medFont, "Back", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().delete(screen);
			}
		}));
		
		setZIndex(parent.getZIndex() + 1);
	}
	
	int index = 0;
	
	float x = 0;
	
	final float START_Y = 700;
	final float Y_ADD = 50;
	
	final BitmapFont smallFont = getFont(FontType.SMALL);
	final BitmapFont largeFont = getFont(FontType.LARGE);
	final BitmapFont medFont = getFont(FontType.MEDIUM);
	
	public void addSpecialBossMatch(String name, final EnvironmentType type, final Getter<SpecialFlowScheme> create)
	{
		final Runnable startPractise = new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().setScheme(getSpecialSpellPractiseScheme(create, type));
				Game.getGame().getScheme().start();
			}
		};
		
		addButton(name, startPractise);
	}
	
	public void addBossMatch(String name, final EnvironmentType type, final Getter<AllStarBoss> create)
	{
		final Runnable startPractise = new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().setScheme(getSpellPractiseScheme(create, type));
				Game.getGame().getScheme().start();
			}
		};
		
		addButton(name, startPractise);
	}
	
	private void addButton(String name, final Runnable startPractise)
	{
		final SpellPracticeDay screen = this;
		
		getButtonManager().addButton(new ShadowedTextButton(x, START_Y - Y_ADD * index, smallFont, name, new Runnable()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL, true);
				
				System.out.println(game.getStageObjects().size());
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				
				System.out.println("HEREEEE");
				
				Game.getGame().setOutOfGame(false);
				Game.getGame().setPaused(false);
				
				TouhouSounds.Hud.OK.play();
				
				startPractise.run();
			}
		})
		{
			{
				setFont(smallFont);
			}
		});
		
		index++;
	}
	
	public GameFlowScheme getSpecialSpellPractiseScheme(final Getter<SpecialFlowScheme> create, final EnvironmentType type)
	{
		AllStarStageScheme scheme = new AllStarStageScheme(0)
		{
			@Override
			public GameFlowScheme getRestartInstance()
			{
				return getSpecialSpellPractiseScheme(create, type);
			}
			
			@Override
			public void runScheme()
			{
				loadBossAura();
				spawnPlayer();
				
				final SaveableObject<SpecialFlowScheme> save = new SaveableObject<SpecialFlowScheme>();
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						if(type.getSpawnEnvironment() != null)
						{
							Background3D bg = type.getSpawnEnvironment().get();
							
							System.out.println(bg);

							if(bg != null)
							{
								Game.getGame().spawn(bg);
								bg.setZIndex(-1000);
								bg.setFrameBuffer(getBossAura().getBackgroundBuffer());
							}
						}
						
						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								SpecialFlowScheme boss = create.get();

								save.setObject(boss);
							}
						}, type.getSpawnAnimationDelay());
					}
				}, 1);
				
				setWait(new WaitConditioner()
				{
					@Override
					public boolean returnTrueToWait()
					{
						return save.getObject() == null;
					}
				});
				
				doWait();

				final SpecialFlowScheme boss = save.getObject();
				
				boss.executeFight(this);
			}
		};
		
		return scheme;
	}
	
	public GameFlowScheme getSpellPractiseScheme(final Getter<AllStarBoss> create, final EnvironmentType type)
	{
		AllStarStageScheme scheme = new AllStarStageScheme(0)
		{
			@Override
			public GameFlowScheme getRestartInstance()
			{
				return getSpellPractiseScheme(create, type);
			}
			
			@Override
			public void runScheme()
			{
				loadBossAura();
				spawnPlayer();
				
				final SaveableObject<AllStarBoss> save = new SaveableObject<AllStarBoss>();
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						if(type.getSpawnEnvironment() != null)
						{
							Background3D bg = type.getSpawnEnvironment().get();

							if(bg != null)
							{
								Game.getGame().spawn(bg);
								bg.setZIndex(-1000);
								bg.setFrameBuffer(getBossAura().getBackgroundBuffer());
							}
						}
						
						Game.getGame().addTaskGame(new Runnable()
						{
							@Override
							public void run()
							{
								final AllStarBoss boss = create.get();
								
								((AllStarGame)Game.getGame()).setPC98(boss.isPC98());
								
								save.setObject(boss);
							}
						}, type.getSpawnAnimationDelay());
					}
				}, 1);
				
				setWait(new WaitConditioner()
				{
					@Override
					public boolean returnTrueToWait()
					{
						return save.getObject() == null;
					}
				});
				
				doWait();

				final AllStarBoss boss = save.getObject();

				if(boss.isPC98())
				{
					waitTicks(20);
				}
				
				boss.executeFight(this);
				
				getBossAura().clearAuras();
			}
		};
		
		return scheme;
	}
	
	public void writeLine(String text, float x, float y)
	{
		writeLine(text, smallFont, x, y);
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
