package fr.ippon.tatami.service.util;

/**
 * 
 * @author DuyHai DOAN
 */
public class TatamiConstants
{
	public static final int DEFAULT_TWEET_LIST_SIZE = 10;

	public static final int DEFAULT_TAG_LIST_SIZE = 10;

	public static final int DEFAULT_FAVORITE_LIST_SIZE = 10;

	public static final int DEFAULT_DAY_LIST_SIZE = 10;

	public static final int DEFAULT_WEEK_LIST_SIZE = 10;

	public static final int DEFAULT_MONTH_LIST_SIZE = 10;

	public static final int DEFAULT_YEAR_LIST_SIZE = 10;

	public static final int DEFAULT_USER_LIST_SIZE = 5;

	public static final int USER_SUGGESTION_LIMIT = 5;

	public static final int USER_SEARCH_LIMIT = 10;

	public static final int MAX_TWEET_SIZE = 140;

	public static final int FIRST_FETCH_SIZE = 5;

	public static final int SECOND_FETCH_SIZE = 10;

	public static final int THIRD_FETCH_SIZE = 20;

	public static final int MAX_CHARACTERS_PER_TWEET = 140;

	public static final String HASHTAG_REGEXP = "#(\\w+)";

	public static final String HTML_ENCODED_HASHTAG_REGEXP = "&#x23;(\\w+)";

	public static final String HASHTAG = "#";

	public static final String TAG_LINK_PATTERN = "<a href='#' data-tag='$1' title='Show $1 related tweets'><em>#$1</em></a>";

	public static final String USER_REGEXP = "@(\\w+)";

	public static final String HTML_ENCODED_USER_REGEXP = "&#x40;(\\w+)";

	public static final String USERTAG = "@";

	public static final String USER_LINK_PATTERN = "<a href='#' data-user='$1' title='Show $1 tweets'><em>@$1</em></a>";

	public static final String LINK_REGEXP = "((?:(?:https?|ftp|file)://|www)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";

	public static final String LINK_PATTERN = "<a href='_URL_' title='_URL_' target='_blank'>_SHORT-URL_</a>";

	public static final String LINK_PROTOCOL_PREFIX = "(https?|ftp|file)://";

	public static final int LINK_SHORT_LENGTH = 13;

	public static final Character LOGIN_SEPARATOR = ':';
}
