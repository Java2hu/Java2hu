package java2hu.gameflow;


public interface SpecialFlowScheme<T extends GameFlowScheme>
{
	public void executeFight(T scheme);
}
