package java2hu.events.sound;

import java2hu.events.CancellableEvent;

public class MusicModifierChangeEvent extends CancellableEvent
{
	private float newValue;
	private float fromValue;
	
	public MusicModifierChangeEvent(float fromValue, float newValue)
	{
		this.newValue = newValue;
		this.fromValue = fromValue;
	}
	
	public float getNewValue()
	{
		return newValue;
	}
	
	public float getValue()
	{
		return fromValue;
	}
	
	public void setNewValue(float newValue)
	{
		this.newValue = newValue;
	}
}
