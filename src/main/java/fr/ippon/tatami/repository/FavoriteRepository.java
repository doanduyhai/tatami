package fr.ippon.tatami.repository;

import java.util.Collection;

public interface FavoriteRepository
{
	void addFavorite(String login, String tweetId);

	void removeFavorite(String login, String tweetId);

	Collection<String> findFavoritesForUser(String login);
}
