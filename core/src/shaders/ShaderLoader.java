package shaders;

import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderLoader extends J2hObject
{
	static final public ShaderProgram createShader(String vertexFileName, String fragmentFileName)
	{
		String vertexShader = Gdx.files.classpath("shaders/" + vertexFileName).readString();
		String fragmentShader = Gdx.files.classpath("shaders/" + fragmentFileName).readString();
		
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		
		if (!shader.isCompiled())
		{
			System.out.println(shader.getLog());
			
			return null; // Will throw NPE
		}

		return shader;
	}
}
