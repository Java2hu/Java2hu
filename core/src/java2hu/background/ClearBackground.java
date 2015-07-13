package java2hu.background;

import java2hu.util.ImageUtil;

import com.badlogic.gdx.graphics.Color;

/**
 * A background which is completely black, to block out any backgrounds under it.
 */
public class ClearBackground extends Background
{
	public ClearBackground(int zIndex)
	{
		super(ImageUtil.makeDummyTexture(Color.BLACK, 1, 1));
		
		getSprite().setScale(game.getWidth(), game.getHeight());
		
		setZIndex(zIndex);
	}
}
