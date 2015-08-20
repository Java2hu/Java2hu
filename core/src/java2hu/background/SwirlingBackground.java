package java2hu.background;

import java.util.ArrayList;
import java2hu.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class SwirlingBackground extends ABackground
{
	ShaderProgram sp;
	Mesh mesh;
	Texture texture;
	public Color color;
	boolean useColor = true;
	
	public SwirlingBackground(Texture texture)
	{
		this(texture, true);
	}
	
	/**
	 * Make a swirling background with the specified texture
	 * @param texture
	 * @param useBlendColor - Use the predefined color field that contains a copy of the color WHITE as a blending color, if false it will use the texture's color.
	 */
	public SwirlingBackground(Texture texture, boolean useBlendColor)
	{
		this(texture, useBlendColor, Color.WHITE.cpy());
	}

	/**
	 * Make a swirling background with the specified texture
	 * @param texture
	 * @param useBlendColor - Use the predefined color field that contains a copy of the color WHITE as a blending color, if false it will use the texture's color.
	 */
	public SwirlingBackground(Texture texture, boolean useBlendColor, Color color)
	{
		this.texture = texture;
		
		this.color = color;
		
		 // this shader tells opengl where to put things
        String vertexShader = "#version 120                  \n"
        					+ "attribute vec4 a_position;    \n"
        					+ "attribute vec4 a_color;       \n"
        					+ "attribute vec2 a_texCoords;   \n"
    						+ "varying vec4 v_color;         \n"
    						+ "varying vec2 v_texCoords;     \n"
                            + "void main()                   \n"
                            + "{                             \n"
                            + "   v_color = a_color;         \n"
                            + "   v_texCoords = a_texCoords; \n"
                            + "   gl_Position = a_position;  \n"
                            + "}                             \n";
 
        // this one tells it what goes in between the points (i.e
        // colour/texture)
        String fragmentShader = "#ifdef GL_ES                \n"
				  			  + "#version 120                \n"
                              + "precision mediump float;    \n"
                              + "#endif                      \n"
                              
                              + (useBlendColor ? 
                            	"varying vec4 v_color;       \n" : 
                            	"")
                            	
                              + "varying vec2 v_texCoords;   \n"
                              + "uniform sampler2D u_texture;\n"
                              + "void main()                 \n"
                              + "{                           \n"
                              
                              + (useBlendColor ? 
                            	"  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);   \n"
                            		  :
                            	"  gl_FragColor = texture2D(u_texture, v_texCoords);   \n"
                            	)
                            	  
                              + "}";
 
        // make an actual shader from our strings
        sp = new ShaderProgram(vertexShader, fragmentShader);
        
        // check there's no shader compile errors
        if (sp.isCompiled() == false)
            new IllegalStateException(sp.getLog()).printStackTrace();
	}

	@Override
	public void onDraw()
	{
		Game.getGame().batch.end();
		
		if(mesh == null)
		{
			float[] vertices = makeSwirlMesh(getTimer(), useColor ? color : null);

			if(useColor)
				mesh = new Mesh(false, vertices.length, 0,
					new VertexAttribute(Usage.Position, 3, "a_position"),
					new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
					new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
			else
				mesh = new Mesh(false, vertices.length, 0,
						new VertexAttribute(Usage.Position, 3, "a_position"),
						new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

			mesh.setVertices(vertices);
		}
		else
		{
			mesh.setVertices(makeSwirlMesh(getTimer(), useColor ? color : null));
		}

		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(getBlendFuncSrc(), getBlendFuncDst());

		sp.begin();
		sp.setUniformi("u_texture", 0);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

		mesh.render(sp, GL20.GL_TRIANGLES);

		sp.end();

		Game.getGame().batch.begin();
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		updateTimer();
	}
	
	public abstract void updateTimer();
	
	public abstract float getTimer();

	public static float[] makeSwirlMesh(float currentSize, Color color)
	{
		boolean useColor = color != null;
		
		int size = 400; // How much triangles make up this mesh.
        ArrayList<Float> verticesList = new ArrayList<Float>();
        
        float centerX = 0f;
        float centerY = 0.5f;
        
        float increment = 360f / size;
        float widthModifier = 1.5f;
        float heightModifier = 2f;
        
        float sectionSize = 1f;
        float degreeOffset = 0f;
        
        // For every section one triangle starting from the middle to the ends.
        for(float deg = 0; deg < 360 - increment; deg += increment)
        {
        	// Left top
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * widthModifier) + centerX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * heightModifier) + centerY);
        	verticesList.add(0f);
        	verticesList.add((deg + degreeOffset) / 360);
        	verticesList.add(currentSize);
        	
        	if(useColor)
        		verticesList.add(color.toFloatBits());
        	
        	// Middle bot
        	verticesList.add(centerX);
        	verticesList.add(centerY);
        	verticesList.add(0f);
        	verticesList.add((deg + degreeOffset) / 360);
        	verticesList.add(currentSize + sectionSize);
        	
        	if(useColor)
        		verticesList.add(color.toFloatBits());
        	
        	// Right top
        	verticesList.add((float) (Math.sin(Math.toRadians(deg + increment)) * widthModifier) + centerX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg + increment)) * heightModifier) + centerY);
        	verticesList.add(0f);
        	verticesList.add((deg + increment + degreeOffset) / 360);
        	verticesList.add(currentSize);
        	
        	if(useColor)
        		verticesList.add(color.toFloatBits());
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
}
