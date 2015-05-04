package java2hu.allstar.backgrounds;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.Fog;
import java2hu.background.bg3d.controllers.CameraPath;
import java2hu.util.MathUtil;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class BambooForestBG extends Background3D
{
	public Model stageModel;
	
	public ModelInstance frontInstance;
	public ModelInstance middleInstance;
	public ModelInstance backInstance;
	
	public BambooForestBG()
	{
		createStandardCamera();
		createStandardEnvironment();
		
		Fog fog = new Fog(new Color(25f/255f, 0f/255f, 117f/255f, 1f))
		{
			@Override
			public void update(Camera camera)
			{
				bgFog.transform.setTranslation(0, 0, camera.position.z - 13);
			}
		};
		
		setFog(fog);
		
		J2hGame game = Game.getGame();
		
		final float yaw = 270;
		final float pitch = -25;
		
		float height = 4;
		
		for(float i = 10; i < 1000; i += 1)
		{
			float offsetAngle = i / 20f * 360f;
			offsetAngle = MathUtil.normalizeDegree(offsetAngle);
			
			float xOffset = (float) (Math.cos(Math.toRadians(offsetAngle)) * 0.5f);
		
			Vector3 pos = new Vector3(xOffset, 3 + height, -i);
			Vector3 look = new Vector3();

			if(height <= 0)
			{
				look.add((float) Math.cos(Math.toRadians(yaw)), (float) Math.sin(Math.toRadians(pitch)), (float) Math.sin(Math.toRadians(yaw)));
			}
			else
			{
				look.add((float) Math.cos(Math.toRadians(yaw)), -height, (float) Math.sin(Math.toRadians(yaw)));
			}
			
			if(getCameraPather() == null)
			{
				setCameraPather(new CameraPath(getCamera(), pos, look));
			}
			else
			{
				float angle = 0;
				
				if(height <= 0)
				{
					float max = 40f;
					
					angle = i % max;

					if(angle > max / 2f)
						angle = max - angle;
				}
				
				((CameraPath)getCameraPather()).addPath(pos, look, angle, 20);
			}
			
			if(height > 0)
			{
				height -= 0.7f;
			}
		}
		
		if(game.assets.isLoaded("scenes/bambooforest/bambooforest.g3db"))
			game.assets.unload("scenes/bambooforest/bambooforest.g3db");

		game.assets.load("scenes/bambooforest/bambooforest.g3db", Model.class);

		while(!game.assets.update())
		{

		}

		stageModel = game.assets.get("scenes/bambooforest/bambooforest.g3db");

		frontInstance = new ModelInstance(stageModel);
		middleInstance = new ModelInstance(stageModel);
		backInstance = new ModelInstance(stageModel);
	}
	
	float offset = 0; // To loop, moving all 3 stage models forward by this offset to create the illusion of a loop.
	
	@Override
	public void onUpdate(long tick)
	{
		if(getCamera().position.z > 20 + offset)
		{
			offset += 20;
		}
		
		if(getCamera().position.z < 0 + offset)
		{
			offset -= 20;
		}
		
		middleInstance.transform.setTranslation(0, 0, offset);
		frontInstance.transform.setTranslation(0, 0, -20 + offset);
		backInstance.transform.setTranslation(0, 0, 20 + offset);
		
		super.onUpdate(tick);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		modelBatch.render(middleInstance, environment);
		
		modelBatch.render(frontInstance, environment);

		modelBatch.render(backInstance, environment);
	}
}
