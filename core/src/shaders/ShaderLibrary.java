package shaders;

import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderLibrary extends J2hObject
{
	public static ShaderPath STANDARD = new ShaderPath("standard.vertex.glsl", "standard.fragment.glsl");
	public static ShaderPath GLOW = new ShaderPath("standard.vertex.glsl", "glow.fragment.glsl");
	public static ShaderPath RELIEF = new ShaderPath("standard.vertex.glsl", "relief.fragment.glsl");
	public static ShaderPath PIXELATE = new ShaderPath("standard.vertex.glsl", "pixelate.fragment.glsl");
	public static ShaderPath WATER = new ShaderPath("standard.vertex.glsl", "water.fragment.glsl");
	public static ShaderPath WARP = new ShaderPath("standard.vertex.glsl", "warp.fragment.glsl");
	
	/**
	 * Special shader that combines a fish eye effect and a water effect.
	 * Special usage is needed, see @BackgroundBossAura
	 */
	public static ShaderPath BOSS_BACKGROUND = new ShaderPath("standard.vertex.glsl", "fisheye.fragment.glsl");
	
	public static class ShaderPath extends J2hObject
	{
		ShaderProgram program = null;
		String vertex;
		String fragment;
		
		public ShaderPath(String vertex, String fragment)
		{
			this.vertex = vertex;
			this.fragment = fragment;
		}
		
		public ShaderProgram getProgram()
		{
			if(program == null)
				program = ShaderLoader.createShader(vertex, fragment);
			
			return program;
		}
	}
}
