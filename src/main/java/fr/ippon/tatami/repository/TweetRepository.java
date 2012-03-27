package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.Tweet;

/**
 * The User Respository.
 * 
 * @author Julien Dubois
 */
public interface TweetRepository
{

	Tweet createTweet(String login, String content);

	void addTweetToDayline(Tweet tweet, String key);

	void addTweetToUserline(Tweet tweet);

	void addTweetToTimeline(String login, Tweet tweet);

	void addTweetToTagline(Tweet tweet);

	void addTweetToFavoritesline(Tweet tweet, String login);

	/**
	 * a day's tweets
	 */
	Collection<String> getDayline(String date);

	/**
	 * a user's and his followed users tweets
	 */
	Collection<String> getTimeline(String login, int size);

	/**
	 * a user's own tweets
	 */
	Collection<String> getUserline(String login, int size);

	/**
	 * a tag's tweets
	 */
	Collection<String> getTagline(String tag, int size);

	/**
	 * a user's favorite tweets
	 */
	Collection<String> getFavoritesline(String login);

	Tweet findTweetById(String tweetId);

	void removeTweet(Tweet tweet);
}
