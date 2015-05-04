package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.utils.Array;

public class MagicalStormBG extends Background3D
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
					
					if(d1 > d2)
						return -1;
					
					return 0;
				}
			});
		}
	});
	
	public ArrayList<ModelInstance> instances = new ArrayList<ModelInstance>();
	public HashMap<ModelInstance, Float> clouds = new HashMap<ModelInstance, Float>();
	
	public ModelInstance arch1;
	public AngleData arch1d = new AngleData();
	public ModelInstance arch2;
	public AngleData arch2d = new AngleData();
	public ModelInstance arch3;
	public AngleData arch3d = new AngleData();
	
	public static class AngleData
	{
		public float yaw = 0;
		public float pitch = 0;
		public float roll = 0;
	}
	
	public ModelInstance hueFog;
	
	public MagicalStormBG()
	{
		setModelBatch(batch);

		RotationPerspectiveCamera camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0,0,0);
		camera.lookAt(0,0,0);
		camera.near = 0.1f;
		camera.far = 1000f;
		camera.update();
		
		setCamera(camera);
		
		Environment environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, 1f, 0f, 0f));
        
        setEnvironment(environment);
        
		J2hGame game = Game.getGame();
		
		Texture fog = Loader.texture(Gdx.files.internal("scenes/magical storm/fog.png"));
		fog.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			
		Texture cloud = Loader.texture(Gdx.files.internal("scenes/magical storm/cloud1.png"));
		cloud.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		Texture arch = Loader.texture(Gdx.files.internal("scenes/magical storm/arch.png"));
		arch.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		// Arches
		
		
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat1 = new Material();
			mat1.set(ColorAttribute.createDiffuse(1f, 1f, 1f, 0.9f));
			mat1.set(new BlendingAttribute(true, 0.9f));
			mat1.set(TextureAttribute.createDiffuse(arch));

			String name = "arch1";

			Node node = b.node();

			node.id = name;
			mat1.id = name;
			b.part(makeArchMesh(b, mat1, name, 8, 9.5f), mat1);
			
			name = "arch2";
			
			node = b.node();

			node.id = name;
			mat1.id = name;
			b.part(makeArchMesh(b, mat1, name, 10f, 11.5f), mat1);

			Model model = b.end();
			
			arch1 = new ModelInstance(model);
			
			arch2 = new ModelInstance(model);
			
			for(Material m : arch2.materials)
			{
				m.set(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.8f, 0.5f));
			}
			
			arch3 = new ModelInstance(model);
			
			for(Material m : arch3.materials)
			{
				m.set(ColorAttribute.createDiffuse(0.8f, 0.5f, 0.5f, 0.5f));
			}
			
			arch1.userData = 40;

			instances.add(arch1);
			instances.add(arch2);
			instances.add(arch3);
		}
		
		// Fog that creates the depth at the bottom
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat1 = new Material();
			mat1.set(ColorAttribute.createDiffuse(0, 0f, 0f, 0.8f));
			mat1.set(new BlendingAttribute(true, 1f));
			mat1.set(TextureAttribute.createDiffuse(fog));

			String name = "depth_fog";

			Node node = b.node();

			node.id = name;
			mat1.id = name;
			b.part(makeStormMesh(b, mat1, name, true, 9, 9), mat1);

			ModelInstance modelInstance = new ModelInstance(b.end());

			modelInstance.userData = 1;

			instances.add(modelInstance);
		}

		// Fog that creates the golden hue over the clouds
		{
			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat1 = new Material();
			mat1.set(ColorAttribute.createDiffuse(1, 0.9f, 0.4f, 0.4f));
			mat1.set(new BlendingAttribute(true, 0.8f));
			mat1.set(TextureAttribute.createDiffuse(fog));

			String name = "color_fog";

			Node node = b.node();

			node.id = name;
			mat1.id = name;
			b.part(makeStormMesh(b, mat1, name, true, 9, 9), mat1);

			hueFog = new ModelInstance(b.end());

			hueFog.userData = 0;

			instances.add(hueFog);
		}

		// clouds
		{
			int scale = 10;

			ModelBuilder b = new ModelBuilder();
			b.begin();

			Material mat1 = new Material();
			
			mat1.set(ColorAttribute.createDiffuse(1, 0.8f, 0.8f, 1f));
			mat1.set(new BlendingAttribute(true, 0.3f));
			mat1.set(TextureAttribute.createDiffuse(cloud));

			String name = "storm" + scale;

			Node node = b.node();

			node.id = name;
			mat1.id = name;
			b.part(makeStormMesh(b, mat1, name, false, scale, scale), mat1);

			ModelInstance modelInstance = new ModelInstance(b.end());

			modelInstance.userData = scale;

			instances.add(modelInstance);
			clouds.put(modelInstance, (float) (Math.random() * 720f - 360f));
		}
		
		onUpdate(0);
	}
	
	public MeshPart makeStormMesh(ModelBuilder b, Material mat, String name, boolean fog, float scaleX, float scaleY)
	{
		MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);
		
		float increment = 1f;
		float minHeight = -20;
		float maxHeight = 20;
		
		for(float angle = 0; angle < 360; angle += increment)
		{
			float texStart = angle / 360;
			float texEnd = (angle + increment) / 360;
			
			float texMinHeight = 1;
			float texMaxHeight = 0f;
			
			if(!fog)
			{
				texStart *= 6;
				texEnd *= 6;
				texMinHeight *= 6;
			}
			
			float rad1 = (float)Math.toRadians(angle);
			float rad2 = (float)Math.toRadians(angle + increment);
			
			float sin1 = (float)Math.sin(rad1) * scaleY;
			float sin2 = (float)Math.sin(rad2) * scaleY;
			
			float cos1 = (float)Math.cos(rad1) * scaleX;
			float cos2 = (float)Math.cos(rad2) * scaleX;
			
			VertexInfo v1 = new VertexInfo().setPos(cos1, minHeight, sin1).setNor(1, 0, 1).setUV(texStart, texMinHeight);
			VertexInfo v2 = new VertexInfo().setPos(cos1, maxHeight, sin1).setNor(1, 0, 1).setUV(texStart, texMaxHeight);
			VertexInfo v3 = new VertexInfo().setPos(cos2, maxHeight, sin2).setNor(1, 0, 1).setUV(texEnd, texMaxHeight);
			VertexInfo v4 = new VertexInfo().setPos(cos2, minHeight, sin2).setNor(1, 0, 1).setUV(texEnd, texMinHeight);
			
			mpb.rect(v1, v4, v3, v2);
		}

		return mpb.getMeshPart();
	}
	
	public MeshPart makeArchMesh(ModelBuilder b, Material mat, String name, float radiusBegin, float radiusEnd)
	{
		MeshPartBuilder mpb = b.part(name, GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, mat);
		
		float increment = 2;
		
		for(int scaleOffset = 0; scaleOffset < 2; scaleOffset += 2)
		for(float angle = 0; angle < 360; angle += increment)
		{
			float u1 = angle / 40f;
			float u2 = (angle + increment) / 40f;
			
			float v1 = 0;
			float v2 = 1;
			
			float rad1 = (float) Math.toRadians(angle);
			float rad2 = (float) Math.toRadians(angle + increment);
			
			float x1 = (float) (Math.cos(rad1) * radiusBegin);
			float y1 = (float) (Math.sin(rad1) * radiusBegin);
			
			float x2 = (float) (Math.cos(rad1) * radiusEnd);
			float y2 = (float) (Math.sin(rad1) * radiusEnd);
			
			float x3 = (float) (Math.cos(rad2) * radiusEnd);
			float y3 = (float) (Math.sin(rad2) * radiusEnd);
			
			float x4 = (float) (Math.cos(rad2) * radiusBegin);
			float y4 = (float) (Math.sin(rad2) * radiusBegin);
			
			VertexInfo i1 = new VertexInfo().setPos(x1, 0, y1).setUV(u1, v1);
			VertexInfo i2 = new VertexInfo().setPos(x2, 0, y2).setUV(u1, v2);
			VertexInfo i3 = new VertexInfo().setPos(x3, 0, y3).setUV(u2, v2);
			VertexInfo i4 = new VertexInfo().setPos(x4, 0, y4).setUV(u2, v1);
			
			mpb.rect(i4, i3, i2, i1);
		}

		return mpb.getMeshPart();
	}
	
	@Override
	protected boolean useStandardFadeOut()
	{
		return true;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		for(Entry<ModelInstance, Float> s : clouds.entrySet())
		{
			s.getKey().transform.rotate(0, 1, 0, -0.1f);
		}
		
		arch1d.yaw += 0.4f;
		arch2d.yaw += 0.4f;
		arch3d.yaw += 0.4f;
		
		arch2d.pitch = -20;
		arch3d.pitch = -20;
		arch1d.roll = -10;
		

		arch1.transform.setFromEulerAngles(arch1d.yaw, arch1d.pitch, arch1d.roll);
		arch1.transform.setTranslation(-4, -6, 0);
		
		arch2.transform.setFromEulerAngles(arch2d.yaw, arch2d.pitch, arch2d.roll);
		arch2.transform.setTranslation(-5, 1, -3);
		
		arch3.transform.setFromEulerAngles(arch3d.yaw, arch3d.pitch, arch3d.roll);
		arch3.transform.setTranslation(-5, 1, 3);
		
		if(tick % 310 > 0 && tick % 310 < 10)
		{
			float mul = tick % 310f / 5f;
			
			if(mul > 1)
				mul = 2 - mul;
			
			hueFog.transform.setTranslation(0, mul * 5f, 0);
			
			for(Material m : hueFog.materials)
			{
				m.set(ColorAttribute.createDiffuse(1, 0.9f + 0.1f * mul, 0.4f + 0.2f * mul, 0.4f + 0.2f * mul));
				m.set(new BlendingAttribute(true, 0.7f + 0.2f * mul));
			}
		}
		
		getCamera().position.set(-12, 5, 0);
		getCamera().direction.set(1, -0.4f, 0);
		getCamera().update();
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		Gdx.gl.glDepthMask(false);
		
		for(ModelInstance instance : instances)
			modelBatch.render(instance, environment);
	}
}
