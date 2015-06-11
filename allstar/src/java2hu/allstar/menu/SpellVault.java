package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.AllStarStageScheme;
import java2hu.allstar.backgrounds.MistLakeBG;
import java2hu.allstar.enemies.day1.Wakasagihime;
import java2hu.allstar.spellcards.LaserTestSpell;
import java2hu.allstar.spellcards.ShouCurvingNonSpell;
import java2hu.allstar.spellcards.YouAreTheBoss;
import java2hu.background.bg3d.Background3D;
import java2hu.gameflow.GameFlowScheme;
import java2hu.menu.Menu;
import java2hu.object.StageObject;
import java2hu.object.enemy.greater.Boss;
import java2hu.overwrite.J2hObject;
import java2hu.spellcard.Spellcard;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.BossUtil;
import java2hu.util.Getter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.utils.Disposable;

public class SpellVault extends AllStarMenu
{
	{
		getButtonManager().name = "spellvault";
	}
	
	int index = 0;
	
	final float START_Y = 650;
	final float Y_ADD = 50;
	
	final BitmapFont botFont = getFont(FontType.SMALL);
	final BitmapFont topFont = getFont(FontType.LARGE);
	final BitmapFont bigFont = getFont(FontType.MEDIUM);
	
	public static abstract class TestScheme extends AllStarStageScheme
	{
		public TestScheme()
		{
			super(0);
		}

		public abstract void startSpellcard(Boss boss);
		
		@Override
		public void runScheme()
		{
			final J2hGame g = Game.getGame();
			
			loadBossAura();
			spawnPlayer();
			
			g.addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					final Wakasagihime dummy = new Wakasagihime(100f, Game.getGame().getWidth() / 2, 700);
					g.spawn(dummy);
					
					Background3D bg = new MistLakeBG();
					
					bg.setZIndex(-100);
					getBossAura().setZIndex(-1);
					
					bg.setFrameBuffer(getBossAura().getBackgroundBuffer());
					
					g.spawn(bg);
					
					g.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							BossUtil.startFight(dummy);
							
							final Color blue = Color.BLUE.cpy().mul(0.7f);
							
							BossUtil.addBossEffects(dummy, blue, blue);
						}
					}, 1 * 60);
					
					final Music bgm = Gdx.audio.newMusic(Gdx.files.internal("music/wakasagihime/bgm.mp3"));
					bgm.setVolume(1f * Game.getGame().getMusicModifier());
					bgm.setLooping(true);
					
					dummy.getBackgroundMusic().stop();
					dummy.getBackgroundMusic().dispose();
		
					dummy.setBackgroundMusic(bgm);
					dummy.getBackgroundMusic().play();
					
					dummy.addDisposable(new Disposable()
					{
						@Override
						public void dispose()
						{
							bgm.dispose();
						}
					});
					
//					DrawObject dummyText = new DrawObject()
//					{
//						@Override
//						public void onDraw()
//						{
//							String text = "Dummy Boss";
//							TextBounds bounds = g.font.getBounds(text);
//							g.font.draw(g.batch, text, dummy.getX() - bounds.width / 2, dummy.getY() + 100);
//						}
//					};
//					
//					g.spawn(dummyText);
					
					game.addTaskGame(new Runnable()
					{
						@Override
						public void run()
						{
							startSpellcard(dummy);
						}
					}, 50);
				}
			}, 10);
		}
	}
	
	{
		final SpellVault screen = this;
		
		final J2hGame game = Game.getGame();
		
		final float startX = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2;
		final float startY = Game.getGame().getHeight() - 150;
		
		addCard("Shou Laser Test", new SpellCardGetter()
		{
			@Override
			public Spellcard get(Boss boss)
			{
				return new ShouCurvingNonSpell(boss);
			}
		});
		
		addCard("Laser Draw Test", new SpellCardGetter()
		{
			@Override
			public Spellcard get(Boss boss)
			{
				return new LaserTestSpell(boss);
			}
		});
		
		addCard("You are the boss", new SpellCardGetter()
		{
			@Override
			public Spellcard get(Boss boss)
			{
				return new YouAreTheBoss(boss);
			}
		});
		
//		addCard("4/20 Special", new Getter<GameFlowScheme>()
//		{
//			@Override
//			public GameFlowScheme get()
//			{
//				return new BlazeItScheme();
//			}
//		});
			
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
	
	public SpellVault(Menu parent)
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
		
		String text = "The Vault";
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
		
		botFont.setScale(0.6f);
		
		String[] explanation = { 
				"Spellcards not made to be in the game",
				"Usually for tests/examples, I figured I might as well let interested people out there try it.",
				};
		
		int y = 750;
		
		for(String s : explanation)
		{
			text = s;
			bounds = botFont.getBounds(text);

			botFont.setColor(Color.BLACK);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, y);

			botFont.setColor(Color.WHITE);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, y + 3);
			
			y -= 30;
		}
		
		botFont.setScale(1f);
	}
	
	public static abstract class SpellCardGetter extends J2hObject
	{
		public abstract Spellcard get(Boss boss);
	}
	
	public void addCard(String name, final Getter<GameFlowScheme> getter)
	{
		final Runnable start = new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().setScheme(getter.get());
				
				Game.getGame().getScheme().start();
			}
		};
		
		addButton(name, start);
	}
	
	public void addCard(String name, final SpellCardGetter getter)
	{
		final Getter<TestScheme> schemeGetter = new Getter<SpellVault.TestScheme>()
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
					public void startSpellcard(Boss boss)
					{
						Spellcard card = getter.get(boss);
						
						Game.getGame().startSpellCard(card);
					}
				};
			}
		};
		
		final Runnable start = new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().setScheme(schemeGetter.get());
				
				Game.getGame().getScheme().start();
			}
		};
		
		addButton(name, start);
	}
	
	public void addButton(String name, final Runnable stageRunner)
	{
		TextBounds bound = botFont.getBounds(name);
		final SpellVault screen = this;
		
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
				
				stageRunner.run();
				Game.getGame().setPaused(false);
				Game.getGame().setOutOfGame(false);
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
