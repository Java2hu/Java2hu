package java2hu.background.bg3d;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.ZIndex;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;
import java2hu.object.DrawObject;
import java2hu.object.UpdateObject;
import java2hu.util.ImageUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

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
		setZIndex(-ZIndex.BACKGROUND_LAYER_1);
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
		if(this.fog instanceof Disposable)
			removeDisposable((Disposable)fog);
		
		this.fog = fog;
		
		if(fog instanceof Disposable)
			addDisposable((Disposable)fog);
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
			
			float speed = 0.005f;
			
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
	
	@Override
	public void onUpdateDelta(float delta)
	{
		if(screen != null)
			return;
		
		if(getCameraPather() != null)
			getCameraPather().update(delta);
	}
	
	/**
	 * Makes a plate mesh with the specific material, name and an UV from 0 to UVMul.
	 * The size for this plate is 10x10 (default)
	 */
	public MeshPart makePlateMesh(ModelBuilder b, Material mat, String name, float UVMul)
	{
		return makePlateMesh(b, mat, name, 10, UVMul);
	}
	
	/**
	 * Makes a plate mesh with the specific material, name and an UV from 0 to UVMul
	 * The size for this plate is the one you specify in both length and width.
	 */
	public MeshPart makePlateMesh(ModelBuilder b, Material mat, String name, float size, float UVMul)
	{
		return makePlateMesh(b, mat, name, size, size, UVMul, UVMul);
	}
	
	/**
	 * Makes a plate mesh with the specific material, name and an UV from 0 to UVMul
	 * The size for this plate is the one you specify in both length and width.
	 */
	public MeshPart makePlateMesh(ModelBuilder b, Material mat, String name, float sizeX, float sizeZ, float UMul, float VMul)
	{
		MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);

		VertexInfo v1 = new VertexInfo().setPos(-sizeX, 0, -sizeZ).setNor(1, 0, 1).setUV(0, 0);
		VertexInfo v2 = new VertexInfo().setPos(-sizeX, 0, sizeZ).setNor(1, 0, 1).setUV(0, 1 * VMul);
		VertexInfo v3 = new VertexInfo().setPos(sizeX, 0, sizeZ).setNor(1, 0, 1).setUV(1 * UMul, 1 * VMul);
		VertexInfo v4 = new VertexInfo().setPos(sizeX, 0, -sizeZ).setNor(1, 0, 1).setUV(1 * UMul, 0);

		mpb.rect(v1, v4, v3, v2);

		return mpb.getMeshPart();
	}
	
	/**
	 * Translate a ModelInstance at a certain velocity, looping the object per UV
	 * The object gets moved back once it's looped over an entire UV, making it look like it's moving forward forever.
	 */
	public void translateLooped(ModelInstance instance, float x, float z, float size, float uv)
	{
		translateLooped(instance, x, z, size, size, uv, uv);
	}
	
	/**
	 * Translate a ModelInstance at a certain velocity, looping the object per UV
	 * The object gets moved back once it's looped over an entire UV, making it look like it's moving forward forever.
	 * @param offset offset from 0, 0, 0 
	 */
	public void translateLooped(ModelInstance instance, Vector3 offset, float x, float z, float size, float uv)
	{
		translateLooped(instance, offset, x, z, size, size, uv, uv);
	}
	
	/**
	 * Translate a ModelInstance at a certain velocity, looping the object per UV
	 * The object gets moved back once it's looped over an entire UV, making it look like it's moving forward forever.
	 */
	public void translateLooped(ModelInstance instance, float x, float z, float sizeX, float sizeZ, float u, float v)
	{
		translateLooped(instance, null, x, z, sizeX, sizeZ, u, v);
	}
	
	/**
	 * Translate a ModelInstance at a certain velocity, looping the object per UV
	 * The object gets moved back once it's looped over an entire UV, making it look like it's moving forward forever.
	 * @param offset offset from 0, 0, 0 
	 */
	public void translateLooped(ModelInstance instance, Vector3 offset, float x, float z, float sizeX, float sizeZ, float u, float v)
	{
		if(offset == null)
			offset = new Vector3();
		
		instance.transform.translate(x, 0, z);
		
		Vector3 trans = instance.transform.getTranslation(new Vector3());
		
		u /= 2f;
		v /= 2f;
		
		float uSize = sizeX / u;
		
		if((trans.x - offset.x) < -uSize || (trans.x - offset.x) > uSize)
			instance.transform.translate((((trans.x - offset.x) < -uSize ? 1 : -1) * uSize), 0, 0);
		
		float vSize = sizeX / u;
		trans = instance.transform.getTranslation(new Vector3());
		
		if((trans.z - offset.z) < -vSize || (trans.z - offset.z) > vSize)
			instance.transform.translate(0, 0, ((trans.z - offset.z) < -vSize ? 1 : -1) * vSize);
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
