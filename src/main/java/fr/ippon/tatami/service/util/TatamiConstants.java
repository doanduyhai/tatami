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

	// &#x23; is the HTML encoded version of the # character
	public static final String HASHTAG_REGEXP = "&#x23;(\\w+)";

	public static final String TAG_LINK_PATTERN = "<a href='#' data-tag='$1' title='Show $1 related tweets'><em>#$1</em></a>";

	// &#x40; is the HTML encoded version of the @ character
	public static final String USER_REGEXP = "&#x40;(\\w+)";

	public static final String USER_LINK_PATTERN = "<a href='#' data-user='$1' title='Show $1 tweets'><em>@$1</em></a>";

	public static final String TWEET_NB_PATTERN = "__TWEET-NB__";
}
