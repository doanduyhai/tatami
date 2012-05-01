package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

/**
 * 
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public interface UserLineRepository
{
	void addTweetToUserline(User user, String tweetId);

	void removeTweetFromUserline(User user, String tweetId);

	Collection<String> getTweetsRangeFromUserline(User user, String startTweetId, int count);
}
