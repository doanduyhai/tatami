package fr.ippon.tatami.repository;

import java.util.Collection;

/**
 * 
 * @author DuyHai DOAN
 */
public interface FavoriteRepository
{
	void addFavorite(String userLogin, String tweetId);

	void removeFavorite(String userLogin, String tweetId);

	Collection<String> findFavoritesForUser(String userLogin);

	Collection<String> findFavoritesRangeForUser(String userLogin, String startTweetId, int count);

	// Indexing
	void addTweetToFavoriteIndex(String userLogin, String authorLogin, String tweetId);

	void removeTweetFromFavoriteIndex(String userLogin, String authorLogin, String tweetId);

	Collection<String> getUsersForTweetFromIndex(String tweetId);

	Collection<String> getTweetsForUserFromIndex(String userLogin, String authorLogin);

	void removeIndexForTweet(String userLogin, String authorLogin, String tweetId);
}
