package java2hu.allstar.util;

import java2hu.Game;
import java2hu.J2hGame;
import java2hu.Loader;
import java2hu.allstar.enemies.AllStarBoss;
import java2hu.object.DrawObject;
import java2hu.object.StageObject;
import java2hu.object.enemy.greater.Boss;
import java2hu.overwrite.J2hObject;
import java2hu.system.SaveableObject;
import java2hu.touhou.font.TouhouFont;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.AnimationUtil;
import java2hu.util.Getter;
import java2hu.util.MathUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AllStarUtil extends J2hObject
{
	public static Sprite bg;
	
	public static void introduce(final AllStarBoss boss)
	{
		introduce(boss, 0, Game.getGame().getHeight());
	}
	
	public static void introduce(final AllStarBoss... bosses)
	{
		float perBossY = 170;
		
		int i = 0;
		
		for(AllStarBoss boss : bosses)
		{
			introduce(boss, 0 + i * perBossY, Game.getGame().getHeight() - (bosses.length - 1 - i) * perBossY);
			i++;
		}
	}

	public static void introduce(final AllStarBoss boss, final float yStart, final float yEnd)
	{
		Game.getGame().addTaskGame(new Runnable()
		{
			@Override
			public void run()
			{
				Game.getGame().assets.load("introduction.png", Texture.class);
				Game.getGame().assets.finishLoading();
				bg = new Sprite((Texture)Game.getGame().assets.get("introduction.png"));

				final Sprite nametag = new Sprite(boss.getNametag());

				final SaveableObject<Float> yPos = new SaveableObject<>();
				yPos.setObject(yEnd);

				StageObject object = new StageObject(0, 0)
				{
					{
						this.setZIndex(J2hGame.GUI_Z_ORDER - 4);
					}

					@Override
					public void onUpdate(long tick)
					{
						if(yPos.getObject() > 0)
							yPos.setObject(Math.max(0, yPos.getObject() - 50f));

						if(getTicksAlive() > 80)
						{
							yPos.setObject(yPos.getObject() - 50f);
						}

						if(bg != null)
						{
							bg.setSize(Game.getGame().getWidth(), yEnd - yStart + yPos.getObject());

							if(yPos.getObject() < -bg.getHeight())
								Game.getGame().delete(this);
						}
					}

					@Override
					public float getWidth()
					{
						return 0;
					}

					@Override
					public float getHeight()
					{
						return 0;
					}

					@Override
					public void onDraw()
					{
						J2hGame game = Game.getGame();

						if(bg == null)
							return;

						bg.setPosition(0, yStart);
						bg.setColor(boss.getColor());
						bg.setAlpha(1f);
						bg.draw(game.batch);
					}
				};

				Game.getGame().spawn(object);

				object = new StageObject(0, 0)
				{
					{
						this.setZIndex(J2hGame.GUI_Z_ORDER - 3);
					}

					@Override
					public void onUpdate(long tick)
					{
						if(getTicksAlive() > 80)
						{
							Game.getGame().delete(this);
						}
					}

					@Override
					public float getWidth()
					{
						return 0;
					}

					@Override
					public float getHeight()
					{
						return 0;
					}

					@Override
					public void onDraw()
					{
						J2hGame game = Game.getGame();

						nametag.setScale(1.5f, (yEnd - yStart - yPos.getObject()) / (yEnd - yStart) * 1.5f);
						nametag.setPosition(Game.getGame().getWidth() / 2f - nametag.getWidth() / 2f, yPos.getObject() + yStart + bg.getHeight() / 2f - nametag.getHeight() / 2f);
						nametag.draw(game.batch);
					}
				};

				Game.getGame().spawn(object);
			}
		}, 1);
	}
	
	public static void doPC98EnterAnimation()
	{
		doPC98EnterAnimation(Color.BLACK);
	}
	
	public static void doPC98EnterAnimation(final Color color)
	{
		final Animation pc98enter = AnimationUtil.makeSpriteAnimationFromFolder(3, Gdx.files.internal("animations/pc98enter"));
		
		DrawObject obj = new DrawObject()
		{
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("animations/pc98.mp3"));
			
			{
				addDisposable(sound);
				sound.play();
			}
			
			@Override
			public void onDraw()
			{
				Sprite r = (Sprite) pc98enter.getKeyFrame(getTicksAlive());
				r.setColor(color);

				r.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				r.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());

				r.draw(Game.getGame().batch);
			}

			@Override
			public void onUpdate(long tick)
			{
				if(pc98enter.isAnimationFinished(getTicksAlive()))
					Game.getGame().delete(this);
			}
		};

		obj.setZIndex(J2hGame.GUI_Z_ORDER - 1);

		Game.getGame().spawn(obj);
	}
	
	public static void doPC98EndAnimation(final Color color)
	{
		final Animation pc98end = AnimationUtil.makeSpriteAnimationFromFolder(5, Gdx.files.internal("animations/pc98end"));
		
		DrawObject obj = new DrawObject()
		{
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("animations/pc98.mp3"));
			
			{
				addDisposable(sound);
				sound.play();
			}
			
			@Override
			public void onDraw()
			{
				Sprite r = (Sprite) pc98end.getKeyFrame(getTicksAlive());
				r.setColor(color);

				r.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
				r.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());

				r.draw(Game.getGame().batch);
			}

			@Override
			public void onUpdate(long tick)
			{
				if(pc98end.isAnimationFinished(getTicksAlive()))
					Game.getGame().delete(this);
			}
		};

		obj.setZIndex(J2hGame.GUI_Z_ORDER - 1);

		Game.getGame().spawn(obj);
	}

	public static void presentSpellCard(final Boss boss, final String spellName)
	{
		TouhouSounds.Enemy.ACTIVATE_1.play();
		
		final Sprite image = new Sprite(boss.getFullBodySprite());
		float scale = 900 / image.getHeight();
		image.setScale(scale);
		
		final J2hGame stage = Game.getGame();
		
		final SaveableObject<Boolean> fade = new SaveableObject<Boolean>();
		fade.setObject(false);
		
		spawnSpellName(spellName, new Getter<Boolean>()
		{
			@Override
			public Boolean get()
			{
				return fade.getObject();
			}
		});
	
		stage.spawn(new StageObject(0, 0)
		{
			float x = stage.getMaxX();

			float alpha = 0.60F;
			
			{
				setZIndex(J2hGame.GUI_Z_ORDER - 1);
			}
			
			@Override
			public void onDraw()
			{
				image.setAlpha(alpha);
				image.setOrigin(getWidth() / 2, getHeight() / 2);
				image.setPosition(x, stage.getMinY());
				image.draw(stage.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				float distance = MathUtil.getDistance(x + image.getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getY() + stage.getBoundary().getHeight() / 2) / 30;
				
				x -= 1F + distance;
				
				fade.setObject(x < stage.getMinX());
				
				if(fade.getObject())
				{
					alpha -= 0.04F;
					
					if(alpha < 0.05)
						stage.delete(this);
				}
			}

			@Override
			public float getWidth()
			{
				return 0;
			}

			@Override
			public float getHeight()
			{
				return 0;
			}
		});
	}
	
	public static void presentSpellCard(final Boss boss, final Boss boss2, final String spellName)
	{
		TouhouSounds.Enemy.ACTIVATE_1.play();
		
		final Sprite image = new Sprite(boss.getFullBodySprite());
		float scale = 900 / image.getHeight();
		image.setScale(scale);
		
		final Sprite image2 = new Sprite(boss2.getFullBodySprite());
		scale = 900 / image2.getHeight();
		image2.setScale(scale);
		
		final J2hGame stage = Game.getGame();
		
		final SaveableObject<Boolean> fade = new SaveableObject<Boolean>();
		fade.setObject(false);
		
		spawnSpellName(spellName, new Getter<Boolean>()
		{
			@Override
			public Boolean get()
			{
				return fade.getObject();
			}
		});
		
		stage.spawn(new StageObject(0, 0)
		{
			float x = stage.getMaxX();

			float alpha = 0.60F;
			
			{
				setZIndex(J2hGame.GUI_Z_ORDER - 1);
			}
			
			@Override
			public void onDraw()
			{
				image.setAlpha(alpha);
				image.setOrigin(getWidth() / 2, getHeight() / 2);
				image.setPosition(x, stage.getMinY());
				image.draw(stage.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				float distance = MathUtil.getDistance(x + image.getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getY() + stage.getBoundary().getHeight() / 2) / 30;
				
				x -= 1F + distance;
				
				fade.setObject(x < stage.getMinX());
				
				if(fade.getObject())
				{
					alpha -= 0.04F;
					
					if(alpha < 0.05)
						stage.delete(this);
				}
			}

			@Override
			public float getWidth()
			{
				return 0;
			}

			@Override
			public float getHeight()
			{
				return 0;
			}
		});
		
		stage.spawn(new StageObject(0, 0)
		{
			float x = stage.getMaxX();

			float alpha = 0.60F;
			
			{
				setZIndex(J2hGame.GUI_Z_ORDER - 1);
			}
			
			@Override
			public void onDraw()
			{
				image2.setAlpha(alpha);
				image2.setOrigin(getWidth() / 2, getHeight() / 2);
				image2.setPosition(Game.getGame().getWidth() - image2.getWidth() * 1.5f - x, stage.getMinY());
				image2.draw(stage.batch);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				float distance = MathUtil.getDistance(x + image2.getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getX() + stage.getBoundary().getWidth() / 2, stage.getBoundary().getY() + stage.getBoundary().getHeight() / 2) / 30;
				
				x -= 1F + distance;

				if(fade.getObject())
				{
					alpha -= 0.04F;
					
					if(alpha < 0.05)
						stage.delete(this);
				}
			}

			@Override
			public float getWidth()
			{
				return 0;
			}

			@Override
			public float getHeight()
			{
				return 0;
			}
		});
	}
	
	private static BitmapFont spellCardFont = TouhouFont.get(50);
	
	private static void spawnSpellName(final String spellName, final Getter<Boolean> start)
	{
		final Texture spellcard = Loader.texture(Gdx.files.internal("spellcard.png"));
		final Sprite sprite = new Sprite(spellcard);
		
		spellCardFont = TouhouFont.get(30);
		
		game.spawn(new DrawObject()
		{
			{
				setZIndex(J2hGame.GUI_Z_ORDER + 1);
				
				addDisposable(spellcard);
			}
			
			float y = Game.getGame().getHeight() - 800;
			
			@Override
			public void onDraw()
			{
				TextBounds b = spellCardFont.getBounds(spellName);
				float x = game.getMinX() + game.getMaxX() / 2 - b.width / 2;
				
				game.batch.setProjectionMatrix(Game.getGame().standardProjectionMatrix);
				sprite.setPosition(game.getMinX() + game.getMaxX() / 2 - sprite.getWidth() / 2f, y - sprite.getHeight() * 0.8f);
				sprite.draw(game.batch);
				spellCardFont.setColor(Color.BLACK);
				spellCardFont.draw(Game.getGame().batch, spellName, x - 3, y - 3);
				spellCardFont.setColor(Color.WHITE);
				spellCardFont.draw(Game.getGame().batch, spellName, x, y);
				game.batch.setProjectionMatrix(game.camera.camera.combined);
			}
			
			@Override
			public void onUpdate(long tick)
			{
				if(!start.get())
					return;
				
				if(y < Game.getGame().getHeight() - 30)
				{	
					y += 15F;
				}
			}
		});
	}
	
//	public static void presentSpellCard(final Boss boss, final Boss boss2, final String spellName)
//	{
//		TouhouSounds.Enemy.ACTIVATE_1.play();
//		
//		final Sprite image1 = new Sprite(boss.getFullBodySprite());
//		float scale = 900 / image1.getHeight();
//		image1.setScale(scale);
//		
//		final Java2huGame stage = Game.getGame();
//		
//		final SaveableObject<Boolean> fade = new SaveableObject<Boolean>();
//		fade.setObject(false);
//		
//		stage.spawn(new DrawObject()
//		{
//			{
//				setZIndex(Java2huGame.GUI_Z_ORDER - 2);
//			}
//			
//			float y = Game.getGame().getHeight() - 800;
//			
//			@Override
//			public void onDraw()
//			{
//				Game.getGame().batch.setProjectionMatrix(Game.getGame().standardProjectionMatrix);
//				Game.getGame().font.draw(Game.getGame().batch, spellName, stage.getBoundaryMinX() + (stage.getBoundaryMaxX() / 2) - (Game.getGame().font.getBounds(spellName).width / 2), y);
//				Game.getGame().batch.setProjectionMatrix(Game.getGame().camera.camera.combined);
//			}
//			
//			@Override
//			public void onUpdate(long tick)
//			{
//				if(fade.getObject()  && y < Game.getGame().getHeight() - 20)
//				{	
//					y += 20F;
//				}
//			}
//		});
//		
//		stage.spawn(new StageObject(0, 0)
//		{
//			float y = 900 - image1.getHeight();
//
//			float alpha = 0.60F;
//			
//			{
//				setZIndex(Java2huGame.GUI_Z_ORDER - 1);
//			}
//			
//			@Override
//			public void onDraw()
//			{
//				image1.setAlpha(alpha);
//				image1.setOrigin(getWidth() / 2, getHeight() / 2);
//				image1.setPosition((Game.getGame().getHeight() / 2f) - image1.getWidth() - 100, y);
//				image1.draw(stage.batch);
//			}
//			
//			@Override
//			public void onUpdate(long tick)
//			{
//				y -= 2F;
//				
//				fade.setObject(y < 100);
//				
//				if(fade.getObject())
//				{
//					alpha -= 0.04F;
//					
//					if(alpha < 0.05)
//						stage.delete(this);
//				}
//			}
//
//			@Override
//			public float getWidth()
//			{
//				return 0;
//			}
//
//			@Override
//			public float getHeight()
//			{
//				return 0;
//			}
//		});
//		
//		final Sprite image2 = new Sprite(boss2.getFullBodySprite());
//		scale = 900 / image2.getHeight();
//		image2.setScale(scale);
//		
//		stage.spawn(new StageObject(0, 0)
//		{
//			float y = 900 - image2.getHeight();
//
//			float alpha = 0.60F;
//			
//			{
//				setZIndex(Java2huGame.GUI_Z_ORDER - 1);
//			}
//			
//			@Override
//			public void onDraw()
//			{
//				image2.setAlpha(alpha);
//				image2.setOrigin(getWidth() / 2, getHeight() / 2);
//				image2.setPosition((Game.getGame().getHeight() / 2f) + 100, y);
//				image2.draw(stage.batch);
//			}
//			
//			@Override
//			public void onUpdate(long tick)
//			{
//				y -= 2F;
//				
//				fade.setObject(y < 100);
//				
//				if(fade.getObject())
//				{
//					alpha -= 0.04F;
//					
//					if(alpha < 0.05)
//						stage.delete(this);
//				}
//			}
//
//			@Override
//			public float getWidth()
//			{
//				return 0;
//			}
//
//			@Override
//			public float getHeight()
//			{
//				return 0;
//			}
//		});
//	}
}
