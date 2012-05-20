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
}
