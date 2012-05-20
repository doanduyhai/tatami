package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVORITELINE_CF;
import static fr.ippon.tatami.config.CounterKeys.FAVORITE_TWEET_COUNTER;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.repository.FavoriteRepository;

/**
 * @author DuyHai DOAN
 */
public class CassandraFavoriteRepository extends CassandraAbstractRepository implements FavoriteRepository
{

	@Override
	@CacheEvict(value = "favorite-cache", key = "#userLogin")
	public void addFavorite(String userLogin, String tweetId)
	{
		this.insertIntoCF(FAVORITELINE_CF, userLogin, tweetId);
		this.incrementCounter(FAVORITE_TWEET_COUNTER, userLogin);
	}

	@Override
	@CacheEvict(value = "favorite-cache", key = "#userLogin")
	public void removeFavorite(String userLogin, String tweetId)
	{
		this.removeFromCF(FAVORITELINE_CF, userLogin, tweetId);
		this.decrementCounter(FAVORITE_TWEET_COUNTER, userLogin);
	}

	@Override
	@Cacheable(value = "favorite-cache", key = "#userLogin")
	public Collection<String> findFavoritesForUser(String userLogin)
	{
		long favoriteTweetsCount = this.getCounterValue(FAVORITE_TWEET_COUNTER, userLogin);
		return this.findRangeFromCF(FAVORITELINE_CF, userLogin, null, true, (int) favoriteTweetsCount);
	}

	@Override
	public Collection<String> findFavoritesRangeForUser(String userLogin, String startTweetId, int count)
	{
		assert count >= 0 : "Favorite search count should be positive";

		return this.findRangeFromCF(FAVORITELINE_CF, userLogin, startTweetId, true, count);
	}

}
