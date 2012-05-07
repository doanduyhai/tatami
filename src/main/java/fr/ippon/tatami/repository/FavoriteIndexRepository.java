package fr.ippon.tatami.repository;

import java.util.Collection;

public interface FavoriteIndexRepository
{
	void addTweetToFavoriteIndex(String userLogin, String tweetId);

	void removeTweetFromFavoriteIndex(String userLogin, String tweetId);

	Collection<String> getUsersForTweetFromIndex(String tweetId);

	void removeIndexForTweet(String tweetId);
}
