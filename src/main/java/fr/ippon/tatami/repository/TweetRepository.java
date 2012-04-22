package fr.ippon.tatami.repository;

import fr.ippon.tatami.domain.Tweet;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface TweetRepository
{

	Tweet createTweet(String login, String content);

	Tweet findTweetById(String tweetId);

	void removeTweet(Tweet tweet);
}
