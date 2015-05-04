package java2hu.allstar.dialogue;

import java2hu.Loader;
import java2hu.allstar.dialogue.Shinki98DP.Shinki98DPFace;
import java2hu.conversation.DialogueParticipant;
import java2hu.conversation.IDialogueFaceEnum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Shinki98DP extends DialogueParticipant<Shinki98DPFace>
{
	public Shinki98DP()
	{
		super(Shinki98DPFace.IDLE, Facing.LEFT);
		
		setYOffset(100);
	}
	
	public enum Shinki98DPFace implements IDialogueFaceEnum
	{
		IDLE, HAND, ARMS_SPREAD, ANNOYED;
		
		private Texture texture;
		
		private Shinki98DPFace()
		{
			texture = Loader.texture(Gdx.files.internal("enemy/shinki/faces_98/" + name() + ".png"));
		}

		@Override
		public Sprite getSprite()
		{
			Sprite sprite = new Sprite(texture);
			
			sprite.setScale(3f);
			sprite.setOriginCenter();
			
			return sprite;
		}
		
		@Override
		public Facing getFacing()
		{
			return Facing.LEFT;
		}
	}
}
