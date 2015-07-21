package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.Days;
import java2hu.allstar.ExtraStageScheme;
import java2hu.events.EventHandler;
import java2hu.events.EventListener;
import java2hu.events.game.ObjectRemoveEvent;
import java2hu.events.input.KeyDownEvent;
import java2hu.menu.Menu;
import java2hu.menu.ShadowedTextButton;
import java2hu.overwrite.J2hMusic;
import java2hu.system.SaveableObject;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class MainMenu extends AllStarMenu
{
	public MainMenu(Menu parent)
	{
		super(parent, true);
	}
	
	{
		getButtonManager().name = "MainMenu";
	}

	Music bgm = new J2hMusic(Gdx.audio.newMusic(Gdx.files.internal("music/menu.mp3")));

	{
		bgm.setPosition(0.5f);
		bgm.setVolume(0.6f);
		bgm.setLooping(true);
		
		final MainMenu menu = this;
		
		float width = Game.getGame().getWidth();
		
		BitmapFont botFont = getFont(FontType.SMALL);
		
		final SaveableObject<Integer> dayObject = new SaveableObject<Integer>(1);
		
		float y = 650; 
		
		final ShadowedTextButton play = addButton(width / 2f, y, botFont, "Play Day 1", new Runnable()
		{
			@Override
			public void run()
			{
				final int day = dayObject.getObject();
				
				boolean exists = Days.getDay(day) != null;
				
				if(!exists)
				{
					TouhouSounds.Hud.INVALID.play();
					return;
				}
				
				game.clear(ClearType.ALL, true);
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				((AllStarGame)Game.getGame()).day = day;
				
				Game.getGame().onStartGame();
				
				Game.getGame().setOutOfGame(false);
				Game.getGame().setPaused(false);
				
				TouhouSounds.Hud.OK.play();
			}
		});
		
		game.registerEvents(new EventListener()
		{
			private int day = 1;
			
			@EventHandler
			public void onDelete(ObjectRemoveEvent event)
			{
				if(event.getObject() == MainMenu.this)
				{
					game.unregisterEvents(this);
				}
			}
			
			@EventHandler
			public void onKeyDown(KeyDownEvent event)
			{
				if(event.getKey() == (Keys.RIGHT))
				{
					day = (day + 1) > 10 ? 1 : (day + 1);
				}
				else if(event.getKey() == (Keys.LEFT))
				{
					day = (day - 1) < 1 ? 10 : (day - 1);
				}
				else
					return;
				
				dayObject.setObject(day);
				
				boolean exists = Days.getDay(day) != null;
				
				final String text = "Play Day " + day;
				
				TextBounds b = play.getFont().getBounds(text);
				
				play.setInactiveColor(exists ? Color.GRAY : Color.DARK_GRAY);
				play.setText(text);
				play.setX(game.getWidth() / 2f - (b.width / 2f));
			}
		});
		
		y -= 50;
		
		ShadowedTextButton button = addButton(width / 2f, y, botFont, "Extra Start", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.INVALID.play();
				
				String haha = "Well wouldn't you loooooooveeeee to know what's going to be here (^-^)";
				
				System.out.println(haha);
				
				if(true)
					return;
				
				game.clear(ClearType.ALL, true);
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				
				Game.getGame().setScheme(new ExtraStageScheme());
				Game.getGame().getScheme().start();
				
				Game.getGame().setOutOfGame(false);
				Game.getGame().setPaused(false);
				
				TouhouSounds.Hud.OK.play();
			}
		});
		
		button.setInactiveColor(Color.DARK_GRAY);
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Spell Practice", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new SpellPractice(menu));
			}
		});
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Spellcard Vault", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new SpellVault(menu));
			}
		});
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Story / Dev Notes", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new Story(menu));
			}
		});
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Credits", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				bgm.pause();
				
				Game.getGame().spawn(new Credits(menu));
			}
		});
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Pre-start Options", new Runnable()
		{
			@Override
			public void run()
			{
				((AllStarGame)game).onPreGameSettings();
			}
		});
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Developer Menu", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new DeveloperMenu(menu));
			}
		});
		
		y -= 50;
		
		button = addButton(width / 2f, y, botFont, "Options", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				game.spawn(new Options(menu));
			}
		});
		
//		button.setInactiveColor(Color.DARK_GRAY);
		
		y -= 50;
		
		addButton(width / 2f, y, botFont, "Exit", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.CANCEL.play();
				
				Runtime.getRuntime().exit(0);
			}
		});
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
		
		BitmapFont topFont = getFont(FontType.LARGE);
		BitmapFont botFont = getFont(FontType.SMALL);
		
		Color color = Color.WHITE;
		
		topFont.setColor(color);
		botFont.setColor(color);
		
		drawLogoDefault();
		
		String version = "0.03 (Demo #3)";
		
		botFont.setScale(0.6f);
		
		TextBounds bounds = botFont.getBounds(version);
		writeLineCenteredShadow(version, botFont, 20 + bounds.width / 2f, 20 + bounds.height);
		
		botFont.setScale(1f);
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		
		bgm.stop();
	}
	
	@Override
	public void onHide()
	{
		super.onHide();
		
		System.out.println("Hiding main menu");
	}

	@Override
	public void onShow()
	{
		super.onShow();
		
		System.out.println("Showing main menu");
		
		bgm.play();
	}
}
