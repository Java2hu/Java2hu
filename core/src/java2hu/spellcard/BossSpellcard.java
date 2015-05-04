package java2hu.spellcard;

import java2hu.J2hGame;
import java2hu.object.StageObject;

public abstract class BossSpellcard<T extends StageObject> extends Spellcard
{
	private T owner;
	
	public BossSpellcard(T owner)
	{
		super(owner);
		this.owner = owner;
	}

	@Override
	public void tick(int tick)
	{
		tick(tick, game, owner);
	}
	
	public abstract void tick(int tick, final J2hGame game, final T boss);
}
