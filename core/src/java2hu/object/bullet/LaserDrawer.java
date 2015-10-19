package java2hu.object.bullet;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Position;
import java2hu.overwrite.J2hObject;
import java2hu.util.GetterSetter;
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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	double thickness;
	
	private double hitboxThickness;
	
	public LaserDrawer(LaserAnimation ani, double thickness, double hitboxThickness)
	{
		super((Animation)null, 0, 0);
		
		textures = ani;
		this.thickness = thickness;
		this.setHitboxThickness(hitboxThickness);
		this.setZIndex(1000);
		useDeleteAnimation = false;
		useSpawnAnimation = false;
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
	
	public void setThickness(double thickness)
	{
		this.thickness = thickness;
	}
	
	public double getThickness()
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
	public void setDirectionDegTick(double degree, double speed)
	{
		super.setDirectionDegTick(degree, speed);
	}
	
	@Deprecated
	@Override
	public void setDirectionRadsTick(double radians, double speed)
	{
		super.setDirectionRadsTick(radians, speed);
	}
	
	@Deprecated
	@Override
	public void useDeathAnimation(boolean bool)
	{

	}
	
	@Deprecated
	@Override
	public void useSpawnAnimation(boolean useSpawnAnimation)
	{

	}
	
	@Deprecated
	@Override
	public void spawnAnimation()
	{

	}
	
	@Deprecated
	@Override
	public void deleteAnimation()
	{

	}
	
	@Deprecated
	@Override
	public void oldDeleteAnimation()
	{

	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		
		for(Position p : getPoints())
		{
			if(Float.isNaN(p.getX()) || Float.isNaN(p.getY()))
				continue;
			
			spawnSwirl(p, false);
		}
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

			TextureRegion textureRegion = getCurrentTexture();
			Texture texture = textureRegion.getTexture();
			
			texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

			Matrix4 combined = new Matrix4().set(Game.getGame().camera.camera.combined);

			sp.setUniformMatrix("u_projTrans", combined);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			
			Gdx.gl.glBlendFunc(getBlendFuncSrc(), getBlendFuncDst());
			
			mesh.render(sp, GL20.GL_TRIANGLES);

			sp.end();

			Game.getGame().batch.begin();
		}
	}

	private TextureRegion getCurrentTexture()
	{
		return textures.getCurrentTexture(Game.getGame().getTick());
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
			
			double shortestDistance = Float.MAX_VALUE;
			Position shortestPoint = null;
			
			for(Position p : getPoints())
			{
				double distance = MathUtil.getDistance(p, g.getPlayer());
				
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
		
		@Override
		public MeshData clone()
		{
			MeshData newData = new MeshData();
			newData.x = x;
			newData.y = y;
			newData.u = u;
			newData.v = v;
			
			return newData;
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
		
		TextureRegion r = getCurrentTexture();
		
		double uMin = r.getU();
		double uMax = r.getU2();
		double uSize = uMax - uMin;
		
		double vMin = r.getV();
		double vMax = r.getV2();
		double vSize = vMax - vMin;
		
		GetterSetter<Double, Double> convertU = (f) -> (uMin + ((f - uMin) * uSize));
		GetterSetter<Double, Double> convertV = (f) -> (vMin + ((f - vMin) * vSize));
		
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
			
			// Cant make a section that's so small...
			if(sectionPoints.size() < 2)
				continue;
			
			Double lastAngle = null;
			Position first = sectionPoints.get(0);
			
			Float lastX = first.getX();
			Float lastY = first.getY();
			
			for(int i = 1; i < sectionPoints.size(); i++)
			{
				Position p = sectionPoints.get(i);
				
				double distance = MathUtil.getDistance(lastX, lastY, p.getX(), p.getY());
				
				double angle = MathUtil.getAngle(lastX, lastY, p.getX(), p.getY());
				
				if (lastAngle == null)
					lastAngle = angle;
				
				if(Double.isNaN(angle))
				{
					angle = lastAngle;
				}
				
				double angleLeft = angle + 90d;
				double angleRight = angle - 90d;

				double cosLeft = fastCos(angleLeft);
				double cosRight = fastCos(angleRight);

				double sinLeft = fastSin(angleLeft); 
				double sinRight = fastSin(angleRight);
				
				double angleLeftLast = lastAngle + 90d;
				double angleRightLast = lastAngle - 90d;

				double cosLeftLast = fastCos(angleLeftLast);
				double cosRightLast = fastCos(angleRightLast);

				double sinLeftLast = fastSin(angleLeftLast);
				double sinRightLast = fastSin(angleRightLast);
				
				double V1 = ((currentDistance / totalDistance));
				double V2 = ((currentDistance + distance) / totalDistance);
				
				double U1 = 0d;
				double U2 = 1d;
				
				double X = p.getX();
				
				double Y = p.getY();
				
				MeshData p00 = new MeshData().setPos((lastX + cosLeftLast * getThickness()), (lastY + sinLeftLast * getThickness())).setUV(U1, V1);
				MeshData p01 = new MeshData().setPos((lastX + cosRightLast * getThickness()), (lastY + sinRightLast * getThickness())).setUV(U2, V1);
					
				MeshData p10 = new MeshData().setPos((X + cosLeft * getThickness()), (Y + sinLeft * getThickness())).setUV(U1, V2);
				MeshData p11 = new MeshData().setPos((X + cosRight * getThickness()), (Y + sinRight * getThickness())).setUV(U2, V2);
				
				MeshData h00 = new MeshData().setPos((lastX + cosLeft * getHitboxThickness()), (lastY + sinLeft * getHitboxThickness()));
				MeshData h01 = new MeshData().setPos((lastX + cosRight * getHitboxThickness()), (lastY + sinRight * getHitboxThickness()));
				
				MeshData h11 = new MeshData().setPos((X + cosRight * getHitboxThickness()), (Y + sinRight * getHitboxThickness()));
				MeshData h10 = new MeshData().setPos((X + cosLeft * getHitboxThickness()), (Y + sinLeft * getHitboxThickness()));
				
				MeshData[] info = { 
						p00, p10, p11,
						p00, p01, p11,
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
					
					float precision = 5f;
					
					mesh.add((float) MathUtil.roundOff(convertU.get(v.u).floatValue(), precision));
					mesh.add((float) MathUtil.roundOff(convertV.get(v.v).floatValue(), precision));
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
				
				lastAngle = angle;
				lastX = p.getX();
				lastY = p.getY();
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
	
	/**
	 * Make use of fast math.
	 */
	private static final boolean FAST = true;
	
	public double fastSin(double degree)
	{
		if(FAST)
			return MathUtil.fastSin(degree);
		
		return Math.sin(Math.toRadians(degree));
	}
	
	public double fastCos(double degree)
	{
		if(FAST)
			return MathUtil.fastCos(degree);
		
		return Math.cos(Math.toRadians(degree));
	}
	
	public double getHitboxThickness()
	{
		return hitboxThickness;
	}

	public void setHitboxThickness(double hitboxThickness)
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
		Array<TextureRegion> frames;
		float timePerFrame;
		
		/**
		 * Creates a laser animation from textures, assuming UV {0, 0 - 1, 1}
		 * @param timePerFrame
		 * @param frames
		 */
		public LaserAnimation(float timePerFrame, Texture... frames)
		{
			this(timePerFrame, getArrayFrom(frames));
		}
		
		/**
		 * Creates a laser animation
		 * @param timePerFrame
		 * @param frames
		 */
		public LaserAnimation(float timePerFrame, TextureRegion... frames)
		{
			this(timePerFrame, getArrayFrom(frames));
		}
		
		static Array<TextureRegion> getArrayFrom(Texture... frames)
		{
			Array<TextureRegion> array = new Array<TextureRegion>();
			
			for (Texture t : frames)
				array.add(new TextureRegion(t));
			
			return array;
		}
		
		static Array<TextureRegion> getArrayFrom(TextureRegion... frames)
		{
			Array<TextureRegion> array = new Array<TextureRegion>();
			array.addAll(frames);
			
			return array;
		}
		
		public LaserAnimation(float timePerFrame, Array<TextureRegion> frames)
		{
			this.frames = frames;
			this.timePerFrame = timePerFrame;
		}
		
		public TextureRegion getCurrentTexture(float time)
		{
			int frameId = getKeyFrameIndex(time) % frames.size;
			
			return frames.get(frameId);
		}
		
		public int getKeyFrameIndex(float stateTime)
		{
			return (int) (stateTime / timePerFrame);
		}
		
		public Array<TextureRegion> getFrames()
		{
			return frames;
		}
	}
}
