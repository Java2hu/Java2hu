package java2hu.allstar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.menu.LoadScreen;
import java2hu.allstar.menu.MainMenu;
import java2hu.allstar.util.AllStarUtil;
import java2hu.object.StageObject;

public class AllStarGame extends J2hGame
{
	LoadScreen screen;
	
	public AllStarGame(int width, int height)
	{
		setSize(width, height);
	}

	@Override
	public void create()
	{
		super.create();
		
		scoreboardTexture = Loader.texture(Gdx.files.internal("scoreboard.png"));
		deathScore = new Sprite(scoreboardTexture);
	}
	
	public void onPreGameSettings()
	{
		
	}
	
	@Override
	public void onLoadStart()
	{
		screen = new LoadScreen(null);
		spawn(screen);
		Game.getGame().setPaused(true);
		
		super.onLoadStart();
	}
	
	@Override
	public void onLoadFinished()
	{
		if(screen != null)
			delete(screen);

		addTask(new Runnable()
		{
			@Override
			public void run()
			{
				MainMenu menu = new MainMenu(null);
				spawn(menu);
			}
		}, 1);
	}
	
	@Override
	public void onStartGame()
	{
		super.onStartGame();
		
		setPC98(false, true);
		
		setScheme(new AllStarStageScheme(1));
		getScheme().start();
	}
	
	public int deaths = 0;
	public int score = 0;
	private boolean isPC98 = false;
	
	public void setPC98(final boolean bool)
	{
		setPC98(bool, false);
	}
	
	public void setPC98(final boolean bool, boolean noAnimation)
	{
		if(noAnimation)
		{
			isPC98 = bool;
			return;
		}
		
		if(!this.isPC98 && bool)
		{
			AllStarUtil.doPC98EnterAnimation(Color.WHITE);
		}
		else if(this.isPC98 && !bool)
		{
			AllStarUtil.doPC98EndAnimation(Color.WHITE);
		}
		
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				isPC98 = bool;
				System.out.println("Pc98? " + isPC98);
			}
		}, 30);
	}
	
	public boolean isPC98()
	{
		return isPC98;
	}
	
	public Texture scoreboardTexture;
	public Sprite deathScore;
	
	@Override
	public void drawUI()
	{
		if(isOutOfGame())
			return;
		
		deathScore.setColor(new Color(52 / 256f, 99 / 256f, 229 / 256f, 1.0f));
		deathScore.setPosition(getWidth() - deathScore.getWidth() - 100, getHeight() - deathScore.getHeight());
		deathScore.setScale(1f, 1f);
		deathScore.draw(batch);
		
		String deathAmount = deaths + "x";
		TextBounds bounds = font.getBounds(deathAmount);
		
		font.draw(batch, deathAmount, deathScore.getX() + 25, deathScore.getY() + 15 + bounds.height);
		
		String deathLabel = "Died";
		bounds = font.getBounds(deathLabel);
		
		font.draw(batch, deathLabel, deathScore.getX() + 20, deathScore.getY() + 35 + bounds.height);
		
//		drawDebugUI();
	}
	
	@Override
	public void onPause()
	{
		for(StageObject obj : getStageObjects())
		{
			if(obj instanceof AllStarBoss)
			{
				if(((AllStarBoss)obj).getBackgroundMusic() != null)
					((AllStarBoss)obj).getBackgroundMusic().pause();
			}
		}
		
		super.onPause();
	}
	
	@Override
	public void onDePause()
	{
		for(StageObject obj : getStageObjects())
		{
			if(obj instanceof AllStarBoss)
			{
				if(((AllStarBoss)obj).getBackgroundMusic() != null)
					((AllStarBoss)obj).getBackgroundMusic().play();
			}
		}
		
		super.onDePause();
	};
	
	@Override
	public void onRetry()
	{
		clear(ClearType.ALL, true);
		
		setPC98(false, true);
		
		if(Game.getGame().getScheme() != null)
			Game.getGame().getScheme().stopScheme();

		addTask(new Runnable()
		{
			@Override
			public void run()
			{
				if(Game.getGame().getScheme() != null)
				{
					Game.getGame().setScheme(Game.getGame().getScheme().getRestartInstance());
					Game.getGame().getScheme().start();
				}
			}
		}, 5);

		((AllStarGame)Game.getGame()).deaths = 0;
	}
	
	@Override
	public void onToTitle()
	{
		clear(ClearType.ALL, true);
		
		setPC98(false, true);
		
		Game.getGame().clearObjects();
		Game.getGame().getSpellcards().clear();
		Game.getGame().getStageObjects().clear();

		Game.getGame().setOutOfGame(true);

		MainMenu menu = new MainMenu(null);
		spawn(menu);

		onDePause();
		Game.getGame().setPaused(true);

		if(Game.getGame().getScheme() != null)
			Game.getGame().getScheme().stopScheme();
	}
}
