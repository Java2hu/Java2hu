package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;

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
import com.badlogic.gdx.utils.Array;

public class MistLakeBG extends Background3D
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
					
					return (int) Math.round(d1 - d2);
				}
			});
		}
	});
	
	public ArrayList<ModelInstance> instances = new ArrayList<ModelInstance>();
	
	public ModelInstance ground;
	public ModelInstance water1;
	public ModelInstance water2;
	
	public ModelInstance leaf1;
	public ModelInstance leaf2;
	
	public MistLakeBG()
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
		
		FileHandle dir = Gdx.files.internal("scenes/mist lake/");
		
		Texture cloud = Loader.texture(dir.child("cloud.png"));
		cloud.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture leaf1 = Loader.texture(dir.child("leaf1.png"));
		leaf1.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture leaf2 = Loader.texture(dir.child("leaf2.png"));
		leaf2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture ground = Loader.texture(dir.child("water2.png"));
		ground.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture water1 = Loader.texture(dir.child("water1.png"));
		water1.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		// Ground
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 0.9f, 0.9f, 0.9f));
			mat.set(new BlendingAttribute(true, 1f));
			mat.set(TextureAttribute.createDiffuse(ground));

			String name = "ground";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 10), mat);
			
			ModelInstance m = new ModelInstance(b.end());
			m.transform.setToTranslation(0, 0, 0);
			m.userData = 0d;
			
			this.ground = m;

			instances.add(m);
		}
		
		// Water
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(water1));

			String name = "water2";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 8), mat);
			
			Model model = b.end();
			
			this.water1 = new ModelInstance(model);
			this.water1.transform.setToTranslation(0, 0.5f, 0);
			this.water1.userData = 1d;
			
			this.water2 = new ModelInstance(model);
			this.water2.transform.setToTranslation(0, 1f, 0);
			this.water1.userData = 2d;

			instances.add(this.water1);
			instances.add(this.water2);
		}
		
		// Leaves
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(leaf1));

			String name = "leaf1";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 16), mat);
			
			Model model = b.end();
			
			this.leaf1 = new ModelInstance(model);
			this.leaf1.userData = 3d;

			instances.add(this.leaf1);
		}
		
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(leaf2));

			String name = "leaf2";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 16), mat);
			
			Model model = b.end();
			
			this.leaf2 = new ModelInstance(model);
			this.leaf2.userData = 4d;

			instances.add(this.leaf2);
		}
		
//		setController(new CameraController(getCamera()));
//		game.addAllListeners(getController());
		
		onUpdate(0);
	}

	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		float UVratio = 20f / 8; // Size is 20, 4x UV over the entire area
		
		float dividant = 400;
		
		water1.transform.setToTranslation(-(tick / dividant % UVratio), 0.5f, tick / dividant % UVratio - UVratio);
		water2.transform.setToTranslation(-(tick / dividant % UVratio) - 1f, 1, -1f);
		
		dividant = 500f;
		
		leaf1.transform.setToTranslation(-(tick / dividant % UVratio), 1.1f, tick / dividant % UVratio - UVratio);
		
		dividant = 400f;
		
		leaf2.transform.setToTranslation(-(tick / dividant % UVratio) - 1f, 1.2f, tick / dividant % UVratio - UVratio);
		
		getCamera().position.set(0, 4, 0);
		getCamera().direction.set(0, -2f, -1f);
		getCamera().update();
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		DefaultShader.defaultCullFace = 1;
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
	}
}
