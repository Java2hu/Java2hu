package java2hu.allstar.spellcards;

import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.object.StageObject;
import java2hu.object.bullet.Spark;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.spellcard.Spellcard;
import java2hu.util.HitboxUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SleepyYuukaCard extends Spellcard
{
	public SleepyYuukaCard(StageObject owner)
	{
		super(owner);
	}

	@Override
	public void tick(int tick)
	{
		final J2hGame game = Game.getGame();
		final Player player = game.getPlayer();
		final Boss boss = (Boss) getOwner();
		
		if(tick % 500 == 0)
		{
			Texture sparkTexture = Loader.texture(Gdx.files.internal("enemy/sleepyyuuka/laser.png"));
			sparkTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Nearest);
			HitboxSprite sprite = new HitboxSprite(new Sprite(sparkTexture));
			sprite.setScale(4f, 4f);
			sprite.setHitbox(HitboxUtil.makeHitboxFromSprite(new TextureRegion(sparkTexture), HitboxUtil.StandardBulletSpecification.get()));
			
			sprite.setOrigin(0, sprite.getHeight() / 2f);
			sprite.getHitbox().setOrigin(0, sprite.getHitbox().getBoundingRectangle().height / 2f);
			
			sprite.getHitbox().rotate(180f);
			sprite.setHitboxScaleOffsetModifierX(1f);
			sprite.setHitboxScaleOffsetModifierY(0.7f);
			
			Animation ani = new Animation(1f, sprite);
			
			Spark spark = new Spark(ani, boss.getX(), boss.getY())
			{
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(getTicksAlive() > 400)
						Game.getGame().delete(this);
					
					setRotationDeg(getTicksAlive());
				}
			};
			
			Game.getGame().spawn(spark);
		}
	}
}
