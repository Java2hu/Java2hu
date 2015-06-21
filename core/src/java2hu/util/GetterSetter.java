package java2hu.util;

import java2hu.overwrite.J2hObject;

/**
 * Gets an object of the specified class <T1> from the get method.
 * Then has to return an object of class <T2>.
 * Useful for simple getters and setters.
 * @param <T>
 */
public abstract class GetterSetter<T1, T2> extends J2hObject
{
	public abstract T1 get(T2 obj);
}
