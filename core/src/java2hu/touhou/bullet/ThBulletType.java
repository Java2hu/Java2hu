package java2hu.touhou.bullet;


public enum ThBulletType
{
	LAZER_STATIONARY,
	POINTER(1f),
	BALL_1, BALL_2, BALL_BIG, BALL_LARGE_HOLLOW, BALL_REFLECTING,
	DISK,
	SWORD,
	BUTTERFLY,
	ORB, ORB_MEDIUM, ORB_LARGE, ORB_SHADE,
	DOT_SMALL_MOON, DOT_SMALL_OUTLINE, DOT_SMALL_FILLED, DOT_MEDIUM,
	STAR, STAR_LARGE,
	BULLET,
	FIREBALL,
	RICE, RICE_LARGE,
	SEAL,
	RAIN,
	KUNAI,
	KNIFE,
	CRYSTAL,
	HEART,
	ARROW,
	NOTE_EIGHT(1f),
	NOTE_QUARTER_REST(1f),
	POWER_SMALL, POWER_MEDIUM, POWER_LARGE, FULL_POWER,
	POINT_SMALL, POINT_MEDIUM,
	GRAZE_SMALL, GRAZE_MEDIUM, GRAZE_LARGE,
	ONEUP_SECTION, ONEUP, BOMB_SECTION, BOMB,
	UNKNOWN_1, UNKNOWN_2, UNKNOWN_3,;
	
	float offsetModifierX = 0.5f;
	float offsetModifierY = 0.5f;
	
	private ThBulletType()
	{
		
	}
	
	private ThBulletType(float offsetModifier)
	{
		this.offsetModifierX = offsetModifier;
		this.offsetModifierY = offsetModifier;
	}

	private ThBulletType(float offsetModifierX, float offsetModifierY)
	{
		this.offsetModifierX = offsetModifierX;
		this.offsetModifierY = offsetModifierY;
	}
	
	public float getOffsetModifierX()
	{
		return offsetModifierX;
	}
	
	public float getOffsetModifierY()
	{
		return offsetModifierY;
	}
}
