package java2hu.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.object.DrawObject;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.MathUtil;

import shaders.ShaderLibrary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class PauseMenu extends Menu
{
	private boolean canGoBackToGame = false;
	
	public boolean canGoBackToGame()
	{
		return canGoBackToGame;
	}
	
	public PauseMenu(Menu parent, boolean canGoBackToGame)
	{
		super(parent);
		
		this.canGoBackToGame = canGoBackToGame;
	}

	Color textureColor = Color.BLACK.cpy();
	
	private Sprite flower;
	private boolean forward = true;
	private float rot = 0f;
	
	private DrawObject fakeBg;
	
	private ButtonManager manager = new ButtonManager();
	
	private int pauseY = 0;
	private int debugY = 0;
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		init();
	}
	
	private void init()
	{
		final Rectangle b = Game.getGame().getBoundary();//new Rectangle(0, 0, 300, 300); //Game.getGame().getBoundary();
		
		final float xOffset = Game.getGame().camera.viewport.x;
		final float yOffset = Game.getGame().camera.viewport.y;

		float xMul = Game.getGame().camera.viewport.width / Game.getGame().getWidth();
		float yMul = Game.getGame().camera.viewport.height / Game.getGame().getHeight();
		
		final Sprite bgText = new Sprite(ScreenUtils.getFrameBufferTexture((int)(xOffset + b.x), (int)(yOffset + b.y), (int)(b.width * xMul), (int)(b.height * yMul)));
		
		bgText.setAlpha(0f);
		
		fakeBg = new DrawObject()
		{
			@Override
			public void onDraw()
			{
				bgText.setBounds(b.x, b.y, b.width, b.height);
				bgText.draw(Game.getGame().batch);
			}
			
			@Override
			public boolean isActiveDuringPause()
			{
				return true;
			}
			
			@Override
			public void onUpdateDelta(float delta)
			{
				if(bgText.getColor().a >= 1)
					return;
				
				final float newAlpha = Math.min(1, bgText.getColor().a + (1f * delta));

				bgText.setAlpha(newAlpha);
			}
		};
		
		fakeBg.setZIndex(getZIndex() - 1);
		fakeBg.setShader(ShaderLibrary.RELIEF.getProgram());
		
		Game.getGame().spawn(fakeBg);
		
		Texture texture = Loader.texture(Gdx.files.internal("pause_1280.png"));

		flower = new Sprite(texture, 96, 420);
		flower.setColor(Game.getGame().batch.getColor());
		
		int y = 650;
		
		if(!canGoBackToGame)
		{
			y -= 100;
		}
		
		pauseY = y;
		
		y -= 50;
		
		if(canGoBackToGame)
		{
			manager.addButton(new ShadowedTextButton(450, y, "Return to Game", new Runnable()
			{
				@Override
				public void run()
				{
					TouhouSounds.Hud.OK.play();

					Game.getGame().addTaskPause(new Runnable()
					{
						@Override
						public void run()
						{
							Game.getGame().setPaused(false);
						}
					}, 1);
				}
			}));
			
			y -= 50;
		}
		
		manager.addButton(new ShadowedTextButton(450, y, "Return to Title", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().addTaskPause(new Runnable()
				{
					@Override
					public void run()
					{
						game.setPaused(true);
						game.setOutOfGame(true);
						Game.getGame().onToTitle();
					}
				}, 1);
			}
		}));
		
		y -= 50;
		
		manager.addButton(new ShadowedTextButton(450, y, canGoBackToGame ? "Give up and Retry" : "Play again", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				
				Game.getGame().addTaskPause(new Runnable()
				{
					@Override
					public void run()
					{
						Game.getGame().setPaused(false);
						Game.getGame().onRetry();
					}
				}, 1);
			}
		}));
		
		y -= 50;
		
		if(!canGoBackToGame)
		{
			debugY = -10;
			return;
		}
		
		debugY = y;
		
		y -= 50;
		
		manager.addButton(new ShadowedTextButton(450, y, "(S) Kill Screen / Skip", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				for(StageObject obj : Game.getGame().getStageObjects())
				{
					if(obj instanceof LivingObject)
					{
						((LivingObject)obj).setHealth(0);
					}
				}
			}
		}));
		
		y -= 50;
		
		manager.addButton(new ShadowedTextButton(450, y, "(D) Toggle Debug Mode (LAGGY!)", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().setDebugMode(!Game.getGame().isDebugMode());
			}
		}));
		
		y -= 50;
		
		manager.addButton(new ShadowedTextButton(450, y, "(F) Toggle Profiling Mode (Needs console)", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().setProfiling(!Game.getGame().isProfiling());
			}
		}));
		
		y -= 50;
		
		manager.addButton(new ShadowedTextButton(450, y, "(G) Toggle Z-Indexing Mode (Shows draw order)", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().setZIndexing(!Game.getGame().isZIndexing());
			}
		}));
	}
	
	private int ticks = 0;
	
	@Override
	public void onDraw()
	{
		final J2hGame game = Game.getGame();
		
		game.batch.setProjectionMatrix(game.standardProjectionMatrix);
		
//		game.batch.end();
//		
//		game.shape.begin(ShapeType.Filled);
//		
//		game.shape.setColor(0.3f, 0.3f, 0.3f, 0.1f);
//		
//		game.shape.rect(450 - 30, 700, 500, -500);
//		
//		game.shape.end();
//		
//		game.batch.begin();
		
		flower.setRotation(rot);
		
		float mul = 0.5f + (Math.min(1, ticks / 30f) * 0.5f);
		Color batchColor = game.batch.getColor();
		
		flower.setPosition(330 - 5, 670 - flower.getHeight());
	
		flower.setColor(0f, 0f, 0f, (mul - 0.5f) * 2f);
		flower.draw(game.batch);
		
		flower.setPosition(330, 670 - flower.getHeight());
		
		flower.setColor(mul * batchColor.r, mul * batchColor.g, mul * batchColor.b, (mul - 0.5f) * 2f);
		flower.draw(game.batch);
		
		game.font.setColor(Color.BLACK);
		
		game.font.draw(game.batch, canGoBackToGame ? "Pause Menu:" : "End menu:", 450 + 2, pauseY - 2);
		
		game.font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
		
		game.font.draw(game.batch, canGoBackToGame ? "Pause Menu:" : "End menu:", 450, pauseY);
		
		
		game.font.setColor(Color.BLACK);
		
		game.font.draw(game.batch, "Debug Menu:", 450 + 2, debugY - 2);
	
		game.font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
		
		game.font.draw(game.batch, "Debug Menu:", 450, debugY);
		
		game.font.setColor(Color.WHITE);
		
		manager.draw();
		
		game.batch.setProjectionMatrix(game.camera.camera.combined); // This takes a lot of time, 10ms according to the profiler. (Might be vsync though)
	}

	@Override
	public void onUpdate(long tick)
	{
		ticks++;
		
		float rotSpeed = 1f;
		float maxRot = 10f;
		float minRot = 0f;
		float minSpeed = 0.1f;
		
		if(forward)
		{
			rot += rotSpeed * Math.max(MathUtil.getDifference(rot, maxRot) / 10, minSpeed) * Math.min(Math.max(MathUtil.getDifference(rot, minRot) / 10, minSpeed), 1f);
			
			if(rot > maxRot)
				forward = false;
		}
		else
		{
			rot -= rotSpeed * Math.max(MathUtil.getDifference(rot, minRot) / 10, minSpeed) *  Math.min(Math.max(MathUtil.getDifference(rot, maxRot) / 10, minSpeed), 1f);
			
			if(rot < minRot)
				forward = true;
		}
		
		manager.update();
	}

	@Override
	public void onDelete()
	{
		super.onDelete();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().delete(fakeBg);
			}
		}, 1);
	}

	@Override
	public void onHide()
	{
		Game.getGame().unregisterEvents(manager);
	}

	@Override
	public void onShow()
	{
		Game.getGame().registerEvents(manager);
	}
}
