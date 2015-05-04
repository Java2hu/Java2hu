package java2hu.menu;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.menu.ButtonManager.TextButton;
import java2hu.object.DrawObject;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.MathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class PauseMenu extends Menu
{
	public PauseMenu(Menu parent)
	{
		super(parent);
	}

	Color textureColor = null;
	
	{
		textureColor = Color.BLACK.cpy();
	}
	
	private Sprite flower;
	private boolean forward = true;
	private float rot = 0f;
	
	private DrawObject fakeBg;
	
	private ButtonManager manager = new ButtonManager();
	
	{
		final Rectangle b = Game.getGame().getBoundary();//new Rectangle(0, 0, 300, 300); //Game.getGame().getBoundary();
		
		final float xOffset = Game.getGame().camera.viewport.x;
		final float yOffset = Game.getGame().camera.viewport.y;

		float xMul = Game.getGame().camera.viewport.width / Game.getGame().camera.lastWidth;
		float yMul = Game.getGame().camera.viewport.height / Game.getGame().camera.lastHeight;
		
		final Sprite bgText = new Sprite(ScreenUtils.getFrameBufferTexture((int)(xOffset + b.x), (int)(yOffset + b.y), (int)(b.width * xMul), (int)(b.height * yMul)));
		
		final Texture water = Loader.texture(Gdx.files.internal("sprites/water.jpg"));
		
		fakeBg = new DrawObject()
		{
			@Override
			public void onDraw()
			{
				if(getShader() != null)
				{
					water.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				}
				
				bgText.setBounds(b.x, b.y, b.width, b.height);
				bgText.draw(Game.getGame().batch);
			}
		};
		
		fakeBg.setZIndex(getZIndex() - 1);
//		fakeBg.setShader(ShaderLibrary.RELIEF.getProgram());
		
//		Game.getGame().spawn(fakeBg);
		
		Texture texture = Loader.texture(Gdx.files.internal("pause_1280.png"));

		flower = new Sprite(texture, 96, 420);
		flower.setColor(Game.getGame().batch.getColor());
		
		manager.addButton(new TextButton(450, 600, "Return to Game", new Runnable()
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
						Game.getGame().onDePause();
					}
				}, 1);
			}
		}));
		
		manager.addButton(new TextButton(450, 550, "Return to Title", new Runnable()
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
						Game.getGame().onToTitle();
					}
				}, 1);
			}
		}));
		
		manager.addButton(new TextButton(450, 500, "Give up and Retry", new Runnable()
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
						Game.getGame().onDePause();
						Game.getGame().onRetry();
					}
				}, 1);
			}
		}));
		
		manager.addButton(new TextButton(450, 350, "(S) Kill Screen / Skip", new Runnable()
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
		
		manager.addButton(new TextButton(450, 300, "(D) Toggle Debug Mode (LAGGY!)", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().setDebugMode(!Game.getGame().isDebugMode());
			}
		}));
		
		manager.addButton(new TextButton(450, 250, "(F) Toggle Profiling Mode (Needs console)", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				Game.getGame().setProfiling(!Game.getGame().isProfiling());
			}
		}));
	}
	
	@Override
	public void onDraw()
	{
		final J2hGame game = Game.getGame();
		
		game.batch.setProjectionMatrix(game.standardProjectionMatrix);
		
		flower.setRotation(rot);
		flower.setPosition(330, 670 - flower.getHeight());
		flower.draw(game.batch);
		
		game.font.setColor(new Color(0.4f, 1f, 0.4f, 1f));
		
		game.font.draw(game.batch, "Pause Menu:", 450, 650);
		
		game.font.draw(game.batch, "Debug Menu:", 450, 400);
		
		game.font.setColor(Color.WHITE);
		
		manager.draw();
		
		game.batch.setProjectionMatrix(game.camera.camera.combined); // This takes a lot of time, 10ms according to the profiler. (Might be vsync though)
	}

	@Override
	public void onUpdate(long tick)
	{
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
		
		fakeBg.setZIndex(getZIndex() - 1);
		
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
