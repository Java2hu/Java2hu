package java2hu.menu;

import java.util.HashMap;
import java2hu.Game;
import java2hu.events.EventHandler;
import java2hu.events.EventListener;
import java2hu.events.input.KeyDownEvent;
import java2hu.overwrite.J2hObject;
import java2hu.touhou.sounds.TouhouSounds;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A simple manager class for a list of buttons controlled by the up/down keys.
 * The order in which you add the buttons is the selection order for up/down
 */
public class ButtonManager implements EventListener
{
	private HashMap<Integer, Button> buttons = new HashMap<Integer, ButtonManager.Button>();
	int currentSelected = 0;
	
	boolean gameTick = true;
	long startTick = 0;
	
	public ButtonManager()
	{
		startTick = Game.getGame().getActiveTick();
		gameTick = Game.getGame().getActiveTick() == Game.getGame().getTick();
	}
	
	public int getNextId()
	{
		if(buttons.isEmpty())
			return 0;
		
		int largest = 0;
		
		for(Integer integer : buttons.keySet())
		{
			if(integer > largest)
				largest = integer;
		}
		
		return largest + 1;
	}
	
	public void addButton(Button button)
	{
		buttons.put(getNextId(), button);
	}
	
	float red = 0.5f;
	boolean toRed = true;
	
	public void draw()
	{
		Color previous = Game.getGame().font.getColor();
		
		Game.getGame().font.setColor(Color.GRAY);
		
		for(int i = 0; i < getNextId(); i++)
		{
			Button b = buttons.get(i);
			
			boolean selected = i == currentSelected;
			
			if(b instanceof TextButton && selected)
				((TextButton)b).getFont().setColor(new Color(red, 1f - red, 1f - red, 1f));
			
			b.draw(selected);
			
			if(b instanceof TextButton && selected)
				((TextButton)b).getFont().setColor(((TextButton)b).getInactiveColor());
		}
		
		Game.getGame().font.setColor(previous);
	}
	
	int ticksInactive = 20; // Dont handle anything for 10 ticks, so that concurrent key presses dont open up a menu 1000x
	
	public void update()
	{
		if(ticksInactive > 0)
		{
			ticksInactive--;
			return;
		}
		
		if(red <= 0.8f && toRed)
		{
			red += 0.005f;
			
			if(red > 0.8f)
				toRed = false;
		}
		else if(red >= 0.5f && !toRed)
		{
			red -= 0.005f;
			
			if(red < 0.5f)
			{
				toRed = true;
			}
		}
	}
	
	public String name = "ButtonManager";
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@EventHandler
	public void keyDown(KeyDownEvent event)
	{
		int keyCode = event.getKey();
		
		if(ticksInactive > 0)
		{
			return;
		}
		
		if(keyCode == Keys.DOWN || keyCode == Keys.UP)
		{
			TouhouSounds.Hud.SWITCH.play();
		}
		
		if(keyCode == Keys.DOWN)
		{
			int nextId = getNextId();
			
			if(currentSelected + 1 == nextId)
				currentSelected = 0;
			else
				currentSelected++;
		}
		else if(keyCode == Keys.UP)
		{
			if(currentSelected - 1 < 0)
				currentSelected = getNextId() - 1;
			else
				currentSelected--;
		}
		else if(keyCode == Keys.ENTER || keyCode == Keys.Z)
		{
			System.out.println("OnClick" + toString());
			ticksInactive = 20;
			buttons.get(currentSelected).getOnClick().run();
		}
	}
	
	public Button getSelectedButton()
	{
		return buttons.get(currentSelected);
	}
	
	public static class SpriteButton extends Button
	{
		Sprite sprite;
		
		public SpriteButton(float x, float y, Sprite sprite, Runnable onClick)
		{
			super(x, y, onClick);
			this.sprite = sprite;
		}
		
		@Override
		public void draw(boolean active)
		{
			sprite.setPosition(x, y);
			sprite.draw(Game.getGame().batch);
		}
		
		public Sprite getSprite()
		{
			return sprite;
		}
		
		public void setSprite(Sprite sprite)
		{
			this.sprite = sprite;
		}
	}
	
	public static class TextButton extends Button
	{
		String text;
		BitmapFont font = Game.getGame().font;
		Color inactiveColor = Color.WHITE;
		
		public TextButton(float x, float y, String string, Runnable onClick)
		{
			super(x, y, onClick);
			this.text = string;
		}
		
		@Override
		public void draw(boolean active)
		{
			if(!active)
				font.setColor(inactiveColor);
			
			font.draw(Game.getGame().batch, text, getX(), getY());
		}
		
		public String getText()
		{
			return text;
		}
		
		public void setText(String text)
		{
			this.text = text;
		}
		
		public BitmapFont getFont()
		{
			return font;
		}
		
		public void setFont(BitmapFont font)
		{
			this.font = font;
		}
		
		public Color getInactiveColor()
		{
			return inactiveColor;
		}
		
		public void setInactiveColor(Color inactiveColor)
		{
			this.inactiveColor = inactiveColor;
		}
	}
	
	public abstract static class Button extends J2hObject
	{
		float x;
		float y;
		Runnable onClick;

		public Button(float x, float y, Runnable onClick)
		{
			this.onClick = onClick;
			this.x = x;
			this.y = y;
		}
		
		public abstract void draw(boolean active);
		
		public float getX()
		{
			return x;
		}
		
		public void setX(float x)
		{
			this.x = x;
		}
		
		public float getY()
		{
			return y;
		}
		
		public void setY(float y)
		{
			this.y = y;
		}
		
		public Runnable getOnClick()
		{
			return onClick;
		}

		public void setOnClick(Runnable onClick)
		{
			this.onClick = onClick;
		}
	}
}
