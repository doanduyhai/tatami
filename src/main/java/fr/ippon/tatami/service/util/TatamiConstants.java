package fr.ippon.tatami.service.util;

public class TatamiConstants
{
	public static final int DEFAULT_TWEET_LIST_SIZE = 10;

	public static final int DEFAULT_TAG_LIST_SIZE = 10;

	public static final int DEFAULT_FAVORITE_LIST_SIZE = 10;

	public static final int DEFAULT_DAY_LIST_SIZE = 10;

	public static final int DEFAULT_WEEK_LIST_SIZE = 10;

	public static final int DEFAULT_MONTH_LIST_SIZE = 10;

	public static final int DEFAULT_YEAR_LIST_SIZE = 10;

	public static final int USER_SUGGESTION_LIMIT = 5;

	public static final int MAX_TWEET_SIZE = 140;

	// (?<!&) : negative look-behind -> the # should not be preceded by a &
	// (?!x) : negative look-ahead -> the # should not be followed by a x
	// This pattern is set to avoid matching all HTML escaped characters (ex: &#x28; ) as hash tags
	public static final String HASHTAG_REGEXP = "(?<!&)#(?!x)(\\w+)";

	public static final String TAG_LINK_PATTERN = "<a href='#' data-tag='$1' title='Show $1 related tweets'><em>#$1</em></a>";

	public static final String USER_REGEXP = "@(\\w+)";

	public static final String USER_LINK_PATTERN = "<a href='#' data-user='$1' title='Show $1 tweets'><em>@$1</em></a>";

}
