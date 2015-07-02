package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;
import java2hu.object.UpdateObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Template background
 * 
 * set userData of Renderables to z-index.
 */
public class TemplateBG extends Background3D
{
	public ModelBatch batch = new ModelBatch(new RenderableSorter()
	{
		@Override
		public void sort(Camera camera, Array<Renderable> renderables)
		{
			renderables.sort(new Comparator<Renderable>()
			{
				@Override
				public int compare(Renderable o1, Renderable o2)
				{
					if(!(o1.userData instanceof Number) || !(o2.userData instanceof Number))
						return 0;
					
					Number n1 = (Number) o1.userData;
					Number n2 = (Number) o2.userData;
					
					double d1 = n1.doubleValue();
					double d2 = n2.doubleValue();
					
					if(d1 < d2)
						return -1;
					else if(d1 > d2)
						return 1;
					else
						return 0;
				}
			});
		}
	});
	
	public ArrayList<ModelInstance> instances = new ArrayList<ModelInstance>();
	
	public ModelInstance plane1;
	public ModelInstance plane2;
	
	public TemplateBG()
	{
		setModelBatch(batch);

		RotationPerspectiveCamera camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0,5,0);
		camera.lookAt(0,0,0);
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.update();
		
		setCamera(camera);
		
		createStandardEnvironment();
        
		J2hGame game = Game.getGame();
		
		FileHandle dir = Gdx.files.internal("scenes/");
		
		Texture plane1Text = Loader.texture(dir.child("grid1.png"));
		plane1Text.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture plane2Text = Loader.texture(dir.child("grid2.png"));
		plane2Text.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		// Sample Plane (plane1)
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(plane1Text));

			Node node = b.node();

			b.part(makePlateMesh(b, mat, name, 15), mat);
			
			Model model = b.end();
			
			plane1 = new ModelInstance(model);
			plane1.userData = 0d; // Lowest z-index.

			instances.add(plane1);
		}
		
		// Sample Plane (plane2) above plane1
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(plane2Text));

			Node node = b.node();

			b.part(makePlateMesh(b, mat, name, 15), mat);
			
			Model model = b.end();
			
			plane2 = new ModelInstance(model);
			plane2.userData = 1d; // Higher z-index than plane1.

			instances.add(plane2);
		}
		
		setCameraPather(new UpdateObject()
		{
			float yaw = 0f;
			float pitch = 0f;
			
			@Override
			public void onUpdate(long tick)
			{
				Vector3 pos = new Vector3(0, 3f, 0f);
				Vector3 look = new Vector3();
				
				yaw = 270f;
				pitch = 190f;
				
				look.add((float) Math.cos(Math.toRadians(yaw)), (float) Math.cos(Math.toRadians(pitch)), (float) (Math.sin(Math.toRadians(yaw))));

				{
					getCamera().direction.set(look);
					getCamera().position.set(pos);
					getCamera().update();
				}
			}
		});
	}
	
	@Override
	public void onUpdateDelta(float delta)
	{
		translateLooped(plane1, -0.0080f * delta, -0.0080f * delta, 10f, 15f);
		translateLooped(plane2, -0.0040f * delta, -0.0080f * delta, 10f, 15f);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		DefaultShader.defaultCullFace = 1;
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
	}
}
