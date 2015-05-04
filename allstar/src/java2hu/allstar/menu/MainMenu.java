package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.AllStarGame;
import java2hu.allstar.ExtraStageScheme;
import java2hu.menu.Menu;
import java2hu.overwrite.J2hMusic;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.Gdx;
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
		
		addButton(width / 2f, 650, botFont, "Play", new Runnable()
		{
			@Override
			public void run()
			{
				game.clear(ClearType.ALL, true);
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				
				Game.getGame().onStartGame();
				
				Game.getGame().setPaused(false);
				Game.getGame().setOutOfGame(false);
				
				TouhouSounds.Hud.OK.play();
			}
		});
		
		ShadowedTextButton button = addButton(width / 2f, 600, botFont, "Extra Start", new Runnable()
		{
			@Override
			public void run()
			{
				String haha = "Well wouldn't you loooooooveeeee to know what's going to be here (^-^)";
				
				if(true)
					return;
				
				game.clear(ClearType.ALL, true);
				
				((AllStarGame)Game.getGame()).score = 0;
				((AllStarGame)Game.getGame()).deaths = 0;
				
				Game.getGame().setScheme(new ExtraStageScheme());
				Game.getGame().getScheme().start();
				
				Game.getGame().setPaused(false);
				Game.getGame().setOutOfGame(false);
				
				TouhouSounds.Hud.OK.play();
			}
		});
		
		button.setInactiveColor(Color.DARK_GRAY);
		
		addButton(width / 2f, 550, botFont, "Spell Practice", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new SpellPractice(menu));
			}
		});
		
		addButton(width / 2f, 500, botFont, "Spellcard Vault", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new SpellVault(menu));
			}
		});
		
		addButton(width / 2f, 450, botFont, "Credits", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				bgm.pause();
				
				Game.getGame().spawn(new Credits(menu));
			}
		});
		
		addButton(width / 2f, 400, botFont, "Pre-start Options", new Runnable()
		{
			@Override
			public void run()
			{
				((AllStarGame)game).onPreGameSettings();
			}
		});
		
		addButton(width / 2f, 350, botFont, "Developer Menu", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().spawn(new DeveloperMenu(menu));
			}
		});
		
		button = addButton(width / 2f, 300, botFont, "Options", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				game.spawn(new Options(menu));
			}
		});
		
		button.setInactiveColor(Color.DARK_GRAY);
		
		addButton(width / 2f, 250, botFont, "Exit", new Runnable()
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
		
		String version = "0.02 (Demo #2)";
		
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
