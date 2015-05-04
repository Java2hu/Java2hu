package java2hu;

import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * OrthographicCamera wrapper that keeps the same aspect ratio (Keeps the game at 4:3)
 */
public class AspectRatioCamera extends J2hObject
{
	public OrthographicCamera camera;
	public final float ASPECT_RATIO;
	public final float VIRTUAL_HEIGHT;
	public final float VIRTUAL_WIDTH;
	public Rectangle viewport;
	public float lastWidth = 0;
	public float lastHeight = 0;
	
	public AspectRatioCamera(int width, int height)
	{
		camera = new OrthographicCamera(width, height);

		ASPECT_RATIO = (float)width/(float)height;
		this.VIRTUAL_WIDTH = width;
		this.VIRTUAL_HEIGHT = height;
		
		resizeScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public boolean resizeScreen(float width, float height)
	{
		if(lastWidth == width && lastHeight == height)
			return false;
		
		lastWidth = width;
		lastHeight = height;
		
		// calculate new viewport
        float aspectRatio = width/height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);

        if(aspectRatio > ASPECT_RATIO)
        {
            scale = height/VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = width/VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = width/VIRTUAL_WIDTH;
        }

        float w = VIRTUAL_WIDTH*scale;
        float h = VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
        
        return true;
	}
	
	public void applyAspectRatio()
	{
		applyAspectRatio(true);
	}
	
	public void applyAspectRatio(boolean clear)
	{
		resizeScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.gl.glViewport((int)viewport.x, (int)viewport.y, (int)viewport.width, (int)viewport.height);
		
		if(clear)
		{
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		
		camera.update();
	}
}
