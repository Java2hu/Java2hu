package java2hu.touhou.bullet;

import java2hu.object.bullet.ILaserType;
import java2hu.object.bullet.LaserDrawer.LaserAnimation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum ThStationaryLaserType implements ILaserType
{
	WHITE(fix(ThBulletColor.WHITE)), 
	ORANGE(fix(ThBulletColor.ORANGE)),
	YELLOW_LIGHT(fix(ThBulletColor.YELLOW_LIGHT)), 
	YELLOW(fix(ThBulletColor.YELLOW)),
	GREEN_LIGHTER(fix(ThBulletColor.GREEN_LIGHTER)), 
	GREEN_LIGHT(fix(ThBulletColor.GREEN_LIGHT)),
	GREEN(fix(ThBulletColor.GREEN)), 
	CYAN_LIGHT(fix(ThBulletColor.CYAN_LIGHT)),
	CYAN(fix(ThBulletColor.CYAN)), 
	BLUE(fix(ThBulletColor.BLUE)),
	BLUE_DARK(fix(ThBulletColor.BLUE_DARK)), 
	PINK(fix(ThBulletColor.PINK)),
	PURPLE(fix(ThBulletColor.PURPLE)), 
	RED(fix(ThBulletColor.RED)),
	RED_DARK(fix(ThBulletColor.RED_DARK)), 
	BLACK(fix(ThBulletColor.BLACK));
	
	private static class RegionAndColor
	{
		public TextureRegion region;
		public ThBulletColor color;
	}
	
	/**
	 * Completely clueless as to why this is needed, but TextureRegions passed to lasers have a weird offset
	 * which makes them render wrongly, this fixes those regions.
	 */
	private static RegionAndColor fix(ThBulletColor color)
	{
		TextureRegion r = new ThBullet(ThBulletType.LAZER_STATIONARY, color).getAnimation().getKeyFrames()[0];
		
		float offset = 0.01f;
		
		r.setU(r.getU() + offset);
		r.setU2(r.getU2() + offset);
		
		r.setV2(r.getV2() - offset);
		
		RegionAndColor rac = new RegionAndColor();
		
		rac.region = r;
		rac.color = color;
		
		return rac;
	}
	
	private TextureRegion region;
	private ThBulletColor color;
	
	private ThStationaryLaserType(RegionAndColor region)
	{
		this.region = region.region;
		this.color = region.color;
	}
	
	public TextureRegion getTextureRegion()
	{
		return new TextureRegion(region);
	}

	@Override
	public LaserAnimation getAnimation()
	{
		return new LaserAnimation(1f, getTextureRegion());
	}

	@Override
	public float getThickness()
	{
		return 20;
	}

	@Override
	public float getHitboxThickness()
	{
		return 15;
	}

	@Override
	public Color getColor()
	{
		return color.getColor();
	}
}
