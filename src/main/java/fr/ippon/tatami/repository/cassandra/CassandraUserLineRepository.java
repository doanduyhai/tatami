package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USERLINE_CF;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.UserLineRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraUserLineRepository extends CassandraAbstractRepository implements UserLineRepository
{
	@Override
	@CacheEvict(value = "user-cache", key = "#user.login")
	public void addTweetToUserline(User user, String tweetId)
	{

		this.insertIntoCF(USERLINE_CF, user.getLogin(), tweetId);

		user.incrementTweetCount();
		em.persist(user);
	}

	@Override
	@CacheEvict(value = "user-cache", key = "#user.login")
	public void removeTweetFromUserline(User user, String tweetId)
	{
		this.removeFromCF(USERLINE_CF, user.getLogin(), tweetId);
		user.decrementTweetCount();
		em.persist(user);
	}

	@Override
	public Collection<String> getTweetsRangeFromUserline(User user, String startTweetId, int count)
	{
		assert count >= 0 : "Userline search count should be positive";

		return this.findRangeFromCF(USERLINE_CF, user.getLogin(), startTweetId, true, count);
	}
}
