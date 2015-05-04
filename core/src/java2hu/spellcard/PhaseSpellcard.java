package java2hu.spellcard;

import java.util.ArrayList;
import java2hu.J2hGame;
import java2hu.object.StageObject;

public class PhaseSpellcard<T extends StageObject> extends BossSpellcard<T>
{
	public PhaseSpellcard(T owner)
	{
		super(owner);
	}
	
	private ArrayList<Phase<T>> phases = new ArrayList<Phase<T>>();
	
	public static abstract class Phase<T>
	{
		public Phase(int phaseInterval)
		{
			this.phaseInterval = phaseInterval;
		}
		
		private int phaseInterval;
		private int tick = 0;
		
		public int getTick()
		{
			return tick;
		}
		
		public int getPhaseInterval()
		{
			return phaseInterval;
		}
	
		public abstract void tick(int tick, J2hGame game, T boss);
	}
	
	public void addPhase(Phase<T> phase)
	{
		phases.add(phase);
	}
	
	private int correctedPhaseInterval(Phase phase)
	{
		return phase.getPhaseInterval();
	}
	
	public int getTotalPhaseTime()
	{
		int time = 0;
		
		for(Phase<T> phase : phases)
		{
			time += correctedPhaseInterval(phase);
		}
		
		return time;
	}
	
	public Phase<T> getPhase(int tick)
	{
		if(phases.size() == 1)
		{
			Phase first = phases.get(0);
			
			if(tick % first.phaseInterval == 0)
				first.tick = 0;
			
			return phases.get(0);
		}
		
		tick = tick % getTotalPhaseTime();
		
		Phase<T> current = null;
		
		int time = 0;
		
		for(Phase<T> phase : phases)
		{
			if(tick < time + correctedPhaseInterval(phase))
			{
				current = phase;
				break;
			}
			
			time += correctedPhaseInterval(phase);
		}
		
		return current;
	}
	
	private Phase<T> lastPhase = null;

	@Override
	public void tick(int tick, J2hGame game, T boss)
	{
		Phase<T> ph = getPhase(tick);
		
		if(lastPhase != null && lastPhase != ph)
		{
			lastPhase.tick = 0;
		}
		
		if(ph != null)
		{
			ph.tick(ph.tick, game, boss);
			ph.tick = ph.tick + 1;
			
			lastPhase = ph;
		}
	}
}
