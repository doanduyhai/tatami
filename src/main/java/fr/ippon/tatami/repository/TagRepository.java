package fr.ippon.tatami.repository;

import java.util.Collection;

public interface TagRepository
{
	void addTweet(String tag, String tweetId);

	void removeTweet(String tag, String tweetId);

	Collection<String> findTweetsForTag(String tag);
}
