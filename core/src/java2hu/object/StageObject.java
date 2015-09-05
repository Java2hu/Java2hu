package java2hu.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java2hu.Game;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.events.EventListener;
import java2hu.object.bullet.Bullet;
import java2hu.overwrite.J2hObject;
import java2hu.pathing.PathingHelper;
import java2hu.plugin.Plugin;
import java2hu.util.Duration;
import java2hu.util.Duration.Unit;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import javax.annotation.Nullable;

public abstract class StageObject extends J2hObject implements IPosition
{
	private ArrayList<Plugin> effects = new ArrayList<Plugin>();
	
	public ArrayList<Plugin> getEffects()
	{
		return effects;
	}
	
	public void addEffect(Plugin effect)
	{
		effects.add(effect);
	}
	
	public boolean removeEffect(Plugin effect)
	{
		return effects.remove(effect);
	}
	
	protected ArrayList<Disposable> disposables = new ArrayList<Disposable>();
	
	protected float x;
	protected float y;
	protected float lastX;
	protected float lastY;
	protected long lastMoveTime;
	
	protected long createTick;
	
	protected int zIndex = 0;
	
	protected String name;
	
	public StageObject(float x, float y)
	{
		this.x = x;
		this.y = y;
		this.lastY = y;
		this.lastX = x;
	}
	
	public void setX(float x)
	{
		if(!Game.getGame().inBoundary(x, y))
		{
			return;
		}
		
		this.lastX = this.x;
		this.x = x;
		this.lastMoveTime = System.currentTimeMillis();
	}
	
	public void setY(float y)
	{
		if(!Game.getGame().inBoundary(x, y))
		{
			return;
		}
		
		this.lastY = this.y;
		this.y = y;
		this.lastMoveTime = System.currentTimeMillis();
	}
	
	public void setPosition(IPosition pos)
	{
		setPosition(pos.getX(), pos.getY());
	}
	
	public void setPosition(float x, float y)
	{
		setX(x);
		setY(y);
	}
	
	@Override
	public float getX()
	{
		return x;
	}
	
	@Override
	public float getY()
	{
		return y;
	}
	
	/**
	 * Name is used for better profiling (Or whatever you want to use it for)
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Name is used for better profiling (Or whatever you want to use it for)
	 * @return
	 */
	public String getName()
	{
		return name != null ? name : toString();
	}
	
	@Override
	public String toString()
	{
		return name != null ? name : super.toString();
	}
	
	public long getLastMoveTime()
	{
		return lastMoveTime;
	}
	
	public void onSpawn()
	{
		this.createTick = Game.getGame().getActiveTick();
		gameTick = Game.getGame().getActiveTick() == Game.getGame().getTick();
	}
	
	public void delete()
	{
		if(getOwnedBy() != null)
			return;
		
		onDelete();
	}
	
	public void onDelete()
	{
		disposeAll();
	}
	
	public void disposeAll()
	{
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				disposeDisposables();
				
				disposeChildren();
			}
		}, 5 * 20);
	}
	
	/**
	 * Disposes of all children by deleting them.
	 */
	public void disposeChildren()
	{
		Game.getGame().runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				for(StageObject obj : children)
				{
					game.delete(obj);
				}
				
				children.clear();
			}
		});
	}
	
	/**
	 * Disposes all the disposables of this object.
	 */
	public void disposeDisposables()
	{
		Game.getGame().runAsync(new Runnable()
		{
			@Override
			public void run()
			{ 
				Iterator<Disposable> it = disposables.iterator();
				
				int i = 0;
				
				while(it.hasNext())
				{
					final Disposable disp = it.next();
					
					Game.getGame().addTask(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								disp.dispose();
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}, i + 2);
				}
			}
		});
	}
	
	public Disposable addDisposable(TextureRegion disp)
	{
		if(disp == null)
			return null;
		
		addDisposable(disp.getTexture());
		
		return disp.getTexture();
	}
	
	public List<Disposable> addDisposable(Animation disp)
	{
		if(disp == null)
			return null;
		
		ArrayList<Disposable> list = new ArrayList<Disposable>();
		
		for(TextureRegion r : disp.getKeyFrames())
		{
			Disposable d = addDisposable(r);
			list.add(d);
		}
		
		return list;
	}
	
	/**
	 * Unregisters a listener once this object is deleted.
	 * @param listener
	 */
	public Disposable addDisposable(final EventListener listener)
	{
		Disposable disp = new Disposable()
		{
			@Override
			public void dispose()
			{
				Game.getGame().unregisterEvents(listener);
			}
		};
		
		addDisposable(disp);
		
		return disp;
	}
	
	public Disposable addDisposable(Disposable disp)
	{
		if(disp == null)
			return null;
		
		disposables.add(disp);
		
		return disp;
	}
	
	public boolean removeDisposable(Disposable disp)
	{
		return disposables.remove(disp);
	}
	
	public abstract float getWidth();
	public abstract float getHeight();
	
	public float getLastX()
	{
		return lastX;
	}
	
	public float getLastY()
	{
		return lastY;
	}
	
	boolean gameTick = true;
	
	public long getTicksAlive()
	{
		return (gameTick ? Game.getGame().getTick() : Game.getGame().getPauseTick()) - createTick;
	}
	
	public int getZIndex()
	{
		return zIndex;
	}
	
	public void setZIndex(int zIndex)
	{
		this.zIndex = zIndex;
	}
	
	/**
	 * A child object is an object which will be deleted once it's parent is deleted, therefore rendering them a "managed" object.
	 * Child status is gained by being added to the list of children, parent status is having objects in your childrens list.
	 * So you can only influence that by removing or adding children, you can't remove or add a parent to an object.
	 */
	private ArrayList<StageObject> children = new ArrayList<StageObject>();
	
	/**
	 * Returns a read only list of the children of this object.
	 */
	public ArrayList<StageObject> getChildren()
	{
		return new ArrayList<StageObject>(children);
	}
	
	/**
	 * Returns a read only list of all active stage objects that have this object as a child.
	 */
	public ArrayList<StageObject> getParents()
	{
		ArrayList<StageObject> list = new ArrayList<StageObject>();
		
		for(StageObject obj : game.getStageObjects())
		{
			if(obj.isChild(this))
			{
				list.add(obj);
			}
		}
		
		for(Bullet obj : game.getBullets())
		{
			if(obj.isChild(this))
			{
				list.add(obj);
			}
		}
		
		return list;
	}
	
	/**
	 * Adds the specified object as a child for this object.
	 * See {@link #children} for working.
	 */
	public void addChild(StageObject obj)
	{
		children.add(obj);
	}
	
	/**
	 * Removes the specified object as a child for this object.
	 * See {@link #children} for working.
	 */
	public boolean removeChild(StageObject obj)
	{
		return children.remove(obj);
	}
	
	/**
	 * Adds this object as a child to the specified object.
	 * See {@link #children} for working.
	 */
	public void addParent(StageObject obj)
	{
		obj.addChild(this);
	}
	
	/**
	 * Removes this object as a child to the specified object.
	 * See {@link #children} for working.
	 */
	public boolean removeParent(StageObject obj)
	{
		return obj.removeChild(this);
	}
	
	/**
	 * Returns if the specified object is a child to this object.
	 * See {@link #children} for working.
	 */
	public boolean isChild(StageObject obj)
	{
		return children.contains(obj);
	}
	
	/**
	 * Returns if the specified object is a parent of this object.
	 * See {@link #children} for working.
	 */
	public boolean isParent(StageObject obj)
	{
		return obj.children.contains(this);
	}
	
	/**
	 * If this object remains on the stage when it gets cleared.
	 * @return
	 */
	public boolean isPersistant()
	{
		return false;
	}
	
	/**
	 * If this object will get updated while being paused.
	 * @return
	 */
	public boolean isActiveDuringPause()
	{
		return false;
	}
	
	private StageObject ownedBy;
	
	/**
	 * Sets the object this one is owned by, which will make some methods return the status of the owner instead.
	 * @param ownedBy
	 */
	public void setOwnedBy(StageObject ownedBy)
	{
		this.ownedBy = ownedBy;
	}
	
	/**
	 * Null if unowned.
	 */
	public StageObject getOwnedBy()
	{
		return ownedBy;
	}
	
	/**
	 * Returns if this object is on stage.
	 * Or, if this object is owned, returns if it's owner is on stage.
	 */
	public boolean isOnStage()
	{
		if(ownedBy != null)
		{
			return ownedBy.isOnStage();
		}
		
		return isOnStageRaw();
	}
	
	/**
	 * Returns if this object is on the stage.
	 */
	public boolean isOnStageRaw()
	{
		J2hGame g = Game.getGame();
		
		if(g.getStageObjects().contains(this))
		{
			return true;
		}
		else if(g.getBullets().contains(this))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected PathingHelper pathing = new PathingHelper();
	
	public PathingHelper getPathing()
	{
		return pathing;
	}
	
	protected ShaderProgram shader = null;
	
	/**
	 * Set object to a specific shader.
	 * Note: Not pooling items like this together (with z index), or using a lot of different shaders, caused a TON of lag.
	 * @param shader
	 */
	public void setShader(@Nullable ShaderProgram shader)
	{
		this.shader = shader;
	}
	
	public ShaderProgram getShader()
	{
		return shader;
	}
	
	public void clearShader()
	{
		setShader(null);
	}
	
	protected FrameBuffer buffer = null;
	
	/**
	 * Set object to use a specific framebuffer
	 * Note: We haven't tested performance impact if you don't pool framebuffer shared objects together (with z index)
	 * Drawing to a frame buffer will mean that it won't get drawn default, since I'm also new to this, what I used as guide is this:
	 * http://stackoverflow.com/questions/24480901/libgdx-overlay-texture-above-another-texture-using-shader
	 * @param shader
	 */
	public void setFrameBuffer(@Nullable FrameBuffer buffer)
	{
		this.buffer = buffer;
	}
	
	public FrameBuffer getFrameBuffer()
	{
		return buffer;
	}
	
	public void clearFrameBuffer()
	{
		setFrameBuffer(null);
	}
	
	/**
	 * A list of owned objects, any owned objects will be rendered/updated in the same draw/update calls with this object.
	 * The boolean key decides when the object will be drawn.
	 */
	private HashMap<StageObject, OwnedObjectData> ownedObjects = new HashMap<StageObject, OwnedObjectData>();
	
	public static class OwnedObjectData
	{
		/**
		 * Draw the owned object after it's owner.
		 */
		public boolean drawAfter = true;
		
		/**
		 * Set to true to delete.
		 */
		public boolean delete = false;
	}
	
	/**
	 * Adds the specified object as owned by this one.
	 * This method will now call the tick/render methods for this object and it is removed from the stage.
	 * This will use the default data, which will draw after the owned object
	 */
	public void addOwnedObject(StageObject obj)
	{
		addOwnedObject(obj, new OwnedObjectData());
	}
	
	/**
	 * Adds the specified object as owned by this one.
	 * This method will now call the tick/render methods for this object and it is removed from the stage.
	 */
	public void addOwnedObject(StageObject obj, OwnedObjectData data)
	{
		ownedObjects.put(obj, data);
		obj.setOwnedBy(this);
	}
	
	public void removeOwnedObject(StageObject obj)
	{
		OwnedObjectData data = ownedObjects.get(obj);
		
		if(data != null)
		{
			data.delete = true;
			
			obj.setOwnedBy(null);
		}
	}
	
	private int SRC = GL20.GL_SRC_ALPHA;
	private int DST = GL20.GL_ONE_MINUS_SRC_ALPHA;
	
	/**
	 * Sets this objects blend mode to one that will make it appear to be glowing hot.
	 */
	public void setGlowing()
	{
		setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
	}
	
	/**
	 * Sets this object's blend mode to the default.
	 * SRC: GL_SRC_ALPHA
	 * DST: GL_ONE_MINUS_SRC_ALPHA
	 */
	public void setBlendFuncDefault()
	{
		setBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void setBlendFunc(int sFactor, int dFactor)
	{
		SRC = sFactor;
		DST = dFactor;
	}
	
	public int getBlendFuncSrc()
	{
		return SRC;
	}
	
	public int getBlendFuncDst()
	{
		return DST;
	}
	
	public void draw()
	{
		if(!isOnStage())
			return;
		
		for(Entry<StageObject, OwnedObjectData> owned : ownedObjects.entrySet())
		{
			if(owned.getValue().drawAfter)
				continue;
			
			owned.getKey().draw();
		}
		
		onDraw();
		
		for(Entry<StageObject, OwnedObjectData> owned : ownedObjects.entrySet())
		{
			if(!owned.getValue().drawAfter)
				continue;
			
			owned.getKey().draw();
		}
	}
	
	/**
	 * The draw method should only draw whatever it is on screen.
	 * This will keep drawing even when the game is paused, but updating will not proceed.
	 */
	public abstract void onDraw();
	
	public void update(long tick)
	{
		onUpdate(tick);
		
		{
			Iterator<Plugin> it = effects.iterator();

			while(it.hasNext())
			{
				Plugin effect = it.next();

				effect.update(this, tick);

				if(effect.doDelete())
				{
					it.remove();
				}
			}
		}
		
		getPathing().tick();
		
		Iterator<Entry<StageObject, OwnedObjectData>> it = ownedObjects.entrySet().iterator();
		
		while(it.hasNext())
		{
			Entry<StageObject, OwnedObjectData> owned = it.next();
			
			owned.getKey().update(tick);
			
			if(owned.getKey().isOnStageRaw())
			{
				game.delete(owned.getKey()); // Shouldn't be drawn on it's own.
			}
			
			if(owned.getValue().delete)
			{
				owned.getKey().onDelete();
				it.remove();
			}
		}
	}
	
	public void update(float second)
	{
		onUpdateDelta(second);
		
		for(Entry<StageObject, OwnedObjectData> owned : ownedObjects.entrySet())
		{
			owned.getKey().update(second);
		}
	}
	
	/**
	 * Makes this object update forward for the specified duration.
	 */
	public void progress(Duration dur)
	{
		int ticks = (int) dur.toTicks();
		
		for(int i = 0; i < ticks; i++)
		{
			update(game.getTick() + i);
		}
		
		update((float)dur.getValue(Unit.SECOND));
	}
	
	/**
	 * The update method should be used to update any logic, positioning, etc.
	 * This will halt when the game is paused.
	 * This methods runs on the logic loop, which runs at {@value J2hGame#currentTPS} tps by default, but can be different. 
	 * @param tick
	 */
	public abstract void onUpdate(long tick);
	
	/**
	 * This update method will be called every frame, with delta as the time passed since the last frame was rendered.
	 * Basically any visual things should be updated here, like movement.
	 * This is because they can be smoothed out to look really good at all framerates.
	 */
	public void onUpdateDelta(float delta)
	{
		
	}
}
