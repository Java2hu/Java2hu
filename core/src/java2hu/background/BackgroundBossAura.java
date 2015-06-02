package java2hu.background;

import java2hu.Game;
import java2hu.IPosition;
import java2hu.ZIndex;
import java2hu.object.DrawObject;
import java2hu.util.Getter;

import shaders.ShaderLibrary;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.sun.istack.internal.Nullable;

/**
 * Boss aura that is applied to the background of the game.
 * The underlying shader supports up to 3 aura's visible at the same time, to add more you'd need to update the shader to work with more.
 */
public class BackgroundBossAura extends DrawObject
{
	public BackgroundBossAura()
	{
		setZIndex(ZIndex.BACKGROUND_AURA);
	}
	
	private final int MAX_AURAS = 3; // The shader only supports up to 3.
	
	private final FrameBuffer bgBuffer = new FrameBuffer(Format.RGBA8888, Game.getGame().getWidth(), Game.getGame().getHeight(), true);
	
	/**
	 * Returns the framebuffer that backgrounds need to be bound to, to work.
	 * @return
	 */
	public FrameBuffer getBackgroundBuffer()
	{
		return bgBuffer;
	}

	private BubbleData[] auras = new BubbleData[MAX_AURAS]; 
	
	public static class BubbleData
	{
		public Getter<IPosition> getter;
		public float scale = 0;
	}
	
	/**
	 * The getter should return the coordinates to display this bubble.
	 * @param id - Which aura you're setting, this shader supports up to 3.
	 * @param getter - Null will disable this aura.
	 */
	public void setAura(int id, @Nullable Getter<IPosition> getter)
	{
		if(id >= MAX_AURAS)
			return;
		
		BubbleData data = new BubbleData();
		data.getter = getter;
		
		auras[id] = data;
	}
	
	public void clearAuras()
	{
		for(int i = 0; i < MAX_AURAS; i++)
		{
			auras[i] = null;
		}
	}
	
	/**
	 * Returns the vector that is considered as "not existing" by the shader.
	 * @return
	 */
	private Vector2 getNull()
	{
		return new Vector2(-99, -99);
	}
	
	{
		setShader(ShaderLibrary.BOSS_BACKGROUND.getProgram());
	}
	
	@Override
	public boolean isPersistant()
	{
		return true;
	}

	public Sprite sprite = null;

	@Override
	public void onDraw()
	{
		Texture tex = bgBuffer.getColorBufferTexture();

		if(sprite == null)
		{
			tex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			sprite = new Sprite(tex);
		}

		if(getShader() != null)
		{
			getShader().setUniformi("water", 1);

			for(int i = 0; i < MAX_AURAS; i++)
			{
				BubbleData data = auras[i];
				
				float scale = data != null ? data.scale : 1.0f;
				
				float scaleX = 1.8f / scale;
				float scaleY = 1.8f / scale;

				float ratio = (float)game.getWidth() / (float)game.getHeight();

				Vector2 size = new Vector2(ratio * scaleX, 1.0f * scaleY);
				Vector2 actualSize = new Vector2(1f/size.x * Game.getGame().getWidth(), 1f/size.y * Game.getGame().getHeight());
				
				Vector2 set = null;
				
				if(data == null || data.getter == null)
				{
					set = getNull();
				}
				else
				{
					IPosition pos = data.getter.get();
					
					// Convert to UV coordinates.
					set = new Vector2((pos.getX() - actualSize.x / 2f) / Game.getGame().getWidth() * size.x, (pos.getY() - actualSize.y / 2f) / Game.getGame().getHeight() * size.y);
				}
				
				getShader().setUniformf("size" + (i + 1), size);
				getShader().setUniformf("pos" + (i + 1), set);
			}
			
			getShader().setUniformf("time", game.getElapsedTime() * 0.3f);
		}
		
		game.batch.flush();
		
		game.batch.disableBlending();
		
		game.camera.applyAspectRatio(true);
		
		sprite.setFlip(false, true);
		sprite.draw(game.batch);
		
		game.batch.enableBlending();
		
		game.batch.flush();
		
		getBackgroundBuffer().begin();
		
		game.batch.flush();
		
		game.camera.applyAspectRatio(true);
		
		game.batch.flush();
		
		getBackgroundBuffer().end();
		
		game.camera.applyAspectRatio(false);
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		for(int i = 0; i < MAX_AURAS; i++)
		{
			BubbleData data = auras[i];
			
			if(data == null)
				continue;
		
			if(data.scale < 1)
				data.scale = Math.min(1, data.scale += 0.01f);
		}
	}
}
