package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FAVLINE_CF;

import java.util.Collection;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FavoriteRepository;

/**
 * @author DuyHai DOAN
 */
public class CassandraFavoriteRepository extends CassandraAbstractRepository implements FavoriteRepository
{

	@Override
	public void addFavorite(User user, String tweetId)
	{
		this.insertIntoCF(FAVLINE_CF, user.getLogin(), tweetId);

		user.incrementFavoritesCount();
		em.persist(user);

	}

	@Override
	public void removeFavorite(User user, String tweetId)
	{
		this.removeFromCF(FAVLINE_CF, user.getLogin(), tweetId);

		user.decrementFavoritesCount();
		em.persist(user);

	}

	@Override
	public Collection<String> findFavoritesForUser(User user)
	{
		return this.findRangeFromCF(FAVLINE_CF, user.getLogin(), null, true, (int) user.getFavoritesCount());
	}

	@Override
	public Collection<String> findFavoritesRangeForUser(User user, String startTweetId, int count)
	{
		assert count >= 0 : "Favorite search count should be positive";

		return this.findRangeFromCF(FAVLINE_CF, user.getLogin(), startTweetId, true, count);
	}

}
