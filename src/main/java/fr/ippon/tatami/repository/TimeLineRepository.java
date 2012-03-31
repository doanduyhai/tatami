package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

public interface TimeLineRepository
{
	void addTweetToTimeline(User user, String tweetId);

	Collection<String> getTweetsFromTimeline(User user);

	Collection<String> getTweetsRangeFromTimeline(User user, int start, int end);
}
