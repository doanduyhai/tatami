package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.FRIENDS_CF;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.FriendRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
@Repository
public class CassandraFriendRepository extends CassandraAbstractRepository implements FriendRepository
{
	@Override
	public void addFriend(User user, User friend)
	{
		this.insertIntoCF(FRIENDS_CF, user.getLogin(), friend.getLogin());

		user.incrementFriendsCount();
		em.persist(user);

	}

	@Override
	public void removeFriend(User user, User friend)
	{
		this.removeFromCF(FRIENDS_CF, user.getLogin(), friend.getLogin());

		user.decrementFriendsCount();
		em.persist(user);

	}

	@Override
	public Collection<String> findFriendsForUser(User user)
	{
		return this.findRangeFromCF(FRIENDS_CF, user.getLogin(), null, false, (int) user.getFriendsCount());

	}

	@Override
	public Collection<String> findFriendsForUser(User user, String startUser, int count)
	{
		assert count >= 0 : "Friends search count should be positive";

		return this.findRangeFromCF(FRIENDS_CF, user.getLogin(), startUser, false, count);
	}

}
