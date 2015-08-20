package java2hu.allstar.enemies;

import java2hu.Game;
import java2hu.allstar.AllStarStageScheme;
import java2hu.object.BGMPlayer;
import java2hu.object.enemy.greater.Boss;
import java2hu.util.AnimationUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class AllStarBoss extends Boss
{
	protected TextureRegion nametag;
	protected Color auraColor = Color.WHITE;
	protected Color bgAuraColor = Color.RED;
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
	
	TextureAtlas atlas = new TextureAtlas();
	
	/**
	 * Use format: String TextureRegion|Animation for bulk usage.
	 * @param objects
	 */
	public void atlas(Object... objects)
	{
		for(int i = 0; i < objects.length; i += 2)
		{
			String id = (String) objects[i];
			Object o = objects[i + 1];
			
			if(o instanceof TextureRegion)
				atlas(id, (TextureRegion)o);
			
			if(o instanceof Animation)
				atlas(id, (Animation)o);
		}
	}
	
	public void atlas(String id, TextureRegion reg)
	{
		AtlasRegion r = atlas.addRegion(id, reg);
		r.flip(reg.isFlipX(), reg.isFlipY());
	}
	
	public void atlas(String id, Animation ani)
	{
		AnimationUtil.toAtlas(atlas, ani, id);
	}
	
	public void saveAtlas(FileHandle handle)
	{
		String nl = "\n";
		
		String s = handle.name() + nl;
		
		s += "format: RGBA8888" + nl;
		s += "filter: MipMapLinearNearest,Nearest" + nl;
		s += "repeat: xy" + nl;
		
		for(AtlasRegion r : atlas.getRegions())
		{
			s += r.name + nl;
			s += "rotate: " + r.rotate + nl;
			
			int regionX = r.getRegionX();
			int regionY = r.getRegionY();
			int packedWidth = r.packedWidth;
			int packedHeight = r.packedHeight;
			int originalWidth = packedWidth;
			int originalHeight = packedHeight;
			
			s += "xy: " + regionX + ", " + regionY + nl;
			s += "size: " + packedWidth + ", " + packedHeight + nl;
			s += "orig: " + originalWidth + ", " + originalHeight + nl;
			s += "offset: " + ((int)r.offsetX) + ", " + ((int)r.offsetY) + nl;
			s += "index: " + r.index + nl;
		}
		
		handle = handle.sibling(handle.nameWithoutExtension() + ".atlas");
		handle = Gdx.files.local(handle.path());
		
		handle.writeString(s, false);
	}
	
	public TextureAtlas loadAtlas(FileHandle handle)
	{
		handle = handle.sibling(handle.nameWithoutExtension() + ".atlas");
		handle = Gdx.files.local(handle.path());
		
		atlas = new TextureAtlas(handle);
		
		return atlas;
	}
	
	public TextureAtlas getAtlas()
	{
		return atlas;
	}
	
	public boolean isPC98()
	{
		return false;
	}
	
	@Override
	public void onUpdate(long tick)
	{
		super.onUpdate(tick);
		
		Game.getGame().batch.setColor(getAuraColor());
	}
	
	public TextureRegion getNametag()
	{
		return nametag;
	}

	public Color getAuraColor()
	{
		return auraColor;
	}

	public void setAuraColor(Color color)
	{
		this.auraColor = color;
	}
	
	public Color getBgAuraColor()
	{
		return bgAuraColor;
	}
	
	public void setBgAuraColor(Color bgAuraColor)
	{
		this.bgAuraColor = bgAuraColor;
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
		
		super.disposeChildren();
		
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
		spawnBGM();
	}
	
	public void spawnBGM()
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
		
		bgmPlayer.getBgm().setVolume(0f);
		
		game.runAsync(new Runnable()
		{
			@Override
			public void run()
			{
				boolean playing = bgmPlayer.getBgm().isPlaying();
				
				bgmPlayer.getBgm().setPosition(bgmPosition);
				
				if(!playing)
					bgmPlayer.getBgm().pause();
				
				bgmPlayer.fadeIn();
			}
		});

		game.spawn(bgmPlayer);
	}
}
