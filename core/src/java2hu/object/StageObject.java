package java2hu.object;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.events.EventListener;
import java2hu.object.bullet.Bullet;
import java2hu.overwrite.J2hObject;
import java2hu.pathing.PathingHelper;
import java2hu.plugin.Plugin;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.sun.istack.internal.Nullable;

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
		return name;
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
	
	public void onDelete()
	{
		disposeAll();
	}
	
	public void disposeAll()
	{
		disposeDisposables();
		
		disposeChildren();
	}
	
	/**
	 * Disposes of all children by deleting them, done over 5 ticks.
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
	 * Disposes all the disposables of this object, done over 5 ticks.
	 */
	public void disposeDisposables()
	{
		Game.getGame().runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				for(Disposable disp : disposables)
				{
					disp.dispose();
				}
				
				disposables.clear();
			}
		});
	}
	
	public void addDisposable(TextureRegion disp)
	{
		if(disp == null)
			return;
		
		addDisposable(disp.getTexture());
	}
	
	public void addDisposable(Animation disp)
	{
		if(disp == null)
			return;
		
		for(TextureRegion r : disp.getKeyFrames())
			addDisposable(r);
	}
	
	/**
	 * Unregisters a listener once this object is deleted.
	 * @param listener
	 */
	public void addDisposable(final EventListener listener)
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
	}
	
	public void addDisposable(Disposable disp)
	{
		if(disp == null)
			return;
		
		disposables.add(disp);
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
	
	public boolean isOnStage()
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
	
	public void draw()
	{
		if(!isOnStage())
			return;
		
		onDraw();
	}
	
	/**
	 * The draw method should only draw whatever it is on screen.
	 * This will keep drawing even when the game is paused, but updating will not proceed.
	 */
	public abstract void onDraw();
	
	public void update(long tick)
	{
		onUpdate(tick);
		
		for(Plugin effect : effects)
		{
			effect.update(this, tick);
		}
		
		getPathing().tick();
	}
	
	public void update(float tick)
	{
		onUpdateDelta(tick);
		
//		for(Plugin effect : effects)
//		{
//			effect.update(this, tick);
//		}
	}
	
	/**
	 * The update method should be used to update any logic, positioning, etc.
	 * This will halt when the game is paused.
	 * This methods runs on the logic loop, which runs at 60 tps by default, but can be different. 
	 * @param tick
	 */
	public abstract void onUpdate(long tick);
	
	/**
	 * This update method will be called instead with the 
	 * @param delta
	 */
	public void onUpdateDelta(float delta)
	{
		
	}
}
