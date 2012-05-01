package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface TimeLineRepository
{
	void addTweetToTimeline(User user, String tweetId);

	void removeTweetFromTimeline(User user, String tweetId);

	Collection<String> getTweetsRangeFromTimeline(User user, String startTweetId, int count);
}
