package java2hu.util;


/**
 * Gets an object of the specified class <T> from the get method.
 * Useful for outside classes that need to get a variable that might change over time.
 * @param <T>
 */
public interface Getter<T>
{
	public T get();
}
