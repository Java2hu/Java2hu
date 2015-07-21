package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.menu.Menu;
import java2hu.menu.ShadowedTextButton;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

public class Story extends AllStarMenu
{
	{
		getButtonManager().name = "storyscreen";
	}
	
	int index = 0;
	
	final BitmapFont botFont = getFont(FontType.SMALL);
	final BitmapFont topFont = getFont(FontType.LARGE);
	final BitmapFont bigFont = getFont(FontType.MEDIUM);
	
	{
		final Story screen = this;
		
		final J2hGame game = Game.getGame();
		
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
	
	public Story(Menu parent)
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
		
		String text = "Story";
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
		
		String[] story = { 
				"Recently Gensokyo has been restless, but there seems to be almost no reason for it.",
				"",
				"Marisa: I haven't been able to fly through the forest without getting attacked by... pretty much everything.",
				"Reimu: The shrine is getting visited a lot as well...",
				"Marisa: That's terrible, how did that ever happen?!",
				"Reimu: More YOUKAI, not followers...",
				"Sanae: We've been getting unwanted visitors as well, even Suwako and Kanako have been giving me evil eyes. *shudders*",
				"Reimu: So what's up with that, if even those 2 are on your back.",
				"Sanae: I would ask them, but they have been almost avoiding me it seems.",
				"Marisa: I guess the only way well know is by beating them up, heh.",
				"Reimu: An incident it is huh? Time to pull out the Ying Yang orbs and beat up youkai!",
				"Reimu: To question them of course!",
				"",
				};
		
		int y = 750;
		
		botFont.setScale(0.4f);
		
		for(String s : story)
		{
			text = s;
			bounds = botFont.getBounds(text);

			botFont.setColor(Color.BLACK);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, y);

			botFont.setColor(Color.WHITE);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, y + 3);
			
			y -= 20;
		}
		
		y -= 20;
		
		writeLine("Dev Notes", topFont, game.getWidth() / 2f, y);
		
		topFont.setScale(0.5f);
		
		writeLine("-----", topFont, game.getWidth() / 2f, y - 75);
	
		topFont.setScale(1f);
		
		String[] devnotes = { 
				"Sanae and Reimu are not yet playable.",
				"Right now, the game is only about 15% done, but one of the largest parts,",
				"the internal engine, effects, etc, are done.",
				"So after this it's basically only new spells and stuff getting added,",
				"and less about improvements to the engine."
				};

		y -= 120;
		
		for(String s : devnotes)
		{
			text = s;
			bounds = botFont.getBounds(text);

			botFont.setColor(Color.BLACK);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2, y);

			botFont.setColor(Color.WHITE);
			botFont.draw(game.batch, text, Game.getGame().getWidth()/2 - bounds.width / 2 - 3, y + 3);
			
			y -= 20;
		}
		
		botFont.setScale(1f);
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
