package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.menu.Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class LoadScreen extends AllStarMenu
{
	public LoadScreen(Menu parent)
	{
		super(parent, true);
	}

	@Override
	public void onDraw()
	{
		super.onDraw();
		
		J2hGame game = Game.getGame();
		
		BitmapFont topFont = getFont(FontType.LARGE);
		BitmapFont botFont = getFont(FontType.SMALL);
		
		Color color = Color.WHITE;
		
		topFont.setColor(color);
		botFont.setColor(color);
		
		drawLogoDefault();
		
		String dots = "";
		
		for(int i = 0; i < (float)Game.getGame().getActiveTick() / 100 % 3; i++)
		{
			dots += ".";
		}
		
		String text = "Loading" + dots;
		TextBounds bounds = botFont.getBounds(text);
		
		botFont.setColor(Color.BLACK);
		botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 + 3, 490);

		botFont.setColor(Color.WHITE);
		botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, 493);
	}
}
