package fr.ippon.tatami.repository.cassandra;

import java.util.Collection;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserFriends;
import fr.ippon.tatami.repository.FriendRepository;

/**
 * Cassandra implementation of the Follower repository.
 * 
 * @author Julien Dubois
 */
@Repository
public class CassandraFriendRepository implements FriendRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraFriendRepository.class);

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addFriend(String login, String friendLogin)
	{
		User user = em.find(User.class, login);
		UserFriends userFriends = em.find(UserFriends.class, login);
		if (userFriends == null)
		{
			userFriends = new UserFriends();
			userFriends.setLogin(login);
		}

		userFriends.getFriends().add(friendLogin);
		user.incrementFriendsCount();
		em.persist(user);
		em.persist(userFriends);
	}

	@Override
	public void removeFriend(String login, String friendLogin)
	{
		User user = em.find(User.class, login);
		UserFriends userFriends = em.find(UserFriends.class, login);
		if (userFriends == null)
		{
			// TODO Functional exception
			return;
		}

		userFriends.getFriends().remove(friendLogin);
		user.decrementFriendsCount();
		em.persist(user);
		em.persist(userFriends);
	}

	@Override
	public Collection<String> findFriendsForUser(String login)
	{
		UserFriends userFriends = em.find(UserFriends.class, login);
		if (userFriends != null)
		{
			return userFriends.getFriends();
		}
		return null;

	}
}
