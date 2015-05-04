package java2hu.allstar.menu;

import java.util.HashMap;

import java2hu.Game;
import java2hu.Loader;
import java2hu.menu.ButtonManager;
import java2hu.menu.Menu;
import java2hu.touhou.font.TouhouFont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AllStarMenu extends Menu
{
	private static Texture texture = Loader.texture(Gdx.files.internal("title_1280.png"));
	
	public static Sprite getMenuBackground()
	{
		return new Sprite(texture);
	}
	
	private static HashMap<String, BitmapFont> fontMap = new HashMap<String, BitmapFont>();
	
	static
	{
		for(FontType t : FontType.values())
		{
			fontMap.put(t.name(), TouhouFont.get(t.getSize()));
		}
	}
	
	public static enum FontType
	{
		LARGE(100), MEDIUM(50), SMALL(38);
		
		private int size = 0;
		
		private FontType(int size)
		{
			this.size = size;
		}
		
		public int getSize()
		{
			return size;
		}
		
		@Override
		public String toString()
		{
			return name();
		}
	}
	
	public static BitmapFont getFont(FontType type)
	{
		return getFont(type.name());
	}
	
	public static BitmapFont getFont(String id)
	{
		if(!fontMap.containsKey(id))
		{
			return null;
		}
		
		return fontMap.get(id);
	}
	
	public boolean drawBackground = false;
	
	public AllStarMenu(Menu parent, boolean drawBackground)
	{
		super(parent);
		this.drawBackground = drawBackground;
	}

	private Sprite bg = getMenuBackground();
	
	private ButtonManager manager = new ButtonManager();
	
	{
		manager.name = "Menu";
	}
	
	public ButtonManager getButtonManager()
	{
		return manager;
	}

	public ShadowedTextButton addButton(float x, float y, BitmapFont font, String text, Runnable onClick)
	{
		TextBounds b = font.getBounds(text);
		ShadowedTextButton button = new ShadowedTextButton(x - b.width / 2f, y, font, text, onClick);
		
		manager.addButton(button);
		
		return button;
	}
	
	public void writeLineCenteredShadow(String text, float x, float y)
	{
		writeLineCenteredShadow(text, getFont(FontType.SMALL), x, y);
	}
	
	public void writeLineCenteredShadow(String text, BitmapFont font, float x, float y)
	{
		TextBounds bounds = font.getBounds(text);
		
		font.setColor(Color.BLACK);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2 + 3, y);

		font.setColor(Color.WHITE);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2, y + 3);
	}
	
	public void drawLogoDefault()
	{
		drawLogo(game.getWidth() / 2f, 790);
	}
	
	public void drawLogo(float x, float y)
	{
		writeLineCenteredShadow("Touhou", getFont(FontType.LARGE), x, y + 110);
		writeLineCenteredShadow("-----", getFont(FontType.SMALL), x, y + 30);
		writeLineCenteredShadow("All Star Danmaku", getFont(FontType.SMALL), x, y);
	}

	@Override
	public void onDraw()
	{
		if(drawBackground)
		{
			bg.setPosition(0, 0);
			bg.setSize(game.getWidth(), game.getHeight());
			bg.draw(game.batch);
		}

		manager.draw();
	}

	@Override
	public void onUpdate(long tick)
	{
		manager.update();
	}

	@Override
	public void onHide()
	{
		Game.getGame().unregisterEvents(manager);
	}

	@Override
	public void onShow()
	{
		Game.getGame().registerEvents(manager);
	}
}
