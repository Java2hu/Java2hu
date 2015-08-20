package java2hu.object.bullet;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Position;
import java2hu.overwrite.J2hObject;
import java2hu.util.MathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;

public class LaserDrawer extends Bullet
{
	ArrayList<Position> points = new ArrayList<Position>();
	
	Mesh mesh;
	
	LaserData data;
	Polygon hitbox = new Polygon();
	ShaderProgram sp = SpriteBatch.createDefaultShader();
	LaserAnimation textures;
	float thickness;
	
	private float hitboxThickness;
	
	public LaserDrawer(LaserAnimation ani, float thickness, float hitboxThickness)
	{
		super((Animation)null, 0, 0);
		
		textures = ani;
		this.thickness = thickness;
		this.setHitboxThickness(hitboxThickness);
		this.setZIndex(1000);
	}

	@Override
	public float getWidth()
	{
		return 0;
	}

	@Override
	public float getHeight()
	{
		return 0;
	}
	
	public void setThickness(float thickness)
	{
		this.thickness = thickness;
	}
	
	public float getThickness()
	{
		return thickness;
	}
	
	public LaserAnimation getTextures()
	{
		return textures;
	}
	
	public void setTextures(LaserAnimation textures)
	{
		this.textures = textures;
	}
	
	@Override
	public boolean doDelete()
	{
		if(points.isEmpty())
			return false;
		
		for(Position pos : points)
		{
			if(Game.getGame().inBoundary(pos.getX(), pos.getY()))
				return false;
		}
		
		return true;
	};
	
	public ArrayList<Position> getPoints()
	{
		return points;
	}
	
	public Position addPoint(float x, float y)
	{
		Position p = new Position(x, y);
		points.add(p);
		
		return p;
	}
	
	public void deletePoint(Position pos)
	{
		for(Position p : getPoints())
		{
			if(p.equals(pos))
			{
				p.setX(Float.NaN);
				p.setY(Float.NaN);
			}
		}
	}
	
	@Deprecated
	@Override
	public void setDirectionDegTick(float degree, float speed)
	{
		super.setDirectionDegTick(degree, speed);
	}
	
	@Deprecated
	@Override
	public void setDirectionRadsTick(float radians, float speed)
	{
		super.setDirectionRadsTick(radians, speed);
	}
	
	@Deprecated
	@Override
	public void spawnAnimation()
	{

	}
	
	@Override
	public void deleteAnimation()
	{
		for(Position p : getPoints())
		{
			if(Float.isNaN(p.getX()) || Float.isNaN(p.getY()))
				continue;
			
			spawnSwirl(p, false);
		}
	}
	
	@Deprecated
	@Override
	public void oldDeleteAnimation()
	{

	}
	
	@Override
	public HitboxSprite getCurrentSprite()
	{
		return null;
	}
	
	@Override
	public Animation getAnimation()
	{
		return null;
	}

	@Override
	public void onDraw()
	{
		if(data != null)
		{
			Game.getGame().batch.end();

			mesh = getMesh(mesh, data.verticesMesh);

			sp.begin();

			Texture texture = textures.getCurrentTexture(Game.getGame().getTick());

			texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

			Matrix4 combined = new Matrix4().set(Game.getGame().camera.camera.combined);

			sp.setUniformMatrix("u_projTrans", combined);

			Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
			
			Gdx.gl.glBlendFunc(getBlendFuncSrc(), getBlendFuncDst());
			
			Gdx.graphics.getGL20().glEnable(GL20.GL_TEXTURE_2D);
			Gdx.graphics.getGL20().glActiveTexture(GL20.GL_TEXTURE0);

			mesh.render(sp, 7 /* Quads */);

			sp.end();

			Game.getGame().batch.begin();
		}
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		if(makeNewMesh)
			makeNewMesh();
	}
	
	
	boolean makeNewMesh = true;
	
	public void doMakeNewMesh(boolean makeNewMesh)
	{
		this.makeNewMesh = makeNewMesh;
	}
	
	public boolean doMakeNewMesh()
	{
		return makeNewMesh;
	}
	
	public void makeNewMesh()
	{
		data = makeLaserMeshVertices();

		if(data.verticesHitbox != null && data.verticesHitbox.length >= 6)
		{
			hitbox.setVertices(data.verticesHitbox);
			hitbox.setPosition(data.centerX, data.centerY);
			hitbox.setOrigin(data.centerX, data.centerY);
		}
	}

	@Override
	public void checkCollision()
	{
		J2hGame g = Game.getGame();

		if(getTicksAlive() < 10)
			return;
		
		if(getHitbox() == null)
			return;
		
		float[] vertices = getHitbox().getTransformedVertices();
		float[] playerVertices = g.getPlayer().getHitbox().getTransformedVertices();
		
		boolean hit = false;
		
		// I had to use alternative detection for this.
		// The polygon is in convex, but is not handled properly by the normal polygon intersection.
		// This should be just as good though, since lasers are simple forms.
		for(int i = 0; i < playerVertices.length; i += 2)
		{
			float x = playerVertices[i];
			float y = playerVertices[i + 1];
			
			if(Intersector.isPointInPolygon(vertices, 0, vertices.length, x, y))
			{
				hit = true;
				break;
			}
		}
		
		if(hit)
		{
			g.getPlayer().onHit(this);
			onHit();
			
			float shortestDistance = Float.MAX_VALUE;
			Position shortestPoint = null;
			
			for(Position p : getPoints())
			{
				float distance = MathUtil.getDistance(p, g.getPlayer());
				
				if(shortestPoint == null || distance < shortestDistance)
				{
					shortestDistance = distance;
					shortestPoint = p;
				}
			}
			
			if(shortestPoint != null)
			{
				deletePoint(shortestPoint);
			}
		}
	}
	
	@Override
	public Polygon getHitbox()
	{ 
		return hitbox;
	}
	
	public Mesh getMesh(Mesh lastMesh, float[] vertices)
	{
		boolean createNewMesh = lastMesh == null || lastMesh.getMaxVertices() < vertices.length;
		
		Mesh mesh;
		
		if(createNewMesh)
		{
			if(lastMesh != null)
			{
				disposables.remove(lastMesh);
				lastMesh.dispose();
			}
			
			mesh = new Mesh(false, vertices.length, 0,
	                new VertexAttribute(Usage.Position, 2, "a_position"),
	                new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
	                new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"));
			
			addDisposable(mesh);
		}
		else
		{
			mesh = lastMesh;
		}
		
		mesh.setVertices(vertices);
		
		return mesh;
	}
	
	private class MeshData
	{
		public double x;
		public double y;
		
		public double u;
		public double v;
		
		public MeshData setPos(double x, double y)
		{
			this.x = x;
			this.y = y;
			
			return this;
		}
		
		public MeshData setUV(double u, double v)
		{
			this.u = u;
			this.v = v;
			
			return this;
		}
	}
	
	public LaserData makeLaserMeshVertices()
	{
		LaserData data = new LaserData();
		
		Color color = Color.WHITE.cpy();
		
		ArrayList<Position> hitboxForward = new ArrayList<Position>();
		ArrayList<Position> hitboxBackward = new ArrayList<Position>();
		ArrayList<Float> mesh = new ArrayList<Float>();
		
		ArrayList<ArrayList<Position>> laserSections = new ArrayList<ArrayList<Position>>();
		
		ArrayList<Position> list = new ArrayList<Position>();
		
		laserSections.add(list);
		
		for(Position p : points)
		{
			// Laser break detected.
			if(Float.isNaN(p.getX()))
			{
				list = new ArrayList<Position>();
				laserSections.add(list);
				continue;
			}
			
			list.add(p);
		}
		
		for(ArrayList<Position> sectionPoints : laserSections)
		{
			double totalDistance = 0;
			double currentDistance = 0;
			
			for(int i = 0; i < sectionPoints.size() - 1; i++)
			{
				Position p1 = sectionPoints.get(i);
				Position p2 = sectionPoints.get(i + 1);
				
				double distance = MathUtil.getDistance(p1, p2);
				
				totalDistance += distance;
			}
			
			MeshData lastP11 = null;
			MeshData lastP10 = null;
			
			MeshData lastH11 = null;
			MeshData lastH10 = null;
			
			// Cant make a section that's so small...
			if(sectionPoints.size() < 2)
				continue;
			
			double lastAngle = 0;
			
			for(int i = 0; i < sectionPoints.size() - 1; i++)
			{
				Position p1 = sectionPoints.get(i);
				Position p2 = sectionPoints.get(i + 1);
				
				double distance = MathUtil.getDistance(p1, p2);
				
				double angle = MathUtil.getAngle(p1, p2);
				
				if(Double.isNaN(angle))
				{
					angle = lastAngle;
					distance = 0;
				}
				else
				{
					lastAngle = angle;
				}
				
				double angleLeft = angle + 90f;
				double angleRight = angle - 90f;

				double cosLeft = fastCos(angleLeft);
				double cosRight = fastCos(angleRight);

				double sinLeft = fastSin(angleLeft);
				double sinRight = fastSin(angleRight);
				
				double U1 = currentDistance / totalDistance;
				double U2 = ((currentDistance + distance) / totalDistance);

				MeshData p00 = null;
				
				if(lastP10 == null)
					p00 = new MeshData().setPos((p1.getX() + cosLeft * getThickness()), (p1.getY() + sinLeft * getThickness())).setUV(U1, 0);
				else
					p00 = lastP10.setUV(U1, 0);
				
				MeshData p01 = null;
				
				if(lastP11 == null)
					p01 = new MeshData().setPos((p1.getX() + cosRight * getThickness()), (p1.getY() + sinRight * getThickness())).setUV(U1, 1);
				else
					p01 = lastP11.setUV(U1, 1);
				
				MeshData p11 = new MeshData().setPos((p2.getX() + cosRight * getThickness()), (p2.getY() + sinRight * getThickness())).setUV(U2, 1);
				MeshData p10 = new MeshData().setPos((p2.getX() + cosLeft * getThickness()), (p2.getY() + sinLeft * getThickness())).setUV(U2, 0);

				lastP11 = p11;
				lastP10 = p10;
				
				MeshData h00 = null;
				
				if(lastH10 == null)
					h00 = new MeshData().setPos((p1.getX() + cosLeft * getHitboxThickness()), (p1.getY() + sinLeft * getHitboxThickness()));
				else
					h00 = lastH10;
				
				MeshData h01 = null;
				
				if(lastH11 == null)
					h01 = new MeshData().setPos((p1.getX() + cosRight * getHitboxThickness()), (p1.getY() + sinRight * getHitboxThickness()));
				else
					h01 = lastH11;
				
				MeshData h11 = new MeshData().setPos((p2.getX() + cosRight * getHitboxThickness()), (p2.getY() + sinRight * getHitboxThickness()));
				MeshData h10 = new MeshData().setPos((p2.getX() + cosLeft * getHitboxThickness()), (p2.getY() + sinLeft * getHitboxThickness()));

				lastH11 = h11;
				lastH10 = h10;
				
				MeshData[] info = { 
						p00, p01, p11, p10, // Quad
						};
				
				MeshData[] forward = { 
						h00, h10
						};
				
				MeshData[] back = { 
						h01, h11
						};
				
				for(MeshData v : info)
				{
					mesh.add((float) v.x);
					mesh.add((float) v.y);
					
					mesh.add(color.toFloatBits());
					
					mesh.add((float) v.v);
					mesh.add((float) v.u);
				}
				
				for(MeshData h : forward)
				{
					hitboxForward.add(new Position(h.x, h.y));
				}
				
				for(MeshData h : back)
				{
					hitboxBackward.add(new Position(h.x, h.y));
				}
				
				currentDistance += distance;
			}
		}
		
		data.verticesMesh = toFloatArray(mesh);
		
		Position[] hitboxVertices = new Position[hitboxForward.size() + hitboxBackward.size()];
		
		int pos = 0;
		
		for(int i = 0; i < hitboxForward.size(); i++)
		{
			hitboxVertices[i] = hitboxForward.get(i);
			
			pos = i;
		}
		
		for(int i = hitboxBackward.size() - 1; i >= 0; i--)
		{
			hitboxVertices[pos + hitboxBackward.size() - i] = hitboxBackward.get(i);
		}
		
		float[] hitboxFloatVertices = new float[hitboxVertices.length * 2];
		
		int index = 0;
		
		for(Position p : hitboxVertices)
		{
			hitboxFloatVertices[index] = p.getX();
			hitboxFloatVertices[index + 1] = p.getY();
			
			index += 2;
		}
		
		data.verticesHitbox = hitboxFloatVertices;
		
		return data;
	}
	
	public float[] toFloatArray(ArrayList<Float> arrayList)
	{
		float[] newArray = new float[arrayList.size()];
		
		for(int i = 0; i < arrayList.size(); i++)
		{
			newArray[i] = arrayList.get(i);
		}
		
		return newArray;
	}
	
	public double fastSin(double degree)
	{
		boolean fast = true;
		
		if(fast)
			return MathUtil.fastSin(degree);
		
		return Math.sin(Math.toRadians(degree));
	}
	
	public double fastCos(double degree)
	{
		boolean fast = true;
		
		if(fast)
			return MathUtil.fastCos(degree);
		
		return Math.cos(Math.toRadians(degree));
	}
	
	public float getHitboxThickness()
	{
		return hitboxThickness;
	}

	public void setHitboxThickness(float hitboxThickness)
	{
		this.hitboxThickness = hitboxThickness;
	}

	public static class LaserData extends J2hObject
	{
		float centerX;
		float centerY;
		
		float[] verticesMesh;
		float[] verticesHitbox;
	}
	
	public static class LaserAnimation extends J2hObject
	{
		Array<Texture> frames;
		float timePerFrame;
		
		public LaserAnimation(float timePerFrame, Texture... frames)
		{
			this(timePerFrame, getArrayFrom(frames));
		}
		
		static Array<Texture> getArrayFrom(Texture... frames)
		{
			Array<Texture> array = new Array<Texture>();
			array.addAll(frames);
			
			return array;
		}
		
		public LaserAnimation(float timePerFrame, Array<Texture> frames)
		{
			this.frames = frames;
			this.timePerFrame = timePerFrame;
		}
		
		public Texture getCurrentTexture(float time)
		{
			int frameId = getKeyFrameIndex(time) % frames.size;
			
			return frames.get(frameId);
		}
		
		public int getKeyFrameIndex(float stateTime)
		{
			return (int) (stateTime / timePerFrame);
		}
		
		public Array<Texture> getFrames()
		{
			return frames;
		}
	}
}
