package java2hu.spellcard;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.object.StageObject;
import java2hu.object.bullet.Bullet;
import java2hu.overwrite.J2hObject;

public abstract class Spellcard extends J2hObject
{
	private StageObject owner;
	private int tick = 0;
	
	private Set<Bullet> bullets = Collections.newSetFromMap(new ConcurrentHashMap<Bullet, Boolean>());
	
	public Spellcard(StageObject owner)
	{
		this.owner = owner;
	}
	
	public void addBullet(Bullet bullet)
	{
		bullets.add(bullet);
		Game.getGame().spawn(bullet);
	}
	
	public Set<Bullet> getBullets()
	{
		return bullets;
	}
	
	public J2hGame getGame()
	{
		return Game.getGame();
	}
	
	public StageObject getOwner()
	{
		return owner;
	}
	
	public void run()
	{
		tick(tick++);
	}
	
	public int getSpellcardTick()
	{
		return tick;
	}
	
	public void setSpellcardTick(int tick)
	{
		this.tick = tick;
	}
	
	public void onRemove()
	{
		Game.getGame().clear(ClearType.PLUGINS);
	}
	
	public abstract void tick(int tick);
}
