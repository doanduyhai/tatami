package fr.ippon.tatami.repository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.esapi.reference.DefaultEncoder;

public class Test
{

	@org.testng.annotations.Test
	public void t()
	{
		String url1 = "http&#x3a;&#x2f;&#x2f;www.lemonde.fr&#x2f;election-presidentielle-2012&#x2f;article&#x2f;2012&#x2f;05&#x2f;11, it&#x27;s cool";
		String url2 = "https&#x3a;&#x2f;&#x2f;www.rue89.com";
		String url3 = "ftp&#x3a;&#x2f;&#x2f;www.lefigaro.fr";

		// [-a-zA-Z0-9+&@#/%?=~_|!:,.;]* [-a-zA-Z0-9+&@#/%=~_|]
		Pattern linkPattern = Pattern
				.compile("((?:https?|ftp|file)&#x3a;&#x2f;&#x2f;(?:[-a-zA-Z0-9_,.]|&#x2b;|&amp;|&#x40;|&#x23;|&#x2f;|&#x25;|&#x3f;|&#x3d;|&#x7e;|&#x7c;|&#x21;|&#x3a;|&#x3b;)*(?:[-a-zA-Z0-9_]|&#x2b;|&amp;|&#x40;|&#x23;|&#x2f;|&#x25;|&#x3f;|&#x3d;|&#x7e;|&#x7c;))");
		Matcher matcher = linkPattern.matcher(url1);
		while (matcher.find())
		{
			System.out.println("matched = " + matcher.group(1));
		}

		System.out.println("url 1 = " + url1.replace("\\w+\\w(?!\\s)", "TOTO"));
		System.out.println("url 2 = " + url2.replaceAll("(https?|ftp|file)&#x3a;&#x2f;&#x2f;", ""));
		System.out.println("url 3 = " + url3.replaceAll("(https?|ftp|file)&#x3a;&#x2f;&#x2f;", ""));

		// &#x2b; = +
		// &amp; = &
		// &#x40; = @
		// &#x23; = #
		// &#x2f; = /
		// &#x25; = %
		// &#x3f; = ?
		// &#x3d; = =
		// &#x7e; = ~
		// _
		// &#x7c; = |
		// &#x21; = !
		// &#x3a; = :
		// ,
		// .
		// &#x3b; = ;
		System.out.println("encoded = " + DefaultEncoder.getInstance().encodeForHTML("+&@#/%?=~_|!:,.;"));
	}
}
