package java2hu.util;


/**
 * Gets an object of the specified class <T1> from the get method.
 * Then has to return an object of class <T2>.
 * Useful for simple getters and setters.
 * @param <T>
 */
public interface GetterSetter<T1, T2>
{
	public T1 get(T2 obj);
}
