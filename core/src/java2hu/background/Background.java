package java2hu.background;

import java2hu.Game;
import java2hu.ZIndex;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;

// Standard implementation of a background/
public class Background extends ABackground
{
	private Texture text;
	private Sprite sprite;
	
	public Sprite getSprite()
	{
		return sprite;
	}
	
	private static final float RENDERED_TILES = 3;
	
	public Background(Texture backgroundTexture)
	{
		setZIndex(ZIndex.BACKGROUND_LAYER_2);
		
		text = backgroundTexture;
		setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		sprite = new Sprite(text);
		
		float width = game.getBoundary().getWidth() * RENDERED_TILES;
		float height = game.getBoundary().getHeight() * RENDERED_TILES;
		sprite.setSize(width, height);
		
		sprite.setOriginCenter();

		sprite.setPosition(game.getCenterX() - (width / 2f), game.getCenterY() - (height / 2f));
		
		addDisposable(text);
	}
	
	public void setWrap(TextureWrap u, TextureWrap v)
	{
		text.setWrap(u, v);
	}
	
	private double startU = 0d;
	private double startV = 0d;

	public double getStartU()
	{
		return startU;
	}
	
	public double getStartV()
	{
		return startV;
	}
	
	public void setStartU(double startU)
	{
		this.startU = startU;
	}
	
	public void setStartV(double startV)
	{
		this.startV = startV;
	}
	
	private double endU = 1d;
	private double endV = 1d;

	public double getEndU()
	{
		return endU;
	}
	
	public double getEndV()
	{
		return endV;
	}
	
	public void setEndU(double endU)
	{
		this.endU = endU;
	}
	
	public void setEndV(double endV)
	{
		this.endV = endV;
	}
	
	private double velU;
	private double velV;
	
	public double getVelX()
	{
		return velU;
	}
	
	public double getVelY()
	{
		return velV;
	}
	
	public void setVelU(double velU)
	{
		this.velU = velU;
	}
	
	public void setVelV(double velV)
	{
		this.velV = velV;
	}
	
	private double degreesPerSecond = 0d;
	
	/**
	 * Returns the rotation in angles per second.
	 * @return
	 */
	public double getRotationDegs()
	{
		return degreesPerSecond;
	}
	
	/**
	 * Returns the rotation in rads per second.
	 * @return
	 */
	public double getRotationRads()
	{
		return Math.toRadians(getRotationDegs());
	}
	
	public void setRotationDegs(double degreesPerSecond)
	{
		this.degreesPerSecond = degreesPerSecond;
	}
	
	public void setRotationRads(double radsPerSecond)
	{
		setRotationDegs(Math.toDegrees(radsPerSecond));
	}
	
	private double uOffset = 0d;
	private double vOffset = 0d;
	private double rotation = 0d;
	
	@Override
	public void onDraw()
	{
		sprite.setRegion((float)((startU + uOffset) * RENDERED_TILES), (float)((startV + vOffset) * RENDERED_TILES), (float)((endU + uOffset) * RENDERED_TILES), (float)((endV + vOffset) * RENDERED_TILES));
		
		sprite.setRotation((float) rotation);
		
		sprite.draw(Game.getGame().batch);
	}
	
	@Override
	public void onUpdateDelta(float delta)
	{
		super.onUpdateDelta(delta);
		
		uOffset += velU * delta;
		vOffset += velV * delta;
		
		rotation += degreesPerSecond * delta;
		rotation = rotation % 360;
	}
}
