package java2hu.allstar.enemies;

import java2hu.Game;
import java2hu.allstar.AllStarStageScheme;
import java2hu.object.BGMPlayer;
import java2hu.object.enemy.greater.Boss;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class AllStarBoss extends Boss
{
	protected TextureRegion nametag;
	protected Color color = Color.WHITE;
	protected Music bgm;
	
	public AllStarBoss(float maxHealth, TextureRegion nametag, Sprite fullBodySprite, Animation idle, Animation left, Animation right, Animation special, final Music bgm, float x, float y)
	{
		super(fullBodySprite, idle, left, right, special, maxHealth, x, y);
		
		set(nametag, bgm);
	}
	
	public AllStarBoss(float maxHealth, float x, float y)
	{
		super(maxHealth, x, y);
	}
	
	protected void set(TextureRegion nametag, final Music bgm)
	{
		this.nametag = nametag;
		this.bgm = bgm;
		
		addDisposable(nametag);
		
		if(bgm != null)
		addDisposable(new Disposable()
		{
			@Override
			public void dispose()
			{
				bgm.dispose();
			}
		});
	}
	
	public boolean isPC98()
	{
		return false;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		Game.getGame().batch.setColor(getColor());
	}
	
	public TextureRegion getNametag()
	{
		return nametag;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Music getBackgroundMusic()
	{
		return bgm;
	}
	
	public void setBackgroundMusic(Music music)
	{
		this.bgm = music;
	}
	
	private float bgmPosition = 0f;
	
	/**
	 * Bgm start position once the music starts.
	 */
	public float getBgmPosition()
	{
		return bgmPosition;
	}
	
	/**
	 * Bgm start position once the music starts.
	 */
	public void setBgmPosition(float bgmStartSeconds)
	{
		this.bgmPosition = bgmStartSeconds;
	}
	
	public abstract void executeFight(AllStarStageScheme scheme);
	
	@Override
	public void onDelete()
	{
		if(bgmPlayer != null)
			bgmPlayer.fadeOut();
		
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				AllStarBoss.super.onDelete();
			}
		}, 10 * 60);
	}
	
	private BGMPlayer bgmPlayer;
	
	@Override
	public void onSpawn()
	{
		if(getBackgroundMusic() == null)
			return;
		
		bgmPlayer = new BGMPlayer(getBackgroundMusic())
		{
			@Override
			public boolean isPersistant()
			{
				return true;
			}
		};
	
		bgmPlayer.getBgm().play();
		bgmPlayer.getBgm().setPosition(bgmPosition);
		bgmPlayer.fadeIn();

		game.spawn(bgmPlayer);
	}
}
