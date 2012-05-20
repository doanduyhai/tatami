package fr.ippon.tatami.repository;

import java.util.Collection;

public interface FavoriteTweetIndexRepository
{
	void addTweetToFavoriteIndex(String userLogin, String authorLogin, String tweetId);

	void removeTweetFromFavoriteIndex(String userLogin, String authorLogin, String tweetId);

	Collection<String> getUsersForTweetFromIndex(String tweetId);

	Collection<String> getTweetsForUserFromIndex(String userLogin, String authorLogin);

	void removeIndexForTweet(String userLogin, String authorLogin, String tweetId);
}
