package java2hu.events.input;

import java2hu.events.CancellableEvent;

public class KeyDownEvent extends CancellableEvent
{
	private int key;
	
	public KeyDownEvent(int key)
	{
		this.key = key;
	}
	
	public int getKey()
	{
		return key;
	}
}
