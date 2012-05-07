package fr.ippon.tatami.repository;

import java.util.Collection;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface TimeLineRepository
{
	void addTweetToTimeline(String userLogin, String tweetId);

	void removeTweetFromTimeline(String userLogin, String tweetId);

	Collection<String> getTweetsRangeFromTimeline(String userLogin, String startTweetId, int count);
}
