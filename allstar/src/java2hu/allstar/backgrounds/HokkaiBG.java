package java2hu.allstar.backgrounds;

import java.util.ArrayList;
import java.util.Comparator;
import java2hu.Game;
import java2hu.J2hGame;
import java2hu.SmartTimer;
import java2hu.background.bg3d.Background3D;
import java2hu.background.bg3d.controllers.RotationPerspectiveCamera;
import java2hu.object.DrawObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.utils.Array;

public class HokkaiBG extends Background3D
{
	public ModelBatch batch = new ModelBatch(new RenderableSorter()
	{
		@Override
		public void sort(Camera camera, Array<Renderable> renderables)
		{
			renderables.sort(new Comparator<Renderable>()
			{
				@Override
				public int compare(Renderable o1, Renderable o2)
				{
					if(!(o1.userData instanceof Float) || !(o2.userData instanceof Float))
						return 0;
					
					float int1 = (float) o1.userData;
					float int2 = (float) o2.userData;
					
					return Math.round(int1 - int2);
				}
			});
		}
	});
	
	public Model stageModel;
	public Model stageAlienModel;
	
	public ArrayList<ModelInstance> instances = new ArrayList<ModelInstance>();
	
	public HokkaiBG()
	{
		RotationPerspectiveCamera camera = new RotationPerspectiveCamera(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(15f, 4f, 0);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 1000f;
		camera.update();
		
		setCamera(camera);
		setModelBatch(batch);

		Environment environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));

		this.setEnvironment(environment);

		J2hGame game = Game.getGame();

		String normal = "scenes/makai stage/texture512.g3db";
		
		if(game.assets.isLoaded(normal))
			game.assets.unload(normal);

		game.assets.load(normal, Model.class);
		
		String alien = "scenes/makai stage/texture256.g3db";
		
		if(game.assets.isLoaded(alien))
			game.assets.unload(alien);

		game.assets.load(alien, Model.class);

		while(!game.assets.update())
		{

		}

		stageModel = game.assets.get(normal);
		stageAlienModel = game.assets.get(alien);
		
		for(int i = 0; i < 4; i++)
		{
			instances.add(new ModelInstance(stageModel));
		}
		
		for(int i = 0; i < 2; i++)
		{
			instances.add(new ModelInstance(stageAlienModel));
		}

		for(ModelInstance inst : instances)
		for(Material mat : inst.materials)
		{
			mat.set(new ColorAttribute(ColorAttribute.Diffuse, 1f, 1f, 1f, 0.6f));
			mat.set(new BlendingAttribute(true, 0.6f));
		}
		
		onUpdate(0);
	}
	
	@Override
	protected boolean useStandardFadeOut()
	{
		return false;
	}
	
	boolean fadeOut = false;
	boolean fadeOutDelete = false;
	float fadeOutTick = 0;
	
	@Override
	public void fadeOut(boolean remove)
	{
		this.fadeOut = true;
		this.fadeOutDelete = remove;
	}
	
	final SmartTimer timer = new SmartTimer(0.1f, -1f, -0.2f, 1f, 0.8f, 0.005f);
	
	@Override
	public void onUpdate(long tick)
	{
		int i = 0;
		for(ModelInstance inst : instances)
		{
			if(i < 2)
			{
				float mul = (tick - i * 1000) / 2000f % 1;
				
				float y = 1f * i;
				
				inst.transform.setToScaling(10, 1, 10);
				inst.transform.setTranslation(10 + (mul * 40 - 20), y, mul * 8 - 4);
				inst.userData = y;
			}
			else if(i < 4)
			{
				float mul = (tick - i * 500) / 1500f % 1;
				
				float y = 1f * i;
				
				inst.transform.setToScaling(10, 1, 10);
				inst.transform.setTranslation(10 + (mul * 40 - 20), y, -(mul * 8 - 4));
				inst.userData = y;
			}
			else if(i < 8)
			{
				float mul = (tick - i * 1200) / 2000f % 1;
				
				float y = 0f + i * 0.1f;
				
				inst.transform.setToScaling(5, 1, 5);
				inst.transform.setTranslation(10 + (mul * 40 - 20), y, -(mul * 8 - 4));
				inst.userData = y;
			}
			
			i++;
		}
		
		timer.tick();
		
		float intervalSeconds = 1.37f;
		float intervalTicks = intervalSeconds * 60f;
		
		float timer = tick % intervalTicks / intervalTicks;
		
		timer *= Math.PI;
		
		double hb = Math.sin(timer);
		
		hb = hb * 0.2f;
		
		getCamera().position.set(14, (float) (10 + hb), 0);
		
		if(!fadeOut)
		{
			getCamera().direction.set(-0.6f, -1f, 0f);
		}
		else
		{
			float x = Math.min(0, -0.6f + fadeOutTick / 40f);
			float y = Math.min(0, -1f + fadeOutTick / 40f);
			float z = fadeOutTick / 60f;
			
			getCamera().direction.set(x, y, z);
			
			if(fadeOutTick == 40)
			{
				DrawObject obj = new DrawObject()
				{
					Texture black = utils().images().makeDummyTexture(Color.BLACK, 1, 1);
					Sprite sprite = new Sprite(black);
					float alpha = 1f;
					
					{
						sprite.setBounds(0, 0, Game.getGame().getWidth(), Game.getGame().getHeight());
						addDisposable(black);
					}
					
					@Override
					public void onDraw()
					{
						sprite.draw(Game.getGame().batch);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						if(alpha > 0)
						{
							alpha = Math.max(0, alpha - 0.005f);
						}
						else
						{
							Game.getGame().delete(this);
						}
						
						sprite.setAlpha(alpha);
					}
				};
				
				obj.setZIndex(getZIndex() + 1);
				
				Game.getGame().spawn(obj);
			}
			
			if(fadeOutTick > 60)
			{
				Game.getGame().delete(this);
			}
			
			fadeOutTick += 1;
		}
		
		getCamera().update();
		
		super.onUpdate(tick);
	}

	@Override
	public void drawBackground(ModelBatch modelBatch, Environment environment, boolean drawFog)
	{
		for(ModelInstance inst : instances)
		{
			modelBatch.render(inst, environment);
		}
	}
}
