package java2hu.conversation;

import java2hu.Game;
import java2hu.Loader;
import java2hu.conversation.DDCDialogueTextBalloon.DDCBalloonType;
import java2hu.conversation.DialogueParticipant.Facing;
import java2hu.touhou.font.TouhouFont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class DDCDialogueTextBalloon<DDCBalloonType> extends DialogueTextBalloon
{
	BitmapFont font = TouhouFont.get(17);
	Texture texture = Loader.texture(Gdx.files.internal("sprites/ddc_balloons.png"));
	Sprite arrow;
	Sprite section;
	Sprite end;
	
	{
		addDisposable(texture);
	}
	
	private DDCBalloonType type = DDCBalloonType.NORMAL;
	
	public DDCBalloonType getType()
	{
		return type;
	}
	
	@Override
	public float getWidth()
	{
		return 0;
	}

	@Override
	public float getHeight()
	{
		return 0;
	}

	@Override
	public void onDraw()
	{
		if(getText() == null || arrow == null || section == null || end == null)
			return;
		
		{
			float startX = 200;
			float startY = 100;
			
			boolean left = getSpeechArrow() == Facing.LEFT;
			
			if(!left)
				startX = Game.getGame().getWidth() - 200;
			
			arrow.setPosition(left ? startX : startX - arrow.getWidth(), startY);
			
			if(left)
				arrow.setFlip(false, false);
			else
				arrow.setFlip(true, false);
			
			arrow.draw(Game.getGame().batch);
			
			BitmapFont f = font;
			f.setScale(1.4f);
			f.setColor(Color.BLACK);
			
			TextBounds longestBounds = new TextBounds();
			
			for(String line : getText().split("\n"))
			{
				TextBounds bound = f.getBounds(line);
				
				if(longestBounds.width < bound.width)
				{
					longestBounds.set(bound);
				}
			}
			
			TextBounds b = longestBounds;
			
			float sectionSize = left ? b.width - arrow.getWidth() / 2f - end.getWidth() : b.width - end.getWidth() / 2f;
			int size = (int)(((int)(sectionSize / section.getWidth()) + (left ? +1 : 0)) * section.getWidth());
			
			if(size < section.getWidth())
			{
				size = 0;
			}
			else
			{
				for(float i = 0; i <= size; i += section.getWidth())
				{
					if(!left && i == 0)
						continue;

					section.setPosition(left ? startX + arrow.getWidth() + i : startX - arrow.getWidth() - i, startY);
					section.draw(Game.getGame().batch);
				}
			}
			
			if(left)
				end.setFlip(false, false);
			else
				end.setFlip(true, false);
			
			end.setPosition(left ? startX + arrow.getWidth() + size + end.getWidth() / 2f : startX - arrow.getWidth() - size - end.getWidth(), startY);
			end.draw(Game.getGame().batch);
			
			boolean singleLine = !getText().contains("\n");
			
			float offset = 0;
			
			if(singleLine)
				offset = getType().getSingleOffset();
			else
				offset = getType().getDoubleOffset();
			
			int i = 0;
			for(String line : getText().split("\n"))
			{
				TextBounds lineBounds = f.getBounds(line);
				f.setColor(Color.DARK_GRAY);
				f.draw(Game.getGame().batch, line, startX + 2 + (left ? 30 : -b.width - 40), startY - 2 + offset - i * 35);
				f.setColor(Color.BLACK);
				f.draw(Game.getGame().batch, line, startX + (left ? 30 : -b.width - 40), startY + offset - i * 35);
				i++;
			}
		}
	}
	
	private String lastText = null;
	private DDCBalloonType lastType = null;

	@Override
	public void onUpdate(long tick)
	{
		if(lastText == null || lastText != getText() || lastType == null || lastType != getType())
		{
			if(getText() != null && getType() != null)
			{
				lastText = getText();
				lastType = getType();

				boolean singleLine = !lastText.contains("\n");

				if(singleLine)
				{
					int yOffset = 0;
					int height = 0;

					switch(lastType)
					{
						case NORMAL:
							height = 96;
							break;
						case SCREAM:
							yOffset = 96;
							height = 118;
							break;
						case THINK:
							yOffset = 96 + 118; // 214
							height = 118;
							break;
						case QUESTION:
							yOffset = 96 + 118 + 118; // 332
							height = 118;
							break;
					}

					section = new Sprite(texture, 0, yOffset, 28, height);
					end = new Sprite(texture, 28, yOffset, 44, height);
					arrow = new Sprite(texture, 118, yOffset, 78, height);
				}
				else
				{
					int yOffset = 0;
					int height = 0;

					switch(lastType)
					{
						case NORMAL:
							yOffset = 450;
							height = 128;
							break;
						case SCREAM:
							yOffset = 578;
							height = 144;
							break;
						case THINK:
							yOffset = 732;
							height = 144;
							break;
						case QUESTION:
							yOffset = 880;
							height = 144;
							break;
					}

					section = new Sprite(texture, 0, yOffset, 28, height);
					end = new Sprite(texture, 28, yOffset, 44, height);
					arrow = new Sprite(texture, 118, yOffset, 78, height);
				}
			}
			else
			{
				section = null;
				end = null;
				arrow = null;
			}
		}
	}
	
	public static enum DDCBalloonType implements ITextBalloonType
	{
		NORMAL(45, 80), SCREAM(65, 95), THINK(60, 88), QUESTION(63, 90);
		
		private float singleOffset;
		private float doubleOffset;
		
		private DDCBalloonType(float singleTextOffset, float doubleTextOffset)
		{
			this.singleOffset = singleTextOffset;
			this.doubleOffset = doubleTextOffset;
		}
		
		public float getSingleOffset()
		{
			return singleOffset;
		}
		
		public float getDoubleOffset()
		{
			return doubleOffset;
		}
		
		public static Runnable setToType(final DDCDialogueTextBalloon balloon, final DDCBalloonType type)
		{
			return new Runnable()
			{
				@Override
				public void run()
				{
					balloon.setType(type);
				};
			};
		}
	}

	@Override
	public void setType(ITextBalloonType t)
	{
		this.type = (DDCBalloonType)t;
	}
}
