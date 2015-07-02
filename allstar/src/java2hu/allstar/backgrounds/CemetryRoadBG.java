package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.allstar.util.AllStarUtil;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.Fog;
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

/**
 * Template background
 * 
 * set userData of Renderables to z-index.
 */
public class CemetryRoadBG extends Background3D
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
	
	public ModelInstance road;
	public ModelInstance stonesLeft;
	public ModelInstance stonesRight;
	public ModelInstance moon;
	
	public ArrayList<ModelInstance> lamps = new ArrayList<ModelInstance>();
	
	public CemetryRoadBG()
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
		
		FileHandle dir = Gdx.files.internal("scenes/cemetry");
		
		Texture roadText = Loader.texture(dir.child("road.png"));
		roadText.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture stonesText = Loader.texture(dir.child("stones.png"));
		stonesText.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture lampText = Loader.texture(dir.child("lamp.png"));
		stonesText.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture lightText = Loader.texture(dir.child("light.png"));
		stonesText.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture moonText = Loader.texture(dir.child("moon.png"));
		stonesText.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		// Road
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(new BlendingAttribute(true, 1f));
			mat.set(TextureAttribute.createDiffuse(roadText));

			Node node = b.node();
			
			String name = "plane1";
			
			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 5, 50, 1, 10), mat);
			
			Model model = b.end();
			
			road = new ModelInstance(model);
			road.userData = 2d; // Lowest z-index.
			road.transform.setToTranslation(0, 0f, 0f);

			instances.add(road);
		}
		
		// Stones on the left
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(TextureAttribute.createDiffuse(stonesText));

			Node node = b.node();
			
			String name = "stonesLeft";
			
			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 5, 50, 1, 10), mat);
			
			Model model = b.end();
			
			stonesLeft = new ModelInstance(model);
			stonesLeft.userData = 3d;
			stonesLeft.transform.setToTranslation(-10, 0, 0f);

			instances.add(stonesLeft);
		}
		
		// Stones on the right
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1, 1f, 1f, 1f));
			mat.set(TextureAttribute.createDiffuse(stonesText));

			Node node = b.node();
			
			String name = "stonesRight";
			
			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 5, 50, -1, 10), mat);
			
			Model model = b.end();
			
			stonesRight = new ModelInstance(model);
			stonesRight.userData = 3d;
			stonesRight.transform.setToTranslation(10, 0, 0f);

			instances.add(stonesRight);
		}
		
		// Darkness to cover up end of planes.
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(0, 0f, 0f, 0.99f));
			mat.set(new BlendingAttribute(true, 1f));

			Node node = b.node();
			
			String name = "darkness";
			
			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 5, 50, -1, 10), mat);
			
			Model model = b.end();
			
			ModelInstance inst = new ModelInstance(model);
			inst.userData = 3d;
			inst.transform.setToTranslation(19.5f, 0, 0f);

			instances.add(inst);
			
			inst = new ModelInstance(model);
			inst.userData = 3d;
			inst.transform.setToTranslation(-19.5f, 0, 0f);

			instances.add(inst);
		}
		
		// Moon
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 0.7f));
			mat.set(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.4f));
			mat.set(TextureAttribute.createDiffuse(moonText));

			Node node = b.node();

			String name = "moon";
			
			System.out.println("Moon");

			node.id = name;
			mat.id = name;
			b.part(makePlateMesh(b, mat, name, 8, 8, 1, 1), mat);

			Model model = b.end();

			moon = new ModelInstance(model);
			moon.userData = 15d;
			moon.transform.setToTranslation(0f, 11f, -15f);
			moon.transform.rotate(10, 0, 0, 140f);

			instances.add(moon);
		}
		
		// Lamp
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat = new Material();
			mat.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 0.99f));
			mat.set(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f));
			mat.set(TextureAttribute.createDiffuse(lampText));

			float vMin = 0;
			float vMax = 0;
			
			Node node = b.node();
			
			String name = "lampBase";
			
			vMin = 0.5125f;
			vMax = 0.875f;
			
			node.id = name;
			mat.id = name;
			
			// Base
			{
				MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);

				VertexInfo v1 = new VertexInfo().setPos(-1, 0, -1).setNor(1, 0, 1).setUV(0, vMin);
				VertexInfo v2 = new VertexInfo().setPos(-1, 0, 1).setNor(1, 0, 1).setUV(0, vMax);
				VertexInfo v3 = new VertexInfo().setPos(1, 0, 1).setNor(1, 0, 1).setUV(1, vMax);
				VertexInfo v4 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 1).setUV(1, vMin);

				mpb.rect(v1, v4, v3, v2);

				b.part(mpb.getMeshPart(), mat);
			}
			
			node.translation.set(0, 0.05f, 0f);
			
			node = b.node();
			
			vMin = 65/256f;
			vMax = 126/256f;
			
			name = "lampMiddle";
			
			node.id = name;
			mat.id = name;
			
			node.translation.set(0, 0.1f, -1.25f);
			
			// Middle
			{
				MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);

				VertexInfo v1 = new VertexInfo().setPos(-1, 0, -1).setNor(1, 0, 1).setUV(0, vMin);
				VertexInfo v2 = new VertexInfo().setPos(-1, 0, 1).setNor(1, 0, 1).setUV(0, vMax);
				VertexInfo v3 = new VertexInfo().setPos(1, 0, 1).setNor(1, 0, 1).setUV(1, vMax);
				VertexInfo v4 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 1).setUV(1, vMin);

				mpb.rect(v1, v4, v3, v2);

				final MeshPart meshPart = mpb.getMeshPart();
				
				b.part(meshPart, mat);
			}
			
			node = b.node();
			
			name = "lampLight";
			
			node.id = name;
			mat.id = name;
			
			node.translation.set(0, 0.18f, -1.3f);
			
			vMin = 0;
			vMax = 1;
			
			Material matLamp = new Material();
			matLamp.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 0.99f));
			matLamp.set(new BlendingAttribute(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_COLOR, 0.8f));
			matLamp.set(TextureAttribute.createDiffuse(lightText));
			
			// Light
			{
				MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, matLamp);

				VertexInfo v1 = new VertexInfo().setPos(-1, 0, -1).setNor(1, 0, 1).setUV(0, vMin);
				VertexInfo v2 = new VertexInfo().setPos(-1, 0, 1).setNor(1, 0, 1).setUV(0, vMax);
				VertexInfo v3 = new VertexInfo().setPos(1, 0, 1).setNor(1, 0, 1).setUV(1, vMax);
				VertexInfo v4 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 1).setUV(1, vMin);

				mpb.rect(v1, v4, v3, v2);

				final MeshPart meshPart = mpb.getMeshPart();
				
				b.part(meshPart, matLamp);
			}
			
			node = b.node();
			
			name = "lampTop";
			
			node.id = name;
			mat.id = name;
			
			node.translation.set(0, 0.2f, -2.5f);
			
			vMin = 0/256f;
			vMax = 65/256f;
			
			// Top
			{
				MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);

				VertexInfo v1 = new VertexInfo().setPos(-1, 0, -1).setNor(1, 0, 1).setUV(0, vMin);
				VertexInfo v2 = new VertexInfo().setPos(-1, 0, 1).setNor(1, 0, 1).setUV(0, vMax);
				VertexInfo v3 = new VertexInfo().setPos(1, 0, 1).setNor(1, 0, 1).setUV(1, vMax);
				VertexInfo v4 = new VertexInfo().setPos(1, 0, -1).setNor(1, 0, 1).setUV(1, vMin);

				mpb.rect(v1, v4, v3, v2);

				final MeshPart meshPart = mpb.getMeshPart();
				
				b.part(meshPart, mat);
			}
			
			
			Model model = b.end();
			
			for(int i = 0; i < 2 * 10; i++)
			{
				ModelInstance inst = new ModelInstance(model);
				inst.userData = 7d; 
				inst.transform.setToTranslation(0f, 5f, -10f);

				instances.add(inst);
				lamps.add(inst);
			}
		}
		
		setCameraPather(new UpdateObject()
		{
			float yaw = 0f;
			float pitch = 0f;
			
			@Override
			public void onUpdate(long tick)
			{
				Vector3 pos = new Vector3(0, 10f, 0f);
				Vector3 look = new Vector3();
				
				yaw = 270f;
				pitch = 250f;
				
				look.add((float) Math.cos(Math.toRadians(yaw)), (float) Math.cos(Math.toRadians(pitch)), (float) (Math.sin(Math.toRadians(yaw))));

				{
					getCamera().direction.set(look);
					getCamera().position.set(pos);
					getCamera().update();
				}
			}
		});
		
		Fog fog = new Fog(AllStarUtil.from255RGB(0f, 0f, 0f), 5f, 40f, 40f, 0.1f)
		{
			@Override
			public void update(Camera camera)
			{
				bgFog.transform.setTranslation(0, 0, camera.position.z - 37.5f);
				bgFog.userData = 6d;
			}
		};
		
		setFog(fog);
	}
	
	private float lampTimer = 0f;
	
	@Override
	public void onUpdateDelta(float delta)
	{
		translateLooped(road, 0, -1f * delta, 50f, 10f);
		translateLooped(stonesLeft, new Vector3(-10, 0, 0), -0f * delta, -1f * delta, 50f, 10f);
		translateLooped(stonesRight, new Vector3(10, 0, 0), -0f * delta, -1f * delta, 50f, 10f);
		
		moon.transform.rotate(0, 10, 0, 5f * delta);
		
		int lampIndex = 0;
		
		for(int x = 0; x < 2; x++)
		{
			for(int y = 0; y < 10; y++)
			{
				ModelInstance lamp = lamps.get(lampIndex++);
				
				Vector3 trans = stonesLeft.transform.getTranslation(new Vector3());
				
				final float z = trans.z + 5f + (-y * 10f);
				lamp.transform.setToTranslation(x == 0 ? -6.5f : 6.5f, 0.2f, z);
				lamp.transform.rotate(10, 0, 0, 50f);
			}
		}
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		DefaultShader.defaultCullFace = 1;
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
		
		getFog().draw(getCamera(), modelBatch, getEnvironment());
	}
}
