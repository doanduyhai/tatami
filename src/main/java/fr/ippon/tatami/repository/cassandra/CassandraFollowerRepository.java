package fr.ippon.tatami.repository.cassandra;

import java.util.Collection;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.domain.UserFollowers;
import fr.ippon.tatami.repository.FollowerRepository;

/**
 * Cassandra implementation of the Follower repository.
 * 
 * @author Julien Dubois
 */
@Repository
public class CassandraFollowerRepository implements FollowerRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraFollowerRepository.class);

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addFollower(String login, String followerLogin)
	{
		User user = em.find(User.class, login);
		UserFollowers userFollowers = em.find(UserFollowers.class, login);
		if (userFollowers == null)
		{
			userFollowers = new UserFollowers();
			userFollowers.setLogin(user.getLogin());
		}

		userFollowers.getFollowers().add(followerLogin);
		user.incrementFollowersCount();
		em.persist(user);
		em.persist(userFollowers);
	}

	@Override
	public void removeFollower(String login, String followerLogin)
	{
		User user = em.find(User.class, login);
		UserFollowers userFollowers = em.find(UserFollowers.class, login);
		if (userFollowers == null)
		{
			// TODO Functional exception
			return;
		}

		userFollowers.getFollowers().remove(followerLogin);
		user.decrementFollowersCount();
		em.persist(user);
		em.persist(userFollowers);
	}

	@Override
	public Collection<String> findFollowersForUser(String login)
	{
		UserFollowers userFollowers = em.find(UserFollowers.class, login);
		if (userFollowers != null)
		{
			return userFollowers.getFollowers();
		}
		return null;
	}
}
