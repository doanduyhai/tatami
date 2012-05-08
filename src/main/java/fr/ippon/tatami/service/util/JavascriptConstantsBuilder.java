package fr.ippon.tatami.service.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javassist.Modifier;

public class JavascriptConstantsBuilder
{

	private final StringBuilder script = new StringBuilder();

	public JavascriptConstantsBuilder() {}

	public JavascriptConstantsBuilder add(Map<String, Object> map)
	{
		for (Entry<String, Object> entry : map.entrySet())
		{
			this.add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public JavascriptConstantsBuilder add(Class<?>... classes) throws IllegalArgumentException, IllegalAccessException
	{

		Map<String, Object> constantsMap = new HashMap<String, Object>();
		for (Class<?> clazz : classes)
		{
			for (Field field : clazz.getDeclaredFields())
			{
				if (Modifier.isPublic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))
				{
					constantsMap.put(field.getName(), field.get(null));
				}
			}
		}

		for (Entry<String, Object> entry : constantsMap.entrySet())
		{
			this.add(entry.getKey(), entry.getValue());
		}

		return this;

	}

	public JavascriptConstantsBuilder add(String name, Object constant)
	{
		script.append("var ").append(name).append('=');
		if (constant instanceof String)
		{
			script.append("'").append(constant).append("'");
		}
		else if (constant instanceof Number)
		{
			script.append(constant);
		}
		else
		{
			throw new IllegalArgumentException("The variable " + constant + " should be of type String or Number");
		}
		script.append(";\n");

		return this;
	}

	public String build()
	{
		return this.script.toString();
	}

}
