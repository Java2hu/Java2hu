package java2hu.util;

import java2hu.Game;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Simple util to deal with images.
 */
public class ImageUtil extends J2hObject
{
	public static Texture makeDummyTexture(Color color, int width, int height)
	{
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA4444);
		pixmap.setColor(color);
		pixmap.fill();
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		
		return texture;
	}
	
	public static Pixmap captureScreenPixmap()
	{
		return captureScreenPixmap(Game.getGame().getBoundary());
	}
	
	public static Pixmap captureScreenPixmap(Rectangle b)
	{
		final float xOffset = Game.getGame().camera.viewport.x;
		final float yOffset = Game.getGame().camera.viewport.y;

		float xMul = Game.getGame().camera.viewport.width / Game.getGame().camera.lastWidth;
		float yMul = Game.getGame().camera.viewport.height / Game.getGame().camera.lastHeight;
		
		return ScreenUtils.getFrameBufferPixmap((int)(xOffset + b.x), (int)(yOffset + b.y), (int)(b.width * xMul), (int)(b.height * yMul));
	}
	
	public static Sprite captureScreen()
	{
		return captureScreen(Game.getGame().getBoundary());
	}

	public static Sprite captureScreen(Rectangle b)
	{
		final Sprite screen = new Sprite(new Texture(captureScreenPixmap(b)));

		return screen;
	}
	
	/**
	 * Adds an image to the specified TextureAtlas, while reserving flip.
	 */
	public static void addToTextureAtlas(TextureAtlas atlas, String name, TextureRegion r)
	{
		AtlasRegion reg = atlas.addRegion(name, r);
		
		if(r.isFlipX())
		{
			reg.offsetX += reg.originalWidth;
			reg.originalWidth = -reg.originalWidth;
			reg.packedWidth = reg.originalWidth;
		}
		
		if(r.isFlipY())
		{
			reg.offsetY += reg.originalHeight;
			reg.originalHeight = -reg.originalHeight;
			reg.packedHeight = reg.originalHeight;
		}
	}
}
