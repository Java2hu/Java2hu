package java2hu.menu;

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
	
	public ShadowedTextButton(float x, float y, String string, Runnable runnable)
	{
		super(x, y, string, runnable);
	}

	@Override
	public void draw(boolean active)
	{
		Color previous = getFont().getColor();
		getFont().setColor(Color.BLACK);
		
		getFont().draw(Game.getGame().batch, getText(), getX() + offset, getY() - offset);
		getFont().setColor(previous);
		
		getFont().setScale(1f);
		
		super.draw(active);
	}
	
	private int offset = 2;
	
	/**
	 * How many units the shadow is offset from the normal text.
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	
	/**
	 * How many units the shadow is offset from the normal text.
	 */
	public int getOffset()
	{
		return offset;
	}
}
