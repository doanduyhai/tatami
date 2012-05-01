package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FOLLOWERS_CF;

import java.util.Collection;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FollowerRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraFollowerRepository extends CassandraAbstractRepository implements FollowerRepository
{

	@Override
	public void addFollower(User user, User follower)
	{
		this.insertIntoCF(FOLLOWERS_CF, user.getLogin(), follower.getLogin());

		user.incrementFollowersCount();
		em.persist(user);

	}

	@Override
	public void removeFollower(User user, User follower)
	{

		this.removeFromCF(FOLLOWERS_CF, user.getLogin(), follower.getLogin());

		user.decrementFollowersCount();
		em.persist(user);

	}

	@Override
	public Collection<String> findFollowersForUser(User user)
	{

		return this.findRangeFromCF(FOLLOWERS_CF, user.getLogin(), null, false, (int) user.getFollowersCount());
	}

	@Override
	public Collection<String> findFollowersForUser(User user, String startUser, int count)
	{
		assert count >= 0 : "Follower search count should be positive";

		return this.findRangeFromCF(FOLLOWERS_CF, user.getLogin(), startUser, false, count);
	}
}
