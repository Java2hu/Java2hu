package java2hu.allstar;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.IPosition;
import java2hu.J2hGame;
import java2hu.J2hGame.ClearType;
import java2hu.allstar.Days.CharacterData;
import java2hu.allstar.Days.EnvironmentType;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.allstar.players.Marisa;
import java2hu.background.BackgroundBossAura;
import java2hu.background.bg3d.Background3D;
import java2hu.gameflow.GameFlowScheme;
import java2hu.gameflow.SpecialFlowScheme;
import java2hu.object.player.Player;
import java2hu.system.SaveableObject;
import java2hu.util.Getter;

public class AllStarStageScheme extends GameFlowScheme
{
	private BackgroundBossAura bossAura = null;
	
	public BackgroundBossAura getBossAura()
	{
		return bossAura;
	}
	
	public void loadBossAura()
	{
		System.out.println("Loading boss aura");
		
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				bossAura = new BackgroundBossAura();
				Game.getGame().spawn(bossAura);
			}
		}, 1);
		
		setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return bossAura == null;
			}
		});
		
		doWait();
	}
	
	private Getter<Player> playerGetter;
	
	public Getter<Player> getPlayerGetter()
	{
		return playerGetter;
	}
	
	public void setPlayerGetter(Getter<Player> playerGetter)
	{
		this.playerGetter = playerGetter;
	}
	
	public void spawnPlayer()
	{
		System.out.println("Loading player");
		
		Game.getGame().clearPlayer();
		
		Game.getGame().addTask(new Runnable()
		{
			@Override
			public void run()
			{
				Player p = null;
				
				if(playerGetter != null)
					p = playerGetter.get();
				else
					p = Marisa.newInstance(Game.getGame().getWidth() / 2f, 100f);
				
				Game.getGame().spawn(p);
			}
		}, 1);
		
		wait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return game.getPlayer() == null;
			}
		});
		
		System.out.println("Done loading player! " + Game.getGame().getPlayer());
	}
	
	public static abstract class SpawnBossTask<T> extends ReturnSyncTask<T>
	{
		private T result;
		
		@Override
		public T getResult()
		{
			return result;
		}

		@Override
		public void run()
		{
			result = get();
			
			setCompleted(true);
		}
		
		public abstract T get();
	}
	
	private int day;
	
	public AllStarStageScheme(int day)
	{
		this.day = day;
	}
	
	@Override
	public GameFlowScheme getRestartInstance()
	{
		return new AllStarStageScheme(day);
	}
	
	@Override
	public void runScheme()
	{
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		final J2hGame game = Game.getGame();
		final AllStarStageScheme scheme = this;
		
		loadBossAura();
		spawnPlayer();
		
		ArrayList<CharacterData> datas = Days.getDay(day);
		
		final SaveableObject<Background3D> last = new SaveableObject<Background3D>();
		final SaveableObject<Boolean> loaded = new SaveableObject<Boolean>(false);
		EnvironmentType lastType = null;
		
		for(final CharacterData data : datas)
		{
			if(last.getObject() == null || lastType == null || data.environment != null && data.environment != lastType)
			{
				final Background3D before = last.getObject();
				
				lastType = data.environment;
				final EnvironmentType currentType = lastType;
				
				Game.getGame().addTaskGame(new Runnable()
				{
					@Override
					public void run()
					{
						Getter<Background3D> getter = data.environment.getSpawnEnvironment();
						
						if(getter != null)
							last.setObject(getter.get());
						
						Background3D current = last.getObject();
						
						if(current != null)
						{
							current.setZIndex(-100);
							current.setFrameBuffer(getBossAura().getBackgroundBuffer());
							game.spawn(current);
						}
						
						loaded.setObject(true);
						
						if(before != null)
						{
							before.setZIndex(-9);
							before.fadeOut(true);
						}
					}
				}, 1);
				
				long startTick = game.getTick();
				
				setWait(new WaitConditioner()
				{
					@Override
					public boolean returnTrueToWait()
					{
						return !loaded.getObject() || before != null && before.isOnStage();
					}
				});
				
				doWait();
				
//				long waitTicks = 100 - (game.getTick() - startTick);
//				
//				if(waitTicks > 0)
//					waitTicks((int) waitTicks);
				
				waitTicks(60);
			}
			
			System.out.println(data.name);
			
			if(data.bossGetter != null)
				executeFight(data.bossGetter);
			else if(data.specialGetter != null)
				executeSpecialFight(data.specialGetter);
			
			reset();
			
			waitTicks(60);
		}
	}
	
	public void executeFight(final Getter<AllStarBoss> create)
	{
		final SaveableObject<AllStarBoss> save = new SaveableObject<AllStarBoss>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				final AllStarBoss boss = create.get();
				
				((AllStarGame)Game.getGame()).setPC98(boss.isPC98());
				
				getBossAura().clearAuras();
				
				getBossAura().setAura(0, new Getter<IPosition>()
				{
					@Override
					public IPosition get()
					{
						return boss;
					}
				});
				
				save.setObject(boss);
			}
		}, 1);
		
		setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return save.getObject() == null;
			}
		});
		
		doWait();

		final AllStarBoss boss = save.getObject();
		
		if(boss.isPC98())
			waitTicks(20);
		
		boss.executeFight(this);
		
		getBossAura().clearAuras();
	}
	
	public void executeSpecialFight(final Getter<SpecialFlowScheme> create)
	{
		final SaveableObject<SpecialFlowScheme> save = new SaveableObject<SpecialFlowScheme>();
		
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				SpecialFlowScheme boss = create.get();
				
				save.setObject(boss);
			}
		}, 1);
		
		setWait(new WaitConditioner()
		{
			@Override
			public boolean returnTrueToWait()
			{
				return save.getObject() == null;
			}
		});
		
		doWait();

		final SpecialFlowScheme boss = save.getObject();
		
		boss.executeFight(this);
		
		getBossAura().clearAuras();
	}
	
	public void reset()
	{
		System.out.println("Reset");
		
		if(getBossAura() != null)
			getBossAura().clearAuras();
		
		Game.getGame().clearSpellcards();
		Game.getGame().clear(ClearType.ALL_OBJECTS);
	}
}
