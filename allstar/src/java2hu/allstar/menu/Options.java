package java2hu.allstar.menu;

import java2hu.Game;
import java2hu.events.EventHandler;
import java2hu.events.EventListener;
import java2hu.events.input.KeyDownEvent;
import java2hu.menu.ButtonManager;
import java2hu.menu.ButtonManager.Button;
import java2hu.menu.Menu;
import java2hu.object.DrawObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.Getter;
import java2hu.util.ImageUtil;
import java2hu.util.Setter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.utils.Disposable;

public class Options extends AllStarMenu
{
	public Options(Menu parent)
	{
		super(parent, true);
		
		BitmapFont medFont = getFont(FontType.MEDIUM);
		BitmapFont smallFont = getFont(FontType.SMALL);
		
		// Music button for J2hGame.getMusicModifier()
		{
			String text = "Music";

			TextBounds bound = medFont.getBounds(text);

			final Button musicButton = new ShadowedTextButton(100, 800, smallFont, text, new Runnable()
			{
				@Override
				public void run()
				{

				}
			});

			getButtonManager().addButton(musicButton);

			final PercentButtonHelper pbhMusic = new PercentButtonHelper(getButtonManager(), musicButton, new Getter<Float>()
					{
				@Override
				public Float get()
				{
					return game.getMusicModifier();
				};
					}, new Setter<Float>()
					{
						@Override
						public void set(Float t)
						{
							game.setMusicModifier(t);
						}
					}, 0.02f);

			pbhMusic.setZIndex(getZIndex() + 2);

			game.spawn(pbhMusic);

			addDisposable(new Disposable()
			{
				@Override
				public void dispose()
				{
					game.delete(pbhMusic);
				}
			});
		}
		
		// Sound button for J2hGame.getSoundModifier()
		{
			String text = "Sound";

			TextBounds bound = medFont.getBounds(text);

			final Button soundButton = new ShadowedTextButton(100, 750, smallFont, text, new Runnable()
			{
				@Override
				public void run()
				{

				}
			});

			getButtonManager().addButton(soundButton);

			final PercentButtonHelper pbhSound = new PercentButtonHelper(getButtonManager(), soundButton, new Getter<Float>()
			{
				@Override
				public Float get()
				{
					return game.getSoundModifier();
				};
			},
			new Setter<Float>()
			{
				@Override
				public void set(Float t)
				{
					game.setSoundModifier(t);
				}
			}, 0.02f);

			pbhSound.setZIndex(getZIndex() + 2);

			game.spawn(pbhSound);

			addDisposable(new Disposable()
			{
				@Override
				public void dispose()
				{
					game.delete(pbhSound);
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
				
				Game.getGame().delete(Options.this);
			}
		}));
	}
	
	@Override
	public void onHide()
	{
		disposeAll();
		
		super.onHide();
	}
	
	public static class PercentButtonHelper extends DrawObject implements EventListener
	{
		private Button button;
		private ButtonManager manager;
		
		private Getter<Float> getValue;
		private Setter<Float> setValue;
		
		private float increment;
		
		public PercentButtonHelper(ButtonManager manager, Button button, Getter<Float> getValue, Setter<Float> setValue, float increment)
		{
			this.button = button;
			this.manager = manager;
			
			this.getValue = getValue;
			this.setValue = setValue;
			
			this.increment = increment;
			
			game.registerEvents(this);
			
			setZIndex(1000);
		}
		
		private boolean isActive()
		{
			return manager.getSelectedButton().equals(button);
		}
		
		@EventHandler
		public void keyDown(KeyDownEvent event)
		{
			int keyCode = event.getKey();
			
			if(!isActive())
				return;
			
			if(keyCode == Keys.RIGHT)
			{
				float get = getValue.get();
				
				float newAmount = Math.max(0, Math.min(1, get + increment));
				
				System.out.println("New amount: " + newAmount);
				
				setValue.set(newAmount);
			}
			else if(keyCode == Keys.LEFT)
			{
				float get = getValue.get();
				
				float newAmount = Math.max(0, Math.min(1, get - increment));
				
				System.out.println("New amount: " + newAmount);
				
				setValue.set(newAmount);
			}
		}
		
		private Texture gray = ImageUtil.makeDummyTexture(Color.GRAY, 1, 1);
		private Texture white = ImageUtil.makeDummyTexture(Color.WHITE, 1, 1);
		
		@Override
		public void onDraw()
		{
			float posX = button.getX() + 200;
			float posY = button.getY();
			
			float width = 200;
			float height = 40;
			
			float value = getValue.get();
			float whiteWidth = value * width;
			
			game.batch.draw(gray, posX, posY - height, width, height);
			game.batch.draw(white, posX, posY - height, whiteWidth, height);
			
			BitmapFont font = getFont(FontType.SMALL);
			
			font.setColor(Color.WHITE);
			
			font.draw(game.batch, Math.round(value * 100) + "%", posX + width + 20, posY - 2);
		}
		
		@Override
		public boolean isActiveDuringPause()
		{
			return true;
		}
		
		@Override
		public void onUpdate(long tick)
		{
			super.onUpdate(tick);
			
			if(!isActive())
				return;
			
			if(game.getActiveTick() % 2 == 0)
			{
				if(Gdx.input.isKeyPressed(Keys.RIGHT))
				{
					keyDown(new KeyDownEvent(Keys.RIGHT));
				}
				else if(Gdx.input.isKeyPressed(Keys.LEFT))
				{
					keyDown(new KeyDownEvent(Keys.LEFT));
				}
			}
		}
	}
}
