package java2hu.allstar.dialogue;

import java2hu.Loader;
import java2hu.allstar.dialogue.Marisa98DP.Marisa98DPFace;
import java2hu.conversation.DialogueParticipant;
import java2hu.conversation.IDialogueFaceEnum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Marisa98DP extends DialogueParticipant<Marisa98DPFace>
{
	public Marisa98DP()
	{
		super(Marisa98DPFace.IDLE, Facing.RIGHT);
		
		setYOffset(100);
	}
	
	public enum Marisa98DPFace implements IDialogueFaceEnum
	{
		ANNOYED, HAPPY, IDLE, IDIOCY, EXCITED;
		
		private Texture texture;
		
		private Marisa98DPFace()
		{
			texture = Loader.texture(Gdx.files.internal("player/marisa/faces_98/" + name() + ".png"));
		}

		@Override
		public Sprite getSprite()
		{
			Sprite sprite = new Sprite(texture);
			
			sprite.setScale(3f);
			sprite.setOrigin(sprite.getWidth() * sprite.getScaleX() / 2f, sprite.getHeight() * sprite.getScaleY() / 2f);
			
			return sprite;
		}
		
		@Override
		public Facing getFacing()
		{
			return Facing.RIGHT;
		}
	}
}
