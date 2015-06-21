package java2hu;

import java2hu.util.AnimationUtil;
import java2hu.util.Duration;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * An animation containing a start animation drawn at the beginning of the movement, an animation looping while in movement, and an animation drawn at the end of the movement.
 */
public class MovementAnimation extends StartupLoopAnimation
{
	Array<TextureRegion> startFrames;
	Array<TextureRegion> loopFrames;
	Array<TextureRegion> endFrames;
	
	private float timer = 0f;
	
	public final float timePerFrame;
	
	public MovementAnimation(MovementAnimation copy)
	{
		this(AnimationUtil.copyFrames(copy.startFrames), AnimationUtil.copyFrames(copy.loopFrames), AnimationUtil.copyFrames(copy.endFrames), copy.timePerFrame);
	}
	
	/**
	 * Uses a single set of frames twice to create a start and end animation.
	 * Start frames is the wind animation normally.
	 * End frames is the wind animation reversed.
	 */
	public MovementAnimation(Animation wind, Animation loop, float timePerFrame)
	{
		this(new Array<TextureRegion>(wind.getKeyFrames()), new Array<TextureRegion>(loop.getKeyFrames()), flip(wind), timePerFrame);
	}
	
	private static Array<TextureRegion> flip(Animation ani)
	{
		Array<TextureRegion> end = new Array<TextureRegion>();
		
		TextureRegion[] frames = AnimationUtil.copyFrames(ani.getKeyFrames());
		
		for(int i = frames.length - 1; i >= 0; i--)
		{
			end.add(frames[i]);
		}
		
		return end;
	}
	
	public MovementAnimation(Animation start, Animation loop, Animation end, float timePerFrame)
	{
		this(new Array<TextureRegion>(start.getKeyFrames()), new Array<TextureRegion>(loop.getKeyFrames()), new Array<TextureRegion>(end.getKeyFrames()), timePerFrame);
	}
	
	public MovementAnimation(Array<TextureRegion> start, Array<TextureRegion> loop, Array<TextureRegion> end, float timePerFrame)
	{
		super(start, loop, timePerFrame); // We don't use any of this...
		
		startFrames = start;
		loopFrames = loop;
		endFrames = end;
		
		this.timePerFrame = timePerFrame;
	}
	
	/**
	 * Draw the movement animation
	 * @param batch
	 * @param x
	 * @param y
	 * @param deltaTime
	 */
	@Override
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
	@Override
	public void increase(float deltaTime)
	{
		timer += deltaTime;
	}
	
	@Override
	public float getTimer()
	{
		return timer;
	}
	
	@Override
	public void setTimer(float timer)
	{
		this.timer = timer;
	}
	
	/**
	 * Reset the internal timer keeping track of when to loop.
	 */
	@Override
	public void reset()
	{
		timer = 0f;
		endFrame = -1;
	}
	
	@Override
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
		else if(endFrame >= 0 && endFrame < endFrames.size)
		{
			frame = endFrames.get(endFrame);
		}
		else
		{
			frameId = frameId - startFrames.size;
			frameId = frameId % loopFrames.size;
			
			frame = loopFrames.get(frameId);
		}
		
		return frame;
	}
	
	private int endFrame = -1;
	
	/**
	 * Updates the animation to display the end animation by checking if the movement time to completion is less than the animation's time.
	 */
	public void checkEnd(Duration timeLeft)
	{
		if(timeLeft.getValue() <= getEndAnimationTime().getValue())
		{
			endFrame = (int) (endFrames.size * (timeLeft.getValue() / getEndAnimationTime().getValue()));
		
			endFrame = endFrames.size - endFrame - 1;
		}
	}
	
	private Duration getEndAnimationTime()
	{
		final float seconds = endFrames.size / timePerFrame;
		
		return Duration.seconds(seconds);
	}
	
	/**
	 * Just returns an combination array of both frame arrays
	 */
	@Override
	public TextureRegion[] getKeyFrames()
	{
		TextureRegion[] regs = new TextureRegion[startFrames.size + loopFrames.size + endFrames.size];
		
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
		
		for(TextureRegion r : endFrames)
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
	
	@Override
	public void setLoopFrames(Array<TextureRegion> loopFrames)
	{
		this.loopFrames = loopFrames;
	}
	
	@Override
	public void setStartFrames(Array<TextureRegion> startFrames)
	{
		this.startFrames = startFrames;
	}
	
	public void setEndFrames(Array<TextureRegion> endFrames)
	{
		this.endFrames = endFrames;
	}
}
