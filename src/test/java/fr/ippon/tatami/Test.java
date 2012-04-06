package fr.ippon.tatami;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Pattern pattern = Pattern.compile("^((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])?");
		Matcher match = pattern.matcher("");

		System.out.println(" found ? " + match.find());

	}

}
