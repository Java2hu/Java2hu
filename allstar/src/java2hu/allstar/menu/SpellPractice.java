package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.allstar.Days;
import java2hu.menu.Menu;
import java2hu.menu.ShadowedTextButton;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class SpellPractice extends AllStarMenu
{
	int index = 0;
	
	float x = 0;
	
	final float START_Y = 700;
	final float Y_ADD = 50;
	
	public SpellPractice(Menu parent)
	{
		super(parent, true);
	}
	
	final BitmapFont smallFont = getFont(FontType.SMALL);
	final BitmapFont largeFont = getFont(FontType.LARGE);
	final BitmapFont medFont = getFont(FontType.MEDIUM);
	
	{
		final SpellPractice screen = this;
		
		final J2hGame game = Game.getGame();
		
		x = game.getMinX() + (game.getMaxX() - game.getMinX()) / 2 - 300;
		
		for(final int day : Days.getDays())
		{
			addButton("Day " + day, new Runnable()
			{
				@Override
				public void run()
				{
					SpellPracticeDay newScreen = new SpellPracticeDay(screen, day);
					game.spawn(newScreen);
				}
			});
		}
		
		TextBounds bound = medFont.getBounds("Exit");
		
		getButtonManager().addButton(new ShadowedTextButton(bound.width / 2, 100, medFont, "Exit", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().delete(screen);
			}
		}));
		
		setZIndex(J2hGame.GUI_Z_ORDER + 2);
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
		
		J2hGame game = Game.getGame();
		
		Color color = Color.WHITE;
		
		largeFont.setColor(color);
		smallFont.setColor(color);
		
		String text = "Spell Practice";
		TextBounds bounds = largeFont.getBounds(text);
		
		largeFont.setColor(Color.BLACK);
		largeFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, 900);

		largeFont.setColor(Color.WHITE);
		largeFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, 903);
		
		text = "-----";
		bounds = smallFont.getBounds(text);

		smallFont.setColor(Color.BLACK);
		smallFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, 820);

		smallFont.setColor(Color.WHITE);
		smallFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, 823);
	}
	
	private void addButton(String name, final Runnable onClick)
	{
		TextBounds bound = smallFont.getBounds(name);
		
		final SpellPractice screen = this;
		
		getButtonManager().addButton(new ShadowedTextButton(x, START_Y - Y_ADD * index, smallFont, name, new Runnable()
		{
			@Override
			public void run()
			{
				onClick.run();
			}
		})
		{
			{
				setFont(smallFont);
			}
		});
		
		index++;
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
