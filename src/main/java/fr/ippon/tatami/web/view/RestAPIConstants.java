package fr.ippon.tatami.web.view;

public class RestAPIConstants
{
	// Lines
	public static final String TIMELINE_REST = "statuses/home_timeline";

	public static final String USERLINE_REST = "statuses/user_timeline";

	public static final String FAVORITELINE_REST = "statuses/favorites";

	public static final String TAGLINE_REST = "statuses/hashtag";

	public static final String DAY_STATS_REST = "stats/day";

	public static final String WEEK_STATS_REST = "stats/week";

	// Contacts
	public static final String USER_SUGGESTIONS_REST = "users/suggestions";

	public static final String FRIENDSLINE_REST = "friends";

	public static final String FOLLOWERSLINE_REST = "followers";

	public static final String FRIEND_ADD_REST = "friendships/create/{id}";

	public static final String FRIEND_REMOVE_REST = "friendships/destroy/{id}";

	// Tweet
	public static final String TWEET_POST_REST = "statuses/update";

	public static final String TWEET_REMOVE_REST = "statuses/destroy/{id}";

	public static final String FAVORITE_ADD_REST = "favorites/create/{id}";

	public static final String FAVORITE_REMOVE_REST = "favorites/destroy/{id}";

	// User
	public static final String USER_SHOW_REST = "users/show/{id}";

	public static final String USER_STATS_REST = "users/stats/{id}";

	public static final String USER_PREVIEW_REST = "users/preview/{id}";

	public static final String USER_SEARCH_REST = "users/search";

	public static final String USER_UPDATE_REST = "account/update_profile";

}
