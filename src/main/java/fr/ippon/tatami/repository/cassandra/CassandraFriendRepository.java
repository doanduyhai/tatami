package fr.ippon.tatami.repository.cassandra;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserFriends;
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
		UserFriends userFriends = em.find(UserFriends.class, user.getLogin());
		if (userFriends == null)
		{
			userFriends = new UserFriends();
			userFriends.setLogin(user.getLogin());
		}

		userFriends.getFriends().add(friend.getLogin());
		user.incrementFriendsCount();
		em.persist(user);
		em.persist(userFriends);
	}

	@Override
	public void removeFriend(User user, User friend)
	{
		UserFriends userFriends = em.find(UserFriends.class, user.getLogin());
		if (userFriends == null)
		{
			// TODO Functional exception
			return;
		}

		userFriends.getFriends().remove(friend.getLogin());
		user.decrementFriendsCount();
		em.persist(user);
		em.persist(userFriends);
	}

	@Override
	public Collection<String> findFriendsForUser(User user)
	{
		UserFriends userFriends = em.find(UserFriends.class, user.getLogin());
		if (userFriends != null)
		{
			return userFriends.getFriends();
		}
		return new ArrayList<String>();

	}
}
