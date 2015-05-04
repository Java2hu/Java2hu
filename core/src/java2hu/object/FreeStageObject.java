package java2hu.object;


/** 
 * Stage object not bound within the stage (So they can travel outside of the visible stage, and not get deleted unless you kill them.
 */
public abstract class FreeStageObject extends StageObject
{
	public FreeStageObject(float x, float y)
	{
		super(x, y);
	}
	
	public void setX(float x)
	{
		this.lastX = this.x;
		this.x = x;
		this.lastMoveTime = System.currentTimeMillis();
	}
	
	public void setY(float y)
	{
		this.lastY = this.y;
		this.y = y;
		this.lastMoveTime = System.currentTimeMillis();
	}
}
