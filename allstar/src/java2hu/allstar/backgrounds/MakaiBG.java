package java2hu.allstar.backgrounds;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

public class MakaiBG extends Background3D
{
	public Model stageModel;
	
	public ModelInstance instance;
	
	public MakaiBG()
	{
		RotationPerspectiveCamera camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(15f, 4f, 0);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 100f;
		camera.update();
		
		setCamera(camera);

		Environment environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));

		this.setEnvironment(environment);

		J2hGame game = Game.getGame();

		if(game.assets.isLoaded("scenes/makai/makai.g3db"))
			game.assets.unload("scenes/makai/makai.g3db");

		game.assets.load("scenes/makai/makai.g3db", Model.class);

		while(!game.assets.update())
		{

		}

		stageModel = game.assets.get("scenes/makai/makai.g3db");

		instance = new ModelInstance(stageModel);
		
		onUpdate(0);
	}

	@Override
	public void onUpdate(long tick)
	{
		int deviation = 4;
		int dividant = deviation * 4;
		
		float timer = tick / 100f % dividant;
		
		if(timer > dividant / 2f)
			timer = dividant - timer;
		
		getCamera().position.set(15f, 4f, timer - deviation);
		
		final float yaw = 180 + (timer - deviation) * 3;
		final float pitch = -8;
		
		Vector3 lookVector = new Vector3((float) Math.cos(Math.toRadians(yaw)), (float) Math.sin(Math.toRadians(pitch)), (float) Math.sin(Math.toRadians(yaw)));
		
		getCamera().direction.set(lookVector);
		getCamera().update();
		super.onUpdate(tick);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		modelBatch.render(instance, environment);
	}
}
