package java2hu;

import java.util.ArrayList;
import java.util.List;
import java2hu.util.ImageSplitter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

public class AnimationBuilder
{
	private int chunkHeight;
	private int chunkWidth;
	private Texture sprite;

	/**
	 * A utility class to easily make several boss sprite related animations.
	 * @param chunkHeight
	 * @param chunkWidth
	 */
	public AnimationBuilder(Texture sprite, int chunkWidth, int chunkHeight)
	{
		this.sprite = sprite;
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;
	}
	
	/**
	 * Create a movement animation from the data.
	 * Data should be in the following format:
	 * 
	 * windup ; loop ; animation time
	 * Add a -1 to indicate the end of the frames
	 * 
	 * So an animation with frames 1-3 as windup, frames 4 and 5 as loop, at 8 seconds per frame would be:
	 * 1,2,3,-1,4,5,-1,8f
	 * 
	 * @param data
	 * @return
	 */
	public MovementAnimation movement(Object... data) 
	{
		FramesResult windup = getFrames(data, 0);
		FramesResult loop = getFrames(data, windup.end);
		float secondsPerFrame = (float)data[loop.end];

		final Animation windupAnimation = getAnimation(windup.frameIds);
		final Animation loopAnimation = getAnimation(loop.frameIds);
		
		return new MovementAnimation(windupAnimation, loopAnimation, secondsPerFrame);
	}
	
	public Animation simpleLoop(float frameDuration, Integer... frames)
	{
		return simple(PlayMode.LOOP, frameDuration, frames);
	}
	
	public Animation simple(float frameDuration, Integer... frames)
	{
		return simple(PlayMode.NORMAL, frameDuration, frames);
	}
	
	public Animation simple(PlayMode type, float frameDuration, Integer... frames)
	{
		final Animation ani = getAnimation(frameDuration, frames);
		
		ani.setPlayMode(type);
		
		return ani;
	}
	
	private class FramesResult
	{
		private Integer[] frameIds;
		private int end;
	}
	
	private Animation getAnimation(Integer... frames)
	{
		return getAnimation(0f, frames);
	}
	
	private Animation getAnimation(float frameDuration, Integer... frames)
	{
		return ImageSplitter.getAnimationFromSprite(sprite, chunkHeight, chunkWidth, frameDuration, frames);
	}
	
	private FramesResult getFrames(Object[] data, int start)
	{
		FramesResult r = new FramesResult();
		
		List<Integer> frames = new ArrayList<Integer>();
		
		int end = 0;
		
		for (int i = start; i < data.length; i++)
		{
			Object obj = data[i];
			
			final int frameId = (int)obj;
			
			if (frameId == -1) {
				end = i + 1;
				break;
			}
			
			frames.add(frameId);
		}
		
		r.end = end;
		
		Integer[] frameIds = frames.toArray(new Integer[frames.size()]);
		
		r.frameIds = frameIds;
		
		return r;
	}
}
