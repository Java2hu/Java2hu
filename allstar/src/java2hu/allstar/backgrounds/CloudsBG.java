package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;
import java2hu.object.UpdateObject;
import java2hu.util.Getter;
import java2hu.util.MathUtil;

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
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class CloudsBG extends Background3D
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
	private ArrayList<MovingCloud> clouds = new ArrayList<CloudsBG.MovingCloud>();
	
	private class MovingCloud
	{
		public float velX;
		public float velY;
		public ModelInstance instance;
	}
	
	public ModelInstance cloud1;
	public ModelInstance cloud2;
	public ModelInstance cloud3;
	public ModelInstance cloud4;
	
	public ModelInstance forest;
	
	public CloudsBG()
	{
		setModelBatch(batch);

		RotationPerspectiveCamera camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0,5,0);
		camera.lookAt(0,0,0);
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.update();
		
		setCamera(camera);
		
//		Environment environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
//        environment.add(new DirectionalLight().set(1f, 1f, 1f, 0f, -1f, 0f));
//        
//        setEnvironment(environment);
		
		createStandardEnvironment();
        
		J2hGame game = Game.getGame();
		
		FileHandle dir = Gdx.files.internal("scenes/clouds/");
		
		Texture cloud = Loader.texture(dir.child("cloud.png"));
		cloud.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture forest = Loader.texture(dir.child("forest.png"));
		forest.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		// Forest
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(forest));

			String name = "leaf1";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 16), mat);
			
			Model model = b.end();
			
			this.forest = new ModelInstance(model);
			this.forest.userData = 0d;

			instances.add(this.forest);
		}
		
		// Cloud
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.5f));
			mat.set(TextureAttribute.createDiffuse(cloud));

			String name = "cloud1";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			
			final MeshPart plateMesh = makePlateMesh(b, mat, name, 0.5f, 1);
			
			b.part(plateMesh, mat);
			
			final Model model = b.end();
			
			createCloud = new Getter<ModelInstance>()
			{
				@Override
				public ModelInstance get()
				{
					return new ModelInstance(model);
				}
			};
			
			setCameraPather(new UpdateObject()
			{
				float height = 0f;
				float yaw = 0f;
				float pitch = 0f;
				
				@Override
				public void onUpdate(long tick)
				{
					int i = (int) tick;
					
					float offsetAngle = i / 500f * 360f;
					offsetAngle = MathUtil.normalizeDegree(offsetAngle);

					float xOffset = (float) (Math.cos(Math.toRadians(offsetAngle)) * 0.5f);
					
					Vector3 pos = new Vector3(xOffset, 7f, -2f);
					Vector3 look = new Vector3();
					
					yaw += 2f;
					pitch += 0.1f;

					look.add((float) Math.cos(Math.toRadians(yaw)), -100f, (float) (-10f + Math.sin(Math.toRadians(pitch)) * -20f));

					{
						float angle = 0;

						if(height <= 0)
						{
							float max = 40f;

							angle = i % max;

							if(angle > max / 2f)
								angle = max - angle;
						}

						getCamera().direction.set(look);
						getCamera().position.set(pos);
						getCamera().update();
					}
				}
			});
		}
		
		for(int i = 0; i < 1000; i++)
			onUpdate(i);
	}
	
	private Getter<ModelInstance> createCloud = null;
	
	public void createCloud(double startX, double startY, double startZ, double velX, double velY)
	{
		startZ -= 2f;
		
		MovingCloud cloud = new MovingCloud();
		cloud.instance = createCloud.get();
		cloud.instance.transform.setTranslation((float)startX, (float)startY, (float)startZ);
		cloud.instance.userData = startY;
		cloud.velX = (float)velX;
		cloud.velY = (float)velY;
		
		clouds.add(cloud);
		instances.add(cloud.instance);
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		if(tick % 4 == 0)
		{
			createCloud((Math.random() * 5f) - 2.5f, 5f * Math.random(), -4f, 0.001f * (2*(Math.random()-0.5f)), 0.015f * ((Math.random())));
		}
		
		float UVratio = 20f / 8; // Size is 20, 4x UV over the entire area
		
		float dividant = 100;
		
		Iterator<MovingCloud> it = clouds.iterator();
			
		while(it.hasNext())
		{
			MovingCloud cloud = it.next();
			
			cloud.instance.transform.translate(cloud.velX, 0, cloud.velY);
			
			final float z = cloud.instance.transform.getTranslation(new Vector3()).z;
			
			final boolean below = z > 2;
			
			if(below)
			{
				it.remove();
				instances.remove(cloud.instance);
			}
		}

		forest.transform.setToTranslation(0f, 0f, tick / dividant % UVratio - UVratio);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		DefaultShader.defaultCullFace = 1;
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
	}
}
