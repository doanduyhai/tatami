package fr.ippon.tatami.repository;

import java.util.Collection;

public interface ReTweetRepository
{

	// RetweetLine
	void addToRetweetLine(String userLogin, String originalTweetId);

	boolean isTweetInRetweetLine(String userLogin, String tweetId);

	void removeFromRetweetLine(String userLogin, String originalTweetId);

	// Retweeters
	void addRetweeter(String retweeterLogin, String originalTweetId, String retweetId);

	void removeRetweeter(String retweeterLogin, String originalTweetId);

	String findRetweetIdForRetweeter(String retweeterLogin, String originalTweetId);

	long countRetweeters(String originalTweetId);

	Collection<String> findRetweetIdsForTweet(String originalTweetId);

	Collection<String> findRetweetersForTweet(String originalTweetId);

	void removeRetweeterIndex(String originalTweetId);

}
