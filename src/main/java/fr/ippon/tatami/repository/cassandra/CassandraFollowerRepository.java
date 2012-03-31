package fr.ippon.tatami.repository.cassandra;

import java.util.Arrays;
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
public class CassandraFollowerRepository extends CassandraAbstractRepository implements FollowerRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraFollowerRepository.class);

	@Inject
	private EntityManagerImpl em;

	@Override
	public void addFollower(User user, User follower)
	{
		UserFollowers userFollowers = em.find(UserFollowers.class, user.getLogin());
		if (userFollowers == null)
		{
			userFollowers = new UserFollowers();
			userFollowers.setLogin(user.getLogin());
		}

		userFollowers.getFollowers().add(follower.getLogin());
		user.incrementFollowersCount();
		em.persist(user);
		em.persist(userFollowers);
	}

	@Override
	public void removeFollower(User user, User follower)
	{
		UserFollowers userFollowers = em.find(UserFollowers.class, user.getLogin());
		if (userFollowers == null)
		{
			// TODO Functional exception
			return;
		}

		userFollowers.getFollowers().remove(follower.getLogin());
		user.decrementFollowersCount();
		em.persist(user);
		em.persist(userFollowers);
	}

	@Override
	public Collection<String> findFollowersForUser(User user)
	{
		UserFollowers userFollowers = em.find(UserFollowers.class, user.getLogin());
		if (userFollowers != null)
		{
			return userFollowers.getFollowers();
		}
		return Arrays.asList();
	}
}
