package fr.ippon.tatami.repository;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class TestUtil
{

	@Test
	public void testRegExp()
	{
		Pattern firstname = Pattern.compile("^[a-zA-Z][ a-zA-Z-]{1,29}(?<!-)$");
		Matcher match = firstname.matcher("DuyHai DOAN-");
		assertFalse(match.find());
	}

	@Test
	public void testTagPattern()
	{

		Pattern firstname = Pattern.compile("(?<!&)#(?!x)(\\w+)");
		Matcher match = firstname
				.matcher("&lt;script type&#x3d;&quot;text&#x2f;javascript&quot;&gt;alert&#x28;&#x27;Test XSS&#x27;&#x29;&#x3b;&lt;&#x2f;script&gt; ");
		assertFalse(match.find());
		Matcher match2 = firstname.matcher("<script>alert#Java");
		assertTrue(match2.find());
	}
}