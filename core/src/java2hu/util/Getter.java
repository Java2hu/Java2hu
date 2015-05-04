package java2hu.util;

import java2hu.overwrite.J2hObject;

/**
 * Gets an object of the specified class <T> from the get method.
 * Useful for outside classes that need to get a variable that might change over time.
 * @param <T>
 */
public abstract class Getter<T> extends J2hObject
{
	public abstract T get();
}
