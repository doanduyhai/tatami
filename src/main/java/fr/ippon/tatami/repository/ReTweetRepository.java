package fr.ippon.tatami.repository;

import java.util.Collection;

public interface ReTweetRepository
{

	void addRetweeter(String retweeterLogin, String tweetId);

	void addTargetUserForRetweet(String targetUserLogin, String tweetId);

	void removeTargetUserForRetweet(String targetUserLogin, String tweetId);

	Collection<String> findRetweeterForTweet(String tweetId);

	Collection<String> findTartgetUsersForRetweet(String tweetId);

	long countRetweeter(String tweetId);

}
