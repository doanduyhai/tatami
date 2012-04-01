package fr.ippon.tatami;

import org.owasp.esapi.reference.DefaultEncoder;

import fr.ippon.tatami.service.util.TatamiConstants;

public class TestPattern
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Test 3 #Java".replaceAll(TatamiConstants.USER_REGEXP, TatamiConstants.USER_LINK_PATTERN).replaceAll(
				TatamiConstants.HASHTAG_REGEXP, TatamiConstants.TAG_LINK_PATTERN));
		System.out.println(DefaultEncoder.getInstance().encodeForHTML("Test #java for @jdubois"));
	}

}
