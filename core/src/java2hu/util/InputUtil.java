package java2hu.util;

import java2hu.object.StageObject;
import java2hu.object.player.Player;
import java2hu.overwrite.J2hObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

/**
 * Util helping with input handling
 */
public class InputUtil extends J2hObject
{
	public static void handleMovementArrowKeys(StageObject object, float speed, float shiftSpeed)
	{
		Input i = Gdx.input;
		
		boolean shift = i.isKeyPressed(Keys.SHIFT_LEFT) || i.isKeyPressed(Keys.SHIFT_RIGHT);
		boolean up = i.isKeyPressed(Keys.UP);
		boolean down = i.isKeyPressed(Keys.DOWN);
		boolean left = i.isKeyPressed(Keys.LEFT);
		boolean right = i.isKeyPressed(Keys.RIGHT);

		if(shift)
			speed = shiftSpeed;
		
		if(up)
		{
			object.setY(object.getY() + speed);
		}

		if(down)
		{
			object.setY(object.getY() - speed);
		}

		if(left)
		{
			object.setX(object.getX() - speed);
		}

		if(right)
		{
			object.setX(object.getX() + speed);
		}

		if(!(up || down || left || right))
		{
			object.setX(object.getX());
			object.setY(object.getY());
		}
	}
	
	public static void handleMovementArrowKeys(Player player, float speed, float shiftSpeed)
	{
		Input i = Gdx.input;
		
		boolean shift = i.isKeyPressed(Keys.SHIFT_LEFT) || i.isKeyPressed(Keys.SHIFT_RIGHT);
		
		if(shift)
		{
			player.showHitbox(true);
			player.setFocused(true);
		}
		else
		{
			player.showHitbox(false);
			player.setFocused(false);
		}
		
		handleMovementArrowKeys((StageObject)player, speed, shiftSpeed);
	}
}
