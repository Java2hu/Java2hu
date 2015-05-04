package java2hu;

import java.util.Collection;
import java.util.Iterator;
import java2hu.object.StageObject;

/**
 * This list will take care of concurrent changes during an iteration.
 * Only supports 1 main iteration.
 */
public class RenderSet<E> extends ConcurrentSet<E>
{
	private boolean iterating;
	
	public void startReading()
	{
		iterating = true;
	}
	
	public void endReading()
	{
		iterating = false;
		
		if(cleared)
		{
			cleared = false;
			super.clear();
		}
		else
		{
			for(Object obj : removedObjects)
			{
				if(obj instanceof StageObject)
				{
					String name = ((StageObject) obj).getName();
				}
				
				remove(obj);
			}
		}
		
		removedObjects.clear();
		
	}
	
	private ConcurrentSet removedObjects = new ConcurrentSet();
	
	/**
	 * Returns true if an object was marked for removal during iteration.
	 * @param object
	 * @return
	 */
	public boolean willBeRemoved(E object)
	{
		return removedObjects.contains(object);
	}
	
	@Override
	public boolean remove(Object o)
	{
		if(iterating)
		{
			removedObjects.add(o);
			return true;
		}
		
		if(o == null)
			return false;
		
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean changed = false;
		
		for(Object obj : c)
		{
			if(remove(obj))
				changed = true;
		}
		
		return changed;
	}
	
	private boolean cleared = false;
	
	public boolean isCleared()
	{
		return cleared;
	}
	
	public boolean isIterating()
	{
		return iterating;
	}
	
	@Override
	public void clear()
	{
		if(iterating)
			cleared = true;
		else
		{
			super.clear();
		}
	}
	
	public Iterator<E> renderIterator()
	{
		Iterator<E> iterator = new Iterator<E>()
		{
			private Iterator<E> underlying = RenderSet.this.iterator();
			
			{
				loadNext();
			}

			@Override
			public boolean hasNext()
			{
				if(isCleared() || next == null)
					return false;
				
				return true;
			}
			
			private E current;
			private E next;

			@Override
			public E next()
			{
				if(next != null)
				{
					current = next;
					
					loadNext();
					
					return current;
				}
				
				return null;
			}
			
			private void loadNext()
			{
				if(isCleared())
				{
					next = null;
					return;
				}
				
				if(!underlying.hasNext())
				{
					next = null;
					return;
				}
				
				E next = underlying.next();
				
				boolean removed = willBeRemoved(next);
				
				if(removed)
				{
					loadNext();
					return;
				}
				
				this.next = next;
			}
			
			@Override
			public void remove()
			{
				removedObjects.add(current);
			}
		};
		
		return iterator;
	}
}
