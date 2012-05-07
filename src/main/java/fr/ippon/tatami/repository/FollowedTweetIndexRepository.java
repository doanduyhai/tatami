package fr.ippon.tatami.repository;

import java.util.Collection;

public interface FollowedTweetIndexRepository
{

	void addTweetToIndex(String authorLogin, String followerLogin, String tweetId);

	Collection<String> findTweetsForUserAndFollower(String authorLogin, String followerLogin);

	void removeTweetFromIndex(String authorLogin, String followerLogin, String tweetId);

	void removeIndex(String authorLogin, String followerLogin);
}
