package java2hu.util;

import java2hu.overwrite.J2hObject;

/**
 * Simple class that passes an object through the set method, and you can then use to modify.
 * Useful for passing objects that might change!
 * @param <T>
 */
public abstract class Setter<T> extends J2hObject
{
	public abstract void set(T t);
}
