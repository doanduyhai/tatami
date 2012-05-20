package fr.ippon.tatami.repository;

import java.util.Collection;

public interface MentionLineRepository
{
	void addTweetToMentionline(String userLogin, String tweetId);

	void removeTweetFromMentionline(String userLogin, String tweetId);

	Collection<String> findMentionTweetsForUser(String userLogin);

	Collection<String> findMentionTweetsRangeForUser(String userLogin, String startTweetId, int count);
}
