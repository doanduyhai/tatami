package fr.ippon.tatami.repository;

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
		assertTrue(match.find());
	}
}
