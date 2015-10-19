package java2hu.allstar.players;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.Loader;
import java2hu.StartupLoopAnimation;
import java2hu.allstar.AllStarGame;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.object.player.Player;
import java2hu.object.player.PlayerBullet;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.HitboxUtil;
import java2hu.util.ImageSplitter;
import java2hu.util.MathUtil;
import java2hu.util.PlayerUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Marisa extends Player
{
	public static Marisa newInstance(float x, float y)
	{
		Texture texture = Loader.texture(Gdx.files.internal("player/marisa/marisa.png"));
		
		Animation idle = ImageSplitter.getAnimationFromSprite(texture, 95, 64, 6F, 1,2,3,4,5,6,7,8);
		idle.setPlayMode(PlayMode.LOOP);
		
		Animation leftStart = ImageSplitter.getAnimationFromSprite(texture, 96, 64, 6F, 9,10,11,12,13,14);
		Animation leftLoop = ImageSplitter.getAnimationFromSprite(texture, 96, 64, 6F, 15,16);
		Animation left = new StartupLoopAnimation(leftStart, leftLoop, 6f);
		
		Animation rightStart = ImageSplitter.getAnimationFromSprite(texture, 96, 64, 6F, 17,18,19,20,21,22);
		Animation rightLoop = ImageSplitter.getAnimationFromSprite(texture, 96, 64, 6F, 23,24);
		Animation right = new StartupLoopAnimation(rightStart, rightLoop, 6f);
		
		Sprite hakkero = new Sprite(texture, 5 * 64, 3 * 96, 32, 32);
		
		HitboxSprite hitbox = new HitboxSprite(new TextureRegion(texture, 0, 3 * 96, 64, 31));
		hitbox.setHitbox(HitboxUtil.rectangleToPolygon(hitbox.getBoundingRectangle()));
		hitbox.setOriginCenter();
		
		Animation bullet1 = new Animation(1, hitbox);
		
		hitbox = new HitboxSprite(new TextureRegion(texture, 256, 4 * 96, 64, 31));
		hitbox.setHitbox(HitboxUtil.rectangleToPolygon(hitbox.getBoundingRectangle()));
		hitbox.setOriginCenter();

		Animation bullet2 = new Animation(1, hitbox);
	
		texture = Loader.texture(Gdx.files.internal("player/marisa/marisa_98.png"));
		
		Animation idle98 = ImageSplitter.getAnimationFromSprite(texture, 108, 70, 6F, 1);
		Animation left98 = ImageSplitter.getAnimationFromSprite(texture, 108, 70, 6F, 2);
		Animation right98 = ImageSplitter.getAnimationFromSprite(texture, 108, 70, 6F, 3);
		
		Sprite hakkero98 = new Sprite(texture, 210, 0, 32, 38);
		HitboxSprite bullet2_98 = new HitboxSprite(new Sprite(texture, 246, 0, 32, 54));
		bullet2_98.setHitbox(hitbox.getHitbox());
		Animation bullet2_98_ani = new Animation(1f, bullet2_98);

		float hitboxSize = 1F;
		
		return new Marisa(hakkero, hakkero98, bullet1, bullet1, bullet2, bullet2_98_ani, idle, idle98, left, left98, right, right98, hitboxSize, x, y);
	}
	
	private ArrayList<FreeStageObject> hakkeros = new ArrayList<FreeStageObject>();
	private Sprite hakkeroSprite;
	
	private Sprite normalHakkero;
	private Sprite pc98Hakkero;
	
	private Animation bullet1;
	private Animation bullet2;
	
	private Animation normalBullet1;
	private Animation normalBullet2;
	
	private Animation pc98Bullet1;
	private Animation pc98Bullet2;
	
	private Animation normalIdle;
	private Animation normalLeft;
	private Animation normalRight;
	
	private Animation pc98Idle;
	private Animation pc98Left;
	private Animation pc98Right;
	
	private Marisa(final Sprite hakkero, Sprite hakkero98, Animation bullet1, Animation bullet1_98, Animation bullet2, Animation bullet2_98, Animation idle, Animation idle98, Animation left, Animation left98, Animation right, Animation right98, float hitboxSize, float x, float y)
	{
		super(idle, left, right, hitboxSize, x, y);
		
		hakkeroSprite = hakkero;
		normalHakkero = hakkero;
		pc98Hakkero = hakkero98;
		
		this.bullet1 = bullet1;
		this.bullet2 = bullet2;
		
		this.normalBullet1 = bullet1;
		this.normalBullet2 = bullet2;
		
		this.pc98Bullet1 = bullet1_98;
		this.pc98Bullet2 = bullet2_98;
		
		this.normalIdle = idle;
		this.normalLeft = left;
		this.normalRight = right;
		
		this.pc98Idle = idle98;
		this.pc98Left = left98;
		this.pc98Right = right98;
		
		final Marisa marisa = this;
		
		for(int i = 0; i < 360; i += 180)
		{
			final float rotation = i;
			
			final FreeStageObject hakkeroObject = new FreeStageObject(-100, -100)
			{
				float rotate = 0;
				
				float cos = 0;
				
				@Override
				public void onDraw()
				{
					hakkeroSprite.setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
					
					float scale = (float) (1.1f - MathUtil.getDifference(0, cos) / 50f * 0.2f);

					hakkeroSprite.setScale(scale);
					hakkeroSprite.draw(Game.getGame().batch);
				}

				@Override
				public float getWidth()
				{
					return hakkeroSprite.getWidth();
				}

				@Override
				public float getHeight()
				{
					return hakkeroSprite.getHeight();
				}

				@Override
				public void onUpdate(long tick)
				{
					if(!marisa.isOnStage())
						game.delete(this);
					
					if(((AllStarGame)Game.getGame()).isPC98() && tick % 5 == 0 || !((AllStarGame)Game.getGame()).isPC98())
					{
						cos = (float) (Math.cos(Math.toRadians(rotation + rotate)) * (marisa.isFocused() ? 50f : 80f));
						setX(marisa.getX() + cos);
						setY(marisa.getY() - 20 + (float) (Math.sin(Math.toRadians(rotation + rotate)) * 10));

						float rotNorm = (float) MathUtil.normalizeDegree(rotation + rotate);

						if(rotNorm > 0 && rotNorm <= 180)
						{
							setZIndex(marisa.getZIndex() - 2);
						}
						else
						{
							setZIndex(marisa.getZIndex() + 2);
						}
					}
					
					rotate += 5f;
					rotate = (float) MathUtil.normalizeDegree(rotate);

				}
			};
			
			hakkeros.add(hakkeroObject);
			Game.getGame().spawn(hakkeroObject);
		}
	}
	
	public void setToPc98()
	{
		this.idle = pc98Idle;
		this.left = pc98Left;
		this.right = pc98Right;
		
		this.hakkeroSprite = pc98Hakkero;
		this.bullet1 = pc98Bullet1;
		this.bullet2 = pc98Bullet2;
	}
	
	public void setToNormal()
	{
		this.idle = normalIdle;
		this.left = normalLeft;
		this.right = normalRight;
		
		this.hakkeroSprite = normalHakkero;
		this.bullet1 = normalBullet1;
		this.bullet2 = normalBullet2;
	}
	
	@Override
	public void onDraw()
	{
		super.onDraw();
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		if(((AllStarGame)Game.getGame()).isPC98() && idle != pc98Idle)
		{
			setToPc98();
		}
		else if(!((AllStarGame)Game.getGame()).isPC98() && idle == pc98Idle)
		{
			setToNormal();
		}
		
		for(FreeStageObject obj : hakkeros)
		{
			if(!obj.isOnStage())
			{
				Game.getGame().spawn(obj);
			}
		}
	}
	
	@Override
	public void onDeath(StageObject killer)
	{
		super.onDeath(killer);
		((AllStarGame)Game.getGame()).deaths++;
		TouhouSounds.Player.DEATH_1.play(0.5f);
		TouhouSounds.Player.DEATH_2.play(0.5f);
		PlayerUtil.deathAnimation(this);
	};
	
	@Override
	public void shoot()
	{
		if(Game.getGame().getTick() % 5 == 0)
			TouhouSounds.Player.ATTACK_6.play(0.4F);
		
		if(Game.getGame().getTick() % 6 == 0)
		{
			for(FreeStageObject obj : hakkeros)
			{
				PlayerBullet shot = new PlayerBullet(AnimationUtil.copyAnimation(bullet1), obj.getX(), obj.getY() - 5);

				shot.getCurrentSprite().setAlpha(0.5f);
				shot.setRotationDeg(90);
				shot.setDamage(0.3f);
				
				shot.setVelocityYTick(-20f);
				shot.useSpawnAnimation(false);

				Game.getGame().spawn(shot);
			}
		}
		
		if(Game.getGame().getTick() % 20 == 0)
		{
			if(!isFocused())
			{
				TouhouSounds.Player.ATTACK_3.play(1f, 1.2f, 1f);
			}
			else
				TouhouSounds.Player.ATTACK_3.play(1F);
			
			for(int i = isFocused() ? 0 : -10; isFocused() ? i == 0 : i <= 10; i += 10)
			{
				PlayerBullet shot = new PlayerBullet(AnimationUtil.copyAnimation(bullet2), getX() + i, getY() + 10);
				
				final boolean pc98 = idle == pc98Idle;
				
				shot.setDamage(!isFocused() ? 1.8f/3f : 1.4f);
				
				if(!pc98)
					shot.setRotationDeg(90);
				
				if(!isFocused())
				{
					shot.getCurrentSprite().setScale(pc98 ? 0.2f : 1f, pc98 ? 1f : 0.4f);
				}

				shot.getCurrentSprite().setAlpha(0.5f);
				
				shot.setVelocityYTick(isFocused() ? -10f : -30f);
				shot.useSpawnAnimation(false);

				Game.getGame().spawn(shot);
			}
		}
	}
}
