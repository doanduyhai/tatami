package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITELINE_CF;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;

/**
 * @author DuyHai DOAN
 */
public class CassandraFavoriteRepository extends CassandraAbstractRepository implements FavoriteRepository
{

	@Override
	@CacheEvict(value =
	{
			"favorite-cache",
			"user-cache"
	}, key = "#user.login")
	public void addFavorite(User user, String tweetId)
	{
		this.insertIntoCF(FAVORITELINE_CF, user.getLogin(), tweetId);

		user.incrementFavoritesCount();
		em.persist(user);

	}

	@Override
	@CacheEvict(value =
	{
			"favorite-cache",
			"user-cache"
	}, key = "#user.login")
	public void removeFavorite(User user, String tweetId)
	{
		this.removeFromCF(FAVORITELINE_CF, user.getLogin(), tweetId);

		user.decrementFavoritesCount();
		em.persist(user);

	}

	@Override
	@Cacheable(value = "favorite-cache", key = "#user.login")
	public Collection<String> findFavoritesForUser(User user)
	{
		return this.findRangeFromCF(FAVORITELINE_CF, user.getLogin(), null, true, (int) user.getFavoritesCount());
	}

	@Override
	public Collection<String> findFavoritesRangeForUser(User user, String startTweetId, int count)
	{
		assert count >= 0 : "Favorite search count should be positive";

		return this.findRangeFromCF(FAVORITELINE_CF, user.getLogin(), startTweetId, true, count);
	}

}
