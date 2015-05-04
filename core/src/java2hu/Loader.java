package java2hu;

import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

/**
 * Loader class, used for loading stuff with specific settings.
 * ie. load a texture with specific min and max filters easily!
 */
public class Loader extends J2hObject
{
	public static class TextureLoadSettings
	{
		public TextureLoadSettings(TextureFilter min, TextureFilter max)
		{
			this(min, max, null, null, false);
		}
		
		public TextureLoadSettings(TextureFilter min, TextureFilter max, boolean useMipMap)
		{
			this(min, max, null, null, useMipMap);
		}
		
		public TextureLoadSettings(TextureFilter min, TextureFilter max, TextureWrap u, TextureWrap v, boolean useMipMap)
		{
			this.min = min != null ? min : standard.min;
			this.max = max != null ? max : standard.max;
			this.u = u != null ? u : standard.u;
			this.v = v != null ? v : standard.v;

			this.useMipMap = useMipMap;
		}
		
		private TextureLoadSettings()
		{
			
		}
		
		public boolean useMipMap = false;
		
		public TextureFilter min;
		public TextureFilter max;
		
		public TextureWrap u;
		public TextureWrap v;
		
		private static TextureLoadSettings standard = createStandard();
		
		/**
		 * Returns a TextureLoadSettings with:
		 * Mipmap on,
		 * Min = MipMapLinearNearest,
		 * Max = Nearest,
		 * U and V = Repeat,
		 * @return
		 */
		public static TextureLoadSettings createStandard()
		{
			TextureLoadSettings standard = new TextureLoadSettings();
			
			standard.useMipMap = true;
			standard.min = TextureFilter.MipMapLinearNearest;
			standard.max = TextureFilter.MipMapLinearNearest;
			
			standard.u = TextureWrap.Repeat;
			standard.v = TextureWrap.Repeat;
			
			return standard;
		}
		
		public void standard()
		{
			cloneFrom(standard);
		}
		
		private void cloneFrom(TextureLoadSettings s)
		{	
			this.useMipMap = s.useMipMap;
			
			this.min = s.min;
			this.max = s.max;
			
			this.u = s.u;
			this.v = s.v;
		}
		
		public static void setStandard(TextureLoadSettings settings)
		{
			if(settings != null)
				standard = settings;
		}
	}
	
	private static TextureLoadSettings settings = TextureLoadSettings.createStandard();
	
	/**
	 * Used for chaining texture settings, be sure to call .standard() after you're done.
	 * @return
	 */
	public static TextureLoadSettings textureSettings()
	{
		return settings;
	}
	
	public static Texture texture(FileHandle handle)
	{
		return texture(handle, textureSettings());
	}
	
	public static Texture texture(FileHandle handle, TextureLoadSettings settings)
	{
		Texture tex = new Texture(handle, settings.useMipMap);
		tex.setFilter(settings.min, settings.max);
		tex.setWrap(settings.u, settings.v);
		
		return tex;
	}
}
