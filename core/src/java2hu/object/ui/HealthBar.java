package java2hu.object.ui;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.DrawObject;
import java2hu.object.LivingObject;
import java2hu.util.ImageUtil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class HealthBar extends DrawObject
{
	private Texture texture;
	private LivingObject source;
	private float x;
	private float y;
	private float width;
	private float height;
	private Color activeColor = Color.WHITE;
	private Color backColor = Color.DARK_GRAY;
	private Color shadowColor = Color.DARK_GRAY;
	
	public HealthBar(LivingObject source, float x, float y, float width, float height)
	{
		this.source = source;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		setZIndex(J2hGame.GUI_Z_ORDER);
		
		texture = ImageUtil.makeDummyTexture(Color.WHITE, 1, 1);
	}

	@Override
	public void onDraw()
	{
		float fullHealth = source.getMaxHealth();
		float partHealth = source.getHealth();
		float percentage = partHealth / fullHealth;
		
		J2hGame g = Game.getGame();
		
		g.batch.setColor(activeColor);
		g.batch.draw(texture, x, y, width * percentage, height - 1);
		g.batch.setColor(backColor);
		g.batch.draw(texture, x + (width * percentage), y, width - (width * percentage), height - 1);
		g.batch.setColor(shadowColor);
		g.batch.draw(texture, x + 1, y - 1, width, 1); // 1 line below the health bar shifted 1 to the right for a shadow effect
		g.batch.setColor(Color.WHITE);
	}
	
	public void setActiveColor(Color activeColor)
	{
		this.activeColor = activeColor;
	}
	
	public Color getActiveColor()
	{
		return activeColor;
	}
	
	public void setBackColor(Color backColor)
	{
		this.backColor = backColor;
	}
	
	public Color getBackColor()
	{
		return backColor;
	}
	
	public void setShadowColor(Color shadowColor)
	{
		this.shadowColor = shadowColor;
	}
	
	public Color getShadowColor()
	{
		return shadowColor;
	}
	
	@Override
	public boolean isPersistant()
	{
		return source.isOnStage();
	}
}
