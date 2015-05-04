package java2hu.touhou.sounds;

import java2hu.StartLoader.LoadOnStartup;
import java2hu.overwrite.J2hObject;
import java2hu.overwrite.J2hSound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * All Touhou Sounds used by ZUN
 * They are all loaded into memory and can be easily accessed because these classes are completely static.
 * All names should be pretty self explanatory, but else you'll have to listen to them to get the idea (hard to explain music)
 */
public class TouhouSounds extends J2hObject
{
	public static class Camera extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/camera/";
		
		public static Sound CLICK = loadSound(soundPath + "click.mp3");
		public static Sound FOCUS = loadSound(soundPath + "focus.mp3");
		public static Sound READY = loadSound(soundPath + "ready.mp3");
		public static Sound ROTATE_1 = loadSound(soundPath + "rotate1.mp3");
		public static Sound ROTATE_2 = loadSound(soundPath + "rotate2.mp3");
		public static Sound TAKE_PICTURE = loadSound(soundPath + "take picture.mp3");
		public static Sound WOOD_HIT = loadSound(soundPath + "wood hit.mp3");
		public static Sound ZOOM = loadSound(soundPath + "zoom.mp3");
	}
	
	public static class Enemy extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/enemy/";
		
		public static Sound ACTIVATE_1 = loadSound(soundPath + "activate1.mp3");
		public static Sound ACTIVATE_2 = loadSound(soundPath + "activate2.mp3");
		public static Sound ACTIVATE_3 = loadSound(soundPath + "activate3.mp3");
		public static Sound BREAK_1 = loadSound(soundPath + "break1.mp3");
		public static Sound BREAK_2 = loadSound(soundPath + "break2.mp3");
		public static Sound BULLET_1 = loadSound(soundPath + "bullet1.mp3");
		public static Sound BULLET_2 = loadSound(soundPath + "bullet2.mp3");
		public static Sound BULLET_3 = loadSound(soundPath + "bullet3.mp3");
		public static Sound BULLET_4 = loadSound(soundPath + "bullet4.mp3");
		public static Sound CAT = loadSound(soundPath + "cat.mp3");
		public static Sound CHARGE = loadSound(soundPath + "charge1.mp3");
		public static Sound EXPLOSION_1 = loadSound(soundPath + "explosion1.mp3");
		public static Sound EXPLOSION_2 = loadSound(soundPath + "explosion2.mp3");
		public static Sound EXPLOSION_3 = loadSound(soundPath + "explosion3.mp3");
		public static Sound HEAL = loadSound(soundPath + "heal.mp3");
		public static Sound HUM_1 = loadSound(soundPath + "hum1.mp3");
		public static Sound HUM_2 = loadSound(soundPath + "hum2.mp3");
		public static Sound ICE_1 = loadSound(soundPath + "ice1.mp3");
		public static Sound ICE_2 = loadSound(soundPath + "ice2.mp3");
		public static Sound LAZER_1 = loadSound(soundPath + "lazer1.mp3");
		public static Sound LAZER_2 = loadSound(soundPath + "lazer2.mp3");
		public static Sound LAZER_3 = loadSound(soundPath + "lazer3.mp3");
		public static Sound NOISE = loadSound(soundPath + "noise.mp3");
		public static Sound RELEASE_1 = loadSound(soundPath + "release1.mp3");
		public static Sound RELEASE_2 = loadSound(soundPath + "release2.mp3");
		public static Sound RELEASE_3 = loadSound(soundPath + "release3.mp3");
		public static Sound SLASH = loadSound(soundPath + "slash.mp3");
		public static Sound SPAWN = loadSound(soundPath + "spawn.mp3");
	}
	
	public static class Hud extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/hud/";
		
		public static Sound CANCEL = loadSound(soundPath + "cancel.mp3");
		public static Sound INVALID = loadSound(soundPath + "invalid.mp3");
		public static Sound OK = loadSound(soundPath + "ok.mp3");
		public static Sound PAUSE = loadSound(soundPath + "pause.mp3");
		public static Sound SWITCH = loadSound(soundPath + "switch.mp3");
	}
	
	public static class Notes extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/notes/";
		
		public static Sound NOTE_1 = loadSound(soundPath + "note1.mp3");
		public static Sound NOTE_2 = loadSound(soundPath + "note2.mp3");
		public static Sound NOTE_3 = loadSound(soundPath + "note3.mp3");
		public static Sound NOTE_4 = loadSound(soundPath + "note4.mp3");
		public static Sound NOTE_5 = loadSound(soundPath + "note5.mp3");
	}
	
	public static class Player extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/player/";
		
		public static Sound ATTACK_1 = loadSound(soundPath + "attack1.mp3");
		public static Sound ATTACK_2 = loadSound(soundPath + "attack2.mp3");
		public static Sound ATTACK_3 = loadSound(soundPath + "attack3.mp3");
		public static Sound ATTACK_4 = loadSound(soundPath + "attack4.mp3");
		public static Sound ATTACK_5 = loadSound(soundPath + "attack5.mp3");
		public static Sound ATTACK_6 = loadSound(soundPath + "attack6.mp3");
		public static Sound DAMAGE_1 = loadSound(soundPath + "damage1.mp3");
		public static Sound DAMAGE_2 = loadSound(soundPath + "damage2.mp3");
		public static Sound DEATH_1 = loadSound(soundPath + "death1.mp3");
		public static Sound DEATH_2 = loadSound(soundPath + "death2.mp3");
		public static Sound EXTEND = loadSound(soundPath + "extend.mp3");
		public static Sound GROW = loadSound(soundPath + "grow.mp3");
		public static Sound GRAZE = loadSound(soundPath + "graze.mp3");
		public static Sound HITBOX_HIDE = loadSound(soundPath + "hitbox hide.mp3");
		public static Sound HITBOX_SHOW = loadSound(soundPath + "hitbox show.mp3");
		public static Sound ITEM_1 = loadSound(soundPath + "item1.mp3");
		public static Sound ITEM_2 = loadSound(soundPath + "item2.mp3");
		public static Sound LAST_LIFE = loadSound(soundPath + "last life.mp3");
		public static Sound NO_DAMAGE = loadSound(soundPath + "no damage.mp3");
		public static Sound POWER_1 = loadSound(soundPath + "power1.mp3");
		public static Sound POWER_2 = loadSound(soundPath + "power2.mp3");
		public static Sound POWER_UP = loadSound(soundPath + "powerup.mp3");
		public static Sound SPARK = loadSound(soundPath + "spark.mp3");
	}
	
	public static class Stage extends J2hObject
	{
		@LoadOnStartup
		public static void load() { } // Just here to statically load all the sounds.
		
		private static String soundPath = "sounds/stage/";
		
		public static Sound BONUS_1 = loadSound(soundPath + "bonus1.mp3");
		public static Sound BONUS_2 = loadSound(soundPath + "bonus2.mp3");
		public static Sound BONUS_3 = loadSound(soundPath + "bonus3.mp3");
		public static Sound BORDER = loadSound(soundPath + "border.mp3");
		public static Sound BUBBLE = loadSound(soundPath + "bubble.mp3");
		public static Sound CARD_GET = loadSound(soundPath + "cardget.mp3");
		public static Sound PING_1 = loadSound(soundPath + "ping1.mp3");
		public static Sound PING_2 = loadSound(soundPath + "ping2.mp3");
		public static Sound TIME_STOP = loadSound(soundPath + "time stop.mp3");
		public static Sound TIMEOUT = loadSound(soundPath + "timeout.mp3");
		public static Sound TIMING_OUT_1 = loadSound(soundPath + "timing out1.mp3");
		public static Sound TIMING_OUT_2 = loadSound(soundPath + "timing out2.mp3");
		public static Sound UFO_ALERT = loadSound(soundPath + "ufo alert.mp3");
		public static Sound UFO_CHANGE = loadSound(soundPath + "ufo change.mp3");
		public static Sound UFO_RELEASE = loadSound(soundPath + "ufo release.mp3");
		public static Sound UFO_SPAWN = loadSound(soundPath + "ufo spawn.mp3");
		public static Sound WARNING_1 = loadSound(soundPath + "warning1.mp3");
		public static Sound WARNING_2 = loadSound(soundPath + "warning2.mp3");
		public static Sound WARP_LEFT_TO_RIGHT = loadSound(soundPath + "warp left to right.mp3");
		public static Sound WARP_RIGHT_TO_LEFT = loadSound(soundPath + "warp right to left.mp3");
		public static Sound WATER = loadSound(soundPath + "water.mp3");
		public static Sound WOLF = loadSound(soundPath + "wolf.mp3");
	}
	
	public static class LoadOnUseSound implements Sound
	{
		Sound sound = null;
		FileHandle handle = null;
		
		public LoadOnUseSound(String internal)
		{
			handle = Gdx.files.internal(internal);
		}
		
		public Sound getSound()
		{
			if(sound != null)
				return sound;
			
			if(handle != null)
			{
				sound = new J2hSound(Gdx.audio.newSound(handle));
			}
			
			return sound;
		}
		
		@Override
		public long play()
		{
			return getSound().play();
		}

		@Override
		public long play(float volume)
		{
			return getSound().play(volume);
		}

		@Override
		public long play(float volume, float pitch, float pan)
		{
			return getSound().play(volume, pitch, pan);
		}

		@Override
		public long loop()
		{
			return getSound().loop();
		}

		@Override
		public long loop(float volume)
		{
			return getSound().loop(volume);
		}

		@Override
		public long loop(float volume, float pitch, float pan)
		{
			return getSound().loop(volume, pitch, pan);
		}

		@Override
		public void stop()
		{
			getSound().stop();
		}

		@Override
		public void pause()
		{
			getSound().pause();
		}

		@Override
		public void resume()
		{
			getSound().resume();
		}

		@Override
		public void dispose()
		{
			getSound().dispose();
		}

		@Override
		public void stop(long soundId)
		{
			getSound().stop(soundId);
		}

		@Override
		public void pause(long soundId)
		{
			getSound().pause();
		}

		@Override
		public void resume(long soundId)
		{
			getSound().resume(soundId);
		}

		@Override
		public void setLooping(long soundId, boolean looping)
		{
			getSound().setLooping(soundId, looping);
		}

		@Override
		public void setPitch(long soundId, float pitch)
		{
			getSound().setPitch(soundId, pitch);
		}

		@Override
		public void setVolume(long soundId, float volume)
		{
			getSound().setVolume(soundId, volume);
		}

		@Override
		public void setPan(long soundId, float pan, float volume)
		{
			getSound().setPan(soundId, pan, volume);
		}

		@Override
		public void setPriority(long soundId, int priority)
		{
			getSound().setPriority(soundId, priority);
		}
		
	}

	private static Sound loadSound(final String soundLoc)
	{
		System.out.println("[TouhouSounds] Loading sound: " + soundLoc);
		
		return new LoadOnUseSound(soundLoc);
	}
}
