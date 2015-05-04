package java2hu.background.bg3d;

import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public abstract class ModelObject extends J2hObject
{
	public abstract void draw(Camera camera, ModelBatch batch, Environment env);
	public abstract void update(Camera camera);
}
