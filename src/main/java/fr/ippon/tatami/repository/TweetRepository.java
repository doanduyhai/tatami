package fr.ippon.tatami.repository;

import fr.ippon.tatami.domain.Tweet;

/**
 * The User Respository.
 * 
 * @author Julien Dubois
 */
public interface TweetRepository
{

	Tweet createTweet(String login, String content);

	Tweet findTweetById(String tweetId);

	void removeTweet(Tweet tweet);
}
