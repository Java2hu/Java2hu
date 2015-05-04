package java2hu;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ConcurrentSet<E> implements Set<E>
{
	private ConcurrentMap<E, Boolean> internalMap = new ConcurrentMap<E, Boolean>();

	@Override
	public boolean add(E arg0)
	{
		Boolean result = internalMap.put(arg0, Boolean.TRUE);
		
		return result != null ? result : false;
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0)
	{
		boolean changed = false;
		
		for(E e : arg0)
		{
			if(add(e))
				changed = true;
		}
		
		return changed;
	}

	@Override
	public void clear()
	{
		internalMap.clear();
	}

	@Override
	public boolean contains(Object arg0)
	{
		Boolean result = internalMap.containsKey(arg0);
		
		return result != null ? result : false;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		Boolean result = internalMap.keySet().containsAll(c);
		
		return result != null ? result : false;
	}

	@Override
	public boolean isEmpty()
	{
		Boolean result = internalMap.keySet().isEmpty();
		
		return result != null ? result : false;
	}

	@Override
	public Iterator<E> iterator()
	{
		return internalMap.keySet().iterator();
	}

	@Override
	public boolean remove(Object o)
	{
		Boolean result = internalMap.remove(o);
		
		return result != null ? result : false;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		Boolean result = internalMap.keySet().removeAll(c);
		
		return result != null ? result : false;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		Boolean result = internalMap.keySet().retainAll(c);
		
		return result != null ? result : false;
	}

	@Override
	public int size()
	{
		return internalMap.keySet().size();
	}

	@Override
	public Object[] toArray()
	{
		return internalMap.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return internalMap.keySet().toArray(a);
	}

}
