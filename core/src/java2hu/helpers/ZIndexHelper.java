package java2hu.helpers;

import java2hu.object.StageObject;

/**
 * Simple Z-Indexer
 * Add bullets to this helper, and it will increment a z-index for you.
 */
public class ZIndexHelper
{
	private int indexLimit = -1;
	
	/**
	 * Creates an indexer without an index limit, use with caution.
	 * An index limit prevents overflows of stage objects to different z-index ranges by resetting the timer once the index limit is reached.
	 */
	public ZIndexHelper()
	{
		this(-1);
	}
	
	/**
	 * Creates an indexer, with a specified index limit.
	 * An index limit prevents overflows of stage objects to different z-index ranges by resetting the timer once the index limit is reached.
	 */
	public ZIndexHelper(int indexLimit)
	{
		this.indexLimit = indexLimit;
	}
	
	public int index = 0;
	
	public void index(StageObject obj)
	{
		obj.setZIndex(obj.getZIndex() + (index++));
		
		if(indexLimit != -1)
		{
			index = index % indexLimit;
		}
	}
}
