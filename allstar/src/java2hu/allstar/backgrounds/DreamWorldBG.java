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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class DreamWorldBG extends Background3D
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
	
	public ModelInstance grid1;
	public ModelInstance grid2;
	public ModelInstance grid3;
	
	public ModelInstance stars;
	
	public DreamWorldBG()
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
		
		FileHandle dir = Gdx.files.internal("scenes/dream world/");
		
		Texture grid1 = Loader.texture(dir.child("grid1.png"));
		grid1.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture grid2 = Loader.texture(dir.child("grid2.png"));
		grid2.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
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
		
		// Grid 1 and 3
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(grid1));

			String name = "grid1";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 15), mat);
			
			Model model = b.end();
			
			this.grid1 = new ModelInstance(model);
			this.grid1.userData = 1d;
			this.grid1.transform.setToTranslation(0, 1f, 0f);
			
			instances.add(this.grid1);
			
			this.grid3 = new ModelInstance(model);
			this.grid3.userData = 3d;
			this.grid3.transform.setToTranslation(0, 2f, 0f);

			instances.add(this.grid3);
		}
		
		// Grid 2
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, 0.3f));
			mat.set(TextureAttribute.createDiffuse(grid2));

			String name = "grid2";

			Node node = b.node();

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 15), mat);
			
			Model model = b.end();
			
			this.grid2 = new ModelInstance(model);
			this.grid2.userData = 2d;
			this.grid2.transform.setToTranslation(0, 1.5f, 0f);

			instances.add(this.grid2);
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
	
	public MeshPart makePlateMesh(ModelBuilder b, Material mat, String name, float UVMul)
	{
		return makePlateMesh(b, mat, name, 10, UVMul);
	}
	
	public MeshPart makePlateMesh(ModelBuilder b, Material mat, String name, float size, float UVMul)
	{
		MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);

		VertexInfo v1 = new VertexInfo().setPos(-size, 0, -size).setNor(1, 0, 1).setUV(0, 0);
		VertexInfo v2 = new VertexInfo().setPos(-size, 0, size).setNor(1, 0, 1).setUV(0, 1 * UVMul);
		VertexInfo v3 = new VertexInfo().setPos(size, 0, size).setNor(1, 0, 1).setUV(1 * UVMul, 1 * UVMul);
		VertexInfo v4 = new VertexInfo().setPos(size, 0, -size).setNor(1, 0, 1).setUV(1 * UVMul, 0);

		mpb.rect(v1, v4, v3, v2);

		return mpb.getMeshPart();
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		translateLooped(grid1, -0.005f, -0.005f, 10f, 15f);
		translateLooped(grid2, -0.0025f, -0.005f, 10f, 15f);
		translateLooped(grid3, -0.00125f, -0.005f, 10f, 15f);
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
