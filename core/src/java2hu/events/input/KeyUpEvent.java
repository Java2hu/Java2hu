package java2hu.events.input;

import java2hu.events.CancellableEvent;

public class KeyUpEvent extends CancellableEvent
{
	private int key;
	
	public KeyUpEvent(int key)
	{
		this.key = key;
	}
	
	public int getKey()
	{
		return key;
	}
}
