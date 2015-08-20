package java2hu;

import java2hu.util.AnimationUtil;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * An animation containing a start animation drawn once, then loops through and draws the loop animation.
 */
public class StartupLoopAnimation extends Animation
{
	Array<TextureRegion> startFrames;
	Array<TextureRegion> loopFrames;
	private float timer = 0f;
	public final float timePerFrame;
	
	public StartupLoopAnimation(StartupLoopAnimation copy)
	{
		this(AnimationUtil.copyFrames(copy.startFrames), AnimationUtil.copyFrames(copy.loopFrames), copy.timePerFrame);
	}
	
	public StartupLoopAnimation(Animation start, Animation loop, float timePerFrame)
	{
		this(new Array<TextureRegion>(start.getKeyFrames()), new Array<TextureRegion>(loop.getKeyFrames()), timePerFrame);
	}
	
	public StartupLoopAnimation(Array<TextureRegion> start, Array<TextureRegion> loop, float timePerFrame)
	{
		super(timePerFrame, start); // We don't use any of this...
		startFrames = start;
		loopFrames = loop;
		this.timePerFrame = timePerFrame;
	}
	
	public Array<TextureRegion> getStartFrames()
	{
		return startFrames;
	}
	
	public Array<TextureRegion> getLoopFrames()
	{
		return loopFrames;
	}
	
	/**
	 * Draw the movement animation
	 * @param batch
	 * @param x
	 * @param y
	 * @param deltaTime
	 */
	public void draw(Batch batch, float x, float y)
	{
		TextureRegion toDraw = getCurrentFrame();
		
		if(toDraw instanceof Sprite)
		{
			((Sprite)toDraw).setPosition(x, y);
			((Sprite)toDraw).draw(batch);
		}
		else
			batch.draw(toDraw, x, y);
	}
	
	/**
	 * Increase the internal timer keeping track of what to draw and when to loop.
	 * @param deltaTime
	 */
	public void increase(float deltaTime)
	{
		timer += deltaTime;
	}
	
	public float getTimer()
	{
		return timer;
	}
	
	public void setTimer(float timer)
	{
		this.timer = timer;
	}
	
	/**
	 * Reset the internal timer keeping track of when to loop.
	 */
	public void reset()
	{
		timer = 0f;
	}
	
	public TextureRegion getCurrentFrame()
	{
		return getKeyFrame(timer);
	}
	
	@Override
	public TextureRegion getKeyFrame(float stateTime, boolean looping)
	{
		return getKeyFrame(stateTime);
	}
	
	@Override
	public int getKeyFrameIndex(float stateTime)
	{
		return (int) (stateTime / timePerFrame);
	}
	
	@Override
	public boolean isAnimationFinished(float stateTime)
	{
		return false; // We neva finish
	}
	
	@Override
	public TextureRegion getKeyFrame(float stateTime)
	{
		int frameId = getKeyFrameIndex(stateTime);
		
		TextureRegion frame;
		
		if(frameId < startFrames.size)
		{
			frame = startFrames.get(frameId);
		}
		else
		{
			frameId = frameId - startFrames.size;
			frameId = frameId % loopFrames.size;
			
			frame = loopFrames.get(frameId);
		}
		
		return frame;
	}
	
	/**
	 * Just returns an combination array of both frame arrays
	 */
	@Override
	public TextureRegion[] getKeyFrames()
	{
		TextureRegion[] regs = new TextureRegion[startFrames.size + loopFrames.size];
		
		int i = 0;
		
		for(TextureRegion r : startFrames)
		{
			regs[i] = r;
			i++;
		}
		
		for(TextureRegion r : loopFrames)
		{
			regs[i] = r;
			i++;
		}
		
		return regs;
	}
	
	@Override
	public PlayMode getPlayMode()
	{
		return PlayMode.NORMAL;
	}
	
	@Override
	public void setPlayMode(PlayMode playMode)
	{
		// We don't use this.
	}
	
	public void setLoopFrames(Array<TextureRegion> loopFrames)
	{
		this.loopFrames = loopFrames;
	}
	
	public void setStartFrames(Array<TextureRegion> startFrames)
	{
		this.startFrames = startFrames;
	}
}
