package java2hu.allstar.dialogue;

import java2hu.Loader;
import java2hu.allstar.dialogue.ByakurenDP.ByakurenDPFace;
import java2hu.conversation.IDialogueFaceEnum;
import java2hu.conversation.TouhouDP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ByakurenDP extends TouhouDP<ByakurenDPFace>
{
	public ByakurenDP()
	{
		super(getBaseSprite(), ByakurenDPFace.IDLE, Facing.LEFT, Facing.LEFT);
		
		setXOffset(+200);
	}
	
	private static Texture texture;
	private static Sprite getBaseSprite()
	{
		texture = Loader.texture(Gdx.files.internal("enemy/byakuren/faces/base.png"));
		
		return new Sprite(texture);
	}
	
	@Override
	public void positionFace(Sprite sprite)
	{
		super.positionFace(sprite);
	}
	
	public enum ByakurenDPFace implements IDialogueFaceEnum
	{
		IDLE, HAPPY, SURPRISED;
		
		private Texture texture;
		
		private ByakurenDPFace()
		{
			texture = Loader.texture(Gdx.files.internal("enemy/byakuren/faces/" + name() + ".png"));
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
