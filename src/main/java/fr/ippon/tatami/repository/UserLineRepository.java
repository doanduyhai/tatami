package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

public interface UserLineRepository
{
	void addTweetToUserline(User user, String tweetId);

	Collection<String> getTweetsRangeFromUserline(User user, int start, int end);
}
