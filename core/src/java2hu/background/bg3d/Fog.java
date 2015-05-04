package java2hu.background.bg3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public abstract class Fog extends ModelObject
{
	public ModelInstance bgFog;
	
	public Fog(Color color)
	{
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		
		color = color.cpy();
		color.a = 0.4f;
		
		float size = 30;
		
		float increase = 0.1f;
		
		for(float i = 0; i < 3; i += increase)
		{
			Node node = mb.node();

			MeshPartBuilder builder = mb.part("plane" + i, GL20.GL_TRIANGLES, new VertexAttributes(new VertexAttribute(Usage.Position, 3, "a_position")), new Material(ColorAttribute.createDiffuse(color), new BlendingAttribute(color.a)));
			
			float halfSize = size / 2f;
			builder.rect(new Vector3(-halfSize, -halfSize, 0), new Vector3(halfSize, -halfSize, 0), new Vector3(halfSize, halfSize, 0), new Vector3(-halfSize, halfSize, 0), new Vector3());
			builder.setColor(color);
			
			
			node.translation.z -= i;
		}

		bgFog = new ModelInstance(mb.end());
	}

	@Override
	public void draw(Camera camera, ModelBatch modelBatch, Environment environment)
	{
		modelBatch.render(bgFog, environment);
	}
}
