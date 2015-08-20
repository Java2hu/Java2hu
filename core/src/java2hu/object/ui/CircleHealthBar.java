package java2hu.object.ui;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.LivingObject;
import java2hu.object.StageObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

/**
 * Health bar that surrounds the object. (TH-13+ style)
 */
public class CircleHealthBar extends StageObject
{
	private ShaderProgram sp;
	private ArrayList<Mesh> meshes = new ArrayList<Mesh>();
	private LivingObject source;
	private float radius;
	private Color healthColor = Color.WHITE;
	private Color ringColor = Color.RED;
	private Color indicatorColor = Color.BLUE;
	private Color noHealthColor = null;
	
	private float lastSplitPercentage = 0f;
	private ArrayList<Float> splits = new ArrayList<Float>();
	
	public CircleHealthBar(LivingObject source)
	{
		super(source.getX(), source.getY());
		
		this.source = source;
		this.radius = 110f;
        
		 // this shader tells opengl where to put things
        String vertexShader = "#version 120                  \n"
        					+ "attribute vec4 a_position;    \n"
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
				  			  + "#version 120                \n"
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
        sp = new ShaderProgram(vertexShader, fragmentShader);
        
        // check there's no shader compile errors
        if (sp.isCompiled() == false)
            new IllegalStateException(sp.getLog()).printStackTrace();
		
        addDisposable(sp);
		
		setZIndex(J2hGame.GUI_Z_ORDER - 2);
	}
	
	private float[] makePartialCircleMesh(float posX, float posY, float segments, float radiusStart, float radiusEnd, float percentage, Color inner, Color outer)
	{
		ArrayList<Float> verticesList = new ArrayList<Float>();
		
        // For every section one triangle starting from the middle to the ends.
		
		float increment = 360 / segments;
		float max = 360f * percentage;
		
        for(float deg = 90; deg < max + 90; deg += increment)
        {
        	float end = deg + increment;
			boolean last = !(end < max + 90);
        	
        	if(last)
        	{
        		end = max + 90;
        	}
        	
        	// Triangle 1
        	
        	// Left top
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// left bot
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusStart) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusStart) + posY);
        	verticesList.add(inner.toFloatBits());
        	
        	// Right bot
        	verticesList.add((float) (Math.cos(Math.toRadians(end)) * radiusStart) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(end)) * radiusStart) + posY);
        	verticesList.add(inner.toFloatBits());

        	// Triangle 2
        	
        	// Right top
        	verticesList.add((float) (Math.cos(Math.toRadians(end)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(end)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Left top
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Right bot
        	verticesList.add((float) (Math.cos(Math.toRadians(end)) * radiusStart) + posX);
        	verticesList.add((float) (Math.sin(Math.toRadians(end)) * radiusStart) + posY);
        	verticesList.add(inner.toFloatBits());
        	
        	if(end > max + 90)
        		increment = max + 90 - deg;
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
	
	private float[] makeCircleMesh(float posX, float posY, float segments, float radiusStart, float radiusEnd, Color inner, Color outer)
	{
		ArrayList<Float> verticesList = new ArrayList<Float>();
		
        // For every section one triangle starting from the middle to the ends.
		
		float increment = 360 / segments;
		
        for(float deg = 90; deg < 360 + 90; deg += increment)
        {
        	// Triangle 1
        	
        	// Left top
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// left bot
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusStart) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusStart) + posY);
        	verticesList.add(inner.toFloatBits());
        	
        	// Right bot
        	verticesList.add((float) (Math.sin(Math.toRadians(deg + increment)) * radiusStart) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg + increment)) * radiusStart) + posY);
        	verticesList.add(inner.toFloatBits());
        }
        
        // For every section one triangle starting from the middle to the ends.
        for(float deg = 90; deg < 360 + 90; deg += 360 / segments)
        {
        	// Triangle 2
        	
        	// Right top
        	verticesList.add((float) (Math.sin(Math.toRadians(deg + increment)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg + increment)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Left top
        	verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posY);
        	verticesList.add(outer.toFloatBits());
        	
        	// Right bot
        	verticesList.add((float) (Math.sin(Math.toRadians(deg + increment)) * radiusStart) + posX);
        	verticesList.add((float) (Math.cos(Math.toRadians(deg + increment)) * radiusStart) + posY);
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
	
	private float[] makeSectionIndicator(float posX, float posY, float segments, float radiusStart, float radiusEnd, Color color, float percentage)
	{
		ArrayList<Float> verticesList = new ArrayList<Float>();
		
		float radiusStartBegin = radiusStart;
		float radiusEndBegin = radiusEnd;
		
		radiusEnd = radiusStart + ((radiusEndBegin - radiusStartBegin) / 2f);
		
		Color white = Color.WHITE;
		
		// Quad 1 - Consists of 2 triangles, colored by variable color.
		{
			float size = 3f;
			float deg = 90 - 360 * percentage - size / 2;
			
			// Triangle 1

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(white.toFloatBits());

			// left bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusStart) + posY);
			verticesList.add(color.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
			verticesList.add(color.toFloatBits());

			// Triangle 2

			// Right top
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusEnd) + posY);
			verticesList.add(white.toFloatBits());

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(white.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
			verticesList.add(color.toFloatBits());
		}
		
		radiusStart = radiusStart + ((radiusEndBegin - radiusStartBegin) / 2f);
		radiusEnd = radiusEndBegin + 0.1f;
		
		// Quad 1 - Consists of 2 triangles, colored by variable color.
		{
			float size = 3f;
			float deg = 90 - 360 * percentage - size / 2;
			
			// Triangle 1

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// left bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusStart) + posY);
			verticesList.add(white.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
			verticesList.add(white.toFloatBits());

			// Triangle 2

			// Right top
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
			verticesList.add(white.toFloatBits());
		}
		
		radiusStart = radiusStartBegin;
		
		radiusEnd -= 2f;
		radiusStart += 2f;
		color = Color.WHITE;
		
		// Quad 2 - Consists of 2 triangles, colored white.
		{
			float size = 1f;
			float deg = 90 - 360 * percentage - size / 2;
			
			// Triangle 1

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// left bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusStart) + posY);
			verticesList.add(color.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
			verticesList.add(color.toFloatBits());

			// Triangle 2

			// Right top
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// Left top
			verticesList.add((float) (Math.cos(Math.toRadians(deg)) * radiusEnd) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg)) * radiusEnd) + posY);
			verticesList.add(color.toFloatBits());

			// Right bot
			verticesList.add((float) (Math.cos(Math.toRadians(deg + size)) * radiusStart) + posX);
			verticesList.add((float) (Math.sin(Math.toRadians(deg + size)) * radiusStart) + posY);
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
	
	public float getNextSplitPercentage()
	{
		float closest = 1;
		
		for(Float split : splits)
		{
			if(split < closest)
				closest = split;
		}
		
		return closest;
	}
	
	private Mesh mesh(float[] vertices)
	{
		Mesh mesh = new Mesh(false, vertices.length, 0,
                new VertexAttribute(Usage.Position, 2, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		
		mesh.setVertices(vertices);
		
		meshes.add(mesh);
		
		return mesh;
	}
	
	public void generateNewMeshes(float percentage)
	{
		ArrayList<Float> splits = (ArrayList<Float>) this.splits.clone();
		
		if(!source.isHealing())
		{
			if(!splits.isEmpty())
			{
				percentage = (getNextSplitPercentage() - lastSplitPercentage) * percentage;
				percentage += 1 - getNextSplitPercentage();
			}
			else
			{
				percentage = (1 - lastSplitPercentage) * percentage;
			}
		}

		int size = 40; // Roughness of the circle, the more the smoother, but more intensive

		float startRadius = getRadius();
		
		final float posX = getX();//((source.getX() / Game.getGame().getWidth()f) * 2) - 1;
		final float posY = getY();//(((source.getY() / Game.getGame().getHeight()f) * 2) - 1) / yCorrection;
		
		for(Mesh m : meshes)
		{
			m.dispose();
		}
		
		meshes.clear();
		
		boolean drawNonHealth = getNoHealthColor() != null;
		
		int i = 0;
		
		float borderWidth = 4.4f;
		float gap = -1.8f;
		float middleWidth = 6f;
		
		Color trans = new Color(0, 0, 0, 0f);
		
		mesh(makeCircleMesh(posX, posY, size, startRadius, startRadius + borderWidth * 0.5f, trans, ringColor)); // Inner rings
		mesh(makeCircleMesh(posX, posY, size, startRadius + borderWidth * 0.5f, startRadius + borderWidth, ringColor, trans)); // Inner rings
		
		startRadius += borderWidth + gap;
		
		i++;
		
		float outer = 0.25f;
		
		float beginMiddle = startRadius;
		
		if(drawNonHealth)
		{	
			mesh(makeCircleMesh(posX, posY, size, startRadius, startRadius + (middleWidth * outer), ringColor, noHealthColor)); // Inner Ring
			mesh(makeCircleMesh(posX, posY, size, startRadius + (middleWidth * outer), startRadius + (middleWidth * (1f - outer)), noHealthColor, noHealthColor)); // Inner Ring
			mesh(makeCircleMesh(posX, posY, size, startRadius + (middleWidth * (1f - outer)), startRadius + middleWidth, noHealthColor, ringColor)); // Inner Ring
			
			i++;
		}
		
		mesh(makePartialCircleMesh(posX, posY, size, startRadius, startRadius + (middleWidth * outer), percentage, ringColor, healthColor)); // Inner Ring
		mesh(makePartialCircleMesh(posX, posY, size, startRadius + (middleWidth * outer), startRadius + (middleWidth * (1f - outer)), percentage, healthColor, healthColor)); // Inner Ring
		mesh(makePartialCircleMesh(posX, posY, size, startRadius + (middleWidth * (1f - outer)), startRadius + middleWidth, percentage, healthColor, ringColor)); // Inner Ring

		startRadius += middleWidth + gap;
		
		i++;
		
		mesh(makeCircleMesh(posX, posY, size, startRadius, startRadius + borderWidth * 0.5f, trans, ringColor)); // Outer rings
		mesh(makeCircleMesh(posX, posY, size, startRadius + borderWidth * 0.5f, startRadius + borderWidth, ringColor, trans)); // Outer rings
		
		startRadius += borderWidth;
		
		i++;
		
		for(Float split : splits)
		{
			float overflow = 5f;
			mesh(makeSectionIndicator(posX, posY, 1, beginMiddle - overflow, startRadius - 2f + overflow, getIndicatorColor(), split)); // Section indicator
			i++;
		}
	}

	@Override
	public void onDraw()
	{
		J2hGame g = Game.getGame();
		
		Game.getGame().batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(0.1f);
		
		sp.begin();
		
		Matrix4 combined = Game.getGame().camera.camera.combined;

		sp.setUniformMatrix("u_projTrans", combined);
		
//		for(int i = meshes.size() - 1; i >= 0; i--)
//		{
//			Mesh mesh = meshes.get(i);
			
		for(Mesh mesh : meshes)
		{
			mesh.render(sp, GL20.GL_TRIANGLES);
		}
        
        sp.end();
        
        Game.getGame().batch.begin();
	}
	
	@Override
	public void onUpdate(long tick)
	{
		setPositionUpdate();
		
		float fullHealth = source.getMaxHealth();
		float partHealth = source.getHealth();
		float percentage = partHealth / fullHealth;
		
		generateNewMeshes(percentage);
	}
	
	/**
	 * Overwrite if you want it to take a position
	 */
	public void setPositionUpdate()
	{
		setX(source.getX());
		setY(source.getY());
	}
	
	public void setNoHealthColor(Color activeColor)
	{
		this.noHealthColor = activeColor;
	}
	
	public Color getNoHealthColor()
	{
		return noHealthColor;
	}
	
	public void setHealthColor(Color activeColor)
	{
		this.healthColor = activeColor;
	}
	
	public Color getHealthColor()
	{
		return healthColor;
	}
	
	public void setRingColor(Color backColor)
	{
		this.ringColor = backColor;
	}
	
	public Color getRingColor()
	{
		return ringColor;
	}
	
	public void setIndicatorColor(Color shadowColor)
	{
		this.indicatorColor = shadowColor;
	}
	
	public Color getIndicatorColor()
	{
		return indicatorColor;
	}
	
	public LivingObject getOwner()
	{
		return source;
	}
	
	public void addSplit(float percentage)
	{
		splits.add(percentage);
	}
	
	public void split()
	{
		lastSplitPercentage = getNextSplitPercentage();
		splits.remove(splits.indexOf(lastSplitPercentage));
	}
	
	public void setRadius(float radius)
	{
		this.radius = radius;
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	@Override
	public boolean isPersistant()
	{
		return source.isOnStage();
	}

	@Override
	public float getWidth()
	{
		return getRadius();
	}

	@Override
	public float getHeight()
	{
		return getRadius();
	}
}
