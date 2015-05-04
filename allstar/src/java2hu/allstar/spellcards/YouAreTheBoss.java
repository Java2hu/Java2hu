package java2hu.allstar.spellcards;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.object.StageObject;
import java2hu.object.enemy.greater.Boss;
import java2hu.object.player.Player;
import java2hu.plugin.Plugin;
import java2hu.spellcard.Spellcard;
import java2hu.util.InputUtil;

public class YouAreTheBoss extends Spellcard
{
	public YouAreTheBoss(StageObject owner)
	{
		super(owner);
	}

	@Override
	public void tick(int tick)
	{
		final J2hGame game = Game.getGame();
		final Player player = game.getPlayer();
		final Boss boss = (Boss) getOwner();
		
		if(tick == 0)
		{
			game.delete(player);
			
			boss.addEffect(new Plugin<Boss>()
			{
				@Override
				public void update(Boss object, long tick)
				{
					InputUtil.handleMovementArrowKeys(object, 5f, 2.5f);
				}
			});
		}
	}
}
