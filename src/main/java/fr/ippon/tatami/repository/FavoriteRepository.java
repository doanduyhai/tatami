package fr.ippon.tatami.repository;

import java.util.Collection;

import fr.ippon.tatami.domain.User;

public interface FavoriteRepository
{
	void addFavorite(User user, String tweetId);

	void removeFavorite(User user, String tweetId);

	Collection<String> findFavoritesForUser(User user);

	Collection<String> findFavoritesRangeForUser(User user, int start, int end);
}
