package java2hu.background.bg3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public abstract class Fog extends ModelObject implements Disposable
{
	public ModelInstance bgFog;
	
	private Model model;
	
	public Fog(Color color)
	{
		this(color, 40f, 10f, 10f, 0.5f);
	}
	
	public Fog(Color color, float length, float sizeX, float sizeZ, float increase)
	{
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		
		color = color.cpy();
		color.a = 0f;
		
		for(float i = 0; i <= length; i += increase)
		{
			float iMul = (i / length);
			
			color.a = (1f * (1f-iMul));
			
			Node node = mb.node();

			final Material mat = new Material();
		
			if(color.a < 1f)
			{
				mat.set(ColorAttribute.createDiffuse(color));
				mat.set(new BlendingAttribute(color.a));
			}
			else
			{
				mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.9f));
				mat.set(new BlendingAttribute(0.99f));
			}
			
			MeshPartBuilder builder = mb.part("plane" + i, GL20.GL_TRIANGLES, new VertexAttributes(new VertexAttribute(Usage.Position, 3, "a_position")), mat);
			
			float halfSizeX = sizeX / 2f;
			float halfSizeZ = sizeZ / 2f;
			
			builder.rect(new Vector3(-halfSizeX, -halfSizeZ, 0), new Vector3(halfSizeX, -halfSizeZ, 0), new Vector3(halfSizeX, halfSizeZ, 0), new Vector3(-halfSizeX, halfSizeZ, 0), new Vector3());
			builder.setColor(color);
			
			node.translation.z = -(length * (1f-iMul));
		}

		model = mb.end();

		bgFog = new ModelInstance(model);
	}

	@Override
	public void draw(Camera camera, ModelBatch modelBatch, Environment environment)
	{
		modelBatch.render(bgFog, environment);
	}
	
	@Override
	public void dispose()
	{
		model.dispose();
	}
}
