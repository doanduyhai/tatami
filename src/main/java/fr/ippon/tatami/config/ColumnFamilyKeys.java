package fr.ippon.tatami.config;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class ColumnFamilyKeys
{

	public final static String USER_CF = "User";

	public final static String FRIENDS_CF = "UserFriends";

	public final static String FOLLOWERS_CF = "UserFollowers";

	// Column family to keep track of all tweets in timeline coming from a followed user
	public final static String FOLLOWED_TWEET_INDEX_CF = "FollowedTweetIndex";

	public final static String TWEET_CF = "Tweet";

	public final static String DAYLINE_CF = "DayLine";

	public final static String WEEKLINE_CF = "WeekLine";

	public final static String MONTHLINE_CF = "MonthLine";

	public final static String YEARLINE_CF = "YearLine";

	public final static String FAVORITELINE_CF = "FavoriteLine";

	// Column family to keep track of all users having a tweet as favorite
	public final static String FAVORITE_INDEX_CF = "FavoriteIndex";

	// Column family to keep track of all favorite tweets by couple (currentUser,author)
	public final static String FAVORITE_TWEET_USER_INDEX_CF = "FavoriteTweetUserIndex";

	public final static String TAGLINE_CF = "TagLine";

	public final static String TIMELINE_CF = "TimeLine";

	public final static String USERLINE_CF = "UserLine";

	public final static String MENTIONLINE_CF = "MentionLine";

	// Column family to keep track of all tweets in timeline coming from an user mentioning you
	public final static String MENTION_TWEET_INDEX_CF = "MentionTweetIndex";

	public final static String USERBLOCK_CF = "UserBlock";

	public final static String RETWEETER_CF = "Retweeter";

	public final static String RETWEETLINE_CF = "RetweetLine";

	public final static String COUNTER_CF = "Counter";

	public final static String USER_INDEX_CF = "UserIndex";
}
