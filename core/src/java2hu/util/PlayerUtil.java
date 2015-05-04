package java2hu.util;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.DrawObject;
import java2hu.object.player.Player;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;

public class PlayerUtil extends J2hObject
{
	public static void deathAnimation(final Player player)
	{
		if(true)
			return;
		
		// Death animation is made by 5 expanding circles that negative whatevers in them in the form of a + (One on each end and in the middle)
		float[] circles = new float[] { 
						0,200,
				-200,0,	0,0,	200,0,
						0,-200,
		};
		
		for(int i = 0; i < circles.length; i += 2)
		{
			final float xPos = player.getX() + circles[i];
			final float yPos = player.getY() + circles[i + 1];
			
			DrawObject circle = new DrawObject()
			{
				float radius = 100;
				float scale = -0.1f;
				float scaleIncrease = 0.01f;
				float innerIncrease = 0.01f;
				Color color = Color.WHITE.cpy();
				Mesh mesh;
				
				@Override
				public void onDraw()
				{
					float[] vertices = MeshUtil.makeCircleVertices(xPos, yPos, 60, radius * scale * innerIncrease, radius * scale, color);
					
					mesh = MeshUtil.makeMesh(mesh, vertices);
					
					Game.getGame().batch.end();
					
					MeshUtil.startShader();
					
					Gdx.gl.glBlendFunc(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ZERO);
					
					MeshUtil.renderMesh(mesh);
					
					MeshUtil.endShader();
					
					Game.getGame().batch.begin();
				}
				
				@Override
				public void onUpdate(long tick)
				{
					super.onUpdate(tick);
					
					if(scaleIncrease < 0.3f)
						scaleIncrease *= 1.1;
					
					innerIncrease *= 1.05f;
					
					scale += scaleIncrease;
					
					if(scale > 30)
						Game.getGame().delete(this);
				}
				
				@Override
				public boolean isPersistant()
				{
					return true;
				}
			};
			
			circle.setZIndex(J2hGame.GUI_Z_ORDER - 1);
			
			Game.getGame().spawn(circle);
		}
	}
}
