package java2hu.background.bg3d;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;
import java2hu.object.DrawObject;
import java2hu.object.UpdateObject;
import java2hu.util.ImageUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

public abstract class Background3D extends DrawObject
{
	private RotationPerspectiveCamera camera;
	
	private Environment environment;
//	private CameraController controller;
	
	private UpdateObject path;
	private ModelObject fog;
	
	private ModelBatch batch = Game.getGame().modelBatch;
	
	private boolean drawFog = true;
	
	public Background3D()
	{
		setZIndex(-10);
	}
	
	@Override
	public boolean isPersistant()
	{
		return true;
	}
	
	public void createStandardCamera()
	{
		camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(10,0,10);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 100f;
		camera.update();
	}
	
	public void setCamera(RotationPerspectiveCamera camera)
	{
		this.camera = camera;
	}
	
	public void createStandardEnvironment()
	{
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));
	}
	
	public Environment getEnvironment()
	{
		return environment;
	}
	
	public void setEnvironment(Environment environment)
	{
		this.environment = environment;
	}
	
	public UpdateObject getCameraPather()
	{
		return path;
	}
	
	public void setCameraPather(UpdateObject path)
	{
		this.path = path;
	}
	
//	public void setController(CameraController controller)
//	{
//		this.controller = controller;
//	}
	
	public void setModelBatch(ModelBatch batch)
	{
		this.batch = batch;
	}
	
	public ModelBatch getModelBatch()
	{
		return batch;
	}
	
//	public CameraController getController()
//	{
//		return controller;
//	}
	
	public RotationPerspectiveCamera getCamera()
	{
		return camera;
	}
	
	public ModelObject getFog()
	{
		return fog;
	}
	
	public void setFog(ModelObject fog)
	{
		this.fog = fog;
	}
	
	public boolean doDrawFog()
	{
		return drawFog;
	}
	
	public void doDrawFog(boolean drawFog)
	{
		this.drawFog = drawFog;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
	}
	
	Sprite screen;
	float alpha = 0;

	protected boolean useStandardFadeOut()
	{
		return true;
	}
	
	@Override
	public void onDraw()
	{
		J2hGame game = Game.getGame();
		
		if(!isOnStage())
			return;
		
		if(screen == null || !useStandardFadeOut())
		{
			game.batch.end();

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			if(getFog() != null)
				getFog().draw(camera, game.modelBatch, environment);

			batch.begin(camera);		

			drawBackground(batch, environment, drawFog);

			batch.end();

			game.batch.begin();
			
			if(useStandardFadeOut())
			{
				if(fadeOut)
				{
					screen = ImageUtil.captureScreen();
				}

				if(fadeOut)
				{
					alpha = 1;
				}
			}
		}
		else
		{
			screen.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
			
			float speed = 0.001f;
			
			if(fadeOut)
			{
				alpha -= speed;
				
				if(alpha < 0)
				{
					if(fadeOutRemove)
					{
						game.delete(this);
						return;
					}
					else
					{
						fadeOut = false;
						screen = null;
						return;
					}
				}
			}
			
			System.out.println(alpha);
			
			alpha = Math.min(1, Math.max(0, alpha));
			
			screen.setAlpha(alpha);
			screen.draw(game.batch);
			return;
		}
	}
	
	@Override
	public void onUpdate(long tick)
	{
		if(screen != null)
			return;
		
		if(getCameraPather() != null)
			getCameraPather().update(tick);
		
//		if(getController() != null)
//			getController().update();
		
		if(getFog() != null)
			getFog().update(camera);
	}
	
	public abstract void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog);

	boolean fadeOut = false;
	boolean fadeOutRemove = false;
	
	public void fadeOut(boolean remove)
	{
		fadeOut = true;
		this.fadeOutRemove = remove;
	}
}
