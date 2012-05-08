package fr.ippon.tatami.service.util;

import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TAG_LIST_SIZE;
import static fr.ippon.tatami.service.util.TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class JavascriptConstantsBuilderTest
{

	@Test
	public void testBuildScriptManualInput()
	{
		JavascriptConstantsBuilder builder = new JavascriptConstantsBuilder();

		String script = builder.add("DEFAULT_TWEET_LIST_SIZE", DEFAULT_TWEET_LIST_SIZE).add("DEFAULT_TAG_LIST_SIZE", DEFAULT_TAG_LIST_SIZE)
				.add("DEFAULT_FAVORITE_LIST_SIZE", DEFAULT_FAVORITE_LIST_SIZE).build();

		assertTrue(script.contains("var DEFAULT_TWEET_LIST_SIZE=" + DEFAULT_TWEET_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_TAG_LIST_SIZE=" + DEFAULT_TAG_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_FAVORITE_LIST_SIZE=" + DEFAULT_FAVORITE_LIST_SIZE + ";"));
	}

	@Test
	public void testBuildScriptMapInput()
	{
		Map<String, Object> constantsMap = new HashMap<String, Object>();
		constantsMap.put("DEFAULT_TWEET_LIST_SIZE", DEFAULT_TWEET_LIST_SIZE);
		constantsMap.put("DEFAULT_TAG_LIST_SIZE", DEFAULT_TAG_LIST_SIZE);
		constantsMap.put("DEFAULT_FAVORITE_LIST_SIZE", DEFAULT_FAVORITE_LIST_SIZE);

		String script = new JavascriptConstantsBuilder().add(constantsMap).build();

		assertTrue(script.contains("var DEFAULT_TWEET_LIST_SIZE=" + DEFAULT_TWEET_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_TAG_LIST_SIZE=" + DEFAULT_TAG_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_FAVORITE_LIST_SIZE=" + DEFAULT_FAVORITE_LIST_SIZE + ";"));

	}

	@Test
	public void testBuildScriptByClasses() throws IllegalArgumentException, IllegalAccessException
	{
		@SuppressWarnings("unused")
		class TestClass1
		{

			public static final int DEFAULT_TWEET_LIST_SIZE = TatamiConstants.DEFAULT_TWEET_LIST_SIZE;
			public static final int DEFAULT_TAG_LIST_SIZE = TatamiConstants.DEFAULT_TAG_LIST_SIZE;

		}

		@SuppressWarnings("unused")
		class TestClass2
		{
			public static final int DEFAULT_FAVORITE_LIST_SIZE = TatamiConstants.DEFAULT_FAVORITE_LIST_SIZE;

		}

		String script = new JavascriptConstantsBuilder().add(TestClass1.class, TestClass2.class).build();
		assertTrue(script.contains("var DEFAULT_TWEET_LIST_SIZE=" + DEFAULT_TWEET_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_TAG_LIST_SIZE=" + DEFAULT_TAG_LIST_SIZE + ";"));
		assertTrue(script.contains("var DEFAULT_FAVORITE_LIST_SIZE=" + DEFAULT_FAVORITE_LIST_SIZE + ";"));

	}
}
