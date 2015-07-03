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

public class MoonBG extends Background3D
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
	
	public ModelInstance moon;
	public ModelInstance rabbit;
	
	public Material rabbitMat;
	
	public ModelInstance stars;
	
	public MoonBG()
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
		
		FileHandle dir = Gdx.files.internal("scenes/moon/");
		
		Texture moon = Loader.texture(dir.child("moon.png"));
		moon.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture rabbit = Loader.texture(dir.child("rabbit.png"));
		
		Texture stars = Loader.texture(dir.child("stars.png"));
		stars.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		// Stars
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(stars));

			String name = "stars";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 15), mat);
			
			Model model = b.end();
			
			this.stars = new ModelInstance(model);
			this.stars.userData = 0d;

			instances.add(this.stars);
		}
		
		// Moon
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(0, 0f, 0f, 0f));
			mat.set(new BlendingAttribute(true, 1f));
			mat.set(TextureAttribute.createDiffuse(moon));

			String name = "moon";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 3, 3, 1, 1), mat);
			
			Model model = b.end();
			
			this.moon = new ModelInstance(model);
			this.moon.userData = 2d;
			this.moon.transform.setToTranslation(0, 1f, 0f);
			
			instances.add(this.moon);
		}
		
		// Rabbit
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 0f));
			mat.set(new BlendingAttribute(true, 0f));
			mat.set(TextureAttribute.createDiffuse(rabbit));
			
			rabbitMat = mat;

			String name = "rabbit";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 3, 3, 1, 1), mat);
			
			Model model = b.end();
			
			this.rabbit = new ModelInstance(model);
			this.rabbit.userData = 3d;
			this.rabbit.transform.setToTranslation(0, 1.5f, 0f);

			instances.add(this.rabbit);
		}
		
		setCameraPather(new UpdateObject()
		{
			float yaw = 0f;
			float pitch = 0f;
			
			@Override
			public void onUpdate(long tick)
			{
				Vector3 pos = new Vector3(0, 4f, -0.4f);
				Vector3 look = new Vector3();
				
				yaw = 270f;
				pitch = 180f;
				
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
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		moon.transform.setToTranslation(0, 0.1f, -4);
		
		final float circleTime = 800f;
		final float mul = (tick % circleTime) / circleTime;
		
		moon.transform.rotate(0, 1, 0, mul * 360f);
		
		Material mat = rabbit.materials.first();
		
		float ticker = (tick % 500) / 500f;
		
		ticker *= 2;
		
		if(ticker > 1f)
			ticker = 2f - ticker;
		
		mat.set(ColorAttribute.createDiffuse(1f, 1f - (0.7f * ticker), 1f - (0.7f * ticker), 1f * ticker));
		mat.set(new BlendingAttribute(true, 1f * ticker));
		
		mat = moon.materials.first();
		
		mat.set(ColorAttribute.createDiffuse(1f, 1f - (0.7f * ticker), 1f - (0.7f * ticker), 1f));
		mat.set(new BlendingAttribute(1f));
		
		rabbit.transform.setToTranslation(0f, 0.11f, -4f);
		rabbit.transform.rotate(0, 1, 0, mul * 360f);
		
		translateLooped(stars, 0f, -0.01f, 10f, 15f);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		DefaultShader.defaultCullFace = 1;
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
	}
}
