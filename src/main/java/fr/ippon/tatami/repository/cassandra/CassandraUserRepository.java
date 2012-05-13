package fr.ippon.tatami.repository.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.repository.UserRepository;

/**
 * @author Julien Dubois
 * @author DuyHai DOAN
 */
public class CassandraUserRepository extends CassandraAbstractRepository implements UserRepository
{

	private final Logger log = LoggerFactory.getLogger(CassandraUserRepository.class);

	@Override
	public void createUser(User user)
	{
		log.debug("Creating user {} ", user);
		em.persist(user);
	}

	@Override
	@CacheEvict(value = "user-cache", key = "#user.login")
	public void updateUser(User user)
	{
		log.debug("Updating user {}", user);
		em.persist(user);
	}

	@Override
	@Cacheable(value = "user-cache", key = "#login")
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
