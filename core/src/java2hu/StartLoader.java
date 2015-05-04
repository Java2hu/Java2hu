package java2hu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java2hu.overwrite.J2hObject;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;


/**
 * Loader to load all that needs to be loaded.
 * ie. Add the @LoadOnStartup annotation to a static method, and it shall be loaded once the loadStartup function is called here.
 * This way you don't need to make a list of all things to load in the main class, but simply call this. (It is already standardly loaded in Java2huGame.create())
 * Note: Must not have any arguments.
 */
public class StartLoader extends J2hObject
{
	public static void loadStartup(final Runnable onFinish)
	{
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forClassLoader())
				.setScanners(new MethodAnnotationsScanner()));

				for(Method method : reflections.getMethodsAnnotatedWith(LoadOnStartup.class))
				{
					if(!method.isAccessible())
					{
						method.setAccessible(true);
					}

					if(!Modifier.isStatic(method.getModifiers()))
					{
						System.out.println("Method " + method + " is not static and contains LoadByLoader annotation, please static the method or remove it.");
						continue;
					}

					if(method.getParameterTypes().length != 0)
					{
						System.out.println("Method " + method + " has arguments, please remove the arguments or remove it.");
						continue;
					}

					try
					{
						method.invoke(null, (Object[])null);
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}
				}
				
				if(onFinish != null)
				{
					if(!Game.getGame().isPaused())
						Game.getGame().addTaskGame(onFinish, 0);
					else
						Game.getGame().addTaskPause(onFinish, 0);
				}
				
				interrupt();
			}
		};
		
		thread.start();
	}
	
	/**
	 * Assign this annotation to a static method, and it shall be executed once you call the loadStartup() method
	 */
	public static @interface LoadOnStartup
	{

	}
}
