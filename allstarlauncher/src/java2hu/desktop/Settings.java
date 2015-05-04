package java2hu.desktop;

import java2hu.overwrite.J2hObject;

public class Settings extends J2hObject
{
	public int width = 1280;
	public int height = 960;
	public int viewportWidth = 1280;
	public int viewportHeight = 960;
	public int samples = 4;
	public boolean fullScreen = false;
	public boolean showAtStartup = true;
	public boolean vsync = true;
	public int fps;
}
