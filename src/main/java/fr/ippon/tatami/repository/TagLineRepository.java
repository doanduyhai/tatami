package fr.ippon.tatami.repository;

import java.util.Collection;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface TagLineRepository
{
	void addTweet(String tag, String tweetId);

	void removeTweet(String tag, String tweetId);

	Collection<String> findTweetsRangeForTag(String tag, String startTweetId, int count);
}
