package java2hu.touhou.font;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * The default font I use for this project, the font file it reads from is ../font.ttf
 */
public class TouhouFont extends J2hObject
{
	public static BitmapFont get(int size)
	{
		FreeTypeFontParameter para = new FreeTypeFontParameter();
		para.size = size;
		
		return get(para);
	}
	
	public static BitmapFont get(FreeTypeFontParameter para)
	{
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		BitmapFont font = gen.generateFont(para);
		gen.dispose();
		return font;
	}
}
