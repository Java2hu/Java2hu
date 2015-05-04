package java2hu.plugin;

import java2hu.util.Getter;

public abstract class GetterPlugin<T> extends Plugin
{
	private Getter<T> getter;
	
	public GetterPlugin(Getter<T> getter)
	{
		this.getter = getter;
	}
	
	public T get()
	{
		return getter.get();
	}
}
