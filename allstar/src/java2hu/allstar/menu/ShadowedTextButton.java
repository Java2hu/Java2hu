package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.menu.ButtonManager.TextButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ShadowedTextButton extends TextButton
{
	public ShadowedTextButton(float x, float y, BitmapFont botFont, String string, Runnable onClick)
	{
		super(x, y, string, onClick);
		setFont(botFont);
		setInactiveColor(Color.GRAY);
	}
	
	@Override
	public void draw(boolean active)
	{
		Color previous = getFont().getColor();
		getFont().setColor(Color.BLACK);
		
		getFont().draw(Game.getGame().batch, getText(), getX() + 3, getY() - 3);
		getFont().setColor(previous);
		
		getFont().setScale(1f);
		
		super.draw(active);
	}
}
