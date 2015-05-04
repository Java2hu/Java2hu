package java2hu.menu;

import java2hu.J2hGame;
import java2hu.object.StageObject;

public abstract class Menu extends StageObject
{
	private Menu parent;
	
	public Menu(Menu parent)
	{
		super(0, 0);
		this.parent = parent;
		setZIndex(parent == null ? J2hGame.GUI_Z_ORDER : parent.getZIndex() + 1);
	}
	
	@Override
	public float getWidth()
	{
		return 0;
	}

	@Override
	public float getHeight()
	{
		return 0;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		show();
		
		if(parent != null && parent.isOnStage())
			parent.hide();
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		
		hide();
		
		if(parent != null && parent.isOnStage())
			parent.show();
	}
	
	@Override
	public boolean isActiveDuringPause()
	{
		return true;
	}
	
	public Menu getParent()
	{
		return parent;
	}
	
	public void setParent(Menu parent)
	{
		this.parent = parent;
	}
	
	public void hide()
	{
		onHide();
	}
	
	public void show()
	{
		 onShow();
	}
	
	/**
	 * Called when this menu opens another menu
	 */
	public abstract void onHide();
	
	/**
	 * Called when any overlaying menu's get closed or when first opened.
	 */
	public abstract void onShow();
}
