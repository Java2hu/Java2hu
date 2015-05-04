package java2hu.util;

import java.util.ArrayList;

import java2hu.Game;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

/**
 * Util to make a few simple meshes
 * 
 * The format for the vertices is  new VertexAttribute(Usage.Position, 2, "a_position"), new VertexAttribute(Usage.ColorPacked, 4, "a_color")
 */
public class MeshUtil extends J2hObject
{
	private static ShaderProgram program;
	
	private static ShaderProgram getShaderProgram()
	{
		if(program == null)
		{
			 // this shader tells opengl where to put things
	        String vertexShader = "attribute vec4 a_position;    \n"
	        					+ "attribute vec4 a_color;       \n"
	    						+ "varying vec4 v_color;         \n"
	    						+ "uniform mat4 u_projTrans;     \n"
	                            + "void main()                   \n"
	                            + "{                             \n"
	                            + "   v_color = a_color;         \n"
	                            + "   gl_Position = u_projTrans * a_position;  \n"
	                            + "}                             \n";
	 
	        // this one tells it what goes in between the points (i.e
	        // colour/texture)
	        String fragmentShader = "#ifdef GL_ES				 \n"
	    						  + "#define LOWP lowp			 \n"
	    						  + "precision mediump float;	 \n"
	    						  + "#else						 \n"
	    						  + "#define LOWP 				 \n"
	    						  + "#endif						 \n"
	                              + "varying vec4 v_color;       \n"
	                              + "varying vec2 v_texCoords;   \n"
	                              + "void main()                 \n"
	                              + "{                           \n"
	                              + "  gl_FragColor = v_color;   \n"
	                              + "}";
	 
	        // make an actual shader from our strings
	        program = new ShaderProgram(vertexShader, fragmentShader);
	        
	        // check there's no shader compile errors
	        if (program.isCompiled() == false)
	            new IllegalStateException(program.getLog()).printStackTrace();
		}
		
		return program;
	}
	
	/**
	 * Starts the ShaderProgram made for all things created in this util
	 */
	public static void startShader()
	{
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(1f);
		
		getShaderProgram().begin();
		
		Matrix4 combined = Game.getGame().camera.camera.combined;

		getShaderProgram().setUniformMatrix("u_projTrans", combined);
	}
	
	/**
	 * Ends the ShaderProgram made for all things created in this util
	 */
	public static void endShader()
	{
		getShaderProgram().end();
	}
	
	public static void renderMesh(Mesh mesh)
	{
		mesh.render(getShaderProgram(), GL20.GL_TRIANGLES);
	}
	
	/**
	 * Update or make a mesh usable with this MeshUtil
	 * Feed in the last mesh you used to see if it can be re-used.
	 * @param lastMesh - If this mesh isn't re-used, it's disposed.
	 * @param vertices
	 * @return
	 */
	public static Mesh makeMesh(Mesh lastMesh, float[] vertices)
	{
		boolean createNewMesh = lastMesh == null || lastMesh.getMaxVertices() < vertices.length;
		
		Mesh mesh;
		
		if(createNewMesh)
		{
			if(lastMesh != null)
				lastMesh.dispose();
			
			mesh = new Mesh(true, vertices.length, 0,
	                new VertexAttribute(Usage.Position, 2, "a_position"),
	                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		}
		else
		{
			mesh = lastMesh;
		}
		
		mesh.setVertices(vertices);
		
		return mesh;
	}
	
	/**
	 * Makes a mesh circle
	 * @param posX
	 * @param posY
	 * @param segments - A circle is never a perfect circle, but rather a lot of segments, more segments means a better circle, but less performance.
	 * @param radiusStart
	 * @param radiusEnd
	 * @param color
	 * @return
	 */
	public static float[] makeCircleVertices(float posX, float posY, float segments, float radiusStart, float radiusEnd, Color color)
	{
		return makeCircleVertices(posX, posY, segments, radiusStart, radiusEnd, color, color);
	}
	
	/**
	 * Makes a mesh circle
	 * @param posX
	 * @param posY
	 * @param segments - A circle is never a perfect circle, but rather a lot of segments, more segments means a better circle, but less performance.
	 * @param radiusStart
	 * @param radiusEnd
	 * @param inner - Inner color
	 * @param outer - Outer color
	 * @return
	 */
	public static float[] makeCircleVertices(float posX, float posY, float segments, float radiusStart, float radiusEnd, Color inner, Color outer)
	{
		ArrayList<Float> verticesList = new ArrayList<Float>();
		
        // For every section one triangle starting from the middle to the ends.
		
		float increment = 360 / segments;
		
        for(float deg = 0; deg < 360; deg += increment)
        {
        	// Triangle 1
        	
        	float rad1 = (float) Math.toRadians(deg);
        	float cos1 = (float) Math.cos(rad1);
        	float sin1 = (float) Math.sin(rad1);
        	
        	float rad2 = (float) Math.toRadians(deg + increment);
        	float cos2 = (float) Math.cos(rad2);
        	float sin2 = (float) Math.sin(rad2);
        	
        	// Left top
        	verticesList.add(sin1 * radiusEnd + posX);
        	verticesList.add(cos1 * radiusEnd + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Left bot
        	verticesList.add(sin1 * radiusStart + posX);
        	verticesList.add(cos1 * radiusStart + posY);
        	verticesList.add(inner.toFloatBits());
        	
        	// Right bot
        	verticesList.add(sin2 * radiusStart + posX);
        	verticesList.add(cos2 * radiusStart + posY);
        	verticesList.add(inner.toFloatBits());

        	// Triangle 2
        	
        	// Right top
        	verticesList.add(sin2 * radiusEnd + posX);
        	verticesList.add(cos2 * radiusEnd + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Left top
        	verticesList.add(sin1 * radiusEnd + posX);
        	verticesList.add(cos1 * radiusEnd + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Right bot
        	verticesList.add(sin2 * radiusStart + posX);
        	verticesList.add(cos2 * radiusStart + posY);
        	verticesList.add(inner.toFloatBits());
        }
        
		float[] vertices = new float[verticesList.size()];
		
		int i = 0;
		for(Float flo : verticesList)
		{
			vertices[i] = flo;
			i++;
		}

		return vertices;
	}
	
	public static float[] makeRectangleVertices(float posX, float posY, float width, float height, Color color)
	{
		ArrayList<Float> verticesList = new ArrayList<Float>();
		
        // For every section one triangle starting from the middle to the ends.

		// Triangle 1

		// Left top
		verticesList.add(posX);
		verticesList.add(posY + height);
		verticesList.add(color.toFloatBits());

		// Left bot
		verticesList.add(posX);
		verticesList.add(posY);
		verticesList.add(color.toFloatBits());

		// Right bot
		verticesList.add(posX + width);
		verticesList.add(posY);
		verticesList.add(color.toFloatBits());

		// Triangle 2

		// Right top
		verticesList.add(posX + width);
		verticesList.add(posY + height);
		verticesList.add(color.toFloatBits());

		// Left top
		verticesList.add(posX);
		verticesList.add(posY + height);
		verticesList.add(color.toFloatBits());

		// Right bot
		verticesList.add(posX + width);
		verticesList.add(posY);
		verticesList.add(color.toFloatBits());

		float[] vertices = new float[verticesList.size()];
		
		int i = 0;
		for(Float flo : verticesList)
		{
			vertices[i] = flo;
			i++;
		}

		return vertices;
	}
}
