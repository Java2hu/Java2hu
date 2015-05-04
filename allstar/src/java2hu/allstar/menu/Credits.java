package java2hu.allstar.menu;

import java.util.ArrayList;
import java2hu.Game;
import java2hu.HitboxSprite;
import java2hu.J2hGame;
import java2hu.menu.ButtonManager;
import java2hu.menu.Menu;
import java2hu.object.FreeStageObject;
import java2hu.object.StageObject;
import java2hu.overwrite.J2hObject;
import java2hu.touhou.bullet.ThBullet;
import java2hu.touhou.bullet.ThBulletColor;
import java2hu.touhou.bullet.ThBulletType;
import java2hu.touhou.font.TouhouFont;
import java2hu.touhou.sounds.TouhouSounds;
import java2hu.util.ImageUtil;
import java2hu.util.MeshUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Credits extends Menu
{
	ButtonManager manager = new ButtonManager();
	Sprite bg;
	Music bgm = Gdx.audio.newMusic(Gdx.files.internal("music/credits.mp3"));
	
	{
		bgm.setVolume(1f * Game.getGame().getMusicModifier());
		bgm.setLooping(true);
	}
	
	ArrayList<StageObject> objects = new ArrayList<StageObject>();
	
	{
		Texture texture = ImageUtil.makeDummyTexture(Color.WHITE, 1, 1);
		bg = new Sprite(texture);
	}
	
	FreeTypeFontParameter topPara = new FreeTypeFontParameter();
	FreeTypeFontParameter botPara = new FreeTypeFontParameter();
	FreeTypeFontParameter bigPara = new FreeTypeFontParameter();
	
	{
		topPara.size = 100;
		bigPara.size = 30;
		botPara.size = 20;
	}
	
	BitmapFont topFont = TouhouFont.get(topPara);
	BitmapFont botFont = TouhouFont.get(botPara);
	BitmapFont bigFont = TouhouFont.get(bigPara);
	
	{
		TextBounds bound = botFont.getBounds("Exit");
		final Menu screen = this;
				
		manager.addButton(new ShadowedTextButton(bound.width / 2, 100, bigFont, "Exit", new Runnable()
		{
			@Override
			public void run()
			{
				TouhouSounds.Hud.OK.play();
				bgm.stop();
				bgm.dispose();
				
				for(StageObject obj : objects)
				{
					Game.getGame().delete(obj);
				}
				
				Game.getGame().getSpellcards().clear();
				Game.getGame().delete(screen);
			}
		}));
		
		setZIndex(J2hGame.GUI_Z_ORDER + 2);
		
		bgm.play();
	}
	
	private long startTick = 0;
	
	public Credits(Menu parent)
	{
		super(parent);
		startTick = Game.getGame().getActiveTick() + 1; // First tick is going to be NEXT tick
	}
	
	@Override
	public void onDraw()
	{
		bg.setPosition(0, 0);
		bg.setSize(Game.getGame().getWidth(), Game.getGame().getHeight());
		bg.draw(Game.getGame().batch);
		
		J2hGame game = Game.getGame();
		
		Color color = Color.WHITE;
		
		manager.draw();
	}
	
	public void writeLine(String text, float x, float y)
	{
		writeLine(text, botFont, x, y);
	}
	
	public void writeLine(String text, BitmapFont font, float x, float y)
	{
		TextBounds bounds = font.getBounds(text);
		
		font.setColor(Color.BLACK);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2 + 3, y);

		font.setColor(Color.WHITE);
		font.draw(Game.getGame().batch, text, x - bounds.width / 2, y + 3);
	}
	
	private CreditRoll roll = new CreditRoll(this);

	@Override
	public void onUpdate(long tick)
	{
		manager.update();
		
		roll.tick((int) (Game.getGame().getActiveTick() - startTick));
	}
	
	// Increase in pace: 750
	// Climax: 2950
	
	public static class CreditRoll extends J2hObject
	{
		Credits screen;
		
		public CreditRoll(Credits screen)
		{
			this.screen = screen;
		}
		
		private float x = 0;
		private float y = 0;
		
		private float lastX = 0;
		private float lastY = 0;
		
		public void setX(float x)
		{
			lastX = this.x;
			this.x = x;
		}
		
		public void setY(float y)
		{
			lastY = this.y;
			this.y = y;
		}
		
		public float getXDiff()
		{
			return -lastX + x;
		}
		
		public float getYDiff()
		{
			return -lastY + y;
		}
		
		private int rollTick = 0;
		
		public void tick(int tick)
		{
			final CreditRoll roll = this;
			rollTick = tick;
			
			System.out.println(tick);
			
			if(tick == 0)
			{
				FreeStageObject sun = new FreeStageObject(Game.getGame().getWidth()/2f, Game.getGame().getHeight())
				{
					Mesh mesh;
					
					@Override
					public void onDraw()
					{
						Game.getGame().batch.end();
						
						MeshUtil.startShader();
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 100, 0, 110, new Color(0.8f, 0.8f, 0f, 1f)));
						
						MeshUtil.renderMesh(mesh);
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 100, 0, 100, Color.YELLOW));
						
						MeshUtil.renderMesh(mesh);
						
						MeshUtil.endShader();
						
						Game.getGame().batch.begin();
					}
					
					@Override
					public void onUpdate(long tick)
					{
						tick = Game.getGame().getActiveTick();
						
						setY(getY() - 2f);
						
						setX(getX() + getXDiff());
						setY(getY() + getYDiff());
						
						if(tick % 2 == 0 && rollTick < 1900)
						{
							FreeStageObject bullet = new FreeStageObject((float) (getX() + (Math.random() * 160 - 80)), getY())
							{
								@Override
								public void onUpdate(long tick)
								{
									tick = Game.getGame().getActiveTick();
									
									setY(getY() + 10f);
									
									setX(getX() + getXDiff());
									setY(getY() + getYDiff());
									
									HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
									
									s.setAlpha(s.getColor().a * 0.92f);
									
									if(s.getColor().a < 0.1f)
										Game.getGame().delete(this);
								};
								
								@Override
								public boolean isActiveDuringPause()
								{
									return true;
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
								
								Animation ani = new ThBullet(ThBulletType.BALL_BIG, ThBulletColor.YELLOW).getAnimation();

								@Override
								public void onDraw()
								{
									HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
									
									s.setPosition(getX() - s.getWidth() / 2f, getY() - s.getHeight() / 2f);
									s.draw(Game.getGame().batch);
								};
							};
							
							bullet.setZIndex(screen.getZIndex() + 1);
							
							Game.getGame().spawn(bullet);
							screen.objects.add(bullet);
						}
					}
					
					@Override
					public boolean isActiveDuringPause()
					{
						return true;
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
				};
				
				sun.setZIndex(screen.getZIndex() + 2);
				
				Game.getGame().spawn(sun);
				screen.objects.add(sun);
				
				FreeStageObject ground = new FreeStageObject(0, Game.getGame().getHeight() - 4500)
				{
					Mesh mesh;
					
					@Override
					public void onDraw()
					{
						Game.getGame().batch.end();
						
						MeshUtil.startShader();
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX() + 620, getY(), 200, 0, 1020, new Color(0.1f, 0.3f, 0.1f, 1f)));
						
						MeshUtil.renderMesh(mesh);
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX() + 620, getY(), 200, 0, 1000, new Color(0.1f, 0.4f, 0.1f, 1f)));
						
						MeshUtil.renderMesh(mesh);
						
						MeshUtil.endShader();
						
						Game.getGame().batch.begin();
					}
					
					@Override
					public void onUpdate(long tick)
					{
						setX(getX() + getXDiff());
						setY(getY() + getYDiff());
					}
					
					@Override
					public boolean isActiveDuringPause()
					{
						return true;
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
				};
				
				ground.setZIndex(screen.getZIndex() + 3);
				
				Game.getGame().spawn(ground);
				screen.objects.add(ground);
				
				for(int i = 0; i < 8; i++)
				{
					final int finalI = i;
					final int delay = finalI * 5;
					
					FreeStageObject launcher = new FreeStageObject(325 + i * 80, Game.getGame().getHeight() - 4500 + 950)
					{
						Mesh mesh;
						
						@Override
						public void onDraw()
						{
							Game.getGame().batch.end();
							
							MeshUtil.startShader();
							
							mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeRectangleVertices(getX(), getY(), 25, 100, Color.BLACK));
							
							MeshUtil.renderMesh(mesh);
							
							MeshUtil.endShader();
							
							Game.getGame().batch.begin();
						}
						
						@Override
						public void onUpdate(long tick)
						{
							setX(getX() + getXDiff());
							setY(getY() + getYDiff());
							
							if(getTicksAlive() < delay)
								return;
							
							if(rollTick == 2400 + delay)
							{
								FreeStageObject bullet = new FreeStageObject(getX() + 12.5f, getY())
								{
									@Override
									public void onUpdate(long tick)
									{
										setX((float) (getX() + getXDiff() + (Math.random() * 4f - 2f)));
										setY(getY() + getYDiff());
										
										tick = Game.getGame().getActiveTick();
										
										setY(getY() + 7f);
										
										if(rollTick == 3000 + delay)
										{
											Game.getGame().delete(this);
											
											final ThBulletType type = ThBulletType.BUTTERFLY;
											
											ThBulletColor bc = null;
											
											while(bc == null || new ThBullet(type, bc).getAnimation() == null)
											{
												bc = ThBulletColor.values()[(int) (Math.random() * ThBulletColor.values().length)];
											}
											
											final ThBulletColor bulletColor = bc;
											
											FreeStageObject glow = new FreeStageObject(getX(), getY() + 100)
											{
												Color color = bulletColor.getColor().cpy();
												
												{
													color.a = 0.6f;
												}

												@Override
												public void onUpdate(long tick)
												{
													tick = Game.getGame().getActiveTick();
													
													setX(getX() + getXDiff());
													setY(getY() + getYDiff());

													color.a -= 0.003f;

													if(color.a <= 0.001f)
														Game.getGame().delete(this);
												};

												@Override
												public boolean isActiveDuringPause()
												{
													return true;
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

												Mesh mesh;

												@Override
												public void onDraw()
												{
													Game.getGame().batch.end();

													MeshUtil.startShader();

													mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 100, 0, getTicksAlive() * 5, color, color.cpy().mul(1, 1, 1, 0)));

													MeshUtil.renderMesh(mesh);

													MeshUtil.endShader();

													Game.getGame().batch.begin();
												};
											};

											glow.setZIndex(screen.getZIndex() + 5);

											Game.getGame().spawn(glow);
											screen.objects.add(glow);
											
											for(int i = 0; i < 360; i += 10)
											{
												final int finalI = i;
												
												FreeStageObject bullet = new FreeStageObject(getX(), getY() + 100)
												{
													@Override
													public void onUpdate(long tick)
													{
														tick = Game.getGame().getActiveTick();

														setX(getX() + getXDiff());
														setY(getY() + getYDiff());
												
														setX((float) (getX() + Math.cos(Math.toRadians(finalI)) * 8f));
														setY((float) (getY() + Math.sin(Math.toRadians(finalI)) * 8f));
													};

													@Override
													public boolean isActiveDuringPause()
													{
														return true;
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

													Animation ani = new ThBullet(type, bulletColor).getAnimation();

													@Override
													public void onDraw()
													{
														HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
														
														s.setRotation(finalI - 90);
														s.setPosition(getX() - s.getWidth() / 2f, getY() - s.getHeight() / 2f);
														s.draw(Game.getGame().batch);
													};
												};

												bullet.setZIndex(screen.getZIndex() + 5);

												Game.getGame().spawn(bullet);
												screen.objects.add(bullet);
											}
										}
									};
									
									@Override
									public boolean isActiveDuringPause()
									{
										return true;
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
									
									Animation ani = new ThBullet(ThBulletType.BULLET, ThBulletColor.BLACK).getAnimation();

									@Override
									public void onDraw()
									{
										HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
										
										s.setPosition(getX() - s.getWidth() / 2f, getY() - s.getHeight() / 2f);
										s.draw(Game.getGame().batch);
									};
								};
								
								bullet.setZIndex(screen.getZIndex() + 1);
								
								Game.getGame().spawn(bullet);
								screen.objects.add(bullet);
								
								FreeStageObject cloud = new FreeStageObject(getX(), getY())
								{
									@Override
									public void onDraw()
									{

									}

									@Override
									public void onUpdate(long tick)
									{
										if(getTicksAlive() >= 40)
											Game.getGame().delete(this);
										
										setX(getX() + getXDiff());
										setY(getY() + getYDiff());

										final float velX = (float) (Math.random() * 2f) - 1f;
										final float velY = (float) (Math.random() * 2f) - 1.8f;

										FreeStageObject bullet = new FreeStageObject(getX(), getY() + 100)
										{
											Color color = Color.GRAY.cpy();

											@Override
											public void onUpdate(long tick)
											{
												tick = Game.getGame().getActiveTick();
												
												setX(getX() + getXDiff());
												setY(getY() + getYDiff());

												setY(getY() + velY);
												setX(getX() + velX);

												color.a -= 0.006f;

												if(color.a <= 0.01f)
													Game.getGame().delete(this);
											};

											@Override
											public boolean isActiveDuringPause()
											{
												return true;
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

											Mesh mesh;

											@Override
											public void onDraw()
											{
												Game.getGame().batch.end();

												MeshUtil.startShader();

												mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 20, 0, 20, color, color.cpy().mul(1, 1, 1, 0)));

												MeshUtil.renderMesh(mesh);

												MeshUtil.endShader();

												Game.getGame().batch.begin();
											};
										};

										bullet.setZIndex(screen.getZIndex() + 5);

										Game.getGame().spawn(bullet);
										screen.objects.add(bullet);
									}

									@Override
									public boolean isActiveDuringPause()
									{
										return true;
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
								};

								cloud.setZIndex(screen.getZIndex() + 4);

								Game.getGame().spawn(cloud);
								screen.objects.add(cloud);
							}
						}
						
						@Override
						public boolean isActiveDuringPause()
						{
							return true;
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
					};
					
					launcher.setZIndex(screen.getZIndex() + 4);
					
					Game.getGame().spawn(launcher);
					screen.objects.add(launcher);
				}
			}
			
			if(tick == 0)
			{
				for(int i = 0; i < 20; i++)
				{
					final boolean rain = Math.random() < 0.4f;
					final boolean lightning = Math.random() < 0.1f;
					
					FreeStageObject cloud = new FreeStageObject((float)(Math.random() * Game.getGame().getWidth()), (float) (y + (Math.random() * 2400 - 1700)))
					{
						@Override
						public void onDraw()
						{

						}

						@Override
						public void onUpdate(long tick)
						{
							if(rollTick > 2000)
								Game.getGame().delete(this);
							
							setX(getX() + getXDiff());
							setY(getY() + getYDiff());
							
							if(rollTick % 20 != 0)
								return;

							final float velX = (float) (Math.random() * 2f) - 1f;
							final float velY = (float) (Math.random() * 2f) - 1f;

							FreeStageObject bullet = new FreeStageObject((float) (getX() + (Math.random() * 160 - 80)), getY())
							{
								Color color = Color.GRAY.cpy();

								@Override
								public void onUpdate(long tick)
								{
									tick = Game.getGame().getActiveTick();
									
									setX(getX() + getXDiff());
									setY(getY() + getYDiff());

									setY(getY() + velY);
									setX(getX() + velX);

									color.a -= 0.006f;

									if(color.a <= 0.01f)
										Game.getGame().delete(this);
									
									if(rain && rollTick % 6 == 0)
									{
										FreeStageObject bullet = new FreeStageObject((float) (getX() + (Math.random() * 160 - 80)), getY())
										{
											@Override
											public void onUpdate(long tick)
											{
												setX(getX() + getXDiff());
												setY(getY() + getYDiff());
												
												tick = Game.getGame().getActiveTick();
												
												setY(getY() - 10f);
												setX((float) (getX() + (Math.random() * 4f - 2f)));
												
												HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
												
												s.setScale(0.5f);
												
												if(s.getColor().a > 0.5)
													s.setAlpha(0.5f);
												else
													s.setAlpha(s.getColor().a * 0.92f);
												
												if(s.getColor().a < 0.1f)
													Game.getGame().delete(this);
											};
											
											@Override
											public boolean isActiveDuringPause()
											{
												return true;
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
											
											Animation ani = new ThBullet(ThBulletType.RAIN, ThBulletColor.BLUE).getAnimation();

											@Override
											public void onDraw()
											{
												HitboxSprite s = (HitboxSprite)ani.getKeyFrames()[0];
												
												s.setPosition(getX() - s.getWidth() / 2f, getY() - s.getHeight() / 2f);
												s.draw(Game.getGame().batch);
											};
										};
										
										bullet.setZIndex(screen.getZIndex() + 1);
										
										Game.getGame().spawn(bullet);
										screen.objects.add(bullet);
									}
								};

								@Override
								public boolean isActiveDuringPause()
								{
									return true;
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

								Mesh mesh;

								@Override
								public void onDraw()
								{
									Game.getGame().batch.end();

									MeshUtil.startShader();

									mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 20, 0, 80, color, new Color(color.r, color.g, color.b, 0f)));

									MeshUtil.renderMesh(mesh);

									MeshUtil.endShader();

									Game.getGame().batch.begin();
								};
							};

							bullet.setZIndex(screen.getZIndex() + 5);

							Game.getGame().spawn(bullet);
							screen.objects.add(bullet);
						}

						@Override
						public boolean isActiveDuringPause()
						{
							return true;
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
					};

					cloud.setZIndex(screen.getZIndex() + 4);

					Game.getGame().spawn(cloud);
					screen.objects.add(cloud);
				}
			}
			
			if(tick == 1500)
			{
				FreeStageObject text = new FreeStageObject(Game.getGame().getWidth()/2f, Game.getGame().getHeight() - 100)
				{
					float alpha = 0f;
					Color black = new Color(0f, 0f, 0f, 1f);
					Color white = new Color(1f, 1f, 1f, 1f);
					
					@Override
					public void onDraw()
					{
						String str = "Touhou 3.14 - All Star";
						
						black.a = alpha;
						white.a = alpha;
						
						TextBounds b = screen.topFont.getBounds(str);
						
						screen.topFont.setColor(black);
						screen.topFont.draw(Game.getGame().batch, str, getX() - b.width / 2f - 2, getY() - 2);
						
						screen.topFont.setColor(white);
						screen.topFont.draw(Game.getGame().batch, str, getX() - b.width / 2f, getY());
						
						str = "A fan game based on the Touhou Project series.";
						
						black.a = alpha;
						white.a = alpha;
						
						b = screen.botFont.getBounds(str);
						
						screen.botFont.setColor(black);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f - 2, getY() - 152);
						
						screen.botFont.setColor(white);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f, getY() - 150);
						
						str = "We have no affilation with Team Shanghai Alice, nor do they endorse us.";
						
						black.a = alpha;
						white.a = alpha;
						
						b = screen.botFont.getBounds(str);
						
						screen.botFont.setColor(black);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f - 2, getY() - 202);
						
						screen.botFont.setColor(white);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f, getY() - 200);
						
						str = "All rights to their respective owners.";
						
						black.a = alpha;
						white.a = alpha;
						
						b = screen.botFont.getBounds(str);
						
						screen.botFont.setColor(black);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f - 2, getY() - 252);
						
						screen.botFont.setColor(white);
						screen.botFont.draw(Game.getGame().batch, str, getX() - b.width / 2f, getY() - 250);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						setX(getX() + getXDiff());
						setY(getY() + getYDiff());
						
						if(alpha < 1)
						{
							alpha += 0.01f;
							alpha = Math.min(alpha, 1f);
						}
					}
					
					@Override
					public boolean isActiveDuringPause()
					{
						return true;
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
				};
				
				text.setZIndex(screen.getZIndex() + 3);
				
				Game.getGame().spawn(text);
				screen.objects.add(text);
			}
			
			if(tick == 2000)
			{
				for(int i = 0; i < 1000; i++)
				{
					FreeStageObject star = new FreeStageObject((float)(Math.random() * 3000), (float) (y + (Math.random() * 8000 - 2400)))
					{
						Color color = Color.WHITE.cpy();
						
						{
							color.a = 0f;
						}
						
						Mesh mesh;
						
						@Override
						public void onDraw()
						{
							if(!Game.getGame().inBoundary(getX(), getY()))
									return;
							
							Game.getGame().batch.end();

							MeshUtil.startShader();

							mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(getX(), getY(), 20, 0, 10, color, color.cpy().mul(1, 1, 1, 0)));

							MeshUtil.renderMesh(mesh);

							MeshUtil.endShader();

							Game.getGame().batch.begin();
						}

						@Override
						public void onUpdate(long tick)
						{
							setX(getX() + getXDiff());
							setY(getY() + getYDiff());
							
							tick = Game.getGame().getActiveTick();
							
							setX(getX() + getXDiff());
							setY(getY() + getYDiff());

							if(color.a < 1f)
							{
								color.a += 0.006f;
								color.a = Math.min(color.a, 1f);
							}
						}

						@Override
						public boolean isActiveDuringPause()
						{
							return true;
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
					};

					star.setZIndex(screen.getZIndex() + 2);

					Game.getGame().spawn(star);
					screen.objects.add(star);
				}
			}
			
			if(tick == 3200)
			{
				FreeStageObject text = new FreeStageObject(Game.getGame().getWidth()/2f, Game.getGame().getHeight() - 100)
				{
					float alpha = 0f;
					Color black = new Color(0f, 0f, 0f, 1f);
					Color white = new Color(1f, 1f, 1f, 1f);
					
					@Override
					public void onDraw()
					{
						black.a = alpha;
						white.a = alpha;
						
						drawCircle(getX() - 300, getY() - 100, 1f, "Jun'ya Ota (ZUN)", "Most artwork, Music");
						
						drawCircle(getX() + 300, getY() - 100, 1f, "Java2hu", "Lead Programmer");
						
						drawCircle(getX() + 80, getY() - 550, 0.6f, "Kaoru", "Various character portraits");
						drawCircle(getX() - 80, getY() - 450, 0.6f, "Yuke", "Idea / Inspritation");
					}
					
					private Mesh mesh;
					
					public void drawCircle(float x, float y, float size, String name, String credit)
					{
						Game.getGame().batch.end();

						MeshUtil.startShader();
						
						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(x, y - 25, 100, 0, Math.min(getTicksAlive() * 3f * size, 190 * size), white.cpy().sub(0.2f, 0.2f, 0.2f, 0f)));

						MeshUtil.renderMesh(mesh);

						mesh = MeshUtil.makeMesh(mesh, MeshUtil.makeCircleVertices(x, y - 25, 100, 0, Math.min(getTicksAlive() * 3f * size, 180 * size), black.cpy().add(0.2f, 0.2f, 0.2f, 0f)));

						MeshUtil.renderMesh(mesh);

						MeshUtil.endShader();

						Game.getGame().batch.begin();
						
						String str = name;
						
						screen.topFont.setScale(0.39f * size);
						
						TextBounds b = screen.topFont.getBounds(str);
						
						screen.topFont.setColor(black);
						screen.topFont.draw(Game.getGame().batch, str, x - b.width / 2f - 2, y - 2);
						
						screen.topFont.setColor(white);
						screen.topFont.draw(Game.getGame().batch, str, x - b.width / 2f, y);
						
						str = credit;
						
						screen.topFont.setScale(0.3f * size);
						
						b = screen.topFont.getBounds(str);
						
						screen.topFont.setColor(black);
						screen.topFont.draw(Game.getGame().batch, str, x - b.width / 2f - 2, y - 50 * size - 2);
						
						screen.topFont.setColor(white);
						screen.topFont.draw(Game.getGame().batch, str, x - b.width / 2f, y - 50 * size);
					}
					
					@Override
					public void onUpdate(long tick)
					{
						setX(getX() + getXDiff());
						setY(getY() + getYDiff());
						
						if(alpha < 1)
						{
							alpha += 0.01f;
							alpha = Math.min(alpha, 1f);
						}
					}
					
					@Override
					public boolean isActiveDuringPause()
					{
						return true;
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
				};
				
				text.setZIndex(screen.getZIndex() + 3);
				
				Game.getGame().spawn(text);
				screen.objects.add(text);
			}

			if(tick < 2000)
				screen.bg.setColor(Math.min(tick * 0.001f, 0.3f), Math.min(tick * 0.002f, 0.6f), Math.min(tick * 0.003f, 0.9f), 1f);
			else
				screen.bg.setColor(Math.max(0.3f - (tick - 2000) * 0.01f, 0.05f), Math.max(0.6f - (tick - 2000) * 0.01f, 0.05f), Math.max(0.9f - (tick - 2000) * 0.01f, 0.05f), 1f);
			
			if(tick < 750)
				setY(Math.min(tick, 2000));
			else if(tick < 2500)
				setY(750 + Math.min((tick - 750) * 3f, 2000));
			else
				setY(Math.max(2750 - (tick - 2500) * 8f, -900));
		}
	}

	@Override
	public void onHide()
	{
		Game.getGame().unregisterEvents(manager);
	}

	@Override
	public void onShow()
	{
		Game.getGame().registerEvents(manager);
	}
}
