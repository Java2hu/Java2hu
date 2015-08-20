package java2hu.util;


/**
 * Simple class that passes an object through the set method, and you can then use to modify.
 * Useful for passing objects that might change!
 * @param <T>
 */
public interface Setter<T>
{
	public void set(T t);
}
