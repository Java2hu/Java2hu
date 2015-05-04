package java2hu.events;

public class CancellableEvent extends Event implements ICancellable
{
	private boolean cancelled = false;
	
	public CancellableEvent()
	{
		this(false);
	}
	
	public CancellableEvent(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
}
