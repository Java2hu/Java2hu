package java2hu.allstar.dialogue;

import java2hu.Loader;
import java2hu.allstar.dialogue.MarisaDP.MarisaDPFace;
import java2hu.conversation.IDialogueFaceEnum;
import java2hu.conversation.TouhouDP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MarisaDP extends TouhouDP<MarisaDPFace>
{
	public MarisaDP()
	{
		super(getBaseSprite(), MarisaDPFace.IDLE, Facing.LEFT, Facing.LEFT);
	}
	
	private static Texture texture;
	private static Sprite getBaseSprite()
	{
		texture = Loader.texture(Gdx.files.internal("player/marisa/faces/base.png"));
		
		return new Sprite(texture);
	}
	
	@Override
	public void positionFace(Sprite sprite)
	{
		super.positionFace(sprite);
	}
	
	public enum MarisaDPFace implements IDialogueFaceEnum
	{
		ANNOYED, SIGH, HAPPY, JOKING, IDLE, SHOCKED, IDIOCY, CONFUSED, YUKKURI;
		
		private Texture texture;
		
		private MarisaDPFace()
		{
			texture = Loader.texture(Gdx.files.internal("player/marisa/faces/" + name() + ".png"));
		}

		@Override
		public Sprite getSprite()
		{
			return new Sprite(texture);
		}
		
		@Override
		public Facing getFacing()
		{
			return Facing.LEFT;
		}
	}
}
