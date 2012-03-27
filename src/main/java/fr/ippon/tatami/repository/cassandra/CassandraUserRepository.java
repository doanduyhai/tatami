package fr.ippon.tatami.repository.cassandra;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.UserRepository;

/**
 * Cassandra implementation of the user repository.
 * 
 * @author Julien Dubois
 */
@Repository
public class CassandraUserRepository implements UserRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraUserRepository.class);

	@Inject
	private EntityManagerImpl em;

	@Override
	public void createUser(User user)
	{
		log.debug("Creating user {} ", user);
		em.persist(user);
	}

	@Override
	public void updateUser(User user)
	{
		log.debug("Updating user {}", user);
		em.persist(user);
	}

	@Override
	public User findUserByLogin(String login)
	{
		try
		{
			return em.find(User.class, login);
		}
		catch (Exception e)
		{
			log.debug("Exception while looking for user {} : ", login, e.getMessage());
			return null;
		}
	}

}
