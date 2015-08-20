package java2hu.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json
{
	/**
	 * Static gson instance.
	 */
	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
}
