package java2hu.touhou.enemy;

import java2hu.HitboxSprite;
import java2hu.Position;
import java2hu.object.enemy.Enemy;
import java2hu.object.enemy.IEnemyType;
import java2hu.util.AnimationUtil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;

public class Sentinel extends Enemy
{
	public static enum SentinelColor
	{
		RED(256, 512, Color.RED), GREEN(320, 512, Color.GREEN), BLUE(384, 512, Color.BLUE), PURPLE(448, 512, Color.PURPLE);
		
		private Position pos;
		private Color color;
		
		private SentinelColor(float x, float y, Color color)
		{
			pos = new Position(x, y);
			this.color = color;
		}
		
		public Position getUV()
		{
			return pos;
		}
		
		public Color getColor()
		{
			return color;
		}
	}
	
	private final static int WIDTH = 64;
	private final static int HEIGHT = 64;
	
	public Sentinel(SentinelColor color, float maxHealth, float x, float y)
	{
		super(new IEnemyType()
		{
			private Texture t = TouhouEnemyType.getEnemyTexture();
			
			private Animation ani;
			
			{
				int x = (int) color.getUV().getX();
				int y = (int) color.getUV().getY();
				
				HitboxSprite s = new HitboxSprite(new TextureRegion(t, x, y, WIDTH, HEIGHT));
				s.setScale(1.2f);
				
				ani = new Animation(1, s);
			}
			
			@Override
			public Animation getSpecialAnimation()
			{
				return ani;
			}
			
			@Override
			public Animation getRightAnimation()
			{
				return ani;
			}
			
			@Override
			public Animation getLeftAnimation()
			{
				return ani;
			}
			
			@Override
			public Animation getIdleAnimation()
			{
				return ani;
			}
		}, maxHealth, x, y);
		
		int xPos = (int) color.getUV().getX();
		int yPos = (int) color.getUV().getY() + HEIGHT;
		
		Sprite round = new Sprite(TouhouEnemyType.getEnemyTexture(), xPos, yPos, WIDTH, HEIGHT);
		round.setOriginCenter();
		
		outer = new Sprite(round);
		inner = round;
		
		aura = TouhouEnemyType.getAuraAnimation();
		auraColor = color.getColor();
		
		playerHitBox = playerHitHitbox;
		playerHitHitbox = null;
	}
	
	private Color auraColor;
	
	/**
	 * Gets the color of the aura behind this sentinel.
	 */
	public Color getAuraColor()
	{
		return auraColor;
	}
	
	/**
	 * Sets the color of the aura behind this sentinel.
	 */
	public void setAuraColor(Color auraColor)
	{
		this.auraColor = auraColor;
	}
	
	private Animation aura;
	
	private Sprite inner;
	private Sprite outer;
	
	private Polygon playerHitBox;
	
	@Override
	public void onDraw()
	{
		HitboxSprite s = AnimationUtil.getCurrentSprite(aura, true);
		
		final float grayMul = ((1 - grey) * 0.75f) + 0.25f;
		
		s.setPosition(getX() + 5 - (s.getWidth() / 2f), getY() + 20 - (s.getHeight() / 2f));

		s.setColor(new Color(auraColor.r * Math.max(0.2f, grayMul), auraColor.g * Math.max(0.2f, grayMul), auraColor.b * Math.max(0.2f, grayMul), 1f));
		s.setAlpha(0.75f);
		s.setScale(2.5f * scale);
		
		outer.setColor(new Color(1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1f));
		outer.setScale(1.75f * scale);
		
		inner.setColor(new Color(1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1f));
		inner.setScale(1.2f * scale);
		
		s.draw(game.batch);
		
		inner.setPosition(getX() - (inner.getWidth() / 2f), getY() - (inner.getHeight() / 2f));
		outer.setPosition(getX() - (outer.getWidth() / 2f), getY() - (outer.getHeight() / 2f));
		
		inner.draw(game.batch);
		
		game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		outer.draw(game.batch);
		
		game.batch.setBlendFunction(getBlendFuncSrc(), getBlendFuncDst());
		
		HitboxSprite cur = getCurrentSprite();
		
		cur.setColor(new Color(1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1 * Math.max(0.2f, grayMul), 1f));
		cur.setScale(1.2f * scale);
		
		super.onDraw();
	}
	
	private float scale = 0.4f;
	private float grey = 1f;
	
	@Override
	public void onUpdateDelta(float delta)
	{
		super.onUpdateDelta(delta);
		
		inner.rotate(140f * delta);
		outer.rotate(-280f * delta);
		
		if (grey > 0)
		{
			grey = Math.max(0, grey - (1f * delta));
		}
		
		if (scale < 1)
		{
			scale = Math.min(1, scale + (0.5f * delta));
		}
		
		if (playerHitHitbox == null && scale > 0.8f)
		{
			playerHitHitbox = playerHitBox;
		}
	}
}
