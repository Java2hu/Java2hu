package java2hu.object;

import java2hu.Game;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

public abstract class LivingObject extends FreeStageObject
{
	private Polygon hitbox;
	private float maxHealth;
	private float health;
	protected boolean isHealing = false;
	
	public LivingObject(Polygon hitbox, float maxHealth, float x, float y)
	{
		super(x, y);
		this.hitbox = hitbox;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
	}
	
	public void setMaxHealth(float maxHealth)
	{
		this.maxHealth = maxHealth;
	}
	
	public float getMaxHealth()
	{
		return maxHealth;
	}
	
	public void setHealth(float health)
	{
		this.health = Math.max(health, 0);
	}
	
	public float getHealth()
	{
		return health;
	}

	private float damageModifier = 1F;
	
	/**
	 * Damage modifier, useful for armor.
	 * @return
	 */
	public float getDamageModifier()
	{
		return damageModifier;
	}
	
	public void setDamageModifier(float damageModifier)
	{
		this.damageModifier = damageModifier;
	}
	
	public void decreaseHealth(float decrease)
	{
		if(isHealing)
			return;
		
		setHealth(getHealth() - decrease * getDamageModifier());
	}
	
	public void increaseHealth(float increase)
	{
		setHealth(getHealth() + increase);
	}
	
	public Polygon getHitbox()
	{
		return hitbox;
	}
	
	public void setHitbox(Polygon hitbox)
	{
		this.hitbox = hitbox;
	}
	
	public boolean isDead()
	{
		return health <= 0;
	}
	
	public void onHit()
	{
		
	}

	/**
	 * Heal up over 45 ticks, as in ZUN's games.
	 */
	public void healUp()
	{
		setHealth(0.1f);
		healUp(45);
	}
	
	/**
	 * Heal to full over specified ticks, invincible during this time.
	 * @param ticks
	 */
	public void healUp(int ticks)
	{
		float currentHealth = getHealth();
		float maxHealth = getMaxHealth();
		final float increaseTick = (maxHealth - currentHealth) / ticks;
		
		isHealing = true;
		
		for(int i = 1; i <= ticks; i++)
		{
			final boolean last = !(i + 1 <= ticks);
			
			Game.getGame().addTaskGame(new Runnable()
			{
				@Override
				public void run()
				{
					increaseHealth(increaseTick);
					
					if(last)
					{
						isHealing = false;
						setHealth(getMaxHealth());
					}
				}
			}, i);
		}
	}
	
	public boolean isHealing()
	{
		return isHealing;
	}
	
	/**
	 * Mark a living object as healing, so you can make some stuff ignore it's health while it's playing a healing animation. (ex. Circulair Health Bar)
	 * @param isHealing
	 */
	public void setHealing(boolean isHealing)
	{
		this.isHealing = isHealing;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		Rectangle bound = hitbox.getBoundingRectangle();
		
		hitbox.setPosition(getX() - bound.getWidth() / 2, getY() - bound.getHeight() / 2);
	}
	
	@Override
	public void onDelete()
	{
		super.onDelete();
		setHealth(0);
	}
}
